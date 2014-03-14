package com.scotttherobot.ratchet;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NearbyUsersActivity extends Activity {

    ArrayList<User> nearby = new ArrayList<User>();
    GridView userGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_users);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        setTitle("Nearby");

        ApiClient.setContext(getApplicationContext());

        getNearbyUsers();

    }

    private void getNearbyUsers() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Getting threads.");
        progress.setMessage("Hold, please.");
        progress.show();

        RequestParams rp = new RequestParams();
        rp.add("latitude", "35.274658");
        rp.add("longitude", "-120.662781");

        ApiClient.get("location/nearby", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                //Log.d("LIST", "Response: " + response.toString());
                try {
                    JSONArray users = response.getJSONArray("nearby");
                    nearby.clear();
                    nearby.addAll(User.fromJson(users));
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
        userGrid = (GridView)findViewById(R.id.userGrid);
        UsersAdapter ua = new UsersAdapter(this, nearby);
        //ListAdapter adapter = ua;
        userGrid.setAdapter(ua);
        userGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent userProfileIntent = new Intent(getApplicationContext(), ProfileViewActivity.class);
                //Bundle b = new Bundle();
                //b.putSerializable("user", nearby.get(position));
                userProfileIntent.putExtra("user", nearby.get(position));
                startActivity(userProfileIntent);
                //Log.v("NEARBY", "CLICKED:");
                //Log.v("NEARBY", nearby.get(position).username);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nearby_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.refreshButton:
                getNearbyUsers();
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
            View rootView = inflater.inflate(R.layout.fragment_nearby_users, container, false);
            return rootView;
        }
    }

}
