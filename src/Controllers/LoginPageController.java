package Controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

import db.DatabaseConnect;
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

public class LoginPageController {
    public static int userid;

    private DatabaseConnect dbConnect = new DatabaseConnect();

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

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void onPressed(ActionEvent event) throws IOException {
        String username = empIDTf.getText().trim();
        String password = new String(psfield.getText());

        // Check if the username length is between 6 and 16 characters
        if (username.length() < 6 || username.length() > 56) {
            JOptionPane.showMessageDialog(null, "Username must be between 6 and 16 characters.", "Error",
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
            Connection con = dbConnect.connect();
            Statement stm = con.createStatement();
            String sql = "SELECT employee_id, email, password_hash FROM employee where email='" + username
                    + "' and password_hash='"
                    + password + "'";
            ResultSet rs = stm.executeQuery(sql);

            if (rs.next()) {
                userid = rs.getInt("employee_id");

                root = FXMLLoader.load(getClass().getResource("/View/COH_Dashboard.fxml"));
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } else {
                JOptionPane.showMessageDialog(null, "Username or Password is incorrect.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                empIDTf.setText("");
                psfield.setText("");
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error occurred", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
