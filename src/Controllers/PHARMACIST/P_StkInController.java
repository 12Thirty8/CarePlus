package Controllers.PHARMACIST;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import db.DatabaseConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.SceneLoader;
import javafx.scene.control.Alert.AlertType;

public class P_StkInController implements Initializable {

    @FXML
    private Button addstkBtn;

    @FXML
    private Button BackBttn;

    @FXML
    private TextField cattf;

    @FXML
    private DatePicker expdate;

    @FXML
    private TextField nametf;

    @FXML
    private TextField qtytf;

    @FXML
    private TextField stkinbytf;

    private Alert a = new Alert(AlertType.NONE);

    private Connection connection = DatabaseConnect.connect();

    private Stage stage;
    private Scene scene;
    private Parent root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stkinbytf.setText(Integer.toString(P_DashboardController.employeeId));
    }

    @FXML
    void BackBttnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Stocks.fxml");

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

    private void addStock(String name, String category, int quantity, String stkinby, Date expdate)
            throws SQLException {
        String sql = "INSERT INTO medicine (med_name, med_cat, med_stock, stockin_by, med_exp) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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
