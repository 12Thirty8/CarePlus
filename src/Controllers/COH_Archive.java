package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class COH_Archive {

    @FXML
    private TableView<?> AccountManagmentTableView;

    @FXML
    private Button FilterBttn, hamburgermenuBtn, minimizedBtn, closeBtn, accountBtn, homeBtn,
            crossBtn, recordsBtn, clipboardBtn, LogOutBtn;

    @FXML
    private TextField TFsearch;

    @FXML
    private TableColumn<?, ?> depcol, dobcol, emailcol, emp_idcol, f_namecol, l_namecol, numbercol, offcol, shiftcol;

    @FXML
    private AnchorPane hamburgerPane;

    @FXML
    public void initialize() {
        hamburgerPane.setPrefWidth(ViewState.isHamburgerPaneExtended ? 230 : 107);
    }

    @FXML
    void AccountMenuActionBttn(ActionEvent event) {

    }

    @FXML
    void LogOutActionBttn(ActionEvent event) {

    }

    @FXML
    void homeBtnAction(ActionEvent event) {

    }

    @FXML
    void toggleHamburgerMenu(ActionEvent event) {

    }

}
