package com.skillbridge.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** A booking together with the people, skill and payment details needed to display it. */
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a", Locale.ENGLISH);

    private int id;
    private int customerId;
    private String customerName;
    private String customerPhone;
    private int workerId;
    private int workerUserId;
    private String workerName;
    private String workerPhone;
    private String skillName;
    private String skillIcon;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int hours;
    private String address;
    private String notes;
    private BigDecimal hourlyRate;
    private BigDecimal subtotal;
    private BigDecimal platformFee;
    private BigDecimal total;
    private String status;
    private LocalDateTime createdAt;
    private String paymentStatus;
    private String paymentMethod;
    private String paymentDetail;
    private String txnRef;
    private int reviewRating;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public int getWorkerId() { return workerId; }
    public void setWorkerId(int workerId) { this.workerId = workerId; }
    public int getWorkerUserId() { return workerUserId; }
    public void setWorkerUserId(int workerUserId) { this.workerUserId = workerUserId; }
    public String getWorkerName() { return workerName; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }
    public String getWorkerPhone() { return workerPhone; }
    public void setWorkerPhone(String workerPhone) { this.workerPhone = workerPhone; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getSkillIcon() { return skillIcon; }
    public void setSkillIcon(String skillIcon) { this.skillIcon = skillIcon; }
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public int getHours() { return hours; }
    public void setHours(int hours) { this.hours = hours; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getPlatformFee() { return platformFee; }
    public void setPlatformFee(BigDecimal platformFee) { this.platformFee = platformFee; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentDetail() { return paymentDetail; }
    public void setPaymentDetail(String paymentDetail) { this.paymentDetail = paymentDetail; }
    public String getTxnRef() { return txnRef; }
    public void setTxnRef(String txnRef) { this.txnRef = txnRef; }
    public int getReviewRating() { return reviewRating; }
    public void setReviewRating(int reviewRating) { this.reviewRating = reviewRating; }

    public boolean isReviewed() { return reviewRating > 0; }
    public String getStatusClass() { return status == null ? "" : status.toLowerCase(Locale.ENGLISH); }
    public String getStatusLabel() {
        if (status == null) return "";
        return status.substring(0, 1) + status.substring(1).toLowerCase(Locale.ENGLISH);
    }
    public String getDateLabel() { return bookingDate == null ? "" : bookingDate.format(DATE); }
    public String getDateIso() { return bookingDate == null ? "" : bookingDate.toString(); }
    public String getStartLabel() { return startTime == null ? "" : startTime.format(TIME); }
    public String getEndLabel() { return endTime == null ? "" : endTime.format(TIME); }
    public String getCreatedLabel() { return createdAt == null ? "" : createdAt.format(STAMP); }
}
