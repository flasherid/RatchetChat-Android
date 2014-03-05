package com.scotttherobot.ratchet;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by scott on 3/2/14.
 */
public class MessagesAdapter extends ArrayAdapter<Message> {

    public static class ViewHolder {
        TextView body;
        ImageView leftImage;
        ImageView rightImage;
        TextView senderLabelLeft;
        TextView senderLabelRight;
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.body.setText(message.body);
        viewHolder.rightImage.setImageResource(R.drawable.circle_mask);
        viewHolder.leftImage.setImageResource(R.drawable.circle_mask);

        if (ApiClient.userId.compareTo(message.userid) == 0) {
            viewHolder.rightImage.setBackgroundResource(R.drawable.chat);
            viewHolder.leftImage.getLayoutParams().width = 0;
            viewHolder.senderLabelLeft.getLayoutParams().width = 0;
            viewHolder.body.setGravity(Gravity.RIGHT);
        } else {
            viewHolder.leftImage.setBackgroundResource(R.drawable.chat);
            viewHolder.rightImage.getLayoutParams().width = 0;
            viewHolder.body.setGravity(Gravity.LEFT);
            viewHolder.senderLabelLeft.setText(message.username);
            viewHolder.senderLabelRight.getLayoutParams().width = 0;
        }

        return convertView;
    }
}
