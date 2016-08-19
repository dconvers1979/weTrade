package co.com.firefly.wetrade.viewholder;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import co.com.firefly.wetrade.ArticleDetailActivity;
import co.com.firefly.wetrade.ChatActivity;
import co.com.firefly.wetrade.MyArticlesChatActivity;
import co.com.firefly.wetrade.R;
import co.com.firefly.wetrade.database.MyChatsContract;
import co.com.firefly.wetrade.database.WeTradeDbHelper;
import co.com.firefly.wetrade.model.WeTradeArticle;

/**
 * Created by toshiba on 25/07/2016.
 */
public class MyArticleViewHolder extends RecyclerView.ViewHolder{

    private WeTradeArticle article;
    private TextView name;
    private TextView price;
    private TextView currency;
    private ImageView articleImage;
    private Toolbar toolbar;
    private View view;

    public MyArticleViewHolder(View itemView){
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.article_name);
        price = (TextView) itemView.findViewById(R.id.article_price);
        currency = (TextView) itemView.findViewById(R.id.article_currency);
        articleImage = (ImageView) itemView.findViewById(R.id.article_image);
        toolbar = (Toolbar) itemView.findViewById(R.id.article_menu);
        view = itemView;
    }

    public void bindToArticle(final WeTradeArticle article, final String articleKey, final String buyerId, final String topicKey) {
        name.setText(article.getName());
        price.setText(article.getPrice());
        currency.setText(article.getCurrency());

        toolbar.inflateMenu(R.menu.article_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item){

                int id = item.getItemId();

                if(id == R.id.articles_menu_detail){
                    Intent intent = new Intent(view.getContext(), ArticleDetailActivity.class);
                    intent.putExtra(ArticleDetailActivity.ARTICLE_TAG, articleKey);
                    intent.putExtra(ArticleDetailActivity.TOPIC_TAG, topicKey);
                    intent.putExtra(ArticleDetailActivity.RENDER_CHAT_TAG, false);

                    view.getContext().startActivity(intent);
                } else if(id == R.id.articles_menu_chat){
                    Intent intent = new Intent(view.getContext(), MyArticlesChatActivity.class);

                    intent.putExtra(MyArticlesChatActivity.ARTICLE_UID,articleKey);
                    intent.putExtra(MyArticlesChatActivity.TOPIC_KEY,topicKey);

                    view.getContext().startActivity(intent);
                }

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

}
