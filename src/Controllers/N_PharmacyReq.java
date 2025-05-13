package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.SceneLoader;

public class N_PharmacyReq {

    @FXML
    private Button AddMedBtn;

    @FXML
    private Button BackBttn;

    @FXML
    private Button ClearBtn;

    @FXML
    private TextField Nnametf;

    @FXML
    private Button SubmitBtn;

    @FXML
    private TextField medicinetf;

    @FXML
    private TextField qtytf;

    @FXML
    private TextField recordtf;

    @FXML
    void AddMedBtnAction(ActionEvent event) {

    }

    @FXML
    void BackBttnAction(ActionEvent event) {
        Stage currentStage = (Stage) BackBttn.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    void ClearBtnAction(ActionEvent event) {

    }

    @FXML
    void SubmitBtnAction(ActionEvent event) {

    }

}
