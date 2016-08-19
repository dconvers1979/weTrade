package co.com.firefly.wetrade.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by toshiba on 25/07/2016.
 */
public class WeTradeArticle implements Serializable{

    private String name;
    private String sellerId;
    private String price;
    private String currency;
    private String description;
    private String sendingCharges;
    private String imageURL;
    private boolean notificationEnabled;
    private String location;

    public WeTradeArticle(){

    }

    @Exclude
    public static WeTradeArticle buildFromMap(Map values){
        WeTradeArticle result = new WeTradeArticle();
        result.setName(String.valueOf(values.get("name")));
        result.setSellerId(String.valueOf(values.get("sellerId")));
        result.setPrice(String.valueOf(values.get("price")));
        result.setCurrency(String.valueOf(values.get("currency")));
        result.setDescription(String.valueOf(values.get("description")));
        result.setSendingCharges(String.valueOf(values.get("sendingCharges")));
        result.setImageURL(String.valueOf(values.get("imageURL")));
        result.setLocation(String.valueOf(values.get("location")));
        return result;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("sellerId", sellerId);
        result.put("price", price);
        result.put("currency", currency);
        result.put("description", description);
        result.put("sendingCharges", sendingCharges);
        result.put("imageURL", imageURL);
        result.put("location", location);

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSendingCharges() {
        return sendingCharges;
    }

    public void setSendingCharges(String sendingCharges) {
        this.sendingCharges = sendingCharges;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
