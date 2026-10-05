package com.skillbridge.servlet;

import com.skillbridge.dao.UserDAO;
import com.skillbridge.util.Json;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** AJAX endpoint: is this username / e-mail / phone free? Called while the user types. */
@WebServlet("/ajax/check")
public class CheckAvailabilityServlet extends BaseServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String field = param(req, "field");
        String value = param(req, "value");
        boolean valid;
        String badMessage;
        switch (field) {
            case "username" -> {
                valid = Validator.isValidUsername(value);
                badMessage = "4 to 20 characters, start with a letter; letters, digits, underscore only.";
            }
            case "email" -> {
                value = value.toLowerCase();
                valid = Validator.isValidEmail(value);
                badMessage = "Enter a valid e-mail address.";
            }
            case "phone" -> {
                valid = Validator.isValidPhone(value);
                badMessage = "Enter a 10-digit mobile number starting with 6, 7, 8 or 9.";
            }
            default -> {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown field");
                return;
            }
        }
        if (!valid) {
            json(resp, Json.obj("valid", false, "available", false, "message", badMessage));
            return;
        }
        boolean taken = userDAO.exists(field, value);
        json(resp, Json.obj("valid", true, "available", !taken,
                "message", taken ? "Already registered." : "Available."));
    }
}
