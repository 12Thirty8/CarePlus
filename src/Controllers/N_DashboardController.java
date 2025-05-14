package Controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import Models.NurseModel;
import db.DatabaseConnect;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    private Button closeBtn;

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
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
    private void crossBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_RequestMonitor.fxml");
    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
                SceneLoader.loadScene(event, "/View/P_Dashboard.fxml");

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

    

}