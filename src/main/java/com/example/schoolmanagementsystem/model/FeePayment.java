package com.example.schoolmanagementsystem.model;

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * Fee / Payment Model
 */
public class FeePayment {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty studentId = new SimpleIntegerProperty();
    private final StringProperty studentName = new SimpleStringProperty();
    private final StringProperty studentRef = new SimpleStringProperty();
    private final StringProperty classGroup = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();
    private final DoubleProperty amountPaid = new SimpleDoubleProperty();
    private final DoubleProperty balance = new SimpleDoubleProperty();
    private final StringProperty feeType = new SimpleStringProperty(); // Tuition, Library, Lab, Sport, etc.
    private final StringProperty term = new SimpleStringProperty();
    private final StringProperty academicYear = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> paymentDate = new SimpleObjectProperty<>();
    private final StringProperty paymentMethod = new SimpleStringProperty(); // Cash, M-Pesa, Bank
    private final StringProperty receiptNumber = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty(); // Paid, Partial, Unpaid

    public FeePayment() {}

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

    public double getAmount() { return amount.get(); }
    public void setAmount(double v) { this.amount.set(v); }
    public DoubleProperty amountProperty() { return amount; }

    public double getAmountPaid() { return amountPaid.get(); }
    public void setAmountPaid(double v) { this.amountPaid.set(v); }
    public DoubleProperty amountPaidProperty() { return amountPaid; }

    public double getBalance() { return balance.get(); }
    public void setBalance(double v) { this.balance.set(v); }
    public DoubleProperty balanceProperty() { return balance; }

    public String getFeeType() { return feeType.get(); }
    public void setFeeType(String v) { this.feeType.set(v); }
    public StringProperty feeTypeProperty() { return feeType; }

    public String getTerm() { return term.get(); }
    public void setTerm(String v) { this.term.set(v); }
    public StringProperty termProperty() { return term; }

    public String getAcademicYear() { return academicYear.get(); }
    public void setAcademicYear(String v) { this.academicYear.set(v); }
    public StringProperty academicYearProperty() { return academicYear; }

    public LocalDate getPaymentDate() { return paymentDate.get(); }
    public void setPaymentDate(LocalDate v) { this.paymentDate.set(v); }
    public ObjectProperty<LocalDate> paymentDateProperty() { return paymentDate; }

    public String getPaymentMethod() { return paymentMethod.get(); }
    public void setPaymentMethod(String v) { this.paymentMethod.set(v); }
    public StringProperty paymentMethodProperty() { return paymentMethod; }

    public String getReceiptNumber() { return receiptNumber.get(); }
    public void setReceiptNumber(String v) { this.receiptNumber.set(v); }
    public StringProperty receiptNumberProperty() { return receiptNumber; }

    public String getStatus() { return status.get(); }
    public void setStatus(String v) { this.status.set(v); }
    public StringProperty statusProperty() { return status; }
}
