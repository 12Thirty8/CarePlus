package Controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import db.DatabaseConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class P_AddProductController {

    @FXML
    private Button BackBttn;

    @FXML
    private Button addmedBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private TextArea descTextArea;

    @FXML
    private TextField medcattf;

    @FXML
    private TextField mednametf;

    private Alert a = new Alert(AlertType.NONE);

    private Runnable refreshCallback;

    @FXML
    void BackBttnAction(ActionEvent event) {
        try {
            // Get the current stage (popup window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Close the current popup window
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error closing the window.");
            a.setHeaderText("Error");
            a.show();
        }
    }

    @FXML
    void addmedBtnPressed(ActionEvent event) {
        if (mednametf.getText().isEmpty() || medcattf.getText().isEmpty() || descTextArea.getText().isEmpty()) {
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Please fill in all fields.");
            a.setHeaderText("Error");
            a.show();
        } else {
            try {
                addMedicine(mednametf.getText(), medcattf.getText(), descTextArea.getText());

                // Execute callback if it exists
                if (refreshCallback != null) {
                    refreshCallback.run();
                }

                // Close the window after successful addition
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();

            } catch (SQLException e) {
                a.setAlertType(AlertType.ERROR);
                a.setContentText("Error adding medicine." + e.getMessage());
                a.setHeaderText("Error");
                a.show();
            }
        }
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    @FXML
    void clearBtnPressed(ActionEvent event) {
        mednametf.clear();
        medcattf.clear();
        descTextArea.clear();
    }

    private void addMedicine(String name, String category, String description) throws SQLException {
        String query = "INSERT INTO medicine (med_name, med_cat, med_desc) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnect.connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setString(3, description);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                a.setAlertType(AlertType.INFORMATION);
                a.setContentText("Medicine added successfully.");
                a.setHeaderText("Success");
                a.show();
            } else {
                a.setAlertType(AlertType.ERROR);
                a.setContentText("Failed to add medicine.");
                a.setHeaderText("Error");
                a.show();
            }
        }
    }

}
