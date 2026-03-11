package com.example.schoolmanagementsystem.controller;

import com.example.schoolmanagementsystem.MainApp;
import com.example.schoolmanagementsystem.dao.TeacherDAO;
import com.example.schoolmanagementsystem.model.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Optional;

/**
 * Teacher Controller - Full CRUD for Teachers
 */
public class TeacherController {

    @FXML private TableView<Teacher> teacherTable;
    @FXML private TableColumn<Teacher, String> colTeacherId;
    @FXML private TableColumn<Teacher, String> colFirstName;
    @FXML private TableColumn<Teacher, String> colLastName;
    @FXML private TableColumn<Teacher, String> colSubject;
    @FXML private TableColumn<Teacher, String> colPhone;
    @FXML private TableColumn<Teacher, Double> colSalary;
    @FXML private TableColumn<Teacher, String> colStatus;

    @FXML private TextField teacherIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private TextField subjectField;
    @FXML private TextField qualificationField;
    @FXML private DatePicker hireDatePicker;
    @FXML private TextField addressField;
    @FXML private TextField salaryField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;

    private final TeacherDAO teacherDAO = new TeacherDAO();
    private ObservableList<Teacher> teacherList = FXCollections.observableArrayList();
    private Teacher selectedTeacher = null;

    @FXML
    public void initialize() {
        setupTable();
        setupCombos();
        loadTeachers();
        teacherIdField.setText(teacherDAO.generateNextTeacherId());

        teacherTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) populateForm(selected);
        });
    }

    private void setupTable() {
        colTeacherId.setCellValueFactory(new PropertyValueFactory<>("teacherId"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        teacherTable.setItems(teacherList);
    }

    private void setupCombos() {
        genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        statusCombo.setItems(FXCollections.observableArrayList("Active", "Inactive", "On Leave"));
        statusCombo.setValue("Active");
    }

    private void loadTeachers() {
        teacherList.setAll(teacherDAO.getAllTeachers());
        setStatus("Loaded " + teacherList.size() + " teachers.");
    }

    @FXML
    private void handleSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) { loadTeachers(); return; }
        List<Teacher> results = teacherDAO.searchTeachers(q);
        teacherList.setAll(results);
        setStatus("Found " + results.size() + " results.");
    }

    @FXML
    private void handleAdd() {
        if (!validateForm()) return;
        Teacher t = buildTeacherFromForm();
        if (teacherDAO.addTeacher(t)) {
            loadTeachers(); clearForm();
            setStatus("✅ Teacher added successfully.");
        } else {
            setStatus("❌ Error adding teacher.");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedTeacher == null) { setStatus("⚠ Select a teacher to update."); return; }
        if (!validateForm()) return;
        Teacher t = buildTeacherFromForm();
        t.setId(selectedTeacher.getId());
        if (teacherDAO.updateTeacher(t)) {
            loadTeachers(); clearForm();
            setStatus("✅ Teacher updated successfully.");
        } else {
            setStatus("❌ Error updating teacher.");
        }
    }

    @FXML
    private void handleDelete() {
        Teacher t = teacherTable.getSelectionModel().getSelectedItem();
        if (t == null) { setStatus("⚠ Select a teacher to delete."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete teacher " + t.getFullName() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            if (teacherDAO.deleteTeacher(t.getId())) { loadTeachers(); clearForm(); setStatus("✅ Teacher deleted."); }
        }
    }

    @FXML private void handleClear() { clearForm(); }

    private Teacher buildTeacherFromForm() {
        Teacher t = new Teacher();
        t.setTeacherId(teacherIdField.getText().trim());
        t.setFirstName(firstNameField.getText().trim());
        t.setLastName(lastNameField.getText().trim());
        t.setEmail(emailField.getText().trim());
        t.setPhone(phoneField.getText().trim());
        t.setGender(genderCombo.getValue());
        t.setSubject(subjectField.getText().trim());
        t.setQualification(qualificationField.getText().trim());
        t.setHireDate(hireDatePicker.getValue());
        t.setAddress(addressField.getText().trim());
        try { t.setSalary(Double.parseDouble(salaryField.getText().trim())); } catch (NumberFormatException e) { t.setSalary(0); }
        t.setStatus(statusCombo.getValue() != null ? statusCombo.getValue() : "Active");
        return t;
    }

    private void populateForm(Teacher t) {
        selectedTeacher = t;
        teacherIdField.setText(t.getTeacherId());
        firstNameField.setText(t.getFirstName());
        lastNameField.setText(t.getLastName());
        emailField.setText(t.getEmail());
        phoneField.setText(t.getPhone());
        genderCombo.setValue(t.getGender());
        subjectField.setText(t.getSubject());
        qualificationField.setText(t.getQualification());
        hireDatePicker.setValue(t.getHireDate());
        addressField.setText(t.getAddress());
        salaryField.setText(String.valueOf(t.getSalary()));
        statusCombo.setValue(t.getStatus());
    }

    private void clearForm() {
        selectedTeacher = null;
        teacherIdField.setText(teacherDAO.generateNextTeacherId());
        firstNameField.clear(); lastNameField.clear(); emailField.clear();
        phoneField.clear(); genderCombo.setValue(null); subjectField.clear();
        qualificationField.clear(); hireDatePicker.setValue(null); addressField.clear();
        salaryField.clear(); statusCombo.setValue("Active");
    }

    private boolean validateForm() {
        if (firstNameField.getText().trim().isEmpty()) { setStatus("⚠ First name is required."); return false; }
        if (lastNameField.getText().trim().isEmpty()) { setStatus("⚠ Last name is required."); return false; }
        return true;
    }

    private void setStatus(String msg) { statusLabel.setText(msg); }

    @FXML private void goToDashboard() {
        try { MainApp.navigateTo("dashboard.fxml", "Dashboard"); } catch (Exception e) { e.printStackTrace(); }
    }
}
