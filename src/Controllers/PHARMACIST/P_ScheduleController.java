package Controllers.PHARMACIST;

import java.io.IOException;
import java.util.Optional;

import Controllers.ViewState;
import db.DatabaseConnect;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;

public class P_ScheduleController {

    @FXML
    private Button clipboardBtn;

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
    private VBox vbox;

    @FXML
    private Button closeBtn;

    @FXML
    private boolean isHamburgerPaneExtended = false;

    @FXML
    private Text nameLabel;

    private Alert a = new Alert(AlertType.NONE);

    public static int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);

        String pharmacistName = DatabaseConnect.getPharmacistName(employeeId);
        nameLabel.setText(pharmacistName != null ? pharmacistName : "Name not found");
    };

    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Schedule.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.setHeaderText("Error");
            a.show();
        }
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
    private void PharmacyBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Stocks.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading Pharmacy page.");
            a.setHeaderText("Error");
            a.show();
        }
    }

    @FXML
    void crossBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Stocks.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.setHeaderText("Error");
            a.show();
        }
    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.setHeaderText("Error");
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
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setIconified(true);
    }

}
