package com.skillbridge.dao;

import com.skillbridge.model.Booking;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private static final String BASE =
            "SELECT b.id, b.customer_id, b.worker_id, b.booking_date, b.start_time, b.end_time, b.hours, b.address, b.notes, "
                    + "b.hourly_rate, b.subtotal, b.platform_fee, b.total, b.status, b.created_at, "
                    + "cu.full_name AS customer_name, cu.phone AS customer_phone, w.user_id AS worker_user_id, "
                    + "wu.full_name AS worker_name, wu.phone AS worker_phone, s.name AS skill_name, s.icon AS skill_icon, "
                    + "p.status AS pay_status, p.method AS pay_method, p.detail AS pay_detail, p.txn_ref AS txn_ref, "
                    + "COALESCE(r.rating, 0) AS review_rating "
                    + "FROM bookings b JOIN users cu ON cu.id = b.customer_id JOIN workers w ON w.id = b.worker_id "
                    + "JOIN users wu ON wu.id = w.user_id JOIN skills s ON s.id = w.skill_id "
                    + "LEFT JOIN payments p ON p.booking_id = b.id LEFT JOIN reviews r ON r.booking_id = b.id ";

    private Booking map(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setCustomerId(rs.getInt("customer_id"));
        b.setCustomerName(rs.getString("customer_name"));
        b.setCustomerPhone(rs.getString("customer_phone"));
        b.setWorkerId(rs.getInt("worker_id"));
        b.setWorkerUserId(rs.getInt("worker_user_id"));
        b.setWorkerName(rs.getString("worker_name"));
        b.setWorkerPhone(rs.getString("worker_phone"));
        b.setSkillName(rs.getString("skill_name"));
        b.setSkillIcon(rs.getString("skill_icon"));
        b.setBookingDate(rs.getDate("booking_date").toLocalDate());
        b.setStartTime(rs.getTime("start_time").toLocalTime());
        b.setEndTime(rs.getTime("end_time").toLocalTime());
        b.setHours(rs.getInt("hours"));
        b.setAddress(rs.getString("address"));
        b.setNotes(rs.getString("notes"));
        b.setHourlyRate(rs.getBigDecimal("hourly_rate"));
        b.setSubtotal(rs.getBigDecimal("subtotal"));
        b.setPlatformFee(rs.getBigDecimal("platform_fee"));
        b.setTotal(rs.getBigDecimal("total"));
        b.setStatus(rs.getString("status"));
        b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        b.setPaymentStatus(rs.getString("pay_status"));
        b.setPaymentMethod(rs.getString("pay_method"));
        b.setPaymentDetail(rs.getString("pay_detail"));
        b.setTxnRef(rs.getString("txn_ref"));
        b.setReviewRating(rs.getInt("review_rating"));
        return b;
    }

    private List<Booking> query(String tail, Object... params) {
        List<Booking> list = new ArrayList<>();
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
            throw new DataAccessException("Could not load bookings", e);
        }
    }

    public Booking findById(int id) {
        List<Booking> list = query("WHERE b.id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Booking> listForCustomer(int userId) {
        return query("WHERE b.customer_id = ? ORDER BY b.booking_date DESC, b.start_time DESC", userId);
    }

    public List<Booking> listForWorker(int workerId) {
        return query("WHERE b.worker_id = ? ORDER BY FIELD(b.status,'PENDING','ACCEPTED','COMPLETED','CANCELLED'), b.booking_date, b.start_time", workerId);
    }

    public List<Booking> listAll(int limit) {
        return query("ORDER BY b.created_at DESC, b.id DESC LIMIT ?", limit);
    }

    /** Pending/accepted jobs of a worker on one day (used to explain why a slot is busy). */
    public List<Booking> busySlots(int workerId, LocalDate date) {
        return query("WHERE b.worker_id = ? AND b.booking_date = ? AND b.status IN ('PENDING','ACCEPTED') ORDER BY b.start_time",
                workerId, java.sql.Date.valueOf(date));
    }

    private boolean slotFree(Connection c, int workerId, LocalDate date, LocalTime start, LocalTime end) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE worker_id = ? AND booking_date = ? AND status IN ('PENDING','ACCEPTED') "
                + "AND start_time < ? AND end_time > ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, workerId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setTime(3, Time.valueOf(end));
            ps.setTime(4, Time.valueOf(start));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 0;
            }
        }
    }

    public boolean isSlotFree(int workerId, LocalDate date, LocalTime start, LocalTime end) {
        try (Connection c = DBUtil.getConnection()) {
            return slotFree(c, workerId, date, start, end);
        } catch (SQLException e) {
            throw new DataAccessException("Could not check availability", e);
        }
    }

    /**
     * Saves the booking and its payment in ONE transaction. The worker row is locked first so two
     * customers cannot grab the same slot at the same moment.
     */
    public int createPaid(Booking b, String method, String detail, String txnRef) throws SlotTakenException {
        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement lock = c.prepareStatement("SELECT id FROM workers WHERE id = ? FOR UPDATE")) {
                    lock.setInt(1, b.getWorkerId());
                    lock.executeQuery().close();
                }
                if (!slotFree(c, b.getWorkerId(), b.getBookingDate(), b.getStartTime(), b.getEndTime())) {
                    c.rollback();
                    throw new SlotTakenException("Sorry, that time slot was just booked by someone else.");
                }
                int bookingId;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO bookings (customer_id, worker_id, booking_date, start_time, end_time, hours, address, notes, "
                                + "hourly_rate, subtotal, platform_fee, total, status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?, 'PENDING')",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, b.getCustomerId());
                    ps.setInt(2, b.getWorkerId());
                    ps.setDate(3, java.sql.Date.valueOf(b.getBookingDate()));
                    ps.setTime(4, Time.valueOf(b.getStartTime()));
                    ps.setTime(5, Time.valueOf(b.getEndTime()));
                    ps.setInt(6, b.getHours());
                    ps.setString(7, b.getAddress());
                    ps.setString(8, b.getNotes());
                    ps.setBigDecimal(9, b.getHourlyRate());
                    ps.setBigDecimal(10, b.getSubtotal());
                    ps.setBigDecimal(11, b.getPlatformFee());
                    ps.setBigDecimal(12, b.getTotal());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        bookingId = keys.getInt(1);
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO payments (booking_id, amount, method, detail, txn_ref, status) VALUES (?,?,?,?,?, 'SUCCESS')")) {
                    ps.setInt(1, bookingId);
                    ps.setBigDecimal(2, b.getTotal());
                    ps.setString(3, method);
                    ps.setString(4, detail);
                    ps.setString(5, txnRef);
                    ps.executeUpdate();
                }
                c.commit();
                return bookingId;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not save booking", e);
        }
    }

    /** Moves a booking from one status to another. Returns false if it was not in the expected status. */
    public boolean transition(int id, String from, String to) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE bookings SET status = ? WHERE id = ? AND status = ?")) {
            ps.setString(1, to);
            ps.setInt(2, id);
            ps.setString(3, from);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException("Could not update booking", e);
        }
    }

    /** Cancels a PENDING or ACCEPTED booking and refunds its payment, atomically. */
    public boolean cancelWithRefund(int id) {
        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try {
                int changed;
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE bookings SET status = 'CANCELLED' WHERE id = ? AND status IN ('PENDING','ACCEPTED')")) {
                    ps.setInt(1, id);
                    changed = ps.executeUpdate();
                }
                if (changed == 1) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE payments SET status = 'REFUNDED' WHERE booking_id = ? AND status = 'SUCCESS'")) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                    }
                }
                c.commit();
                return changed == 1;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not cancel booking", e);
        }
    }
}
