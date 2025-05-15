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
import Models.ProductsModel;
import db.DatabaseConnect;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;
import util.SceneLoader;

public class P_ProductsController implements Initializable {

    @FXML
    private Button addstockBtn;

    @FXML
    private Button movetoStocksBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private TableView<ProductsModel> ProductTable;

    @FXML
    private Button clipboardBtn;

    @FXML
    private Button crossBtn;

    @FXML
    private Button movetostockBtn;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button hamburgermenuBtn;

    @FXML
    private Button homeBtn;

    @FXML
    private TableColumn<ProductsModel, Integer> idcol;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TableColumn<ProductsModel, String> namecol;

    @FXML
    private TableColumn<ProductsModel, String> catcol;

    @FXML
    private TableColumn<ProductsModel, Text> desccol;

    @FXML
    private ComboBox<String> nametf;

    @FXML
    private TextField medidtf;

    @FXML
    private TextField cattf;

    @FXML
    private TextArea descta;

    @FXML
    private Button closeBtn;

    @FXML
    private Button minimizeBtn;

    @FXML
    private Text nameLabel;

    private ObservableList<ProductsModel> EmployeeList = FXCollections.observableArrayList();

    private Alert a = new Alert(AlertType.NONE);

    public static int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        setupTableColumns();
        refreshEmployeeTable();
        setupRowContextMenu();
        initializeRowSelectionListener();
        String pharmacistName = DatabaseConnect.getPharmacistName(employeeId);
        nameLabel.setText(pharmacistName != null ? pharmacistName : "Name not found");
    }

    @FXML
    private void initializeRowSelectionListener() {
        ProductTable.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null) {
                nametf.getSelectionModel().select(newSelection.getName());
                medidtf.setText(String.valueOf(newSelection.getId()));
                descta.setText(newSelection.getDesc().getText());
                cattf.setText(newSelection.getCategory());

            }
        });
    }

    private void setupTableColumns() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        catcol.setCellValueFactory(new PropertyValueFactory<>("category"));
        desccol.setCellValueFactory(new PropertyValueFactory<>("desc"));
    }

    private void setupRowContextMenu() {
        ProductTable.setRowFactory(_ -> {
            TableRow<ProductsModel> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete");

            deleteItem.setOnAction(_ -> {
                ProductsModel selectedItem = row.getItem();
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

    private void deleteRow(ProductsModel item) {
        // Show confirmation dialog before deleting
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete this account?");
        alert.setContentText("Are you sure you want to delete: " + item + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    archiveAccount(item.getId()); // Pass the employee ID
                    ProductTable.getItems().remove(item); // Remove from TableView
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
                        "DELETE FROM medicine WHERE med_id = ?")) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } // Resources will be auto-closed
    }

    private void refreshEmployeeTable() {
        EmployeeList.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT
                        m.med_id, m.med_name, m.med_cat, m.med_desc
                    FROM medicine m
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                EmployeeList.add(new ProductsModel(
                        rs.getInt("med_id"),
                        rs.getString("med_name"),
                        rs.getString("med_cat"),
                        new Text(rs.getString("med_desc"))));
            }

            ProductTable.setItems(EmployeeList);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void movetostockBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Stocks.fxml"));
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
        SceneLoader.loadScene(event, "/View/P_Dashboard.fxml");
    }

    @FXML
    void addstockBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_AddProduct.fxml");
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
    void movetoProductBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Products.fxml");
    }

    @FXML
    void movetoStocksBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Stocks.fxml");
    }

    @FXML
    private void closeAction(ActionEvent Action) {
        Stage currentStage = (Stage) closeBtn.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void minimizeAction(ActionEvent event) {
        // Get the current stage and minimize it
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setIconified(true);
    }

    @FXML
    void updateBtnPressed(ActionEvent event) {
        ProductsModel selectedItem = ProductTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String name = nametf.getValue();
            String category = cattf.getText();
            String description = descta.getText();

            // Get the current employee ID from the session
            int currentEmpId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

            String setEmployeeIdQuery = "SET @current_employee_id = ?"; // Set session variable
            String updateMedicineQuery = "UPDATE medicine SET med_name = ?, med_cat = ?, med_desc = ? WHERE med_id = ?";
            try (Connection conn = DatabaseConnect.connect()) {

                // 1. Set the session variable @current_employee_id
                try (PreparedStatement setStmt = conn.prepareStatement(setEmployeeIdQuery)) {
                    setStmt.setInt(1, currentEmpId);
                    setStmt.executeUpdate();
                }

                try (PreparedStatement pstmt = conn.prepareStatement(updateMedicineQuery)) {
                    pstmt.setString(1, name);
                    pstmt.setString(2, category);
                    pstmt.setString(3, description);
                    pstmt.setInt(4, selectedItem.getId());
                    pstmt.executeUpdate();

                    showAlert("Success", "Product updated successfully.");
                    refreshEmployeeTable();

                    pstmt.close();
                    conn.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update product: " + e.getMessage());
            }
        } else {
            showAlert("Error", "Please select a product to update.");
        }
    }
}
