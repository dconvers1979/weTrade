package co.com.firefly.wetrade.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by toshiba on 25/07/2016.
 */
public class WeTradeTopics implements Serializable{

    private String topicName;
    private int imageSRC;
    private int imageBG;
    private Map<String, WeTradeArticle> articles = new HashMap<>();

    public WeTradeTopics(){

    }

    public WeTradeTopics(String topicName, int imageSRC, int imageBG){
        this.topicName = topicName;
        this.imageSRC = imageSRC;
        this.imageBG = imageBG;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("topicName", topicName);
        result.put("imageSRC", imageSRC);
        result.put("imageBG", imageBG);
        result.put("articles", articles);

        return result;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getImageSRC() {
        return imageSRC;
    }

    public void setImageSRC(int imageSRC) {
        this.imageSRC = imageSRC;
    }

    public Map<String, WeTradeArticle> getArticles() {
        return articles;
    }

    public void setArticles(Map<String, WeTradeArticle> articles) {
        this.articles = articles;
    }

    public int getImageBG() {
        return imageBG;
    }

    public void setImageBG(int imageBG) {
        this.imageBG = imageBG;
    }
}
