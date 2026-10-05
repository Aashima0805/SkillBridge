package com.skillbridge.dao;

import com.skillbridge.model.Skill;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO {

    private Skill map(ResultSet rs, boolean withCount) throws SQLException {
        Skill s = new Skill();
        s.setId(rs.getInt("id"));
        s.setName(rs.getString("name"));
        s.setIcon(rs.getString("icon"));
        s.setDescription(rs.getString("description"));
        if (withCount) {
            s.setWorkerCount(rs.getInt("worker_count"));
        }
        return s;
    }

    public List<Skill> listAll() {
        List<Skill> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, name, icon, description FROM skills ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs, false));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Could not list skills", e);
        }
    }

    /** Skills with the number of verified workers in each (used on the home page and admin screen). */
    public List<Skill> listWithCounts() {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.icon, s.description, COUNT(w.id) AS worker_count FROM skills s "
                + "LEFT JOIN workers w ON w.skill_id = s.id AND w.verified = 1 GROUP BY s.id, s.name, s.icon, s.description ORDER BY s.id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs, true));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Could not list skills", e);
        }
    }

    public Skill findById(int id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, name, icon, description FROM skills WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs, false) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load skill", e);
        }
    }

    public boolean nameExists(String name, int excludeId) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT 1 FROM skills WHERE name = ? AND id <> ?")) {
            ps.setString(1, name);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not check skill", e);
        }
    }

    public void create(String name, String icon, String description) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO skills (name, icon, description) VALUES (?,?,?)")) {
            ps.setString(1, name);
            ps.setString(2, icon);
            ps.setString(3, description);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not add skill", e);
        }
    }

    public void update(int id, String name, String icon, String description) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE skills SET name = ?, icon = ?, description = ? WHERE id = ?")) {
            ps.setString(1, name);
            ps.setString(2, icon);
            ps.setString(3, description);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update skill", e);
        }
    }

    /** Returns false (and deletes nothing) when workers still use this skill. */
    public boolean delete(int id) {
        try (Connection c = DBUtil.getConnection()) {
            try (PreparedStatement chk = c.prepareStatement("SELECT COUNT(*) FROM workers WHERE skill_id = ?")) {
                chk.setInt(1, id);
                try (ResultSet rs = chk.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        return false;
                    }
                }
            }
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM skills WHERE id = ?")) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete skill", e);
        }
    }
}
