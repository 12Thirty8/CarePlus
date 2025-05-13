package Controllers.COH;

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
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import util.GetCurrentEmployeeID;

import java.net.URL;
import java.util.ResourceBundle;
import db.DatabaseConnect;

public class UpdateAccountController implements Initializable {
    private int currentEmployeeId;

    @FXML
    private ComboBox<String> dayoffcb, depcb, shiftcb;
    @FXML
    private Button cancelbtn, savebtn, BackBttn;
    @FXML
    private DatePicker dob;
    @FXML
    private TextField fnametf, lnametf, numbertf, emailtf;
    @FXML
    private PasswordField psfield;
    private Alert a = new Alert(AlertType.NONE);
    private Runnable refreshCallback;

    private boolean isCOHDepartmentFull(int currentEmployeeId) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM employee " +
                "WHERE dep_id = (SELECT dep_id FROM department WHERE dep_name = 'COH') " +
                "AND employee_id != ?";

        try (Connection conn = DatabaseConnect.connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, currentEmployeeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") >= 1; // Already one assigned
            }
        }
        return false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            depcb.setPromptText("Select Department");
            shiftcb.setPromptText("Select Shift");
            dayoffcb.setPromptText("Select Day Off");

            depcb.getItems().addAll(getDep());
            shiftcb.getItems().addAll(getShift());
            dayoffcb.getItems().addAll(getOff());

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load dropdown data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    // Method to load employee data into the form
    public void loadEmployeeData(int employeeId) {
        this.currentEmployeeId = employeeId;
        String query = "SELECT e.*, d.dep_name, s.timeslot, dw.dotw_name FROM employee e " +
                "LEFT JOIN department d ON e.dep_id = d.dep_id " +
                "LEFT JOIN shift s ON e.shift_id = s.shift_id " +
                "LEFT JOIN dotweek dw ON e.dayoff_id = dw.dotw_id " +
                "WHERE e.employee_id = ?";

        try (Connection conn = DatabaseConnect.connect();
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
    void BackBttnAction(ActionEvent event) {
        try {
            // Get the current stage (popup window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Close the current popup window
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error closing the window.");
            a.setHeaderText("Error");
            a.show();
        }
    }

    @FXML
    void SaveChangesAction(ActionEvent event) {
        if (fnametf.getText().isEmpty() || lnametf.getText().isEmpty() ||
                dob.getValue() == null || numbertf.getText().isEmpty() ||
                emailtf.getText().isEmpty() || psfield.getText().isEmpty() ||
                depcb.getSelectionModel().isEmpty() ||
                shiftcb.getSelectionModel().isEmpty() ||
                dayoffcb.getSelectionModel().isEmpty()) {

            showAlert("Input Error", "Please fill in all fields");
            return;
        }

        // Check COH constraint
        if ("COH".equalsIgnoreCase(depcb.getValue())) {
            try {
                if (isCOHDepartmentFull(currentEmployeeId)) {
                    showAlert("Department Constraint", "Only one employee is allowed in the COH department.");
                    return;
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to validate COH department rule: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        try {
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

            if (refreshCallback != null) {
                refreshCallback.run(); // Trigger the refresh
            }

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void CancelButtonAction(ActionEvent event) {
        clearForm();
    }

    private List<String> getDep() throws SQLException {
        List<String> depList = new ArrayList<>();
        String query = "SELECT dep_name FROM department";

        try (Connection conn = DatabaseConnect.connect();
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

        try (Connection conn = DatabaseConnect.connect();
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

        try (Connection conn = DatabaseConnect.connect();
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

        // Get the current employee ID from the session
        int currentEmpId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

        String setEmployeeIdQuery = "SET @current_employee_id = ?"; // Set session variable
        String updateEmployeeQuery = "UPDATE employee SET f_name = ?, l_name = ?, dob = ?, contact_no = ?, " +
                "email = ?, password_hash = ?, dep_id = (SELECT dep_id FROM department WHERE dep_name = ?), " +
                "shift_id = (SELECT shift_id FROM shift WHERE timeslot = ?), " +
                "dayoff_id = (SELECT dotw_id FROM dotweek WHERE dotw_name = ?) " +
                "WHERE employee_id = ?";

        try (Connection conn = DatabaseConnect.connect()) {

            // 1. Set the session variable @current_employee_id
            try (PreparedStatement setStmt = conn.prepareStatement(setEmployeeIdQuery)) {
                setStmt.setInt(1, currentEmpId);
                setStmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(updateEmployeeQuery)) {
                {

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
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to update account: " + e.getMessage());
                throw e;
            }
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