package co.com.firefly.wetrade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.com.firefly.wetrade.model.WeTradeTopics;
import co.com.firefly.wetrade.util.WeTradeConfig;
import co.com.firefly.wetrade.viewholder.TopicViewHolder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_INVITE = 3333;

    public DatabaseReference mDatabase;
    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseRecyclerAdapter<WeTradeTopics, TopicViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private StaggeredGridLayoutManager mManager;
    private TextView notificationCount;
    private String myTopic;

    private Spinner topicSpinner;

    private List<String> spinnerData = new ArrayList<String>();

    private static final String FRIENDLY_ENGAGE_TOPIC = "friendly_engage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseMessaging.getInstance().subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO seed data
                ArrayList<WeTradeTopics> topics = new ArrayList<WeTradeTopics>();

                WeTradeTopics newItem1 = new WeTradeTopics("Herramientas",R.drawable.ic_tools, R.mipmap.ic_tools);
                WeTradeTopics newItem2 = new WeTradeTopics("Dispositivos",R.drawable.ic_devices, R.mipmap.ic_devices);
                WeTradeTopics newItem3 = new WeTradeTopics("Mascotas",R.drawable.ic_pets, R.mipmap.ic_pets);
                WeTradeTopics newItem4 = new WeTradeTopics("Motos",R.drawable.ic_motorcycle, R.mipmap.ic_cars);
                WeTradeTopics newItem5 = new WeTradeTopics("Juegos",R.drawable.ic_games, R.mipmap.ic_games);
                WeTradeTopics newItem6 = new WeTradeTopics("Impresoras",R.drawable.ic_printers, R.mipmap.ic_printer);
                WeTradeTopics newItem7 = new WeTradeTopics("Oficina",R.drawable.ic_office, R.mipmap.ic_office);
                WeTradeTopics newItem8 = new WeTradeTopics("Muebles",R.drawable.ic_furniture, R.mipmap.ic_furniture);

                topics.add(newItem1);
                topics.add(newItem2);
                topics.add(newItem3);
                topics.add(newItem4);
                topics.add(newItem5);
                topics.add(newItem6);
                topics.add(newItem7);
                topics.add(newItem8);

                for(WeTradeTopics item: topics ){
                    newEquityTopic(item);
                }

            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView userLoggedEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_logged_email);

        TextView userLoggedName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_logged_name);
        ImageView userLoggedPhoto = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_logged_photo);
        notificationCount = (TextView) navigationView.getMenu().findItem(R.id.nav_notifications).getActionView().findViewById(R.id.actionbar_notifcation_textview);

        if(WeTradeConfig.getInstance().getAccount()!=null){
            userLoggedName.setText(WeTradeConfig.getInstance().getAccount().getDisplayName());
            userLoggedEmail.setText(WeTradeConfig.getInstance().getAccount().getEmail());

            if(WeTradeConfig.getInstance().getAccount().getPhotoUrl()!=null){
                userLoggedPhoto.setImageURI(WeTradeConfig.getInstance().getAccount().getPhotoUrl());
            }

        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = (RecyclerView) findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(100);
        itemAnimator.setRemoveDuration(100);
        mRecycler.setItemAnimator(itemAnimator);

        // Set up Layout Manager, reverse layout
        mManager = new StaggeredGridLayoutManager(WeTradeConfig.getInstance().getSpanCount(),StaggeredGridLayoutManager.VERTICAL);
        mManager.setReverseLayout(false);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query topicsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<WeTradeTopics, TopicViewHolder>(WeTradeTopics.class, R.layout.item_topics,
                TopicViewHolder.class, topicsQuery) {

            @Override
            protected void populateViewHolder(final TopicViewHolder viewHolder, final WeTradeTopics model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ArticlesListActivity.class);
                        intent.putExtra(ArticlesListActivity.TOPIC_NAME, model);
                        intent.putExtra(ArticlesListActivity.TOPIC_KEY, postKey);
                        startActivity(intent);
                    }
                });

                spinnerData.add(model.getTopicName());

                viewHolder.bindToTopic(model);

                myTopic = model.getTopicName();

            }
        };
        mRecycler.setAdapter(mAdapter);

        // Initialize Firebase Measurement.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        notificationCount.setText(String.valueOf(WeTradeConfig.getInstance().getNotificationCount(this)));

        SharedPreferences settings = getSharedPreferences(WeTradeConfig.PREFERENCES, MODE_PRIVATE);

        settings.registerOnSharedPreferenceChangeListener(this);

    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

        notificationCount.setText(String.valueOf(WeTradeConfig.getInstance().getNotificationCount(this)));
    }

    public void myArticlesDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(R.layout.menu_topics);

        final AlertDialog dialog = builder.create();

        dialog.setCancelable(false);

        dialog.show();

        topicSpinner = (Spinner) dialog.findViewById(R.id.topics_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinnerData);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        topicSpinner.setAdapter(adapter);

        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.this.myTopic = parent.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button search = (Button) dialog.findViewById(R.id.topics_spinner_ok);
        Button cancel = (Button) dialog.findViewById(R.id.topics_spinner_cancel);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MainActivity.this, MyArticlesActivity.class);
                intent.putExtra(MyArticlesActivity.TOPIC_KEY,MainActivity.this.myTopic);

                startActivity(intent);

                dialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public void myArticlesDialogSuscription(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(R.layout.menu_config);

        final AlertDialog dialog = builder.create();

        dialog.setCancelable(false);

        dialog.show();

        topicSpinner = (Spinner) dialog.findViewById(R.id.topics_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinnerData);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        topicSpinner.setAdapter(adapter);

        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.this.myTopic = parent.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button search = (Button) dialog.findViewById(R.id.topics_spinner_ok);
        Button cancel = (Button) dialog.findViewById(R.id.topics_spinner_cancel);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseMessaging.getInstance().subscribeToTopic(MainActivity.this.myTopic);

                dialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery(DatabaseReference databaseReference){
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("topics");

        // [END recent_posts_query]

        return recentPostsQuery;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(WeTradeConfig.getInstance().getSpanCount()==1){
                WeTradeConfig.getInstance().setSpanCount(2);
            }else{
                WeTradeConfig.getInstance().setSpanCount(1);
            }

            mManager.setSpanCount(WeTradeConfig.getInstance().getSpanCount());

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_mypost) {

            myArticlesDialog();

        } else if (id == R.id.nav_notifications) {
            Intent intent = new Intent(this,NotificationsListingActivity.class);

            startActivity(intent);
        } else if (id == R.id.nav_chat) {
            Intent intent = new Intent(this, MyChatListActivity.class);

            startActivity(intent);
        } else if (id == R.id.nav_config) {
            myArticlesDialogSuscription();
        } else if (id == R.id.nav_invite) {
            sendInvitation();
        } else if (id == R.id.nav_about) {
            showAboutDialog();
        } else if (id == R.id.nav_signout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // [START signOut]
    private void signOut() {
        WeTradeConfig.getInstance().getmGoogleApiClient().disconnect();
        updateUI(true);

    }
    // [END signOut]


    private void updateUI(boolean signedIn) {
        if (signedIn) {
            finishAffinity();
        }
    }

    private void showAboutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("weTrade");
        builder.setMessage("Firefly development version 1.0.1");
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    //TODO seed data

    public void newEquityTopic(final WeTradeTopics topic){

        final String userId = getUid();

        mDatabase.child("topics").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value

                        writeNewTopic(topic);


                        // Finish this Activity, back to the stream
                        //finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        /*Toast.makeText(StockListingActivity.this,
                                "getUser:onCancelled" + databaseError.toException(),
                                Toast.LENGTH_SHORT).show();*/
                    }
                });
        // [END single_value_read]

    }

    // [START write_fan_out]
    public void writeNewTopic(WeTradeTopics topic) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        //String key = mDatabase.child("topics").push().getKey(); TODO
        Map<String, Object> equityValues = topic.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/topics/" + key, equityValues); TODO
        childUpdates.put("/topics/" + topic.getTopicName(), equityValues); // TODO

        mDatabase.updateChildren(childUpdates);
    }

    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Use Firebase Measurement to log that invitation was sent.
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");

                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Use Firebase Measurement to log that invitation was not sent
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, payload);

                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }

}
