package com.scotttherobot.ratchet;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MessageThreadActivity extends Activity {

    int threadId;
    String threadName;
    String cacheFile;
    ArrayList<Message> messageList = new ArrayList<Message>();
    ListView list;
    MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_thread);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        Intent thisIntent = getIntent();
        threadId = thisIntent.getIntExtra("threadid", 0);
        threadName = thisIntent.getStringExtra("threadname");
        messageList = new ArrayList<Message>();

        registerReceiver(broadcastReceiver, new IntentFilter(GcmIntentService.BROADCAST_ACTION));

        //addDataToList();

        cacheFile = "thread_1_" + ApiClient.userId + "_" + threadId + ".dat";

        setTitle(threadName);

        ApiClient.setContext(getApplicationContext());
        ApiClient.getCredentials();

        //restoreData();
        //addDataToList();
        fetchAndShowProgress();
    }

    public void fetchAndShowProgress() {
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Getting messages.");
        progress.setMessage("Hold, please.");
        progress.show();
        Log.v("THREAD", "Fetching new stuff.");
        getThreadData();
        progress.dismiss();
    }

    @Override
    public void onNewIntent(Intent thisIntent) {
        threadId = thisIntent.getIntExtra("threadid", 0);
        threadName = thisIntent.getStringExtra("threadname");
        messageList.clear();
        setTitle(threadName);
        fetchAndShowProgress();
        Log.e("THREAD", "onNewIntent called " + threadId + " " + threadName);
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
    }

    @Override
    public void onStop() {
        super.onStop();
        //unregisterReceiver(broadcastReceiver);
        //finish();
    }

    public void restoreData() {
        Context c = getApplicationContext();
        try {
            File inFile = new File(Environment.getExternalStorageDirectory(), cacheFile);
            ObjectInput in = new ObjectInputStream(new FileInputStream(inFile));
            messageList = (ArrayList) in.readObject();
            Log.v("THREAD", "Successfully read data.");
        } catch (Exception e) {
            Log.e("THREAD", "Error reading data.", e);
        }

    }
    public void persistData() {
        Context c = getApplicationContext();
        try {
            File saveFile = new File(Environment.getExternalStorageDirectory(), cacheFile);
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(saveFile));
            out.writeObject(messageList);
            Log.v("THREAD", "Successfully wrote data.");
        } catch (Exception e) {
             Log.e("THREAD", "Error saving data.", e);
         }

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("THREAD", "Received broadcast. Updating data.");
           // int id = intent.getIntExtra("threadid", 0);
            //if (id == threadId)
                getThreadData();
        }
    };

    public void getThreadData() {

        RequestParams p = new RequestParams();
        if (messageList.isEmpty()) {
            Log.v("THREAD", "Empty. Getting all..");
            p.put("since", 0);
        } else {
            Log.v("THREAD", "Getting last one.");
            // Get the timestamp on the oldest one.
            Message message = messageList.get(messageList.size() - 1);
            p.put("since", message.sent);
        }

        ApiClient.get("threads/" + this.threadId, p, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                //Log.d("THREAD", "Response: " + response.toString());
                try {
                    JSONArray messages = response.getJSONArray("transcript");
                    messageList.addAll(Message.fromJson(messages));
                    addDataToList();
                    //persistData();

                } catch (Exception e) {
                    Log.e("LIST", "Error retrieving messages from response.");
                }
            }
        });
    }

    public void addDataToList() {
        // Assign the data to the list.
        list = (ListView)findViewById(R.id.messageList);
        messagesAdapter = new MessagesAdapter(this, messageList);
        ListAdapter adapter = messagesAdapter;
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            }
        });
        list.setSelection(adapter.getCount() - 1);
    }

    public void sendMessage(View v) {
        EditText messageInput = (EditText)findViewById(R.id.messageInput);
        RequestParams p = new RequestParams();
        p.put("body", messageInput.getText().toString());

        ApiClient.post("threads/" + this.threadId, p, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                getThreadData();
            }
        });
        messageInput.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.message_thread, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.refreshButton:
                // Refresh the data.
                getThreadData();
                return true;
            case R.id.leaveButton:
                // Leave the thread.
                leaveThread();
                return true;
            case R.id.renameButton:
                // rename the thread
                renameAlert("Rename", "New thread name");
                return true;
            case R.id.silenceButton:
                // toggle notifications
                return true;
            case R.id.inviteUser:
                inviteUser("Invite", "Username");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void inviteUser(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        final EditText input = new EditText(this);
        input.setHint(message);
        alert.setView(input);
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                String name = input.getText().toString();
                if (name.trim().isEmpty()) {
                    showAlert("Error", "You must provide a name.");
                    return;
                }
                invite(name);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    public void invite(String name) {
        RequestParams p = new RequestParams();
        p.add("user", name);
        ApiClient.post("threads/" + this.threadId + "/joinbyname", p, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getJSONArray("errors").length() != 0) {
                        // There was an error! Throw an alert.
                        showAlert("Error", response.getJSONArray("errors").getString(0));
                    } else {
                        showAlert("User added", "Successfully added " + response.getJSONObject("added").getString("username"));
                    }
                } catch (Exception e) {
                    Log.e("LIST", "Error retrieving messages from response.");
                }
            }
        });
    }

    public void leaveThread() {
        final int threadid = threadId;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You will not be able to see this thread unless you are added again.")
                .setTitle("Are you sure?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        ApiClient.delete("threads/" + threadid, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                finish();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void rename(String name) {
        RequestParams p = new RequestParams();
        p.add("name", name);
        ApiClient.post("threads/" + this.threadId + "/rename", p, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getJSONArray("errors").length() != 0) {
                        // There was an error! Throw an alert.
                        showAlert("Error", response.getJSONArray("errors").getString(0));
                    } else {
                        setTitle(response.getString("name"));
                        getThreadData();
                    }
                } catch (Exception e) {
                    Log.e("LIST", "Error retrieving messages from response.");
                }
            }
        });
    }

    public void renameAlert(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        final EditText input = new EditText(this);
        input.setHint(message);
        alert.setView(input);
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                String name = input.getText().toString();
                if (name.trim().isEmpty()) {
                    showAlert("Error", "You must provide a new name.");
                    return;
                }
                rename(name);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
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
            View rootView = inflater.inflate(R.layout.fragment_message_thread, container, false);
            return rootView;
        }
    }

}
