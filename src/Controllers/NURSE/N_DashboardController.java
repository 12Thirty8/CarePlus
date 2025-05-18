package Controllers.NURSE;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;

import Controllers.ViewState;

import Models.RecordsModel;
import db.DatabaseConnect;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;
import util.SceneLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

public class N_DashboardController {
    @FXML
    private Button accountBtn;

    @FXML
    private Button crossBtn;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button hamburgermenuBtn;

    @FXML
    private Button homeBtn, GenerateReportBtn, ArchiveBtn;

    @FXML
    private Button LogoutBtn;

    @FXML
    private TableView<RecordsModel> StkInTableView;

    @FXML
    private TableColumn<RecordsModel, String> patientColumn;

    @FXML
    private TableColumn<RecordsModel, String> patientIdColumn;

    @FXML
    private TableColumn<RecordsModel, String> doctorColumn;

    @FXML
    private TableColumn<RecordsModel, String> diagnosisColumn;

    @FXML
    private TableColumn<RecordsModel, String> dispositionColumn;

    @FXML
    private TableColumn<RecordsModel, String> statusColumn;

    private ObservableList<RecordsModel> recordsModelObservableList = FXCollections.observableArrayList();

    @FXML
    private Text nameLabel;

    @FXML
    private Button closeBtn, minimizeBtn, AddPatientDataBtn;

    private Alert a = new Alert(AlertType.INFORMATION);

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        setupTableColumns();
        refreshRecordsTable();
        setupRowContextMenu();
        int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();
        String nurseName = DatabaseConnect.getNurseName(employeeId);
        nameLabel.setText(nurseName + ", RN");

    }

    private void setupTableColumns() {
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        dispositionColumn.setCellValueFactory(new PropertyValueFactory<>("disposition"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

    }

    private void refreshRecordsTable() {
        recordsModelObservableList.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT
                        CONCAT(COALESCE(r.f_name, ''), ' ', COALESCE(r.l_name, '')) AS patientName,
                        r.patient_id,
                        r.doctor_name,
                        r.diagnosis,
                        r.disposition,
                        r.status
                    FROM records r
                    LEFT JOIN patient p ON r.patient_id = p.patient_id
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                recordsModelObservableList.add(new RecordsModel(
                        rs.getString("patientName"),
                        rs.getInt("patient_id"),
                        rs.getString("doctor_name"),
                        rs.getString("diagnosis"),
                        rs.getString("disposition"),
                        rs.getInt("status")));

            }

            StkInTableView.setItems(recordsModelObservableList);
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    private void crossBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_RequestMonitor.fxml");
    }

    @FXML
    private void AddPatientDataBtnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/N_AddMedicalRecord.fxml"));
            Parent root = loader.load();
            // Create a new pop-up stage
            Stage popupStage = new Stage();
            popupStage.setTitle("Add Patient Data");

            popupStage.initModality(Modality.WINDOW_MODAL); // Makes it modal
            Scene scene = new Scene(root);
            popupStage.setScene(scene);
            popupStage.setResizable(false); // Optional: make it fixed size
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow()); // Set owner to current window
            popupStage.showAndWait(); // Wait until this window is closed (optional)
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open update form: " + e.getMessage());
        }
    }

    private void setupRowContextMenu() {
        StkInTableView.setRowFactory(_ -> {
            TableRow<RecordsModel> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem updateItem = new MenuItem("Update");
            updateItem.setOnAction(_ -> {
                RecordsModel selectedRecord = row.getItem();
                if (selectedRecord != null) {
                    openUpdateMedicalRecordWindow(selectedRecord);
                }
            });

            contextMenu.getItems().add(updateItem);

            // Set context menu only for non-empty rows
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu));

            return row;
        });
    }

    private void openUpdateMedicalRecordWindow(RecordsModel record) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/N_UpdateRecord.fxml"));
            Parent root = loader.load();

            // Pass the selected record to the controller
            N_UpdateMedicalRecord controller = loader.getController();
            controller.setRecordData(record); // This method must be defined in N_UpdateMedicalRecord.java

            Stage stage = new Stage();
            stage.setTitle("Update Medical Record");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait for the update to finish

            // Refresh table after closing update window
            refreshRecordsTable();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open update window.").showAndWait();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Dashboard.fxml");
    }

    @FXML
    void accountBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Account.fxml");
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

    @FXML
    void movetoProductBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_PatientData.fxml");
    }

    @FXML
    void movetoStocksBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Dashboard.fxml");
    }

    @FXML
    void GenerateReportBtnAction(ActionEvent event) {
        // NO FUNCTIONALITY YET

        
    }
    
    @FXML
    void ArchiveBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_ArchiveReports.fxml");
    }

}