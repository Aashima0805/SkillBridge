package com.skillbridge.dao;

import com.skillbridge.model.Review;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    private Review map(ResultSet rs) throws SQLException {
        Review r = new Review();
        r.setId(rs.getInt("id"));
        r.setBookingId(rs.getInt("booking_id"));
        r.setCustomerId(rs.getInt("customer_id"));
        r.setWorkerId(rs.getInt("worker_id"));
        r.setCustomerName(rs.getString("customer_name"));
        r.setWorkerName(rs.getString("worker_name"));
        r.setSkillName(rs.getString("skill_name"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return r;
    }

    private static final String BASE =
            "SELECT r.id, r.booking_id, r.customer_id, r.worker_id, r.rating, r.comment, r.created_at, "
                    + "cu.full_name AS customer_name, wu.full_name AS worker_name, s.name AS skill_name "
                    + "FROM reviews r JOIN users cu ON cu.id = r.customer_id JOIN workers w ON w.id = r.worker_id "
                    + "JOIN users wu ON wu.id = w.user_id JOIN skills s ON s.id = w.skill_id ";

    /**
     * Saves a review only if the booking belongs to this customer and is COMPLETED.
     * Returns false if the rule fails or the booking was already reviewed.
     */
    public boolean add(int bookingId, int customerId, int rating, String comment) {
        String sql = "INSERT INTO reviews (booking_id, customer_id, worker_id, rating, comment) "
                + "SELECT id, customer_id, worker_id, ?, ? FROM bookings WHERE id = ? AND customer_id = ? AND status = 'COMPLETED'";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, bookingId);
            ps.setInt(4, customerId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                return false; // duplicate review
            }
            throw new DataAccessException("Could not save review", e);
        }
    }

    public List<Review> listByWorker(int workerId, int limit) {
        return query("WHERE r.worker_id = ? ORDER BY r.created_at DESC, r.id DESC LIMIT ?", workerId, limit);
    }

    /** Recent good reviews for the home page testimonials. */
    public List<Review> latestGood(int limit) {
        return query("WHERE r.rating >= 4 AND r.comment <> '' ORDER BY r.created_at DESC, r.id DESC LIMIT ?", limit);
    }

    private List<Review> query(String tail, Object... params) {
        List<Review> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(BASE + tail)) {
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
            throw new DataAccessException("Could not load reviews", e);
        }
    }
}
