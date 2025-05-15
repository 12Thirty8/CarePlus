package Controllers.PHARMACIST;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import Controllers.ViewState;
import Models.StocksModel;
import db.DatabaseConnect;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;
import util.SceneLoader;

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
    private Button movetoStocksBtn;

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
        initializeRowSelectionListener();
        String pharmacistName = DatabaseConnect.getPharmacistName(employeeId);
        nameLabel.setText(pharmacistName != null ? pharmacistName : "Name not found");
        sintf.setText(Integer.toString(P_DashboardController.employeeId));

    }

    public TableView<StocksModel> getStockTable() {
        return StockTable;
    }

    @FXML
    private void initializeRowSelectionListener() {
        StockTable.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null) {
                nametf.getSelectionModel().select(newSelection.getName());
                qtytf.setText(String.valueOf(newSelection.getQuantity()));
                dosetf.setText(newSelection.getDose());
                expdate.setValue(newSelection.getExpDate().toLocalDate());
                batchidtf.setText(String.valueOf(newSelection.getId()));
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
        StockTable.setRowFactory(_ -> {
            TableRow<StocksModel> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete");

            deleteItem.setOnAction(_ -> {
                StocksModel selectedItem = row.getItem();
                if (selectedItem != null) {
                    deleteRow(selectedItem);
                }
            });

            contextMenu.getItems().addAll(deleteItem);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu));

            return row;
        });
    }

    private void deleteRow(StocksModel item) {
        // Show confirmation dialog before deleting
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete this account?");
        alert.setContentText("Are you sure you want to delete: " + item + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    archiveAccount(item.getId());
                    StockTable.getItems().remove(item);
                    showAlert("Success", "Product deleted successfully.");
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete item: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void archiveAccount(int id) throws SQLException {
        try (Connection conn = DatabaseConnect.connect();
                PreparedStatement pstmt = conn.prepareStatement(
                        "DELETE FROM batch WHERE batch_id = ?")) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public void refreshEmployeeTable() {
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
        SceneLoader.loadScene(event, "/View/P_Account.fxml");
    }

    @FXML
    private void PharmacyBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Stocks.fxml");
    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Dashboard.fxml");
    }

    @FXML
    void addstockBtnPressed(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/P_StockIn.fxml"));
            Parent root = loader.load();

            // Get the controller and pass the selected employee's data
            P_StkInController controller = loader.getController();

            controller.setRefreshCallback(() -> refreshEmployeeTable());

            // Create a new pop-up stage
            Stage popupStage = new Stage();
            popupStage.setTitle("Stock In");
            popupStage.initModality(Modality.WINDOW_MODAL); // Makes it modal
            Scene scene = new Scene(root);
            popupStage.setScene(scene);
            popupStage.setResizable(false); // Optional: make it fixed size
            popupStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            popupStage.showAndWait(); // Wait until this window is closed (optional)
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open update form: " + e.getMessage());
        }
    }

    @FXML
    void movetoProductBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Products.fxml");
    }

    @FXML
    void movetoStocksBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Stocks.fxml");
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
        // Check if a row is selected
        StocksModel selectedRow = StockTable.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            showAlert("Error", "Please select a row to update.");
            return;
        }

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
        // Set the session variable with the current employee ID
        int currentEmpId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

        String setEmployeeIdQuery = "SET @current_employee_id = ?"; // Set session variable
        String updateBatchQuery = "UPDATE batch SET med_id = ?, batch_stock = ?, batch_dosage = ?, batch_exp = ?, stockin_by = ?, stockin_date = ? WHERE batch_id = ?";

        try (Connection conn = DatabaseConnect.connect()) {
            // 1. Set the session variable @current_employee_id
            try (PreparedStatement setStmt = conn.prepareStatement(setEmployeeIdQuery)) {
                setStmt.setInt(1, currentEmpId);
                setStmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(updateBatchQuery)) {
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
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }
}
