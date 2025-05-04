package Controllers;

import java.io.IOException;
import javafx.scene.control.Label;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javafx.animation.FadeTransition;
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
    void LogOutActionBttn(ActionEvent event) {
        showAlert("Confirm Logout", "Are you sure you want to log out?");
        try {
            root = FXMLLoader.load(getClass().getResource("/View/COH_Login.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading login page.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
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
        fadeInNode(NamePanel, 200);
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
