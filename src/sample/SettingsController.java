package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class SettingsController implements Initializable, DAO {

    @FXML
    public Pane titlePanel;
    public Button cleareBaseBut;
    public TextField textBotToken, textBotPassword;

    @FXML
    public ComboBox<Integer> baudRateComBox;
    public ComboBox<String> portNumberComBox;
    public ComboBox<Integer> dataBitsComBox;
    public ComboBox<String> parityComBox;
    public ComboBox<String> stopBitComBox;
    public ComboBox<String> timeZoneComboBox;
    ObservableList<String> portNumberItems = FXCollections.observableArrayList("COM1", "COM2", "COM3", "COM4","COM5", "COM6", "COM7", "COM8",
            "COM9", "COM10");
    ObservableList<Integer> baudRateItems = FXCollections.observableArrayList(600, 1200, 2400, 4800,9600,14400,19200,28800,
            38400,56000,57600,115200,128000,256000);
    ObservableList<Integer> dataBitsItems = FXCollections.observableArrayList(5, 6, 7, 8);
    ObservableList<String> parityItems = FXCollections.observableArrayList("none", "odd", "even", "mark","space");
    ObservableList<String> stopBitItems = FXCollections.observableArrayList("1", "1.5", "2");
    ObservableList<String> timeZoneItems = FXCollections.observableArrayList("-12:00", "-11:00", "-10:00","-09:00", "-08:00", "-07:00",
            "-06:00", "-05:00", "-04:00","-03:00", "-02:00", "-01:00","00:00", "+01:00", "+02:00","+03:00", "+04:00", "+05:00",
            "+06:00", "+07:00","+08:00", "+09:00", "+10:00","+11:00", "+12:00","+13:00", "+14:00");

    @FXML
    public void Close(MouseEvent event){
        Main.stage.show();
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
        saveStrParam("bottoken", textBotToken.getText());
        saveStrParam("botpassword", textBotPassword.getText());
    }

    @FXML
    public void ClearBase() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();

            String deleteSql = "DELETE FROM record";
            statement.executeUpdate(deleteSql);
            statement.close();

            statement = connection.createStatement();
            deleteSql = "DELETE FROM param";
            statement.executeUpdate(deleteSql);
            statement.close();

            statement = connection.createStatement();
            deleteSql = "DELETE FROM telegramuser";
            statement.executeUpdate(deleteSql);
            statement.close();

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "comnumber");
            preparedStatement.setString(2, "COM1");
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "combaudrate");
            preparedStatement.setString(2, "57600");
            preparedStatement.setInt(3, 57600);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "comparity");
            preparedStatement.setString(2, "none");
            preparedStatement.setInt(3, 0);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "comdatabits");
            preparedStatement.setString(2, "8");
            preparedStatement.setInt(3, 8);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "comstopbits");
            preparedStatement.setString(2, "1");
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "bottoken");
            preparedStatement.setString(2, "");
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("INSERT INTO param (name, valueStr, valueInt) VALUES (?, ?, ?)");
            preparedStatement.setString(1, "botpassword");
            preparedStatement.setString(2, "");
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();

            textBotToken.setText("");
            textBotPassword.setText("");


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
        portNumberComBox.setValue(DisplayController.comNumber);
        portNumberComBox.setItems(portNumberItems);

        baudRateComBox.setValue(DisplayController.comBaudRate);
        baudRateComBox.setItems(baudRateItems);

        dataBitsComBox.setValue(DisplayController.comDataBits);
        dataBitsComBox.setItems(dataBitsItems);

        parityComBox.setValue(DisplayController.comParity);
        parityComBox.setItems(parityItems);

        stopBitComBox.setValue(DisplayController.comStopBits);
        stopBitComBox.setItems(stopBitItems);

        timeZoneComboBox.setValue(DisplayController.timeZone);
        timeZoneComboBox.setItems(timeZoneItems);

        textBotToken.setText(DisplayController.BotToken);
        textBotPassword.setText(DisplayController.BotPassword);
    }
}
