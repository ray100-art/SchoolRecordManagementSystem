package com.example.schoolmanagementsystem.dao;

import com.example.schoolmanagementsystem.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO - Authentication and user management
 */
public class UserDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password_hash=? AND active=1";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password); // In production, use BCrypt hashing
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean addUser(User u) {
        String sql = "INSERT INTO users (username, password_hash, role, full_name, email, active) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getRole());
            ps.setString(4, u.getFullName());
            ps.setString(5, u.getEmail());
            ps.setInt(6, u.isActive() ? 1 : 0);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        try (ResultSet rs = db.getConnection().createStatement().executeQuery("SELECT * FROM users ORDER BY full_name")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean changePassword(int userId, String newPassword) {
        try (PreparedStatement ps = db.getConnection().prepareStatement("UPDATE users SET password_hash=? WHERE id=?")) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setActive(rs.getInt("active") == 1);
        return u;
    }
}
