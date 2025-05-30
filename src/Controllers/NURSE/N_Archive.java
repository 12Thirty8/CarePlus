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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.SceneLoader;

public class N_Archive {

    @FXML
    private Button BackBttn;

    @FXML
    private Button LogoutBtn;

    @FXML
    private TableView<RecordsModel> StkInTableView;

    @FXML
    private Button clipboardBtn;

    @FXML
    private Button closeBtn;

    @FXML
    private Button crossBtn;

    @FXML
    private TableColumn<RecordsModel, String> diagnosisColumn;

    @FXML
    private TableColumn<RecordsModel, String> dispositionColumn;

    @FXML
    private TableColumn<RecordsModel, String> doctorColumn;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button hamburgermenuBtn;

    @FXML
    private Button homeBtn;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Button minimizeBtn;

    @FXML
    private TableColumn<RecordsModel, String> patientColumn;

    @FXML
    private TableColumn<RecordsModel, String> patientIdColumn;

    @FXML
    private TableColumn<RecordsModel, String> statusColumn;

    private ObservableList<RecordsModel> recordsModelObservableList = FXCollections.observableArrayList();

    private Alert a = new Alert(AlertType.INFORMATION);

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        setupTableColumns();
        refreshRecordsTable();
        setupRowContextMenu();
    }

    private void setupRowContextMenu() {
        StkInTableView.setRowFactory(_ -> {
            TableRow<RecordsModel> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem archiveItem = new MenuItem("Unarchive");
            archiveItem.setOnAction(_ -> {
                RecordsModel selectedRecord = row.getItem();
                if (selectedRecord != null) {
                    archiveRecord(selectedRecord);
                }
            });

            contextMenu.getItems().addAll(archiveItem);

            // Set context menu only for non-empty rows
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu));

            return row;
        });
    }

    private void archiveRecord(RecordsModel record) {
        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Unarchive");
        confirmation.setHeaderText("Unarchive this record?");
        confirmation.setContentText("Are you sure you want to unarchive this record?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Connection conn = DatabaseConnect.connect();
                String query = "UPDATE records SET archive_status = 1 WHERE record_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, record.getRecordId());

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    showAlert("Success", "Record unarchived successfully.");
                    refreshRecordsTable(); // Refresh the table to show changes
                } else {
                    showAlert("Error", "Failed to unarchive record.");
                }

                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "An error occurred while unarchiving the record.");
            }
        }
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
                        record_id,
                        CONCAT(COALESCE(r.f_name, ''), ' ', COALESCE(r.l_name, '')) AS patientName,
                        r.patient_id,
                        r.doctor_name,
                        r.diagnosis,
                        r.disposition,
                        r.status
                    FROM records r
                    LEFT JOIN patient p ON r.patient_id = p.patient_id
                    WHERE r.archive_status = 0
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                recordsModelObservableList.add(new RecordsModel(
                        rs.getInt("record_id"), // add this
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
    void BackBttnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Dashboard.fxml");
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void accountBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Account.fxml");
    }

    @FXML
    void closeAction(ActionEvent event) {
        Stage currentStage = (Stage) closeBtn.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    void crossBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_RequestMonitor.fxml");

    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Dashboard.fxml");

    }

    @FXML
    void minimizeAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setIconified(true);
    }

}
