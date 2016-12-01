package com.vp6.anish.stow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DownloadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

  ShowUploadedFilesAsync showUploadedFilesAsync;

    ArrayList<ArrayList> files;
    ArrayList<String>foldername ;
    ArrayList<String>foldercreated ;
    ArrayList<String> folderId;
    ArrayList<String> fileId;
    ArrayList<String> filesize;
    ArrayList<String> filecreated;
    ArrayList<String> filename;
    ArrayList<Boolean> fileuploaded;
    ArrayList<String> foldersize;
    ArrayList<String> filetype;
    ProgressBar progressBar;
    FolderListAdapter folderListAdapter;
    FileListAdapter fileListAdapter;
    RecyclerView listView;
    RecyclerView recyclerView;
     TextView textfolder;
    TextView textfile;
    ImageView No_item;
    TextView Noitem;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;
    ArrayList<String>photoaddress;
    ArrayList<String>filesaddress;
    ArrayList<String>musicaddress;
    String currentFolderId = "0";
    String  currentFolderName = "Stow";
    FloatingActionButton fab;
    public BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Stow");
        textfolder = (TextView)findViewById(R.id.textfolder);
        textfile = (TextView)findViewById(R.id.textfiles);

        No_item = (ImageView)findViewById(R.id.noitemimage);
        Noitem = (TextView)findViewById(R.id.noitemtext);
        progressBar = (ProgressBar)findViewById(R.id.progresscircledownload);
        listView = (RecyclerView)findViewById(R.id.folders);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_downloads);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        files = new ArrayList<>();

        foldername = new ArrayList<>();
        foldercreated = new ArrayList<>();
        folderId = new ArrayList<>();
        fileId= new ArrayList<>();
        filesize = new ArrayList<>();
        filecreated = new ArrayList<>();
        filename= new ArrayList<>();
        fileuploaded = new ArrayList<>();
        foldersize = new ArrayList<>();
        filetype = new ArrayList<>();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);





      // final FloatingActionButton
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                fab.setVisibility(View.INVISIBLE);
            }
        });


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        };

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View BottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    fab.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                // React to dragging events

            }
        });

        openfolder("0", "Stow");

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
//        savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
//        savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }




    public void uploadfiles(View v)
    {
        Intent intent = new Intent(this, GroupUploadActivity.class);
        // intent.putExtra("address", address);
        startActivityForResult(intent, 1);
    }
    public void createfolder(View v)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Folder");
        final String[] m_Text = new String[1];

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text[0] = input.getText().toString();
                        if(hasInternetAccess(DownloadActivity.this)) {

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                            String currentDateandTime = sdf.format(new Date());
                 folderListAdapter.insert(m_Text[0],"0",currentDateandTime );
                 openfolder("-1",m_Text[0]);
                }
                else {
                            Toast.makeText(DownloadActivity.this,"Sorry, It seems like you are not connected to internet", Toast.LENGTH_SHORT).show();
                        }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                photoaddress = data.getStringArrayListExtra("photosurl");
                filesaddress = data.getStringArrayListExtra("fileurl");
                musicaddress = data.getStringArrayListExtra("musicurl");



                ArrayList<String> alladdress = new ArrayList<>();
                ArrayList<Long>allsize = new ArrayList<>();
                ArrayList<String> allname = new ArrayList<>();
                ArrayList<ArrayList>completedata = new ArrayList<>();



                int pos = filename.size();

                for(int i=0;  i<photoaddress.size(); i++)
                {
                    Log.i("Address", photoaddress.get(i));

                    File photo = new File(photoaddress.get(i));
                    String name = photo.getName();
                    long size = photo.length();
                    fileListAdapter.insert(filename.size() , name, "image", size+"",currentDateandTime);//,photoaddress.get(i));
                            alladdress.add(photoaddress.get(i));
                            allname.add(name);
                            allsize.add(size);
                }

                for(int i=0;  i<filesaddress.size(); i++)
                {
                    Log.i("Address", filesaddress.get(i));

                    File file = new File(filesaddress.get(i));
                    String name = file.getName();
                    long size = file.length();
                    //   filename.add(name);
                    fileListAdapter.insert(filename.size() , name,name.substring(name.lastIndexOf(".")),size+"",currentDateandTime);//,filesaddress.get(i));
                    alladdress.add(filesaddress.get(i));
                    allname.add(name);

                    allsize.add(size);
                }
                for(int i=0;  i<musicaddress.size(); i++)
                {
                    Log.i("Address", musicaddress.get(i));

                    File music = new File(musicaddress.get(i));
                    String name = music.getName();
                    long size = music.length();
                    fileListAdapter.insert(filename.size(), name,"music",size+"",currentDateandTime);//, musicaddress.get(i));

                    alladdress.add(musicaddress.get(i));
                    allname.add(name);
                    allsize.add(size);
                }

                completedata.add(alladdress);
                completedata.add(allsize);
                completedata.add(allname);
                UploadingAsync uploadingAsync = new UploadingAsync(this,recyclerView, fileListAdapter, pos, currentFolderId, currentFolderName,"file" );
                uploadingAsync.execute(completedata);




            }
        }
    }


    public  void firstfile(){
        No_item.setVisibility(View.GONE);
        Noitem.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        textfile.setVisibility(View.VISIBLE);


    }


    public  void firstfolder(){
        No_item.setVisibility(View.GONE);
        Noitem.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        textfolder.setVisibility(View.VISIBLE);


    }
    public  void lastfile(){
        if(foldername.size() <= 0) {
            No_item.setVisibility(View.VISIBLE);
            Noitem.setVisibility(View.VISIBLE);
        }
        textfile.setVisibility(View.GONE);


    }
    public  void lastfolder(){
        if(filename.size() <= 0) {
            No_item.setVisibility(View.VISIBLE);
            Noitem.setVisibility(View.VISIBLE);
        }
        textfolder.setVisibility(View.GONE);


    }

    public void checkclick(final int position, ImageButton button,final int type, final boolean uploaded, final String name, final String clickedId)
    {


        PopupMenu popup = new PopupMenu(DownloadActivity.this, button);
        //Inflating the Popup using xml file
        if(type == 0)
        popup.getMenuInflater().inflate(R.menu.folder_menu, popup.getMenu());
        else
        popup.getMenuInflater().inflate(R.menu.file_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.rename:
                        rename(type, name, position,clickedId);
                        break;
                    case R.id.delete:
                         delete(type, name, position,clickedId);
                        break;

                    case R.id.download:
                         download( uploaded, name, clickedId);
                        break;
                    case R.id.open:
                        openfolder(clickedId, name);
                        break;
                }
                return true;
            }
        });

        popup.show(); //showing popup menu

        //Toast.makeText(this, "Clicked "+position, Toast.LENGTH_LONG).show();
    }



    public void rename(final int type, final String oldname, final int position, final String id) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename");
        final String[] m_Text = new String[1];
        final String typevalue;
        if(type==0)
        {
            typevalue = "folders";
        }
        else {
            typevalue = "files";
        }

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(oldname);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text[0] = input.getText().toString();
                String newname = m_Text[0];
                String oldextension = oldname.substring(oldname.lastIndexOf('.') + 1);
                String newextension = newname.substring(newname.lastIndexOf('.') + 1);
                if(type != 0) {

                    if (!oldextension.equals(newextension)) {
                        newname = newname + "." + oldextension;
                    }
                }
                if(hasInternetAccess(DownloadActivity.this)) {
                    //Toast.makeText(this, "Download started in background. Check notification bar for progress", Toast.LENGTH_SHORT).show();

                    DownloadAsync downloadAsync = new DownloadAsync(DownloadActivity.this);

                    downloadAsync.execute(oldname,"rename",newname, typevalue,id,oldextension);
                    if(type == 0)
                    {
                        folderListAdapter.renamed(position,newname);
                    }
                    else
                    {
                        fileListAdapter.renamed(position, newname);
                    }
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }




    //////////// menu delete



    public void delete(final int type,final String name, final int position, final String id) {

        final String extension = name.substring(name.lastIndexOf('.')+1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
//        final String[] m_Text = new String[1];
        final String typevalue;
        if(type==0)
        {
            typevalue = "folders";
        }
        else {
            typevalue = "files";
        }
// Set up the input
        final TextView input = new TextView(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setText("Are you sure you want to delete "+ name+"?");
        builder.setView(input);


// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // m_Text[0] = input.getText().toString();
                if(hasInternetAccess(DownloadActivity.this)) {
                    //Toast.makeText(this, "Download started in background. Check notification bar for progress", Toast.LENGTH_SHORT).show();

                    DownloadAsync downloadAsync = new DownloadAsync(DownloadActivity.this);

                    downloadAsync.execute(name,"delete", typevalue, id, extension);
                    if(type == 0)
                    {
                        folderListAdapter.deletefolder(position);
                        if(folderListAdapter.getItemCount() <=0)
                        {
                            textfolder.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        fileListAdapter.deletefile(position);
                        if(fileListAdapter.getItemCount() <= 0)
                        {
                            textfile.setVisibility(View.GONE);
                        }
                    }
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }



//////////////////////menu download

public void download( boolean uploaded, String name, String id)
{
    final String extension = name.substring(name.lastIndexOf('.')+1);
                    if(hasInternetAccess(this) && uploaded) {
                    Toast.makeText(this, "Download started in background. Check notification bar for progress", Toast.LENGTH_SHORT).show();
                  //  Snackbar.make(,"Download started in background. Check notification bar for progress",Snackbar.LENGTH_LONG)
                    DownloadAsync downloadAsync = new DownloadAsync(this);
                    downloadAsync.execute(name,"download", id+"."+extension);
                }
                else if(!hasInternetAccess(this))
                {
                    Toast.makeText(this, "No Internet Access", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(this, "Let the file upload first", Toast.LENGTH_SHORT).show();
                }

}


//////////////////////open folder


    public void openfolder(String id, String name)
    {

       // mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        textfile.setVisibility(View.GONE);
        textfolder.setVisibility(View.GONE);
        listView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        No_item.setVisibility(View.INVISIBLE);
        Noitem.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        if(hasInternetAccess(this))
        {
          if(!id.equals("0"))
          {
              toggle.setDrawerIndicatorEnabled(false);
              getSupportActionBar().setTitle(name);
              getSupportActionBar().setDisplayHomeAsUpEnabled(true);
              if(!id.equals("-1")) {
                  toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          // Doesn't have to be onBackPressed
                          openfolder("0", "Stow");
                      }
                  });
              }
              else
              {
                  toggle.setToolbarNavigationClickListener(null);
              }
          }
            else
          {
              getSupportActionBar().setDisplayHomeAsUpEnabled(false);
              // Show hamburger
              getSupportActionBar().setTitle(name);
              toggle.setDrawerIndicatorEnabled(true);
              toggle.setToolbarNavigationClickListener(null);
          }
            if(!id.equals("-1")) {
                currentFolderId = id;
                currentFolderName = name;
                showUploadedFilesAsync = (ShowUploadedFilesAsync) new ShowUploadedFilesAsync(DownloadActivity.this, new ShowUploadedFilesAsync.AsyncResponse() {
                    @Override
                    public void process(ArrayList<ArrayList> Data) {


                        files = Data;
                        filename = Data.get(0);
                        filetype = Data.get(1);
                        filecreated = Data.get(2);
                        filesize = Data.get(3);
                        // fileaddress= Data.get(4);
                        foldername = Data.get(4);
                        foldercreated = Data.get(5);
                        fileuploaded = Data.get(6);
                        folderId = Data.get(7);
                        fileId = Data.get(8);
                        foldersize = Data.get(9);
                        progressBar.setVisibility(View.GONE);
                        folderListAdapter = new FolderListAdapter(DownloadActivity.this, R.layout.download_file_list, foldername, foldercreated, DownloadActivity.this, folderId,foldersize);
                        fileListAdapter = new FileListAdapter(DownloadActivity.this, filename, filetype, fileuploaded, filesize, DownloadActivity.this, fileId,filecreated);

                        RecyclerView.LayoutManager layoutManager = new linearmanager(DownloadActivity.this);
                        listView.setLayoutManager(layoutManager);
                        listView.setItemAnimator(new DefaultItemAnimator());

                        listView.setAdapter(folderListAdapter);

                        if (foldername.size() > 0) {
                            textfolder.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.VISIBLE);


                        }


                        RecyclerView.LayoutManager mLayoutManager = new gridmanager(DownloadActivity.this, 2);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(fileListAdapter);

                        if (filename.size() > 0) {
                            textfile.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);


                        } else {
                            if (foldername.size() <= 0) {
                                No_item.setVisibility(View.VISIBLE);
                                Noitem.setVisibility(View.VISIBLE);
                            }
                        }


                    }
                }).execute(SessionManager.getKeyEmail(this), id);
            }
            else
            {

                ArrayList<String>foldername_list = new ArrayList<>();
                foldername_list.add(name);
                currentFolderName = name;
                ArrayList<ArrayList>cover = new ArrayList<ArrayList>();
                cover.add(foldername_list);
                UploadingAsync uploadingAsync = (UploadingAsync)new UploadingAsync(DownloadActivity.this, name, currentFolderId, currentFolderName, folderListAdapter, "folder", new UploadingAsync.AsyncResponse() {
                    @Override
                    public void process(String id) {
                        currentFolderId = id;
                        folderListAdapter.upload_complete(id);
                        No_item.setVisibility(View.VISIBLE);
                        Noitem.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(DownloadActivity.this,"Folder Created", Toast.LENGTH_SHORT).show();
                        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openfolder("0", "Stow");
                            }
                        });

                    }
                });
                uploadingAsync.execute(cover);
            }
        }
        else

        {
            Toast.makeText(this, "No Internet Access", Toast.LENGTH_SHORT).show();

            progressBar.setVisibility(View.GONE);

        }

    }









    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
//        mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
//        mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.download, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_group) {
            // Handle the camera action
            if(hasInternetAccess(this)) {
                Intent intent = new Intent(this, GroupActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(this, "No Internet Access", Toast.LENGTH_LONG).show();
            }
        }  else if (id == R.id.nav_logout) {

            SessionManager.setIsUserLogin(this, false);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            finish();


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



















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
            catch (RuntimeException e)
            {
                Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                Log.e("Runtime", "Exception");
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




}

 class gridmanager extends  GridLayoutManager {
    public gridmanager(Context context, int spanCount) {
        super(context, spanCount);
    }

     @Override
     public boolean canScrollHorizontally(){
         return false;
     }
}


 class linearmanager extends LinearLayoutManager {

     public linearmanager(Context context) {
         super(context);
     }
     @Override
     public boolean canScrollHorizontally(){
         return false;
     }
 }
