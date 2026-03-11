package com.example.schoolmanagementsystem.controller;

import com.example.schoolmanagementsystem.MainApp;
import com.example.schoolmanagementsystem.dao.UserDAO;
import com.example.schoolmanagementsystem.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label messageLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll("ADMIN", "TEACHER", "CLERK");
        roleCombo.setValue("CLERK");
        messageLabel.setText("");
    }

    @FXML
    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String email    = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();
        String role     = roleCombo.getValue();

        if (fullName.isEmpty()) { showMessage("⚠ Full name is required.", false); return; }
        if (username.isEmpty()) { showMessage("⚠ Username is required.", false); return; }
        if (username.length() < 3) { showMessage("⚠ Username must be at least 3 characters.", false); return; }
        if (password.isEmpty()) { showMessage("⚠ Password is required.", false); return; }
        if (password.length() < 6) { showMessage("⚠ Password must be at least 6 characters.", false); return; }
        if (!password.equals(confirm)) {
            showMessage("⚠ Passwords do not match.", false);
            confirmPasswordField.clear();
            return;
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setRole(role);
        user.setActive(true);

        if (userDAO.addUser(user)) {
            showMessage("✅ Account created! Redirecting to login...", true);
            new Thread(() -> {
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                javafx.application.Platform.runLater(this::goToLogin);
            }).start();
        } else {
            showMessage("❌ Username already exists. Please choose another.", false);
        }
    }

    @FXML
    private void goToLogin() {
        try { MainApp.navigateTo("login.fxml", "Login"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void showMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.setStyle(success
                ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }
}