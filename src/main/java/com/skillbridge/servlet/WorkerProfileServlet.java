package com.skillbridge.servlet;

import com.skillbridge.dao.ReviewDAO;
import com.skillbridge.dao.WorkerDAO;
import com.skillbridge.model.User;
import com.skillbridge.model.Worker;
import com.skillbridge.util.SlotRules;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/** Public worker profile with reviews and the booking form. Also remembers the visit in a cookie. */
@WebServlet("/profile")
public class WorkerProfileServlet extends BaseServlet {
    private final WorkerDAO workerDAO = new WorkerDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Validator.toInt(req.getParameter("id"), 0);
        Worker w = workerDAO.findById(id);
        User viewer = currentUser(req);
        boolean privileged = viewer != null && ("ADMIN".equals(viewer.getRole()) || viewer.getId() == (w == null ? -1 : w.getUserId()));
        if (w == null || (!privileged && (!w.isVerified() || !w.isUserActive()))) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Worker not found.");
            return;
        }
        if (w.isVerified()) {
            RecentCookie.add(req, resp, w.getId());
        }
        LocalDate today = now().toLocalDate();
        req.setAttribute("worker", w);
        req.setAttribute("reviews", reviewDAO.listByWorker(w.getId(), 10));
        req.setAttribute("minDate", today.toString());
        req.setAttribute("maxDate", today.plusDays(SlotRules.MAX_DAYS_AHEAD).toString());
        req.setAttribute("canBook", viewer != null && "CUSTOMER".equals(viewer.getRole())
                && w.isVerified() && w.isAvailable() && w.isUserActive());
        req.setAttribute("recent", RecentCookie.load(req, workerDAO, w.getId()));
        view(req, resp, "worker-profile");
    }
}
