package com.example.schoolmanagementsystem.dao;

import com.example.schoolmanagementsystem.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDAO - All database operations for Students (PostgreSQL)
 */
public class StudentDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public boolean addStudent(Student s) {
        String sql = """
            INSERT INTO students (student_id, first_name, last_name, email, phone, gender,
            date_of_birth, address, class_group, guardian_name, guardian_phone,
            enrollment_date, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getStudentId());
            ps.setString(2, s.getFirstName());
            ps.setString(3, s.getLastName());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getGender());
            ps.setDate(7, s.getDateOfBirth() != null ? Date.valueOf(s.getDateOfBirth()) : null);
            ps.setString(8, s.getAddress());
            ps.setString(9, s.getClassGroup());
            ps.setString(10, s.getGuardianName());
            ps.setString(11, s.getGuardianPhone());
            ps.setDate(12, s.getEnrollmentDate() != null ? Date.valueOf(s.getEnrollmentDate()) : Date.valueOf(java.time.LocalDate.now()));
            ps.setString(13, s.getStatus());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStudent(Student s) {
        String sql = """
            UPDATE students SET first_name=?, last_name=?, email=?, phone=?, gender=?,
            date_of_birth=?, address=?, class_group=?, guardian_name=?, guardian_phone=?,
            enrollment_date=?, status=? WHERE id=?
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            ps.setString(5, s.getGender());
            ps.setDate(6, s.getDateOfBirth() != null ? Date.valueOf(s.getDateOfBirth()) : null);
            ps.setString(7, s.getAddress());
            ps.setString(8, s.getClassGroup());
            ps.setString(9, s.getGuardianName());
            ps.setString(10, s.getGuardianPhone());
            ps.setDate(11, s.getEnrollmentDate() != null ? Date.valueOf(s.getEnrollmentDate()) : null);
            ps.setString(12, s.getStatus());
            ps.setInt(13, s.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStudent(int id) {
        try (PreparedStatement ps = db.getConnection().prepareStatement("DELETE FROM students WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return false;
        }
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students ORDER BY first_name")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error fetching students: " + e.getMessage());
        }
        return list;
    }

    public List<Student> searchStudents(String query) {
        List<Student> list = new ArrayList<>();
        String sql = """
            SELECT * FROM students
            WHERE first_name ILIKE ? OR last_name ILIKE ?
               OR student_id ILIKE ? OR class_group ILIKE ? OR email ILIKE ?
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            String q = "%" + query + "%";
            ps.setString(1, q); ps.setString(2, q); ps.setString(3, q);
            ps.setString(4, q); ps.setString(5, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching students: " + e.getMessage());
        }
        return list;
    }

    public int getTotalCount() {
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students WHERE status='Active'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error getting student count: " + e.getMessage());
        }
        return 0;
    }

    // Fix #5: use MAX instead of COUNT to avoid duplicate IDs on concurrent inserts
    public String generateNextStudentId() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(student_id FROM 4) AS INTEGER)), 0) + 1 FROM students WHERE student_id ~ '^STD[0-9]+$'";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return String.format("STD%04d", rs.getInt(1));
        } catch (SQLException e) {
            System.err.println("Error generating student ID: " + e.getMessage());
        }
        return "STD0001";
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setStudentId(rs.getString("student_id"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setGender(rs.getString("gender"));
        Date dob = rs.getDate("date_of_birth");
        if (dob != null) s.setDateOfBirth(dob.toLocalDate());
        s.setAddress(rs.getString("address"));
        s.setClassGroup(rs.getString("class_group"));
        s.setGuardianName(rs.getString("guardian_name"));
        s.setGuardianPhone(rs.getString("guardian_phone"));
        Date enroll = rs.getDate("enrollment_date");
        if (enroll != null) s.setEnrollmentDate(enroll.toLocalDate());
        s.setStatus(rs.getString("status"));
        return s;
    }
}