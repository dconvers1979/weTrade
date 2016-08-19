package co.com.firefly.wetrade;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

import co.com.firefly.wetrade.database.MyChatsContract;
import co.com.firefly.wetrade.database.WeTradeDbHelper;
import co.com.firefly.wetrade.model.WeTradeArticle;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    public static final String ARTICLE_TAG = "article";
    public static final String TOPIC_TAG = "topic";
    public static final String RENDER_CHAT_TAG = "render";
    private String articleKey;
    private String topicKey;
    private boolean renderChat;
    public DatabaseReference mDatabase;

    private ImageView detailArticleImage;
    private TextView detailArticleName;
    private TextView detailArticlePrice;
    private TextView detailArticleCurrency;
    private TextView detailArticleDescription;
    private TextView detailArticleSendingCharges;
    private Button detailArticleButton;

    public WeTradeArticle article;


    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.articleKey = (String) extras.getSerializable(ARTICLE_TAG);
            this.topicKey = (String) extras.getSerializable(TOPIC_TAG);
            this.renderChat = extras.getBoolean(RENDER_CHAT_TAG);
        }else{
            finish();
        }

        detailArticleImage = (ImageView)findViewById(R.id.detail_article_image);
        detailArticleName  = (TextView)findViewById(R.id.detail_article_name);
        detailArticlePrice = (TextView)findViewById(R.id.detail_article_price);
        detailArticleCurrency = (TextView)findViewById(R.id.detail_article_currency);
        detailArticleDescription = (TextView)findViewById(R.id.detail_article_description);
        detailArticleSendingCharges = (TextView)findViewById(R.id.detail_article_sending_charges);
        detailArticleButton = (Button)findViewById(R.id.detail_article_button);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("topics").child(topicKey).child("articles").child(articleKey).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."

                        article = (WeTradeArticle.buildFromMap((Map)snapshot.getValue()));

                        detailArticleName.setText(article.getName());
                        detailArticlePrice.setText(article.getPrice());
                        detailArticleCurrency.setText(article.getCurrency());
                        detailArticleDescription.setText(article.getDescription());
                        detailArticleSendingCharges.setText(article.getSendingCharges());

                        if(article.getImageURL()!=null && !article.getImageURL().isEmpty()){

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://wetrade-f7d03.appspot.com");

                            //ImageView imageView = (ImageView) findViewById(R.id.imgView);
                            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                            StorageReference photoRef = storageRef.child(article.getImageURL());

                            final long ONE_MEGABYTE = 1024 * 1024;
                            photoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Data for "images/island.jpg" is returns, use this as needed
                                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    detailArticleImage.setImageBitmap(bm);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });

                        }

                    }
                    @Override public void onCancelled(DatabaseError error) { }
                });

        detailArticleButton.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View view) {
                                                       Intent intent = new Intent(view.getContext(), ChatActivity.class);

                                                       intent.putExtra(ChatActivity.ARTICLE_UID,articleKey);
                                                       intent.putExtra(ChatActivity.BUYER_UID,getUid());
                                                       intent.putExtra(ChatActivity.TOPIC_KEY,topicKey);

                                                       WeTradeDbHelper helper = new WeTradeDbHelper(view.getContext());

                                                       SQLiteDatabase db = helper.getWritableDatabase();

                                                       MyChatsContract myChatsContract = new MyChatsContract();

                                                       myChatsContract.setUnread(0);
                                                       myChatsContract.setTopic(topicKey);
                                                       myChatsContract.setArticle(articleKey);
                                                       myChatsContract.setArticleName(article.getName());
                                                       myChatsContract.setChatUrl(getUid());

                                                       helper.createMyChat(db, myChatsContract);

                                                       view.getContext().startActivity(intent);
                                                   }
                                               });

        if(!renderChat){
            detailArticleButton.setVisibility(View.GONE);
        } else {
            detailArticleButton.setVisibility(View.VISIBLE);
        }

    }

    public Query getQuery(DatabaseReference databaseReference){
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("topics").child(topicKey).child("articles").child(articleKey);

        // [END recent_posts_query]

        return recentPostsQuery;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
