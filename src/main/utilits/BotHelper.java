package main.utilits;

import com.google.gson.JsonObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import main.units.MenuItem;

import java.util.ArrayList;
import java.util.List;

public interface BotHelper {

    public default SendMessage prepareMsg(String chatId, String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        sendMessage.disableWebPagePreview();
        return sendMessage;
    }

    public default SendMessage prepareMsg(String chatId, String s, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);

        sendMessage.setText(s);
        //sendMessage.disableWebPagePreview();
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public default EditMessageText prepareEditMsg(Update update, String text, InlineKeyboardMarkup keyboard) {
        EditMessageText editMessage = new EditMessageText();
        editMessage.enableMarkdown(true);
        editMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        editMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessage.setText(text);
        editMessage.disableWebPagePreview();
        editMessage.setReplyMarkup(keyboard);
        return editMessage;
    }

    public default List<List<InlineKeyboardButton>> getMenuFromItemList(ArrayList<MenuItem> menuItem){
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for(MenuItem bufItem:menuItem) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(bufItem.getVisibleName());
            inlineKeyboardButton.setCallbackData(getJsonForBotMenu(bufItem.getFirstTag(),bufItem.getSecondTag()));
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(inlineKeyboardButton);
            rowList.add(keyboardButtonsRow);
        }
        return rowList;
    }

    public default String getJsonForBotMenu(String Name, String Data) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", Name);
        jsonObject.addProperty("data", Data);
        return jsonObject.toString();
    }

    public default InlineKeyboardMarkup getAdminMenu(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Выключить бота","settings","exit"));
        menuItems.add(new MenuItem("Отправить сообщение всем" ,"bottalk","bottalk"));
        menuItems.add(new MenuItem("Назад","back","main"));
        List<List<InlineKeyboardButton>> rowList = getMenuFromItemList(menuItems);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public default InlineKeyboardMarkup getSettingsMenu(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Разрешить информировать меня","settings","mainrule"));
        menuItems.add(new MenuItem("Фильтр по цене закупок","settings","filtrprice"));
        menuItems.add(new MenuItem("Добавить Котрагента","settings","addkontragent"));
        menuItems.add(new MenuItem("Удалить Котрагента","settings","deletekontragent"));
        menuItems.add(new MenuItem("Добавить Группу","settings","addgroup"));
        menuItems.add(new MenuItem("Удалить Группу","settings","deletegroup"));
        menuItems.add(new MenuItem("Назад","back","main"));
        List<List<InlineKeyboardButton>> rowList = getMenuFromItemList(menuItems);//new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }
}

