package co.com.firefly.wetrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

import co.com.firefly.wetrade.viewholder.ArticleViewHolder;
import co.com.firefly.wetrade.viewholder.MyArticlesChatViewHolder;

public class MyArticlesChatActivity extends AppCompatActivity {

    public static final String MESSAGES_CHILD = "messages";
    public static final String ARTICLE_UID = "articleKEY";
    public static final String TOPIC_KEY = "TopicKey";
    private String articleKey;
    private String buyerUID;
    private String topicKey;

    public DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Object, MyArticlesChatViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private StaggeredGridLayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_articles_chat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.articleKey = (String) extras.getSerializable(ARTICLE_UID);
            this.topicKey = (String) extras.getSerializable(TOPIC_KEY);
            //equityNameDetail.setText(this.equity.getEquity()+" - "+this.equity.getValue());
        }else{
            finish();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = (RecyclerView) findViewById(R.id.my_articles_chat_list);
        mRecycler.setHasFixedSize(true);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(100);
        itemAnimator.setRemoveDuration(100);
        mRecycler.setItemAnimator(itemAnimator);

        // Set up Layout Manager, reverse layout
        mManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mManager.setReverseLayout(false);
        //mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Object, MyArticlesChatViewHolder>(Object.class, R.layout.item_my_articles_chat,
                MyArticlesChatViewHolder.class, postsQuery) {

            @Override
            protected void populateViewHolder(final MyArticlesChatViewHolder viewHolder, final Object model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MyArticlesChatActivity.this, ChatActivity.class);

                        intent.putExtra(ChatActivity.ARTICLE_UID,articleKey);
                        intent.putExtra(ChatActivity.BUYER_UID,postKey);
                        intent.putExtra(ChatActivity.TOPIC_KEY,topicKey);

                        MyArticlesChatActivity.this.startActivity(intent);

                    }
                });

                viewHolder.bindToArticleChat(postKey);

            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery(DatabaseReference databaseReference){
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("topics").child(topicKey).child("articles").child(articleKey).child("messages");

        // [END recent_posts_query]

        return recentPostsQuery;
    }
}
