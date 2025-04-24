package Controllers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

import db.DatabaseConnect;

public class UpdateAccountController implements Initializable {

    private DatabaseConnect dbConnect = new DatabaseConnect();
    private int currentEmployeeId; // To store the ID of the employee being updated

    @FXML
    private ComboBox<String> dayoffcb, depcb, shiftcb;
    @FXML
    private Button cancelbtn, savebtn;
    @FXML
    private DatePicker dob;
    @FXML
    private TextField fnametf, lnametf, numbertf, emailtf;
    @FXML
    private PasswordField psfield;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Set prompt texts
            depcb.setPromptText("Select Department");
            shiftcb.setPromptText("Select Shift");
            dayoffcb.setPromptText("Select Day Off");

            // Populate comboboxes
            depcb.getItems().addAll(getDep());
            shiftcb.getItems().addAll(getShift());
            dayoffcb.getItems().addAll(getOff());

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load dropdown data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to load employee data into the form
    public void loadEmployeeData(int employeeId) {
        this.currentEmployeeId = employeeId;
        String query = "SELECT e.*, d.dep_name, s.timeslot, dw.dotw_name FROM employee e " +
                "LEFT JOIN department d ON e.dep_id = d.dep_id " +
                "LEFT JOIN shift s ON e.shift_id = s.shift_id " +
                "LEFT JOIN dotweek dw ON e.dayoff_id = dw.dotw_id " +
                "WHERE e.employee_id = ?";

        try (Connection conn = dbConnect.connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                fnametf.setText(rs.getString("f_name"));
                lnametf.setText(rs.getString("l_name"));
                dob.setValue(rs.getDate("dob").toLocalDate());
                numbertf.setText(rs.getString("contact_no"));
                emailtf.setText(rs.getString("email"));
                psfield.setText(rs.getString("password_hash"));

                // Set combobox values
                depcb.setValue(rs.getString("dep_name"));
                shiftcb.setValue(rs.getString("timeslot"));
                dayoffcb.setValue(rs.getString("dotw_name"));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load employee data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void SaveChangesAction(ActionEvent event) {
        // Validate inputs
        if (fnametf.getText().isEmpty() || lnametf.getText().isEmpty() ||
                dob.getValue() == null || numbertf.getText().isEmpty() ||
                emailtf.getText().isEmpty() || psfield.getText().isEmpty() ||
                depcb.getSelectionModel().isEmpty() ||
                shiftcb.getSelectionModel().isEmpty() ||
                dayoffcb.getSelectionModel().isEmpty()) {

            showAlert("Input Error", "Please fill in all fields");
            return;
        }

        try {
            // Convert LocalDate to SQL Date
            LocalDate localDate = dob.getValue();
            Date sqlDate = Date.valueOf(localDate);

            updateAccount(
                    currentEmployeeId,
                    fnametf.getText(),
                    lnametf.getText(),
                    sqlDate,
                    numbertf.getText(),
                    emailtf.getText(),
                    psfield.getText(),
                    depcb.getValue(),
                    shiftcb.getValue(),
                    dayoffcb.getValue());

            showAlert("Success", "Account updated successfully");

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void CancelButtonAction(ActionEvent event) {
        // Clear the form or close the window
        clearForm();
        // You might want to add code to close the window here
    }

    private List<String> getDep() throws SQLException {
        List<String> depList = new ArrayList<>();
        String query = "SELECT dep_name FROM department";

        try (Connection conn = dbConnect.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                depList.add(rs.getString("dep_name"));
            }
        }
        return depList;
    }

    private List<String> getShift() throws SQLException {
        List<String> shiftList = new ArrayList<>();
        String query = "SELECT timeslot FROM shift";

        try (Connection conn = dbConnect.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                shiftList.add(rs.getString("timeslot"));
            }
        }
        return shiftList;
    }

    private List<String> getOff() throws SQLException {
        List<String> offList = new ArrayList<>();
        String query = "SELECT dotw_name FROM dotweek";

        try (Connection conn = dbConnect.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                offList.add(rs.getString("dotw_name"));
            }
        }
        return offList;
    }

    private void updateAccount(int empId, String fname, String lname, Date dob, String number,
            String email, String password, String dep, String shift, String dayoff) throws SQLException {

        String query = "UPDATE employee SET f_name = ?, l_name = ?, dob = ?, contact_no = ?, " +
                "email = ?, password_hash = ?, dep_id = (SELECT dep_id FROM department WHERE dep_name = ?), " +
                "shift_id = (SELECT shift_id FROM shift WHERE timeslot = ?), " +
                "dayoff_id = (SELECT dotw_id FROM dotweek WHERE dotw_name = ?) " +
                "WHERE employee_id = ?";

        try (Connection conn = dbConnect.connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, fname);
            pstmt.setString(2, lname);
            pstmt.setDate(3, dob);
            pstmt.setString(4, number);
            pstmt.setString(5, email);
            pstmt.setString(6, password);
            pstmt.setString(7, dep);
            pstmt.setString(8, shift);
            pstmt.setString(9, dayoff);
            pstmt.setInt(10, empId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating account failed, no rows affected.");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update account: " + e.getMessage());
            throw e;
        }
    }

    private void clearForm() {
        fnametf.clear();
        lnametf.clear();
        dob.setValue(null);
        numbertf.clear();
        emailtf.clear();
        psfield.clear();
        depcb.getSelectionModel().clearSelection();
        shiftcb.getSelectionModel().clearSelection();
        dayoffcb.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}