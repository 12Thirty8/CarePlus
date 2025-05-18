package Controllers.NURSE;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Models.PatientStatus;
import Models.RecordsModel;
import db.DatabaseConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import util.TextReportGenerator;

public class N_UpdateMedicalRecord {

    @FXML
    private Button BackBttn;

    @FXML
    private DatePicker CheckupDate;

    @FXML
    private TextArea diagnosisArea;

    @FXML
    private TextArea dispositionArea;

    @FXML
    private TextArea complaintArea;

    @FXML
    private ChoiceBox<PatientStatus> statusBox;

    @FXML
    private Button cancelbtn;

    @FXML
    private TextField doctorIDtf;

    @FXML
    private TextField patientIDtf;

    @FXML
    private TextField fNameTf;

    @FXML
    private TextField lNameTf;

    @FXML
    private Button savebtn;

    private static class PatientStatus {
        final int id;
        final String name;

        PatientStatus(int id, String name) {
            this.id = id;
            this.name = name;
        }

        // This is what shows up in the ChoiceBox
        @Override
        public String toString() {
            return name;
        }

        public int getId() {
            return id;
        }
    }

    public void loadStatuses() {
        ObservableList<PatientStatus> statuses = FXCollections.observableArrayList();
        String sql = "SELECT id, name FROM patient_status";
        try (Connection conn = DatabaseConnect.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                statuses.add(new PatientStatus(rs.getInt("id"), rs.getString("name")));
            }
            statusBox.setItems(statuses);
        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load patient statuses.").showAndWait();
        }
    }

    @FXML
    public void initialize() {
        loadStatuses();

        patientIDtf.setEditable(false);
        fNameTf.setEditable(false);
        lNameTf.setEditable(false);
    }

    private void clearForm() {

        doctorIDtf.clear();

        CheckupDate.setValue(null);
        statusBox.getSelectionModel().clearSelection();
        complaintArea.clear();
        diagnosisArea.clear();
        dispositionArea.clear();
        resetFieldStyles();
    }

    private void resetFieldStyles() {
        patientIDtf.setStyle(null);
        doctorIDtf.setStyle(null);
        fNameTf.setStyle(null);
        lNameTf.setStyle(null);
        CheckupDate.setStyle(null);
        statusBox.setStyle(null);
        complaintArea.setStyle(null);
        diagnosisArea.setStyle(null);
        dispositionArea.setStyle(null);
    }

    private boolean validateForm() {
        boolean ok = true;
        if (isEmpty(patientIDtf))
            ok = false;
        if (isEmpty(doctorIDtf))
            ok = false;
        if (isEmpty(fNameTf))
            ok = false;
        if (isEmpty(lNameTf))
            ok = false;
        if (CheckupDate.getValue() == null) {
            highlight(CheckupDate);
            ok = false;
        }
        if (statusBox.getValue() == null) {
            highlight(statusBox);
            ok = false;
        }
        if (isEmpty(complaintArea))
            ok = false;
        if (isEmpty(diagnosisArea))
            ok = false;
        if (isEmpty(dispositionArea))
            ok = false;
        return ok;
    }

    private boolean isEmpty(TextInputControl ctl) {
        String text = ctl.getText();
        if (text == null || text.trim().isEmpty()) {
            highlight(ctl);
            return true;
        }
        return false;
    }

    private void highlight(Control ctl) {
        ctl.setStyle("-fx-border-color: red;");
        if (ctl instanceof TextInputControl) {
            ((TextInputControl) ctl).setPromptText("Please fill out this field");
        }
    }

    @FXML
    void BackBttnAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void CancelButtonAction(ActionEvent event) {
        clearForm();
    }

    @FXML
    void SaveChangesAction(ActionEvent event) {
        resetFieldStyles();
        if (!validateForm()) {
            new Alert(Alert.AlertType.WARNING, "Please fill out all required fields.").showAndWait();
            return;
        }

        // Prepare updated data from form fields
        int patientId = Integer.parseInt(patientIDtf.getText().trim());
        String doctorName = doctorIDtf.getText().trim(); // Assuming this is doctor name; adjust if it's ID
        String firstName = fNameTf.getText().trim();
        String lastName = lNameTf.getText().trim();
        String complaint = complaintArea.getText().trim();
        String diagnosis = diagnosisArea.getText().trim();
        String disposition = dispositionArea.getText().trim();
        PatientStatus selectedStatus = statusBox.getValue();
        java.sql.Date checkupDate = (CheckupDate.getValue() == null) ? null
                : java.sql.Date.valueOf(CheckupDate.getValue());

        if (selectedStatus == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a patient status.").showAndWait();
            return;
        }

        // Build SQL update query
        String sql = "UPDATE records SET " +
                "patient_id = ?, " + // If patient ID is editable (or remove this)
                "doctor_name = ?, " + // Or doctor_id if applicable
                "f_name = ?, l_name = ?, " +
                "chief_complaint = ?, diagnosis = ?, disposition = ?, " +
                "status = ?, record_date = ? " +
                "WHERE patient_id = ?"; // or use record id if available

        try (java.sql.Connection conn = DatabaseConnect.connect();
                java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            pstmt.setString(2, doctorName);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, complaint);
            pstmt.setString(6, diagnosis);
            pstmt.setString(7, disposition);
            pstmt.setInt(8, selectedStatus.getId());
            pstmt.setDate(9, checkupDate);
            pstmt.setInt(10, patientId); // condition for WHERE clause

            int updated = pstmt.executeUpdate();
            if (updated > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Record updated successfully!").showAndWait();

                // Close this window after successful update
                Stage stage = (Stage) savebtn.getScene().getWindow();
                stage.close();
            } else {
                new Alert(Alert.AlertType.ERROR, "No record was updated.").showAndWait();
            }

        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database error occurred: " + ex.getMessage()).showAndWait();
        }

          // Generate report

        String reportContent = String.format(
            "Patient ID   : %d%n" +
            "Doctor       : %s%n" +
            "First Name   : %s%n" +
            "Last Name    : %s%n" +
            "Complaint    : %s%n" +
            "Diagnosis    : %s%n" +
            "Disposition  : %s%n" +
            "Status       : %s%n" +
            "Date         : %s",
            Integer.parseInt(patientIDtf.getText().trim()),
            doctorIDtf.getText().trim(),
            fNameTf.getText().trim(),
            lNameTf.getText().trim(),
            complaintArea.getText().trim(),
            diagnosisArea.getText().trim(),
            dispositionArea.getText().trim(),
            selectedStatus.name,
            checkupDate.toString()
        );

        // Create reports folder if it doesn't exist
        new File("reports").mkdirs();

        // Generate timestamped file name
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = now.format(formatter);

        String filePath = "reports/Medical_Record_Report_" +
            Integer.parseInt(patientIDtf.getText().trim()) + "_" + timestamp + ".txt";

        TextReportGenerator.generateMedicalRecordReport(filePath, "Medical Record Report", reportContent);

    }

    private void loadPatientName(int patientId) {
        String sql = "SELECT f_name, l_name FROM patient WHERE patient_id = ?";
        try (Connection conn = DatabaseConnect.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    fNameTf.setText(rs.getString("f_name"));
                    lNameTf.setText(rs.getString("l_name"));
                } else {
                    fNameTf.clear();
                    lNameTf.clear();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load patient name.").showAndWait();
        }
    }

    public void setRecordData(RecordsModel record) {

        patientIDtf.setText(String.valueOf(record.getPatientId()));

        doctorIDtf.setText(record.getDoctorName());

        loadPatientName(record.getPatientId());
        complaintArea.setText(record.getChiefComplaint());
        diagnosisArea.setText(record.getDiagnosis());
        dispositionArea.setText(record.getDisposition());
        CheckupDate.setValue(record.getRecordDate());

        for (PatientStatus status : statusBox.getItems()) {
            if (status.id == record.getStatus()) {
                statusBox.setValue(status);
                break;
            }
        }

    }

}
