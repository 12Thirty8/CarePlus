package Controllers.PHARMACIST;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import Controllers.ViewState;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;
import util.SceneLoader;
import Models.ListModel;
import Models.RequestModel;
import db.DatabaseConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class P_DashboardController implements Initializable {

    @FXML
    private Button approvereqBtn;

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
    private Button minimizeBtn;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Button closeBtn;

    @FXML
    private Text nameLabel;

    @FXML
    private TableColumn<RequestModel, Integer> idcol;

    @FXML
    private TableColumn<ListModel, Integer> batchidcol;

    @FXML
    private TableColumn<RequestModel, Integer> recordcol;

    @FXML
    private TableColumn<ListModel, String> namecol;

    @FXML
    private TableColumn<ListModel, String> dosagecol;

    @FXML
    private TableColumn<ListModel, String> enccol;

    @FXML
    private TableView<RequestModel> reqTableView;

    @FXML
    private TableView<ListModel> listTableView;

    @FXML
    private TableColumn<RequestModel, String> reqdatecol;

    @FXML
    private TableColumn<ListModel, Integer> qtycol;

    @FXML
    private TableColumn<RequestModel, Boolean> statcol;

    private Alert a = new Alert(AlertType.INFORMATION);

    private ObservableList<RequestModel> EmployeeList = FXCollections.observableArrayList();

    public static int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

    public void initialize(URL url, ResourceBundle rb) {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        initializeRowSelectionListener();
        setupTableColumns();
        refreshEmployeeTable();
        setupRowContextMenu();
        String pharmacistName = DatabaseConnect.getPharmacistName(employeeId);
        nameLabel.setText(pharmacistName != null ? pharmacistName : "Name not found");

        batchidcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        dosagecol.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        qtycol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
    }

    private void setupTableColumns() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("reqid"));
        recordcol.setCellValueFactory(new PropertyValueFactory<>("recId"));
        reqdatecol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        statcol.setCellValueFactory(new PropertyValueFactory<>("status"));
        enccol.setCellValueFactory(new PropertyValueFactory<>("nurseName"));
    }

    private void refreshEmployeeTable() {
        EmployeeList.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT
                        r.request_id, r.record_id, e.f_name AS encodedBy, r.request_date, r.status
                    FROM request r LEFT JOIN employee e ON r.encoded_by = e.employee_id
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                EmployeeList.add(new RequestModel(
                        rs.getInt("request_id"),
                        rs.getInt("record_id"),
                        rs.getString("encodedBy"),
                        rs.getDate("request_date"),
                        rs.getBoolean("status")));
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
    private void initializeRowSelectionListener() {
        reqTableView.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) -> {
            if (newSelection != null) {
                int selectedRequestId = newSelection.getReqid();
                ObservableList<ListModel> listItems = FXCollections.observableArrayList();
                try {
                    Connection conn = DatabaseConnect.connect();
                    String query = """
                            SELECT
                                l.batch_id,
                                m.med_name AS name,
                                b.batch_dosage,
                                l.qty
                            FROM
                                requestlist l
                            JOIN
                                batch b ON l.batch_id = b.batch_id
                            LEFT JOIN
                                medicine m ON b.med_id = m.med_id
                            WHERE
                                l.req_id = ?
                            """;
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, selectedRequestId);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        listItems.add(new ListModel(
                                rs.getInt("batch_id"),
                                rs.getString("name"),
                                rs.getString("batch_dosage"),
                                rs.getInt("qty")));
                    }
                    listTableView.setItems(listItems);
                    rs.close();
                    pstmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Removed instance initializer block; moved logic to initialize()
                    initializeRowSelectionListener();
                    // Optionally, setup columns for listTableView if not already done elsewhere
                    batchidcol.setCellValueFactory(new PropertyValueFactory<>("batchid"));
                    namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
                    dosagecol.setCellValueFactory(new PropertyValueFactory<>("dosage"));
                    qtycol.setCellValueFactory(new PropertyValueFactory<>("qty"));
                }
            }
        });
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
        SceneLoader.loadScene(event, "/View/P_Account.fxml");
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
    void PharmacyBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Stocks.fxml");

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
    void minimizeAction(ActionEvent event) {

    }

    private ObservableList<ListModel> getBatchListForRequest(int requestId) {
        ObservableList<ListModel> listItems = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnect.connect()) {
            String query = """
                        SELECT l.batch_id, m.med_name AS name, b.batch_dosage, l.qty
                        FROM requestlist l
                        JOIN batch b ON l.batch_id = b.batch_id
                        LEFT JOIN medicine m ON b.med_id = m.med_id
                        WHERE l.req_id = ?
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, requestId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                listItems.add(new ListModel(
                        rs.getInt("batch_id"),
                        rs.getString("name"),
                        rs.getString("batch_dosage"),
                        rs.getInt("qty")));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listItems;
    }

    private void approveRequest(RequestModel request) {
        int requestId = request.getReqid();
        ObservableList<ListModel> items = getBatchListForRequest(requestId);

        try (Connection conn = DatabaseConnect.connect()) {
            conn.setAutoCommit(false); // For transaction safety

            // Update each batch's stock
            for (ListModel item : items) {
                String updateQuery = "UPDATE batch SET batch_stock = batch_stock - ? WHERE batch_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(updateQuery);
                pstmt.setInt(1, item.getQuantity());
                pstmt.setInt(2, item.getId());
                pstmt.executeUpdate();
                pstmt.close();
            }

            // Mark request as approved
            String statusUpdate = "UPDATE request SET status = 1 WHERE request_id = ?";
            PreparedStatement statusStmt = conn.prepareStatement(statusUpdate);
            statusStmt.setInt(1, requestId);
            statusStmt.executeUpdate();
            statusStmt.close();

            conn.commit(); // Commit all changes
            showAlert("Success", "Request approved and stock updated.");
            refreshEmployeeTable();
            listTableView.getItems().clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to approve request.");
        }
    }

    private void setupRowContextMenu() {
        reqTableView.setRowFactory(_ -> {
            TableRow<RequestModel> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem approveItem = new MenuItem("Approve");
            approveItem.setOnAction(_ -> {
                RequestModel selectedRequest = row.getItem();
                if (!selectedRequest.getStatus()) { // Only approve if not yet approved
                    approveRequest(selectedRequest);
                } else {
                    showAlert("Info", "This request is already approved.");
                }
            });

            contextMenu.getItems().add(approveItem);

            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            // Hide context menu when not on a valid row
            row.setOnMouseClicked(_ -> {
                if (contextMenu.isShowing()) {
                    contextMenu.hide();
                }
            });

            return row;
        });
    }

}
