package Controllers.NURSE;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import Controllers.ViewState;
import Models.ListModel;
import Models.MyRequestModel;
import db.DatabaseConnect;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;
import util.SceneLoader;

public class N_RequestMonitorController implements Initializable {

    @FXML
    private Button AddMedBtn;

    @FXML
    private Button ClearBtn;

    @FXML
    private Button LogoutBtn;

    @FXML
    private Button newreqBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private Button clipboardBtn;

    @FXML
    private Button closeBtn;

    @FXML
    private Button crossBtn;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button hamburgermenuBtn;

    @FXML
    private Button homeBtn;

    @FXML
    private TableColumn<MyRequestModel, Integer> idcol;

    @FXML
    private TableColumn<ListModel, Integer> batchidcol;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TextField reqidtf;

    @FXML
    private TextField recordidtf;

    @FXML
    private TextField batchidtf;

    @FXML
    private TextField qtytf;

    @FXML
    private Button minimizeBtn;

    @FXML
    private Text nameLabel;

    @FXML
    private TableColumn<MyRequestModel, Integer> recordcol;

    @FXML
    private TableColumn<ListModel, String> namecol;

    @FXML
    private TableColumn<ListModel, String> dosagecol;

    @FXML
    private TableView<MyRequestModel> reqTableView;

    @FXML
    private TableView<ListModel> listTableView;

    @FXML
    private TableColumn<MyRequestModel, String> reqdatecol;

    @FXML
    private TableColumn<ListModel, Integer> qtycol;

    @FXML
    private TableColumn<MyRequestModel, Boolean> statcol;

    @FXML
    private ComboBox<String> recordname;

    public static int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

    private Map<Integer, String> recordIdToNameMap = new HashMap<>();

    private ObservableList<MyRequestModel> EmployeeList = FXCollections.observableArrayList();
    private ObservableList<String> patientNames = FXCollections.observableArrayList();

    private Alert a = new Alert(AlertType.NONE);

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        setupTableColumns();
        refreshEmployeeTable();
        populatePatientNamesComboBox();
        String nurseName = DatabaseConnect.getNurseName(employeeId);
        nameLabel.setText(nurseName != null ? nurseName : "Name not found");

        recordname.getSelectionModel().selectedItemProperty().addListener((_, _, newVal) -> {
            if (newVal != null) {
                // Find the record ID for the selected patient name
                Optional<Map.Entry<Integer, String>> entry = recordIdToNameMap.entrySet().stream()
                        .filter(e -> e.getValue().equals(newVal))
                        .findFirst();

                if (entry.isPresent()) {
                    recordidtf.setText(String.valueOf(entry.get().getKey()));
                }
            }
        });

        initializeRowSelectionListener();
        initializeRowSelectionListener2();
        batchidcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        dosagecol.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        qtycol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    private void populatePatientNamesComboBox() {
        patientNames.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT DISTINCT CONCAT(p.f_name, ' ', p.l_name) AS patient_name,
                           r.record_id
                    FROM patient p
                    JOIN records r ON p.patient_id = r.patient_id
                    WHERE r.doctor_id = ? OR r.status = 1
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            Map<Integer, String> recordIdToNameMap = new HashMap<>();

            while (rs.next()) {
                String patientName = rs.getString("patient_name");
                int recordId = rs.getInt("record_id");
                patientNames.add(patientName);
                recordIdToNameMap.put(recordId, patientName);
            }

            recordname.setItems(patientNames);

            // Store the mapping as a class field
            this.recordIdToNameMap = recordIdToNameMap;

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("reqid"));
        recordcol.setCellValueFactory(new PropertyValueFactory<>("recordid"));
        reqdatecol.setCellValueFactory(new PropertyValueFactory<>("reqdate"));
        statcol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void refreshEmployeeTable() {
        EmployeeList.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT
                        r.request_id, r.record_id, r.request_date, r.status
                    FROM request r WHERE r.encoded_by = ?
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                EmployeeList.add(new MyRequestModel(
                        rs.getInt("request_id"),
                        rs.getInt("record_id"),
                        rs.getDate("request_date"),
                        rs.getString("status")));
            }

            reqTableView.setItems(EmployeeList);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initializeRowSelectionListener() {
        reqTableView.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null) {
                int selectedRequestId = newSelection.getReqid();
                int recordId = newSelection.getRecordid();
                ObservableList<ListModel> listItems = FXCollections.observableArrayList();
                reqidtf.setText(String.valueOf(selectedRequestId));
                recordidtf.setText(String.valueOf(recordId));

                // Set the ComboBox value based on the record ID
                String patientName = recordIdToNameMap.get(recordId);
                if (patientName != null) {
                    recordname.getSelectionModel().select(patientName);
                }
                batchidtf.clear();
                qtytf.clear();
                try {
                    Connection conn = DatabaseConnect.connect();
                    String query = """
                            SELECT
                                l.batch_id,
                                m.med_name AS name,
                                b.batch_dosage,
                                l.qty
                            FROM
                                requestlist l
                            JOIN
                                batch b ON l.batch_id = b.batch_id
                            LEFT JOIN
                                medicine m ON b.med_id = m.med_id
                            WHERE
                                l.req_id = ?
                            """;
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, selectedRequestId);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        listItems.add(new ListModel(
                                rs.getInt("batch_id"),
                                rs.getString("name"),
                                rs.getString("batch_dosage"),
                                rs.getInt("qty")));
                    }
                    listTableView.setItems(listItems);
                    rs.close();
                    pstmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeRowSelectionListener2() {
        listTableView.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null) {
                batchidtf.setText(String.valueOf(newSelection.getId()));
                qtytf.setText(String.valueOf(newSelection.getQuantity()));
            }
        });
    }

    @FXML
    void updateBtnPressed(ActionEvent event) {
        String requestIdText = reqidtf.getText();
        String recordIdText = recordidtf.getText();
        String batchIdText = batchidtf.getText();
        String quantityText = qtytf.getText();

        if (requestIdText.isEmpty()) {
            showAlert("Error", "Please select a request to update");
            return;
        }

        try {
            int requestId = Integer.parseInt(requestIdText);
            int recordId = Integer.parseInt(recordIdText);

            Connection conn = DatabaseConnect.connect();
            String query = "UPDATE request SET record_id = ? WHERE request_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, recordId);
            pstmt.setInt(2, requestId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No rows affected - request not found");
            }

            pstmt.close();
            conn.close();

            // Check if we're updating the request or the request list item
            if (!batchIdText.isEmpty() && !quantityText.isEmpty()) {
                // Updating a request list item
                int batchId = Integer.parseInt(batchIdText);
                int newQuantity = Integer.parseInt(quantityText);

                // Check stock availability
                if (!isStockAvailable(batchId, newQuantity)) {
                    showAlert("Error", "Requested quantity exceeds available stock");
                    return;
                }

                // Update the request list item
                updateRequestListItem(requestId, batchId, newQuantity);
            } else {

            }

            refreshEmployeeTable();
            ClearBtnAction(event);
            showAlert("Success", "Update successful");
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numeric values");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private boolean isStockAvailable(int batchId, int requestedQuantity) throws SQLException {
        Connection conn = DatabaseConnect.connect();
        String query = "SELECT batch_stock FROM batch WHERE batch_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, batchId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            int availableStock = rs.getInt("batch_stock");
            return requestedQuantity <= availableStock;
        }

        rs.close();
        pstmt.close();
        conn.close();
        return false;
    }

    private void updateRequestListItem(int requestId, int batchId, int newQuantity) throws SQLException {
        Connection conn = DatabaseConnect.connect();
        String query = "UPDATE requestlist SET qty = ? WHERE req_id = ? AND batch_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, newQuantity);
        pstmt.setInt(2, requestId);
        pstmt.setInt(3, batchId);

        int rowsAffected = pstmt.executeUpdate();
        if (rowsAffected == 0) {
            throw new SQLException("No rows affected - item not found");
        }

        pstmt.close();
        conn.close();
    }

    @FXML
    void ClearBtnAction(ActionEvent event) {
        // Clear text fields
        reqidtf.clear();
        recordidtf.clear();
        batchidtf.clear();
        qtytf.clear();

        // Clear selections
        reqTableView.getSelectionModel().clearSelection();
        listTableView.getSelectionModel().clearSelection();
        recordname.getSelectionModel().clearSelection();

        // Clear the list table view
        listTableView.setItems(FXCollections.observableArrayList());

        // Refresh the main table
        refreshEmployeeTable();
    }

    @FXML
    void LogOutActionBttn(ActionEvent event) {
        // Existing implementation
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Rest of the existing methods remain unchanged...
    @FXML
    void newreqBtnPressed(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/N_PharmacyRequest.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.setTitle("Pharmacy Request");
            popupStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            popupStage.setScene(scene);
            popupStage.setResizable(false);
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open update form: " + e.getMessage());
        }
    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Dashboard.fxml");
    }

    @FXML
    private void closeAction(ActionEvent Action) {
        Stage currentStage = (Stage) closeBtn.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void minimizeAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setIconified(true);
    }

    @FXML
    void PharmacyBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_RequestMonitor.fxml");
    }

    @FXML
    private void toggleHamburgerMenu() {
        Timeline timeline = new Timeline();
        double targetWidth = ViewState.isHamburgerPaneExtended ? 107 : 230;

        KeyValue keyValue = new KeyValue(hamburgerPane.prefWidthProperty(), targetWidth);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

        ViewState.isHamburgerPaneExtended = !ViewState.isHamburgerPaneExtended;
    }

    @FXML
    void accountBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Account.fxml");
    }

    @FXML
    void LogoutBtnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Are you sure you want to log out?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/LoginPage.fxml"));
                Parent root = loader.load();

                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(root));
                loginStage.initStyle(StageStyle.UNDECORATED);
                loginStage.setResizable(false);
                loginStage.show();

                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.close();

            } catch (IOException e) {
                e.printStackTrace();
                a.setAlertType(AlertType.ERROR);
                a.setContentText("Error loading page.");
                a.show();
            }
        }
    }
}