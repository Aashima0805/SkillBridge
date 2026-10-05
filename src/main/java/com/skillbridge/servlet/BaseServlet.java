package com.skillbridge.servlet;

import com.skillbridge.model.User;
import com.skillbridge.util.Config;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

/** Shared helper methods for all controllers (the "C" in MVC). */
public abstract class BaseServlet extends HttpServlet {
    private static final String VIEWS = "/WEB-INF/views/";

    protected User currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (User) s.getAttribute("user");
    }

    /** One-time message shown at the top of the next page: type is success, error, warning or info. */
    protected void flash(HttpServletRequest req, String type, String message) {
        req.getSession(true).setAttribute("flash", new String[]{type, message});
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    protected void view(HttpServletRequest req, HttpServletResponse resp, String name) throws ServletException, IOException {
        req.getRequestDispatcher(VIEWS + name + ".jsp").forward(req, resp);
    }

    protected String param(HttpServletRequest req, String name) {
        return Validator.clean(req.getParameter(name));
    }

    protected void json(HttpServletResponse resp, String body) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store");
        resp.getWriter().write(body);
    }

    protected LocalDateTime now() {
        return LocalDateTime.now(Config.zone());
    }

    /** Only allow redirects to paths inside this site (prevents open-redirect attacks). */
    protected String safeNext(String next) {
        if (next == null || next.isBlank()) {
            return null;
        }
        if (!next.startsWith("/") || next.startsWith("//") || next.contains("\\") || next.contains(":") || next.contains("\n")) {
            return null;
        }
        return next;
    }
}
