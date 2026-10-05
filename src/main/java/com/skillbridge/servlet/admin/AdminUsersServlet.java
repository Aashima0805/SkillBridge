package com.skillbridge.servlet.admin;

import com.skillbridge.dao.UserDAO;
import com.skillbridge.model.User;
import com.skillbridge.servlet.BaseServlet;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin: list users and activate or deactivate accounts. */
@WebServlet("/admin/users")
public class AdminUsersServlet extends BaseServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("users", userDAO.listAll());
        view(req, resp, "admin-users");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Validator.toInt(req.getParameter("id"), 0);
        User target = userDAO.findById(id);
        if (target == null || "ADMIN".equals(target.getRole())) {
            flash(req, "error", "This account cannot be changed.");
        } else {
            boolean active = "true".equals(req.getParameter("active"));
            userDAO.setActive(id, active);
            flash(req, "success", target.getFullName() + (active ? " has been activated." : " has been deactivated."));
        }
        redirect(req, resp, "/admin/users");
    }
}
