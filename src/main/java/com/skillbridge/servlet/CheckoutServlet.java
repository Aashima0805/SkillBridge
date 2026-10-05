package com.skillbridge.servlet;

import com.skillbridge.dao.BookingDAO;
import com.skillbridge.dao.SlotTakenException;
import com.skillbridge.model.Booking;
import com.skillbridge.model.BookingDraft;
import com.skillbridge.model.User;
import com.skillbridge.model.Worker;
import com.skillbridge.service.BookingService;
import com.skillbridge.util.MockGateway;
import com.skillbridge.util.PricingUtil;
import com.skillbridge.util.SlotRules;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Step 2 of booking: price breakdown + simulated payment. The booking and its payment are saved
 * together in one database transaction only after the payment succeeds.
 */
@WebServlet("/checkout")
public class CheckoutServlet extends BaseServlet {
    private final BookingService service = new BookingService();
    private final BookingDAO bookingDAO = new BookingDAO();

    private BookingDraft draft(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (BookingDraft) s.getAttribute("draft");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BookingDraft d = draft(req);
        if (d == null) {
            flash(req, "info", "Choose a worker and a time first.");
            redirect(req, resp, "/workers");
            return;
        }
        if (!prepare(req, resp, d)) {
            return;
        }
        req.setAttribute("errors", new LinkedHashMap<String, String>());
        req.setAttribute("form", new LinkedHashMap<String, String>());
        view(req, resp, "checkout");
    }

    /** Re-checks the slot and puts the summary on the request. Returns false if it redirected away. */
    private boolean prepare(HttpServletRequest req, HttpServletResponse resp, BookingDraft d) throws IOException {
        BookingService.Check chk = service.check(d.getWorkerId(), d.getDate().toString(), d.getStart().toString(), d.getHours());
        if (!chk.ok()) {
            req.getSession().removeAttribute("draft");
            flash(req, "error", chk.message());
            redirect(req, resp, "/profile?id=" + d.getWorkerId());
            return false;
        }
        Worker w = chk.worker();
        BigDecimal sub = PricingUtil.subtotal(w.getHourlyRate(), d.getHours());
        BigDecimal fee = PricingUtil.platformFee(sub);
        req.setAttribute("worker", w);
        req.setAttribute("draft", d);
        req.setAttribute("dateLabel", d.getDate().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.ENGLISH)));
        req.setAttribute("startLabel", SlotRules.label(d.getStart()));
        req.setAttribute("endLabel", SlotRules.label(d.getEnd()));
        req.setAttribute("subtotal", sub);
        req.setAttribute("fee", fee);
        req.setAttribute("total", sub.add(fee));
        return true;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BookingDraft d = draft(req);
        if (d == null) {
            flash(req, "warning", "Your booking session expired. Please choose the time again.");
            redirect(req, resp, "/workers");
            return;
        }
        User user = currentUser(req);
        String method = "CARD".equals(param(req, "method")) ? "CARD" : "UPI";
        String upi = param(req, "upi");
        String card = param(req, "cardNumber").replaceAll("[ -]", "");
        String cardName = param(req, "cardName");
        String expiry = param(req, "expiry");
        String cvv = param(req, "cvv");

        Map<String, String> errors = new LinkedHashMap<>();
        if ("UPI".equals(method)) {
            if (!Validator.isValidUpi(upi)) {
                errors.put("upi", "Enter a valid UPI id such as name@okaxis.");
            }
        } else {
            if (!Validator.isValidCardNumber(card)) {
                errors.put("cardNumber", "Enter a valid 16-digit card number.");
            }
            if (!Validator.isValidPersonName(cardName)) {
                errors.put("cardName", "Enter the name printed on the card.");
            }
            if (!Validator.isValidExpiry(expiry, YearMonth.now(com.skillbridge.util.Config.zone()))) {
                errors.put("expiry", "Enter a valid expiry (MM/YY) that is not in the past.");
            }
            if (!Validator.isValidCvv(cvv)) {
                errors.put("cvv", "CVV must be 3 digits.");
            }
        }

        if (!prepare(req, resp, d)) {
            return;
        }
        Worker w = (Worker) req.getAttribute("worker");
        BigDecimal sub = (BigDecimal) req.getAttribute("subtotal");
        BigDecimal fee = (BigDecimal) req.getAttribute("fee");

        if (errors.isEmpty()) {
            MockGateway.Result result = MockGateway.charge(method, card, upi);
            if (!result.success()) {
                errors.put("payment", result.message());
            } else {
                Booking b = new Booking();
                b.setCustomerId(user.getId());
                b.setWorkerId(w.getId());
                b.setBookingDate(d.getDate());
                b.setStartTime(d.getStart());
                b.setEndTime(d.getEnd());
                b.setHours(d.getHours());
                b.setAddress(d.getAddress());
                b.setNotes(d.getNotes());
                b.setHourlyRate(w.getHourlyRate());
                b.setSubtotal(sub);
                b.setPlatformFee(fee);
                b.setTotal(sub.add(fee));
                try {
                    int id = bookingDAO.createPaid(b, method, result.detail(), result.ref());
                    req.getSession().removeAttribute("draft");
                    flash(req, "success", "Booking confirmed! Payment reference " + result.ref()
                            + ". " + w.getFullName() + " will accept your request shortly.");
                    redirect(req, resp, "/my-bookings?new=" + id);
                    return;
                } catch (SlotTakenException e) {
                    req.getSession().removeAttribute("draft");
                    flash(req, "error", e.getMessage() + " You have not been charged.");
                    redirect(req, resp, "/profile?id=" + w.getId());
                    return;
                }
            }
        }
        Map<String, String> form = new LinkedHashMap<>();
        form.put("method", method);
        form.put("upi", upi);
        form.put("cardName", cardName);
        req.setAttribute("form", form);
        req.setAttribute("errors", errors);
        view(req, resp, "checkout");
    }
}
