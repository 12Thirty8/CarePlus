package Controllers.COH;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import Controllers.ViewState;
import Models.ChangeLog;
import db.DatabaseConnect;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.SceneLoader;

public class COH_ActivityReport {

    @FXML
    private Button FilterBttn, hamburgermenuBtn, minimizedBtn, closeBtn, accountBtn, homeBtn,
            crossBtn, recordsBtn, clipboardBtn, LogOutBtn;
    @FXML
    private TextField SearchButton;

    @FXML
    private Label nameLabel;

    @FXML
    private TableView<ChangeLog> StkInTableView;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML private TableColumn<ChangeLog, Integer> idColumn;
    @FXML private TableColumn<ChangeLog, String> tableNameColumn;
    @FXML private TableColumn<ChangeLog, String> actionColumn;
    @FXML private TableColumn<ChangeLog, String> oldDataColumn;
    @FXML private TableColumn<ChangeLog, String> newDataColumn;
    @FXML private TableColumn<ChangeLog, String> changedByColumn;
    @FXML private TableColumn<ChangeLog, LocalDateTime> changedAtColumn;

    private Alert a = new Alert(AlertType.NONE);

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        String cohName = DatabaseConnect.getCOHName();
        nameLabel.setText(cohName != null ? cohName : "Name not found");

        // Table column bindings
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableNameColumn.setCellValueFactory(new PropertyValueFactory<>("tableName"));
        actionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        oldDataColumn.setCellValueFactory(new PropertyValueFactory<>("oldData"));
        newDataColumn.setCellValueFactory(new PropertyValueFactory<>("newData"));
        changedByColumn.setCellValueFactory(new PropertyValueFactory<>("changedBy"));
        changedAtColumn.setCellValueFactory(new PropertyValueFactory<>("changedAt"));

        loadChangeLogs(); // call to load logs
    }

    private void loadChangeLogs() {
    ObservableList<ChangeLog> logs = FXCollections.observableArrayList();

    try (Connection conn = DatabaseConnect.connect();
         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM change_log ORDER BY changed_at DESC");
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            logs.add(new ChangeLog(
                rs.getInt("id"),
                rs.getString("table_name"),
                rs.getString("action"),
                rs.getString("old_data"),
                rs.getString("new_data"),
                rs.getString("changed_by"),
                rs.getTimestamp("changed_at").toLocalDateTime()
            ));
        }

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Database Error", "Failed to load change logs.");
    }

    StkInTableView.setItems(logs);
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void LogOutActionBttn(ActionEvent event) {
        showAlert("Confirm Logout", "Are you sure you want to log out?");
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/LoginPage.fxml"));
            Parent root = loader.load();

            // Create a new stage for the login page
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setResizable(false); // Optional: prevent resizing
            loginStage.show();

            // Close the current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.show();
        }
    }

    @FXML
    private void closeAction(ActionEvent Action) {
        Stage currentStage = (Stage) closeBtn.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void minimizeAction(ActionEvent event) {
        // Get the current stage and minimize it
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setIconified(true);
    }

    @FXML
    void clipboardBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/COH_ManageShiftRequest.fxml");
    }

    @FXML
    private void crossBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/COH_StockInReport.fxml");

    }

    @FXML
    private void homeBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/COH_Dashboard.fxml");
    }

    @FXML
    private void recordsBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/COH_ActivityReports.fxml");
    }

    @FXML
    void AccountMenuActionBttn(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/COH_AccountManagement.fxml");
    }
}
