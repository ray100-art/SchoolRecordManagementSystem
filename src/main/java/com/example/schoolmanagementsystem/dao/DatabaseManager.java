package com.example.schoolmanagementsystem.dao;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * DatabaseManager - PostgreSQL connection manager (Singleton)
 * Reads connection settings from src/main/resources/db.properties
 */
public class DatabaseManager {

    private static DatabaseManager instance;
    private Connection connection;
    private final Object connectionLock = new Object();

    private String jdbcUrl;
    private String dbUsername;
    private String dbPassword;

    private DatabaseManager() {
        loadConfig();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void loadConfig() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) throw new RuntimeException("db.properties not found on classpath.");
            props.load(in);
            String host   = props.getProperty("db.host",     "localhost");
            String port   = props.getProperty("db.port",     "5432");
            String dbName = props.getProperty("db.name",     "school_management");
            dbUsername    = props.getProperty("db.username", "postgres");
            dbPassword    = props.getProperty("db.password", "");
            jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
            System.out.println("✅ DB config loaded: " + jdbcUrl);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties: " + e.getMessage(), e);
        }
    }

    // Fix #2: synchronized connection retrieval to prevent race conditions
    public Connection getConnection() throws SQLException {
        synchronized (connectionLock) {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
                connection.setAutoCommit(true);
            }
            return connection;
        }
    }

    public void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // ---- USERS TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id              SERIAL PRIMARY KEY,
                    username        TEXT UNIQUE NOT NULL,
                    password_hash   TEXT NOT NULL,
                    role            TEXT NOT NULL DEFAULT 'CLERK',
                    full_name       TEXT,
                    email           TEXT,
                    active          BOOLEAN DEFAULT TRUE,
                    failed_attempts INTEGER DEFAULT 0,
                    locked          BOOLEAN DEFAULT FALSE,
                    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            runSafe(stmt, "ALTER TABLE users ADD COLUMN IF NOT EXISTS failed_attempts INTEGER DEFAULT 0");
            runSafe(stmt, "ALTER TABLE users ADD COLUMN IF NOT EXISTS locked BOOLEAN DEFAULT FALSE");

            // ---- STUDENTS TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS students (
                    id              SERIAL PRIMARY KEY,
                    student_id      TEXT UNIQUE NOT NULL,
                    first_name      TEXT NOT NULL,
                    last_name       TEXT NOT NULL,
                    email           TEXT,
                    phone           TEXT,
                    gender          TEXT,
                    date_of_birth   DATE,
                    address         TEXT,
                    class_group     TEXT,
                    guardian_name   TEXT,
                    guardian_phone  TEXT,
                    enrollment_date DATE,
                    status          TEXT DEFAULT 'Active',
                    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ---- TEACHERS TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS teachers (
                    id            SERIAL PRIMARY KEY,
                    teacher_id    TEXT UNIQUE NOT NULL,
                    first_name    TEXT NOT NULL,
                    last_name     TEXT NOT NULL,
                    email         TEXT,
                    phone         TEXT,
                    gender        TEXT,
                    subject       TEXT,
                    qualification TEXT,
                    hire_date     DATE,
                    address       TEXT,
                    salary        REAL DEFAULT 0,
                    status        TEXT DEFAULT 'Active',
                    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ---- SUBJECTS TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS subjects (
                    id           SERIAL PRIMARY KEY,
                    subject_code TEXT UNIQUE NOT NULL,
                    subject_name TEXT NOT NULL,
                    description  TEXT,
                    credit_hours INTEGER DEFAULT 1,
                    teacher_id   INTEGER REFERENCES teachers(id) ON DELETE SET NULL
                )
            """);

            // ---- GRADES TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS grades (
                    id             SERIAL PRIMARY KEY,
                    student_id     INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
                    subject_id     INTEGER NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
                    marks_obtained REAL NOT NULL,
                    total_marks    REAL DEFAULT 100,
                    grade          TEXT,
                    term           TEXT,
                    exam_type      TEXT,
                    academic_year  TEXT,
                    remarks        TEXT,
                    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ---- ATTENDANCE TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS attendance (
                    id          SERIAL PRIMARY KEY,
                    student_id  INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
                    class_group TEXT,
                    date        DATE NOT NULL,
                    status      TEXT NOT NULL,
                    remarks     TEXT,
                    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE (student_id, date)
                )
            """);

            // ---- FEE PAYMENTS TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS fee_payments (
                    id             SERIAL PRIMARY KEY,
                    student_id     INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
                    amount         REAL NOT NULL,
                    amount_paid    REAL NOT NULL,
                    balance        REAL,
                    fee_type       TEXT,
                    term           TEXT,
                    academic_year  TEXT,
                    payment_date   DATE,
                    payment_method TEXT,
                    receipt_number TEXT UNIQUE,
                    status         TEXT DEFAULT 'Unpaid',
                    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // ---- ANNOUNCEMENTS TABLE ----
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS announcements (
                    id              SERIAL PRIMARY KEY,
                    title           TEXT NOT NULL,
                    content         TEXT,
                    target_audience TEXT DEFAULT 'All',
                    created_by      INTEGER REFERENCES users(id) ON DELETE SET NULL,
                    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Fix #1: properly close ResultSet and PreparedStatement
            // ---- SEED DEFAULT ADMIN (password: Admin@2024) — BCrypt hashed ----
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE username = 'admin'")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String hashed = BCrypt.hashpw("Admin@2024", BCrypt.gensalt(12));
                    try (PreparedStatement ps = conn.prepareStatement("""
                        INSERT INTO users (username, password_hash, role, full_name, email)
                        VALUES ('admin', ?, 'ADMIN', 'System Administrator', 'admin@school.ac.ke')
                    """)) {
                        ps.setString(1, hashed);
                        ps.executeUpdate();
                        System.out.println("✅ Default admin created  →  username: admin  |  password: Admin@2024");
                    }
                }
            }

            System.out.println("✅ PostgreSQL database initialized successfully.");

        } catch (SQLException e) {
            System.err.println("❌ Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void runSafe(Statement stmt, String sql) {
        try { stmt.execute(sql); } catch (SQLException ignored) {}
    }

    public void closeConnection() {
        synchronized (connectionLock) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("🔒 Database connection closed.");
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}