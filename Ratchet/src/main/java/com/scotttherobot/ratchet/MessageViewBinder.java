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

/**
 * Created by scott on 2/27/14.
 */
class MessageViewBinder implements SimpleAdapter.ViewBinder {
    public boolean setViewValue(View view, Object inputData, String textRepresentation) {
        int id = view.getId();
        String data = (String)inputData;
        switch (id) {
            case R.id.leftImage:
                Integer leftId = Integer.parseInt(data);
                ImageView leftBox = (ImageView) view.findViewById(id);
                if (leftId == 0) {
                    leftBox.getLayoutParams().width = 0;
                } else {
                    leftBox.setImageResource(leftId);
                }
                break;
            case R.id.rightImage:
                Integer rightId = Integer.parseInt(data);
                ImageView rightBox = (ImageView) view.findViewById(id);
                if (rightId == 0) {
                    rightBox.getLayoutParams().width = 0;
                } else {
                    rightBox.setImageResource(rightId);
                }
                break;
            case R.id.messageBody:
                ((TextView)view.findViewById(id)).setText(data);
                break;
        }
        return true;
    }
}
