package com.skillbridge.servlet.admin;

import com.skillbridge.dao.WorkerDAO;
import com.skillbridge.model.Worker;
import com.skillbridge.servlet.BaseServlet;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/** Admin: verify or un-verify worker profiles. Only verified workers appear in search. */
@WebServlet("/admin/workers")
public class AdminWorkersServlet extends BaseServlet {
    private final WorkerDAO workerDAO = new WorkerDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filter = param(req, "status");
        List<Worker> all = workerDAO.listAll();
        List<Worker> shown = switch (filter) {
            case "pending" -> all.stream().filter(w -> !w.isVerified()).collect(Collectors.toList());
            case "verified" -> all.stream().filter(Worker::isVerified).collect(Collectors.toList());
            default -> all;
        };
        req.setAttribute("workers", shown);
        req.setAttribute("filter", filter);
        view(req, resp, "admin-workers");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Validator.toInt(req.getParameter("workerId"), 0);
        Worker w = workerDAO.findById(id);
        String action = param(req, "action");
        if (w == null) {
            flash(req, "error", "Worker not found.");
        } else if ("verify".equals(action)) {
            workerDAO.setVerified(id, true);
            flash(req, "success", w.getFullName() + " is now verified and visible to customers.");
        } else if ("unverify".equals(action)) {
            workerDAO.setVerified(id, false);
            flash(req, "info", w.getFullName() + " has been removed from search results.");
        }
        String back = safeNext(req.getParameter("back"));
        redirect(req, resp, back != null ? back : "/admin/workers");
    }
}
