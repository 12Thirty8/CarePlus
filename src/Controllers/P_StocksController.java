package Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import Models.StocksModel;
import db.DatabaseConnect;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;

public class P_StocksController implements Initializable {

    @FXML
    private Button clearBtn;

    @FXML
    private DatePicker expdate;

    @FXML
    private TextField medidtf;

    @FXML
    private Text nameLabel;

    @FXML
    private ComboBox<String> nametf;

    private ObservableList<String> allMedicineNames = FXCollections.observableArrayList();

    private FilteredList<String> filteredMedicineNames;

    @FXML
    private TextField qtytf;

    @FXML
    private TableColumn<StocksModel, String> sinbycol;

    @FXML
    private TableColumn<StocksModel, String> sindatecol;

    @FXML
    private TableColumn<StocksModel, String> dosecol;

    @FXML
    private TextField sintf;

    @FXML
    private TableColumn<StocksModel, String> statcol;

    @FXML
    private Button updatebtn;

    @FXML
    private Button LogoutBtn;

    @FXML
    private Button addstockBtn;

    @FXML
    private TextField batchidtf;

    @FXML
    private TextField dosetf;

    @FXML
    private Button movetoProductBtn;

    @FXML
    private TableView<StocksModel> StockTable;

    @FXML
    private Button clipboardBtn;

    @FXML
    private Button crossBtn;

    @FXML
    private TableColumn<StocksModel, String> expcol;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button hamburgermenuBtn;

    @FXML
    private Button homeBtn;

    @FXML
    private TableColumn<StocksModel, Integer> idcol;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TableColumn<StocksModel, String> namecol;

    @FXML
    private TableColumn<StocksModel, Integer> qtycol;

    @FXML
    private Button closeBtn;

    @FXML
    private Button minimizeBtn;

    private ObservableList<StocksModel> EmployeeList = FXCollections.observableArrayList();

    private Alert a = new Alert(AlertType.NONE);

    public static int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        setupTableColumns();
        refreshEmployeeTable();
        setupRowContextMenu();
        loadMedicineNames();
        setupMedicineComboBox();
        String pharmacistName = DatabaseConnect.getPharmacistName(employeeId);
        nameLabel.setText(pharmacistName != null ? pharmacistName : "Name not found");
        sintf.setText(Integer.toString(P_DashboardController.employeeId));

        batchidtf.setOnKeyReleased(_ -> {
            String batchIdText = batchidtf.getText();
            Connection conn = DatabaseConnect.connect();
            String sql = "SELECT b.med_id, m.med_name as medName, b.batch_stock, b.batch_dosage, b.batch_exp FROM batch b LEFT JOIN medicine m ON b.med_id = m.med_id WHERE batch_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, batchIdText);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int medId = rs.getInt("med_id");
                    int stock = rs.getInt("batch_stock");
                    String dosage = rs.getString("batch_dosage");
                    java.sql.Date expDate = rs.getDate("batch_exp");

                    // Set the values in the text fields
                    nametf.setValue(rs.getString("medName"));
                    medidtf.setText(String.valueOf(medId));
                    qtytf.setText(String.valueOf(stock));
                    expdate.setValue(expDate.toLocalDate());
                    dosetf.setText(dosage);

                } else {
                    nametf.setValue(null);
                    medidtf.clear();
                    qtytf.clear();
                    expdate.setValue(null);
                    dosetf.clear();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
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

    private void setupTableColumns() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        qtycol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        dosecol.setCellValueFactory(new PropertyValueFactory<>("dose"));
        expcol.setCellValueFactory(new PropertyValueFactory<>("expDate"));
        sinbycol.setCellValueFactory(new PropertyValueFactory<>("sinby"));
        sindatecol.setCellValueFactory(new PropertyValueFactory<>("sinDate"));
        statcol.setCellValueFactory(new PropertyValueFactory<>("status"));

        StockTable.setItems(EmployeeList);
    }

    private void setupRowContextMenu() {
    }

    private void refreshEmployeeTable() {
        EmployeeList.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT
                        b.batch_id,
                        m.med_name AS medName,
                        b.batch_stock,
                        b.batch_dosage,
                        b.batch_exp,
                        CONCAT(COALESCE(e.f_name, ''), ' ', COALESCE(e.l_name, '')) AS stockinBy,
                        b.stockin_date,
                        s.status_name AS status
                    FROM batch b
                    LEFT JOIN employee e ON b.stockin_by = e.employee_id
                    LEFT JOIN medicine m ON b.med_id = m.med_id
                    LEFT JOIN stockstatus s ON b.status_id = s.status_id
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                EmployeeList.add(new StocksModel(
                        rs.getInt("batch_id"),
                        rs.getString("medName"),
                        rs.getInt("batch_stock"),
                        rs.getString("batch_dosage"),
                        rs.getDate("batch_exp"),
                        rs.getString("stockinBy"),
                        rs.getDate("stockin_date"),
                        rs.getString("status")));
            }

            StockTable.setItems(EmployeeList);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleHamburgerMenu() {
        Timeline timeline = new Timeline();
        double targetWidth = ViewState.isHamburgerPaneExtended ? 107 : 230;

        KeyValue keyValue = new KeyValue(hamburgerPane.prefWidthProperty(), targetWidth);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

        ViewState.isHamburgerPaneExtended = !ViewState.isHamburgerPaneExtended;
    }

    @FXML
    void clipboardBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Schedule.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.setHeaderText("Error");
            a.show();
        }
    }

    @FXML
    private void PharmacyBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Stocks.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading Pharmacy page.");
            a.setHeaderText("Error");
            a.show();
        }
    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.setHeaderText("Error");
            a.show();
        }
    }

    @FXML
    void addstockBtnPressed(ActionEvent event) {

        String medId = medidtf.getText();
        String quantity = qtytf.getText();
        String dose = dosetf.getText();
        String expDate = expdate.getValue() != null ? expdate.getValue().toString() : null;
        String stockinDate = java.time.LocalDate.now().toString();

        if (medId.isEmpty() || quantity.isEmpty() || expDate == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        try {
            Connection conn = DatabaseConnect.connect();
            String sql = "INSERT INTO batch ( med_id, batch_stock, batch_dosage, batch_exp, stockin_by, stockin_date, status_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, medId);
            pstmt.setString(2, quantity);
            pstmt.setString(3, dose);
            pstmt.setString(4, expDate);
            pstmt.setString(6, stockinDate);
            pstmt.setInt(5, employeeId);
            pstmt.setInt(7, 7); // 7 is the ID for "Available" status

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Stock added successfully.");
                refreshEmployeeTable();
                clearBtnPressed(event);
            } else {
                showAlert("Error", "Failed to add stock.");
            }

            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    void movetoProductBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Products.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.setHeaderText("Error");
            a.show();
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

    @FXML
    private void closeAction(ActionEvent Action) {
        Stage currentStage = (Stage) closeBtn.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void minimizeAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setIconified(true);
    }

    @FXML
    void clearBtnPressed(ActionEvent event) {
        batchidtf.clear();
        nametf.setValue(null);
        medidtf.clear();
        qtytf.clear();
        expdate.setValue(null);
        dosetf.clear();
    }

    @FXML
    private void updatebtnPressed(ActionEvent event) {
        String batchId = batchidtf.getText();
        String medId = medidtf.getText();
        String quantity = qtytf.getText();
        String dose = dosetf.getText();
        String expDate = expdate.getValue() != null ? expdate.getValue().toString() : null;
        String stockinDate = java.time.LocalDate.now().toString();

        if (batchId.isEmpty() || medId.isEmpty() || quantity.isEmpty() || expDate == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        try {
            Connection conn = DatabaseConnect.connect();
            String sql = "UPDATE batch SET med_id = ?, batch_stock = ?, batch_dosage = ?, batch_exp = ?, stockin_by = ?, stockin_date = ? WHERE batch_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, medId);
            pstmt.setString(2, quantity);
            pstmt.setString(3, dose);
            pstmt.setString(4, expDate);
            pstmt.setInt(5, employeeId);
            pstmt.setString(6, stockinDate);
            pstmt.setString(7, batchId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Stock updated successfully.");
                refreshEmployeeTable();
                clearBtnPressed(event);
            } else {
                showAlert("Error", "Failed to update stock.");
            }

            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }
}
