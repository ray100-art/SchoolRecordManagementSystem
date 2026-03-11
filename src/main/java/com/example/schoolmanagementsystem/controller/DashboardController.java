package com.example.schoolmanagementsystem.controller;

import com.example.schoolmanagementsystem.MainApp;
import com.example.schoolmanagementsystem.dao.*;
import com.example.schoolmanagementsystem.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Dashboard Controller - Shows summary statistics
 */
public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalTeachersLabel;
    @FXML private Label totalSubjectsLabel;
    @FXML private Label totalFeesLabel;
    @FXML private Label userRoleLabel;

    private final StudentDAO studentDAO = new StudentDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final FeePaymentDAO feeDAO = new FeePaymentDAO();

    @FXML
    public void initialize() {
        User currentUser = User.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getFullName() + "!");
            userRoleLabel.setText("Role: " + currentUser.getRole());
        }
        loadStats();
    }

    private void loadStats() {
        totalStudentsLabel.setText(String.valueOf(studentDAO.getTotalCount()));
        totalTeachersLabel.setText(String.valueOf(teacherDAO.getTotalCount()));
        totalSubjectsLabel.setText(String.valueOf(subjectDAO.getAllSubjects().size()));

        NumberFormat ksh = NumberFormat.getCurrencyInstance(new Locale("en", "KE"));
        totalFeesLabel.setText(ksh.format(feeDAO.getTotalCollected()));
    }

    @FXML private void goToStudents() { navigate("students.fxml", "Students"); }
    @FXML private void goToTeachers() { navigate("teachers.fxml", "Teachers"); }
    @FXML private void goToSubjects() { navigate("subjects.fxml", "Subjects"); }
    @FXML private void goToGrades() { navigate("grades.fxml", "Grades"); }
    @FXML private void goToAttendance() { navigate("attendance.fxml", "Attendance"); }
    @FXML private void goToFees() { navigate("fees.fxml", "Fee Payments"); }

    @FXML
    private void handleLogout() {
        User.logout();
        navigate("login.fxml", "Login");
    }

    private void navigate(String fxml, String title) {
        try { MainApp.navigateTo(fxml, title); }
        catch (Exception e) { e.printStackTrace(); }
    }
}
