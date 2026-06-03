package com.example.schoolmanagementsystem.dao;

import com.example.schoolmanagementsystem.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO - Authentication and user management (PostgreSQL + BCrypt)
 */
public class UserDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    // ----------------------------------------------------------------
    // AUTHENTICATION
    // ----------------------------------------------------------------

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                User user = mapRow(rs);

                if (!user.isActive()) return null;
                if (user.isLocked()) return null;

                if (BCrypt.checkpw(password, user.getPasswordHash())) {
                    resetFailedAttempts(username);
                    return user;
                } else {
                    incrementFailedAttempts(username);
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Returns true if the account is currently locked */
    public boolean isAccountLocked(String username) {
        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT locked FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBoolean("locked");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // Fix #3: expose DB-stored failed attempt count so LoginController
    // always shows accurate remaining-attempts feedback after restarts
    public int getFailedAttempts(String username) {
        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "SELECT failed_attempts FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("failed_attempts");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private void incrementFailedAttempts(String username) {
        String sql = """
            UPDATE users
            SET failed_attempts = failed_attempts + 1,
                locked = CASE WHEN failed_attempts + 1 >= 5 THEN TRUE ELSE FALSE END
            WHERE username = ?
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void resetFailedAttempts(String username) {
        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "UPDATE users SET failed_attempts = 0, locked = FALSE WHERE username = ?")) {
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------

    public boolean addUser(User u) {
        String sql = "INSERT INTO users (username, password_hash, role, full_name, email, active) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, BCrypt.hashpw(u.getPasswordHash(), BCrypt.gensalt(12)));
            ps.setString(3, u.getRole());
            ps.setString(4, u.getFullName());
            ps.setString(5, u.getEmail());
            ps.setBoolean(6, u.isActive());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    public boolean changePassword(int userId, String newPassword) {
        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "UPDATE users SET password_hash = ?, failed_attempts = 0, locked = FALSE WHERE id = ?")) {
            ps.setString(1, hashed);
            ps.setInt(2, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public boolean unlockAccount(String username) {
        try (PreparedStatement ps = db.getConnection().prepareStatement(
                "UPDATE users SET locked = FALSE, failed_attempts = 0 WHERE username = ?")) {
            ps.setString(1, username);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY full_name")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ----------------------------------------------------------------
    // MAPPING
    // ----------------------------------------------------------------

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setActive(rs.getBoolean("active"));
        u.setFailedAttempts(rs.getInt("failed_attempts"));
        u.setLocked(rs.getBoolean("locked"));
        return u;
    }
}