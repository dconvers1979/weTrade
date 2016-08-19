package co.com.firefly.wetrade.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by toshiba on 05/08/2016.
 */
public class WeTradeDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "WeTrade.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOLEAN_TYPE = " NUMERIC";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES_CHAT =
            "CREATE TABLE " + MyChatsContract.FeedEntry.TABLE_NAME + " (" +
                    MyChatsContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    MyChatsContract.FeedEntry.COLUMN_NAME_TOPIC + TEXT_TYPE + COMMA_SEP +
                    MyChatsContract.FeedEntry.COLUMN_NAME_ARTICLE + TEXT_TYPE + COMMA_SEP +
                    MyChatsContract.FeedEntry.COLUMN_NAME_CHATURL + TEXT_TYPE + COMMA_SEP +
                    MyChatsContract.FeedEntry.COLUMN_NAME_ARTICLE_NAME + TEXT_TYPE + COMMA_SEP +
                    MyChatsContract.FeedEntry.COLUMN_NAME_UNREAD + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_ENTRIES_NOTIFICATIONS =
            "CREATE TABLE " + MyNotificationsContract.FeedEntry.TABLE_NAME + " (" +
                    MyNotificationsContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    MyNotificationsContract.FeedEntry.COLUMN_NAME_TOPIC + TEXT_TYPE + COMMA_SEP +
                    MyNotificationsContract.FeedEntry.COLUMN_NAME_ARTICLE + TEXT_TYPE + COMMA_SEP +
                    MyNotificationsContract.FeedEntry.COLUMN_NAME_NEW + BOOLEAN_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES_CHAT =
            "DROP TABLE IF EXISTS " + MyChatsContract.FeedEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_NOTIFICATIONS =
            "DROP TABLE IF EXISTS " + MyChatsContract.FeedEntry.TABLE_NAME;

    public WeTradeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_CHAT);
        db.execSQL(SQL_CREATE_ENTRIES_NOTIFICATIONS);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES_CHAT);
        db.execSQL(SQL_DELETE_ENTRIES_NOTIFICATIONS);
        onCreate(db);
    }

    public long createMyChat(SQLiteDatabase db, MyChatsContract myChatsContract){
        return db.insert(MyChatsContract.FeedEntry.TABLE_NAME,null,myChatsContract.toContentValues());
    }

    public long createMyNotification(SQLiteDatabase db, MyNotificationsContract myNotificationsContract){
        return db.insert(MyNotificationsContract.FeedEntry.TABLE_NAME,null,myNotificationsContract.toContentValues());
    }

    public void updateMyNotification(SQLiteDatabase db, MyNotificationsContract myNotificationsContract){
        db.update(MyNotificationsContract.FeedEntry.TABLE_NAME, myNotificationsContract.toContentValues(), MyNotificationsContract.FeedEntry.COLUMN_NAME_ARTICLE + "='"+myNotificationsContract.getArticle()+"'", null);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<MyChatsContract> getAllChats(SQLiteDatabase db){
        Cursor c = db.query(
                MyChatsContract.FeedEntry.TABLE_NAME,  // Nombre de la tabla
                null,  // Lista de Columnas a consultar
                null,  // Columnas para la cláusula WHERE
                null,  // Valores a comparar con las columnas del WHERE
                null,  // Agrupar con GROUP BY
                null,  // Condición HAVING para GROUP BY
                null  // Cláusula ORDER BY
        );

        ArrayList<MyChatsContract> response = new ArrayList<>();

        while(c.moveToNext()){
            MyChatsContract item = new MyChatsContract();

            item.setArticle(c.getString(c.getColumnIndex(MyChatsContract.FeedEntry.COLUMN_NAME_ARTICLE)));
            item.setChatUrl(c.getString(c.getColumnIndex(MyChatsContract.FeedEntry.COLUMN_NAME_CHATURL)));
            item.setTopic(c.getString(c.getColumnIndex(MyChatsContract.FeedEntry.COLUMN_NAME_TOPIC)));
            item.setArticleName(c.getString(c.getColumnIndex(MyChatsContract.FeedEntry.COLUMN_NAME_ARTICLE_NAME)));
            item.setUnread(c.getInt(c.getColumnIndex(MyChatsContract.FeedEntry.COLUMN_NAME_UNREAD)));

            response.add(item);
        }

        return response;
    }

    public ArrayList<MyNotificationsContract> getAllNotifications(SQLiteDatabase db){
        Cursor c = db.query(
                MyNotificationsContract.FeedEntry.TABLE_NAME,  // Nombre de la tabla
                null,  // Lista de Columnas a consultar
                null,  // Columnas para la cláusula WHERE
                null,  // Valores a comparar con las columnas del WHERE
                null,  // Agrupar con GROUP BY
                null,  // Condición HAVING para GROUP BY
                null  // Cláusula ORDER BY
                );

        ArrayList<MyNotificationsContract> response = new ArrayList<>();

        while(c.moveToNext()){
            MyNotificationsContract item = new MyNotificationsContract();

            item.setArticle(c.getString(c.getColumnIndex(MyNotificationsContract.FeedEntry.COLUMN_NAME_ARTICLE)));
            item.setTopics(c.getString(c.getColumnIndex(MyNotificationsContract.FeedEntry.COLUMN_NAME_TOPIC)));

            if(c.getInt(c.getColumnIndex(MyNotificationsContract.FeedEntry.COLUMN_NAME_NEW))==1){
                item.setNewNotification(true);
            } else {
                item.setNewNotification(false);
            }

            response.add(item);

        }

        return response;
    }


}