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
            showError("Please enter both username and password.");
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
            showError("Invalid username or password. Please try again.");
            passwordField.clear();
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