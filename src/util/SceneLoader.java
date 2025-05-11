package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

public class SceneLoader {

    public static void loadScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(SceneLoader.class.getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Load Error");
            alert.setContentText("Could not load: " + fxmlPath);
            alert.show();
            e.printStackTrace();
        }
    }
}
