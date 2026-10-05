package com.skillbridge.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** A registered account (customer, worker or admin). The password hash is kept out of the session copy. */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String city;
    private boolean active = true;
    private LocalDateTime createdAt;
    private String passwordHash;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getCreatedLabel() {
        return createdAt == null ? "" : createdAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH));
    }

    public String getInitials() {
        return Worker.initialsOf(fullName);
    }

    /** Copy without the password hash, safe to keep in the HTTP session. */
    public User forSession() {
        User u = new User();
        u.id = id; u.username = username; u.fullName = fullName; u.email = email; u.phone = phone;
        u.role = role; u.city = city; u.active = active; u.createdAt = createdAt;
        return u;
    }
}
