package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChangeShift {

    @FXML
    private Button BackBttn;

    @FXML
    private TextField CurrentShifttf;

    @FXML
    private TextArea ReasonArea;

    @FXML
    private Button savebtn;

        private Alert a = new Alert(AlertType.NONE);

    @FXML
    void BackBttnAction(ActionEvent event) {
        try {
            // Get the current stage (popup window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Close the current popup window
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error closing the window.");
            a.setHeaderText("Error");
            a.show();
        }
    }

    @FXML
    void SaveChangesAction(ActionEvent event) {
        
    }

}
