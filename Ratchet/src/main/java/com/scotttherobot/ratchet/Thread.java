package com.scotttherobot.ratchet;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by scott on 3/2/14.
 */
public class Thread implements Serializable {

    private static final long serialVersionUID = 1211551451;
    public String name;
    public int id;
    public String status;
    public int userid;
    public String notifications;
    public int joined;
    public int image;

    public Thread(JSONObject object) {
        try {
            this.name = object.getString("name");
            this.id = Integer.parseInt(object.getString("threadid"));
            this.status = object.getString("status");
            this.userid = Integer.parseInt(object.getString("userid"));
            this.notifications = object.getString("notifications");
            this.joined = Integer.parseInt(object.getString("joined"));
            this.image = R.drawable.chat;
        } catch (JSONException e) {
            Log.e("THREAD_OBJ", "There was an issue instantiating!");
        }
    }

    public static ArrayList<Thread> fromJson(JSONArray jsonObjects) {
        ArrayList<Thread> messages = new ArrayList<Thread>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                messages.add(new Thread(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                Log.e("MESSAGE", "There was an issue converting to JSON");
            }
        }
        return messages;
    }

}
