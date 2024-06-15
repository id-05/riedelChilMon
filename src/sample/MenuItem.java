package sample;

public class MenuItem {
    public String visibleName;
    public String firstTag;
    public String secondTag;

    public String getVisibleName() {
        return visibleName;
    }

    public String getFirstTag() {
        return firstTag;
    }

    public String getSecondTag() {
        return secondTag;
    }

    MenuItem(String visibleName, String firstTag, String  secondTag){
        this.visibleName = visibleName;
        this.firstTag = firstTag;
        this.secondTag = secondTag;
    }
}
