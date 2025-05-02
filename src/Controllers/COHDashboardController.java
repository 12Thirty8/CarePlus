package Controllers;

import java.io.IOException;
import javafx.scene.control.Label;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.util.Duration;

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

    @FXML
    private Label TitleText;

    @FXML
    private AreaChart<?, ?> AreaChartPanel;

    @FXML
    private AnchorPane NamePanel;

    @FXML
    private AnchorPane TotalRequestPanel;
    
    @FXML
    private Button LogOutBttn;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    void AccountMenuActionBttn(ActionEvent event) {
        switchToSceneFullScreen("/View/COH_AccountManagement.fxml", event);
    }

    @FXML
    void DashboardActionBttn(ActionEvent event) {

    }

    @FXML
    void LogOutActionBttn(ActionEvent event) {

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

    @FXML
    public void initialize() {
    // Apply fade-in to all relevant nodes
    fadeInNode(TitleText, 0);
    wipeInFromLeft(NamePanel, 50);
    fadeInNode(TotalRequestPanel, 200);
    fadeInNode(AreaChartPanel, 300);
    fadeInNode(StkInTableView, 400);
}
    private void fadeInNode(Node node, double delayMillis) {
    node.setOpacity(0); // Start fully transparent
    FadeTransition fade = new FadeTransition(Duration.millis(800), node);
    fade.setFromValue(0.0);
    fade.setToValue(1.0);
    fade.setCycleCount(1);
    fade.setDelay(Duration.millis(delayMillis)); // Delay before it starts
    fade.play();
}
    
    private void switchToSceneFullScreen(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
    
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
    
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Error loading page:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void wipeInFromLeft(Node node, double delayMillis) {
    node.setOpacity(0); // start invisible

    // Run after the node has been laid out
    Platform.runLater(() -> {
        double width = node.getBoundsInParent().getWidth();
        double height = node.getBoundsInParent().getHeight();

        Rectangle clip = new Rectangle(0, height);
        node.setClip(clip);

        Timeline wipe = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(clip.widthProperty(), 0)),
            new KeyFrame(Duration.millis(800), new KeyValue(clip.widthProperty(), width))
        );
        wipe.setDelay(Duration.millis(delayMillis));
        wipe.play();

        // Optional: fade in too
        FadeTransition fade = new FadeTransition(Duration.millis(800), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delayMillis));
        fade.play();
    });
}


    }
