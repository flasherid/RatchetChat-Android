package com.scotttherobot.ratchet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
        //getMenuInflater().inflate(R.menu.thread_list, menu);
        Log.e("PROFILE", "onCreateOptionsMenu Activity");
        return true;
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
            setHasOptionsMenu(true);
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

            /*
            MenuItem editProfileButton = (MenuItem)rootView.findViewById(R.id.editProfile);
            MenuItem newImageButton = (MenuItem)rootView.findViewById(R.id.uploadImage);
            // This user is NOT the logged in user. Hide the menu things.
            int userid = Integer.parseInt(ApiClient.userId);
            if (userid == thisUser.id) {
                editProfileButton.setVisible(true);
                newImageButton.setVisible(true);
            }
            */

            username.setText(thisUser.username);

            if (thisUser.distance >= .5) {
                distance.setText(thisUser.distance + " mi.");
            } else {
                distance.setText(Utils.milesToFeet(thisUser.distance) + " ft.");
            }

            ImageLoader.getInstance().displayImage(imageUrl, image);

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.profile_view, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.

            switch(item.getItemId()) {
                case R.id.uploadImage:
                    return true;
                case R.id.editProfile:
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
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
