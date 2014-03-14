package com.scotttherobot.ratchet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends Activity {

    /**
     * Google Cloud Messaging stuff
     */
    private static GoogleCloudMessaging gcm;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "341582368";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        ApiClient.setContext(getApplicationContext());
        final Resources res = getResources();
        if (ApiClient.getCredentials()) {

            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Logging In");
            progress.setMessage("It looks like you've been here before. Hold on while we log you in.");
            progress.show();
            ApiClient.loginWithSavedCredentials(new ApiClient.loginHandler() {
                @Override
                public void onLogin(JSONObject response) {
                    progress.dismiss();
                    gotoThreads();
                }
                @Override
                public void onFailure(JSONObject response) {
                    progress.dismiss();
                    showAlert(res.getString(R.string.loginFailed), res.getString(R.string.loginFailedMessage));
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.createAccount) {
            Intent newUserIntent = new Intent(this, UserRegistrationActivity.class);
            startActivity(newUserIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void doLogin(View view) {
        final Resources res = getResources();
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle(res.getString(R.string.loggingIn));
        progress.setMessage(res.getString(R.string.holdPlease));
        progress.show();

        String username = ((EditText)findViewById(R.id.username)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        ApiClient.login(username, password, new ApiClient.loginHandler() {
            @Override
            public void onLogin(JSONObject response) {
                progress.dismiss();
                try {
                    registerForPushNotifications();
                    gotoThreads();
                } catch (Exception e) {
                }
            }
            @Override
            public void onFailure(JSONObject response) {
                progress.dismiss();
                showAlert(res.getString(R.string.loginFailed), res.getString(R.string.loginFailedMessage));
            }
        });

    }

    public void gotoThreads() {
        Intent threadIntent = new Intent(getApplicationContext(), ThreadListActivity.class);
        startActivity(threadIntent);
        finish();
    }

    public void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            return rootView;
        }
    }

   void registerForPushNotifications() {
        Log.v("LOGIN", "Attempting to register for push");

        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
        String regid = getRegistrationId(getApplicationContext());
        if (regid.isEmpty()) {
           new RegisterBackground().execute();
        }
    }

    private void sendUuidToBackend(String uuid) {
        Log.v("LOGIN", "UUID is " + uuid);

        RequestParams p = new RequestParams();
        p.put("uuid", uuid);
        p.put("type", "GCM");

        ApiClient.post("notificationregister/", p, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.v("LOGIN", "Registration success: " + response.toString());
                storeRegistrationId(getApplicationContext(), getRegistrationId(getApplicationContext()));
            }
            @Override
            public void onFailure(Throwable t, JSONObject o) {
                Log.v("API", "Registration failed", t);
            }
        });
    }

    class RegisterBackground extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                }
                String regid = gcm.register(SENDER_ID);
                msg = regid;
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }
        @Override
        protected void onPostExecute(String msg) {
            Log.v("LOGIN", msg);
            sendUuidToBackend(msg);
        }
    }


    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i("LOGIN", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("LOGIN", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("LOGIN", "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(LoginActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

}
