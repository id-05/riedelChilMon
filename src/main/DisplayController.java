package main;

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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import main.units.ChillerState;
import main.units.TelegramUser;
import main.utilits.DAO;
import main.utilits.MyTelegramBotNewVersion;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.ResourceBundle;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import static javafx.scene.paint.Color.WHITE;

public class DisplayController implements Initializable, DAO {

    private static SerialPort serialPort;
    public static MyTelegramBotNewVersion bot = null;
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
    public Label labelTpi,labelTpo,labelTso,labelTsi,labelErrors, labelDate, levelTpi, levelTpo, levelTso, levelTsi;
    public HBox titleBox;
    int x,y;
    Stage stage;
    //static ProgrammSettings programmSettings;
    static String nameValue = "";
    int value = 0;

    public void botInit(){
        TelegramBotsApi botsApi;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            bot = new MyTelegramBotNewVersion(BotToken, newChillerState);
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
        Parent root = FXMLLoader.load(getClass().getResource("resources/fxml/settings.fxml"));
        Stage stageFrame = new Stage();
        stageFrame.setScene(new Scene(root));
        stageFrame.initStyle(StageStyle.TRANSPARENT);
        stageFrame.show();
        stage.hide();
    }

    @FXML
    public void OpenChart() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("resources/fxml/chartform.fxml"));
        Stage stageFrame = new Stage();
        stageFrame.setScene(new Scene(root));
        stageFrame.initStyle(StageStyle.TRANSPARENT);
        stageFrame.show();
        stage.hide();
    }

    @FXML
    public void changeValueTPI() {
        nameValue = "levelTpi";
        openChangeValueForm();
    }

    @FXML
    public void changeValueTPO() {
        nameValue = "levelTpo";
        openChangeValueForm();
    }

    @FXML
    public void changeValueTSI() {
        nameValue = "levelTsi";
        openChangeValueForm();
    }

    @FXML
    public void changeValueTSO() {
        nameValue = "levelTso";
        openChangeValueForm();
    }

    public void openChangeValueForm()  {
        Dialog<String> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("resources/customDialog.css").toExternalForm());
        dialog.setResizable(true);
        int width = 40;
        Pane pane = new Pane();
        pane.setPrefWidth(width);
        pane.setPrefHeight(25);
        Pane pane1 = new Pane();
        pane1.setPrefWidth(width);
        pane1.setPrefHeight(25);
        Pane pane2 = new Pane();
        pane2.setPrefWidth(width);
        pane2.setPrefHeight(25);
        Pane pane3 = new Pane();
        pane3.setPrefWidth(width);
        pane3.setPrefHeight(25);
        Pane pane4 = new Pane();
        pane4.setPrefWidth(width);
        pane4.setPrefHeight(25);
        Label labelValue = new Label();
        labelValue.setTextFill(WHITE);
        labelValue.setFont(Font.font("System Bold",48));
        labelValue.setPrefWidth(55);
        value = getIntParam(DisplayController.nameValue);
        labelValue.setText(String.valueOf(value));

        FontAwesomeIcon plus = new FontAwesomeIcon();
        plus.setGlyphName("PLUS");
        plus.setFill(WHITE);
        plus.setSize("3em");
        plus.setOnMouseClicked(event -> {
            value++;
            labelValue.setText(String.valueOf(value));
        });

        FontAwesomeIcon minus = new FontAwesomeIcon();
        minus.setGlyphName("MINUS");
        minus.setFill(WHITE);
        minus.setSize("3em");
        minus.setOnMouseClicked(event -> {
            if(value>1) {
                value--;
            }
            labelValue.setText(String.valueOf(value));
        });

        FontAwesomeIcon save = new FontAwesomeIcon();
        save.setGlyphName("SAVE");
        save.setFill(WHITE);
        save.setSize("3em");
        save.setOnMouseClicked(event -> {
            saveIntParam(DisplayController.nameValue,value);
            DialogPane dialogPane = dialog.getDialogPane();
            Stage stageDialog = (Stage)dialogPane.getScene().getWindow();
            stageDialog.close();
            updateLevel();
        });

        GridPane grid = new GridPane();
        grid.add(minus, 1, 1);
        grid.add(pane, 2, 1);
        grid.add(labelValue, 3, 1);
        grid.add(pane1, 4, 1);
        grid.add(plus, 5, 1);
        grid.add(pane2, 2, 2);
        grid.add(save, 3, 3);
        dialog.getDialogPane().setContent(grid);
        dialog.show();
    }

    private class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    Date date = new Date();
                    String data = date.getTime()+ " : "+serialPort.readString(event.getEventValue());
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
                            if( chillerState.getTpo()>Double.valueOf(getIntParam("levelTpo")) |
                                    chillerState.getTpi()>Double.valueOf(getIntParam("levelTpi")) |
                                        chillerState.getTso()>Double.valueOf(getIntParam("levelTso")) |
                                            chillerState.getTsi()>Double.valueOf(getIntParam("levelTsi"))
                                ){
                                bot.sendState(tUser.getId(), chillerState, "Внимание! Одно из измеряемых значений превысило установленный лимит!");
                            }
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
        LOGGER.info("display start");
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
                updateLevel();
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

    public void updateLevel(){
        levelTpi.setText(getIntParam("levelTpi").toString());
        levelTpo.setText(getIntParam("levelTpo").toString());
        levelTsi.setText(getIntParam("levelTsi").toString());
        levelTso.setText(getIntParam("levelTso").toString());
    }
}
