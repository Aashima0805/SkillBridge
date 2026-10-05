package com.skillbridge.servlet.admin;

import com.skillbridge.dao.BookingDAO;
import com.skillbridge.dao.StatsDAO;
import com.skillbridge.dao.WorkerDAO;
import com.skillbridge.model.Worker;
import com.skillbridge.servlet.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/admin")
public class AdminDashboardServlet extends BaseServlet {
    private final StatsDAO statsDAO = new StatsDAO();
    private final WorkerDAO workerDAO = new WorkerDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Worker> pending = workerDAO.listAll().stream().filter(w -> !w.isVerified()).collect(Collectors.toList());
        req.setAttribute("stats", statsDAO.adminStats());
        req.setAttribute("pendingWorkers", pending);
        req.setAttribute("recentBookings", bookingDAO.listAll(6));
        view(req, resp, "admin-dashboard");
    }
}
