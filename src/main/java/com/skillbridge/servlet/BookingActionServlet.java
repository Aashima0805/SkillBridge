package com.skillbridge.servlet;

import com.skillbridge.dao.BookingDAO;
import com.skillbridge.model.Booking;
import com.skillbridge.model.User;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Status changes for a booking: cancel (customer/worker/admin), accept, reject and complete (worker).
 * The status flow is PENDING -> ACCEPTED -> COMPLETED, or CANCELLED with an automatic refund.
 */
@WebServlet("/booking/action")
public class BookingActionServlet extends BaseServlet {
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        int id = Validator.toInt(req.getParameter("id"), 0);
        String action = param(req, "action");
        Booking b = bookingDAO.findById(id);
        String back = safeNext(req.getParameter("back"));
        if (back == null) {
            back = switch (user.getRole()) {
                case "WORKER" -> "/worker/dashboard";
                case "ADMIN" -> "/admin/bookings";
                default -> "/my-bookings";
            };
        }
        if (b == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Booking not found.");
            return;
        }
        boolean isCustomer = "CUSTOMER".equals(user.getRole()) && b.getCustomerId() == user.getId();
        boolean isWorker = "WORKER".equals(user.getRole()) && b.getWorkerUserId() == user.getId();
        boolean isAdmin = "ADMIN".equals(user.getRole());
        if (!isCustomer && !isWorker && !isAdmin) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "This is not your booking.");
            return;
        }

        switch (action) {
            case "cancel" -> {
                if (bookingDAO.cancelWithRefund(id)) {
                    flash(req, "success", "Booking #" + id + " cancelled. The payment has been refunded.");
                } else {
                    flash(req, "error", "This booking can no longer be cancelled.");
                }
            }
            case "reject" -> {
                if (isWorker && "PENDING".equals(b.getStatus()) && bookingDAO.cancelWithRefund(id)) {
                    flash(req, "success", "Request declined. The customer has been refunded.");
                } else {
                    flash(req, "error", "Only a pending request can be declined by the worker.");
                }
            }
            case "accept" -> {
                if (isWorker && bookingDAO.transition(id, "PENDING", "ACCEPTED")) {
                    flash(req, "success", "Booking #" + id + " accepted.");
                } else {
                    flash(req, "error", "Only a pending request can be accepted by the worker.");
                }
            }
            case "complete" -> {
                if (isWorker && bookingDAO.transition(id, "ACCEPTED", "COMPLETED")) {
                    flash(req, "success", "Job #" + id + " marked as completed. The customer can now leave a review.");
                } else {
                    flash(req, "error", "Only an accepted job can be marked as completed.");
                }
            }
            default -> {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action.");
                return;
            }
        }
        redirect(req, resp, back);
    }
}
