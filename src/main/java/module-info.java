module com.example.schoolmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.bootstrapfx.core;
    // Fix #7: explicitly require jbcrypt so the module system can resolve it
    requires jbcrypt;

    opens com.example.schoolmanagementsystem to javafx.fxml;
    opens com.example.schoolmanagementsystem.controller to javafx.fxml;
    opens com.example.schoolmanagementsystem.model to javafx.base;

    exports com.example.schoolmanagementsystem;
    exports com.example.schoolmanagementsystem.controller;
    exports com.example.schoolmanagementsystem.model;
    exports com.example.schoolmanagementsystem.dao;
}