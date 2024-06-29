package main.utilits;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import main.units.ChillerState;
import main.units.MenuItem;
import main.units.ProgrammSettings;
import main.units.TelegramUser;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import static main.Main.logging;

public class MyTelegramBotNewVersion  extends TelegramLongPollingBot implements BotHelper, DAO {
    String Token;
    String UserName = "riedelChillerMonitor";
    String welcometext = "Я бот - который может получать данные от контроллера чиллеров Riedel и сообщать их пользователям!";
    boolean registerStart = false;
    boolean password = false;
    ChillerState curChillerState;
    ProgrammSettings progSet = new ProgrammSettings();
    String buf = "";

    public MyTelegramBotNewVersion(String Token, ChillerState curChillerState){
        this.Token = Token;
        this.curChillerState = curChillerState;
    }

    public synchronized void sendMsg(String chatId, String s) {
        try {
            execute(prepareMsg(chatId,s));
        } catch (TelegramApiException e) {
            logging("Error send 1"+e.getMessage());
        }
    }

    public synchronized void sendMsg(String chatId, String s, InlineKeyboardMarkup inlineKeyboardMarkup) {
        try {
            execute(prepareMsg(chatId,s,inlineKeyboardMarkup));
        } catch (TelegramApiException e) {
            logging("Error send 2 "+e.getMessage()+" chatId "+chatId+"   "+inlineKeyboardMarkup.toString());
        }
    }

    public void editMsg(Update update, String text, InlineKeyboardMarkup keyboard){
        try {
            execute(prepareEditMsg(update,text,keyboard));
        } catch (TelegramApiException ex){
            logging(ex.toString());
        }
    }

    public void sendStateAllUser(String message, ChillerState oldChillerState){
        for(TelegramUser tUser: getAllTelegramUser()){
            sendState(tUser.getId(),oldChillerState, message);
        }
    }

    private void sendState(Update update, ChillerState chillerState, String alarmText){
        sendState(update.getMessage().getChatId().toString(), chillerState, alarmText);
    }

    public void sendState(String chatId, ChillerState chillerState, String alarmText) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            if (!alarmText.equals("")) {
                stringBuilder.append(alarmText).append("\n");
            }
            stringBuilder.append("\n");
            stringBuilder.append(chillerState.getDate()).append("\n").
                    append("Т первичный вход").append(": ").append(chillerState.getTpi()).append("°C").append("\n").
                    append("Т первичный выход").append(": ").append(chillerState.getTpo()).append("°C").append("\n").
                    append("Т вторичный вход").append(": ").append(chillerState.getTsi()).append("°C").append("\n").
                    append("Т вторичный выход").append(": ").append(chillerState.getTso()).append("°C").append("\n").
                    append("Ошибки").append(": ").append(chillerState.getErrors()).append("\n");
        }catch (Exception e){
            logging(e.getMessage());
        }
        sendMsg(chatId,stringBuilder.toString());
    }

    public void sendTestMes(String chatId, String message){
        sendMsg(chatId,message);
    }

    private void sendMsg(String chatId, String s, ReplyKeyboardMarkup replyKeyboardMaker) {
        try {
            execute(prepareMsg(chatId,s,replyKeyboardMaker));
        } catch (TelegramApiException e) {
            logging("Error send 3 "+e.getMessage()+" chatId "+chatId+"   "+replyKeyboardMaker.toString());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            if(update.getMessage().hasText()){
                if(validateUser(update.getMessage().getChat().getId().toString())){
                    if(update.getMessage().getText().equals("Админ")){
                        sendMsg(update.getMessage().getChatId().toString(), "Меню администратора:", getAdminMenu());
                    }else {
                        if(update.getMessage().getText().equals("/start")){
                            MainMenu(update,true);
                        }
                        if(update.getMessage().getText().equals("График за сутки")){
                            sendCurrentChart(update);
                        }
                        if(update.getMessage().getText().equals("Текущее состояние")){
                            sendState(update, new ChillerState(getLastChillerState()),"");
                        }
                        if(update.getMessage().getText().equals("Настройки")){
                            SubscriptionMenu(update);
                        }
                    }
                }else{
                    if(registerStart){
                        if(password){
                            TelegramUser tUser = new TelegramUser(update.getMessage().getChat().getId().toString(),update.getMessage().getText());
                            try{
                                saveNewUser(tUser);
                                password = false;
                                registerStart = false;
                                sendMsg(tUser.getId(), "Hello, "+tUser.getName()+"!");
                                MainMenu(update,true);
                            }catch (Exception e){
                                sendMsg(tUser.getId(), "No valid name! Try again!");
                            }
                        }else{
                            String bufStr = update.getMessage().getText().toUpperCase().replaceAll("[\\s|\\u00A0]+", "");
                            if(bufStr.equals(progSet.getBotPassword())) {
                                sendMsg(update.getMessage().getChat().getId().toString(), "Ok! Enter you name:");
                                password = true;
                            }else{
                                sendMsg(update.getMessage().getChat().getId().toString(), "Please enter password!");
                            }
                        }
                    }else {
                        registerStart = true;
                        sendMsg(update.getMessage().getChat().getId().toString(), "Please enter password!");
                    }
                }
            }

        }else if(update.hasCallbackQuery()){
            String firstTeg = "";
            String secondTeg = "";
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(update.getCallbackQuery().getData()).getAsJsonObject();
            if (jsonObject.has("name")) { firstTeg = jsonObject.get("name").getAsString(); }
            if (jsonObject.has("data")) { secondTeg = jsonObject.get("data").getAsString(); }
            //bot menu start here
            switch (firstTeg){
                case "settings":
                    if ("settings".equals(secondTeg)) {
                        SubscriptionMenu(update);
                    }
                    break;

                case "chSub":
                    TelegramUser user = getUserById(update.getCallbackQuery().getMessage().getChatId().toString());
                    parser = new JsonParser();
                    jsonObject = parser.parse(user.getFilter()).getAsJsonObject();
                    String item = secondTeg.substring(0,secondTeg.indexOf(":"));
                    if(secondTeg.contains("off")){
                        jsonObject.remove(item);
                        jsonObject.addProperty(item,"on");
                    }
                    if(secondTeg.contains("on")){
                        jsonObject.remove(item);
                        jsonObject.addProperty(item,"off");
                    }
                    user.setFilter(jsonObject.toString());
                    saveTeleramUserFilter(user.getId(),jsonObject.toString());
                    SubscriptionMenu(update);
                    break;
            }
        }
    }

    public void sendCurrentChart(Update update){
        XYSeriesCollection data = new XYSeriesCollection();
        XYSeries series = new XYSeries("XY Series");
        int i = 0;
        for (ChillerState butChillerState : getAllRecors()) {
            series.add(i, butChillerState.getTpo());
            i++;
        }
        data.addSeries(series);

        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "T(t) last hour", "time", "Temperature", dataset, true, false, false);
        File file = new File("./source.png");
        try {
            ChartUtilities.saveChartAsPNG(file, chart, 500, 300);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String caption = "График температуры за выбранный период";
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(update.getMessage().getChatId().toString())
                .photo(new InputFile(file))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .build();
        try {
            execute(sendPhoto);
        } catch (Exception e) {
            logging(e.getMessage());
        }
    }

    public void SubscriptionMenu(Update update){
        TelegramUser user;
        if(update.hasCallbackQuery()){
            user = getUserById(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
        }else{
            user = getUserById(String.valueOf(update.getMessage().getChat().getId()));
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(user.getFilter()).getAsJsonObject();
        menuItems.add(new MenuItem("Inform me" + " : "+jsonObject.get("InformMe").getAsString(),"chSub","InformMe" + ":"+jsonObject.get("InformMe").getAsString()));
        menuItems.add(new MenuItem("Only errors" + " : "+jsonObject.get("OnlyErrors").getAsString(),"chSub","OnlyErrors" + ":"+jsonObject.get("OnlyErrors").getAsString()));
        menuItems.add(new MenuItem("Only change errors state" + " : "+jsonObject.get("OnlyChangeState").getAsString(),"chSub","OnlyChangeState" + ":"+jsonObject.get("OnlyChangeState").getAsString()));
        menuItems.add(new MenuItem("Out of range" + " : "+jsonObject.get("OutOfRange").getAsString(),"chSub","OutOfRange" + ":"+jsonObject.get("OutOfRange").getAsString()));
        //menuItems.add(new MenuItem("Назад","back","main"));
        List<List<InlineKeyboardButton>> rowList = getMenuFromItemList(menuItems);
        inlineKeyboardMarkup.setKeyboard(rowList);
        if(update.hasCallbackQuery()) {
            String sBuilder = "Ваши настройки:" + "\n" +
                    "*" + "Inform me" + "*" + " - разрешить боту отравлять вам сообщения." + "\n" +
                    "*" + "OnlyErrors" + "*" + " - информировать меня только в случае наличия ошибки на устройстве" + "\n" +
                    "*" + "Only change errors state" + "*" + " - информировать меня только в случае изменения состояния ошибки." + "\n" +
                    "*" + "Out of range" + "*" + " - информировать меня о выходе измеряемых значений за установленные лимиты." + "\n";
            editMsg(update, sBuilder, inlineKeyboardMarkup);
        }else {
            String sBuilder = "Ваши настройки:" + "\n" +
                    "*" + "Inform me" + "*" + " - разрешить боту отравлять вам сообщения." + "\n" +
                    "*" + "OnlyErrors" + "*" + " - информировать меня только в случае наличия ошибки на устройстве" + "\n" +
                    "*" + "Only change errors state" + "*" + " - информировать меня только в случае изменения состояния ошибки." + "\n" +
                    "*" + "Out of range" + "*" + " - информировать меня о выходе измеряемых значений за установленные лимиты." + "\n";
            sendMsg(update.getMessage().getChatId().toString(), sBuilder, inlineKeyboardMarkup);
            buf = update.getMessage().getMessageId().toString();
        }
    }

    public void MainMenu(Update update){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Узнать текущее состояние","chillerstate","getstate"));
        menuItems.add(new MenuItem("Получить график","chillerstate","getchart"));
        menuItems.add(new MenuItem("Настройки","settings","settings"));
        List<List<InlineKeyboardButton>> rowList = getMenuFromItemList(menuItems);
        inlineKeyboardMarkup.setKeyboard(rowList);
        if(update.hasCallbackQuery()){
            editMsg(update, welcometext, inlineKeyboardMarkup);
        }else{
            sendMsg(update.getMessage().getChatId().toString(), welcometext, inlineKeyboardMarkup);
        }
    }

    public void MainMenu(Update update, Boolean menutype){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Текущее состояние");
        row.add("График за сутки");
        KeyboardRow rowSecond = new KeyboardRow();
        rowSecond.add("График за неделю");
        rowSecond.add("График за месяц");
        KeyboardRow rowThird = new KeyboardRow();
        rowThird.add("График за всё время");
        rowThird.add("Настройки");
        keyboardRows.add(row);
        keyboardRows.add(rowSecond);
        keyboardRows.add(rowThird);
        keyboardMarkup.setKeyboard(keyboardRows);
        sendMsg(update.getMessage().getChatId().toString(), welcometext, keyboardMarkup);
    }

    public void SettingsMenu(Update update){
        if(update.hasCallbackQuery()){
            editMsg(update, welcometext, getSettingsMenu());
        }
    }

    @Override
    public String getBotUsername() {
        return UserName;
    }

    @Override
    public String getBotToken() {
        return Token;
    }

    private XYDataset createDataset()
    {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries seriesTpi = new TimeSeries("Tpi");
        TimeSeries seriesTpo = new TimeSeries("Tpo");
        TimeSeries seriesTsi = new TimeSeries("Tsi");
        TimeSeries seriesTso = new TimeSeries("Tso");
        for (ChillerState butChillerState : getAllRecors()) {
            try {
                Double temp = butChillerState.getTpi();
                DateFormat format = new SimpleDateFormat("hh:mm d/MM/yyyy", Locale.ENGLISH);
                Date date = format.parse(butChillerState.getDate());
                Minute m = new Minute(date);
                seriesTpi.add(m, temp);
                seriesTpo.add(m,butChillerState.getTpo());
                seriesTsi.add(m,butChillerState.getTsi());
                seriesTso.add(m,butChillerState.getTso());
            } catch (Exception e) {
                logging(e.getMessage());
            }
        }
        dataset.addSeries(seriesTpi);
        dataset.addSeries(seriesTpo);
        dataset.addSeries(seriesTsi);
        dataset.addSeries(seriesTso);
        return dataset;
    }
}
