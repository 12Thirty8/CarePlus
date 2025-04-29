package Controllers;

import java.io.IOException;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class COHDashboardController {

    @FXML
    private Button AccountMenuBttn;

    @FXML
    private Button DashboardBttn;

    @FXML
    private Button HamburgerMenuBttn;

    @FXML
    private Button PharmacyBttn;

    @FXML
    private Button ScheduleBttn;

    @FXML
    private Button ScheduleMenuBttn;

    @FXML
    private TableView<?> StkInTableView;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    void AccountMenuActionBttn(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/COH_AccountManagement.fxml"));
            root = loader.load();

            root = FXMLLoader.load(getClass().getResource("/View/COH_AccountManagement.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading page.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    void DashboardActionBttn(ActionEvent event) {

    }

    @FXML
    void HamburgerMenuActionBttn(ActionEvent event) {

    }

    @FXML
    void PharmacyActionBttn(ActionEvent event) {

    }

    @FXML
    void ScheduleActionBttn(ActionEvent event) {

    }

    @FXML
    void ScheduleuActionBttn(ActionEvent event) {

    }

}
