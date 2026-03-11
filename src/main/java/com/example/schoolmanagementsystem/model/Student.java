package com.example.schoolmanagementsystem.model;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Student Model - represents a student in the system
 */
public class Student {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty studentId = new SimpleStringProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty gender = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dateOfBirth = new SimpleObjectProperty<>();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty classGroup = new SimpleStringProperty();
    private final StringProperty guardianName = new SimpleStringProperty();
    private final StringProperty guardianPhone = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> enrollmentDate = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty("Active");

    public Student() {}

    public Student(String studentId, String firstName, String lastName, String email,
                   String phone, String gender, LocalDate dateOfBirth, String address,
                   String classGroup, String guardianName, String guardianPhone,
                   LocalDate enrollmentDate, String status) {
        this.studentId.set(studentId);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.email.set(email);
        this.phone.set(phone);
        this.gender.set(gender);
        this.dateOfBirth.set(dateOfBirth);
        this.address.set(address);
        this.classGroup.set(classGroup);
        this.guardianName.set(guardianName);
        this.guardianPhone.set(guardianPhone);
        this.enrollmentDate.set(enrollmentDate);
        this.status.set(status);
    }

    public String getFullName() { return firstName.get() + " " + lastName.get(); }

    // --- ID ---
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    // --- Student ID ---
    public String getStudentId() { return studentId.get(); }
    public void setStudentId(String sid) { this.studentId.set(sid); }
    public StringProperty studentIdProperty() { return studentId; }

    // --- First Name ---
    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String v) { this.firstName.set(v); }
    public StringProperty firstNameProperty() { return firstName; }

    // --- Last Name ---
    public String getLastName() { return lastName.get(); }
    public void setLastName(String v) { this.lastName.set(v); }
    public StringProperty lastNameProperty() { return lastName; }

    // --- Email ---
    public String getEmail() { return email.get(); }
    public void setEmail(String v) { this.email.set(v); }
    public StringProperty emailProperty() { return email; }

    // --- Phone ---
    public String getPhone() { return phone.get(); }
    public void setPhone(String v) { this.phone.set(v); }
    public StringProperty phoneProperty() { return phone; }

    // --- Gender ---
    public String getGender() { return gender.get(); }
    public void setGender(String v) { this.gender.set(v); }
    public StringProperty genderProperty() { return gender; }

    // --- Date of Birth ---
    public LocalDate getDateOfBirth() { return dateOfBirth.get(); }
    public void setDateOfBirth(LocalDate v) { this.dateOfBirth.set(v); }
    public ObjectProperty<LocalDate> dateOfBirthProperty() { return dateOfBirth; }

    // --- Address ---
    public String getAddress() { return address.get(); }
    public void setAddress(String v) { this.address.set(v); }
    public StringProperty addressProperty() { return address; }

    // --- Class Group ---
    public String getClassGroup() { return classGroup.get(); }
    public void setClassGroup(String v) { this.classGroup.set(v); }
    public StringProperty classGroupProperty() { return classGroup; }

    // --- Guardian Name ---
    public String getGuardianName() { return guardianName.get(); }
    public void setGuardianName(String v) { this.guardianName.set(v); }
    public StringProperty guardianNameProperty() { return guardianName; }

    // --- Guardian Phone ---
    public String getGuardianPhone() { return guardianPhone.get(); }
    public void setGuardianPhone(String v) { this.guardianPhone.set(v); }
    public StringProperty guardianPhoneProperty() { return guardianPhone; }

    // --- Enrollment Date ---
    public LocalDate getEnrollmentDate() { return enrollmentDate.get(); }
    public void setEnrollmentDate(LocalDate v) { this.enrollmentDate.set(v); }
    public ObjectProperty<LocalDate> enrollmentDateProperty() { return enrollmentDate; }

    // --- Status ---
    public String getStatus() { return status.get(); }
    public void setStatus(String v) { this.status.set(v); }
    public StringProperty statusProperty() { return status; }

    @Override
    public String toString() { return getFullName() + " (" + getStudentId() + ")"; }
}
