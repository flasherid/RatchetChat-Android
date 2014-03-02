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
public class ThreadsAdapter extends ArrayAdapter<Thread> {

    public static class ViewHolder {
        TextView name;
    }

    public ThreadsAdapter(Context context, ArrayList<Thread> threads) {
        super(context, R.layout.threadlist_item, threads);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Thread thread = getItem(position);

        ViewHolder viewHolder;
        if (true || convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.threadlist_item, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.threadName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(thread.name);
        return convertView;
    }
}