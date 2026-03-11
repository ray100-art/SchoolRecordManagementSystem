package com.example.schoolmanagementsystem.model;

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * Attendance Model
 */
public class Attendance {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty studentId = new SimpleIntegerProperty();
    private final StringProperty studentName = new SimpleStringProperty();
    private final StringProperty studentRef = new SimpleStringProperty();
    private final StringProperty classGroup = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty(); // Present, Absent, Late, Excused
    private final StringProperty remarks = new SimpleStringProperty();

    public Attendance() {}

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

    public String getClassGroup() { return classGroup.get(); }
    public void setClassGroup(String v) { this.classGroup.set(v); }
    public StringProperty classGroupProperty() { return classGroup; }

    public LocalDate getDate() { return date.get(); }
    public void setDate(LocalDate v) { this.date.set(v); }
    public ObjectProperty<LocalDate> dateProperty() { return date; }

    public String getStatus() { return status.get(); }
    public void setStatus(String v) { this.status.set(v); }
    public StringProperty statusProperty() { return status; }

    public String getRemarks() { return remarks.get(); }
    public void setRemarks(String v) { this.remarks.set(v); }
    public StringProperty remarksProperty() { return remarks; }
}
