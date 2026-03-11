package com.example.schoolmanagementsystem.model;

import javafx.beans.property.*;

/**
 * Subject Model
 */
public class Subject {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty subjectCode = new SimpleStringProperty();
    private final StringProperty subjectName = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty creditHours = new SimpleIntegerProperty();
    private final StringProperty teacherName = new SimpleStringProperty();
    private final IntegerProperty teacherId = new SimpleIntegerProperty();

    public Subject() {}

    public Subject(String code, String name, String description, int creditHours) {
        this.subjectCode.set(code);
        this.subjectName.set(name);
        this.description.set(description);
        this.creditHours.set(creditHours);
    }

    public int getId() { return id.get(); }
    public void setId(int v) { this.id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public String getSubjectCode() { return subjectCode.get(); }
    public void setSubjectCode(String v) { this.subjectCode.set(v); }
    public StringProperty subjectCodeProperty() { return subjectCode; }

    public String getSubjectName() { return subjectName.get(); }
    public void setSubjectName(String v) { this.subjectName.set(v); }
    public StringProperty subjectNameProperty() { return subjectName; }

    public String getDescription() { return description.get(); }
    public void setDescription(String v) { this.description.set(v); }
    public StringProperty descriptionProperty() { return description; }

    public int getCreditHours() { return creditHours.get(); }
    public void setCreditHours(int v) { this.creditHours.set(v); }
    public IntegerProperty creditHoursProperty() { return creditHours; }

    public String getTeacherName() { return teacherName.get(); }
    public void setTeacherName(String v) { this.teacherName.set(v); }
    public StringProperty teacherNameProperty() { return teacherName; }

    public int getTeacherId() { return teacherId.get(); }
    public void setTeacherId(int v) { this.teacherId.set(v); }
    public IntegerProperty teacherIdProperty() { return teacherId; }

    @Override
    public String toString() { return subjectName.get() + " (" + subjectCode.get() + ")"; }
}
