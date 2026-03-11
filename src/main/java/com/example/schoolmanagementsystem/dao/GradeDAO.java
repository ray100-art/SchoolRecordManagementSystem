package com.example.schoolmanagementsystem.dao;

import com.example.schoolmanagementsystem.model.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GradeDAO - All database operations for Grades / Marks
 */
public class GradeDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public boolean addGrade(Grade g) {
        String sql = """
            INSERT INTO grades (student_id, subject_id, marks_obtained, total_marks,
            grade, term, exam_type, academic_year, remarks)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, g.getStudentId());
            ps.setInt(2, g.getSubjectId());
            ps.setDouble(3, g.getMarksObtained());
            ps.setDouble(4, g.getTotalMarks());
            ps.setString(5, g.getGrade());
            ps.setString(6, g.getTerm());
            ps.setString(7, g.getExamType());
            ps.setString(8, g.getAcademicYear());
            ps.setString(9, g.getRemarks());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding grade: " + e.getMessage());
            return false;
        }
    }

    public boolean updateGrade(Grade g) {
        String sql = """
            UPDATE grades SET marks_obtained=?, total_marks=?, grade=?, term=?,
            exam_type=?, academic_year=?, remarks=? WHERE id=?
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, g.getMarksObtained());
            ps.setDouble(2, g.getTotalMarks());
            ps.setString(3, g.getGrade());
            ps.setString(4, g.getTerm());
            ps.setString(5, g.getExamType());
            ps.setString(6, g.getAcademicYear());
            ps.setString(7, g.getRemarks());
            ps.setInt(8, g.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating grade: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteGrade(int id) {
        try (PreparedStatement ps = db.getConnection().prepareStatement("DELETE FROM grades WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    public List<Grade> getAllGrades() {
        List<Grade> list = new ArrayList<>();
        String sql = """
            SELECT g.*, s.first_name || ' ' || s.last_name AS student_name,
                   s.student_id AS student_ref, sub.subject_name
            FROM grades g
            JOIN students s ON g.student_id = s.id
            JOIN subjects sub ON g.subject_id = sub.id
            ORDER BY s.first_name
        """;
        try (ResultSet rs = db.getConnection().createStatement().executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Grade> getGradesForStudent(int studentId) {
        List<Grade> list = new ArrayList<>();
        String sql = """
            SELECT g.*, s.first_name || ' ' || s.last_name AS student_name,
                   s.student_id AS student_ref, sub.subject_name
            FROM grades g
            JOIN students s ON g.student_id = s.id
            JOIN subjects sub ON g.subject_id = sub.id
            WHERE g.student_id = ?
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Grade mapRow(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setId(rs.getInt("id"));
        g.setStudentId(rs.getInt("student_id"));
        g.setStudentName(rs.getString("student_name"));
        g.setStudentRef(rs.getString("student_ref"));
        g.setSubjectId(rs.getInt("subject_id"));
        g.setSubjectName(rs.getString("subject_name"));
        g.setMarksObtained(rs.getDouble("marks_obtained"));
        g.setTotalMarks(rs.getDouble("total_marks"));
        g.setGrade(rs.getString("grade"));
        g.setTerm(rs.getString("term"));
        g.setExamType(rs.getString("exam_type"));
        g.setAcademicYear(rs.getString("academic_year"));
        g.setRemarks(rs.getString("remarks"));
        return g;
    }
}
