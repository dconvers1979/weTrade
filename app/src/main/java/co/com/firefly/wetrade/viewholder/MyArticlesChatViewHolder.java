package co.com.firefly.wetrade.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.com.firefly.wetrade.R;

/**
 * Created by toshiba on 28/07/2016.
 */
public class MyArticlesChatViewHolder extends RecyclerView.ViewHolder{

    private TextView buyerUID;

    public MyArticlesChatViewHolder(View itemView){
        super(itemView);
        buyerUID = (TextView) itemView.findViewById(R.id.my_articles_chat_buyeruid);

    }

    public void bindToArticleChat(String buyerUID) {
        this.buyerUID.setText(buyerUID);

    }
}
