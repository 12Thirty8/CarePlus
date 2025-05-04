package Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class PharmacistDashboardController {


    @FXML
    private Button clipboardBtn;

    @FXML
    private Button clipboardhiglightBtn;

    @FXML
    private Button crossBtn;

    @FXML
    private Button crosshighlightBtn;

    @FXML
    private Button homehighlightBtn;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button hamburgermenuBtn;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Button testButton;

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
            KeyValue keyValue = new KeyValue(hamburgerPane.prefWidthProperty(), 300); 
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
            timeline.getKeyFrames().add(keyFrame);
        }
    
        timeline.play();
        isHamburgerPaneExtended = !isHamburgerPaneExtended;
    }
    

}



