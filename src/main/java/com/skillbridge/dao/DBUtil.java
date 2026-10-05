package com.skillbridge.dao;

import com.skillbridge.util.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;

/** JDBC connection pool (HikariCP) shared by every DAO. */
public final class DBUtil {
    private static volatile HikariDataSource dataSource;

    private DBUtil() {
    }

    private static HikariDataSource pool() {
        HikariDataSource local = dataSource;
        if (local == null) {
            synchronized (DBUtil.class) {
                local = dataSource;
                if (local == null) {
                    HikariConfig cfg = new HikariConfig();
                    cfg.setPoolName("SkillBridgePool");
                    cfg.setDriverClassName(Config.get("db.driver", "com.mysql.cj.jdbc.Driver"));
                    cfg.setJdbcUrl(Config.get("db.url", "jdbc:mysql://localhost:3306/skillbridge"));
                    cfg.setUsername(Config.get("db.user", "root"));
                    cfg.setPassword(Config.get("db.password", ""));
                    cfg.setMaximumPoolSize(Config.getInt("db.poolSize", 10));
                    cfg.setConnectionTimeout(10_000);
                    local = new HikariDataSource(cfg);
                    dataSource = local;
                }
            }
        }
        return local;
    }

    public static Connection getConnection() throws SQLException {
        return pool().getConnection();
    }

    /** Called when the web application stops, so Tomcat can unload it cleanly. */
    public static void shutdown() {
        HikariDataSource local = dataSource;
        if (local != null) {
            local.close();
            dataSource = null;
        }
        for (Driver d : Collections.list(DriverManager.getDrivers())) {
            if (d.getClass().getClassLoader() == DBUtil.class.getClassLoader()) {
                try {
                    DriverManager.deregisterDriver(d);
                } catch (SQLException ignored) {
                    // nothing more to do while shutting down
                }
            }
        }
    }
}
