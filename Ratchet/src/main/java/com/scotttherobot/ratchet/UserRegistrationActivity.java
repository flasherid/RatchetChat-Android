package com.scotttherobot.ratchet;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserRegistrationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    public void registerUser(View v) {
        final Resources res = getResources();
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle(res.getString(R.string.creatingAccount));
        progress.setMessage(res.getString(R.string.holdPlease));
        progress.show();

        String firstname = ((EditText)findViewById(R.id.firstname)).getText().toString().trim();
        String lastname = ((EditText)findViewById(R.id.lastname)).getText().toString().trim();
        String username = ((EditText)findViewById(R.id.username)).getText().toString().trim();
        String password = ((EditText)findViewById(R.id.password)).getText().toString().trim();
        String email = ((EditText)findViewById(R.id.email)).getText().toString().trim();

        RequestParams p = new RequestParams();
        p.add("firstname", firstname);
        p.add("lastname", lastname);
        p.add("username", username);
        p.add("password", password);
        p.add("email", email);

        ApiClient.post("register/", p, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                progress.dismiss();
                try {
                    Log.e("REG", response.toString());
                    if (response.getJSONArray("errors").length() != 0) {
                        JSONArray errors = response.getJSONArray("errors");
                        String er = errors.join("\n");
                        showAlert(res.getString(R.string.regFailure), res.getString(R.string.regFailureMessage) + "\n\n" + er);
                    } else {
                        String username = response.getString("username");
                        showAlertAndFinish(res.getString(R.string.success), res.getString(R.string.resSuccessMessage) + " " + username);
                    }
                } catch (JSONException e) {

                }
            }
            @Override
            public void onFailure(Throwable t, JSONObject o) {
                Log.e("REG", o.toString());
                progress.dismiss();
                showAlert(res.getString(R.string.loginFailed), res.getString(R.string.networkFailedMessage));
            }
        });


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
    public void showAlertAndFinish(String title, String message) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            View rootView = inflater.inflate(R.layout.fragment_user_registration, container, false);
            return rootView;
        }
    }

}
