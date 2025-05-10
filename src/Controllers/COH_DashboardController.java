package Controllers;

import java.io.IOException;

import db.DatabaseConnect;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;

public class COH_DashboardController {

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button minimizedButton, closeButton, AccountMenuBttn, DashboardBttn, hamburgermenuBtn,
            ScheduleBttn, ScheduleMenuBttn, LogOutBttn;

    @FXML
    private TableView<?> StkInTableView;

    @FXML
    private Label TitleText;

    @FXML
    private Text nameLabel;

    @FXML
    private Button closeBtn;

    @FXML
    private AreaChart<?, ?> AreaChartPanel;

    @FXML
    private AnchorPane NamePanel;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private AnchorPane TotalRequestPanel;

    private Alert a = new Alert(AlertType.NONE);

    @FXML
    public void initialize() {

        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        fadeInNode(TitleText, 0);
        fadeInNode(NamePanel, 200);
        fadeInNode(TotalRequestPanel, 200);
        fadeInNode(AreaChartPanel, 300);
        fadeInNode(StkInTableView, 400);

        int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();
        String COHName = DatabaseConnect.getCOHName(employeeId);
        nameLabel.setText(COHName != null ? COHName : "Name not found");

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
    void ScheduleActionBttn(ActionEvent event) {

    }

    @FXML
    private void fadeInNode(Node node, double delayMillis) {
        // node.setOpacity(0); // Start fully transparent
        FadeTransition fade = new FadeTransition(Duration.millis(800), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setCycleCount(1);
        fade.setDelay(Duration.millis(delayMillis)); // Delay before it starts
        fade.play();
    }

    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
    }

    @FXML
    private void crossBtnAction(ActionEvent event) {
         try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/COH_StockInReport.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading Account Management page.");
            a.setHeaderText("Error");
            a.show();
        }

    }

    @FXML
    private void homeBtnAction(ActionEvent event) {

         try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/COH_Dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading Account Management page.");
            a.setHeaderText("Error");
            a.show();
        }

    }

    @FXML
    private void recordsBtnAction(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/COH_ActivityReports.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading Account Management page.");
            a.setHeaderText("Error");
            a.show();
        }
    }
}
