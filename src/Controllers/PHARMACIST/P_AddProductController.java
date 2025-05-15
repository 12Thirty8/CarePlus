package Controllers.PHARMACIST;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import db.DatabaseConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.GetCurrentEmployeeID;
import util.SceneLoader;

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
        Stage currentStage = (Stage) BackBttn.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    void addmedBtnPressed(ActionEvent event) throws SQLException {
        if (mednametf.getText().isEmpty() || medcattf.getText().isEmpty() || descTextArea.getText().isEmpty()) {
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Please fill in all fields.");
            a.setHeaderText("Error");
            a.show();
        } else {
            addMedicine(mednametf.getText(), medcattf.getText(), descTextArea.getText());
            SceneLoader.loadScene(event, "/View/P_Products.fxml");
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
        // Get the current employee ID from the session
        int currentEmpId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

        String setEmployeeIdQuery = "SET @current_employee_id = ?"; // Set session variable
        String insertMedicineQuery = "INSERT INTO medicine (med_name, med_cat, med_desc) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnect.connect()) {
            // 1. Set the session variable @current_employee_id
            try (PreparedStatement setStmt = conn.prepareStatement(setEmployeeIdQuery)) {
                setStmt.setInt(1, currentEmpId);
                setStmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(insertMedicineQuery)) {
                pstmt.setString(1, name);
                pstmt.setString(2, category);
                pstmt.setString(3, description);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                    a.setAlertType(AlertType.INFORMATION);
                    a.setContentText("Product added successfully.");
                    a.setHeaderText("Success");
                    a.showAndWait();
                } else {
                    a.setAlertType(AlertType.ERROR);
                    a.setContentText("Failed to add product.");
                    a.setHeaderText("Error");
                    a.showAndWait();
                }
            }
        }

    }

}
