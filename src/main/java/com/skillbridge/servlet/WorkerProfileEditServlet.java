package com.skillbridge.servlet;

import com.skillbridge.dao.SkillDAO;
import com.skillbridge.dao.WorkerDAO;
import com.skillbridge.model.Worker;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/worker/profile")
public class WorkerProfileEditServlet extends BaseServlet {
    private final WorkerDAO workerDAO = new WorkerDAO();
    private final SkillDAO skillDAO = new SkillDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Worker w = workerDAO.findByUserId(currentUser(req).getId());
        req.setAttribute("worker", w);
        req.setAttribute("skills", skillDAO.listAll());
        req.setAttribute("errors", new LinkedHashMap<String, String>());
        Map<String, String> form = new LinkedHashMap<>();
        form.put("skillId", String.valueOf(w.getSkillId()));
        form.put("experience", String.valueOf(w.getExperienceYears()));
        form.put("rate", w.getHourlyRate().stripTrailingZeros().toPlainString());
        form.put("city", w.getCity());
        form.put("bio", w.getBio());
        form.put("workStart", w.getWorkStartText());
        form.put("workEnd", w.getWorkEndText());
        req.setAttribute("form", form);
        view(req, resp, "worker-edit");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Worker w = workerDAO.findByUserId(currentUser(req).getId());
        Map<String, String> errors = new LinkedHashMap<>();
        WorkerInput in = WorkerInput.parse(req, errors, skillDAO);
        String city = param(req, "city");
        if (!Validator.isValidCity(city)) {
            errors.put("city", "Enter your city (letters only).");
        }
        if (!errors.isEmpty()) {
            Map<String, String> form = new LinkedHashMap<>();
            for (String f : new String[]{"skillId", "experience", "rate", "city", "bio", "workStart", "workEnd"}) {
                form.put(f, param(req, f));
            }
            req.setAttribute("worker", w);
            req.setAttribute("skills", skillDAO.listAll());
            req.setAttribute("errors", errors);
            req.setAttribute("form", form);
            view(req, resp, "worker-edit");
            return;
        }
        workerDAO.updateProfile(w.getId(), in.skillId, in.experience, in.rate, city, in.bio, in.start, in.end);
        flash(req, "success", "Your profile has been updated.");
        redirect(req, resp, "/worker/dashboard");
    }
}
