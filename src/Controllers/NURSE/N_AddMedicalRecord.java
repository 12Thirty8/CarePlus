package Controllers.NURSE;

import db.DatabaseConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class N_AddMedicalRecord implements Initializable {

    @FXML
    private Button BackBttn;
    @FXML
    private Button cancelbtn;
    @FXML
    private Button savebtn;

    @FXML
    private ComboBox<Integer> patientIdCombo;
    @FXML
    private TextField doctorIDtf;
    @FXML
    private TextField fNameTf;
    @FXML
    private TextField lNameTf;

    @FXML
    private DatePicker CheckupDate;
    @FXML
    private ChoiceBox<PatientStatus> statusBox;
    @FXML
    private TextArea complaintArea;
    @FXML
    private TextArea DiagnosisArea;
    @FXML
    private TextArea dispositionArea;

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
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1) populate patient IDs
        loadPatientIds();
        loadStatuses();

        // 2) disable editing on the name fields
        fNameTf.setEditable(false);
        lNameTf.setEditable(false);

        // 3) when a patient is chosen, load their names
        patientIdCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldId, newId) -> {
            if (newId != null) {
                loadPatientName(newId);
            } else {
                fNameTf.clear();
                lNameTf.clear();
            }
        });

    }

    private void loadStatuses() {
        ObservableList<PatientStatus> statuses = FXCollections.observableArrayList();
        String sql = "SELECT status_id, name FROM patient_status";
        try (Connection conn = DatabaseConnect.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                statuses.add(new PatientStatus(
                        rs.getInt("status_id"),
                        rs.getString("name")));
            }
            statusBox.setItems(statuses);

        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load patient statuses.").showAndWait();
        }
    }

    private void loadPatientIds() {
        ObservableList<Integer> ids = FXCollections.observableArrayList();
        String sql = "SELECT patient_id FROM patient";
        try (Connection conn = DatabaseConnect.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ids.add(rs.getInt("patient_id"));
            }
            patientIdCombo.setItems(ids);

        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load patient IDs.").showAndWait();
        }
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

    @FXML
    void SaveChangesAction(ActionEvent event) {
        resetFieldStyles();
        if (!validateForm()) {
            new Alert(Alert.AlertType.WARNING, "Please fill out all required fields.").showAndWait();
            return;
        }

        String insertSql = "INSERT INTO records "
                + "(patient_id, doctor_id, f_name, l_name, record_date, status, chief_complaint, diagnosis, disposition) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnect.connect();
                PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setInt(1, patientIdCombo.getValue());
            ps.setInt(2, Integer.parseInt(doctorIDtf.getText().trim()));
            ps.setString(3, fNameTf.getText().trim());
            ps.setString(4, lNameTf.getText().trim());
            ps.setDate(5, Date.valueOf(CheckupDate.getValue()));

            // get the integer ID from the selected PatientStatus
            PatientStatus selected = statusBox.getValue();
            ps.setInt(6, selected.id);

            ps.setString(7, complaintArea.getText().trim());
            ps.setString(8, DiagnosisArea.getText().trim());
            ps.setString(9, dispositionArea.getText().trim());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Record saved successfully!").showAndWait();
                clearForm();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database error:\n" + ex.getMessage()).showAndWait();
        }
    }

    private boolean validateForm() {
        boolean ok = true;
        if (patientIdCombo.getValue() == null) {
            highlight(patientIdCombo);
            ok = false;
        }
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
        if (isEmpty(DiagnosisArea))
            ok = false;
        if (isEmpty(dispositionArea))
            ok = false;
        return ok;
    }

    private boolean isEmpty(TextInputControl ctl) {
        if (ctl.getText().trim().isEmpty()) {
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

    private void resetFieldStyles() {
        patientIdCombo.setStyle(null);
        doctorIDtf.setStyle(null);
        fNameTf.setStyle(null);
        lNameTf.setStyle(null);
        CheckupDate.setStyle(null);
        statusBox.setStyle(null);
        complaintArea.setStyle(null);
        DiagnosisArea.setStyle(null);
        dispositionArea.setStyle(null);
    }

    private void clearForm() {
        patientIdCombo.getSelectionModel().clearSelection();
        doctorIDtf.clear();
        fNameTf.clear();
        lNameTf.clear();
        CheckupDate.setValue(null);
        statusBox.getSelectionModel().clearSelection();
        complaintArea.clear();
        DiagnosisArea.clear();
        dispositionArea.clear();
        resetFieldStyles();
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
}
