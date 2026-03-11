package com.example.schoolmanagementsystem.controller;

import com.example.schoolmanagementsystem.MainApp;
import com.example.schoolmanagementsystem.dao.StudentDAO;
import com.example.schoolmanagementsystem.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Student Controller - Full CRUD for Students
 */
public class StudentController {

    // Table
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colStudentId;
    @FXML private TableColumn<Student, String> colFirstName;
    @FXML private TableColumn<Student, String> colLastName;
    @FXML private TableColumn<Student, String> colEmail;
    @FXML private TableColumn<Student, String> colPhone;
    @FXML private TableColumn<Student, String> colClass;
    @FXML private TableColumn<Student, String> colStatus;

    // Form fields
    @FXML private TextField studentIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private DatePicker dobPicker;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> classCombo;
    @FXML private TextField guardianNameField;
    @FXML private TextField guardianPhoneField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;

    private final StudentDAO studentDAO = new StudentDAO();
    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private Student selectedStudent = null;

    @FXML
    public void initialize() {
        setupTable();
        setupCombos();
        loadStudents();
        studentIdField.setText(studentDAO.generateNextStudentId());

        // Select row listener
        studentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) populateForm(selected);
        });
    }

    private void setupTable() {
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colClass.setCellValueFactory(new PropertyValueFactory<>("classGroup"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Color-code status column
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "Active" -> "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
                    case "Inactive" -> "-fx-text-fill: #e74c3c; -fx-font-weight: bold;";
                    default -> "-fx-text-fill: #f39c12;";
                });
            }
        });

        studentTable.setItems(studentList);
    }

    private void setupCombos() {
        genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        classCombo.setItems(FXCollections.observableArrayList(
                "Form 1", "Form 2", "Form 3", "Form 4",
                "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6",
                "Grade 7", "Grade 8", "Grade 9"
        ));
        statusCombo.setItems(FXCollections.observableArrayList("Active", "Inactive", "Graduated", "Transferred"));
        statusCombo.setValue("Active");
    }

    private void loadStudents() {
        studentList.setAll(studentDAO.getAllStudents());
        setStatus("Loaded " + studentList.size() + " students.");
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) { loadStudents(); return; }
        List<Student> results = studentDAO.searchStudents(query);
        studentList.setAll(results);
        setStatus("Found " + results.size() + " results.");
    }

    @FXML
    private void handleAdd() {
        if (!validateForm()) return;
        Student s = buildStudentFromForm();
        if (studentDAO.addStudent(s)) {
            loadStudents();
            clearForm();
            setStatus("✅ Student added successfully.");
        } else {
            setStatus("❌ Error adding student. Student ID may already exist.");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedStudent == null) { setStatus("⚠ Select a student to update."); return; }
        if (!validateForm()) return;
        Student s = buildStudentFromForm();
        s.setId(selectedStudent.getId());
        if (studentDAO.updateStudent(s)) {
            loadStudents();
            clearForm();
            setStatus("✅ Student updated successfully.");
        } else {
            setStatus("❌ Error updating student.");
        }
    }

    @FXML
    private void handleDelete() {
        Student s = studentTable.getSelectionModel().getSelectedItem();
        if (s == null) { setStatus("⚠ Select a student to delete."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete student " + s.getFullName() + "? This cannot be undone.", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            if (studentDAO.deleteStudent(s.getId())) {
                loadStudents();
                clearForm();
                setStatus("✅ Student deleted.");
            }
        }
    }

    @FXML
    private void handleClear() { clearForm(); }

    private Student buildStudentFromForm() {
        Student s = new Student();
        s.setStudentId(studentIdField.getText().trim());
        s.setFirstName(firstNameField.getText().trim());
        s.setLastName(lastNameField.getText().trim());
        s.setEmail(emailField.getText().trim());
        s.setPhone(phoneField.getText().trim());
        s.setGender(genderCombo.getValue());
        s.setDateOfBirth(dobPicker.getValue());
        s.setAddress(addressField.getText().trim());
        s.setClassGroup(classCombo.getValue());
        s.setGuardianName(guardianNameField.getText().trim());
        s.setGuardianPhone(guardianPhoneField.getText().trim());
        s.setEnrollmentDate(LocalDate.now());
        s.setStatus(statusCombo.getValue() != null ? statusCombo.getValue() : "Active");
        return s;
    }

    private void populateForm(Student s) {
        selectedStudent = s;
        studentIdField.setText(s.getStudentId());
        firstNameField.setText(s.getFirstName());
        lastNameField.setText(s.getLastName());
        emailField.setText(s.getEmail());
        phoneField.setText(s.getPhone());
        genderCombo.setValue(s.getGender());
        dobPicker.setValue(s.getDateOfBirth());
        addressField.setText(s.getAddress());
        classCombo.setValue(s.getClassGroup());
        guardianNameField.setText(s.getGuardianName());
        guardianPhoneField.setText(s.getGuardianPhone());
        statusCombo.setValue(s.getStatus());
    }

    private void clearForm() {
        selectedStudent = null;
        studentIdField.setText(studentDAO.generateNextStudentId());
        firstNameField.clear(); lastNameField.clear(); emailField.clear();
        phoneField.clear(); genderCombo.setValue(null); dobPicker.setValue(null);
        addressField.clear(); classCombo.setValue(null); guardianNameField.clear();
        guardianPhoneField.clear(); statusCombo.setValue("Active");
    }

    private boolean validateForm() {
        if (firstNameField.getText().trim().isEmpty()) { setStatus("⚠ First name is required."); return false; }
        if (lastNameField.getText().trim().isEmpty()) { setStatus("⚠ Last name is required."); return false; }
        return true;
    }

    private void setStatus(String msg) { statusLabel.setText(msg); }

    @FXML private void goToDashboard() {
        try { MainApp.navigateTo("dashboard.fxml", "Dashboard"); }
        catch (Exception e) { e.printStackTrace(); }
    }
}
