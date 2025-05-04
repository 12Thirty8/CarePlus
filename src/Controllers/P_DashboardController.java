package Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Models.EmployeeModel;
import Models.RequestModel;
import db.DatabaseConnect;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.TableRow;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class P_DashboardController implements Initializable {

    private DatabaseConnect dbConnect = new DatabaseConnect();

    @FXML
    private Button FilterBttn;

    @FXML
    private TextField SearchButton;

    @FXML
    private TableView<RequestModel> StkInTableView;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label dashboardLabel;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label nameLabel;

    @FXML
    private AnchorPane namePane;

    @FXML
    private Label nametitleLabel;

    @FXML
    private Separator separator;

    @FXML
    private TableColumn<EmployeeModel, Integer> idcol;

    @FXML
    private TableColumn<EmployeeModel, Integer> listcol;

    @FXML
    private TableColumn<EmployeeModel, String> patientcol;

    @FXML
    private TableColumn<EmployeeModel, String> doctorcol;

    @FXML
    private TableColumn<EmployeeModel, String> nursecol;

    @FXML
    private TableColumn<EmployeeModel, String> reqcol;

    @FXML
    private TableColumn<EmployeeModel, Boolean> statcol;

    private ObservableList<EmployeeModel> EmployeeList = FXCollections.observableArrayList();

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        refreshEmployeeTable();
        setupRowContextMenu();
    }

    private void setupTableColumns() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        listcol.setCellValueFactory(new PropertyValueFactory<>("requestListId"));
        patientcol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        doctorcol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        nursecol.setCellValueFactory(new PropertyValueFactory<>("nurseName"));
        reqcol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        statcol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupRowContextMenu() {
    }

    private void refreshEmployeeTable() {
        EmployeeList.clear();
        try {
            Connection conn = dbConnect.connect();
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

            StkInTableView.setItems(EmployeeList);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
