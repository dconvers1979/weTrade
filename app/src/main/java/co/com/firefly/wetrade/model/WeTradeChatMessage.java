package co.com.firefly.wetrade.model;

/**
 * Created by toshiba on 26/07/2016.
 */
public class WeTradeChatMessage {

    private String id;
    private String text;
    private String name;
    private String photoUrl;

    public WeTradeChatMessage() {
    }

    public WeTradeChatMessage(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
