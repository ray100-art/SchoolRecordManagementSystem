package com.example.schoolmanagementsystem.dao;

import com.example.schoolmanagementsystem.model.Teacher;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * TeacherDAO - All database operations for Teachers
 */
public class TeacherDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public boolean addTeacher(Teacher t) {
        String sql = """
            INSERT INTO teachers (teacher_id, first_name, last_name, email, phone, gender,
            subject, qualification, hire_date, address, salary, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getTeacherId());
            ps.setString(2, t.getFirstName());
            ps.setString(3, t.getLastName());
            ps.setString(4, t.getEmail());
            ps.setString(5, t.getPhone());
            ps.setString(6, t.getGender());
            ps.setString(7, t.getSubject());
            ps.setString(8, t.getQualification());
            ps.setString(9, t.getHireDate() != null ? t.getHireDate().toString() : LocalDate.now().toString());
            ps.setString(10, t.getAddress());
            ps.setDouble(11, t.getSalary());
            ps.setString(12, t.getStatus());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding teacher: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTeacher(Teacher t) {
        String sql = """
            UPDATE teachers SET first_name=?, last_name=?, email=?, phone=?, gender=?,
            subject=?, qualification=?, hire_date=?, address=?, salary=?, status=? WHERE id=?
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, t.getFirstName());
            ps.setString(2, t.getLastName());
            ps.setString(3, t.getEmail());
            ps.setString(4, t.getPhone());
            ps.setString(5, t.getGender());
            ps.setString(6, t.getSubject());
            ps.setString(7, t.getQualification());
            ps.setString(8, t.getHireDate() != null ? t.getHireDate().toString() : null);
            ps.setString(9, t.getAddress());
            ps.setDouble(10, t.getSalary());
            ps.setString(11, t.getStatus());
            ps.setInt(12, t.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating teacher: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTeacher(int id) {
        try (PreparedStatement ps = db.getConnection().prepareStatement("DELETE FROM teachers WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting teacher: " + e.getMessage());
            return false;
        }
    }

    public List<Teacher> getAllTeachers() {
        List<Teacher> list = new ArrayList<>();
        try (ResultSet rs = db.getConnection().createStatement().executeQuery("SELECT * FROM teachers ORDER BY first_name")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error fetching teachers: " + e.getMessage());
        }
        return list;
    }

    public List<Teacher> searchTeachers(String query) {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT * FROM teachers WHERE first_name LIKE ? OR last_name LIKE ? OR teacher_id LIKE ? OR subject LIKE ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            String q = "%" + query + "%";
            ps.setString(1, q); ps.setString(2, q); ps.setString(3, q); ps.setString(4, q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int getTotalCount() {
        try (ResultSet rs = db.getConnection().createStatement().executeQuery("SELECT COUNT(*) FROM teachers WHERE status='Active'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public String generateNextTeacherId() {
        try (ResultSet rs = db.getConnection().createStatement().executeQuery("SELECT COUNT(*) FROM teachers")) {
            if (rs.next()) return String.format("TCH%04d", rs.getInt(1) + 1);
        } catch (SQLException e) { e.printStackTrace(); }
        return "TCH0001";
    }

    private Teacher mapRow(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setId(rs.getInt("id"));
        t.setTeacherId(rs.getString("teacher_id"));
        t.setFirstName(rs.getString("first_name"));
        t.setLastName(rs.getString("last_name"));
        t.setEmail(rs.getString("email"));
        t.setPhone(rs.getString("phone"));
        t.setGender(rs.getString("gender"));
        t.setSubject(rs.getString("subject"));
        t.setQualification(rs.getString("qualification"));
        String hd = rs.getString("hire_date");
        if (hd != null && !hd.isEmpty()) t.setHireDate(LocalDate.parse(hd));
        t.setAddress(rs.getString("address"));
        t.setSalary(rs.getDouble("salary"));
        t.setStatus(rs.getString("status"));
        return t;
    }
}
