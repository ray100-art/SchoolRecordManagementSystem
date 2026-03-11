package com.example.schoolmanagementsystem.dao;

import com.example.schoolmanagementsystem.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SubjectDAO - Database operations for Subjects
 */
public class SubjectDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public boolean addSubject(Subject s) {
        String sql = "INSERT INTO subjects (subject_code, subject_name, description, credit_hours, teacher_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getSubjectCode());
            ps.setString(2, s.getSubjectName());
            ps.setString(3, s.getDescription());
            ps.setInt(4, s.getCreditHours());
            ps.setObject(5, s.getTeacherId() == 0 ? null : s.getTeacherId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding subject: " + e.getMessage());
            return false;
        }
    }

    public boolean updateSubject(Subject s) {
        String sql = "UPDATE subjects SET subject_code=?, subject_name=?, description=?, credit_hours=?, teacher_id=? WHERE id=?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getSubjectCode());
            ps.setString(2, s.getSubjectName());
            ps.setString(3, s.getDescription());
            ps.setInt(4, s.getCreditHours());
            ps.setObject(5, s.getTeacherId() == 0 ? null : s.getTeacherId());
            ps.setInt(6, s.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating subject: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSubject(int id) {
        try (PreparedStatement ps = db.getConnection().prepareStatement("DELETE FROM subjects WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        String sql = """
            SELECT s.*, t.first_name || ' ' || t.last_name AS teacher_name
            FROM subjects s
            LEFT JOIN teachers t ON s.teacher_id = t.id
            ORDER BY s.subject_name
        """;
        try (ResultSet rs = db.getConnection().createStatement().executeQuery(sql)) {
            while (rs.next()) {
                Subject s = new Subject();
                s.setId(rs.getInt("id"));
                s.setSubjectCode(rs.getString("subject_code"));
                s.setSubjectName(rs.getString("subject_name"));
                s.setDescription(rs.getString("description"));
                s.setCreditHours(rs.getInt("credit_hours"));
                s.setTeacherId(rs.getInt("teacher_id"));
                s.setTeacherName(rs.getString("teacher_name") != null ? rs.getString("teacher_name") : "Unassigned");
                list.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
