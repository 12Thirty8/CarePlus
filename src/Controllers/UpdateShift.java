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

public class UpdateShift {
    @FXML
    private Button BackBttn;
    @FXML
    private TextField CurrentShifttf;
    @FXML
    private ComboBox<String> NewShiftCB;
    @FXML
    private TextArea ReasonArea;
    @FXML
    private Button updateBtn;

    private Alert a = new Alert(AlertType.NONE);
    private Runnable refreshCallback;
    private int employeeId;
    private int requestId; // Added to store the request ID being updated

    public void initialize() {
        employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();
        loadCurrentShift();
        loadAvailableShifts();
        CurrentShifttf.setEditable(false);
    }

    // Method to set the request ID being updated
    public void setRequestId(int requestId) {
        this.requestId = requestId;
        loadExistingRequestData();
    }

    private void loadExistingRequestData() {
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT sr.newshift, sr.description, s.timeslot
                    FROM shiftrequest sr
                    JOIN shift s ON sr.newshift = s.shift_id
                    WHERE sr.sr_id = ?
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, requestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Set the existing values in the form
                NewShiftCB.setValue(rs.getString("timeslot"));
                ReasonArea.setText(rs.getString("description"));
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load request data");
        }
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
    void updateAction(ActionEvent event) {
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

                // Update the existing shift change request
                String updateQuery = """
                        UPDATE shiftrequest
                        SET newshift = ?, description = ?, reqdate = CURRENT_DATE
                        WHERE sr_id = ?
                        """;
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setInt(1, newShiftId);
                updateStmt.setString(2, reason);
                updateStmt.setInt(3, requestId);

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Success", "Shift change request updated successfully");
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                    closeWindow();
                }

                updateStmt.close();
            }

            shiftRs.close();
            shiftStmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update shift change request");
        }
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

    private void showAlert(String title, String message) {
        a.setAlertType(AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}