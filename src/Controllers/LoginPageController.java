package Controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;
import db.DatabaseConnect;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
//import javafx.stage.StageStyle;

public class LoginPageController {
    public static int dep_id;

    @FXML
    private Label careLabel;

    @FXML
    private Label empIDLabel;

    @FXML
    private TextField empIDTf;

    @FXML
    private Button loginBtn;

    @FXML
    private Pane loginPane;

    @FXML
    private Label passwordLabel;

    @FXML
    private PasswordField psfield;

    private Alert a = new Alert(AlertType.NONE);

    @FXML
    public void initialize() {
        // Set Enter key handlers for both fields
        empIDTf.setOnKeyPressed(this::handleKeyPress);
        psfield.setOnKeyPressed(this::handleKeyPress);
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                onPressed(new ActionEvent(loginBtn, null));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void onPressed(ActionEvent event) throws IOException {
        String username = empIDTf.getText().trim();
        String password = new String(psfield.getText());

        // Check if the username length is between 6 and 16 characters
        if (username.length() < 1 || username.length() > 6) {
            a.setAlertType(AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Login Error");
            a.setContentText("Username must be between 1 and 6 characters.");
            a.show();
            return;
        }

        if (!username.matches("\\d+")) {
            a.setAlertType(AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Login Error");
            a.setContentText("Invalid username format.");
            a.show();
            return;
        }

        // Check if the password length is between 8 and 40 characters
        if (password.length() < 6 || password.length() > 20) {
            a.setAlertType(AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Login Error");
            a.setContentText("Password must be between 6 and 20 characters.");
            a.show();
            return;
        }

        try {
            Connection con = DatabaseConnect.connect();
            Statement stm = con.createStatement();
            String sql = "SELECT employee_id, dep_id, password_hash FROM employee where employee_id='" + username
                    + "' and password_hash='"
                    + password + "'";
            ResultSet rs = stm.executeQuery(sql);

            if (rs.next()) {
                // Added by JC. Used to get the current user's employee_id.
                int loggedId = rs.getInt("employee_id");
                System.out.println("Successfully logged in with ID: " + loggedId);
                GetCurrentEmployeeID.getInstance().setEmployeeId(loggedId);
                //
                dep_id = rs.getInt("dep_id");

                // Load the loading page scene first
                Parent loadingPageRoot = FXMLLoader.load(getClass().getResource("/View/LoadingPage.fxml"));
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene loadingScene = new Scene(loadingPageRoot);
                currentStage.setScene(loadingScene);
                currentStage.setMaximized(true);
                currentStage.show();

                // Create a fade transition for the loading page
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(5), loadingPageRoot);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(1.0);

                // Set the action after the fade-out transition completes
                fadeOut.setOnFinished(_ -> {
                    try {
                        // Load the appropriate dashboard scene based on the department
                        Parent root = null;
                        switch (dep_id) {
                            case 1:
                                root = FXMLLoader.load(getClass().getResource("/View/N_Dashboard.fxml"));
                                break;
                            case 2:
                                root = FXMLLoader.load(getClass().getResource("/View/P_Dashboard.fxml"));
                                break;
                            case 3:
                                root = FXMLLoader.load(getClass().getResource("/View/COH_Dashboard.fxml"));
                                break;
                        }

                        currentStage.setMaximized(true);
                        currentStage.getScene().setRoot(root);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        a.setAlertType(AlertType.ERROR);
                        a.setTitle("Error");
                        a.setHeaderText("Loading Error");
                        a.setContentText("Failed to load the dashboard page.");
                        a.show();
                    }
                });

                // Start the fade transition
                fadeOut.play();
            } else {
                a.setAlertType(AlertType.ERROR);
                a.setTitle("Error");
                a.setHeaderText("Login Error");
                a.setContentText("Invalid username or password.");
                a.show();
                empIDTf.setText("");
                psfield.setText("");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Database Error");
            a.setContentText("Database connection error.");
            a.show();
        }
    }

}
