package com.vp6.anish.stow;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
//import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity {

    ProgressBar progressBar;
    public static final int PICK_DIRECTORY = 42;
    TextView textView1;
    RelativeLayout contactsync;
    Switch aSwitch;
    TextView download_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        textView1 = (TextView)findViewById(R.id.contactsyncstatus);
        download_location = (TextView)findViewById(R.id.download_location);
        download_location.setText(SessionManager.hasfolderchoosed(this));
        progressBar = (ProgressBar) findViewById(R.id.progress_circle_contacts);
        contactsync = (RelativeLayout)findViewById(R.id.contactsync);
        if (SessionManager.isSyncing(SettingsActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            textView1.setText("In Progress");
            contactsync.setClickable(false);

        }
        else
        {
            contactsync.setClickable(true);
        }
        if (SessionManager.getLastSync(this) == 0)
        {

            textView1.setText("Not Synced yet");
        }
        else
        {
            SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String format = s.format(SessionManager.getLastSync(this));
            Log.i("Sync date", SessionManager.getLastSync(this)+"");
            textView1.setText("Last Sync "+ format);

        }
      //  TextView textView = (TextView)findViewById(R.id.recordlocation);
        //textView.setText(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pcc/");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


       // aSwitch = (Switch)findViewById(R.id.switch1);
    //    aSwitch.setChecked(SessionManager.isRecordingOn(SettingsActivity.this));
//        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//      //          SessionManager.setIsRecordingOn(SettingsActivity.this,isChecked);
//                if(Build.VERSION.SDK_INT >= 23 && isChecked)
//                { getPermissionToReadPhoneState();}
//
//            }
//        });
    }


    public void sync(View v)
    {

        if(hasInternetAccess(this)) {
            contactsync.setClickable(false);
            textView1.setText("In Progress");
            if (Build.VERSION.SDK_INT >= 23) {
                getPermissionToReadUserContacts();
            }

            //  final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_circle_contacts);
            progressBar.setVisibility(View.VISIBLE);

            SessionManager.setIsSyncing(SettingsActivity.this, true);
            final ContactsSyncAsync contactsSyncAsync = new ContactsSyncAsync(this, new ContactsSyncAsync.AsyncResponse() {
                @Override
                public void Finish(Boolean output) {

                    if (output) {
                        progressBar.setVisibility(View.GONE);
                        SessionManager.setIsSyncing(SettingsActivity.this, false);
                        SessionManager.setLastSync(SettingsActivity.this, System.currentTimeMillis());
                        Log.i("Download ", System.currentTimeMillis() + "");
                        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        String format = s.format(SessionManager.getLastSync(SettingsActivity.this));
                        Log.i("Sync date", SessionManager.getLastSync(SettingsActivity.this) + "");
                        textView1.setText("Last Sync " + format);
                        contactsync.setClickable(true);
                    } else {

                        progressBar.setVisibility(View.GONE);
                        SessionManager.setIsSyncing(SettingsActivity.this, false);
//                    SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy hh:mm");
//                    String format = s.format(new Date());
                        textView1.setText("sync failed");
                        contactsync.setClickable(true);

                        Toast.makeText(SettingsActivity.this, "Check your Internet Connection", Toast.LENGTH_LONG).show();

                    }
                }
            });
            contactsSyncAsync.execute(SessionManager.getKeyEmail(this));
        }
        else
        {
            Toast.makeText(this, "No Internet Access", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent myIntent = new Intent(SettingsActivity.this, DownloadActivity.class);
        startActivity(myIntent);
        finish();
        return true;
    }



///////////////////////////////////////to choose directory
    public  void choosedirectory(View v){
        Intent intent = new Intent(SettingsActivity.this, DirectoryPicker.class);
// optionally set options here
        startActivityForResult(intent, PICK_DIRECTORY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_DIRECTORY && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            String path = (String) extras.get(DirectoryPicker.CHOSEN_DIRECTORY);
            // do stuff with path
            SessionManager.setFolderChoosed(this, path);
            download_location.setText(path);
        }
    }
    //////////////////////////TO CHECK INTERNET CONNECTION



    public boolean hasInternetAccess(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);
            } catch (IOException e) {
                Log.e("TAG", "Error checking internet connection", e);
            }
        } else {
            Log.d("TAG", "No network available!");
        }
        return false;

    }


    //to check connection

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


















    // Identifier for the permission request
    private static final int WRITE_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static final int READ_PHONE_STATE_PERMISSIONS_REQUEST =2;

   // private static final int CAPTURE_AUDIO_PERMISSIONS_REQUEST =3;

    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_CONTACTS)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                    WRITE_CONTACTS_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == WRITE_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if(requestCode == READ_PHONE_STATE_PERMISSIONS_REQUEST)
        {       if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Phone State permission granted", Toast.LENGTH_SHORT).show();
            aSwitch.setChecked(true);
            } else {
                Toast.makeText(this, "Read Phone State permission denied", Toast.LENGTH_SHORT).show();
            aSwitch.setChecked(false);
            }
    } else {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




    }







    @TargetApi(Build.VERSION_CODES.M)
    public void getPermissionToReadPhoneState() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_PHONE_STATE)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI

                Toast.makeText(this,"READ PHONE STATE is required to record your phone calls. You can always turn it on in Settings > Application > Stow", Toast.LENGTH_LONG).show();
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_PERMISSIONS_REQUEST);
        }
    }



}



