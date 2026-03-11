package com.example.schoolmanagementsystem.model;

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * Teacher Model
 */
public class Teacher {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty teacherId = new SimpleStringProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty gender = new SimpleStringProperty();
    private final StringProperty subject = new SimpleStringProperty();
    private final StringProperty qualification = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> hireDate = new SimpleObjectProperty<>();
    private final StringProperty address = new SimpleStringProperty();
    private final DoubleProperty salary = new SimpleDoubleProperty();
    private final StringProperty status = new SimpleStringProperty("Active");

    public Teacher() {}

    public Teacher(String teacherId, String firstName, String lastName, String email,
                   String phone, String gender, String subject, String qualification,
                   LocalDate hireDate, String address, double salary, String status) {
        this.teacherId.set(teacherId);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.email.set(email);
        this.phone.set(phone);
        this.gender.set(gender);
        this.subject.set(subject);
        this.qualification.set(qualification);
        this.hireDate.set(hireDate);
        this.address.set(address);
        this.salary.set(salary);
        this.status.set(status);
    }

    public String getFullName() { return firstName.get() + " " + lastName.get(); }

    public int getId() { return id.get(); }
    public void setId(int v) { this.id.set(v); }
    public IntegerProperty idProperty() { return id; }

    public String getTeacherId() { return teacherId.get(); }
    public void setTeacherId(String v) { this.teacherId.set(v); }
    public StringProperty teacherIdProperty() { return teacherId; }

    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String v) { this.firstName.set(v); }
    public StringProperty firstNameProperty() { return firstName; }

    public String getLastName() { return lastName.get(); }
    public void setLastName(String v) { this.lastName.set(v); }
    public StringProperty lastNameProperty() { return lastName; }

    public String getEmail() { return email.get(); }
    public void setEmail(String v) { this.email.set(v); }
    public StringProperty emailProperty() { return email; }

    public String getPhone() { return phone.get(); }
    public void setPhone(String v) { this.phone.set(v); }
    public StringProperty phoneProperty() { return phone; }

    public String getGender() { return gender.get(); }
    public void setGender(String v) { this.gender.set(v); }
    public StringProperty genderProperty() { return gender; }

    public String getSubject() { return subject.get(); }
    public void setSubject(String v) { this.subject.set(v); }
    public StringProperty subjectProperty() { return subject; }

    public String getQualification() { return qualification.get(); }
    public void setQualification(String v) { this.qualification.set(v); }
    public StringProperty qualificationProperty() { return qualification; }

    public LocalDate getHireDate() { return hireDate.get(); }
    public void setHireDate(LocalDate v) { this.hireDate.set(v); }
    public ObjectProperty<LocalDate> hireDateProperty() { return hireDate; }

    public String getAddress() { return address.get(); }
    public void setAddress(String v) { this.address.set(v); }
    public StringProperty addressProperty() { return address; }

    public double getSalary() { return salary.get(); }
    public void setSalary(double v) { this.salary.set(v); }
    public DoubleProperty salaryProperty() { return salary; }

    public String getStatus() { return status.get(); }
    public void setStatus(String v) { this.status.set(v); }
    public StringProperty statusProperty() { return status; }
}
