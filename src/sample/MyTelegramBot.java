package sample;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class MyTelegramBot extends TelegramLongPollingBot implements BotHelper, DAO {

    String Token;
    String UserName = "bitserver_bot";
    String welcometext = "Я бот - который может получать данные от контроллера чиллеров Riedel и сообщать их пользователям!";
    boolean registerStart = false;
    boolean password = false;

    public MyTelegramBot(String Token){
        this.Token = Token;
    }

    public synchronized void sendMsg(String chatId, String s) {
        try {
            execute(prepareMsg(chatId,s));
        } catch (TelegramApiException e) {
            System.out.println("Error send 1"+e.getMessage());
        }
    }

    public synchronized void sendMsg(String chatId, String s, InlineKeyboardMarkup inlineKeyboardMarkup) {
        try {
            execute(prepareMsg(chatId,s,inlineKeyboardMarkup));
        } catch (TelegramApiException e) {
            System.out.println("Error send 2"+e.getMessage());
        }
    }

    public void editMsg(Update update, String text, InlineKeyboardMarkup keyboard){
        try {
            execute(prepareEditMsg(update,text,keyboard));
        } catch (TelegramApiException ex){
            System.out.println(ex.toString());
        }
    }

    public void sendStateAllUser(ChillerState chillerState){
        for(TelegramUser tUser:getAllTelegramUser()){
            sendState(tUser.getId(),chillerState, "Внимание! Обнаружены ошибки на устройстве!");
        }
    }

    public void sendStateAllUser(String message, ChillerState oldChillerState){
        for(TelegramUser tUser:getAllTelegramUser()){
            sendState(tUser.getId(),oldChillerState, message);
        }
    }

    public void sendState(Update update, ChillerState chillerState, String alarmText){
        String chatId = update.getCallbackQuery().getMessage().getChat().getId().toString();
        sendState(chatId, chillerState, alarmText);
    }

    //основная процедура рассылки состояния
    public synchronized void sendState(String chatId, ChillerState chillerState, String alarmText) {
        StringBuilder stringBuilder = new StringBuilder();
        if(!alarmText.equals("")){
            stringBuilder.append(alarmText).append("\n");
        }
        stringBuilder.append("\n");
        stringBuilder.append(chillerState.getDate()).append("\n").
                append("Т первичный вход").append(": ").append(chillerState.getTpi()).append("°C").append("\n").
                append("Т первичный выход").append(": ").append(chillerState.getTpo()).append("°C").append("\n").
                append("Т вторичный вход").append(": ").append(chillerState.getTsi()).append("°C").append("\n").
                append("Т вторичный выход").append(": ").append(chillerState.getTso()).append("°C").append("\n").
                append("Ошибки").append(": ").append(chillerState.getErrors()).append("\n");
        sendMsg(chatId,stringBuilder.toString());
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            if(update.getMessage().hasText()){
                if(validateUser(update.getMessage().getChat().getId().toString())){
                    if(update.getMessage().getText().equals("Админ")){
                        sendMsg(update.getMessage().getChatId().toString(), "Меню администратора:", getAdminMenu());
                    }else {
                        MainMenu(update);
                    }
                }else{
                    if(registerStart){//start registration new user
                        if(password){
                            TelegramUser tUser = new TelegramUser(update.getMessage().getChat().getId().toString(),update.getMessage().getText());
                            try{
                                saveNewUser(tUser);
                                password = false;
                                registerStart = false;
                                sendMsg(tUser.getId(), "Hello, "+tUser.getName()+"!");
                            }catch (Exception e){
                                sendMsg(tUser.getId(), "No valid name! Try again!");
                            }
                        }else{
                            String bufStr = update.getMessage().getText().toUpperCase().replaceAll("[\\s|\\u00A0]+", "");
                            if(bufStr.equals(DisplayController.BotPassword)) {
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
                case "subscription":
                    MainMenu(update);
                    break;

                case "settings":
                    if ("settings".equals(secondTeg)) {
                        SubscriptionMenu(update);
                    }
                    break;

                case "chSub":
                    TelegramUser user = getUserById(update.getCallbackQuery().getMessage().getChat().getId().toString());
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

                case "chillerstate":
                    switch (secondTeg){
                        case "settings":
                            SubscriptionMenu(update);
                            break;
                        case "getstate":
                            sendState(update, DisplayController.newChillerState,"");
                            break;
                        case "getchart":
                            WritableImage image = ChartController.mainChart.snapshot(new SnapshotParameters(), null);

                            PixelReader pixelReader = image.getPixelReader();
                            int width = (int) image.getWidth();
                            int height = (int) image.getHeight();

                            byte[] byteArray = new byte[width * height * 4]; // 4 bytes per pixel (ARGB)

                            pixelReader.getPixels(0, 0, width, height, PixelFormat.getByteBgraPreInstance(), byteArray, 0, width * 4);

                            byte[] imageBytes =byteArray;
                            String caption = "Lovely Gal";
//
//                            TelegramPhotoSender imgSender = new TelegramPhotoSender();
//                            imgSender.sendPhotoMessage(recipient, imageBytes, caption);

                            sendState(update, DisplayController.newChillerState,"");
                            break;
                    }
                    break;

                case "back":
                    switch (secondTeg) {
                        case "main":
                            MainMenu(update);
                            break;
                        case "settings":
                            SettingsMenu(update);
                            break;
                    }
                    break;
            }
        }
    }

    public void SubscriptionMenu(Update update){
        TelegramUser user = getUserById(update.getCallbackQuery().getMessage().getChat().getId().toString());
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(user.getFilter()).getAsJsonObject();
        menuItems.add(new MenuItem("Inform me" + " : "+jsonObject.get("InformMe").getAsString(),"chSub","InformMe" + ":"+jsonObject.get("InformMe").getAsString()));
        menuItems.add(new MenuItem("Only errors" + " : "+jsonObject.get("OnlyErrors").getAsString(),"chSub","OnlyErrors" + ":"+jsonObject.get("OnlyErrors").getAsString()));
        menuItems.add(new MenuItem("Only change errors state" + " : "+jsonObject.get("OnlyChangeState").getAsString(),"chSub","OnlyChangeState" + ":"+jsonObject.get("OnlyChangeState").getAsString()));
        menuItems.add(new MenuItem("Out of range" + " : "+jsonObject.get("OutOfRange").getAsString(),"chSub","OutOfRange" + ":"+jsonObject.get("OutOfRange").getAsString()));
        menuItems.add(new MenuItem("Назад","back","main"));
        List<List<InlineKeyboardButton>> rowList = getMenuFromItemList(menuItems);
        inlineKeyboardMarkup.setKeyboard(rowList);
        if(update.hasCallbackQuery()) {
            String sBuilder = "Ваши настройки:" + "\n" +
                    "*" + "Inform me" + "*" + " - разрешить боту отравлять вам сообщения." + "\n" +
                    "*" + "OnlyErrors" + "*" + " - информировать меня только в случае наличия ошибки на устройстве" + "\n" +
                    "*" + "Only change errors state" + "*" + " - информировать меня только в случае изменения состояния ошибки." + "\n" +
                    "*" + "Out of range" + "*" + " - информировать меня о выходе измеряемых значений за установленные лимиты." + "\n";
            editMsg(update, sBuilder, inlineKeyboardMarkup);
        }
    }

    public void MainMenu(Update update){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Узнать текущее состояние","chillerstate","getstate"));
        menuItems.add(new MenuItem("Узнать текущее состояние","chillerstate","getchart"));
        menuItems.add(new MenuItem("Настройки","settings","settings"));
        List<List<InlineKeyboardButton>> rowList = getMenuFromItemList(menuItems);
        inlineKeyboardMarkup.setKeyboard(rowList);
        if(update.hasCallbackQuery()){
            editMsg(update, welcometext, inlineKeyboardMarkup);
        }else{
            sendMsg(update.getMessage().getChatId().toString(), welcometext, inlineKeyboardMarkup);
        }
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

//    public void sendPhotoMessage(String recipient, byte[] imageBytes, String caption)
//            throws Exception {
//        byte[] encodedBytes = Base64.encodeBase64(imageBytes);
//        String base64Image = new String(encodedBytes);
//
//        ImageMessage imageMsgObj = new ImageMessage();
//        imageMsgObj.number = recipient;
//        imageMsgObj.image = base64Image;
//        imageMsgObj.caption = caption;
//
//        Gson gson = new Gson();
//        String jsonPayload = gson.toJson(imageMsgObj);
//
//        URL url = new URL(GATEWAY_URL);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setDoOutput(true);
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("X-WM-CLIENT-ID", CLIENT_ID);
//        conn.setRequestProperty("X-WM-CLIENT-SECRET", CLIENT_SECRET);
//        conn.setRequestProperty("Content-Type", "application/json");
//
//        OutputStream os = conn.getOutputStream();
//        os.write(jsonPayload.getBytes());
//        os.flush();
//        os.close();
//
//        int statusCode = conn.getResponseCode();
//        System.out.println("Response from Telegram Gateway: \n");
//        System.out.println("Status Code: " + statusCode);
//        BufferedReader br = new BufferedReader(new InputStreamReader(
//                (statusCode == 200) ? conn.getInputStream()
//                        : conn.getErrorStream()));
//        String output;
//        while ((output = br.readLine()) != null) {
//            System.out.println(output);
//        }
//        conn.disconnect();
//    }

}

