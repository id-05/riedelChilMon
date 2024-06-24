package sample.units;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ChillerState {
    public Double Tpi;
    public Double Tpo;
    public Double Tsi;
    public Double Tso;
    public String errors;
    public String Fpo;
    public String date;

    ProgrammSettings progSet = new ProgrammSettings();

    public Double getTpi() {
        return Tpi;
    }

    public void setTpi(Double tpi) {
        Tpi = tpi;
    }

    public Double getTpo() {
        return Tpo;
    }

    public void setTpo(Double tpo) {
        Tpo = tpo;
    }

    public Double getTsi() {
        return Tsi;
    }

    public void setTsi(Double tsi) {
        Tsi = tsi;
    }

    public Double getTso() {
        return Tso;
    }

    public void setTso(Double tso) {
        Tso = tso;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public String getFpo() {
        return Fpo;
    }

    public void setFpo(String fpo) {
        Fpo = fpo;
    }

    public String getDate() {
        Instant instant = Instant.ofEpochMilli(Long.parseLong(date));
        ZonedDateTime z = instant.atZone(ZoneId.of(progSet.getTimeZone()));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm dd/MM/yyyy");
        return fmt.format(z);
    }

    public ChillerState(String data) {
        Tpi = Double.parseDouble((data.substring(data.indexOf("Tpi:"))).substring(4,(data.substring(data.indexOf("Tpi:"))).indexOf(" ")))/10;
        Tpo = Double.parseDouble((data.substring(data.indexOf("Tpo:"))).substring(4,(data.substring(data.indexOf("Tpo:"))).indexOf(" ")))/10;
        Tsi = Double.parseDouble((data.substring(data.indexOf("Tsi:"))).substring(4,(data.substring(data.indexOf("Tsi:"))).indexOf(" ")))/10;
        Tso = Double.parseDouble((data.substring(data.indexOf("Tso:"))).substring(4,(data.substring(data.indexOf("Tso:"))).indexOf(" ")))/10;
        Fpo = (data.substring(data.indexOf("Fpo:"))).substring(4,(data.substring(data.indexOf("Fpo:"))).indexOf(" "));
        errors = data.substring(data.indexOf("F:")+2,data.indexOf("F:")+21);
        date = data.substring(0,data.indexOf(" "));
    }
}