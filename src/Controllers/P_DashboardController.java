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
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
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

    private ObservableList<RequestModel> EmployeeList = FXCollections.observableArrayList();

    private boolean isHamburgerPaneExtended = false;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        refreshEmployeeTable();
        setupRowContextMenu();

        hamburgerPane.setPrefWidth(230); 
        hamburgermenuBtn.setOnAction(event -> toggleHamburgerMenu());
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
            Connection conn = dbConnect.connect();
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
}
