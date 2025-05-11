package Controllers.COH;

import java.io.IOException;
import java.util.Optional;

import Controllers.ViewState;
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

public class COH_LeaveReq {

    @FXML
    private TableView<?> AccountManagmentTableView;

    @FXML
    private Button FilterBttn, LogoutBtn, StkOutBttn, accountBtn, clipboardBtn, closeBtn, crossBtn,
            hamburgermenuBtn, homeBtn, minimizeBtn, recordsBtn;

    @FXML
    private TextField SearchButton;

    @FXML
    private AnchorPane hamburgerPane;

    private Alert a = new Alert(AlertType.NONE);

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
    }

    @FXML
    void homeBtnAction(ActionEvent event) {
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
    void minimizeAction(ActionEvent event) {

    }

    @FXML
    void recordsBtnAction(ActionEvent event) {

    }

    @FXML
    void toggleHamburgerMenu(ActionEvent event) {

    }

}
