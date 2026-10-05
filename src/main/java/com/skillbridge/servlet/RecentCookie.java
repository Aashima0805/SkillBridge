package com.skillbridge.servlet;

import com.skillbridge.dao.WorkerDAO;
import com.skillbridge.model.Worker;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/** "Recently viewed workers", remembered in a plain cookie (ids only, e.g. 5-3-9). */
public final class RecentCookie {
    public static final String NAME = "sb_recent";
    private static final int MAX = 4;

    private RecentCookie() {
    }

    static List<Integer> ids(HttpServletRequest req) {
        List<Integer> list = new ArrayList<>();
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (NAME.equals(c.getName())) {
                    for (String part : c.getValue().split("-")) {
                        try {
                            list.add(Integer.parseInt(part));
                        } catch (NumberFormatException ignored) {
                            // skip anything that is not a number
                        }
                    }
                }
            }
        }
        return list;
    }

    public static void add(HttpServletRequest req, HttpServletResponse resp, int workerId) {
        List<Integer> list = ids(req);
        list.remove(Integer.valueOf(workerId));
        list.add(0, workerId);
        while (list.size() > MAX) {
            list.remove(list.size() - 1);
        }
        StringBuilder sb = new StringBuilder();
        for (int id : list) {
            if (sb.length() > 0) {
                sb.append('-');
            }
            sb.append(id);
        }
        Cookie c = new Cookie(NAME, sb.toString());
        c.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        c.setMaxAge(30 * 24 * 3600);
        resp.addCookie(c);
    }

    /** Loads the still-visible workers, most recent first, skipping one id (the page being viewed). */
    public static List<Worker> load(HttpServletRequest req, WorkerDAO dao, int skipId) {
        List<Worker> out = new ArrayList<>();
        for (int id : ids(req)) {
            if (id == skipId) {
                continue;
            }
            Worker w = dao.findById(id);
            if (w != null && w.isVerified() && w.isUserActive()) {
                out.add(w);
            }
        }
        return out;
    }
}
