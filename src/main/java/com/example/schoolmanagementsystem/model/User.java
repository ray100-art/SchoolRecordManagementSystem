package com.example.schoolmanagementsystem.model;

import javafx.beans.property.*;

/**
 * User Model - for login/authentication
 */
public class User {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty passwordHash = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty(); // ADMIN, TEACHER, CLERK
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final BooleanProperty active = new SimpleBooleanProperty(true);

    // Singleton session user
    private static User currentUser;

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User u) { currentUser = u; }
    public static void logout() { currentUser = null; }

    public User() {}

    public boolean isAdmin() { return "ADMIN".equals(role.get()); }
    public boolean isTeacher() { return "TEACHER".equals(role.get()); }

    public int getId() { return id.get(); }
    public void setId(int v) { this.id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public String getUsername() { return username.get(); }
    public void setUsername(String v) { this.username.set(v); }
    public StringProperty usernameProperty() { return username; }

    public String getPasswordHash() { return passwordHash.get(); }
    public void setPasswordHash(String v) { this.passwordHash.set(v); }
    public StringProperty passwordHashProperty() { return passwordHash; }

    public String getRole() { return role.get(); }
    public void setRole(String v) { this.role.set(v); }
    public StringProperty roleProperty() { return role; }

    public String getFullName() { return fullName.get(); }
    public void setFullName(String v) { this.fullName.set(v); }
    public StringProperty fullNameProperty() { return fullName; }

    public String getEmail() { return email.get(); }
    public void setEmail(String v) { this.email.set(v); }
    public StringProperty emailProperty() { return email; }

    public boolean isActive() { return active.get(); }
    public void setActive(boolean v) { this.active.set(v); }
    public BooleanProperty activeProperty() { return active; }
}
