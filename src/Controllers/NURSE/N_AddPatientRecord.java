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
            f_addresstf, f_contactnotf, f_fullnametf, firstnametf, lastnametf,
            m_addresstf, m_contactnotf, m_fullnametf, middlenametf, nationalitytf, occupationtf,
            religiontf;

    @FXML
    private TextArea allergicarea;

    @FXML
    private DatePicker birthdate;

    @FXML
    private ComboBox<String> patcatcombobox, gendertf;

    private Map<String, Integer> patientCategoryMap = new HashMap<>();

    @FXML
    public void initialize() {
        loadPatientCategories();
        gendertf.getItems().addAll("Male", "Female");
        gendertf.getSelectionModel().selectFirst();
        patcatcombobox.getSelectionModel().selectFirst();
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

        patcatcombobox.getSelectionModel().selectFirst();
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
        // Validate required fields
        if (firstnametf.getText().trim().isEmpty() ||
                lastnametf.getText().trim().isEmpty() ||
                middlenametf.getText().trim().isEmpty() ||
                agetf.getText().trim().isEmpty() ||
                gendertf.getValue() == null ||
                emailaddtf.getText().trim().isEmpty() ||
                birthdate.getValue() == null ||
                addresstf.getText().trim().isEmpty() ||
                birthplacetf.getText().trim().isEmpty() ||
                nationalitytf.getText().trim().isEmpty() ||
                religiontf.getText().trim().isEmpty() ||
                occupationtf.getText().trim().isEmpty() ||
                contactnotf.getText().trim().isEmpty() ||
                patcatcombobox.getValue() == null ||
                f_fullnametf.getText().trim().isEmpty() ||
                f_addresstf.getText().trim().isEmpty() ||
                f_contactnotf.getText().trim().isEmpty() ||
                m_fullnametf.getText().trim().isEmpty() ||
                m_addresstf.getText().trim().isEmpty() ||
                m_contactnotf.getText().trim().isEmpty() ||
                em_fullnametf.getText().trim().isEmpty() ||
                em_addresstf.getText().trim().isEmpty() ||
                em_contacnotf.getText().trim().isEmpty() ||
                allergicarea.getText().trim().isEmpty() ||
                e_fullnametf.getText().trim().isEmpty() ||
                e_contactnotf.getText().trim().isEmpty() ||
                e_addresstf.getText().trim().isEmpty() ||
                em_relation.getText().trim().isEmpty()) {
            showAlert("Validation Error", "All fields are required.");
            return;
        }

        // Validate age
        String ageText = agetf.getText();
        int age;
        try {
            age = Integer.parseInt(ageText.trim());
            if (age < 0 || age > 150) {
                showAlert("Validation Error", "Age must be between 0 and 150.");
                return;
            }
        } catch (NumberFormatException nfe) {
            showAlert("Validation Error", "Age must be a valid number.");
            return;
        }

        // Validate email format (simple regex)
        String email = emailaddtf.getText().trim();
        if (!email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            showAlert("Validation Error", "Invalid email address.");
            return;
        }

        // Validate contact numbers (basic: digits only, length 7-15)
        String[] contactFields = {
                contactnotf.getText().trim(),
                f_contactnotf.getText().trim(),
                m_contactnotf.getText().trim(),
                em_contacnotf.getText().trim(),
                e_contactnotf.getText().trim()
        };
        for (String contact : contactFields) {
            if (!contact.matches("\\d{7,15}")) {
                showAlert("Validation Error", "Contact numbers must be 7-15 digits.");
                return;
            }
        }

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
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, firstnametf.getText().trim());
            stmt.setString(2, lastnametf.getText().trim());
            stmt.setString(3, middlenametf.getText().trim());
            stmt.setInt(4, age);
            String genderValue = gendertf.getValue();
            String genderShort = genderValue.equalsIgnoreCase("Male") ? "M" : "F";
            stmt.setString(5, genderShort);
            stmt.setString(6, email);
            stmt.setDate(7, Date.valueOf(birthdate.getValue()));
            stmt.setString(8, addresstf.getText().trim());
            stmt.setString(9, birthplacetf.getText().trim());
            stmt.setString(10, nationalitytf.getText().trim());
            stmt.setString(11, religiontf.getText().trim());
            stmt.setString(12, occupationtf.getText().trim());
            stmt.setString(13, contactnotf.getText().trim());
            String selectedCategoryName = patcatcombobox.getValue();
            Integer selectedCategoryId = patientCategoryMap.get(selectedCategoryName);
            if (selectedCategoryId != null) {
                stmt.setInt(14, selectedCategoryId);
            } else {
                showAlert("Error", "Invalid patient category selected.");
                return;
            }

            stmt.setString(15, f_fullnametf.getText().trim());
            stmt.setString(16, f_addresstf.getText().trim());
            stmt.setString(17, f_contactnotf.getText().trim());

            stmt.setString(18, m_fullnametf.getText().trim());
            stmt.setString(19, m_addresstf.getText().trim());
            stmt.setString(20, m_contactnotf.getText().trim());

            stmt.setString(21, em_fullnametf.getText().trim());
            stmt.setString(22, em_addresstf.getText().trim());
            stmt.setString(23, em_contacnotf.getText().trim());

            stmt.setString(24, allergicarea.getText().trim());
            stmt.setString(25, e_fullnametf.getText().trim());
            stmt.setString(26, e_contactnotf.getText().trim());
            stmt.setString(27, e_addresstf.getText().trim());
            stmt.setString(28, em_relation.getText().trim());

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
