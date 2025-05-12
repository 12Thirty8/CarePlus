package Controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import Models.NurseModel;
import db.DatabaseConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import util.GetCurrentEmployeeID;
import util.SceneLoader;
import javafx.scene.control.TableColumn;

public class N_DashboardController {
    @FXML
    private Button clipboardBtn;

    @FXML
    private Button crossBtn;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button hamburgermenuBtn;

    @FXML
    private Button homeBtn;

    @FXML
    private TableView<NurseModel> StkInTableView;

    @FXML
    private TableColumn<NurseModel, String> takenFrColumn;

    @FXML
    private TableColumn<NurseModel, String> activityColumn;

    @FXML
    private TableColumn<NurseModel, LocalDateTime> dateTimeColumn;

    private ObservableList<NurseModel> nurseModelObservableList = FXCollections.observableArrayList();

    @FXML
    private Text nameLabel;

    @FXML
    public void initialize() {
        setupTableColumns();
        refreshEmployeeTable();
        int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();
        String nurseName = DatabaseConnect.getNurseName(employeeId);
        nameLabel.setText(nurseName + ", RN");

    }

    private void setupTableColumns() {
        takenFrColumn.setCellValueFactory(new PropertyValueFactory<>("takenFrom"));
        activityColumn.setCellValueFactory(new PropertyValueFactory<>("activity"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
    }

    private void refreshEmployeeTable() {
        nurseModelObservableList.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT
                        n.taken_from, n.activity, n.datetime_logged
                    FROM nurse_activity_log n
                    LEFT JOIN employee e ON n.employee_id = e.employee_id
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                nurseModelObservableList.add(new NurseModel(
                        rs.getString("taken_from"),
                        rs.getString("activity"),
                        rs.getTimestamp("datetime_logged").toLocalDateTime()));

            }

            StkInTableView.setItems(nurseModelObservableList);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void AccountMenuActionBttn(ActionEvent event) {

    }

    @FXML
    void DashboardActionBttn(ActionEvent event) {

    }

    @FXML
    void HamburgerMenuActionBttn(ActionEvent event) {

    }

    @FXML
    private void crossBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_RequestMonitor.fxml");

    }

    @FXML
    void ScheduleActionBttn(ActionEvent event) {

    }

}