package com.scotttherobot.ratchet;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by scottvanderlind on 3/13/14.
 */
public class UsersAdapter extends ArrayAdapter<User> {

    public static class ViewHolder {
        ImageView userImage;
        TextView userText;
    }

    public UsersAdapter(Context context, ArrayList<User> messages) {
        super(context, R.layout.userlist_item, messages);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User u = getItem(position);

        ViewHolder viewHolder;
        if (true || convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.userlist_item, null);

            viewHolder.userImage = (ImageView) convertView.findViewById(R.id.userImage);
            viewHolder.userText = (TextView) convertView.findViewById(R.id.userText);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // assign stuff into the things now
        viewHolder.userText.setText(u.about);

        // Check to see if it's a relative url
        String imageUrl;
        if (u.avatarSrc.toLowerCase().contains("http://")
         || u.avatarSrc.toLowerCase().contains("https://")) {
            imageUrl = u.avatarSrc;
        } else {
            imageUrl = ApiClient.getUnversionedUrl() + u.avatarSrc;
        }

        ImageLoader.getInstance().displayImage(imageUrl, viewHolder.userImage);

        return convertView;
    }
}
