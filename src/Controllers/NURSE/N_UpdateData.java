package Controllers.NURSE;

import db.DatabaseConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Models.PatientModel;
import util.GetCurrentEmployeeID;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class N_UpdateData {

    @FXML
    private Button cancelbtn, updateBtn, backBtn;

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
    private int patientId;

    public void setPatientData(PatientModel patient) {
        this.patientId = patient.getPatient_id();

        try (Connection conn = DatabaseConnect.connect()) {
            String query = "SELECT * FROM patient WHERE patient_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, patientId);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                firstnametf.setText(rs.getString("f_name"));
                lastnametf.setText(rs.getString("l_name"));
                middlenametf.setText(rs.getString("m_name"));
                agetf.setText(String.valueOf(rs.getInt("age")));
                gendertf.setValue(rs.getString("gender"));
                emailaddtf.setText(rs.getString("email"));
                birthdate.setValue(rs.getDate("dob").toLocalDate());
                addresstf.setText(rs.getString("complete_address"));
                birthplacetf.setText(rs.getString("birthplace"));
                nationalitytf.setText(rs.getString("nationality"));
                religiontf.setText(rs.getString("religion"));
                occupationtf.setText(rs.getString("occupation"));
                contactnotf.setText(rs.getString("contact_no"));

                f_fullnametf.setText(rs.getString("father_name"));
                f_addresstf.setText(rs.getString("father_address"));
                f_contactnotf.setText(rs.getString("father_contact_no"));

                m_fullnametf.setText(rs.getString("mother_name"));
                m_addresstf.setText(rs.getString("mother_address"));
                m_contactnotf.setText(rs.getString("mother_contact_no"));

                em_fullnametf.setText(rs.getString("employer_name"));
                em_addresstf.setText(rs.getString("employer_address"));
                em_contacnotf.setText(rs.getString("employer_contact_no"));

                allergicarea.setText(rs.getString("allergic_to"));
                e_fullnametf.setText(rs.getString("emergency_contact_name"));
                e_contactnotf.setText(rs.getString("emergency_contact_no"));
                e_addresstf.setText(rs.getString("emergency_contact_address"));
                em_relation.setText(rs.getString("relation_to_the_patient"));

                // Set selected category
                int categoryId = rs.getInt("patient_category");
                for (var entry : patientCategoryMap.entrySet()) {
                    if (entry.getValue() == categoryId) {
                        patcatcombobox.setValue(entry.getKey());
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        loadPatientCategories();
        gendertf.getItems().addAll("Male", "Female");
    }

    private void loadPatientCategories() {
        try (Connection conn = DatabaseConnect.connect()) {
            String sql = "SELECT patient_category_id, category_name FROM patient_category";
            PreparedStatement stmt = conn.prepareStatement(sql);
            var rs = stmt.executeQuery();

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

    @FXML
    void clearBtnAction(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        firstnametf.clear();
        lastnametf.clear();
        middlenametf.clear();
        agetf.clear();
        gendertf.getSelectionModel().selectFirst();
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
    void backBtnAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void UpdatebtnAction(ActionEvent event) {
        try (Connection conn = DatabaseConnect.connect()) {
            String sql = """
                        UPDATE patient SET
                            f_name = ?, l_name = ?, m_name = ?, age = ?, gender = ?, email = ?, dob = ?, complete_address = ?, birthplace = ?,
                            nationality = ?, religion = ?, occupation = ?, contact_no = ?, patient_category = ?,
                            father_name = ?, father_address = ?, father_contact_no = ?,
                            mother_name = ?, mother_address = ?, mother_contact_no = ?,
                            employer_name = ?, employer_address = ?, employer_contact_no = ?,
                            allergic_to = ?, emergency_contact_name = ?, emergency_contact_no = ?,
                            emergency_contact_address = ?, relation_to_the_patient = ?,
                            encoded_by = ?
                        WHERE patient_id = ?
                    """;

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, firstnametf.getText());
            stmt.setString(2, lastnametf.getText());
            stmt.setString(3, middlenametf.getText());
            stmt.setInt(4, Integer.parseInt(agetf.getText()));
            stmt.setString(5, gendertf.getValue());
            stmt.setString(6, emailaddtf.getText());
            stmt.setDate(7, Date.valueOf(birthdate.getValue()));
            stmt.setString(8, addresstf.getText());
            stmt.setString(9, birthplacetf.getText());
            stmt.setString(10, nationalitytf.getText());
            stmt.setString(11, religiontf.getText());
            stmt.setString(12, occupationtf.getText());
            stmt.setString(13, contactnotf.getText());

            String selectedCategory = patcatcombobox.getValue();
            Integer categoryId = patientCategoryMap.get(selectedCategory);
            if (categoryId != null) {
                stmt.setInt(14, categoryId);
            } else {
                showAlert("Error", "Invalid category");
                return;
            }

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

            int currentEmployeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();
            stmt.setInt(29, currentEmployeeId);
            stmt.setInt(30, patientId);

            stmt.executeUpdate();
            showAlert("Success", "Patient updated successfully.");

            // Optionally close the window
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update patient: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
