package com.skillbridge.model;

import com.skillbridge.util.SlotRules;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** A worker profile joined with the account and skill it belongs to. */
public class Worker implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private int skillId;
    private String skillName;
    private String skillIcon;
    private String fullName;
    private String username;
    private String phone;
    private String email;
    private String city;
    private int experienceYears;
    private BigDecimal hourlyRate;
    private String bio;
    private LocalTime workStart;
    private LocalTime workEnd;
    private boolean available;
    private boolean verified;
    private boolean userActive = true;
    private double avgRating;
    private int reviewCount;
    private int jobsDone;
    private LocalDateTime joined;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getSkillIcon() { return skillIcon; }
    public void setSkillIcon(String skillIcon) { this.skillIcon = skillIcon; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public LocalTime getWorkStart() { return workStart; }
    public void setWorkStart(LocalTime workStart) { this.workStart = workStart; }
    public LocalTime getWorkEnd() { return workEnd; }
    public void setWorkEnd(LocalTime workEnd) { this.workEnd = workEnd; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public boolean isUserActive() { return userActive; }
    public void setUserActive(boolean userActive) { this.userActive = userActive; }
    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }
    public int getJobsDone() { return jobsDone; }
    public void setJobsDone(int jobsDone) { this.jobsDone = jobsDone; }
    public LocalDateTime getJoined() { return joined; }
    public void setJoined(LocalDateTime joined) { this.joined = joined; }

    public String getInitials() {
        return initialsOf(fullName);
    }

    public String getWorkStartText() {
        return workStart.toString().substring(0, 5);
    }

    public String getWorkEndText() {
        return workEnd.toString().substring(0, 5);
    }

    public String getWorkHoursLabel() {
        return SlotRules.label(workStart) + " - " + SlotRules.label(workEnd);
    }

    public String getJoinedLabel() {
        return joined == null ? "" : joined.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH));
    }

    /** Start times (every 30 minutes) that leave room for at least a one hour job. */
    public List<String> getTimeOptions() {
        List<String> list = new ArrayList<>();
        LocalTime t = workStart;
        while (!t.plusHours(1).isAfter(workEnd) && !t.plusHours(1).isBefore(t)) {
            list.add(t.toString().substring(0, 5));
            t = t.plusMinutes(30);
            if (t.equals(LocalTime.MIDNIGHT)) {
                break;
            }
        }
        return list;
    }

    static String initialsOf(String name) {
        if (name == null || name.isBlank()) {
            return "?";
        }
        String[] parts = name.trim().split("\\s+");
        String s = parts[0].substring(0, 1);
        if (parts.length > 1) {
            s += parts[parts.length - 1].substring(0, 1);
        }
        return s.toUpperCase();
    }
}
