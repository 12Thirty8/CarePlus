package Controllers.NURSE;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import Controllers.ViewState;
import Models.PatientModel;
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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;
import util.SceneLoader;

public class N_PatientDataController {

    @FXML
    private Button AddPatientDataBtn,
            LogoutBtn,
            clipboardBtn,
            closeBtn,
            crossBtn,
            hamburgermenuBtn,
            homeBtn,
            minimizeBtn,
            movetoProductBtn,
            movetoStocksBtn;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Text nameLabel;

    @FXML
    private TableColumn<PatientModel, Integer> patientIdColumn;

    @FXML
    private TableColumn<PatientModel, String> patientLastNameColumn;

    @FXML
    private TableColumn<PatientModel, String> patientFirstNameColumn;

    @FXML
    private TableView<PatientModel> patientDataTableView;

    private ObservableList<PatientModel> patientModelObservableList = FXCollections.observableArrayList();

    private Alert a = new Alert(AlertType.INFORMATION);

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        setupTableColumns();
        loadPatientData();
        setupRowContextMenu();
        int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();
        String nurseName = DatabaseConnect.getNurseName(employeeId);
        nameLabel.setText(nurseName + ", RN");

    }

    private void setupTableColumns() {
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("patient_id"));
        patientLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        patientFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("first_name"));
    }

    private void loadPatientData() {
        patientModelObservableList.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT
                        p.patient_id, p.l_name, p.f_name
                    FROM patient p
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                patientModelObservableList.add(new PatientModel(
                        rs.getInt("patient_id"),
                        rs.getString("f_name"),
                        rs.getString("l_name")));

            }
            patientDataTableView.setItems(patientModelObservableList);
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attaches a right-click context menu to each non-empty row in the patient
     * table.
     * The menu has one item: “View Patient Data”, which opens the N_ViewPatientData
     * popup.
     */
    private void setupRowContextMenu() {
        patientDataTableView.setRowFactory(_ -> {
            TableRow<PatientModel> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();

            MenuItem viewItem = new MenuItem("View Patient Data");
            viewItem.setOnAction(_ -> {
                PatientModel selected = row.getItem();
                if (selected != null) {
                    openViewPatientPopup(selected);
                }
            });

            MenuItem updateItem = new MenuItem("Update");
            updateItem.setOnAction(_ -> {
                PatientModel selectedRecord = row.getItem();
                if (selectedRecord != null) {
                    openUpdatePatientData(selectedRecord);
                }
            });

            // Add both items to the menu
            menu.getItems().addAll(viewItem, updateItem);

            // Only show on non-empty rows
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu));

            return row;
        });
    }

    private void openUpdatePatientData(PatientModel patient) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/N_UpdateData.fxml"));
            Parent root = loader.load();

            // Pass the selected patient to the update form controller
            N_UpdateData controller = loader.getController();
            controller.setPatientData(patient); // <--- Important line

            Stage stage = new Stage();
            stage.setTitle("Update Medical Record");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Wait for the update to finish

            // 🔁 Refresh the table after the window is closed
            loadPatientData();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open update window.").showAndWait();
        }
    }

    private void openViewPatientPopup(PatientModel patient) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/N_ViewPatientData.fxml"));
            Parent root = loader.load();

            // grab the controller and pass the selected patient to it
            N_ViewPatientData ctrl = loader.getController();
            ctrl.setPatient(patient);

            Stage popup = new Stage(StageStyle.UTILITY);
            popup.initModality(Modality.WINDOW_MODAL);
            popup.initOwner(patientDataTableView.getScene().getWindow());
            popup.setTitle("Patient Details");
            popup.setScene(new Scene(root));
            popup.setResizable(false);
            popup.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open patient view: " + e.getMessage());
        }
    }

    @FXML
    private void AddPatientDataBtnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/N_CreateNewRecord.fxml"));
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
    void accountBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Account.fxml");
    }

    @FXML
    private void closeAction(ActionEvent Action) {
        Stage currentStage = (Stage) closeBtn.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void crossBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_RequestMonitor.fxml");
    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Dashboard.fxml");
    }

    @FXML
    private void minimizeAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setIconified(true);
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
    void movetoProductBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Dashboard.fxml");
    }

    @FXML
    void movetoStocksBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_PatientData.fxml");
    }

}
