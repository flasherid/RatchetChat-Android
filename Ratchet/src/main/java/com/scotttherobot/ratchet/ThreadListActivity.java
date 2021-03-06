package com.scotttherobot.ratchet;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ThreadListActivity extends Activity {

    ArrayList<Thread> threadList = new ArrayList<Thread>();
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_list);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        setTitle("Messages");
        threadList = new ArrayList<Thread>();

        registerReceiver(broadcastReceiver, new IntentFilter(GcmIntentService.BROADCAST_ACTION));

        ApiClient.setContext(getApplicationContext());
        getThreadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        // persistData();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GcmIntentService.BROADCAST_ACTION));
        getThreadData();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("THREAD", "Received broadcast.");
        }
    };

    /*
    @Override
    protected void onResume() {
       // getThreadData();
    }
    */

    public void getThreadData() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Getting threads.");
        progress.setMessage("Hold, please.");
        progress.show();

        ApiClient.get("threads/", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                //Log.d("LIST", "Response: " + response.toString());
                try {
                    JSONArray tests = response.getJSONArray("threads");
                    threadList.clear();
                    threadList.addAll(Thread.fromJson(tests));
                    addDataToList();
                    progress.dismiss();
                } catch (Exception e) {
                    Log.e("LIST", "Error retrieving threads from response.");
                }
            }
        });
    }

    public void addDataToList() {
        // Assign the data to the list.
        list = (ListView)findViewById(R.id.threadList);
        ThreadsAdapter ta = new ThreadsAdapter(this, threadList);
        ListAdapter adapter = ta;
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent messageThreadIntent = new Intent(getApplicationContext(), MessageThreadActivity.class);
                messageThreadIntent.putExtra("threadid", threadList.get(+position).id);
                messageThreadIntent.putExtra("threadname", threadList.get(+position).name);
                //messageThreadIntent.putExtra("autologin", "");
                //messageThreadIntent.putExtra("thread", threadList.get(+position));
                startActivity(messageThreadIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.thread_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.refreshButton:
                getThreadData();
                return true;
            case R.id.logoutButton:
                ApiClient.clearCredentials();
                finish();
                return true;
            case R.id.newThreadButton:
                Intent i = new Intent(getApplicationContext(), CreateThreadActivity.class);
                startActivity(i);
                return true;
            case R.id.nearbyUsers:
                Intent n = new Intent(getApplicationContext(), NearbyUsersActivity.class);
                startActivity(n);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            View rootView = inflater.inflate(R.layout.fragment_thread_list, container, false);
            return rootView;
        }
    }

}
