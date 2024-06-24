package sample;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import sample.units.ChillerState;
import sample.units.ProgrammSettings;
import sample.units.TelegramUser;
import sample.utilits.DAO;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.ResourceBundle;

public class DisplayController implements Initializable, DAO {

    private static SerialPort serialPort;
    public static MyTelegramBot bot = null;
    public String BotToken = "";
    public String BotPassword = "";
    public String comNumber;
    public Integer comBaudRate;
    public Integer comDataBits;
    public String comParity;
    public String comStopBits;
    public static ChillerState newChillerState;
    public ChillerState oldChillerState = null;
    public String timeZone;
    @FXML
    public Label labelTpi,labelTpo,labelTso,labelTsi,labelErrors, labelDate;
    public HBox titleBox;
    int x,y;
    Stage stage;
    static ProgrammSettings programmSettings;
    static String nameValue = "";

    public void botInit(){
        TelegramBotsApi botsApi;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            bot = new MyTelegramBot(BotToken, newChillerState);
            botsApi.registerBot(bot);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void Minimazed(MouseEvent event){
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.hide();
    }

    @FXML
    public void Maximazed(MouseEvent event){
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setFullScreen(true);
    }

    @FXML
    public void Close(MouseEvent event){
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();
        System.exit(0);
    }

    @FXML
    public void OpenSettings() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("settings.fxml"));
        Stage stageFrame = new Stage();
        stageFrame.setScene(new Scene(root));
        stageFrame.initStyle(StageStyle.TRANSPARENT);
        stageFrame.show();
        stage.hide();
    }

    @FXML
    public void OpenChart() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("chartform.fxml"));
        Stage stageFrame = new Stage();
        stageFrame.setScene(new Scene(root));
        stageFrame.initStyle(StageStyle.TRANSPARENT);
        stageFrame.show();
        stage.hide();
    }

    @FXML
    public void changeValueTPI() throws IOException {
        nameValue = "levelTpi";
        openChangeValueForm();
    }

    @FXML
    public void changeValueTPO() throws IOException {
        nameValue = "levelTpo";
        openChangeValueForm();
    }

    @FXML
    public void changeValueTSI() throws IOException {
        nameValue = "levelTsi";
        openChangeValueForm();
    }

    @FXML
    public void changeValueTSO() throws IOException {
        nameValue = "levelTso";
        openChangeValueForm();
    }

    public void openChangeValueForm() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("changevalueform.fxml"));
        Stage stageFrame = new Stage();
        stageFrame.setScene(new Scene(root));
        stageFrame.initStyle(StageStyle.TRANSPARENT);
        stageFrame.show();
    }

    public static JFreeChart createChart() {

        double[] values = { 95, 49, 14, 59, 50, 66, 47, 40, 1, 67,
                12, 58, 28, 63, 14, 9, 31, 17, 94, 71,
                49, 64, 73, 97, 15, 63, 10, 12, 31, 62,
                93, 49, 74, 90, 59, 14, 15, 88, 26, 57,
                77, 44, 58, 91, 10, 67, 57, 19, 88, 84
        };


        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries("key", values, 20);

        JFreeChart histogram = ChartFactory.createHistogram("JFreeChart Histogram", "y values", "x values", dataset, PlotOrientation.HORIZONTAL,true,true,true);

        return histogram;
    }

//    @FXML
//    public void Start(){
//        System.out.println("start pres");
//        Connection connection = null;
//        try {
//            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
//            Statement statement = connection.createStatement();
//
//            String selectQuery = "SELECT * FROM record";
//            ResultSet resultSet = statement.executeQuery(selectQuery);
//
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String data = resultSet.getString("rec");
//                System.out.println("ID: " + id + " : " + data+ "    ");
//            }
//
//            resultSet.close();
//            statement.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//
//        try {
//            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
//            Statement statement = connection.createStatement();
//
//            String selectQuery = "SELECT * FROM param";
//            ResultSet resultSet = statement.executeQuery(selectQuery);
//
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String data = resultSet.getString("name");
//                String data2 = resultSet.getString("valueStr");
//                System.out.println("ID: " + id + " : " + data+"   "+data2);
//            }
//
//            resultSet.close();
//            statement.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (connection != null) {
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    Date date = new Date();
                    String data = date.getTime()+ " : "+serialPort.readString(event.getEventValue());
                    //System.out.println(data);
                    if(newChillerState != null){
                        oldChillerState = newChillerState;
                    }
                    newChillerState = new ChillerState(data);
                    saveState(data);
                    analiseState(newChillerState);
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void analiseState(ChillerState chillerState){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                labelTpi.setText(chillerState.getTpi().toString());
                labelTpo.setText(chillerState.getTpo().toString());
                labelTsi.setText(chillerState.getTsi().toString());
                labelTso.setText(chillerState.getTso().toString());
                labelErrors.setText(chillerState.getErrors());
                labelDate.setText(chillerState.getDate());
            }
        });
        //C:3 dp:437 I/Yp/Ya:1000:1000:1000 Tso:199 I/Ym/Ya:-52:52:78 Tsi:198 Pso:531 Psi:94 Tpi:68 Tpo:89 Fpo:3595 Ppi:53 Ttr:190 Htr:402 DI:BF DO:63 F:0000:0000:0000:0000
        if(newChillerState == null){
            bot.sendStateAllUser("Бот был только что включен, последнее известное состояние системы:",chillerState);
        }else{
            for(TelegramUser tUser:getAllTelegramUser()){
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(tUser.getFilter()).getAsJsonObject();
                if(jsonObject.get("InformMe").getAsString().equals("on")){
                    if(jsonObject.get("OnlyErrors").getAsString().equals("on")){
                        if(jsonObject.get("OnlyChangeState").getAsString().equals("on")){
                            if(!chillerState.getErrors().equals(oldChillerState.getErrors())){
                                bot.sendState(tUser.getId(), chillerState, "Внимание! Изменилось состояние ошибок системы!");
                            }
                        }else {
                            if (!chillerState.getErrors().equals("0000:0000:0000:0000")) {
                                bot.sendState(tUser.getId(), chillerState, "Внимание! Обнаружены ошибки на устройстве!");
                            }
                        }
                    }else{
                        if(jsonObject.get("OutOfRange").getAsString().equals("on")){

                        }else {
                            bot.sendState(tUser.getId(), chillerState, "Текущее состояние системы:");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initializ");
        titleBox.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                Node node = (Node) mouseEvent.getSource();
                stage = (Stage) node.getScene().getWindow();
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

        readDateFromBase();

        botInit();
        if(getLastChillerState()!=null) {
            oldChillerState = new ChillerState(getLastChillerState());
        }
        newChillerState = null;
        if(oldChillerState!=null) {
            analiseState(oldChillerState);
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage = (Stage) titleBox.getScene().getWindow();
            }
        });
    }

    public void readDateFromBase(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rcm.db");
            Statement statement = connection.createStatement();

            String createTableSql = "CREATE TABLE IF NOT EXISTS record (id INTEGER PRIMARY KEY, rec TEXT)";
            statement.executeUpdate(createTableSql);

            createTableSql = "CREATE TABLE IF NOT EXISTS param (id INTEGER PRIMARY KEY, name TEXT, valueStr TEXT, valueInt INTEGER)";
            statement.executeUpdate(createTableSql);

            createTableSql = "CREATE TABLE IF NOT EXISTS telegramuser (id INTEGER PRIMARY KEY, tid TEXT, name TEXT, subscription TEXT, filter TEXT)";
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
        if(comNumber==null){
            ClearBase();
        }
        comNumber = getStrParam("comnumber");
        comBaudRate = getIntParam("combaudrate");
        comDataBits = getIntParam("comdatabits");
        comParity = getStrParam("comparity");
        comStopBits = getStrParam("comstopbits");
        BotToken = getStrParam("bottoken");
        BotPassword = getStrParam("botpassword");
        timeZone = getStrParam("timezone");

        serialPort = new SerialPort(comNumber);
        int bufSerialStopBits = 1;
        switch (comStopBits){
            case "1":
                break;
            case "2": bufSerialStopBits = 2;
                break;
            case "1.5": bufSerialStopBits = 3;
                break;
            default:  bufSerialStopBits = 1;
                break;
        }

        int bufSerialParity = 0;
        switch (comParity){
            case "none":
                break;
            case "even": bufSerialParity = 2;
                break;
            case "mark": bufSerialParity = 3;
                break;
            case "space": bufSerialParity = 4;
                break;
            case "odd": bufSerialParity = 1;
                break;
            default:  bufSerialParity = 0;
                break;
        }

        try {
            serialPort.openPort();
            serialPort.setParams(comBaudRate,
                    comDataBits,
                    bufSerialStopBits,
                    bufSerialParity);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
        }
        catch (SerialPortException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
