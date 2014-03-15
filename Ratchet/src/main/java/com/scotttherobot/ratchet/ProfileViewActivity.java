package com.scotttherobot.ratchet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileViewActivity extends Activity {

    User thisUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //showUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public User thisUser;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile_view, container, false);

            ApiClient.setContext(getActivity().getApplicationContext());

            Intent i = getActivity().getIntent();
            thisUser = (User)i.getExtras().get("user");

            String imageUrl;
            if (thisUser.avatarSrc.toLowerCase().contains("http://")
                    || thisUser.avatarSrc.toLowerCase().contains("https://")) {
                imageUrl = thisUser.avatarSrc;
            } else {
                imageUrl = ApiClient.getUnversionedUrl() + thisUser.avatarSrc;
            }
            TextView username = (TextView)rootView.findViewById(R.id.userUsername);
            TextView distance = (TextView)rootView.findViewById(R.id.distanceFromMe);
            ImageView image = (ImageView)rootView.findViewById(R.id.userImage);

            username.setText(thisUser.username);
            distance.setText(thisUser.distance + " mi.");
            ImageLoader.getInstance().displayImage(imageUrl, image);

            return rootView;
        }

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

}