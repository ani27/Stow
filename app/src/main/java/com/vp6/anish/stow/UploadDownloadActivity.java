package com.vp6.anish.stow;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadDownloadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_download);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
           getSupportActionBar().setTitle("Stow");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        File sampleDir = Environment.getExternalStorageDirectory();
        File dir = new File(sampleDir.getAbsolutePath() + "/Stow/");
        if (!dir.exists())

        {
            dir.mkdir();
           Toast.makeText(this,"Stow folder created", Toast.LENGTH_SHORT);
            // Log.i("pcc folder created", dir.getAbsolutePath());
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header  = navigationView.getHeaderView(0);
        TextView email = (TextView)header.findViewById(R.id.useremail);
        TextView name = (TextView)header.findViewById(R.id.username);
        email.setText(SessionManager.getKeyEmail(this));
        name.setText(SessionManager.getUserFirstName(this) + " " + SessionManager.getUserLastName(this));

        if(Build.VERSION.SDK_INT >= 23){
            getPermissionToWriteExternalStorage();

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public void download(View v){
        Intent intent = new Intent (UploadDownloadActivity.this, DownloadActivity.class);
        startActivity(intent);
    }

    public void upload(View v){
        Intent intent = new Intent (UploadDownloadActivity.this, GroupUploadActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.upload_download, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_group) {
            // Handle the camera action
            if(hasInternetAccess(UploadDownloadActivity.this)) {
                Intent intent = new Intent(UploadDownloadActivity.this, GroupActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(this, "No Internet Access", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_logout) {

            SessionManager.setIsUserLogin(this, false);
            Intent intent = new Intent(UploadDownloadActivity.this, LoginActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {

            Intent intent = new Intent(UploadDownloadActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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







    private static final int WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST =1;
    //private static final int RECORD_AUDIO_PERMISSIONS_REQUEST =2;
    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    public void getPermissionToWriteExternalStorage() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
                Toast.makeText(this,"Storage permission is required to save data in your phone. You can always turn it off/on in Settings > Application > Stow", Toast.LENGTH_LONG).show();

            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST);
        }
        else
        {

            File sampleDir = Environment.getExternalStorageDirectory();
            File dir = new File(sampleDir.getAbsolutePath() + "/Stow/");
            if (!dir.exists())

            {
                dir.mkdir();
                Log.i(" folder created", dir.getAbsolutePath());
            }
        }
    }







    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission granted", Toast.LENGTH_SHORT).show();



            } else {
                Toast.makeText(this, "Storage permission denied. It is required to download files", Toast.LENGTH_SHORT).show();
            }
        }


        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}



