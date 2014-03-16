package com.scotttherobot.ratchet;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by scott on 3/2/14.
 */
public class MessagesAdapter extends ArrayAdapter<Message> {

    public static class ViewHolder {
        TextView body;
        ImageView leftImage;
        ImageView rightImage;
        ImageView centerImage;
        TextView senderLabelLeft;
        TextView senderLabelRight;
        LinearLayout leftLayout;
        LinearLayout rightLayout;
    }

    public MessagesAdapter(Context context, ArrayList<Message> messages) {
        super(context, R.layout.messagelist_item, messages);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        ViewHolder viewHolder;
        if (true || convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.messagelist_item, null);
            viewHolder.body = (TextView) convertView.findViewById(R.id.messageBody);
            viewHolder.leftImage = (ImageView) convertView.findViewById(R.id.leftImage);
            viewHolder.rightImage = (ImageView) convertView.findViewById(R.id.rightImage);
            viewHolder.senderLabelLeft = (TextView) convertView.findViewById(R.id.senderLabelLeft);
            viewHolder.senderLabelRight = (TextView) convertView.findViewById(R.id.senderLabelRight);
            viewHolder.centerImage = (ImageView) convertView.findViewById(R.id.middleImage);
            viewHolder.leftLayout = (LinearLayout) convertView.findViewById(R.id.leftLayout);
            viewHolder.rightLayout = (LinearLayout) convertView.findViewById(R.id.rightLayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.body.setText(message.body);
        viewHolder.rightImage.setImageResource(R.drawable.circle_mask);
        viewHolder.leftImage.setImageResource(R.drawable.circle_mask);

        String avatarUrl;
        if (message.avatarSrc.toLowerCase().contains("http://")
                || message.avatarSrc.toLowerCase().contains("https://")) {
            avatarUrl = message.avatarSrc;
        } else {
            avatarUrl = ApiClient.getUnversionedUrl() + message.avatarSrc;
        }

        if (message.mediaSrc != null) {

            String imageUrl;
            if (message.mediaSrc.toLowerCase().contains("http://")
                    || message.mediaSrc.toLowerCase().contains("https://")) {
                imageUrl = message.mediaSrc;
            } else {
                imageUrl = ApiClient.getUnversionedUrl() + message.mediaSrc;
            }
            ImageLoader.getInstance().displayImage(imageUrl, viewHolder.centerImage);
        } else {
            viewHolder.centerImage.getLayoutParams().height = 0;
        }

        if (ApiClient.userId.compareTo(message.userid) == 0) {

            if (message.avatarSrc == null) {
                viewHolder.rightImage.setBackgroundResource(R.drawable.chat);
            } else {
                ImageLoader.getInstance().displayImage(avatarUrl, viewHolder.rightImage);
            }

            viewHolder.leftImage.getLayoutParams().width = 0;
            viewHolder.senderLabelLeft.getLayoutParams().width = 0;
            viewHolder.leftLayout.getLayoutParams().width = 0;
            viewHolder.body.setGravity(Gravity.RIGHT);
            //viewHol
        } else {
            if (message.avatarSrc == null) {
                viewHolder.leftImage.setBackgroundResource(R.drawable.chat);
            } else {
                ImageLoader.getInstance().displayImage(avatarUrl, viewHolder.leftImage);
            }
            viewHolder.rightImage.getLayoutParams().width = 0;
            viewHolder.body.setGravity(Gravity.LEFT);
            viewHolder.senderLabelLeft.setText(message.username);
            viewHolder.senderLabelRight.getLayoutParams().width = 0;
            viewHolder.rightLayout.getLayoutParams().width = 0;
        }

        return convertView;
    }
}
