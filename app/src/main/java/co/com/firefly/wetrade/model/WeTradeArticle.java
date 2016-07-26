package co.com.firefly.wetrade.model;

import java.io.Serializable;

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

    public WeTradeArticle(){

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
}
