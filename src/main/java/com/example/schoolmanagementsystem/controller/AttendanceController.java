package com.example.schoolmanagementsystem.controller;

import com.example.schoolmanagementsystem.MainApp;
import com.example.schoolmanagementsystem.dao.AttendanceDAO;
import com.example.schoolmanagementsystem.dao.StudentDAO;
import com.example.schoolmanagementsystem.model.Attendance;
import com.example.schoolmanagementsystem.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Attendance Controller
 */
public class AttendanceController {

    @FXML private TableView<Attendance> attendanceTable;
    @FXML private TableColumn<Attendance, String> colStudentName;
    @FXML private TableColumn<Attendance, String> colStudentRef;
    @FXML private TableColumn<Attendance, String> colClass;
    @FXML private TableColumn<Attendance, LocalDate> colDate;
    @FXML private TableColumn<Attendance, String> colStatus;
    @FXML private TableColumn<Attendance, String> colRemarks;

    @FXML private ComboBox<Student> studentCombo;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField remarksField;
    @FXML private DatePicker filterDatePicker;
    @FXML private Label statusLabel;
    @FXML private Label statsLabel;

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        setupCombos();
        loadAttendance();
        datePicker.setValue(LocalDate.now());
        filterDatePicker.setValue(LocalDate.now());
    }

    private void setupTable() {
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colStudentRef.setCellValueFactory(new PropertyValueFactory<>("studentRef"));
        colClass.setCellValueFactory(new PropertyValueFactory<>("classGroup"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "Present" -> "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
                    case "Absent"  -> "-fx-text-fill: #e74c3c; -fx-font-weight: bold;";
                    case "Late"    -> "-fx-text-fill: #f39c12; -fx-font-weight: bold;";
                    default        -> "-fx-text-fill: #8e44ad; -fx-font-weight: bold;";
                });
            }
        });

        attendanceTable.setItems(attendanceList);
    }

    private void setupCombos() {
        studentCombo.setItems(FXCollections.observableArrayList(studentDAO.getAllStudents()));
        statusCombo.setItems(FXCollections.observableArrayList("Present", "Absent", "Late", "Excused"));
        statusCombo.setValue("Present");
    }

    private void loadAttendance() {
        attendanceList.setAll(attendanceDAO.getAllAttendance());
        setStatus("Loaded " + attendanceList.size() + " records.");
    }

    @FXML
    private void handleFilterByDate() {
        LocalDate date = filterDatePicker.getValue();
        if (date == null) { loadAttendance(); return; }
        List<Attendance> records = attendanceDAO.getAttendanceByDate(date);
        attendanceList.setAll(records);

        long present = records.stream().filter(a -> "Present".equals(a.getStatus())).count();
        long absent  = records.stream().filter(a -> "Absent".equals(a.getStatus())).count();
        statsLabel.setText(String.format("Date: %s | Present: %d | Absent: %d | Total: %d",
                date, present, absent, records.size()));
        setStatus("Showing records for " + date);
    }

    @FXML
    private void handleMark() {
        Student s = studentCombo.getValue();
        LocalDate date = datePicker.getValue();
        String status = statusCombo.getValue();

        if (s == null) { setStatus("⚠ Select a student."); return; }
        if (date == null) { setStatus("⚠ Select a date."); return; }
        if (status == null) { setStatus("⚠ Select a status."); return; }

        Attendance a = new Attendance();
        a.setStudentId(s.getId());
        a.setClassGroup(s.getClassGroup());
        a.setDate(date);
        a.setStatus(status);
        a.setRemarks(remarksField.getText().trim());

        if (attendanceDAO.markAttendance(a)) {
            loadAttendance();
            remarksField.clear();
            setStatus("✅ Attendance marked: " + s.getFullName() + " - " + status);
        } else {
            setStatus("❌ Error marking attendance.");
        }
    }

    @FXML
    private void handleShowAll() {
        loadAttendance();
        statsLabel.setText("");
    }

    private void setStatus(String msg) { statusLabel.setText(msg); }

    @FXML private void goToDashboard() {
        try { MainApp.navigateTo("dashboard.fxml", "Dashboard"); } catch (Exception e) { e.printStackTrace(); }
    }
}
