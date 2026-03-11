package com.example.schoolmanagementsystem.model;

import javafx.beans.property.*;

/**
 * Grade / Marks Model
 */
public class Grade {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty studentId = new SimpleIntegerProperty();
    private final StringProperty studentName = new SimpleStringProperty();
    private final StringProperty studentRef = new SimpleStringProperty();
    private final IntegerProperty subjectId = new SimpleIntegerProperty();
    private final StringProperty subjectName = new SimpleStringProperty();
    private final DoubleProperty marksObtained = new SimpleDoubleProperty();
    private final DoubleProperty totalMarks = new SimpleDoubleProperty(100);
    private final StringProperty grade = new SimpleStringProperty();
    private final StringProperty term = new SimpleStringProperty();
    private final StringProperty examType = new SimpleStringProperty();
    private final StringProperty academicYear = new SimpleStringProperty();
    private final StringProperty remarks = new SimpleStringProperty();

    public Grade() {}

    public double getPercentage() {
        if (totalMarks.get() == 0) return 0;
        return (marksObtained.get() / totalMarks.get()) * 100;
    }

    public static String calculateGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B+";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C+";
        if (percentage >= 40) return "C";
        if (percentage >= 30) return "D";
        return "F";
    }

    public int getId() { return id.get(); }
    public void setId(int v) { this.id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public int getStudentId() { return studentId.get(); }
    public void setStudentId(int v) { this.studentId.set(v); }
    public IntegerProperty studentIdProperty() { return studentId; }

    public String getStudentName() { return studentName.get(); }
    public void setStudentName(String v) { this.studentName.set(v); }
    public StringProperty studentNameProperty() { return studentName; }

    public String getStudentRef() { return studentRef.get(); }
    public void setStudentRef(String v) { this.studentRef.set(v); }
    public StringProperty studentRefProperty() { return studentRef; }

    public int getSubjectId() { return subjectId.get(); }
    public void setSubjectId(int v) { this.subjectId.set(v); }
    public IntegerProperty subjectIdProperty() { return subjectId; }

    public String getSubjectName() { return subjectName.get(); }
    public void setSubjectName(String v) { this.subjectName.set(v); }
    public StringProperty subjectNameProperty() { return subjectName; }

    public double getMarksObtained() { return marksObtained.get(); }
    public void setMarksObtained(double v) { this.marksObtained.set(v); }
    public DoubleProperty marksObtainedProperty() { return marksObtained; }

    public double getTotalMarks() { return totalMarks.get(); }
    public void setTotalMarks(double v) { this.totalMarks.set(v); }
    public DoubleProperty totalMarksProperty() { return totalMarks; }

    public String getGrade() { return grade.get(); }
    public void setGrade(String v) { this.grade.set(v); }
    public StringProperty gradeProperty() { return grade; }

    public String getTerm() { return term.get(); }
    public void setTerm(String v) { this.term.set(v); }
    public StringProperty termProperty() { return term; }

    public String getExamType() { return examType.get(); }
    public void setExamType(String v) { this.examType.set(v); }
    public StringProperty examTypeProperty() { return examType; }

    public String getAcademicYear() { return academicYear.get(); }
    public void setAcademicYear(String v) { this.academicYear.set(v); }
    public StringProperty academicYearProperty() { return academicYear; }

    public String getRemarks() { return remarks.get(); }
    public void setRemarks(String v) { this.remarks.set(v); }
    public StringProperty remarksProperty() { return remarks; }
}
