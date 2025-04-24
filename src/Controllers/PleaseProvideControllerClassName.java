package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Models.ClassModel;
import Utils.DatabaseConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PleaseProvideControllerClassName implements Initializable {

    DatabaseConnect dbconncect;

    private static DatabaseConnect dbConnect = new DatabaseConnect();

    @FXML
    private TableView<ClassModel> InReportsTable;

    @FXML
    private TableColumn<ClassModel, String> idCol;

    @FXML
    private TableColumn<ClassModel, String> nameCol;

    @FXML
    private Button filterButton;

    ObservableList<ClassModel> ClassList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadData();
        refreshTable();
    }

    private void loadData() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private void refreshTable() {
        ClassList.clear();
        try {
            Connection conn = dbConnect.connect();
            String query = "SELECT * FROM class";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ClassList.add(new ClassModel(
                        rs.getInt("class_id"),
                        rs.getString("class_name")));
            }

            InReportsTable.setItems(ClassList);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
