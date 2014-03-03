package com.scotttherobot.ratchet;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Scott Vanderlind on 2/9/14.
 */
public class ApiClient {

    public interface loginHandler {
        public void onLogin(JSONObject response);
        public void onFailure(JSONObject response);
    }
    private static loginHandler loginDone;

    private static final String BASE_URL = "http://chat.twerkwithfriends.com/0.1/";
    private static String SESSION = null;

    public static String userId;
    public static String globalUsername;
    private static String globalPassword;

    public static Context appContext;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void login(String username, String password, loginHandler isDone) {
        loginDone = isDone;
        Log.v("API", "Attempting login.");

        RequestParams p = new RequestParams();
        p.put("username", username);
        p.put("password", password);

        globalUsername = username;
        globalPassword = password;

        post("login", p, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                //Log.v("API", "Response: " + response.toString());
                try {
                    Log.v("API", "Login success: " + response.toString());
                    SESSION = response.getString("key");
                    userId = response.getString("userid");
                    saveCredentials();
                } catch (Exception e) {
                    Log.v("API", "Fetching key failed.");
                    SESSION = null;
                }
                loginDone.onLogin(response);
            }

            @Override
            public void onFailure(Throwable t, JSONObject o) {
                Log.v("API", "Login failed", t);
                loginDone.onFailure(o);
            }
        });
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("cards-key", SESSION);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("cards-key", SESSION);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("cards-key", SESSION);
        client.delete(getAbsoluteUrl(url), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static void setContext(Context c) {
        appContext = c;
    }

    public static void saveCredentials () {
        SharedPreferences pref = appContext.getSharedPreferences("chatapi", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("session", SESSION);
        editor.putString("username", globalUsername);
        editor.putString("userid", userId);
        editor.putString("password", globalPassword);
        editor.commit();
        Log.v("API", "saved saved credentials for " + globalUsername);
    }

    public static void clearCredentials() {
        SharedPreferences pref = appContext.getSharedPreferences("chatapi", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
    public static Boolean getCredentials () {
        Log.v("API", "Getting saved creds");
        SharedPreferences pref = appContext.getSharedPreferences("chatapi", Context.MODE_PRIVATE);
        SESSION = pref.getString("session", null);
        globalUsername = pref.getString("username", null);
        globalPassword = pref.getString("password", null);
        userId = pref.getString("userid", null);
        return userId != null && globalPassword != null && globalUsername != null && SESSION != null;
    }

}
