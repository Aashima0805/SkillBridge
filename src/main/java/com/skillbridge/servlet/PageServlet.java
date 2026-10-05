package com.skillbridge.servlet;

import com.skillbridge.dao.SkillDAO;
import com.skillbridge.dao.WorkerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Small pages that need little logic: About, XML demo and the Session and Cookies demo. */
@WebServlet(urlPatterns = {"/about", "/xml-workers", "/session-info"})
public class PageServlet extends BaseServlet {
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a", Locale.ENGLISH);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/xml-workers" -> {
                req.setAttribute("skills", new SkillDAO().listAll());
                req.setAttribute("cities", new WorkerDAO().cities());
                view(req, resp, "xml-workers");
            }
            case "/session-info" -> sessionInfo(req, resp);
            default -> view(req, resp, "about");
        }
    }

    /** Experiment 4: shows the session and every cookie the browser sent, and counts visits with a cookie. */
    private void sessionInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int visits = 0;
        List<String[]> cookies = new ArrayList<>();
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("sb_visits".equals(c.getName())) {
                    try {
                        visits = Integer.parseInt(c.getValue());
                    } catch (NumberFormatException ignored) {
                        visits = 0;
                    }
                }
                String value = c.getValue();
                if (value.length() > 28) {
                    value = value.substring(0, 28) + "...";
                }
                cookies.add(new String[]{c.getName(), value});
            }
        }
        visits++;
        Cookie counter = new Cookie("sb_visits", String.valueOf(visits));
        counter.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        counter.setMaxAge(365 * 24 * 3600);
        resp.addCookie(counter);

        HttpSession s = req.getSession(true);
        ZoneId zone = com.skillbridge.util.Config.zone();
        Map<String, String> info = new LinkedHashMap<>();
        String id = s.getId();
        info.put("Session ID", id.substring(0, Math.min(8, id.length())) + "... (shortened)");
        info.put("New session?", String.valueOf(s.isNew()));
        info.put("Created", LocalDateTime.ofInstant(Instant.ofEpochMilli(s.getCreationTime()), zone).format(STAMP));
        info.put("Last accessed", LocalDateTime.ofInstant(Instant.ofEpochMilli(s.getLastAccessedTime()), zone).format(STAMP));
        info.put("Timeout", (s.getMaxInactiveInterval() / 60) + " minutes of inactivity");
        List<String> attrs = new ArrayList<>(Collections.list(s.getAttributeNames()));
        attrs.remove("csrf");
        Collections.sort(attrs);
        req.setAttribute("sessionInfo", info);
        req.setAttribute("sessionAttrs", attrs);
        req.setAttribute("cookies", cookies);
        req.setAttribute("visits", visits);
        view(req, resp, "session-info");
    }
}
