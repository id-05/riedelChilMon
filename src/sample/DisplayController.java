package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.ResourceBundle;

public class DisplayController implements Initializable, DAO {

    private static SerialPort serialPort;

    @FXML
    public Pane titlePanel;
    public Button but;
    public Button settings;
    public static String comNumber;
    public static Integer comBaudRate;
    public static Integer comDataBits;
    public static String comParity;
    public static String comStopBits;


    @FXML
    public void Minimazed(MouseEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.hide();
    }

    @FXML
    public void Maximazed(MouseEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setFullScreen(true);
    }

    @FXML
    public void Close(MouseEvent event){
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void OpenSettings() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("settings.fxml"));
        Stage stageFrame = new Stage();
        stageFrame.setScene(new Scene(root));
        stageFrame.initStyle(StageStyle.TRANSPARENT);
        stageFrame.show();
    }

    @FXML
    public void Start(){
        System.out.println("start pres");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();

            String selectQuery = "SELECT * FROM record";
            ResultSet resultSet = statement.executeQuery(selectQuery);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String data = resultSet.getString("rec");

                System.out.println("ID: " + id + " : " + data);
            }

            resultSet.close();
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

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();

            String selectQuery = "SELECT * FROM param";
            ResultSet resultSet = statement.executeQuery(selectQuery);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String data = resultSet.getString("name");
                String data2 = resultSet.getString("valueStr");
                System.out.println("ID: " + id + " : " + data+"   "+data2);
            }

            resultSet.close();
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

    private static class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    //Получаем ответ от устройства, обрабатываем данные и т.д.
                    Date date = new Date();
                    String data = date.getTime()+" : "+serialPort.readString(event.getEventValue());
                    System.out.println(data);
                    Connection connection = null;
                    try {
                        connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
                        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO record (rec) VALUES (?)");
                        preparedStatement.setString(1, data);
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
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
                    //serialPort.writeString("Get data");
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();

            String createTableSql = "CREATE TABLE IF NOT EXISTS record (id INTEGER PRIMARY KEY, rec TEXT)";
            statement.executeUpdate(createTableSql);

            createTableSql = "CREATE TABLE IF NOT EXISTS param (id INTEGER PRIMARY KEY, name TEXT, valueStr TEXT, valueInt INTEGER)";
            statement.executeUpdate(createTableSql);

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

        comNumber = getStrParam("comnumber");
        comBaudRate = getIntParam("combaudrate");
        comDataBits = getIntParam("comdatabits");
        System.out.println(comNumber);
        System.out.println(comBaudRate);
        System.out.println(comDataBits);

        serialPort = new SerialPort(comNumber);
        try {
            serialPort.openPort();
            serialPort.setParams(comBaudRate,
                    comDataBits,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }

    }
}
