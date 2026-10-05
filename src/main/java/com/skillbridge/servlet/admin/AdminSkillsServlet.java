package com.skillbridge.servlet.admin;

import com.skillbridge.dao.SkillDAO;
import com.skillbridge.servlet.BaseServlet;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin: create, edit and delete skills (full CRUD). */
@WebServlet("/admin/skills")
public class AdminSkillsServlet extends BaseServlet {
    private final SkillDAO skillDAO = new SkillDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("skills", skillDAO.listWithCounts());
        view(req, resp, "admin-skills");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = param(req, "action");
        int id = Validator.toInt(req.getParameter("id"), 0);
        String name = param(req, "name");
        String icon = param(req, "icon");
        String description = param(req, "description");
        if (icon.isEmpty()) {
            icon = "🛠";
        }

        if ("delete".equals(action)) {
            if (skillDAO.delete(id)) {
                flash(req, "success", "Skill deleted.");
            } else {
                flash(req, "error", "This skill is used by workers and cannot be deleted.");
            }
        } else if ("add".equals(action) || "update".equals(action)) {
            if (!Validator.lengthBetween(name, 3, 40) || !Validator.lengthBetween(icon, 1, 16)
                    || description.length() > 200) {
                flash(req, "error", "Name must be 3 to 40 characters and the description at most 200.");
            } else if (skillDAO.nameExists(name, "update".equals(action) ? id : 0)) {
                flash(req, "error", "A skill with this name already exists.");
            } else if ("add".equals(action)) {
                skillDAO.create(name, icon, description);
                flash(req, "success", "Skill \"" + name + "\" added.");
            } else {
                skillDAO.update(id, name, icon, description);
                flash(req, "success", "Skill updated.");
            }
        }
        redirect(req, resp, "/admin/skills");
    }
}
