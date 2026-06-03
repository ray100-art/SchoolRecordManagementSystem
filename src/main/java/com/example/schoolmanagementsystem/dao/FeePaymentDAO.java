package com.example.schoolmanagementsystem.dao;

import com.example.schoolmanagementsystem.model.FeePayment;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * FeePaymentDAO - Database operations for Fee Payments (PostgreSQL)
 */
public class FeePaymentDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public boolean addPayment(FeePayment f) {
        String sql = """
            INSERT INTO fee_payments (student_id, amount, amount_paid, balance, fee_type,
            term, academic_year, payment_date, payment_method, receipt_number, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, f.getStudentId());
            ps.setDouble(2, f.getAmount());
            ps.setDouble(3, f.getAmountPaid());
            ps.setDouble(4, f.getBalance());
            ps.setString(5, f.getFeeType());
            ps.setString(6, f.getTerm());
            ps.setString(7, f.getAcademicYear());
            ps.setDate(8, Date.valueOf(f.getPaymentDate() != null ? f.getPaymentDate() : LocalDate.now()));
            ps.setString(9, f.getPaymentMethod());
            ps.setString(10, f.getReceiptNumber());
            ps.setString(11, f.getStatus());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding payment: " + e.getMessage());
            return false;
        }
    }

    public List<FeePayment> getAllPayments() {
        List<FeePayment> list = new ArrayList<>();
        String sql = """
            SELECT fp.*, s.first_name || ' ' || s.last_name AS student_name,
                   s.student_id AS student_ref, s.class_group
            FROM fee_payments fp
            JOIN students s ON fp.student_id = s.id
            ORDER BY fp.payment_date DESC
        """;
        // Fix #4: use try-with-resources for Statement
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<FeePayment> getPaymentsForStudent(int studentId) {
        List<FeePayment> list = new ArrayList<>();
        String sql = """
            SELECT fp.*, s.first_name || ' ' || s.last_name AS student_name,
                   s.student_id AS student_ref, s.class_group
            FROM fee_payments fp
            JOIN students s ON fp.student_id = s.id
            WHERE fp.student_id = ?
            ORDER BY fp.payment_date DESC
        """;
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public double getTotalCollected() {
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COALESCE(SUM(amount_paid), 0) FROM fee_payments")) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double getTotalOutstanding() {
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COALESCE(SUM(balance), 0) FROM fee_payments WHERE status != 'Paid'")) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Fix #5: use MAX instead of COUNT to avoid duplicate receipt numbers
    public String generateReceiptNumber() {
        String sql = "SELECT COALESCE(MAX(CAST(SUBSTRING(receipt_number FROM 4) AS INTEGER)), 0) + 1 FROM fee_payments WHERE receipt_number ~ '^RCP[0-9]+$'";
        try (Statement stmt = db.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return String.format("RCP%06d", rs.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return "RCP000001";
    }

    private FeePayment mapRow(ResultSet rs) throws SQLException {
        FeePayment f = new FeePayment();
        f.setId(rs.getInt("id"));
        f.setStudentId(rs.getInt("student_id"));
        f.setStudentName(rs.getString("student_name"));
        f.setStudentRef(rs.getString("student_ref"));
        f.setClassGroup(rs.getString("class_group"));
        f.setAmount(rs.getDouble("amount"));
        f.setAmountPaid(rs.getDouble("amount_paid"));
        f.setBalance(rs.getDouble("balance"));
        f.setFeeType(rs.getString("fee_type"));
        f.setTerm(rs.getString("term"));
        f.setAcademicYear(rs.getString("academic_year"));
        Date pd = rs.getDate("payment_date");
        if (pd != null) f.setPaymentDate(pd.toLocalDate());
        f.setPaymentMethod(rs.getString("payment_method"));
        f.setReceiptNumber(rs.getString("receipt_number"));
        f.setStatus(rs.getString("status"));
        return f;
    }
}