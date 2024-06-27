package main.units;

import main.utilits.DAO;

public class ProgrammSettings implements DAO {
    public String BotToken;
    public String BotPassword;
    public String comNumber;
    public Integer comBaudRate;
    public Integer comDataBits;
    public String comParity;
    public String comStopBits;
    public String timeZone;

    public String getBotToken() {
        return BotToken;
    }

    public void setBotToken(String botToken) {
        BotToken = botToken;
    }

    public String getBotPassword() {
        return BotPassword;
    }

    public void setBotPassword(String botPassword) {
        BotPassword = botPassword;
    }

    public String getComNumber() {
        return comNumber;
    }

    public void setComNumber(String comNumber) {
        this.comNumber = comNumber;
    }

    public Integer getComBaudRate() {
        return comBaudRate;
    }

    public void setComBaudRate(Integer comBaudRate) {
        this.comBaudRate = comBaudRate;
    }

    public Integer getComDataBits() {
        return comDataBits;
    }

    public void setComDataBits(Integer comDataBits) {
        this.comDataBits = comDataBits;
    }

    public String getComParity() {
        return comParity;
    }

    public void setComParity(String comParity) {
        this.comParity = comParity;
    }

    public String getComStopBits() {
        return comStopBits;
    }

    public void setComStopBits(String comStopBits) {
        this.comStopBits = comStopBits;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public ProgrammSettings(){
        if(getStrParam("comnumber")==null){
            ClearBase();
        }
        this.comNumber = getStrParam("comnumber");
        this.comBaudRate = getIntParam("combaudrate");
        this.comDataBits = getIntParam("comdatabits");
        this.comParity = getStrParam("comparity");
        this.comStopBits = getStrParam("comstopbits");
        this.BotToken = getStrParam("bottoken");
        this.BotPassword = getStrParam("botpassword");
        this.timeZone = getStrParam("timezone");
    }
}
