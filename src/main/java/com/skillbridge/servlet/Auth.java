package com.skillbridge.servlet;

import com.skillbridge.dao.WorkerDAO;
import com.skillbridge.model.User;
import com.skillbridge.model.Worker;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Small helpers for login state kept in the HttpSession. */
public final class Auth {
    public static final String REMEMBER_COOKIE = "sb_remember";

    private Auth() {
    }

    /** Stores the user (without the password hash) in the session. Workers also get their workerId. */
    public static void login(HttpServletRequest req, User user) {
        HttpSession session = req.getSession(true);
        session.setAttribute("user", user.forSession());
        if ("WORKER".equals(user.getRole())) {
            Worker w = new WorkerDAO().findByUserId(user.getId());
            if (w != null) {
                session.setAttribute("workerId", w.getId());
            }
        }
    }

    public static String landingPage(User user) {
        return switch (user.getRole()) {
            case "ADMIN" -> "/admin";
            case "WORKER" -> "/worker/dashboard";
            default -> "/workers";
        };
    }

    public static void setRememberCookie(HttpServletRequest req, HttpServletResponse resp, String value, int maxAgeSeconds) {
        Cookie c = new Cookie(REMEMBER_COOKIE, value);
        c.setHttpOnly(true);
        c.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        c.setMaxAge(maxAgeSeconds);
        resp.addCookie(c);
    }
}
