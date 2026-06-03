package com.example.schoolmanagementsystem.controller;

import com.example.schoolmanagementsystem.MainApp;
import com.example.schoolmanagementsystem.dao.UserDAO;
import com.example.schoolmanagementsystem.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    private static final int MAX_ATTEMPTS = 5;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) passwordField.requestFocus();
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("⚠ Please enter both username and password.");
            return;
        }

        // Fix #3: check lock state from DB (not an in-memory counter) before attempting auth
        if (userDAO.isAccountLocked(username)) {
            showError("🔒 Account locked after too many failed attempts.\nContact your administrator to unlock it.");
            passwordField.clear();
            passwordField.setDisable(true);
            return;
        }

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            User.setCurrentUser(user);
            try {
                MainApp.navigateTo("dashboard.fxml", "Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error loading dashboard.");
            }
        } else {
            passwordField.clear();

            // Fix #3: read remaining attempts directly from the DB so the
            // count is always accurate even after an app restart
            if (userDAO.isAccountLocked(username)) {
                showError("🔒 Account locked after " + MAX_ATTEMPTS + " failed attempts.\nContact your administrator to unlock it.");
                passwordField.setDisable(true);
            } else {
                int remaining = MAX_ATTEMPTS - userDAO.getFailedAttempts(username);
                if (remaining > 0) {
                    showError("❌ Invalid username or password.\n" + remaining + " attempt(s) remaining before lockout.");
                } else {
                    showError("❌ Invalid username or password.");
                }
            }
        }
    }

    @FXML
    private void goToRegister() {
        try {
            MainApp.navigateTo("register.fxml", "Create Account");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}