package Controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Models.ListModel;
import db.DatabaseConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class N_PharmacyReq implements Initializable {

    @FXML
    private Button AddMedBtn;

    @FXML
    private Button BackBttn;

    @FXML
    private Button ClearBtn;

    @FXML
    private Button SubmitBtn;

    @FXML
    private ComboBox<String> dosage;

    @FXML
    private TableColumn<ListModel, String> dosagecol;

    @FXML
    private TableColumn<ListModel, String> idcol;

    @FXML
    private TableView<ListModel> listTableView;

    @FXML
    private TextField medidtf;

    @FXML
    private ComboBox<String> medname;

    @FXML
    private TableColumn<ListModel, String> namecol;

    @FXML
    private TableColumn<ListModel, Integer> qtycol;

    @FXML
    private TextField qtytf;

    @FXML
    private TextField recordidtf;

    @FXML
    private ComboBox<String> recordname;

    private ObservableList<ListModel> EmployeeList = FXCollections.observableArrayList();

    private ObservableList<String> allMedicineNames = FXCollections.observableArrayList();

    private FilteredList<String> filteredMedicineNames;

    private ObservableList<String> allDosage = FXCollections.observableArrayList();

    private FilteredList<String> filteredDosage;

    private ObservableList<String> allRecordNames = FXCollections.observableArrayList();

    private FilteredList<String> filteredRecords;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        refreshEmployeeTable();
        setupRowContextMenu();
        loadMedicineNames();
        setupMedicineComboBox();
        loadDosage();
        setupDosageComboBox();
        loadRecordNames();
        setupRecordComboBox();

    }

    private void loadMedicineNames() {
        allMedicineNames.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = "SELECT med_name FROM medicine";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                allMedicineNames.add(rs.getString("med_name"));
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupMedicineComboBox() {
        if (allMedicineNames == null) {
            allMedicineNames = FXCollections.observableArrayList();
        }
        medname.setItems(allMedicineNames);

        if (!allMedicineNames.isEmpty()) {
            filteredMedicineNames = new FilteredList<>(allMedicineNames, _ -> true);
            medname.setItems(filteredMedicineNames);

            medname.getEditor().textProperty().addListener((_, _, newValue) -> {
                filteredMedicineNames.setPredicate(item -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return item.toLowerCase().contains(lowerCaseFilter);
                });

                boolean exactMatch = false;
                if (newValue != null && !newValue.isEmpty()) {
                    try {
                        Connection conn = DatabaseConnect.connect();
                        String query = "SELECT COUNT(*) FROM medicine WHERE LOWER(med_name) = ?";
                        PreparedStatement pstmt = conn.prepareStatement(query);
                        pstmt.setString(1, newValue.toLowerCase());
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            exactMatch = true;
                        }
                        rs.close();
                        pstmt.close();
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (newValue != null && !newValue.isEmpty() && !filteredMedicineNames.isEmpty() && !exactMatch) {
                    if (!medname.isShowing()) {
                        medname.show();
                    }
                } else {
                    medname.hide();
                }

                if (newValue == null || newValue.isEmpty()) {
                    medname.setValue(null);
                }
            });
            medname.valueProperty().addListener((_, _, newVal) -> {
                if (newVal != null && !newVal.isEmpty()) {
                    try {
                        Connection conn = DatabaseConnect.connect();
                        String query = "SELECT med_id FROM medicine WHERE med_name = ?";
                        PreparedStatement pstmt = conn.prepareStatement(query);
                        pstmt.setString(1, newVal);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            medidtf.setText(String.valueOf(rs.getInt("med_id")));
                        }

                        rs.close();
                        pstmt.close();
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void setupRowContextMenu() {
    }

    private void refreshEmployeeTable() {

    }

    private void setupTableColumns() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        dosagecol.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        qtycol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        listTableView.setItems(EmployeeList);
    }
    // Fixes for record ComboBox and record name loading

    // Loads patient full names into allRecordNames for ComboBox
    private void loadRecordNames() {
        allRecordNames.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = "SELECT patient_id, f_name, m_name, l_name FROM patient";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                StringBuilder fullName = new StringBuilder();
                fullName.append(rs.getString("f_name"));
                String mName = rs.getString("m_name");
                if (mName != null && !mName.trim().isEmpty()) {
                    fullName.append(" ").append(mName);
                }
                fullName.append(" ").append(rs.getString("l_name"));
                allRecordNames.add(fullName.toString().trim());
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sets up ComboBox for patient names and fills recordidtf when selected
    private void setupRecordComboBox() {
        if (allRecordNames == null) {
            allRecordNames = FXCollections.observableArrayList();
        }
        recordname.setItems(allRecordNames);

        if (!allRecordNames.isEmpty()) {
            filteredRecords = new FilteredList<>(allRecordNames, _ -> true);
            recordname.setItems(filteredRecords);

            recordname.getEditor().textProperty().addListener((_, _, newValue) -> {
                filteredRecords.setPredicate(item -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return item.toLowerCase().contains(lowerCaseFilter);
                });

                if (newValue == null || newValue.isEmpty()) {
                    recordname.setValue(null);
                }
            });

            recordname.valueProperty().addListener((_, _, newVal) -> {
                if (newVal != null && !newVal.isEmpty()) {
                    try {
                        Connection conn = DatabaseConnect.connect();
                        // Split full name into parts
                        String[] parts = newVal.trim().split("\\s+");
                        String fName = parts.length > 0 ? parts[0] : "";
                        String mName = parts.length == 3 ? parts[1] : null;
                        String lName = parts.length == 3 ? parts[2] : (parts.length == 2 ? parts[1] : "");

                        String query = "SELECT patient_id FROM patient WHERE LOWER(f_name) = ? AND LOWER(l_name) = ?" +
                                (mName != null ? " AND LOWER(m_name) = ?" : "");
                        PreparedStatement pstmt = conn.prepareStatement(query);
                        pstmt.setString(1, fName.toLowerCase());
                        pstmt.setString(2, lName.toLowerCase());
                        if (mName != null) {
                            pstmt.setString(3, mName.toLowerCase());
                        }
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            recordidtf.setText(String.valueOf(rs.getInt("patient_id")));
                        } else {
                            recordidtf.clear();
                        }

                        rs.close();
                        pstmt.close();
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    recordidtf.clear();
                }
            });
        }
    }

    private void setupDosageComboBox() {
        if (allDosage == null) {
            allDosage = FXCollections.observableArrayList();
        }
        dosage.setItems(allDosage);

        filteredDosage = new FilteredList<>(allDosage, _ -> true);
        dosage.setItems(filteredDosage);

        // Update dosage list when medicine changes
        medname.valueProperty().addListener((_, _, _) -> {
            loadDosage();
        });

        dosage.getEditor().textProperty().addListener((_, _, newValue) -> {
            filteredDosage.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return item.toLowerCase().contains(lowerCaseFilter);
            });

            boolean exactMatch = false;
            if (newValue != null && !newValue.isEmpty()) {
                try {
                    Connection conn = DatabaseConnect.connect();
                    String query = "SELECT COUNT(*) FROM batch WHERE LOWER(batch_dosage) = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, newValue.toLowerCase());
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        exactMatch = true;
                    }
                    rs.close();
                    pstmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (newValue != null && !newValue.isEmpty() && !filteredDosage.isEmpty() && !exactMatch) {
                if (!dosage.isShowing()) {
                    dosage.show();
                }
            } else {
                dosage.hide();
            }

            if (newValue == null || newValue.isEmpty()) {
                dosage.setValue(null);
            }
        });

        dosage.valueProperty().addListener((_, _, _) -> {
            // Optionally handle dosage selection
        });
    }

    private void loadDosage() {
        allDosage.clear();
        String medIdText = medidtf.getText();
        if (medIdText == null || medIdText.isEmpty()) {
            return;
        }
        try {
            Connection conn = DatabaseConnect.connect();
            String query = "SELECT batch_dosage FROM batch WHERE med_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, Integer.parseInt(medIdText));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                allDosage.add(rs.getString("batch_dosage"));
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void AddMedBtnAction(ActionEvent event) {

    }

    @FXML
    void BackBttnAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void ClearBtnAction(ActionEvent event) {

    }

    @FXML
    void SubmitBtnAction(ActionEvent event) {

    }

}
