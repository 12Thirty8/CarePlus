package Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;
import Models.RequestModel;
import db.DatabaseConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class P_DashboardController implements Initializable {

    @FXML
    private TableView<RequestModel> StkInTableView;

    @FXML
    private Button clipboardBtn;

    @FXML
    private Button crossBtn;

    @FXML
    private TableColumn<RequestModel, String> encbycol;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    private Button hamburgermenuBtn;

    @FXML
    private Button homeBtn;

    @FXML
    private TableColumn<RequestModel, Integer> idcol;

    @FXML
    private TableColumn<RequestModel, Integer> listcol;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TableColumn<RequestModel, Integer> recordcol;

    @FXML
    private TableColumn<RequestModel, String> reqcol;

    @FXML
    private TableColumn<RequestModel, Boolean> statcol;

    @FXML
    private Button closeBtn;

    @FXML
    private Text nameLabel;

    private ObservableList<RequestModel> EmployeeList = FXCollections.observableArrayList();

    private boolean isHamburgerPaneExtended = false;

    private Alert a = new Alert(AlertType.NONE);

    public static int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        refreshEmployeeTable();
        setupRowContextMenu();
        // Added by JC. Used to get the current user's pharmacist name.
        String pharmacistName = DatabaseConnect.getPharmacistName(employeeId);
        nameLabel.setText(pharmacistName != null ? pharmacistName : "Name not found");

        hamburgerPane.setPrefWidth(230);
        hamburgermenuBtn.setOnAction(_ -> toggleHamburgerMenu());
    }

    @FXML
    private void toggleHamburgerMenu() {
        Timeline timeline = new Timeline();

        if (isHamburgerPaneExtended) {
            KeyValue keyValue = new KeyValue(hamburgerPane.prefWidthProperty(), 230);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
            timeline.getKeyFrames().add(keyFrame);

        } else {
            KeyValue keyValue = new KeyValue(hamburgerPane.prefWidthProperty(), 107);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(200), keyValue);
            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();
        isHamburgerPaneExtended = !isHamburgerPaneExtended;
    }

    private void setupTableColumns() {
        idcol.setCellValueFactory(new PropertyValueFactory<>("reqid"));
        listcol.setCellValueFactory(new PropertyValueFactory<>("requestListId"));
        recordcol.setCellValueFactory(new PropertyValueFactory<>("recId"));
        encbycol.setCellValueFactory(new PropertyValueFactory<>("nurseName"));
        reqcol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        statcol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupRowContextMenu() {
    }

    private void refreshEmployeeTable() {
        EmployeeList.clear();
        try {
            Connection conn = DatabaseConnect.connect();
            String query = """
                    SELECT
                        r.request_id, r.record_id, r.requestlist_id, e.f_name AS encodedBy, r.request_date, r.status
                    FROM request r
                    LEFT JOIN employee e ON r.encoded_by = e.employee_id
                    """;
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                EmployeeList.add(new RequestModel(
                        rs.getInt("request_id"),
                        rs.getInt("record_id"),
                        rs.getInt("requestlist_id"),
                        rs.getString("encodedBy"),
                        rs.getDate("request_date"),
                        rs.getBoolean("status")));
            }

            StkInTableView.setItems(EmployeeList);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clipboardBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_ProcessRequest.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.setHeaderText("Error");
            a.show();
        }
    }

    @FXML
    void crossBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Stocks.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.setHeaderText("Error");
            a.show();
        }
    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.setHeaderText("Error");
            a.show();
        }
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
    private void PharmacyBtnPressed(ActionEvent event) {

         try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/P_Stocks.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading Pharmacy page.");
            a.setHeaderText("Error");
            a.show();
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
}
