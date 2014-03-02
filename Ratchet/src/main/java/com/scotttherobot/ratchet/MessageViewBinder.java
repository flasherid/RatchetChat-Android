package com.scotttherobot.ratchet;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by scott on 2/27/14.
 */
class MessageViewBinder implements SimpleAdapter.ViewBinder {
    public boolean setViewValue(View view, Object inputData, String textRepresentation) {
        int id = view.getId();
        String data;
        final float scale = view.getContext().getResources().getDisplayMetrics().density;
        switch (id) {
            case R.id.leftImage:
                data = (String)inputData;
                Integer leftId = Integer.parseInt(data);
                ImageView leftBox = (ImageView) view.findViewById(id);
                if (leftId == 0) {
                    leftBox.getLayoutParams().width = 0;
                } else {
                    leftBox.setImageResource(R.drawable.circle_mask);
                    leftBox.setBackgroundResource(leftId);
                    leftBox.getLayoutParams().width =  (int) (75 * scale + 0.5f);
                }
                break;
            case R.id.rightImage:
                data = (String)inputData;
                Integer rightId = Integer.parseInt(data);
                ImageView rightBox = (ImageView) view.findViewById(id);
                if (rightId == 0) {
                    rightBox.getLayoutParams().width = 0;
                } else {
                    rightBox.setImageResource(R.drawable.circle_mask);
                    rightBox.setBackgroundResource(rightId);
                    rightBox.getLayoutParams().width = (int) (75 * scale + 0.5f);
                }
                break;
            case R.id.messageBody:
                ArrayList<String> body = (ArrayList<String>)inputData;
                TextView message = (TextView)view.findViewById(id);
                message.setText(body.get(1));
                if (body.get(0).compareTo("left") == 0) {
                    message.setGravity(Gravity.LEFT);
                } else {
                    message.setGravity(Gravity.RIGHT);
                }
                break;
        }
        return true;
    }
}
