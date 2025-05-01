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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import db.DatabaseConnect;

public class AddAccountController implements Initializable {

    private DatabaseConnect dbConnect = new DatabaseConnect();

    @FXML
    private ComboBox<String> dayoffcb, depcb, shiftcb;
    @FXML
    private Button DashboardBttn;
    @FXML
    private Button HamburgerMenuBttn;
    @FXML
    private Button PharmacyBttn;
    @FXML
    private Button ScheduleBttn;
    @FXML
    private Button ScheduleMenuBttn;
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

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^\\d{11}$");
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
        // ✅ Phone number format validation
        if (!isValidPhoneNumber(numbertf.getText().trim())) {
            showAlert("Invalid Phone Number", "Contact number must be exactly 11 digits.");
            return;
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

            showAlert("Success", "Account created successfully");
            clearForm();

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
                JOptionPane.showMessageDialog(null, "Error loading dashboard", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to create account: " + e.getMessage());
            e.printStackTrace();
        }
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

    private void createAccount(String fname, String lname, Date dob, String number,
            String email, String password, String dep,
            String shift, String dayoff) throws SQLException {

        String query = "INSERT INTO employee (f_name, l_name, dob, contact_no, email, password_hash, " +
                "dep_id, shift_id, dayoff_id) VALUES (?, ?, ?, ?, ?, ?, " +
                "(SELECT dep_id FROM department WHERE dep_name = ?), " +
                "(SELECT shift_id FROM shift WHERE timeslot = ?), " +
                "(SELECT dotw_id FROM dotweek WHERE dotw_name = ?))";

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

            pstmt.executeUpdate();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            JOptionPane.showMessageDialog(null, "Error creating Account: " + e1.getMessage());
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
            JOptionPane.showMessageDialog(null, "Error loading page.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    void DashboardActionBttn(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/COH_Dashboard.fxml"));
            root = loader.load();

            root = FXMLLoader.load(getClass().getResource("/View/COH_Dashboard.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading page.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    @FXML
    void HamburgerMenuActionBttn(ActionEvent event) {

    }

    @FXML
    void PharmacyActionBttn(ActionEvent event) {

    }

    @FXML
    void ScheduleActionBttn(ActionEvent event) {

    }

    @FXML
    void ScheduleuActionBttn(ActionEvent event) {

    }
}