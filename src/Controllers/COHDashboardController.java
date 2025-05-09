package Controllers;

import java.io.IOException;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class COHDashboardController {

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button minimizedButton, closeButton, AccountMenuBttn, DashboardBttn, hamburgermenuBtn, PharmacyBttn,
            ScheduleBttn, ScheduleMenuBttn, LogOutBttn;

    @FXML
    private TableView<?> StkInTableView;

    @FXML
    private Label TitleText;

    @FXML
    private AreaChart<?, ?> AreaChartPanel;

    @FXML
    private AnchorPane NamePanel;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private AnchorPane TotalRequestPanel;

    private boolean isHamburgerPaneExtended = false;

    private Alert a = new Alert(AlertType.NONE);

    @FXML
    public void initialize() {

        hamburgerPane.setPrefWidth(230);
        hamburgermenuBtn.setOnAction(_ -> toggleHamburgerMenu());
    }

    @FXML
    private void toggleHamburgerMenu() {
        System.out.println("Hamburger menu button clicked");
        try {
            Timeline timeline = new Timeline();

            if (isHamburgerPaneExtended) {
                KeyValue keyValue = new KeyValue(hamburgerPane.prefWidthProperty(), 230);
                KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
                timeline.getKeyFrames().add(keyFrame);

            } else {
                KeyValue keyValue = new KeyValue(hamburgerPane.prefWidthProperty(), 107);
                KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
                timeline.getKeyFrames().add(keyFrame);
            }

            timeline.play();
            isHamburgerPaneExtended = !isHamburgerPaneExtended;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void AccountMenuActionBttn(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/COH_AccountManagement.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.show();
        }
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
    void RecordsBtnPressed(ActionEvent event) {

    }

    @FXML
    void clipboardBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/COH_ManageShiftRequest.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.show();
        }
    }

    @FXML
    void crossBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/COH_StockInReport.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.show();
        }
    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/COH_Dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.show();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
