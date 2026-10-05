package com.skillbridge.servlet;

import com.skillbridge.dao.SkillDAO;
import com.skillbridge.dao.WorkerDAO;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Search page. With ajax=1 it returns only the result cards (used for live filtering). */
@WebServlet("/workers")
public class WorkerSearchServlet extends BaseServlet {
    private final WorkerDAO workerDAO = new WorkerDAO();
    private final SkillDAO skillDAO = new SkillDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int skillId = Validator.toInt(req.getParameter("skill"), 0);
        String city = param(req, "city");
        String q = param(req, "q");
        String sort = param(req, "sort");

        req.setAttribute("workers", workerDAO.search(skillId, city, q, sort));
        req.setAttribute("skillId", skillId);
        req.setAttribute("city", city);
        req.setAttribute("q", q);
        req.setAttribute("sort", sort);

        if ("1".equals(req.getParameter("ajax"))) {
            req.getRequestDispatcher("/WEB-INF/views/fragments/workerCards.jsp").forward(req, resp);
            return;
        }
        req.setAttribute("skills", skillDAO.listAll());
        req.setAttribute("cities", workerDAO.cities());
        req.setAttribute("recent", RecentCookie.load(req, workerDAO, -1));
        view(req, resp, "workers");
    }
}
