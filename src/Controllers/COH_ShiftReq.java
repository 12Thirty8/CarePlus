package Controllers;

import java.io.IOException;
import java.util.Optional;

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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.SceneLoader;

public class COH_ShiftReq {

    @FXML
    private Button FilterBttn;

    @FXML
    private Button LogoutBtn;

    @FXML
    private TextField SearchButton;

    @FXML
    private TableView<?> ShiftRequestView;

    @FXML
    private Button accountBtn;

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
    private Button minimizeBtn;

    @FXML
    private Button recordsBtn;

    private Alert a = new Alert(AlertType.NONE);

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
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
