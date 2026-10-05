package com.skillbridge.servlet;

import com.skillbridge.dao.ReviewDAO;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/review")
public class ReviewServlet extends BaseServlet {
    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int bookingId = Validator.toInt(req.getParameter("bookingId"), 0);
        int rating = Validator.toInt(req.getParameter("rating"), 0);
        String comment = param(req, "comment");
        if (!Validator.inRange(rating, 1, 5)) {
            flash(req, "error", "Please choose a rating from 1 to 5 stars.");
        } else if (comment.length() > 300) {
            flash(req, "error", "Your review can be at most 300 characters.");
        } else if (reviewDAO.add(bookingId, currentUser(req).getId(), rating, comment)) {
            flash(req, "success", "Thank you! Your review has been posted.");
        } else {
            flash(req, "error", "You can only review a completed booking of yours, once.");
        }
        redirect(req, resp, "/my-bookings");
    }
}
