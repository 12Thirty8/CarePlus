package Controllers.COH;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Controllers.ViewState;
import Models.StocksModel;
import db.DatabaseConnect;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;
import util.SceneLoader;

public class COH_StkInReport {

    @FXML
    private Button FilterBttn, hamburgermenuBtn, minimizeBtn, closeBtn, accountBtn, homeBtn,
            crossBtn, recordsBtn, clipboardBtn, LogoutBtn, StkOutBttn, movetoProductBtn, movetoStocksBtn;

    @FXML
    private TextField SearchButton;

    @FXML
    private Label nameLabel;

    @FXML
    private TableView<?> StkInTableView;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private TableColumn<StocksModel, String> sinbycol;

    @FXML
    private TableColumn<StocksModel, String> sindatecol;

    @FXML
    private TableColumn<StocksModel, String> dosecol;

    @FXML
    private TableColumn<StocksModel, String> statcol;

    @FXML
    private TableColumn<StocksModel, String> expcol;

    @FXML
    private TableColumn<StocksModel, Integer> idcol;

    @FXML
    private TableColumn<StocksModel, String> namecol;

    @FXML
    private TableColumn<StocksModel, Integer> qtycol;

    @FXML
    private TableView<StocksModel> StockTable;

    private ObservableList<String> allMedicineNames = FXCollections.observableArrayList();



    private ObservableList<StocksModel> EmployeeList = FXCollections.observableArrayList();

    private Alert a = new Alert(AlertType.NONE);

    public static int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        
        setupTableColumns();
        refreshEmployeeTable();
        loadMedicineNames();        
        String cohName = DatabaseConnect.getCOHName();
        nameLabel.setText(cohName != null ? cohName : "Name not found");
    }

    private void loadMedicineNames() {
        allMedicineNames.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = "SELECT med_name FROM medicine";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                allMedicineNames.add(rs.getString("med_name"));
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    

    private void setupTableColumns() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        qtycol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        dosecol.setCellValueFactory(new PropertyValueFactory<>("dose"));
        expcol.setCellValueFactory(new PropertyValueFactory<>("expDate"));
        sinbycol.setCellValueFactory(new PropertyValueFactory<>("sinby"));
        sindatecol.setCellValueFactory(new PropertyValueFactory<>("sinDate"));
        statcol.setCellValueFactory(new PropertyValueFactory<>("status"));

        StockTable.setItems(EmployeeList);
    }

    private void refreshEmployeeTable() {
        EmployeeList.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT
                        b.batch_id,
                        m.med_name AS medName,
                        b.batch_stock,
                        b.batch_dosage,
                        b.batch_exp,
                        CONCAT(COALESCE(e.f_name, ''), ' ', COALESCE(e.l_name, '')) AS stockinBy,
                        b.stockin_date,
                        s.status_name AS status
                    FROM batch b
                    LEFT JOIN employee e ON b.stockin_by = e.employee_id
                    LEFT JOIN medicine m ON b.med_id = m.med_id
                    LEFT JOIN stockstatus s ON b.status_id = s.status_id
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                EmployeeList.add(new StocksModel(
                        rs.getInt("batch_id"),
                        rs.getString("medName"),
                        rs.getInt("batch_stock"),
                        rs.getString("batch_dosage"),
                        rs.getDate("batch_exp"),
                        rs.getString("stockinBy"),
                        rs.getDate("stockin_date"),
                        rs.getString("status")));
            }

            StockTable.setItems(EmployeeList);

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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void LogOutActionBttn(ActionEvent event) {
        showAlert("Confirm Logout", "Are you sure you want to log out?");
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/LoginPage.fxml"));
            Parent root = loader.load();

            // Create a new stage for the login page
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setResizable(false); // Optional: prevent resizing
            loginStage.show();

            // Close the current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.show();
        }
    }
    
    @FXML
    private void movetoProductBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/COH_Products.fxml");
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
