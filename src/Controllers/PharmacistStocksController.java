package Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class PharmacistStocksController {

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

    private boolean isHamburgerPaneExtended = false;

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(107); 
        hamburgermenuBtn.setOnAction(event -> toggleHamburgerMenu());
    }

    @FXML
    private void toggleHamburgerMenu() {
        Timeline timeline = new Timeline();

        if (isHamburgerPaneExtended) {
            // Collapse the hamburger menu
            KeyValue keyValue = new KeyValue(hamburgerPane.prefWidthProperty(), 107); 
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
            timeline.getKeyFrames().add(keyFrame);
        } else {
            // Expand the hamburger menu
            KeyValue keyValue = new KeyValue(hamburgerPane.prefWidthProperty(), 250); 
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();
        isHamburgerPaneExtended = !isHamburgerPaneExtended;
    }
}