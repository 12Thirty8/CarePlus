package Controllers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import Models.ListModel;
import Models.MyRequestModel;
import Models.RequestModel;
import db.DatabaseConnect;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;
import util.SceneLoader;

public class N_RequestMonitorController implements Initializable {

    @FXML
    private Button AddMedBtn;

    @FXML
    private Button ClearBtn;

    @FXML
    private Button LogoutBtn;

    @FXML
    private Button SubmitBtn;

    @FXML
    private Button clipboardBtn;

    @FXML
    private Button closeBtn;

    @FXML
    private Button crossBtn;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button hamburgermenuBtn;

    @FXML
    private Button homeBtn;

    @FXML
    private TableColumn<MyRequestModel, Integer> idcol;

    @FXML
    private TableColumn<ListModel, Integer> batchidcol;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TextField reqidtf;

    @FXML
    private TextField recordidtf;

    @FXML
    private TextField batchidtf;

    @FXML
    private TextField qtytf;

    @FXML
    private Button minimizeBtn;

    @FXML
    private Text nameLabel;

    @FXML
    private TableColumn<MyRequestModel, Integer> recordcol;

    @FXML
    private TableColumn<ListModel, String> namecol;

    @FXML
    private TableColumn<ListModel, String> dosagecol;

    @FXML
    private TableView<MyRequestModel> reqTableView;

    @FXML
    private TableView<ListModel> listTableView;

    @FXML
    private TableColumn<MyRequestModel, String> reqdatecol;

    @FXML
    private TableColumn<ListModel, Integer> qtycol;

    @FXML
    private TableColumn<MyRequestModel, Boolean> statcol;

    public static int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

    private Alert a = new Alert(Alert.AlertType.NONE);

    private ObservableList<MyRequestModel> EmployeeList = FXCollections.observableArrayList();

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        setupTableColumns();
        refreshEmployeeTable();
        String nurseName = DatabaseConnect.getPharmacistName(employeeId);
        nameLabel.setText(nurseName != null ? nurseName : "Name not found");
    }

    private void setupTableColumns() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("reqid"));
        recordcol.setCellValueFactory(new PropertyValueFactory<>("recordid"));
        reqdatecol.setCellValueFactory(new PropertyValueFactory<>("reqdate"));
        statcol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void refreshEmployeeTable() {
        EmployeeList.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT
                        r.request_id, r.record_id, r.request_date, r.status
                    FROM request r WHERE r.encoded_by = ?
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                EmployeeList.add(new MyRequestModel(
                        rs.getInt("request_id"),
                        rs.getInt("record_id"),
                        rs.getDate("request_date"),
                        rs.getString("status")));
            }

            reqTableView.setItems(EmployeeList);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void AddMedBtnAction(ActionEvent event) {

    }

    @FXML
    void ClearBtnAction(ActionEvent event) {

    }

    @FXML
    void LogOutActionBttn(ActionEvent event) {

    }

    @FXML
    void SubmitBtnAction(ActionEvent event) {

    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Dashboard.fxml");

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
    void PharmacyBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_RequestMonitor.fxml");

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
        SceneLoader.loadScene(event, "/View/N_Schedule.fxml");

    }
}
