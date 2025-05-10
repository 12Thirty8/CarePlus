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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class COH_Archive {

    @FXML
    private TableView<?> AccountManagmentTableView;

    @FXML
    private Button FilterBttn, LogoutBtn, StkOutBttn, accountBtn, clipboardBtn, closeBtn, crossBtn,
            hamburgermenuBtn, homeBtn, minimizeBtn, recordsBtn;

    @FXML
    private TextField TFsearch;

    @FXML
    private TableColumn<?, ?> depcol, dobcol, emailcol, emp_idcol, f_namecol, l_namecol, numbercol, offcol, shiftcol;

    @FXML
    private AnchorPane hamburgerPane;

    private Alert a = new Alert(AlertType.NONE);

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
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
    void homeBtnAction(ActionEvent event) {

    }

    @FXML
    void toggleHamburgerMenu(ActionEvent event) {

    }

}
