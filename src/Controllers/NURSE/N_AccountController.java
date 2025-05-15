package Controllers.NURSE;

import java.io.IOException;
import java.util.Optional;

import Controllers.ViewState;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.SceneLoader;

public class N_AccountController {

    @FXML
    private Button ChangeShiftBtn;

    @FXML
    private Label numtf;

    @FXML
    private Label shifttf;

    @FXML
    private Label dayofftf;

    @FXML
    private Label emaddtf;

    @FXML
    private Label fnametf;

    @FXML
    private Label lnametf;

    @FXML
    private Button LogoutBtn;

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
    private AnchorPane mainPane;

    @FXML
    private Button minimizeBtn;

    @FXML
    private Text nameLabel;

    @FXML
    private Text TITLE1;

    @FXML
    private Text TITLE2;

    @FXML
    private Text lname;

    @FXML
    private Text fname;

    @FXML
    private Text number;

    @FXML
    private Text emadd;

    @FXML
    private Text shift;

    @FXML
    private Text dayoff;

    private Alert a = new Alert(AlertType.NONE);

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
    }

    @FXML
    void ChangeShiftBtnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ChangeShift.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setResizable(false);
            loginStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.show();
        }
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

    @FXML
    void LogOutActionBttn(ActionEvent event) {
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
    void clipboardBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Account.fxml");

    }

}
