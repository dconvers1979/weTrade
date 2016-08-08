package co.com.firefly.wetrade.database;

import android.content.ContentValues;
import android.provider.BaseColumns;

import java.io.Serializable;

/**
 * Created by toshiba on 05/08/2016.
 */
public class MyChatsContract implements Serializable {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private String topic;
    private String article;
    private String chatUrl;
    private String articleName;
    private int unread;

    public MyChatsContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "chats";
        public static final String COLUMN_NAME_TOPIC = "topics";
        public static final String COLUMN_NAME_ARTICLE = "article";
        public static final String COLUMN_NAME_CHATURL = "chat_url";
        public static final String COLUMN_NAME_ARTICLE_NAME = "article_name";
        public static final String COLUMN_NAME_UNREAD = "unread";
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TOPIC, topic);
        values.put(FeedEntry.COLUMN_NAME_ARTICLE, article);
        values.put(FeedEntry.COLUMN_NAME_CHATURL, chatUrl);
        values.put(FeedEntry.COLUMN_NAME_ARTICLE_NAME, articleName);
        values.put(FeedEntry.COLUMN_NAME_UNREAD, unread);
        return values;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getChatUrl() {
        return chatUrl;
    }

    public void setChatUrl(String chatUrl) {
        this.chatUrl = chatUrl;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }
}