package Controllers.NURSE;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import Controllers.ChangeShift;
import Controllers.UpdateShift;
import Controllers.ViewState;
import Models.ShiftRequestModel;
import db.DatabaseConnect;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.GetCurrentEmployeeID;
import util.SceneLoader;

public class N_AccountController {

    @FXML
    private Button ChangeShiftBtn;

    @FXML
    private Label numtf;

    @FXML
    private Label shifttf;

    @FXML
    private Label dayofftf;

    @FXML
    private Label emaddtf;

    @FXML
    private Label fnametf;

    @FXML
    private Label lnametf;

    @FXML
    private Button LogoutBtn;

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
    private AnchorPane mainPane;

    @FXML
    private Button minimizeBtn;

    @FXML
    private Text nameLabel;

    @FXML
    private Text TITLE1;

    @FXML
    private Text TITLE2;

    @FXML
    private Text lname;

    @FXML
    private Text fname;

    @FXML
    private Text number;

    @FXML
    private Text emadd;

    @FXML
    private Text shift;

    @FXML
    private Text dayoff;

    @FXML
    private TableView<ShiftRequestModel> ShiftRequestView;

    @FXML
    private TableColumn<ShiftRequestModel, Integer> srcol;
    @FXML
    private TableColumn<ShiftRequestModel, Boolean> statuscol;
    @FXML
    private TableColumn<ShiftRequestModel, String> shiftID;
    @FXML
    private TableColumn<ShiftRequestModel, Date> requestdatecol;
    @FXML
    private TableColumn<ShiftRequestModel, String> newshiftID;
    @FXML
    private TableColumn<ShiftRequestModel, String> desccol;

    private Alert a = new Alert(AlertType.NONE);

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
        int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();
        String nurseName = DatabaseConnect.getNurseName(employeeId);
        nameLabel.setText(nurseName + ", RN");
        setupTableColumns();
        refreshShiftRequestTable();
        loadEmployeeDetails(employeeId);
        setupRowContextMenu();

        // Disable ChangeShiftBtn if there's a pending request
        checkPendingRequests(employeeId);
    }

    private void setupRowContextMenu() {
        ShiftRequestView.setRowFactory(_ -> {
            TableRow<ShiftRequestModel> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem updateItem = new MenuItem("Update");
            MenuItem deleteItem = new MenuItem("Cancel");

            updateItem.setOnAction(_ -> {
                ShiftRequestModel selectedItem = row.getItem();
                if (selectedItem != null) {
                    // First check if the request is pending (status is false for pending, true for
                    // approved)
                    if (!selectedItem.getStatus()) { // Changed from string comparison to boolean check
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/UpdateShift.fxml"));
                            Parent root = loader.load();

                            UpdateShift controller = loader.getController();
                            controller.setRequestId(selectedItem.getSrid());
                            controller.setRefreshCallback(() -> refreshShiftRequestTable());

                            Stage popupStage = new Stage();
                            popupStage.setTitle("Update Shift Request");
                            popupStage.initModality(Modality.WINDOW_MODAL);
                            popupStage.initOwner(row.getScene().getWindow());
                            Scene scene = new Scene(root);
                            popupStage.setScene(scene);
                            popupStage.setResizable(false);
                            popupStage.showAndWait();
                        } catch (IOException e) {
                            e.printStackTrace();
                            showAlert("Error", "Failed to load update form", AlertType.ERROR);
                        }
                    } else {
                        showAlert("Information", "Only pending requests can be updated", AlertType.INFORMATION);
                    }
                }
            });

            deleteItem.setOnAction(_ -> {
                ShiftRequestModel selectedItem = row.getItem();
                if (selectedItem != null) {
                    deleteRow(selectedItem);
                }
            });

            contextMenu.getItems().addAll(updateItem, deleteItem);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu));

            return row;
        });
    }

    private void deleteRow(ShiftRequestModel item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Cancel this request?");
        alert.setContentText("Are you sure you want to cancel this shift request?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                cancelRequest(item.getSrid());
                refreshShiftRequestTable();
                showAlert("Success", "Shift request cancelled successfully", AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Failed to cancel shift request: " + e.getMessage(), AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void cancelRequest(int requestId) throws SQLException {
        Connection conn = DatabaseConnect.connect();
        String query = "DELETE FROM shiftrequest WHERE sr_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, requestId);
        pstmt.executeUpdate();
        pstmt.close();
        conn.close();
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Add this new method to check for pending requests
    private void checkPendingRequests(int employeeId) {
        try {
            Connection conn = DatabaseConnect.connect();
            String query = "SELECT COUNT(*) FROM shiftrequest WHERE requestedby = ? AND status = 'Pending'";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                ChangeShiftBtn.setDisable(true);
                ChangeShiftBtn.setTooltip(new Tooltip(
                        "You already have a pending shift request. Please wait for approval or delete the existing request."));
            } else {
                ChangeShiftBtn.setDisable(false);
                ChangeShiftBtn.setTooltip(null);
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadEmployeeDetails(int employeeId) {
        try {
            Connection conn = DatabaseConnect.connect();
            String query = "SELECT f_name, l_name, contact_no, email, shift_id, dayoff_id FROM employee WHERE employee_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                fnametf.setText(rs.getString("f_name"));
                lnametf.setText(rs.getString("l_name"));
                numtf.setText(rs.getString("contact_no"));
                emaddtf.setText(rs.getString("email"));

                // Get shift and dayoff names
                int shiftId = rs.getInt("shift_id");
                int dayoffId = rs.getInt("dayoff_id");

                shifttf.setText(getShiftName(shiftId));
                dayofftf.setText(getDayoffName(dayoffId));
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getShiftName(int shiftId) {
        try {
            Connection conn = DatabaseConnect.connect();
            String query = "SELECT timeslot FROM shift WHERE shift_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, shiftId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("timeslot");
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private String getDayoffName(int dayoffId) {
        try {
            Connection conn = DatabaseConnect.connect();
            String query = "SELECT dotw_name FROM dotweek WHERE dotw_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, dayoffId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("dotw_name");
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private void refreshShiftRequestTable() {
        int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();
        ObservableList<ShiftRequestModel> shiftRequests = DatabaseConnect.getShiftRequests(employeeId);
        ShiftRequestView.setItems(shiftRequests);
        checkPendingRequests(employeeId); // Update button state when table refreshes
    }

    private void setupTableColumns() {
        srcol.setCellValueFactory(new PropertyValueFactory<>("srid"));
        statuscol.setCellValueFactory(new PropertyValueFactory<>("status"));
        shiftID.setCellValueFactory(new PropertyValueFactory<>("shift"));
        requestdatecol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
        newshiftID.setCellValueFactory(new PropertyValueFactory<>("newshift"));
        desccol.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    @FXML
    void ChangeShiftBtnAction(ActionEvent event) {
        int employeeId = GetCurrentEmployeeID.fetchEmployeeIdFromSession();

        // Double check if there's a pending request (in case table wasn't refreshed)
        try {
            Connection conn = DatabaseConnect.connect();
            String query = "SELECT COUNT(*) FROM shiftrequest WHERE requestedby = ? AND status = 'Pending'";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Pending Request");
                alert.setHeaderText("You already have a pending shift request");
                alert.setContentText(
                        "Please wait for your current request to be approved or delete it before making a new one.");
                alert.showAndWait();
                return;
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ChangeShift.fxml"));
            Parent root = loader.load();

            ChangeShift controller = loader.getController();
            controller.setRefreshCallback(() -> {
                refreshShiftRequestTable();
                checkPendingRequests(employeeId); // Refresh button state after request is made
            });

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setResizable(false);
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.initOwner(((Node) event.getSource()).getScene().getWindow());

            loginStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            a.setAlertType(AlertType.ERROR);
            a.setContentText("Error loading page.");
            a.show();
        }
    }

    @FXML
    void closeAction(ActionEvent event) {
        Stage currentStage = (Stage) closeBtn.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    void crossBtnAction(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_RequestMonitor.fxml");
    }

    @FXML
    void homeBtnPressed(ActionEvent event) {
        SceneLoader.loadScene(event, "/View/N_Dashboard.fxml");
    }

    @FXML
    void minimizeAction(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.setIconified(true);
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
        SceneLoader.loadScene(event, "/View/N_Account.fxml");

    }

}
