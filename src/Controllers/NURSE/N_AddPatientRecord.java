package Controllers.NURSE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import db.DatabaseConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.GetCurrentEmployeeID;

public class N_AddPatientRecord {

    @FXML
    private Button BackBttn, cancelbtn, savebtn;

    @FXML
    private TextField addresstf, agetf, birthplacetf, contactnotf, e_addresstf, e_contactnotf,
            e_fullnametf, em_addresstf, em_contacnotf, em_fullnametf, em_relation, emailaddtf,
            f_addresstf, f_contactnotf, f_fullnametf, firstnametf, gendertf, lastnametf,
            m_addresstf, m_contactnotf, m_fullnametf, middlenametf, nationalitytf, occupationtf,
            religiontf;

    @FXML
    private TextArea allergicarea;

    @FXML
    private DatePicker birthdate;

    @FXML
    private ComboBox<String> patcatcombobox;

    private Map<String, Integer> patientCategoryMap = new HashMap<>();

    @FXML
    public void initialize() {
        loadPatientCategories();
    }

    private void loadPatientCategories() {
        try (Connection conn = DatabaseConnect.connect()) {
            String sql = "SELECT patient_category_id, category_name FROM patient_category";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("patient_category_id");
                String name = rs.getString("category_name");

                patientCategoryMap.put(name, id);
                patcatcombobox.getItems().add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        firstnametf.clear();
        lastnametf.clear();
        middlenametf.clear();
        agetf.clear();
        gendertf.clear();
        emailaddtf.clear();
        birthdate.setValue(null);
        addresstf.clear();
        birthplacetf.clear();
        nationalitytf.clear();
        religiontf.clear();
        occupationtf.clear();
        contactnotf.clear();

        f_fullnametf.clear();
        f_addresstf.clear();
        f_contactnotf.clear();

        m_fullnametf.clear();
        m_addresstf.clear();
        m_contactnotf.clear();

        em_fullnametf.clear();
        em_addresstf.clear();
        em_contacnotf.clear();

        allergicarea.clear();
        e_fullnametf.clear();
        e_contactnotf.clear();
        e_addresstf.clear();
        em_relation.clear();

        patcatcombobox.getSelectionModel().clearSelection();
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
        try (Connection conn = DatabaseConnect.connect()) {
            String sql = """
                        INSERT INTO patient (
                            f_name, l_name, m_name, age, gender, email, dob, complete_address, birthplace,
                            nationality, religion, occupation, contact_no, patient_category,
                            father_name, father_address, father_contact_no,
                            mother_name, mother_address, mother_contact_no,
                            employer_name, employer_address, employer_contact_no,
                            allergic_to, emergency_contact_name, emergency_contact_no,
                            emergency_contact_address, relation_to_the_patient,
                            encoded_by
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, firstnametf.getText());
            stmt.setString(2, lastnametf.getText());
            stmt.setString(3, middlenametf.getText());
            stmt.setInt(4, Integer.parseInt(agetf.getText()));
            stmt.setString(5, gendertf.getText());
            stmt.setString(6, emailaddtf.getText());
            stmt.setDate(7, Date.valueOf(birthdate.getValue()));
            stmt.setString(8, addresstf.getText());
            stmt.setString(9, birthplacetf.getText());
            stmt.setString(10, nationalitytf.getText());
            stmt.setString(11, religiontf.getText());
            stmt.setString(12, occupationtf.getText());
            stmt.setString(13, contactnotf.getText());
            stmt.setString(14, patcatcombobox.getValue().toString());

            stmt.setString(15, f_fullnametf.getText());
            stmt.setString(16, f_addresstf.getText());
            stmt.setString(17, f_contactnotf.getText());

            stmt.setString(18, m_fullnametf.getText());
            stmt.setString(19, m_addresstf.getText());
            stmt.setString(20, m_contactnotf.getText());

            stmt.setString(21, em_fullnametf.getText());
            stmt.setString(22, em_addresstf.getText());
            stmt.setString(23, em_contacnotf.getText());

            stmt.setString(24, allergicarea.getText());
            stmt.setString(25, e_fullnametf.getText());
            stmt.setString(26, e_contactnotf.getText());
            stmt.setString(27, e_addresstf.getText());
            stmt.setString(28, em_relation.getText());

            // Get employee ID of currently logged-in user
            int currentEmployeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();
            stmt.setInt(29, currentEmployeeId);

            stmt.executeUpdate();
            showAlert("Success", "Patient added successfully.");
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
