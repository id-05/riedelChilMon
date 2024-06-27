package main.utilits;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import main.Main;
import main.units.ProgrammSettings;
import main.units.TelegramUser;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, DAO {

    @FXML
    Button cleareBaseBut;
    @FXML
    TextField textBotToken;
    @FXML
    PasswordField textBotPassword;
    @FXML
    Button testMes;
    @FXML
    Button butClearUserList;
    @FXML
    HBox titleBox;
    int x,y;
    Stage stage;
    @FXML
    ComboBox<Integer> baudRateComBox;
    @FXML
    ComboBox<String> portNumberComBox;
    @FXML
    ComboBox<Integer> dataBitsComBox;
    @FXML
    ComboBox<String> parityComBox;
    @FXML
    ComboBox<String> stopBitComBox;
    @FXML
    ComboBox<String> timeZoneComboBox;
    ObservableList<String> portNumberItems = FXCollections.observableArrayList("COM1", "COM2", "COM3", "COM4","COM5", "COM6", "COM7", "COM8",
            "COM9", "COM10");
    ObservableList<Integer> baudRateItems = FXCollections.observableArrayList(600, 1200, 2400, 4800,9600,14400,19200,28800,
            38400,56000,57600,115200,128000,256000);
    ObservableList<Integer> dataBitsItems = FXCollections.observableArrayList(5, 6, 7, 8);
    ObservableList<String> parityItems = FXCollections.observableArrayList("none", "odd", "even", "mark","space");
    ObservableList<String> stopBitItems = FXCollections.observableArrayList("1", "1.5", "2");
    ObservableList<String> timeZoneItems = FXCollections.observableArrayList("-12:00", "-11:00", "-10:00","-09:00", "-08:00", "-07:00",
            "-06:00", "-05:00", "-04:00","-03:00", "-02:00", "-01:00","+00:00", "+01:00", "+02:00","+03:00", "+04:00", "+05:00",
            "+06:00", "+07:00","+08:00", "+09:00", "+10:00","+11:00", "+12:00","+13:00", "+14:00");

    @FXML
    public void Close(MouseEvent event){
        Main.stage.show();
        Main.stage.setX(stage.getX());
        Main.stage.setY(stage.getY());
        //DisplayController.applyNewSetting();
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
        saveStrParam("bottoken", textBotToken.getText());
        saveStrParam("botpassword", textBotPassword.getText());
    }

    @FXML
    public void sendTestMes(){
        for(TelegramUser tUser: getAllTelegramUser()){
 //           DisplayController.bot.sendTestMes(tUser.getId(),"Test Message!");
        }
    }

    @FXML
    public void ClearUserList(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();
            String deleteSql = "DELETE FROM telegramuser";
            statement.executeUpdate(deleteSql);
            statement.close();
        }catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void ClearOnlyRec() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();

            String deleteSql = "DELETE FROM record";
            statement.executeUpdate(deleteSql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML

    public void saveComPortSetting(){
        saveStrParam("comnumber",portNumberComBox.getSelectionModel().getSelectedItem());
        saveIntParam("combaudrate",baudRateComBox.getSelectionModel().getSelectedItem());
        saveStrParam("comparity",parityComBox.getSelectionModel().getSelectedItem());
        saveIntParam("comdatabits",dataBitsComBox.getSelectionModel().getSelectedItem());
        saveStrParam("comstopbits",stopBitComBox.getSelectionModel().getSelectedItem());
        saveStrParam("timezone",timeZoneComboBox.getSelectionModel().getSelectedItem());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleBox.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
                x = (int) (stage.getX() - mouseEvent.getScreenX());
                y = (int) (stage.getY() - mouseEvent.getScreenY());
            }
        });
        titleBox.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                stage.setX(mouseEvent.getScreenX() + x);
                stage.setY(mouseEvent.getScreenY() + y);
            }
        });

        ProgrammSettings progSet = new ProgrammSettings();

        portNumberComBox.setValue(progSet.getComNumber());
        portNumberComBox.setItems(portNumberItems);

        baudRateComBox.setValue(progSet.getComBaudRate());
        baudRateComBox.setItems(baudRateItems);

        dataBitsComBox.setValue(progSet.getComDataBits());
        dataBitsComBox.setItems(dataBitsItems);

        parityComBox.setValue(progSet.getComParity());
        parityComBox.setItems(parityItems);

        stopBitComBox.setValue(progSet.getComStopBits());
        stopBitComBox.setItems(stopBitItems);

        timeZoneComboBox.setValue(progSet.getTimeZone());
        timeZoneComboBox.setItems(timeZoneItems);

        textBotToken.setText(progSet.getBotToken());
        textBotPassword.setText(progSet.getBotPassword());

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage = (Stage) titleBox.getScene().getWindow();
                stage.setX(Main.stage.getX());
                stage.setY(Main.stage.getY());
            }
        });
    }
}
