package com.scotttherobot.ratchet;

import android.util.Log;

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

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void login(String username, String password, loginHandler isDone) {
        loginDone = isDone;
        Log.v("API", "Attempting login.");

        RequestParams p = new RequestParams();
        p.put("username", username);
        p.put("password", password);

        post("login", p, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                //Log.v("API", "Response: " + response.toString());
                try {
                    Log.v("API", "Login success");
                    SESSION = response.getString("key");
                    userId = response.getString("userid");
                }
                catch (Exception e) {
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
        // TODO: Set the session key in the http header
        client.addHeader("cards-key", SESSION);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("cards-key", SESSION);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
