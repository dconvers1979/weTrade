package co.com.firefly.wetrade;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import co.com.firefly.wetrade.model.WeTradeArticle;
import co.com.firefly.wetrade.model.WeTradeTopics;
import co.com.firefly.wetrade.viewholder.ArticleViewHolder;

public class ArticlesListActivity extends AppCompatActivity {

    public static final String TOPIC_NAME = "topicInfo";
    public static final String TOPIC_KEY = "topicKey";
    public static final int RESULT_LOAD_IMAGE = 9999;
    private WeTradeTopics topic;
    private String topicKey;
    private WeTradeArticle newArticle;
    private AdView mAdView;
    private String filePath;

    public DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<WeTradeArticle, ArticleViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private StaggeredGridLayoutManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newArticleDialog();

            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.topic = (WeTradeTopics) extras.getSerializable(TOPIC_NAME);
            this.topicKey = (String) extras.getSerializable(TOPIC_KEY);
            //equityNameDetail.setText(this.equity.getEquity()+" - "+this.equity.getValue());
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
        mManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mManager.setReverseLayout(false);
        //mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<WeTradeArticle, ArticleViewHolder>(WeTradeArticle.class, R.layout.item_articles,
                ArticleViewHolder.class, postsQuery) {

            @Override
            protected void populateViewHolder(final ArticleViewHolder viewHolder, final WeTradeArticle model, final int position) {
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

    public void newArticleDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(R.layout.new_arcticle);

        final AlertDialog dialog = builder.create();

        dialog.setCancelable(false);

        dialog.show();

        final EditText name = (EditText) dialog.findViewById(R.id.new_article_name);
        final EditText description = (EditText) dialog.findViewById(R.id.new_article_description);
        final EditText price = (EditText) dialog.findViewById(R.id.new_article_price);
        final EditText currency = (EditText) dialog.findViewById(R.id.new_article_currency);
        final EditText charges = (EditText) dialog.findViewById(R.id.new_article_charges);

        Button create = (Button) dialog.findViewById(R.id.new_article_ok);
        Button cancel = (Button) dialog.findViewById(R.id.new_article_cancel);
        ImageButton photo = (ImageButton) dialog.findViewById(R.id.new_article_photo);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                newArticle = new WeTradeArticle();
                newArticle.setName(name.getText().toString());
                newArticle.setCurrency(currency.getText().toString());
                newArticle.setDescription(description.getText().toString());
                newArticle.setPrice(price.getText().toString());
                newArticle.setSendingCharges(charges.getText().toString());
                newArticle.setSellerId(getUid());

                createNewArticle(FirebaseDatabase.getInstance().getReference().child("topics").child(topicKey));

                //ArticlesListActivity.this.mAdapter.notifyDataSetChanged();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();

            //ImageView imageView = (ImageView) findViewById(R.id.imgView);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public Query getQuery(DatabaseReference databaseReference){
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys
        Query recentPostsQuery = databaseReference.child("topics").child(topicKey).child("articles");

        // [END recent_posts_query]

        return recentPostsQuery;
    }

    // [START post_stars_transaction]
    public void createNewArticle(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {


                WeTradeTopics p = mutableData.getValue(WeTradeTopics.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                String key = mDatabase.child("topics").child("articles").push().getKey();

                newArticle.setImageURL(key+".jpg");

                // Star the post and add self to stars
                p.getArticles().put(key, newArticle);


                // Set value and report transaction success
                mutableData.setValue(p);

                String fileName = key+".jpg";

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://wetrade-f7d03.appspot.com");

                //ImageView imageView = (ImageView) findViewById(R.id.imgView);
                //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                StorageReference photoRef = storageRef.child(fileName);

                Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                byte[] data = baos.toByteArray();

                UploadTask uploadTask = photoRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                });

                bitmap.recycle();
                bitmap = null;

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                /*Toast.makeText(StockListingActivity.this, "postTransaction:onComplete:" + databaseError,
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }
    // [END post_stars_transaction]

}
