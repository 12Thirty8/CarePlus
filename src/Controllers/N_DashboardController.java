package Controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import util.GetCurrentEmployeeID;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

public class N_DashboardController {

    @FXML
    private Button AccountMenuBttn;

    @FXML
    private Button DashboardBttn;

    @FXML
    private Button HamburgerMenuBttn;

    @FXML
    private Button PharmacyBttn;

    @FXML
    private Button ScheduleBttn;

    @FXML
    private Button ScheduleMenuBttn;

    @FXML
    private TableView<NurseModel> NurseDashboardTableView;

    @FXML
    private TableColumn<NurseModel, String> takenFrColumn;

    @FXML
    private TableColumn<NurseModel, String> activityColumn;

    @FXML
    private TableColumn<NurseModel, LocalDateTime> dateTimeColumn;

    private ObservableList<NurseModel> nurseModelObservableList;

    @FXML
    private Label nameLabel;

    @FXML
    public void initialize() {

        nurseModelObservableList = FXCollections.observableArrayList();
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
                        taken_from, activity, datetime_logged
                    FROM nurse_activity_log
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("Loaded rows: " + nurseModelObservableList.size());
                Timestamp ts = rs.getTimestamp("datetime_logged");
                nurseModelObservableList.add(new NurseModel(
                        rs.getString("taken_from"),
                        rs.getString("activity"),
                        ts.toLocalDateTime()));

            }

            NurseDashboardTableView.setItems(nurseModelObservableList);

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
    void PharmacyActionBttn(ActionEvent event) {

    }

    @FXML
    void ScheduleActionBttn(ActionEvent event) {

    }

    @FXML
    void ScheduleuActionBttn(ActionEvent event) {

    }

}