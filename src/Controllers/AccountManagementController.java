package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
//import javafx.stage.Screen;
import javafx.stage.Stage;
//import javafx.stage.StageStyle;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Models.EmployeeModel;
import db.DatabaseConnect;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

public class AccountManagementController implements Initializable {

    @FXML
    private TableView<EmployeeModel> AccountManagmentTableView;

    @FXML
    private Button AccountMenuBttn;

    @FXML
    private Button AddAccountBttn;

    @FXML
    private Button DashboardBttn;

    @FXML
    private Button FilterBttn;

    @FXML
    private Button HamburgerMenuBttn;

    @FXML
    private Button PharmacyBttn;

    @FXML
    private Button ScheduleBttn;

    @FXML
    private Button ScheduleMenuBttn;

    @FXML
    private TextField TFsearch;

    @FXML
    private TableColumn<EmployeeModel, String> depcol;

    @FXML
    private TableColumn<EmployeeModel, String> dobcol;

    @FXML
    private TableColumn<EmployeeModel, String> emailcol;

    @FXML
    private TableColumn<EmployeeModel, Integer> emp_idcol;

    @FXML
    private TableColumn<EmployeeModel, String> f_namecol;

    @FXML
    private TableColumn<EmployeeModel, String> l_namecol;

    @FXML
    private TableColumn<EmployeeModel, String> numbercol;

    @FXML
    private TableColumn<EmployeeModel, String> offcol;

    @FXML
    private TableColumn<EmployeeModel, String> shiftcol;

    @FXML
    private Label nameLabel;

    private ObservableList<EmployeeModel> EmployeeList = FXCollections.observableArrayList();

    private Stage stage;
    private Scene scene;
    private Parent root;

    private Alert a = new Alert(AlertType.NONE);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        refreshEmployeeTable();
        setupRowContextMenu();

        // Added by JC. Used to get the name of the COH
        String cohName = DatabaseConnect.getCOHName();
        nameLabel.setText(cohName != null ? cohName : "Name not found");
    }

    private void setupTableColumns() {
        emp_idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        f_namecol.setCellValueFactory(new PropertyValueFactory<>("fname"));
        l_namecol.setCellValueFactory(new PropertyValueFactory<>("lname"));
        dobcol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        numbercol.setCellValueFactory(new PropertyValueFactory<>("number"));
        emailcol.setCellValueFactory(new PropertyValueFactory<>("email"));
        depcol.setCellValueFactory(new PropertyValueFactory<>("depName"));
        shiftcol.setCellValueFactory(new PropertyValueFactory<>("shiftName"));
        offcol.setCellValueFactory(new PropertyValueFactory<>("dayoffName"));
    }

    private void setupRowContextMenu() {
        AccountManagmentTableView.setRowFactory(_ -> {
            TableRow<EmployeeModel> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem updateItem = new MenuItem("Update");
            MenuItem deleteItem = new MenuItem("Delete");

            updateItem.setOnAction(_ -> {
                EmployeeModel selectedItem = row.getItem();
                if (selectedItem != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/COH_UpdateAccount.fxml"));
                        Parent root = loader.load();

                        // Get the controller and pass the selected employee's data
                        UpdateAccountController controller = loader.getController();
                        controller.loadEmployeeData(selectedItem.getId());

                        // Create a new pop-up stage
                        Stage popupStage = new Stage();
                        popupStage.setTitle("Update Account");
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
                EmployeeModel selectedItem = row.getItem();
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

    private void deleteRow(EmployeeModel item) {
        // Show confirmation dialog before deleting
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete this record?");
        alert.setContentText("Are you sure you want to delete: " + item + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    deleteAccount(item.getId()); // Pass the employee ID
                    AccountManagmentTableView.getItems().remove(item); // Remove from TableView
                    showAlert("Success", "Employee deleted successfully");
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete employee: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void deleteAccount(int id) throws SQLException {
        try (Connection conn = DatabaseConnect.connect();
                PreparedStatement pstmt = conn.prepareStatement(
                        "DELETE FROM employee WHERE employee_id = ?")) {

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
                        e.employee_id, e.f_name, e.l_name, e.dob, e.contact_no, e.email,
                        d.dep_name AS depName,
                        e.password_hash,
                        s.timeslot AS shiftName,
                        do.dotw_name AS dayoffName
                    FROM employee e
                    LEFT JOIN department d ON e.dep_id = d.dep_id
                    LEFT JOIN shift s ON e.shift_id = s.shift_id
                    LEFT JOIN dotweek do ON e.dayoff_id = do.dotw_id
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                EmployeeList.add(new EmployeeModel(
                        rs.getInt("employee_id"),
                        rs.getString("f_name"),
                        rs.getString("l_name"),
                        rs.getDate("dob"),
                        rs.getString("contact_no"),
                        rs.getString("email"),
                        rs.getString("depName"),
                        rs.getString("password_hash"), // Assuming you store hashed passwords
                        rs.getString("shiftName"),
                        rs.getString("dayoffName")));
            }

            AccountManagmentTableView.setItems(EmployeeList);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void AddAccountMove(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/COH_AddAccount.fxml"));
            root = loader.load();

            root = FXMLLoader.load(getClass().getResource("/View/COH_AddAccount.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setHeaderText("Error loading page.");
            a.setContentText("Please try again.");
            a.show();
        }
    }

    @FXML
    void handleFilterAction(ActionEvent event) {
        // Implement filtering logic based on TFsearch text
        String searchText = TFsearch.getText().toLowerCase();
        if (searchText.isEmpty()) {
            AccountManagmentTableView.setItems(EmployeeList);
        } else {
            ObservableList<EmployeeModel> filteredList = FXCollections.observableArrayList();
            for (EmployeeModel employee : EmployeeList) {
                if (employee.getFname().toLowerCase().contains(searchText) ||
                        employee.getLname().toLowerCase().contains(searchText) ||
                        employee.getEmail().toLowerCase().contains(searchText) ||
                        String.valueOf(employee.getId()).contains(searchText)) {
                    filteredList.add(employee);
                }
            }
            AccountManagmentTableView.setItems(filteredList);
        }
    }

    @FXML
    void LogOutActionBttn(ActionEvent event) {

    }

    @FXML
    void AccountMenuActionBttn(ActionEvent event) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/COH_AccountManagement.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            a.setAlertType(AlertType.ERROR);
            a.setHeaderText("Error loading page.");
            a.setContentText("Please try again.");
            a.show();
        }

    }

    @FXML
    void DashboardActionBttn(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/COH_Dashboard.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (IOException e) {
            a.setAlertType(AlertType.ERROR);
            a.setHeaderText("Error loading page.");
            a.setContentText("Please try again.");
            a.show();
        }
    }

    @FXML
    void HamburgerMenuActionBttn(ActionEvent event) {

    }

    @FXML
    void PharmacyActionBttn(ActionEvent event) {

    }

    @FXML
    void ScheduleActionBttn(ActionEvent event) {

    }

    @FXML
    void ScheduleuActionBttn(ActionEvent event) {

    }

}