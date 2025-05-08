package Controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;

import javax.swing.JOptionPane;

import db.DatabaseConnect;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

    @FXML
    public void onPressed(ActionEvent event) throws IOException {
        String username = empIDTf.getText().trim();
        String password = new String(psfield.getText());

        // Check if the username length is between 6 and 16 characters
        if (username.length() < 1 || username.length() > 6) {
            JOptionPane.showMessageDialog(null, "Incorrect Account ID Format", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if the password length is between 8 and 40 characters
        if (password.length() < 6 || password.length() > 20) {
            JOptionPane.showMessageDialog(null, "Password must be between 6 and 20 characters.", "Error",
                    JOptionPane.ERROR_MESSAGE);
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
                        JOptionPane.showMessageDialog(null, "Error loading dashboard", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });

                // Start the fade transition
                fadeOut.play();
            } else {
                JOptionPane.showMessageDialog(null, "ID or Password is incorrect.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                empIDTf.setText("");
                psfield.setText("");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error occurred", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
