package Controllers;

import Models.ListModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class N_PharmacyReq {

    @FXML
    private Button AddMedBtn;

    @FXML
    private Button BackBttn;

    @FXML
    private Button ClearBtn;

    @FXML
    private Button SubmitBtn;

    @FXML
    private ComboBox<String> dosage;

    @FXML
    private TableColumn<ListModel, String> dosagecol;

    @FXML
    private TableColumn<ListModel, String> idcol;

    @FXML
    private TableView<ListModel> listTableView;

    @FXML
    private TextField medidtf;

    @FXML
    private ComboBox<String> medname;

    @FXML
    private TableColumn<ListModel, String> namecol;

    @FXML
    private TableColumn<ListModel, Integer> qtycol;

    @FXML
    private TextField qtytf;

    @FXML
    private TextField recordidtf;

    @FXML
    private ComboBox<String> recordname;

    @FXML
    void AddMedBtnAction(ActionEvent event) {

    }

    @FXML
    void BackBttnAction(ActionEvent event) {

    }

    @FXML
    void ClearBtnAction(ActionEvent event) {

    }

    @FXML
    void SubmitBtnAction(ActionEvent event) {

    }

}
