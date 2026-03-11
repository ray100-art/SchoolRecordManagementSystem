package com.example.schoolmanagementsystem.controller;

import com.example.schoolmanagementsystem.MainApp;
import com.example.schoolmanagementsystem.dao.SubjectDAO;
import com.example.schoolmanagementsystem.dao.TeacherDAO;
import com.example.schoolmanagementsystem.model.Subject;
import com.example.schoolmanagementsystem.model.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

/**
 * Subject Controller
 */
public class SubjectController {

    @FXML private TableView<Subject> subjectTable;
    @FXML private TableColumn<Subject, String> colCode;
    @FXML private TableColumn<Subject, String> colName;
    @FXML private TableColumn<Subject, String> colTeacher;
    @FXML private TableColumn<Subject, Integer> colCredits;
    @FXML private TableColumn<Subject, String> colDescription;

    @FXML private TextField subjectCodeField;
    @FXML private TextField subjectNameField;
    @FXML private TextArea descriptionArea;
    @FXML private Spinner<Integer> creditSpinner;
    @FXML private ComboBox<Teacher> teacherCombo;
    @FXML private Label statusLabel;

    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private ObservableList<Subject> subjectList = FXCollections.observableArrayList();
    private Subject selectedSubject = null;

    @FXML
    public void initialize() {
        setupTable();
        loadData();
        creditSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 2));

        subjectTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateForm(sel);
        });
    }

    private void setupTable() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        colTeacher.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("creditHours"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        subjectTable.setItems(subjectList);
    }

    private void loadData() {
        subjectList.setAll(subjectDAO.getAllSubjects());
        teacherCombo.setItems(FXCollections.observableArrayList(teacherDAO.getAllTeachers()));
        setStatus("Loaded " + subjectList.size() + " subjects.");
    }

    @FXML
    private void handleAdd() {
        if (subjectCodeField.getText().trim().isEmpty() || subjectNameField.getText().trim().isEmpty()) {
            setStatus("⚠ Code and name are required."); return;
        }
        Subject s = buildFromForm();
        if (subjectDAO.addSubject(s)) { loadData(); clearForm(); setStatus("✅ Subject added."); }
        else setStatus("❌ Error. Code may already exist.");
    }

    @FXML
    private void handleUpdate() {
        if (selectedSubject == null) { setStatus("⚠ Select a subject to update."); return; }
        Subject s = buildFromForm();
        s.setId(selectedSubject.getId());
        if (subjectDAO.updateSubject(s)) { loadData(); clearForm(); setStatus("✅ Subject updated."); }
    }

    @FXML
    private void handleDelete() {
        Subject s = subjectTable.getSelectionModel().getSelectedItem();
        if (s == null) { setStatus("⚠ Select a subject to delete."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete subject " + s.getSubjectName() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            subjectDAO.deleteSubject(s.getId()); loadData(); clearForm(); setStatus("✅ Subject deleted.");
        }
    }

    @FXML private void handleClear() { clearForm(); }

    private Subject buildFromForm() {
        Subject s = new Subject();
        s.setSubjectCode(subjectCodeField.getText().trim());
        s.setSubjectName(subjectNameField.getText().trim());
        s.setDescription(descriptionArea.getText().trim());
        s.setCreditHours(creditSpinner.getValue());
        Teacher t = teacherCombo.getValue();
        if (t != null) s.setTeacherId(t.getId());
        return s;
    }

    private void populateForm(Subject s) {
        selectedSubject = s;
        subjectCodeField.setText(s.getSubjectCode());
        subjectNameField.setText(s.getSubjectName());
        descriptionArea.setText(s.getDescription());
        creditSpinner.getValueFactory().setValue(s.getCreditHours());
        teacherCombo.getItems().stream()
                .filter(t -> t.getId() == s.getTeacherId()).findFirst()
                .ifPresent(teacherCombo::setValue);
    }

    private void clearForm() {
        selectedSubject = null;
        subjectCodeField.clear(); subjectNameField.clear(); descriptionArea.clear();
        creditSpinner.getValueFactory().setValue(2); teacherCombo.setValue(null);
    }

    private void setStatus(String msg) { statusLabel.setText(msg); }

    @FXML private void goToDashboard() {
        try { MainApp.navigateTo("dashboard.fxml", "Dashboard"); } catch (Exception e) { e.printStackTrace(); }
    }
}
