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
public class Message implements Serializable {

    private static final long serialVersionUID = 696969;
    public String username;
    public String sent;
    public String userid;
    public String body;

    public Message(JSONObject object) {
        try {
            this.userid = object.getString("userid");
            this.username = object.getString("username");
            this.sent = object.getString("sent");
            this.userid = object.getString("userid");
            this.body = object.getString("body");
        } catch (Exception e) {
            Log.e("MESSAGE", "There was an issue instantiating!");
        }
    }

    public static ArrayList<Message> fromJson(JSONArray jsonObjects) {
        ArrayList<Message> messages = new ArrayList<Message>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                messages.add(new Message(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                Log.e("MESSAGE", "There was an issue converting to JSON");
            }
        }
        return messages;
    }

}
