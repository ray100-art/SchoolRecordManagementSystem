package com.example.schoolmanagementsystem;

import com.example.schoolmanagementsystem.dao.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main Application Entry Point
 * School Management System - BrianNdungu
 */
public class MainApp extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Initialize database on startup
        DatabaseManager.getInstance().initializeDatabase();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        scene.getStylesheets().add(MainApp.class.getResource("css/styles.css").toExternalForm());

        stage.setTitle("School Management System");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setResizable(true);
        stage.show();
    }

    /**
     * Navigate to a new scene
     */
    public static void navigateTo(String fxmlFile, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("fxml/" + fxmlFile));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(MainApp.class.getResource("css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("School Management System - " + title);
    }

    public static void main(String[] args) {
        launch();
    }
}
