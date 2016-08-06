package co.com.firefly.wetrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import co.com.firefly.wetrade.model.WeTradeArticle;
import co.com.firefly.wetrade.util.WeTradeConfig;
import co.com.firefly.wetrade.viewholder.MyArticleViewHolder;

public class MyArticlesActivity extends AppCompatActivity {

    public static final String TOPIC_KEY = "topicKey";
    private String topicKey;

    private AdView mAdView;

    public DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<WeTradeArticle, MyArticleViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private StaggeredGridLayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_articles);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.topicKey = (String) extras.getSerializable(TOPIC_KEY);
        }else{
            finish();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = (RecyclerView) findViewById(R.id.articles_list);
        mRecycler.setHasFixedSize(true);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(100);
        itemAnimator.setRemoveDuration(100);
        mRecycler.setItemAnimator(itemAnimator);

        // Set up Layout Manager, reverse layout
        mManager = new StaggeredGridLayoutManager(WeTradeConfig.getInstance().getSpanCount(),StaggeredGridLayoutManager.VERTICAL);
        mManager.setReverseLayout(false);
        //mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<WeTradeArticle, MyArticleViewHolder>(WeTradeArticle.class, R.layout.item_articles,
                MyArticleViewHolder.class, postsQuery) {

            @Override
            protected void populateViewHolder(final MyArticleViewHolder viewHolder, final WeTradeArticle model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Intent intent = new Intent(MainActivity.this, ArticlesListActivity.class);
                        //intent.putExtra(ArticlesListActivity.TOPIC_NAME, model);
                        //startActivity(intent); TODO go to new article

                    }
                });

                viewHolder.bindToArticle(model, postKey, getUid(), topicKey);

            }
        };
        mRecycler.setAdapter(mAdapter);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery(DatabaseReference databaseReference){
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("topics").child(topicKey).child("articles").orderByChild("sellerId").equalTo(getUid());

        // [END recent_posts_query]

        return recentPostsQuery;
    }


}
