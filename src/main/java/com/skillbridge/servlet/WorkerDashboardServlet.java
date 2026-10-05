package com.skillbridge.servlet;

import com.skillbridge.dao.BookingDAO;
import com.skillbridge.dao.WorkerDAO;
import com.skillbridge.model.Booking;
import com.skillbridge.model.Worker;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/worker/dashboard")
public class WorkerDashboardServlet extends BaseServlet {
    private final WorkerDAO workerDAO = new WorkerDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    private Worker me(HttpServletRequest req) {
        return workerDAO.findByUserId(currentUser(req).getId());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Worker w = me(req);
        if (w == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Worker profile not found.");
            return;
        }
        List<Booking> list = bookingDAO.listForWorker(w.getId());
        int pending = 0;
        int accepted = 0;
        int completed = 0;
        BigDecimal earnings = BigDecimal.ZERO;
        for (Booking b : list) {
            switch (b.getStatus()) {
                case "PENDING" -> pending++;
                case "ACCEPTED" -> accepted++;
                case "COMPLETED" -> {
                    completed++;
                    earnings = earnings.add(b.getSubtotal());
                }
                default -> { }
            }
        }
        req.setAttribute("worker", w);
        req.setAttribute("bookings", list);
        req.setAttribute("pendingCount", pending);
        req.setAttribute("acceptedCount", accepted);
        req.setAttribute("completedCount", completed);
        req.setAttribute("earnings", earnings);
        view(req, resp, "worker-dashboard");
    }

    /** Toggle "accepting bookings" on or off. */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Worker w = me(req);
        if (w != null) {
            boolean available = "true".equals(req.getParameter("available"));
            workerDAO.setAvailable(w.getId(), available);
            flash(req, "info", available ? "You are now accepting new bookings." : "You are hidden from new bookings until you switch back.");
        }
        redirect(req, resp, "/worker/dashboard");
    }
}
