package com.example.schoolmanagementsystem.controller;

import com.example.schoolmanagementsystem.MainApp;
import com.example.schoolmanagementsystem.dao.FeePaymentDAO;
import com.example.schoolmanagementsystem.dao.StudentDAO;
import com.example.schoolmanagementsystem.model.FeePayment;
import com.example.schoolmanagementsystem.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.Year;

/**
 * Fee Payment Controller
 */
public class FeeController {

    @FXML private TableView<FeePayment> feeTable;
    @FXML private TableColumn<FeePayment, String> colReceipt;
    @FXML private TableColumn<FeePayment, String> colStudent;
    @FXML private TableColumn<FeePayment, String> colClass;
    @FXML private TableColumn<FeePayment, Double> colAmount;
    @FXML private TableColumn<FeePayment, Double> colPaid;
    @FXML private TableColumn<FeePayment, Double> colBalance;
    @FXML private TableColumn<FeePayment, String> colFeeType;
    @FXML private TableColumn<FeePayment, String> colStatus;
    @FXML private TableColumn<FeePayment, LocalDate> colDate;

    @FXML private ComboBox<Student> studentCombo;
    @FXML private TextField amountField;
    @FXML private TextField amountPaidField;
    @FXML private ComboBox<String> feeTypeCombo;
    @FXML private ComboBox<String> termCombo;
    @FXML private TextField academicYearField;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private DatePicker paymentDatePicker;
    @FXML private Label receiptPreviewLabel;
    @FXML private Label balanceLabel;
    @FXML private Label statusLabel;
    @FXML private Label totalCollectedLabel;
    @FXML private Label totalOutstandingLabel;

    private final FeePaymentDAO feeDAO = new FeePaymentDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private ObservableList<FeePayment> feeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        setupCombos();
        loadFees();
        paymentDatePicker.setValue(LocalDate.now());

        // Auto-calculate balance
        amountField.textProperty().addListener((obs, old, val) -> updateBalance());
        amountPaidField.textProperty().addListener((obs, old, val) -> updateBalance());
    }

    private void setupTable() {
        colReceipt.setCellValueFactory(new PropertyValueFactory<>("receiptNumber"));
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colClass.setCellValueFactory(new PropertyValueFactory<>("classGroup"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colPaid.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colFeeType.setCellValueFactory(new PropertyValueFactory<>("feeType"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "Paid"    -> "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
                    case "Partial" -> "-fx-text-fill: #f39c12; -fx-font-weight: bold;";
                    default        -> "-fx-text-fill: #e74c3c; -fx-font-weight: bold;";
                });
            }
        });

        feeTable.setItems(feeList);
    }

    private void setupCombos() {
        studentCombo.setItems(FXCollections.observableArrayList(studentDAO.getAllStudents()));
        feeTypeCombo.setItems(FXCollections.observableArrayList(
                "Tuition Fee", "Library Fee", "Lab Fee", "Sports Fee", "Examination Fee", "Other"));
        termCombo.setItems(FXCollections.observableArrayList("Term 1", "Term 2", "Term 3"));
        paymentMethodCombo.setItems(FXCollections.observableArrayList("Cash", "M-Pesa", "Bank Transfer", "Cheque"));
        academicYearField.setText(String.valueOf(Year.now().getValue()));
        receiptPreviewLabel.setText(feeDAO.generateReceiptNumber());
    }

    private void updateBalance() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            double paid = Double.parseDouble(amountPaidField.getText().trim());
            double balance = amount - paid;
            balanceLabel.setText(String.format("Balance: KSh %.2f", balance));
        } catch (NumberFormatException e) {
            balanceLabel.setText("Balance: —");
        }
    }

    private void loadFees() {
        feeList.setAll(feeDAO.getAllPayments());
        totalCollectedLabel.setText(String.format("Total Collected: KSh %.2f", feeDAO.getTotalCollected()));
        totalOutstandingLabel.setText(String.format("Outstanding: KSh %.2f", feeDAO.getTotalOutstanding()));
        setStatus("Loaded " + feeList.size() + " payment records.");
    }

    @FXML
    private void handleAdd() {
        Student s = studentCombo.getValue();
        if (s == null) { setStatus("⚠ Select a student."); return; }
        if (amountField.getText().trim().isEmpty()) { setStatus("⚠ Enter total amount."); return; }

        FeePayment f = new FeePayment();
        f.setStudentId(s.getId());
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            double paid = Double.parseDouble(amountPaidField.getText().isEmpty() ? "0" : amountPaidField.getText().trim());
            f.setAmount(amount);
            f.setAmountPaid(paid);
            f.setBalance(amount - paid);
            f.setStatus(paid >= amount ? "Paid" : paid > 0 ? "Partial" : "Unpaid");
        } catch (NumberFormatException e) { setStatus("⚠ Invalid amount."); return; }

        f.setFeeType(feeTypeCombo.getValue());
        f.setTerm(termCombo.getValue());
        f.setAcademicYear(academicYearField.getText().trim());
        f.setPaymentMethod(paymentMethodCombo.getValue());
        f.setPaymentDate(paymentDatePicker.getValue());
        f.setReceiptNumber(feeDAO.generateReceiptNumber());

        if (feeDAO.addPayment(f)) {
            loadFees(); clearForm();
            setStatus("✅ Payment recorded. Receipt: " + f.getReceiptNumber());
        } else {
            setStatus("❌ Error recording payment.");
        }
    }

    @FXML
    private void handleClear() { clearForm(); }

    private void clearForm() {
        studentCombo.setValue(null);
        amountField.clear(); amountPaidField.clear();
        feeTypeCombo.setValue(null); termCombo.setValue(null);
        paymentMethodCombo.setValue(null);
        paymentDatePicker.setValue(LocalDate.now());
        balanceLabel.setText("Balance: —");
        receiptPreviewLabel.setText(feeDAO.generateReceiptNumber());
    }

    private void setStatus(String msg) { statusLabel.setText(msg); }

    @FXML private void goToDashboard() {
        try { MainApp.navigateTo("dashboard.fxml", "Dashboard"); } catch (Exception e) { e.printStackTrace(); }
    }
}
