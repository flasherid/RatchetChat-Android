package com.scotttherobot.ratchet;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by scottvanderlind on 3/13/14.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 234440404;

    public String username;
    public String firstname;
    public String lastname;
    public String email;
    public String about;
    public String avatarSrc;
    public double latitude;
    public double longitude;
    public double distance;
    public int avatarId;
    public int id;


    public User(JSONObject object) {
        try {
            this.username = object.getString("username");
            this.firstname = object.getString("firstname");
            this.lastname = object.getString("lastname");
            this.email = object.getString("email");
            this.about = object.getString("about");
            this.avatarSrc = object.getString("src");
            this.id = Integer.parseInt(object.getString("userid"));
            this.avatarId = Integer.parseInt(object.getString("avatar"));
            this.latitude = Double.parseDouble(object.getString("latitude"));
            this.longitude = Double.parseDouble(object.getString("longitude"));
            this.distance = Double.parseDouble(object.getString("distance"));
        } catch (JSONException e) {
            Log.e("USER", "There was an issue instantiating!");
        }
    }

    public static ArrayList<User> fromJson(JSONArray jsonObjects) {
        ArrayList<User> messages = new ArrayList<User>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                messages.add(new User(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                Log.e("USER", "There was an issue converting to JSON");
            }
        }
        return messages;
    }

}
