package com.scotttherobot.ratchet;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateThreadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_thread);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    public void makeThread(View v) {
        String title = ((EditText)findViewById(R.id.threadName)).getText().toString();
        String user = ((EditText)findViewById(R.id.userName)).getText().toString();

        Log.e("THREAD", "Creating thread " + title + " " + user);

        if (title.isEmpty() || user.isEmpty()) {
            showAlert("Error!", "You must set a title and specify a user.");
            return;
        }

        RequestParams p = new RequestParams();
        p.add("name", title);
        p.add("user", user);

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Creating thread...");
        progress.setMessage("Hold, please.");
        progress.show();
        ApiClient.post("threads/", p, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    progress.dismiss();
                    Log.e("NEW", response.toString());
                    if (response.getJSONArray("errors").length() != 0) {
                        // There was an error! Throw an alert.
                        showAlert("Error", response.getJSONArray("errors").getString(0));
                    } else {
                        finish();
                    }
                } catch (JSONException e) {

                }
            }
            @Override
            public void onFailure(int statusCode, java.lang.Throwable e, JSONObject errorResponse) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_thread, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_create_thread, container, false);
            return rootView;
        }
    }

}
