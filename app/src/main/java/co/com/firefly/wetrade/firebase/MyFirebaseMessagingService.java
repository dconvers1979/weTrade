package co.com.firefly.wetrade.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import co.com.firefly.wetrade.ChatActivity;
import co.com.firefly.wetrade.LoginActivity;
import co.com.firefly.wetrade.MainActivity;
import co.com.firefly.wetrade.R;
import co.com.firefly.wetrade.database.MyChatsContract;
import co.com.firefly.wetrade.database.MyNotificationsContract;
import co.com.firefly.wetrade.database.WeTradeDbHelper;
import co.com.firefly.wetrade.util.WeTradeConfig;

/**
 * Created by toshiba on 26/07/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageSent(String msgID) {
        super.onMessageSent(msgID);
    }

    @Override
    public void onSendError(String msgID, Exception exception) {
        super.onSendError(msgID, exception);
        exception.printStackTrace();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        /*Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " + remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());*/

        Map<String, String> extraData = remoteMessage.getData();

        WeTradeDbHelper helper = new WeTradeDbHelper(this.getBaseContext());

        SQLiteDatabase db = helper.getWritableDatabase();

        MyNotificationsContract myNotificationsContract = new MyNotificationsContract();

        myNotificationsContract.setTopics(extraData.get("topic"));
        myNotificationsContract.setArticle(extraData.get("article"));
        myNotificationsContract.setNewNotification(true);

        WeTradeConfig.getInstance().increaseNotification(this.getBaseContext());

        helper.createMyNotification(db, myNotificationsContract);

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle("Nuevo articulo en " + extraData.get("topic"));
        notificationBuilder.setContentText("Nuevo articulo de tu interes");
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}