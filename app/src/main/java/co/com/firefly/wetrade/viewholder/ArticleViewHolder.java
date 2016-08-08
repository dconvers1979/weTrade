package co.com.firefly.wetrade.viewholder;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import co.com.firefly.wetrade.ChatActivity;
import co.com.firefly.wetrade.R;
import co.com.firefly.wetrade.database.MyChatsContract;
import co.com.firefly.wetrade.database.WeTradeDbHelper;
import co.com.firefly.wetrade.model.WeTradeArticle;
import co.com.firefly.wetrade.util.WeTradeConfig;

/**
 * Created by toshiba on 25/07/2016.
 */
public class ArticleViewHolder extends RecyclerView.ViewHolder{

    private WeTradeArticle article;
    private TextView name;
    private TextView price;
    private TextView currency;
    private TextView description;
    private TextView sendingCharges;
    private ImageView articleImage;
    private Toolbar toolbar;
    private View view;

    public ArticleViewHolder(View itemView){
        super(itemView);
        view = itemView;
        name = (TextView) itemView.findViewById(R.id.article_name);
        price = (TextView) itemView.findViewById(R.id.article_price);
        currency = (TextView) itemView.findViewById(R.id.article_currency);
        description = (TextView) itemView.findViewById(R.id.article_description);
        sendingCharges = (TextView) itemView.findViewById(R.id.article_sending_charges);
        articleImage = (ImageView) itemView.findViewById(R.id.article_image);
        toolbar = (Toolbar) itemView.findViewById(R.id.article_menu);
    }

    public void bindToArticle(final WeTradeArticle article, final String articleKey, final String buyerId, final String topicKey) {
        name.setText(article.getName());
        price.setText(article.getPrice());
        currency.setText(article.getCurrency());
        description.setText(article.getDescription());
        sendingCharges.setText(article.getSendingCharges());
        toolbar.inflateMenu(R.menu.article_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item){
                Intent intent = new Intent(view.getContext(), ChatActivity.class);

                intent.putExtra(ChatActivity.ARTICLE_UID,articleKey);
                intent.putExtra(ChatActivity.BUYER_UID,buyerId);
                intent.putExtra(ChatActivity.TOPIC_KEY,topicKey);

                WeTradeDbHelper helper = new WeTradeDbHelper(view.getContext());

                SQLiteDatabase db = helper.getWritableDatabase();

                MyChatsContract myChatsContract = new MyChatsContract();

                myChatsContract.setUnread(0);
                myChatsContract.setTopic(topicKey);
                myChatsContract.setArticle(articleKey);
                myChatsContract.setArticleName(article.getName());
                myChatsContract.setChatUrl(buyerId);

                helper.createMyChat(db, myChatsContract);

                view.getContext().startActivity(intent);

                return true;
            }
        });

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
                        articleImage.setImageBitmap(bm);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });


        }

    }

    public WeTradeArticle getArticle() {
        return article;
    }

    public void setArticle(WeTradeArticle article) {
        this.article = article;
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getPrice() {
        return price;
    }

    public void setPrice(TextView price) {
        this.price = price;
    }

    public TextView getCurrency() {
        return currency;
    }

    public void setCurrency(TextView currency) {
        this.currency = currency;
    }

    public TextView getDescription() {
        return description;
    }

    public void setDescription(TextView description) {
        this.description = description;
    }

    public TextView getSendingCharges() {
        return sendingCharges;
    }

    public void setSendingCharges(TextView sendingCharges) {
        this.sendingCharges = sendingCharges;
    }
}
