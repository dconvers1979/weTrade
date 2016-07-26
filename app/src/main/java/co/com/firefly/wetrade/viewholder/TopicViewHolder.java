package co.com.firefly.wetrade.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.com.firefly.wetrade.R;
import co.com.firefly.wetrade.model.WeTradeTopics;

/**
 * Created by toshiba on 25/07/2016.
 */
public class TopicViewHolder extends RecyclerView.ViewHolder{

    private TextView topicName;
    private ImageView topicImage;
    private WeTradeTopics topic;

    public TopicViewHolder(View itemView){
        super(itemView);
        topicName = (TextView) itemView.findViewById(R.id.topic_name);
        topicImage = (ImageView) itemView.findViewById(R.id.topic_image);

    }

    public void bindToTopic(WeTradeTopics topic) {
        topicName.setText(topic.getTopicName());
        topicImage.setImageResource(topic.getImageSRC());
    }

    public TextView getTopicName() {
        return topicName;
    }

    public void setTopicName(TextView topicName) {
        this.topicName = topicName;
    }

    public WeTradeTopics getTopic() {
        return topic;
    }

    public void setTopic(WeTradeTopics topic) {
        this.topic = topic;
    }

    public ImageView getTopicImage() {
        return topicImage;
    }

    public void setTopicImage(ImageView topicImage) {
        this.topicImage = topicImage;
    }
}
