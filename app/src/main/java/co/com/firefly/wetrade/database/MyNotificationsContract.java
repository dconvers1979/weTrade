package co.com.firefly.wetrade.database;

import android.content.ContentValues;
import android.provider.BaseColumns;

import java.io.Serializable;

/**
 * Created by toshiba on 05/08/2016.
 */
public class MyNotificationsContract implements Serializable{
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private String topics;
    private String article;

    public MyNotificationsContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "notifications";
        public static final String COLUMN_NAME_TOPIC = "topics";
        public static final String COLUMN_NAME_ARTICLE = "article";
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TOPIC, topics);
        values.put(FeedEntry.COLUMN_NAME_ARTICLE, article);
        return values;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }
}