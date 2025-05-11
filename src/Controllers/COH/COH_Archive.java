package Controllers.COH;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import Controllers.ViewState;
import Models.EmployeeModel;
import db.DatabaseConnect;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.SceneLoader;

public class COH_Archive {

    @FXML
    private TableView<EmployeeModel> AccountManagmentTableView;

    @FXML
    private Button FilterBttn, hamburgermenuBtn, minimizedBtn, closeBtn, accountBtn, homeBtn,
            crossBtn, recordsBtn, clipboardBtn, LogOutBtn, Backbtn;

    @FXML
    private TextField TFsearch;

    @FXML
    private TableColumn<?, ?> depcol, dobcol, emailcol, emp_idcol, f_namecol, l_namecol, numbercol, offcol, shiftcol;

    private ObservableList<EmployeeModel> EmployeeList = FXCollections.observableArrayList();

    @FXML
    private AnchorPane hamburgerPane;

    private Alert a = new Alert(AlertType.NONE);

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        setupTableColumns();
        refreshEmployeeTable();
        setupRowContextMenu();
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
                        do.dotw_name AS dayoffName, e.status
                    FROM employee e
                    LEFT JOIN department d ON e.dep_id = d.dep_id
                    LEFT JOIN shift s ON e.shift_id = s.shift_id
                    LEFT JOIN dotweek do ON e.dayoff_id = do.dotw_id
                    WHERE e.status = '0'
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

    private void setupRowContextMenu() {
        AccountManagmentTableView.setRowFactory(_ -> {
            TableRow<EmployeeModel> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem restoreItem = new MenuItem("Restore");
            restoreItem.setOnAction(_ -> {
                EmployeeModel selectedItem = row.getItem();
                if (selectedItem != null) {
                    restoreRow(selectedItem);
                }
            });

            contextMenu.getItems().addAll(restoreItem);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu));
            return row;
        });
    }

    private void restoreRow(EmployeeModel item) {
        // Show confirmation dialog before restoring
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Restore");
        alert.setHeaderText("Restore this account?");
        alert.setContentText("Are you sure you want to restore: " + item + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    restoreAccount(item.getId()); // Pass the employee ID
                    AccountManagmentTableView.getItems().remove(item); // Optionally remove from archived view
                    showAlert("Success", "Employee account restored successfully.");
                } catch (SQLException e) {
                    showAlert("Error", "Failed to restore employee: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void restoreAccount(int id) throws SQLException {
        try (Connection conn = DatabaseConnect.connect();
                PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE employee SET status = 1 WHERE employee_id = ?")) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    
    @FXML
    void BackBttnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/COH_AccountManagement.fxml");
    }

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

    @FXML
    void clipboardBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/COH_ManageShiftRequest.fxml");
    }

    @FXML
    private void crossBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/COH_StockInReport.fxml");

    }

    @FXML
    private void homeBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/COH_Dashboard.fxml");
    }

    @FXML
    private void recordsBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/COH_ActivityReports.fxml");
    }

    @FXML
    void AccountMenuActionBttn(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/COH_AccountManagement.fxml");
    }
}
