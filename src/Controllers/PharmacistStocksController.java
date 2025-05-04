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
    private Button clipboardBtnStocks;

    @FXML
    private Button crossBtnStocks;

    @FXML
    private AnchorPane hamburgerPaneStocks;

    @FXML
    private Button hamburgermenuBtnStocks;

    @FXML
    private Button homeBtnStocks;

    @FXML
    private AnchorPane mainPaneStocks;

    private boolean isHamburgerPaneExtended = false;

     @FXML
    public void initialize() {
        hamburgerPaneStocks.setPrefWidth(107); 
        hamburgermenuBtnStocks.setOnAction(event -> toggleHamburgerMenu());
    }

    @FXML
    private void toggleHamburgerMenu() {
        Timeline timeline = new Timeline();
    
        if (isHamburgerPaneExtended) {
            // Collapse the hamburger menu
            KeyValue keyValue = new KeyValue(hamburgerPaneStocks.prefWidthProperty(), 107); 
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
            timeline.getKeyFrames().add(keyFrame);
           
        } else {
            // Expand the hamburger menu
            KeyValue keyValue = new KeyValue(hamburgerPaneStocks.prefWidthProperty(), 300); 
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
            timeline.getKeyFrames().add(keyFrame);
        }
    
        timeline.play();
        isHamburgerPaneExtended = !isHamburgerPaneExtended;
    }
    

}
