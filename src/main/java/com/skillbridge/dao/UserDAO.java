package com.skillbridge.dao;

import com.skillbridge.model.User;
import com.skillbridge.model.Worker;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserDAO {
    private static final Set<String> UNIQUE_COLUMNS = Set.of("username", "email", "phone");
    private static final String COLS = "id, username, full_name, email, phone, password_hash, role, city, active, created_at";

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setCity(rs.getString("city"));
        u.setActive(rs.getBoolean("active"));
        u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return u;
    }

    public User findById(int id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLS + " FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load user", e);
        }
    }

    /** Login accepts either the username or the e-mail address. */
    public User findByLogin(String login) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLS + " FROM users WHERE username = ? OR email = ?")) {
            ps.setString(1, login);
            ps.setString(2, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not look up user", e);
        }
    }

    /** column must be username, email or phone (whitelisted, so no SQL injection through it). */
    public boolean exists(String column, String value) {
        if (!UNIQUE_COLUMNS.contains(column)) {
            throw new IllegalArgumentException("Unsupported column");
        }
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM users WHERE " + column + " = ?")) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not check " + column, e);
        }
    }

    /**
     * Creates the account and, for workers, the worker profile in ONE transaction:
     * either both rows are saved or neither is.
     */
    public int register(User u, String passwordHash, Worker w) {
        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                int userId;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO users (username, full_name, email, phone, password_hash, role, city) VALUES (?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, u.getUsername());
                    ps.setString(2, u.getFullName());
                    ps.setString(3, u.getEmail());
                    ps.setString(4, u.getPhone());
                    ps.setString(5, passwordHash);
                    ps.setString(6, u.getRole());
                    ps.setString(7, u.getCity());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        userId = keys.getInt(1);
                    }
                }
                if (w != null) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO workers (user_id, skill_id, experience_years, hourly_rate, city, bio, work_start, work_end, available, verified) "
                                    + "VALUES (?,?,?,?,?,?,?,?,1,0)")) {
                        ps.setInt(1, userId);
                        ps.setInt(2, w.getSkillId());
                        ps.setInt(3, w.getExperienceYears());
                        ps.setBigDecimal(4, w.getHourlyRate());
                        ps.setString(5, u.getCity());
                        ps.setString(6, w.getBio());
                        ps.setTime(7, Time.valueOf(w.getWorkStart()));
                        ps.setTime(8, Time.valueOf(w.getWorkEnd()));
                        ps.executeUpdate();
                    }
                }
                c.commit();
                return userId;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not register user", e);
        }
    }

    public List<User> listAll() {
        List<User> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLS + " FROM users ORDER BY role, id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Could not list users", e);
        }
    }

    public void setActive(int id, boolean active) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE users SET active = ? WHERE id = ? AND role <> 'ADMIN'")) {
            ps.setBoolean(1, active);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update user", e);
        }
    }
}
