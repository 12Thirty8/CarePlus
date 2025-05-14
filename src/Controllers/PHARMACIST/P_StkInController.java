package Controllers.PHARMACIST;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import Controllers.ViewState;
import Controllers.COH.UpdateAccountController;
import Models.StocksModel;
import db.DatabaseConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.GetCurrentEmployeeID;
import util.SceneLoader;
import javafx.scene.control.Alert.AlertType;

public class P_StkInController {

    @FXML
    private ImageView BackBttn;


    @FXML
    private Button addstkBtn;

    @FXML
    private TextField batchidtf;

    @FXML
    private TextField dosetf;

    @FXML
    private DatePicker expdate;

    @FXML
    private TextField medidtf;

    @FXML
    private ComboBox<?> nametf;

    @FXML
    private TextField qtytf;

    @FXML
    private TextField sintf;

    public static int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();
        
    private Alert a = new Alert(AlertType.NONE);



    @FXML
    void BackBttnAction(MouseEvent event) {

    }

    private void refreshEmployeeTable() {
        

    }

    @FXML
    void addstkBtnPressed(ActionEvent event) throws NumberFormatException, SQLException {
        if (nametf.getText().isEmpty() || cattf.getText().isEmpty() || qtytf.getText().isEmpty()
                || stkinbytf.getText().isEmpty()) {
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Please fill all the fields");
            a.show();
            return;
        }

        try {
            LocalDate localDate = expdate.getValue();
            Date sqlDate = Date.valueOf(localDate);

            addStock(nametf.getText(), cattf.getText(), Integer.parseInt(qtytf.getText()), stkinbytf.getText(),
                    sqlDate);
                    
            clearFields();
        } catch (NullPointerException e) {
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Please select an expiry date");
            a.show();
            return;
        }
    }

    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addStock(String name, String category, int quantity, String stkinby, Date expdate)
            throws SQLException {

        // Get the current employee ID from the session
        int currentEmpId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();
        System.out.println("[DEBUG] Current Employee ID: " + currentEmpId); // Add this line

        String setEmployeeIdQuery = "SET @current_employee_id = ?"; // Set session variable
        String insertMedicineQuery = "INSERT INTO medicine (med_name, med_cat, med_stock, stockin_by, med_exp) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnect.connect()) {
            // 1. Set the session variable @current_employee_id
            try (PreparedStatement setStmt = conn.prepareStatement(setEmployeeIdQuery)) {
                setStmt.setInt(1, currentEmpId);
                setStmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(insertMedicineQuery)) {
                pstmt.setString(1, name);
                pstmt.setString(2, category);
                pstmt.setInt(3, quantity);
                pstmt.setString(4, stkinby);
                pstmt.setDate(5, expdate);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    a.setAlertType(AlertType.INFORMATION);
                    a.setContentText("Stock added successfully");
                    a.show();
                    clearFields();
                } else {
                    a.setAlertType(AlertType.ERROR);
                    a.setContentText("Failed to add stock");
                    a.show();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Database error: " + e.getMessage());
            a.show();
        }

    }

    private void clearFields() {
        nametf.clear();
        cattf.clear();
        qtytf.clear();
        expdate.setValue(null);
    }


    }

