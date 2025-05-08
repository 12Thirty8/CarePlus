package Controllers;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;


public class P_ScheduleController {

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

    @FXML
    private boolean isHamburgerPaneExtended = false;


     @FXML
    public void initialize(URL url, ResourceBundle rb) {

        hamburgerPane.setPrefWidth(230);
        hamburgermenuBtn.setOnAction(_ -> toggleHamburgerMenu());
    }

    @FXML
    private void toggleHamburgerMenu() {
        Timeline timeline = new Timeline();

        if (isHamburgerPaneExtended) {
            KeyValue keyValue = new KeyValue(hamburgerPane.prefWidthProperty(), 230);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
            timeline.getKeyFrames().add(keyFrame);

        } else {
            KeyValue keyValue = new KeyValue(hamburgerPane.prefWidthProperty(), 107);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();
        isHamburgerPaneExtended = !isHamburgerPaneExtended;
    }

}
