package com.skillbridge.servlet;

import com.skillbridge.dao.DataAccessException;
import com.skillbridge.dao.SkillDAO;
import com.skillbridge.dao.UserDAO;
import com.skillbridge.model.User;
import com.skillbridge.model.Worker;
import com.skillbridge.util.PasswordUtil;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {
    private final UserDAO userDAO = new UserDAO();
    private final SkillDAO skillDAO = new SkillDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (currentUser(req) != null) {
            redirect(req, resp, Auth.landingPage(currentUser(req)));
            return;
        }
        Map<String, String> form = new LinkedHashMap<>();
        form.put("role", "WORKER".equalsIgnoreCase(req.getParameter("role")) ? "WORKER" : "CUSTOMER");
        show(req, resp, form, new LinkedHashMap<>());
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, Map<String, String> form,
                      Map<String, String> errors) throws ServletException, IOException {
        req.setAttribute("form", form);
        req.setAttribute("errors", errors);
        req.setAttribute("skills", skillDAO.listAll());
        view(req, resp, "register");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> errors = new LinkedHashMap<>();
        Map<String, String> form = new LinkedHashMap<>();
        for (String f : new String[]{"fullName", "username", "email", "phone", "city", "skillId", "experience", "rate",
                "bio", "workStart", "workEnd"}) {
            form.put(f, param(req, f));
        }
        String role = "WORKER".equals(param(req, "role")) ? "WORKER" : "CUSTOMER";
        form.put("role", role);
        form.put("email", form.get("email").toLowerCase());
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String confirm = req.getParameter("confirm") == null ? "" : req.getParameter("confirm");

        if (!Validator.isValidPersonName(form.get("fullName"))) {
            errors.put("fullName", "Enter your name using letters only (2 to 60 characters).");
        }
        if (!Validator.isValidUsername(form.get("username"))) {
            errors.put("username", "Username: 4 to 20 characters, start with a letter, use letters, digits or underscore.");
        } else if (userDAO.exists("username", form.get("username"))) {
            errors.put("username", "This username is already taken.");
        }
        if (!Validator.isValidEmail(form.get("email"))) {
            errors.put("email", "Enter a valid e-mail address.");
        } else if (userDAO.exists("email", form.get("email"))) {
            errors.put("email", "This e-mail is already registered.");
        }
        if (!Validator.isValidPhone(form.get("phone"))) {
            errors.put("phone", "Enter a 10-digit mobile number starting with 6, 7, 8 or 9.");
        } else if (userDAO.exists("phone", form.get("phone"))) {
            errors.put("phone", "This phone number is already registered.");
        }
        if (!Validator.isValidCity(form.get("city"))) {
            errors.put("city", "Enter your city (letters only).");
        }
        if (!Validator.isValidPassword(password)) {
            errors.put("password", "Password must be 8 to 64 characters with at least one letter and one digit.");
        }
        if (!password.equals(confirm)) {
            errors.put("confirm", "The two passwords do not match.");
        }

        WorkerInput wi = null;
        if ("WORKER".equals(role)) {
            wi = WorkerInput.parse(req, errors, skillDAO);
        }

        if (!errors.isEmpty()) {
            show(req, resp, form, errors);
            return;
        }

        User u = new User();
        u.setUsername(form.get("username"));
        u.setFullName(form.get("fullName"));
        u.setEmail(form.get("email"));
        u.setPhone(form.get("phone"));
        u.setCity(form.get("city"));
        u.setRole(role);

        Worker w = null;
        if (wi != null) {
            w = new Worker();
            w.setSkillId(wi.skillId);
            w.setExperienceYears(wi.experience);
            w.setHourlyRate(wi.rate);
            w.setBio(wi.bio);
            w.setWorkStart(wi.start);
            w.setWorkEnd(wi.end);
        }

        try {
            userDAO.register(u, PasswordUtil.hash(password), w);
        } catch (DataAccessException e) {
            // two people registering the same username/e-mail at the same moment
            errors.put("username", "Could not create the account. The username, e-mail or phone may already be in use.");
            show(req, resp, form, errors);
            return;
        }

        if ("WORKER".equals(role)) {
            flash(req, "success", "Account created! An admin will verify your profile before it appears in search. You can log in now.");
        } else {
            flash(req, "success", "Account created! Please log in.");
        }
        redirect(req, resp, "/login");
    }
}
