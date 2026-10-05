package com.skillbridge.servlet;

import com.skillbridge.dao.BookingDAO;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/my-bookings")
public class MyBookingsServlet extends BaseServlet {
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("bookings", bookingDAO.listForCustomer(currentUser(req).getId()));
        req.setAttribute("newId", Validator.toInt(req.getParameter("new"), 0));
        view(req, resp, "my-bookings");
    }
}
