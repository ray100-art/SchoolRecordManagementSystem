package com.example.schoolmanagementsystem.controller;

import com.example.schoolmanagementsystem.MainApp;
import com.example.schoolmanagementsystem.dao.GradeDAO;
import com.example.schoolmanagementsystem.dao.StudentDAO;
import com.example.schoolmanagementsystem.dao.SubjectDAO;
import com.example.schoolmanagementsystem.model.Grade;
import com.example.schoolmanagementsystem.model.Student;
import com.example.schoolmanagementsystem.model.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.util.Optional;

public class GradeController {

    @FXML private TableView<Grade> gradeTable;
    @FXML private TableColumn<Grade, String> colStudent;
    @FXML private TableColumn<Grade, String> colStudentRef;
    @FXML private TableColumn<Grade, String> colSubject;
    @FXML private TableColumn<Grade, Double> colMarks;
    @FXML private TableColumn<Grade, Double> colTotal;
    @FXML private TableColumn<Grade, String> colGrade;
    @FXML private TableColumn<Grade, String> colTerm;
    @FXML private TableColumn<Grade, String> colExamType;
    @FXML private TableColumn<Grade, String> colYear;

    @FXML private ComboBox<Student> studentCombo;
    @FXML private ComboBox<Subject> subjectCombo;
    @FXML private TextField marksField;
    @FXML private TextField totalMarksField;
    @FXML private ComboBox<String> termCombo;
    @FXML private ComboBox<String> examTypeCombo;
    @FXML private TextField academicYearField;
    @FXML private TextField remarksField;
    @FXML private Label gradePreviewLabel;
    @FXML private Label statusLabel;

    private final GradeDAO gradeDAO = new GradeDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final ObservableList<Grade> gradeList = FXCollections.observableArrayList();
    private Grade selectedGrade = null;

    @FXML
    public void initialize() {
        setupTable();
        setupCombos();
        loadGrades();

        gradeTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) populateForm(selected);
        });

        marksField.textProperty().addListener((obs, old, val) -> updateGradePreview());
        totalMarksField.textProperty().addListener((obs, old, val) -> updateGradePreview());
    }

    private void setupTable() {
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colStudentRef.setCellValueFactory(new PropertyValueFactory<>("studentRef"));
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        colMarks.setCellValueFactory(new PropertyValueFactory<>("marksObtained"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalMarks"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colTerm.setCellValueFactory(new PropertyValueFactory<>("term"));
        colExamType.setCellValueFactory(new PropertyValueFactory<>("examType"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("academicYear"));

        colGrade.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(item.startsWith("A") ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;" :
                        item.equals("F")     ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;" :
                                "-fx-text-fill: #2980b9; -fx-font-weight: bold;");
            }
        });

        gradeTable.setItems(gradeList);
    }

    private void setupCombos() {
        // Student ComboBox — shows full name + ID
        studentCombo.setItems(FXCollections.observableArrayList(studentDAO.getAllStudents()));
        studentCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Student s) {
                return s == null ? "" : s.getFullName() + " (" + s.getStudentId() + ")";
            }
            @Override public Student fromString(String str) { return null; }
        });
        studentCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Student s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? "" : s.getFullName() + " (" + s.getStudentId() + ")");
            }
        });
        studentCombo.setPromptText("-- Select Student --");

        // Subject ComboBox — shows name + code
        subjectCombo.setItems(FXCollections.observableArrayList(subjectDAO.getAllSubjects()));
        subjectCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Subject s) {
                return s == null ? "" : s.getSubjectName() + " [" + s.getSubjectCode() + "]";
            }
            @Override public Subject fromString(String str) { return null; }
        });
        subjectCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Subject s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? "" : s.getSubjectName() + " [" + s.getSubjectCode() + "]");
            }
        });
        subjectCombo.setPromptText("-- Select Subject --");

        termCombo.setItems(FXCollections.observableArrayList("Term 1", "Term 2", "Term 3"));
        examTypeCombo.setItems(FXCollections.observableArrayList("CAT", "Mid-Term", "End-Term", "Mock", "KCSE"));
        totalMarksField.setText("100");
        academicYearField.setText(String.valueOf(java.time.Year.now().getValue()));
    }

    private void loadGrades() {
        gradeList.setAll(gradeDAO.getAllGrades());
        setStatus("Loaded " + gradeList.size() + " grade records.");
    }

    private void updateGradePreview() {
        try {
            double marks = Double.parseDouble(marksField.getText().trim());
            double total = Double.parseDouble(totalMarksField.getText().trim());
            double pct   = total > 0 ? (marks / total) * 100 : 0;
            gradePreviewLabel.setText("Grade: " + Grade.calculateGrade(pct) + "  (" + String.format("%.1f", pct) + "%)");
        } catch (NumberFormatException e) {
            gradePreviewLabel.setText("Grade: —");
        }
    }

    @FXML
    private void handleAdd() {
        if (!validateForm()) return;
        if (gradeDAO.addGrade(buildGradeFromForm())) {
            loadGrades(); clearForm();
            setStatus("✅ Grade recorded successfully.");
        } else {
            setStatus("❌ Error recording grade.");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedGrade == null) { setStatus("⚠ Select a grade record to update."); return; }
        Grade g = buildGradeFromForm();
        g.setId(selectedGrade.getId());
        if (gradeDAO.updateGrade(g)) { loadGrades(); clearForm(); setStatus("✅ Grade updated."); }
    }

    @FXML
    private void handleDelete() {
        Grade g = gradeTable.getSelectionModel().getSelectedItem();
        if (g == null) { setStatus("⚠ Select a record to delete."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete grade for " + g.getStudentName() + " - " + g.getSubjectName() + "?",
                ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            gradeDAO.deleteGrade(g.getId()); loadGrades(); clearForm(); setStatus("✅ Grade deleted.");
        }
    }

    @FXML private void handleClear() { clearForm(); }

    private Grade buildGradeFromForm() {
        Grade g = new Grade();
        Student s   = studentCombo.getValue();
        Subject sub = subjectCombo.getValue();
        if (s   != null) g.setStudentId(s.getId());
        if (sub != null) g.setSubjectId(sub.getId());
        try { g.setMarksObtained(Double.parseDouble(marksField.getText().trim())); }
        catch (NumberFormatException e) { g.setMarksObtained(0); }
        try { g.setTotalMarks(Double.parseDouble(totalMarksField.getText().trim())); }
        catch (NumberFormatException e) { g.setTotalMarks(100); }
        double pct = g.getTotalMarks() > 0 ? (g.getMarksObtained() / g.getTotalMarks()) * 100 : 0;
        g.setGrade(Grade.calculateGrade(pct));
        g.setTerm(termCombo.getValue());
        g.setExamType(examTypeCombo.getValue());
        g.setAcademicYear(academicYearField.getText().trim());
        g.setRemarks(remarksField.getText().trim());
        return g;
    }

    private void populateForm(Grade g) {
        selectedGrade = g;
        studentCombo.getItems().stream().filter(s -> s.getId() == g.getStudentId()).findFirst().ifPresent(studentCombo::setValue);
        subjectCombo.getItems().stream().filter(s -> s.getId() == g.getSubjectId()).findFirst().ifPresent(subjectCombo::setValue);
        marksField.setText(String.valueOf(g.getMarksObtained()));
        totalMarksField.setText(String.valueOf(g.getTotalMarks()));
        termCombo.setValue(g.getTerm());
        examTypeCombo.setValue(g.getExamType());
        academicYearField.setText(g.getAcademicYear());
        remarksField.setText(g.getRemarks());
    }

    private void clearForm() {
        selectedGrade = null;
        studentCombo.setValue(null); subjectCombo.setValue(null);
        marksField.clear(); totalMarksField.setText("100");
        termCombo.setValue(null); examTypeCombo.setValue(null);
        remarksField.clear(); gradePreviewLabel.setText("Grade: —");
    }

    private boolean validateForm() {
        if (studentCombo.getValue()  == null) { setStatus("⚠ Please select a student.");    return false; }
        if (subjectCombo.getValue()  == null) { setStatus("⚠ Please select a subject.");    return false; }
        if (marksField.getText().trim().isEmpty()) { setStatus("⚠ Please enter marks.");    return false; }
        if (termCombo.getValue()     == null) { setStatus("⚠ Please select a term.");       return false; }
        if (examTypeCombo.getValue() == null) { setStatus("⚠ Please select an exam type."); return false; }
        return true;
    }

    private void setStatus(String msg) { statusLabel.setText(msg); }

    @FXML private void goToDashboard() {
        try { MainApp.navigateTo("dashboard.fxml", "Dashboard"); }
        catch (Exception e) { e.printStackTrace(); }
    }
}