package com.skillbridge.servlet;

import com.skillbridge.model.BookingDraft;
import com.skillbridge.service.BookingService;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Step 1 of booking: validate the chosen slot, keep it in the session, go to checkout. */
@WebServlet("/book")
public class BookingServlet extends BaseServlet {
    private final BookingService service = new BookingService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int workerId = Validator.toInt(req.getParameter("workerId"), 0);
        int hours = Validator.toInt(req.getParameter("hours"), 0);
        String address = param(req, "address");
        String notes = param(req, "notes");
        String back = "/profile?id=" + workerId;

        BookingService.Check chk = service.check(workerId, param(req, "date"), param(req, "start"), hours);
        if (!chk.ok()) {
            flash(req, "error", chk.message());
            redirect(req, resp, back);
            return;
        }
        if (!Validator.lengthBetween(address, 10, 200)) {
            flash(req, "error", "Please enter the full address where the work is needed (10 to 200 characters).");
            redirect(req, resp, back);
            return;
        }
        if (notes.length() > 200) {
            flash(req, "error", "Notes can be at most 200 characters.");
            redirect(req, resp, back);
            return;
        }
        req.getSession().setAttribute("draft", new BookingDraft(workerId, chk.date(), chk.start(), hours, address, notes));
        redirect(req, resp, "/checkout");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        redirect(req, resp, "/workers");
    }
}
