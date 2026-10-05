package com.skillbridge.servlet.admin;

import com.skillbridge.dao.BookingDAO;
import com.skillbridge.dao.StatsDAO;
import com.skillbridge.servlet.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin: every booking and its payment in one report. */
@WebServlet("/admin/bookings")
public class AdminBookingsServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("bookings", new BookingDAO().listAll(300));
        req.setAttribute("stats", new StatsDAO().adminStats());
        view(req, resp, "admin-bookings");
    }
}
