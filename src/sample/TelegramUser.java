package sample;

import com.google.gson.JsonObject;

public class TelegramUser {

    private String id;
    private String Name;
    private String subscription;
    private String filter;

    public TelegramUser() {

    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public TelegramUser(String id, String Name) {
        this.id = id;
        this.Name = Name;
        this.filter = getDefaultFilter();
    }

    public TelegramUser(String id, String Name, String subscription, String filter) {
        this.id = id;
        this.Name = Name;
        this.subscription = subscription;
        this.filter = filter;
    }

    public String getDefaultFilter() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("InformMe", "on");
        jsonObject.addProperty("OnlyErrors", "on");
        jsonObject.addProperty("OnlyChangeState", "on");
        jsonObject.addProperty("OutOfRange", "off");
        return jsonObject.toString();
    }
}