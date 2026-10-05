package com.skillbridge.filter;

import com.skillbridge.dao.UserDAO;
import com.skillbridge.model.User;
import com.skillbridge.servlet.Auth;
import com.skillbridge.util.TokenUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * One gatekeeper for the whole site:
 * 1. restores a login from the signed "remember me" cookie,
 * 2. checks the anti-CSRF token on every POST,
 * 3. enforces role based access (customer / worker / admin) by URL.
 */
public class AuthFilter implements Filter {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getServletPath();

        if (path.startsWith("/assets/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(true);
        String csrf = (String) session.getAttribute("csrf");
        if (csrf == null) {
            byte[] bytes = new byte[24];
            RANDOM.nextBytes(bytes);
            csrf = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute("csrf", csrf);
        }
        req.setAttribute("ctx", req.getContextPath());
        req.setAttribute("csrf", csrf);
        req.setAttribute("navPath", path);

        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = restoreFromCookie(req, session);
        }

        if ("POST".equalsIgnoreCase(req.getMethod()) && !csrf.equals(req.getParameter("_csrf"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Your security token is missing or has expired. Go back, refresh the page and try again.");
            return;
        }

        String required = requiredRole(path);
        if (required != null) {
            if (user == null) {
                session.setAttribute("flash", new String[]{"warning", "Please log in to continue."});
                String next = "GET".equalsIgnoreCase(req.getMethod()) ? path
                        + (req.getQueryString() == null ? "" : "?" + req.getQueryString()) : null;
                String target = req.getContextPath() + "/login"
                        + (next == null ? "" : "?next=" + URLEncoder.encode(next, StandardCharsets.UTF_8));
                resp.sendRedirect(target);
                return;
            }
            if (!"ANY".equals(required) && !required.equals(user.getRole())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to open this page.");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    /** Which role is needed for a URL: ADMIN, WORKER, CUSTOMER, ANY (any logged-in user) or null (public). */
    static String requiredRole(String path) {
        if (path.equals("/admin") || path.startsWith("/admin/")) {
            return "ADMIN";
        }
        if (path.startsWith("/worker/")) {
            return "WORKER";
        }
        if (path.equals("/book") || path.equals("/checkout") || path.equals("/my-bookings") || path.equals("/review")) {
            return "CUSTOMER";
        }
        if (path.equals("/booking/action")) {
            return "ANY";
        }
        return null;
    }

    private User restoreFromCookie(HttpServletRequest req, HttpSession session) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie c : cookies) {
            if (Auth.REMEMBER_COOKIE.equals(c.getName())) {
                int id = TokenUtil.parse(c.getValue(), Instant.now().getEpochSecond());
                if (id > 0) {
                    User u = userDAO.findById(id);
                    if (u != null && u.isActive()) {
                        Auth.login(req, u);
                        return (User) session.getAttribute("user");
                    }
                }
            }
        }
        return null;
    }
}
