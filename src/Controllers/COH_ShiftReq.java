package Controllers;

import java.io.IOException;
import java.util.Optional;

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
    void AccountMenuActionBttn(ActionEvent event) {

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
    void clipboardBtnAction(ActionEvent event) {

    }

    @FXML
    void closeAction(ActionEvent event) {

    }

    @FXML
    void crossBtnAction(ActionEvent event) {

    }

    @FXML
    void homeBtnAction(ActionEvent event) {

    }

    @FXML
    void minimizeAction(ActionEvent event) {

    }

    @FXML
    void recordsBtnAction(ActionEvent event) {

    }

    @FXML
    void toggleHamburgerMenu(ActionEvent event) {

    }

}
