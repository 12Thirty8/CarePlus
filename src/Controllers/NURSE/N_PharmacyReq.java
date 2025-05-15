package Controllers.NURSE;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import util.GetCurrentEmployeeID;

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

    private ObservableList<ListModel> RequestList = FXCollections.observableArrayList();

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

        listTableView.setItems(RequestList);
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

                        // First get patient_id
                        String patientQuery = "SELECT patient_id FROM patient WHERE LOWER(f_name) = ? AND LOWER(l_name) = ?"
                                +
                                (mName != null ? " AND LOWER(m_name) = ?" : "");
                        PreparedStatement patientStmt = conn.prepareStatement(patientQuery);
                        patientStmt.setString(1, fName.toLowerCase());
                        patientStmt.setString(2, lName.toLowerCase());
                        if (mName != null) {
                            patientStmt.setString(3, mName.toLowerCase());
                        }
                        ResultSet patientRs = patientStmt.executeQuery();

                        if (patientRs.next()) {
                            int patientId = patientRs.getInt("patient_id");

                            // Now get the most recent record_id for this patient
                            String recordQuery = "SELECT record_id FROM records WHERE patient_id = ? ORDER BY record_date DESC LIMIT 1";
                            PreparedStatement recordStmt = conn.prepareStatement(recordQuery);
                            recordStmt.setInt(1, patientId);
                            ResultSet recordRs = recordStmt.executeQuery();

                            if (recordRs.next()) {
                                recordidtf.setText(String.valueOf(recordRs.getInt("record_id")));
                            } else {
                                recordidtf.clear();
                                // Optional: show message that no records exist for this patient
                            }

                            recordRs.close();
                            recordStmt.close();
                        } else {
                            recordidtf.clear();
                        }

                        patientRs.close();
                        patientStmt.close();
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
        String medIdStr = medidtf.getText();
        String medName = medname.getValue();
        String medDosage = dosage.getValue();
        String qtyStr = qtytf.getText();

        if (medIdStr == null || medIdStr.isEmpty() ||
                medName == null || medName.isEmpty() ||
                medDosage == null || medDosage.isEmpty() ||
                qtyStr == null || qtyStr.isEmpty()) {
            showAlert("Validation Error", "Please fill in all medicine fields (ID, Name, Dosage, Quantity)",
                    AlertType.ERROR);
            return;
        }

        // Validate that quantity is a number
        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                showAlert("Validation Error", "Quantity must be a positive number", AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Quantity must be a valid number", AlertType.ERROR);
            return;
        }

        int medId;
        try {
            medId = Integer.parseInt(medidtf.getText());
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Medicine ID must be a valid number", AlertType.ERROR);
            return;
        }

        ListModel entry = new ListModel(medId, medName, medDosage, quantity);
        RequestList.add(entry);

        // Optionally clear fields after adding
        medidtf.clear();
        medname.setValue(null);
        dosage.setValue(null);
        qtytf.clear();
    }

    @FXML
    void BackBttnAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void ClearBtnAction(ActionEvent event) {
        RequestList.clear();
        medidtf.clear();
        medname.setValue(null);
        dosage.setValue(null);
        qtytf.clear();
        recordidtf.clear();
        recordname.setValue(null);

    }

    @FXML
    void SubmitBtnAction(ActionEvent event) {
        String recordIdStr = recordidtf.getText();
        if (recordIdStr == null || recordIdStr.isEmpty() || RequestList.isEmpty()) {
            showAlert("Error", "Please select a patient and add at least one medicine", AlertType.ERROR);
            return;
        }

        int recordId;
        try {
            recordId = Integer.parseInt(recordIdStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid patient record ID", AlertType.ERROR);
            return;
        }

        try {
            Connection conn = DatabaseConnect.connect();
            conn.setAutoCommit(false);

            // 1. Check if record exists
            String checkRecordSql = "SELECT 1 FROM records WHERE record_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkRecordSql)) {
                checkStmt.setInt(1, recordId);
                ResultSet checkRs = checkStmt.executeQuery();
                if (!checkRs.next()) {
                    showAlert("Error", "Patient record not found", AlertType.ERROR);
                    conn.setAutoCommit(true);
                    conn.close();
                    return;
                }
            }

            // 2. Insert pharmacy request
            String insertReqSql = "INSERT INTO request (record_id, request_date, encoded_by, status) VALUES (?, CURRENT_DATE, ?, 0)";
            int requestId;
            try (PreparedStatement reqStmt = conn.prepareStatement(insertReqSql,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                reqStmt.setInt(1, recordId);
                reqStmt.setInt(2, GetCurrentEmployeeID.fetchEmployeeIdFromSession());
                reqStmt.executeUpdate();

                try (ResultSet reqKeys = reqStmt.getGeneratedKeys()) {
                    if (reqKeys.next()) {
                        requestId = reqKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to get request ID");
                    }
                }
            }

            // 3. Process each medicine
            for (ListModel entry : RequestList) {
                int medId = entry.getId();
                String medDosage = entry.getDosage();
                int qty = entry.getQuantity();

                // Find available batch
                String batchSql = "SELECT batch_id, batch_stock FROM batch WHERE med_id = ? AND batch_dosage = ? AND batch_stock >= ? AND status_id = 7 ORDER BY batch_exp ASC LIMIT 1";
                try (PreparedStatement batchStmt = conn.prepareStatement(batchSql)) {
                    batchStmt.setInt(1, medId);
                    batchStmt.setString(2, medDosage);
                    batchStmt.setInt(3, qty);

                    try (ResultSet batchRs = batchStmt.executeQuery()) {
                        if (batchRs.next()) {
                            int batchId = batchRs.getInt("batch_id");

                            // Insert into requestlist
                            String insertItemSql = "INSERT INTO requestlist (req_id, batch_id, qty) VALUES (?, ?, ?)";
                            try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {
                                itemStmt.setInt(1, requestId);
                                itemStmt.setInt(2, batchId);
                                itemStmt.setInt(3, qty);
                                itemStmt.executeUpdate();
                            }
                        } else {
                            conn.rollback();
                            showAlert("Error", "Insufficient stock for " + entry.getName() + " (" + medDosage + ")",
                                    AlertType.ERROR);
                            return;
                        }
                    }
                }
            }

            conn.commit();
            showAlert("Success", "Pharmacy request submitted successfully", AlertType.INFORMATION);

            // Clear form
            RequestList.clear();
            medidtf.clear();
            medname.setValue(null);
            dosage.setValue(null);
            qtytf.clear();
            recordidtf.clear();
            recordname.setValue(null);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
