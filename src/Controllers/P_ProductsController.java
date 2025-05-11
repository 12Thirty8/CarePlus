package Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;

public class P_ProductsController implements Initializable {

    @FXML
    private Button addstockBtn;

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
        String pharmacistName = DatabaseConnect.getPharmacistName(employeeId);
        nameLabel.setText(pharmacistName != null ? pharmacistName : "Name not found");
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

            MenuItem updateItem = new MenuItem("Update");
            MenuItem deleteItem = new MenuItem("Delete");

            updateItem.setOnAction(_ -> {
                ProductsModel selectedItem = row.getItem();
                if (selectedItem != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/P_UpdateProduct.fxml"));
                        Parent root = loader.load();

                        // Get the controller and pass the selected employee's data
                        P_UpdateProductController controller = loader.getController();
                        controller.loadEmployeeData(selectedItem.getId());

                        controller.setRefreshCallback(() -> refreshEmployeeTable());

                        // Create a new pop-up stage
                        Stage popupStage = new Stage();
                        popupStage.setTitle("Update Product");
                        popupStage.initModality(Modality.WINDOW_MODAL); // Makes it modal
                        popupStage.initOwner(row.getScene().getWindow()); // Set owner window

                        Scene scene = new Scene(root);
                        popupStage.setScene(scene);
                        popupStage.setResizable(false); // Optional: make it fixed size
                        popupStage.showAndWait(); // Wait until this window is closed (optional)
                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("Error", "Failed to open update form: " + e.getMessage());
                    }
                }
            });

            deleteItem.setOnAction(_ -> {
                ProductsModel selectedItem = row.getItem();
                if (selectedItem != null) {
                    deleteRow(selectedItem);
                }
            });

            contextMenu.getItems().addAll(updateItem, deleteItem);
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/P_AddProduct.fxml"));
            Parent root = loader.load();

            P_AddProductController controller = loader.getController();
            controller.setRefreshCallback(() -> {
                refreshEmployeeTable(); // Refresh the table
            });

            Stage popupStage = new Stage();
            popupStage.setTitle("Add New Product");
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(addstockBtn.getScene().getWindow());
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait(); // Wait until closed

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open form: " + e.getMessage());
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
        // Get the current stage and minimize it
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setIconified(true);
    }
}
