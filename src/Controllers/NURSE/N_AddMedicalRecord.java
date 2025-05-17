package Controllers.NURSE;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class N_AddMedicalRecord {

    @FXML
    private Button BackBttn;

    @FXML
    private DatePicker CheckupDate;

    @FXML
    private TextArea DiagnosisArea;

    @FXML
    private TextArea DiagnosisArea1;

    @FXML
    private TextArea NotesArea;

    @FXML
    private ChoiceBox<?> StausBox;

    @FXML
    private Button cancelbtn;

    @FXML
    private TextField doctorIDtf;

    @FXML
    private ComboBox<?> patientIDtf;

    @FXML
    private TextField patientIDtf1;

    @FXML
    private TextField patientIDtf11;

    @FXML
    private Button savebtn;

    @FXML
    void BackBttnAction(ActionEvent event) {

    }

    @FXML
    void CancelButtonAction(ActionEvent event) {

    }

    @FXML
    void SaveChangesAction(ActionEvent event) {

    }

}
