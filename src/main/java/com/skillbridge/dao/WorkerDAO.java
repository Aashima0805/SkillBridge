package com.skillbridge.dao;

import com.skillbridge.model.Worker;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class WorkerDAO {
    private static final String BASE =
            "SELECT w.id, w.user_id, w.skill_id, s.name AS skill_name, s.icon AS skill_icon, u.full_name, u.username, u.phone, u.email, "
                    + "u.active AS user_active, u.created_at, w.city, w.experience_years, w.hourly_rate, w.bio, w.work_start, w.work_end, "
                    + "w.available, w.verified, COALESCE(r.avg_rating, 0) AS avg_rating, COALESCE(r.cnt, 0) AS review_count, COALESCE(j.done, 0) AS jobs_done "
                    + "FROM workers w JOIN users u ON u.id = w.user_id JOIN skills s ON s.id = w.skill_id "
                    + "LEFT JOIN (SELECT worker_id, AVG(rating) AS avg_rating, COUNT(*) AS cnt FROM reviews GROUP BY worker_id) r ON r.worker_id = w.id "
                    + "LEFT JOIN (SELECT worker_id, COUNT(*) AS done FROM bookings WHERE status = 'COMPLETED' GROUP BY worker_id) j ON j.worker_id = w.id ";
    private static final String PUBLIC_FILTER = "w.verified = 1 AND u.active = 1";

    private Worker map(ResultSet rs) throws SQLException {
        Worker w = new Worker();
        w.setId(rs.getInt("id"));
        w.setUserId(rs.getInt("user_id"));
        w.setSkillId(rs.getInt("skill_id"));
        w.setSkillName(rs.getString("skill_name"));
        w.setSkillIcon(rs.getString("skill_icon"));
        w.setFullName(rs.getString("full_name"));
        w.setUsername(rs.getString("username"));
        w.setPhone(rs.getString("phone"));
        w.setEmail(rs.getString("email"));
        w.setUserActive(rs.getBoolean("user_active"));
        w.setJoined(rs.getTimestamp("created_at").toLocalDateTime());
        w.setCity(rs.getString("city"));
        w.setExperienceYears(rs.getInt("experience_years"));
        w.setHourlyRate(rs.getBigDecimal("hourly_rate"));
        w.setBio(rs.getString("bio"));
        w.setWorkStart(rs.getTime("work_start").toLocalTime());
        w.setWorkEnd(rs.getTime("work_end").toLocalTime());
        w.setAvailable(rs.getBoolean("available"));
        w.setVerified(rs.getBoolean("verified"));
        w.setAvgRating(Math.round(rs.getDouble("avg_rating") * 10.0) / 10.0);
        w.setReviewCount(rs.getInt("review_count"));
        w.setJobsDone(rs.getInt("jobs_done"));
        return w;
    }

    private List<Worker> query(String sqlTail, Object... params) {
        List<Worker> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(BASE + sqlTail)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load workers", e);
        }
    }

    /** Public search: verified, active workers only. skillId 0 = any skill; blank city/keyword = no filter. */
    public List<Worker> search(int skillId, String city, String keyword, String sort) {
        StringBuilder where = new StringBuilder("WHERE " + PUBLIC_FILTER);
        List<Object> params = new ArrayList<>();
        if (skillId > 0) {
            where.append(" AND w.skill_id = ?");
            params.add(skillId);
        }
        if (city != null && !city.isBlank()) {
            where.append(" AND w.city = ?");
            params.add(city);
        }
        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND (u.full_name LIKE ? OR s.name LIKE ? OR w.city LIKE ? OR w.bio LIKE ?)");
            String like = "%" + keyword.trim() + "%";
            for (int i = 0; i < 4; i++) {
                params.add(like);
            }
        }
        String order;
        switch (sort == null ? "" : sort) {
            case "price_low" -> order = " ORDER BY w.hourly_rate ASC, avg_rating DESC";
            case "price_high" -> order = " ORDER BY w.hourly_rate DESC, avg_rating DESC";
            case "experience" -> order = " ORDER BY w.experience_years DESC, avg_rating DESC";
            default -> order = " ORDER BY avg_rating DESC, review_count DESC, w.id";
        }
        return query(where + order, params.toArray());
    }

    public List<Worker> topRated(int limit) {
        return query("WHERE " + PUBLIC_FILTER + " AND w.available = 1 ORDER BY avg_rating DESC, review_count DESC, w.id LIMIT ?", limit);
    }

    public Worker findById(int id) {
        List<Worker> list = query("WHERE w.id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Worker findByUserId(int userId) {
        List<Worker> list = query("WHERE w.user_id = ?", userId);
        return list.isEmpty() ? null : list.get(0);
    }

    /** Admin view: every worker, pending ones first. */
    public List<Worker> listAll() {
        return query("ORDER BY w.verified ASC, w.id");
    }

    public List<String> cities() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT DISTINCT w.city FROM workers w JOIN users u ON u.id = w.user_id WHERE " + PUBLIC_FILTER + " ORDER BY w.city";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Could not load cities", e);
        }
    }

    public void updateProfile(int workerId, int skillId, int experience, java.math.BigDecimal rate, String city,
                              String bio, LocalTime start, LocalTime end) {
        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE workers SET skill_id = ?, experience_years = ?, hourly_rate = ?, city = ?, bio = ?, work_start = ?, work_end = ? WHERE id = ?")) {
                    ps.setInt(1, skillId);
                    ps.setInt(2, experience);
                    ps.setBigDecimal(3, rate);
                    ps.setString(4, city);
                    ps.setString(5, bio);
                    ps.setTime(6, Time.valueOf(start));
                    ps.setTime(7, Time.valueOf(end));
                    ps.setInt(8, workerId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE users SET city = ? WHERE id = (SELECT user_id FROM workers WHERE id = ?)")) {
                    ps.setString(1, city);
                    ps.setInt(2, workerId);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not update profile", e);
        }
    }

    public void setVerified(int workerId, boolean verified) {
        setFlag("verified", workerId, verified);
    }

    public void setAvailable(int workerId, boolean available) {
        setFlag("available", workerId, available);
    }

    private void setFlag(String column, int workerId, boolean value) {
        String col = "verified".equals(column) ? "verified" : "available";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE workers SET " + col + " = ? WHERE id = ?")) {
            ps.setBoolean(1, value);
            ps.setInt(2, workerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update worker", e);
        }
    }
}
