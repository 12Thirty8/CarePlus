package Controllers;

import java.io.IOException;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class COHDashboardController {

    @FXML
    private Button AccountMenuBttn;

    @FXML
    private Button StkInMenuBttn;

    @FXML
    void AccountMenuActionBttn(ActionEvent event) {
        // Get the current stage (window) from the button's scene
        Stage currentStage = (Stage) AccountMenuBttn.getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("COH_AccountManagement.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Account Management");

            // Close the current stage after the new one is ready
            currentStage.close();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading page.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    void StockInMenuActionBttn(ActionEvent event) {

    }

}
