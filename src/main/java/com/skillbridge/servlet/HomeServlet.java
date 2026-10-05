package com.skillbridge.servlet;

import com.skillbridge.dao.ReviewDAO;
import com.skillbridge.dao.SkillDAO;
import com.skillbridge.dao.StatsDAO;
import com.skillbridge.dao.WorkerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/home")
public class HomeServlet extends BaseServlet {
    private final SkillDAO skillDAO = new SkillDAO();
    private final WorkerDAO workerDAO = new WorkerDAO();
    private final StatsDAO statsDAO = new StatsDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("skills", skillDAO.listWithCounts());
        req.setAttribute("topWorkers", workerDAO.topRated(6));
        req.setAttribute("stats", statsDAO.publicStats());
        req.setAttribute("reviews", reviewDAO.latestGood(6));
        req.setAttribute("recent", RecentCookie.load(req, workerDAO, -1));
        view(req, resp, "home");
    }
}
