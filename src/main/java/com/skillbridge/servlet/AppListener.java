package com.skillbridge.servlet;

import com.skillbridge.dao.DBUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.time.Year;

/** Runs when the application starts and stops. */
@WebListener
public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("appName", "SkillBridge");
        sce.getServletContext().setAttribute("year", Year.now().getValue());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBUtil.shutdown();
    }
}
