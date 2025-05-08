package Controllers;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
    private VBox vbox;

    @FXML
    private boolean isHamburgerPaneExtended = false;


     @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(230);
        hamburgermenuBtn.setOnAction(_ -> toggleHamburgerMenu());
        LeaveRequest[] requests = {
        new LeaveRequest("1 Jun", "Personal Matters", "Approved"),
        new LeaveRequest("2-4 Jun", "Lmao", "Approved")
    };

    for (LeaveRequest req : requests) {
        vbox.getChildren().add(createLeaveCard(req));
    }
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

    public static class LeaveRequest {
    private final String date;
    private final String reason;
    private final String status;

    public LeaveRequest(String date, String reason, String status) {
        this.date = date;
        this.reason = reason;
        this.status = status;
    }
    public String getDate() { return date; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
}

private AnchorPane createLeaveCard(LeaveRequest req) {
    AnchorPane card = new AnchorPane();
    card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2); -fx-padding: 16;");

    // Date label
    javafx.scene.control.Label dateLabel = new javafx.scene.control.Label(req.getDate());
    dateLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");
    dateLabel.setLayoutX(16);
    dateLabel.setLayoutY(10);

    // Reason label
    javafx.scene.control.Label reasonLabel = new javafx.scene.control.Label(req.getReason());
    reasonLabel.setStyle("-fx-font-size: 14;");
    reasonLabel.setLayoutX(90);
    reasonLabel.setLayoutY(18);

    // Status label
    javafx.scene.control.Label statusLabel = new javafx.scene.control.Label(req.getStatus());
    statusLabel.setStyle("-fx-background-color: #00C853; -fx-text-fill: white; -fx-padding: 4 16; -fx-background-radius: 16; -fx-font-weight: bold;");
    statusLabel.setLayoutX(250);
    statusLabel.setLayoutY(16);

    card.setPrefHeight(54);
    card.setMinWidth(320);

    card.getChildren().addAll(dateLabel, reasonLabel, statusLabel);
    return card;
}

}
