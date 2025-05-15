package Controllers.NURSE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import Models.PatientModel;
import db.DatabaseConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class N_ViewPatientData {

    @FXML
    private Button BackBttn, cancelbtn, savebtn;

    @FXML
    private TextField firstnametf, middlenametf, lastnametf, birthplacetf, addresstf, nationalitytf, religiontf,
            occupationtf, agetf, contactnotf, emailaddtf, f_fullnametf, f_addresstf, f_contactnotf, m_fullnametf,
            m_addresstf, m_contactnotf, e_fullnametf, e_addresstf, e_contactnotf, em_fullnametf, em_contacnotf,
            em_addresstf, em_relation;

    @FXML
    private ComboBox<String> gendertf, patcatcombobox;

    @FXML
    private DatePicker birthdate;

    @FXML
    private TextArea allergicarea;

    private PatientModel patient;
    private final Alert alert = new Alert(Alert.AlertType.INFORMATION);

    /** disable editing on all fields */
    @FXML
    public void initialize() {
        firstnametf.setEditable(false);
        middlenametf.setEditable(false);
        lastnametf.setEditable(false);
        birthdate.setDisable(true);
        birthplacetf.setEditable(false);
        addresstf.setEditable(false);
        nationalitytf.setEditable(false);
        religiontf.setEditable(false);
        occupationtf.setEditable(false);
        agetf.setEditable(false);
        gendertf.setDisable(true);
        contactnotf.setEditable(false);
        emailaddtf.setEditable(false);
        patcatcombobox.setDisable(true);
        allergicarea.setEditable(false);

        f_fullnametf.setEditable(false);
        f_addresstf.setEditable(false);
        f_contactnotf.setEditable(false);
        m_fullnametf.setEditable(false);
        m_addresstf.setEditable(false);
        m_contactnotf.setEditable(false);
        e_fullnametf.setEditable(false);
        e_addresstf.setEditable(false);
        e_contactnotf.setEditable(false);
        em_fullnametf.setEditable(false);
        em_addresstf.setEditable(false);
        em_contacnotf.setEditable(false);
        em_relation.setEditable(false);
    }

    /** Called by the row‐context setup to inject a patient model */
    public void setPatient(PatientModel p) {
        this.patient = p;
        loadPatientDetails();
    }

    /** Fetch patient data and category name, then populate the form */
    private void loadPatientDetails() {
        String sql = """
                    SELECT p.*, c.category_name
                    FROM patient p
                    LEFT JOIN patient_category c
                      ON p.patient_category = c.patient_category_id
                    WHERE p.patient_id = ?
                """;

        try (Connection conn = DatabaseConnect.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patient.getPatient_id());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    firstnametf.setText(rs.getString("f_name"));
                    middlenametf.setText(rs.getString("m_name"));
                    lastnametf.setText(rs.getString("l_name"));

                    LocalDate dob = rs.getDate("dob").toLocalDate();
                    birthdate.setValue(dob);

                    birthplacetf.setText(rs.getString("birthplace"));
                    addresstf.setText(rs.getString("complete_address"));

                    nationalitytf.setText(rs.getString("nationality"));
                    religiontf.setText(rs.getString("religion"));
                    occupationtf.setText(rs.getString("occupation"));

                    agetf.setText(String.valueOf(rs.getInt("age")));
                    contactnotf.setText(rs.getString("contact_no"));
                    emailaddtf.setText(rs.getString("email"));

                    gendertf.setValue(rs.getString("gender"));
                    patcatcombobox.setValue(rs.getString("category_name"));
                    allergicarea.setText(rs.getString("allergic_to"));

                    f_fullnametf.setText(rs.getString("father_name"));
                    f_addresstf.setText(rs.getString("father_address"));
                    f_contactnotf.setText(rs.getString("father_contact_no"));

                    m_fullnametf.setText(rs.getString("mother_name"));
                    m_addresstf.setText(rs.getString("mother_address"));
                    m_contactnotf.setText(rs.getString("mother_contact_no"));

                    e_fullnametf.setText(rs.getString("employer_name"));
                    e_addresstf.setText(rs.getString("employer_address"));
                    e_contactnotf.setText(rs.getString("employer_contact_no"));

                    em_fullnametf.setText(rs.getString("emergency_contact_name"));
                    em_addresstf.setText(rs.getString("emergency_contact_address"));
                    em_contacnotf.setText(rs.getString("emergency_contact_no"));
                    em_relation.setText(rs.getString("relation_to_the_patient"));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Error loading patient details: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    void BackBttnAction(ActionEvent event) {
        ((Stage) BackBttn.getScene().getWindow()).close();
    }

    @FXML
    void CancelButtonAction(ActionEvent event) {
        ((Stage) cancelbtn.getScene().getWindow()).close();
    }

    @FXML
    void SaveChangesAction(ActionEvent event) {
        // If edits should be saved back to the DB, implement update logic here
        alert.setContentText("(No changes are saved in view-only mode)");
        alert.showAndWait();
        ((Stage) savebtn.getScene().getWindow()).close();
    }
}
