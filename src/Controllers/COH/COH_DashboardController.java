package Controllers.COH;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import Controllers.ViewState;
import Models.ChiefDashboardModel;
import db.DatabaseConnect;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import util.SceneLoader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;

public class COH_DashboardController {

    @FXML
    private TableView<ChiefDashboardModel> ChiefDashboardTableView;

    @FXML
    private TableColumn<ChiefDashboardModel, Integer> idcol;

    @FXML
    private TableColumn<ChiefDashboardModel, Integer> recordcol;
    @FXML
    private TableColumn<ChiefDashboardModel, Integer> listcol;
    @FXML
    private TableColumn<ChiefDashboardModel, String> encbycol;
    @FXML
    private TableColumn<ChiefDashboardModel, Date> reqcol;
    @FXML
    private TableColumn<ChiefDashboardModel, Integer> statcol;

    ObservableList<ChiefDashboardModel> rows = FXCollections.observableArrayList();

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button minimizedButton, closeButton, AccountMenuBttn, DashboardBttn, hamburgermenuBtn,
            ScheduleBttn, ScheduleMenuBttn, LogOutBttn, closeBtn;

    @FXML
    private Label TitleText, requestCounter;

    @FXML
    private Text nameLabel;

    @FXML
    private AreaChart<?, ?> AreaChartPanel;

    @FXML
    private AnchorPane NamePanel, mainPane, TotalRequestPanel;

    private Alert a = new Alert(AlertType.NONE);

    @FXML
    public void initialize() {

        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);

        setupTableColumns();
        refreshChiefDashboardTable();
        setTotalRequestCounter();

        fadeInNode(TitleText, 0);
        fadeInNode(NamePanel, 200);
        fadeInNode(TotalRequestPanel, 200);
        fadeInNode(AreaChartPanel, 300);
        fadeInNode(ChiefDashboardTableView, 400);

        String COHName = DatabaseConnect.getCOHName();
        nameLabel.setText(COHName != null ? COHName : "Name not found");

    }

    private void setupTableColumns() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        recordcol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        listcol.setCellValueFactory(new PropertyValueFactory<>("listId"));
        encbycol.setCellValueFactory(new PropertyValueFactory<>("encodedBy"));
        reqcol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        statcol.setCellValueFactory(new PropertyValueFactory<>("status"));

    }

    private void refreshChiefDashboardTable() {
        rows.clear();
        String sql = """
                SELECT
                  r.request_id,
                  r.record_id,
                  rl.reqlist_id           AS list_id,
                  CONCAT(COALESCE(e.f_name, ''), ' ', COALESCE(e.l_name, '')) AS encoded_by,
                  r.request_date,
                  r.status
                FROM request r
                JOIN requestlist rl
                  ON r.request_id = rl.req_id
                JOIN employee e
                  ON r.encoded_by = e.employee_id
                ORDER BY r.request_date DESC
                """;

        try (Connection conn = DatabaseConnect.connect();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            // DEBUG: see if there's any rows at all
            if (!rs.isBeforeFirst()) {
                System.out.println("[DEBUG] No rows returned from SQL!");
            } else {
                System.out.println("[DEBUG] Rows present!");
            }

            while (rs.next()) {
                rows.add(new ChiefDashboardModel(
                        rs.getInt("request_id"),
                        rs.getInt("record_id"),
                        rs.getInt("list_id"),
                        rs.getString("encoded_by"),
                        rs.getDate("request_date"),
                        rs.getInt("status")));

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading rows.");
            a.show();
        }

        ChiefDashboardTableView.setItems(rows);
    }

    private void setTotalRequestCounter() {
        String sql = """
                SELECT COUNT(DISTINCT request_id) AS total_request
                FROM request;
                """;

        try (Connection conn = DatabaseConnect.connect();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int total = rs.getInt("total_request");
                // assuming requestCounter is a Label
                requestCounter.setText(Integer.toString(total));
            } else {
                requestCounter.setText("0");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error setting total request count:\n" + ex.getMessage());
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
    private void fadeInNode(Node node, double delayMillis) {
        // node.setOpacity(0); // Start fully transparent
        FadeTransition fade = new FadeTransition(Duration.millis(800), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setCycleCount(1);
        fade.setDelay(Duration.millis(delayMillis)); // Delay before it starts
        fade.play();
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
