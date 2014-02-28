package com.scotttherobot.ratchet;

import android.content.Context;
import android.content.res.Resources;
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
                ((ImageView) view.findViewById(id)).setImageResource(Integer.parseInt(data));
                break;
            case R.id.rightImage:
                ((ImageView) view.findViewById(id)).setImageResource(Integer.parseInt(data));
                break;
            case R.id.messageBody:
                ((TextView)view.findViewById(id)).setText(data);
                break;
        }
        return true;
    }
}
