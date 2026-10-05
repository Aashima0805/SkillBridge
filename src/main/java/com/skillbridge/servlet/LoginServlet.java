package com.skillbridge.servlet;

import com.skillbridge.dao.UserDAO;
import com.skillbridge.model.User;
import com.skillbridge.util.PasswordUtil;
import com.skillbridge.util.TokenUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        if (user != null) {
            redirect(req, resp, Auth.landingPage(user));
            return;
        }
        req.setAttribute("next", safeNext(req.getParameter("next")));
        view(req, resp, "login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = param(req, "login");
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String next = safeNext(req.getParameter("next"));

        User user = login.isEmpty() ? null : userDAO.findByLogin(login);
        String error = null;
        if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
            error = "Invalid username/e-mail or password.";
        } else if (!user.isActive()) {
            error = "This account has been deactivated. Please contact the admin.";
        }
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("login", login);
            req.setAttribute("next", next);
            view(req, resp, "login");
            return;
        }

        // A new session id after login prevents session-fixation attacks.
        req.changeSessionId();
        Auth.login(req, user);

        if ("on".equals(req.getParameter("remember"))) {
            Auth.setRememberCookie(req, resp, TokenUtil.create(user.getId(), Instant.now().getEpochSecond()),
                    TokenUtil.REMEMBER_DAYS * 24 * 3600);
        }
        flash(req, "success", "Welcome back, " + user.getFullName().split(" ")[0] + "!");
        redirect(req, resp, next != null ? next : Auth.landingPage(user));
    }
}
