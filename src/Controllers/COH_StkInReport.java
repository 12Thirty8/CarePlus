package Controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class COH_StkInReport {

    @FXML
    private Button FilterBttn;

    @FXML
    private Button LogoutBtn;

    @FXML
    private TextField SearchButton;

    @FXML
    private TableView<?> StkInTableView;

    @FXML
    private Button StkOutBttn;

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

    }

    @FXML
    void closeAction(ActionEvent event) {

    }

    @FXML
    void minimizeAction(ActionEvent event) {

    }

    @FXML
    void toggleHamburgerMenu(ActionEvent event) {

    }

}
