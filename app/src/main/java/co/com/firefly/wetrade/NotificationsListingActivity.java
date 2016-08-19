package co.com.firefly.wetrade;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.com.firefly.wetrade.database.MyNotificationsContract;
import co.com.firefly.wetrade.database.WeTradeDbHelper;
import co.com.firefly.wetrade.util.WeTradeConfig;

public class NotificationsListingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_listing);

        this.recyclerView = (RecyclerView)findViewById(R.id.my_notifications_listing);

        WeTradeDbHelper helper = new WeTradeDbHelper(this);

        SQLiteDatabase db = helper.getWritableDatabase();

        this.recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(helper.getAllNotifications(db)));

        // Set up Layout Manager, reverse layout

        RecyclerView.LayoutManager mManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);

        this.recyclerView.setLayoutManager(mManager);

    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<MyNotificationsContract> mValues;

        public SimpleItemRecyclerViewAdapter(List<MyNotificationsContract> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notifications_listing, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);

            holder.mIdView.setText(getResources().getString(R.string.notification_message_1)+" "+mValues.get(position).getTopics()+" "+getResources().getString(R.string.notification_message_2));
            if(mValues.get(position).isNewNotification()){
                holder.imageView.setImageResource(R.drawable.ic_notifications_new);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_notifications);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WeTradeDbHelper helper = new WeTradeDbHelper(NotificationsListingActivity.this);

                    if(mValues.get(position).isNewNotification()){
                        SQLiteDatabase db = helper.getWritableDatabase();
                        mValues.get(position).setNewNotification(false);

                        helper.updateMyNotification(db,mValues.get(position));

                        WeTradeConfig.getInstance().decreaseNotification(NotificationsListingActivity.this);
                    }

                    Context context = v.getContext();
                    Intent intent = new Intent(NotificationsListingActivity.this, ArticleDetailActivity.class);
                    intent.putExtra(ArticleDetailActivity.ARTICLE_TAG, holder.mItem.getArticle());
                    intent.putExtra(ArticleDetailActivity.TOPIC_TAG, holder.mItem.getTopics());
                    intent.putExtra(ArticleDetailActivity.RENDER_CHAT_TAG, true);

                    context.startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final ImageView imageView;

            public MyNotificationsContract mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.my_notifications_listing_message);
                imageView = (ImageView) view.findViewById(R.id.my_notifications_listing_image);
            }


        }
    }
}
