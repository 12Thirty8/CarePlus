package Controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.DatabaseConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.GetCurrentEmployeeID;

public class ChangeShift {
    @FXML
    private Button BackBttn;
    @FXML
    private TextField CurrentShifttf; // Changed to TextField
    @FXML
    private ComboBox<String> NewShiftCB;
    @FXML
    private TextArea ReasonArea;
    @FXML
    private Button savebtn;

    private Alert a = new Alert(AlertType.NONE);
    private Runnable refreshCallback;
    private int employeeId;

    public void initialize() {
        employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();
        loadCurrentShift();
        loadAvailableShifts();

        // Make the current shift field non-editable
        CurrentShifttf.setEditable(false);
    }

    private void loadCurrentShift() {
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT s.timeslot
                    FROM employee e
                    JOIN shift s ON e.shift_id = s.shift_id
                    WHERE e.employee_id = ?
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                CurrentShifttf.setText(rs.getString("timeslot"));
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load current shift");
        }
    }

    private void loadAvailableShifts() {
        try {
            Connection conn = DatabaseConnect.connect();
            String query = "SELECT timeslot FROM shift WHERE availability = 1";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            ObservableList<String> shifts = FXCollections.observableArrayList();
            while (rs.next()) {
                shifts.add(rs.getString("timeslot"));
            }

            NewShiftCB.setItems(shifts);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load available shifts");
        }
    }

    @FXML
    void SaveChangesAction(ActionEvent event) {
        String newShift = NewShiftCB.getValue();
        String reason = ReasonArea.getText();

        if (newShift == null || newShift.isEmpty()) {
            showAlert("Error", "Please select a new shift");
            return;
        }

        if (reason == null || reason.trim().isEmpty()) {
            showAlert("Error", "Please provide a reason for the shift change");
            return;
        }

        try {
            // First get the shift_id for the selected timeslot
            Connection conn = DatabaseConnect.connect();
            String shiftQuery = "SELECT shift_id FROM shift WHERE timeslot = ?";
            PreparedStatement shiftStmt = conn.prepareStatement(shiftQuery);
            shiftStmt.setString(1, newShift);
            ResultSet shiftRs = shiftStmt.executeQuery();

            if (shiftRs.next()) {
                int newShiftId = shiftRs.getInt("shift_id");

                // Insert the shift change request
                String insertQuery = """
                        INSERT INTO shiftrequest
                        (shift_id, newshift, description, status, reqdate, requestedby)
                        VALUES (?, ?, ?, 0, CURRENT_DATE, ?)
                        """;
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setInt(1, getCurrentShiftId()); // Current shift ID
                insertStmt.setInt(2, newShiftId); // New shift ID
                insertStmt.setString(3, reason);
                insertStmt.setInt(4, employeeId);

                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Success", "Shift change request submitted successfully");
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                    closeWindow();
                }

                insertStmt.close();
            }

            shiftRs.close();
            shiftStmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to submit shift change request");
        }
    }

    private int getCurrentShiftId() throws SQLException {
        Connection conn = DatabaseConnect.connect();
        String query = "SELECT shift_id FROM employee WHERE employee_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, employeeId);
        ResultSet rs = pstmt.executeQuery();

        int shiftId = -1;
        if (rs.next()) {
            shiftId = rs.getInt("shift_id");
        }

        rs.close();
        pstmt.close();
        conn.close();
        return shiftId;
    }

    private void showAlert(String title, String message) {
        a.setAlertType(AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    @FXML
    void BackBttnAction(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) BackBttn.getScene().getWindow();
        stage.close();
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }
}