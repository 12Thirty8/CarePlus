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
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;
import util.SceneLoader;

public class P_StocksController implements Initializable {

    @FXML
    private Button clearBtn;

    @FXML
    private DatePicker expdate;

    @FXML
    private TextField medidtf;

    @FXML
    private Text nameLabel;

    @FXML
    private TextField nametf;

    @FXML
    private TextField qtytf;

    @FXML
    private TableColumn<StocksModel, String> sinbycol;

    @FXML
    private TableColumn<StocksModel, String> sindatecol;

    @FXML
    private TextField sintf;

    @FXML
    private TableColumn<StocksModel, String> statcol;

    @FXML
    private Button updatebtn;

    @FXML
    private Button LogoutBtn;

    @FXML
    private Button addstockBtn;

    @FXML
    private TextField batchidtf;

    @FXML
    private Button movetoProductBtn;

    @FXML
    private TableView<StocksModel> StockTable;

    @FXML
    private Button clipboardBtn;

    @FXML
    private Button crossBtn;

    @FXML
    private TableColumn<StocksModel, String> expcol;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button hamburgermenuBtn;

    @FXML
    private Button homeBtn;

    @FXML
    private TableColumn<StocksModel, Integer> idcol;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TableColumn<StocksModel, String> namecol;

    @FXML
    private TableColumn<StocksModel, Integer> qtycol;

    @FXML
    private Button closeBtn;

    @FXML
    private Button movetoStocksBtn;

    @FXML
    private Button minimizeBtn;

    private ObservableList<StocksModel> EmployeeList = FXCollections.observableArrayList();

    private Alert a = new Alert(AlertType.NONE);

    public static int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        setupTableColumns();
        refreshEmployeeTable();
        setupRowContextMenu();
        String pharmacistName = DatabaseConnect.getPharmacistName(employeeId);
        nameLabel.setText(pharmacistName != null ? pharmacistName : "Name not found");
    }

    private void setupTableColumns() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("id"));
        namecol.setCellValueFactory(new PropertyValueFactory<>("name"));
        qtycol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        expcol.setCellValueFactory(new PropertyValueFactory<>("expDate"));
        sinbycol.setCellValueFactory(new PropertyValueFactory<>("sinby"));
        sindatecol.setCellValueFactory(new PropertyValueFactory<>("sinDate"));
        statcol.setCellValueFactory(new PropertyValueFactory<>("status"));

        StockTable.setItems(EmployeeList);
    }

    private void setupRowContextMenu() {
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

    @FXML
    void clipboardBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Schedule.fxml"); 
    }

    @FXML
    private void PharmacyBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Stocks.fxml"); 
    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Dashboard.fxml"); 
    }

    @FXML
    void addstockBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_StockIn.fxml"); 
    }

    @FXML
    void movetoProductBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Products.fxml"); 
    }

     @FXML
    void movetoStocksBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/P_Stocks.fxml"); 
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
    void clearBtnPressed(ActionEvent event) {

    }

    @FXML
    private void updatebtnPressed(ActionEvent event) {

    }
}
