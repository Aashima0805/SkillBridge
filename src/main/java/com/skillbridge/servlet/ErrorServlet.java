package com.skillbridge.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Friendly error page for 403, 404 and 500 (mapped in web.xml). */
@WebServlet("/error")
public class ErrorServlet extends BaseServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer code = (Integer) req.getAttribute("jakarta.servlet.error.status_code");
        Throwable ex = (Throwable) req.getAttribute("jakarta.servlet.error.exception");
        String message = (String) req.getAttribute("jakarta.servlet.error.message");
        int status = code == null ? 404 : code;
        if (ex != null) {
            getServletContext().log("Unhandled error", ex);
        }
        String title;
        String text;
        switch (status) {
            case 403 -> {
                title = "Access denied";
                text = message != null && !message.isBlank() ? message : "You do not have permission to open this page.";
            }
            case 404 -> {
                title = "Page not found";
                text = "The page you are looking for does not exist or has moved.";
            }
            default -> {
                title = "Something went wrong";
                text = "An unexpected error occurred on our side. Please try again in a moment.";
                status = 500;
            }
        }
        req.setAttribute("ctx", req.getContextPath());
        req.setAttribute("errorCode", status);
        req.setAttribute("errorTitle", title);
        req.setAttribute("errorText", text);
        resp.setStatus(status);
        view(req, resp, "error");
    }
}
