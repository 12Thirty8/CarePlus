package Controllers.PHARMACIST;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import db.DatabaseConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.GetCurrentEmployeeID;

public class P_StkInController implements Initializable {

    @FXML
    private Button BackBttn;

    @FXML
    private Button addstkBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private TextField dosetf;

    @FXML
    private DatePicker expdate;

    @FXML
    private TextField medidtf;

    @FXML
    private ComboBox<String> nametf;

    @FXML
    private TextField qtytf;

    @FXML
    private TextField sintf;

    public static int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

    private ObservableList<String> allMedicineNames = FXCollections.observableArrayList();

    private FilteredList<String> filteredMedicineNames;

    private Runnable refreshCallback;

    private Alert a = new Alert(AlertType.NONE);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sintf.setText(String.valueOf(employeeId));
        loadMedicineNames();
        setupMedicineComboBox();
    }

    @FXML
    void BackBttnAction(MouseEvent event) {

    }

    @FXML
    void addstkBtnPressed(ActionEvent event) {
        String medId = medidtf.getText();
        String qtyText = qtytf.getText();
        String dose = dosetf.getText();
        String expDate = expdate.getValue() != null ? expdate.getValue().toString() : null;
        String stockinDate = java.time.LocalDate.now().toString();

        if (nametf.getValue() == null || medId == null || medId.isEmpty() || dose == null || dose.isEmpty()
                || qtyText == null || qtyText.isEmpty() || expDate == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        int quantity;
        int medIdInt;
        try {
            medIdInt = Integer.parseInt(medId);
            quantity = Integer.parseInt(qtyText);
            if (quantity <= 0) {
                showAlert("Error", "Quantity must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Quantity must be valid numbers.");
            return;
        }

        addStock(medIdInt, quantity, dose, expDate, employeeId, stockinDate, 7);
    }

    private void loadMedicineNames() {
        allMedicineNames.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = "SELECT med_name FROM medicine";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                allMedicineNames.add(rs.getString("med_name"));
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void LogOutActionBttn(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Are you sure you want to log out?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/LoginPage.fxml"));
                Parent root = loader.load();

                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(root));
                loginStage.initStyle(StageStyle.UNDECORATED);
                loginStage.setResizable(false);
                loginStage.show();

                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.close();

            } catch (IOException e) {
                e.printStackTrace();
                a.setAlertType(AlertType.ERROR);
                a.setContentText("Error loading page.");
                a.show();
            }
        }
    }

    private void setupMedicineComboBox() {
        if (allMedicineNames == null) {
            allMedicineNames = FXCollections.observableArrayList();
        }
        nametf.setItems(allMedicineNames);

        if (!allMedicineNames.isEmpty()) {
            filteredMedicineNames = new FilteredList<>(allMedicineNames, _ -> true);
            nametf.setItems(filteredMedicineNames);

            nametf.getEditor().textProperty().addListener((_, _, newValue) -> {
                filteredMedicineNames.setPredicate(item -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return item.toLowerCase().contains(lowerCaseFilter);
                });

                boolean exactMatch = false;
                if (newValue != null && !newValue.isEmpty()) {
                    try {
                        Connection conn = DatabaseConnect.connect();
                        String query = "SELECT COUNT(*) FROM medicine WHERE LOWER(med_name) = ?";
                        PreparedStatement pstmt = conn.prepareStatement(query);
                        pstmt.setString(1, newValue.toLowerCase());
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            exactMatch = true;
                        }
                        rs.close();
                        pstmt.close();
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (newValue != null && !newValue.isEmpty() && !filteredMedicineNames.isEmpty() && !exactMatch) {
                    if (!nametf.isShowing()) {
                        nametf.show();
                    }
                } else {
                    nametf.hide();
                }

                if (newValue == null || newValue.isEmpty()) {
                    nametf.setValue(null);
                }
            });
            nametf.valueProperty().addListener((_, _, newVal) -> {
                if (newVal != null && !newVal.isEmpty()) {
                    try {
                        Connection conn = DatabaseConnect.connect();
                        String query = "SELECT med_id FROM medicine WHERE med_name = ?";
                        PreparedStatement pstmt = conn.prepareStatement(query);
                        pstmt.setString(1, newVal);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            medidtf.setText(String.valueOf(rs.getInt("med_id")));
                        }

                        rs.close();
                        pstmt.close();
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addStock(int med_id, int quantity, String dose, String expdate, int stockinby, String stockindate,
            int status_id) {

        String query = "INSERT INTO batch (med_id, batch_stock, batch_dosage, batch_exp, stockin_by, stockin_date, status_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnect.connect();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, med_id);
            pstmt.setInt(2, quantity);
            pstmt.setString(3, dose);
            pstmt.setString(4, expdate);
            pstmt.setInt(5, stockinby);
            pstmt.setString(6, stockindate);
            pstmt.setInt(7, status_id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Stock added successfully");
                // Execute the refresh callback if it exists
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
                // Close the current window
                ((Node) addstkBtn).getScene().getWindow().hide();
            } else {
                showAlert("Error", "Failed to add stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    void clearBtnPressed(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        nametf.setValue(null);
        medidtf.clear();
        dosetf.clear();
        qtytf.clear();
        expdate.setValue(null);
    }

}
