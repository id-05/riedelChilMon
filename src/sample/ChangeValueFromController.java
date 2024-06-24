package sample;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.utilits.DAO;
import java.net.URL;
import java.util.ResourceBundle;

public class ChangeValueFromController implements Initializable, DAO {

    @FXML
    public FontAwesomeIcon butPlus, butMinus, butSave;
    @FXML
    public Label labelValue;
    public int x;
    public int y;
    Integer value;

    @FXML
    public void Close(MouseEvent event){
        saveIntParam(DisplayController.nameValue,value);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void Plus(){
        value++;
        labelValue.setText(value.toString());
    }

    @FXML
    public void Minus(){
        value--;
        labelValue.setText(value.toString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        value = getIntParam(DisplayController.nameValue);
        labelValue.setText(value.toString());


        Platform.runLater(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}

