package com.example.schoolmanagementsystem.dao;

import com.example.schoolmanagementsystem.model.Attendance;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * AttendanceDAO - All database operations for Attendance
 */
public class AttendanceDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public boolean markAttendance(Attendance a) {
        String sql = """
            INSERT OR REPLACE INTO attendance (student_id, class_group, date, status, remarks)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, a.getStudentId());
            ps.setString(2, a.getClassGroup());
            ps.setString(3, a.getDate().toString());
            ps.setString(4, a.getStatus());
            ps.setString(5, a.getRemarks());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error marking attendance: " + e.getMessage());
            return false;
        }
    }

    public List<Attendance> getAttendanceByDate(LocalDate date) {
        List<Attendance> list = new ArrayList<>();
        String sql = """
            SELECT a.*, s.first_name || ' ' || s.last_name AS student_name,
                   s.student_id AS student_ref, s.class_group
            FROM attendance a
            JOIN students s ON a.student_id = s.id
            WHERE a.date = ?
            ORDER BY s.first_name
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, date.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Attendance> getAttendanceForStudent(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = """
            SELECT a.*, s.first_name || ' ' || s.last_name AS student_name,
                   s.student_id AS student_ref, s.class_group
            FROM attendance a
            JOIN students s ON a.student_id = s.id
            WHERE a.student_id = ?
            ORDER BY a.date DESC
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Attendance> getAllAttendance() {
        List<Attendance> list = new ArrayList<>();
        String sql = """
            SELECT a.*, s.first_name || ' ' || s.last_name AS student_name,
                   s.student_id AS student_ref, s.class_group
            FROM attendance a
            JOIN students s ON a.student_id = s.id
            ORDER BY a.date DESC, s.first_name
        """;
        try (ResultSet rs = db.getConnection().createStatement().executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Returns attendance percentage for a student */
    public double getAttendancePercentage(int studentId) {
        String sql = """
            SELECT
                COUNT(*) AS total,
                SUM(CASE WHEN status='Present' THEN 1 ELSE 0 END) AS present
            FROM attendance WHERE student_id=?
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                int present = rs.getInt("present");
                if (total == 0) return 0;
                return (present * 100.0) / total;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setStudentName(rs.getString("student_name"));
        a.setStudentRef(rs.getString("student_ref"));
        a.setClassGroup(rs.getString("class_group"));
        String d = rs.getString("date");
        if (d != null) a.setDate(LocalDate.parse(d));
        a.setStatus(rs.getString("status"));
        a.setRemarks(rs.getString("remarks"));
        return a;
    }
}
