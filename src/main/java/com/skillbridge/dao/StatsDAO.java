package com.skillbridge.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Counts and totals for the home page and the admin dashboard. */
public class StatsDAO {

    private Number scalar(Connection c, String sql) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Object o = rs.getObject(1);
                return o == null ? 0 : (Number) o;
            }
            return 0;
        }
    }

    public Map<String, Object> publicStats() {
        Map<String, Object> m = new LinkedHashMap<>();
        try (Connection c = DBUtil.getConnection()) {
            m.put("workers", scalar(c, "SELECT COUNT(*) FROM workers w JOIN users u ON u.id = w.user_id WHERE w.verified = 1 AND u.active = 1").intValue());
            m.put("jobs", scalar(c, "SELECT COUNT(*) FROM bookings WHERE status = 'COMPLETED'").intValue());
            m.put("cities", scalar(c, "SELECT COUNT(DISTINCT city) FROM workers WHERE verified = 1").intValue());
            double avg = scalar(c, "SELECT AVG(rating) FROM reviews").doubleValue();
            m.put("rating", Math.round(avg * 10.0) / 10.0);
            return m;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load statistics", e);
        }
    }

    public Map<String, Object> adminStats() {
        Map<String, Object> m = new LinkedHashMap<>();
        try (Connection c = DBUtil.getConnection()) {
            m.put("users", scalar(c, "SELECT COUNT(*) FROM users").intValue());
            m.put("customers", scalar(c, "SELECT COUNT(*) FROM users WHERE role = 'CUSTOMER'").intValue());
            m.put("workers", scalar(c, "SELECT COUNT(*) FROM workers").intValue());
            m.put("pending", scalar(c, "SELECT COUNT(*) FROM workers WHERE verified = 0").intValue());
            m.put("bookings", scalar(c, "SELECT COUNT(*) FROM bookings").intValue());
            m.put("processed", scalar(c, "SELECT COALESCE(SUM(amount),0) FROM payments WHERE status = 'SUCCESS'").doubleValue());
            m.put("earnings", scalar(c, "SELECT COALESCE(SUM(platform_fee),0) FROM bookings WHERE status = 'COMPLETED'").doubleValue());
            m.put("refunded", scalar(c, "SELECT COALESCE(SUM(amount),0) FROM payments WHERE status = 'REFUNDED'").doubleValue());

            Map<String, Integer> byStatus = new LinkedHashMap<>();
            for (String s : new String[]{"PENDING", "ACCEPTED", "COMPLETED", "CANCELLED"}) {
                byStatus.put(s, 0);
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT status, COUNT(*) FROM bookings GROUP BY status");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    byStatus.put(rs.getString(1), rs.getInt(2));
                }
            }
            m.put("byStatus", byStatus);

            List<Map<String, Object>> bySkill = new ArrayList<>();
            int max = 1;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT s.name, s.icon, COUNT(b.id) AS cnt FROM skills s LEFT JOIN workers w ON w.skill_id = s.id "
                            + "LEFT JOIN bookings b ON b.worker_id = w.id GROUP BY s.id, s.name, s.icon ORDER BY cnt DESC, s.name");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("name", rs.getString(1));
                    row.put("icon", rs.getString(2));
                    int cnt = rs.getInt(3);
                    row.put("count", cnt);
                    max = Math.max(max, cnt);
                    bySkill.add(row);
                }
            }
            for (Map<String, Object> row : bySkill) {
                row.put("percent", Math.round(((Integer) row.get("count")) * 100.0 / max));
            }
            m.put("bySkill", bySkill);
            return m;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load statistics", e);
        }
    }
}
