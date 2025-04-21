import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

public class AccountManagementController implements Initializable {

    private DatabaseConnect dbConnect = new DatabaseConnect();

    @FXML
    private TableView<EmployeeModel> AccountManagmentTableView;

    @FXML
    private Button AddAccountBtn;

    @FXML
    private Button FilterBttn;

    @FXML
    private TextField TFsearch;

    @FXML
    private TableColumn<EmployeeModel, Integer> depcol;

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
    private TableColumn<EmployeeModel, Integer> shiftcol;

    private ObservableList<EmployeeModel> EmployeeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        refreshEmployeeTable();
    }

    private void setupTableColumns() {
        emp_idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        f_namecol.setCellValueFactory(new PropertyValueFactory<>("fname"));
        l_namecol.setCellValueFactory(new PropertyValueFactory<>("lname"));
        dobcol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        numbercol.setCellValueFactory(new PropertyValueFactory<>("number"));
        emailcol.setCellValueFactory(new PropertyValueFactory<>("email"));
        depcol.setCellValueFactory(new PropertyValueFactory<>("dep"));
        shiftcol.setCellValueFactory(new PropertyValueFactory<>("shift"));
        offcol.setCellValueFactory(new PropertyValueFactory<>("dayoff"));
    }

    private void refreshEmployeeTable() {
        EmployeeList.clear();
        try {
            Connection conn = dbConnect.connect();
            String query = "SELECT * FROM employee"; // Adjust table name as needed
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
                        rs.getInt("dep_id"),
                        rs.getString("password_hash"), // Assuming you store hashed passwords
                        rs.getInt("shift_id"),
                        rs.getInt("dayoff_id")));
            }

            AccountManagmentTableView.setItems(EmployeeList);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void AddAccountMove(ActionEvent event) {
        Stage currentStage = (Stage) AddAccountBtn.getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("COH_AddAccount.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Account");

            // Close the current stage after the new one is ready
            currentStage.close();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading page.", "Error",
                    JOptionPane.ERROR_MESSAGE);
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
}