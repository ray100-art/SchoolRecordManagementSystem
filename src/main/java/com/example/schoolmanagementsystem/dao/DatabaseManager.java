package com.example.schoolmanagementsystem.dao;

import java.sql.*;

/**
 * DatabaseManager - Singleton SQLite connection manager
 * Creates and manages all tables for the School Management System
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:school_management.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    /**
     * Creates all tables if they don't exist and seeds default admin user
     */
    public void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // ---- USERS TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    role TEXT NOT NULL DEFAULT 'CLERK',
                    full_name TEXT,
                    email TEXT,
                    active INTEGER DEFAULT 1,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ---- STUDENTS TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id TEXT UNIQUE NOT NULL,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    email TEXT,
                    phone TEXT,
                    gender TEXT,
                    date_of_birth TEXT,
                    address TEXT,
                    class_group TEXT,
                    guardian_name TEXT,
                    guardian_phone TEXT,
                    enrollment_date TEXT,
                    status TEXT DEFAULT 'Active',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ---- TEACHERS TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS teachers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    teacher_id TEXT UNIQUE NOT NULL,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    email TEXT,
                    phone TEXT,
                    gender TEXT,
                    subject TEXT,
                    qualification TEXT,
                    hire_date TEXT,
                    address TEXT,
                    salary REAL DEFAULT 0,
                    status TEXT DEFAULT 'Active',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ---- SUBJECTS TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS subjects (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    subject_code TEXT UNIQUE NOT NULL,
                    subject_name TEXT NOT NULL,
                    description TEXT,
                    credit_hours INTEGER DEFAULT 1,
                    teacher_id INTEGER,
                    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
                )
            """);

            // ---- GRADES TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS grades (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER NOT NULL,
                    subject_id INTEGER NOT NULL,
                    marks_obtained REAL NOT NULL,
                    total_marks REAL DEFAULT 100,
                    grade TEXT,
                    term TEXT,
                    exam_type TEXT,
                    academic_year TEXT,
                    remarks TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (student_id) REFERENCES students(id),
                    FOREIGN KEY (subject_id) REFERENCES subjects(id)
                )
            """);

            // ---- ATTENDANCE TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS attendance (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER NOT NULL,
                    class_group TEXT,
                    date TEXT NOT NULL,
                    status TEXT NOT NULL,
                    remarks TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (student_id) REFERENCES students(id),
                    UNIQUE(student_id, date)
                )
            """);

            // ---- FEE PAYMENTS TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS fee_payments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER NOT NULL,
                    amount REAL NOT NULL,
                    amount_paid REAL NOT NULL,
                    balance REAL,
                    fee_type TEXT,
                    term TEXT,
                    academic_year TEXT,
                    payment_date TEXT,
                    payment_method TEXT,
                    receipt_number TEXT UNIQUE,
                    status TEXT DEFAULT 'Unpaid',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (student_id) REFERENCES students(id)
                )
            """);

            // ---- ANNOUNCEMENTS TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS announcements (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    content TEXT,
                    target_audience TEXT DEFAULT 'All',
                    created_by INTEGER,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (created_by) REFERENCES users(id)
                )
            """);

            // ---- SEED DEFAULT ADMIN USER (password: admin123) ----
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE username = 'admin'");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("""
                    INSERT INTO users (username, password_hash, role, full_name, email)
                    VALUES ('admin', 'admin123', 'ADMIN', 'System Administrator', 'admin@school.ac.ke')
                """);
                System.out.println("✅ Default admin user created: admin / admin123");
            }

            System.out.println("✅ Database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("❌ Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
