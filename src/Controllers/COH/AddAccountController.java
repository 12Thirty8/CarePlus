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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import util.GetCurrentEmployeeID;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import db.DatabaseConnect;

public class AddAccountController implements Initializable {

    private int currentEmployeeId;

    @FXML
    private ComboBox<String> dayoffcb, depcb, shiftcb;
    @FXML
    private Button addaccbtn;
    @FXML
    private DatePicker dob;
    @FXML
    private TextField fnametf, lnametf, numbertf, emailtf;
    @FXML
    private PasswordField psfield;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Alert a = new Alert(AlertType.NONE);
    private Runnable refreshCallback;

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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    @FXML
    void AddAccBtnPress(ActionEvent event) {
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
        // Email format validation
        if (!isValidEmail(emailtf.getText().trim())) {
            showAlert("Invalid Email", "Please enter a valid email address.");
            return;
        }

        if (!fnametf.getText().matches("[a-zA-Z]+") || !lnametf.getText().matches("[a-zA-Z]+")) {
            showAlert("Input Error", "Names can only contain letters");
            return;
        }

        if (!isValidEmail(emailtf.getText())) {
            showAlert("Invalid Email", "Please enter a valid email (e.g., user@example.com)");
            return;
        }

        // Phone number must start with 09 and have exactly 11 digits
        String phone = numbertf.getText().trim();
        if (!phone.matches("^09\\d{9}$")) {
            showAlert("Input Error", "Phone number must start with 09 and have exactly 11 digits.");
            return;
        }

        if (psfield.getText().length() < 6) {
            showAlert("Input Error", "Password must be at least 6 characters long");
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
                showAlert("Department Constraint", "Only one employee is allowed in the COH department.");
                e.printStackTrace();
                return;
            }
        }

        try {
            // Convert LocalDate to SQL Date
            LocalDate localDate = dob.getValue();
            Date sqlDate = Date.valueOf(localDate);

            createAccount(
                    fnametf.getText(),
                    lnametf.getText(),
                    sqlDate,
                    numbertf.getText(),
                    emailtf.getText(),
                    psfield.getText(),
                    depcb.getValue(),
                    shiftcb.getValue(),
                    dayoffcb.getValue());

            showAlert("Success", "Employee onboarded successfully.");
            clearForm();

            if (refreshCallback != null) {
                refreshCallback.run(); // Trigger the refresh
            }

            // close the scene
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to create account: " + e.getMessage());
            e.printStackTrace();
        }
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

    private void createAccount(String fname, String lname, Date dob, String number,
            String email, String password, String dep,
            String shift, String dayoff) throws SQLException {

        // Get the current employee ID from the session
        int currentEmpId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

        String setEmployeeIdQuery = "SET @current_employee_id = ?"; // Set session variable
        String insertEmployeeQuery = "INSERT INTO employee (f_name, l_name, dob, contact_no, email, password_hash, "
                + "dep_id, shift_id, dayoff_id, status) VALUES (?, ?, ?, ?, ?, ?, "
                + "(SELECT dep_id FROM department WHERE dep_name = ?), "
                + "(SELECT shift_id FROM shift WHERE timeslot = ?), "
                + "(SELECT dotw_id FROM dotweek WHERE dotw_name = ?), 1)";

        try (Connection conn = DatabaseConnect.connect()) {

            // 1. Set the session variable @current_employee_id
            try (PreparedStatement setStmt = conn.prepareStatement(setEmployeeIdQuery)) {
                setStmt.setInt(1, currentEmpId);
                setStmt.executeUpdate();
            }

            // 2. Insert the new employee
            try (PreparedStatement insertStmt = conn.prepareStatement(insertEmployeeQuery)) {
                insertStmt.setString(1, fname);
                insertStmt.setString(2, lname);
                insertStmt.setDate(3, dob);
                insertStmt.setString(4, number);
                insertStmt.setString(5, email);
                insertStmt.setString(6, password);
                insertStmt.setString(7, dep);
                insertStmt.setString(8, shift);
                insertStmt.setString(9, dayoff);

                insertStmt.executeUpdate();
            }

        } catch (SQLException e1) {
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error creating account: " + e1.getMessage());
            a.setHeaderText("Error");
            a.show();
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

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    private boolean isCOHDepartmentFull(int currentEmployeeId) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM employee " +
                "WHERE dep_id = (SELECT dep_id FROM department WHERE dep_name = 'COH')";

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

    @FXML
    void AccountMenuActionBttn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/COH_AccountManagement.fxml"));
            root = loader.load();

            root = FXMLLoader.load(getClass().getResource("/View/COH_AccountManagement.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.setHeaderText("Error");
            a.show();
        }
    }

}