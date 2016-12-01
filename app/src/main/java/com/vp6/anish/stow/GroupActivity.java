package com.vp6.anish.stow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import net.sourceforge.jtds.jdbc.*;
import net.sourceforge.jtds.jdbc.Driver;

import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

     //   public void onLongItemClick(View view, int position);
    }
    ArrayList<String>photoaddress;
    ArrayList<String>fileaddress;
    ArrayList<String>musicaddress;
ArrayList<ArrayList> teams;
    ArrayList<ArrayList> members;
    ArrayList<Integer> teamid;
    ArrayList<String> teamname;
    GroupUploadProgressAdapter groupUploadProgressAdapter;
    ArrayList<String> membername;
    ArrayList<String> memberemail;
    SlidingPaneLayout mSlidingPanel;
    private OnItemClickListener mlistner;
    MemberAdapter memberAdapter;
    ArrayList<String> filename;
    ArrayList<String> filetype;
    ImageButton imageButton;
    ArrayList<Boolean>fileuploaded;
    ArrayList<ArrayList> files;
 RecyclerView recyclerView;
    Context mcontext;
    ArrayAdapter<String> adapter;
    Spinner spinner;
    ListView listView;
     FloatingActionButton fab;
    Toolbar toolbar;
    int autoid;
    private String m_Text = "";
    String item_teamname = "";
    public BottomSheetBehavior mBottomSheetBehavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        teams=new ArrayList<>();

        imageButton = (ImageButton)findViewById(R.id.addfolder);
        imageButton.setTag("2");
        members=new ArrayList<>();
        mcontext = GroupActivity.this;
        getSupportActionBar().setTitle("Groups");
        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_group);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(autoid ==0)
                {
                    addteam();
                }
                else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    fab.setVisibility(View.GONE);
                }
            }
        });
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback(){
            @Override
        public void onStateChanged(View BottomSheet, int newState)
            {

               if( newState==BottomSheetBehavior.STATE_COLLAPSED)
               {
                   fab.setVisibility(View.VISIBLE);
               }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                // React to dragging events

            }
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        };

        teamname=new ArrayList<>();
        teamid= new ArrayList<>();


        memberemail = new ArrayList<>();
        membername = new ArrayList<>();
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        final OnItemClickListener onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i("inside not if", groupUploadProgressAdapter.getfiletype(position));
                String Foldername = groupUploadProgressAdapter.getfilename(position);
                if(groupUploadProgressAdapter.getfiletype(position).equals("folder"))
                {

                    getSupportActionBar().setTitle(item_teamname+">"+Foldername);
                    final  ProgressBar progressBar = (ProgressBar)findViewById(R.id.group_upload);
                    final  TextView textView = (TextView)findViewById(R.id.textView_progress);


                    fab.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);

                    imageButton.setImageResource(R.drawable.returnfolder);
                    imageButton.setTag("1");

                    GroupFileAsync groupFileAsync =(GroupFileAsync) new GroupFileAsync(GroupActivity.this, new GroupFileAsync.AsyncResponse() {
                        @Override
                        public void process(ArrayList<ArrayList> files) {

                            filename.clear();
                            filetype.clear();
                            fileuploaded.clear();
                            filename = files.get(0);
                            filetype = files.get(1);
                            fileuploaded =files.get(2);
                            groupUploadProgressAdapter = new GroupUploadProgressAdapter(GroupActivity.this, filename, filetype,fileuploaded);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager( mcontext, 2);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());

                            recyclerView.setAdapter(groupUploadProgressAdapter);
                            recyclerView.setVisibility(View.VISIBLE);

                            progressBar.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                        }
                    }).execute(Foldername,autoid+"");


                }
            }
        };
                            recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                               @Override
                               public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                               View childView = rv.findChildViewUnder(e.getX(), e.getY());
//                               if (childView != null ) {
//                               onItemClickListener.onItemClick(childView,rv.getChildAdapterPosition(childView));
//                               Log.i("Folder, click","Done");
//                               return true;
//                               }
//                             return false;
                                   return true;
                            }


                              @Override
                              public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                                  if(e.equals(MotionEvent.ACTION_UP))
                                  {
                                      Toast.makeText(GroupActivity.this,"Clicked", Toast.LENGTH_LONG).show();
                                  }

                              }
                                @Override
                               public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                                }
});



        if(hasInternetAccess(this)) {

            GroupTeamAsync groupTeamAsync = new GroupTeamAsync(this, new GroupTeamAsync.AsyncResponse() {
                @Override
                public void teamlist(ArrayList<ArrayList> teamlist) {
                  teams = teamlist;
                    teamname=teams.get(0);
                    teamid=teams.get(1);
                    Log.i("email",SessionManager.getKeyEmail(mcontext));

                    adapter = new ArrayAdapter<String>(mcontext, R.layout.support_simple_spinner_dropdown_item, teamname);


                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                    spinner.setAdapter(adapter);
                }
            });
            groupTeamAsync.execute(SessionManager.getKeyEmail(mcontext));
           // teams = checkaccount(SessionManager.getKeyEmail(this));

        }
        else
        {
            Toast.makeText(this, "Check your Internet Connection", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(GroupActivity.this, UploadDownloadActivity.class);
            startActivity(intent);
            finish();
        }


    }




    /////////////////////ADD NEW FOLDER FUNCTION
    public void backtoparent(View v)
    {

        if(imageButton.getTag()=="1")
         {
            Log.i("imagebutton", "working");
            getSupportActionBar().setTitle(item_teamname);
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.group_upload);
            final TextView textView = (TextView) findViewById(R.id.textView_progress);


            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            fab.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            imageButton.setImageResource(R.drawable.addfolder);
             imageButton.setTag("2");

            GroupFileAsync groupFileAsync = (GroupFileAsync) new GroupFileAsync(GroupActivity.this, new GroupFileAsync.AsyncResponse() {
                @Override
                public void process(ArrayList<ArrayList> files) {

                    filename.clear();
                    filetype.clear();
                    fileuploaded.clear();
                    filename = files.get(0);
                    filetype = files.get(1);
                    fileuploaded = files.get(2);
                    groupUploadProgressAdapter = new GroupUploadProgressAdapter(GroupActivity.this, filename, filetype, fileuploaded);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mcontext, 2);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    recyclerView.setAdapter(groupUploadProgressAdapter);
                    Log.i("here", "after adapter");
                    progressBar.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    Log.i("here", "after progressbargone");
                    recyclerView.setVisibility(View.VISIBLE);
                    Log.i("here", "after recycler");

                }
            }).execute("stowstartfolder", autoid + "");

        }
        else
        {

            if(hasInternetAccess(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Create New Folder");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        int j=0;
                        for( int i=0 ; i<filetype.size(); i++)
                        {
                            if(filename.get(i).equals(m_Text) && filetype.get(i).equals("folder"))
                            {
                                j=100;
                            }
                        }
                        if(j==0) {
                            GroupAddTeamAsync groupAddTeamAsync = new GroupAddTeamAsync(GroupActivity.this,"folder",groupUploadProgressAdapter);
                            groupAddTeamAsync.execute(m_Text, SessionManager.getKeyEmail(GroupActivity.this), autoid + "", filename.size() + "");

                            groupUploadProgressAdapter.insertfolder(filename.size(),m_Text);


                         //   startActivity(getIntent());
//                            finish();
                            //  GroupActivity.this.finish();
                            Log.i("Folder", "Created");
                        }
                        else
                        {
                            Toast.makeText(GroupActivity.this,"Try with another name. Folder with same name exist", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
            else
            {
                Toast.makeText(this,"No Internet Access",Toast.LENGTH_LONG).show();
            }
        }
    }


    ////////////////////////// FOR UPLOADING FILES



    public void movetogroupupload(View v)
    {
        Intent intent = new Intent(GroupActivity.this, GroupUploadActivity.class);
        // intent.putExtra("address", address);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                photoaddress = data.getStringArrayListExtra("photosurl");
                fileaddress = data.getStringArrayListExtra("fileurl");
                musicaddress = data.getStringArrayListExtra("musicurl");



ArrayList<String> alladdress = new ArrayList<>();
                ArrayList<Integer>position = new ArrayList<>();
                ArrayList<ArrayList>completedata = new ArrayList<>();
                String address = getSupportActionBar().getTitle().toString();
                String foldername="stowstartfolder";
                int count=0;
                int index=0;
                for(int i=0; i<address.length(); i++)
                {
                    if(address.charAt(i) == '>')
                    {
                        count++;
                        index=i;
                    }
                }

                if(count>1)
                {
                        foldername = address.substring(index+1, address.length());

                }

                int pos = filename.size();
                position.add(pos);
                for(int i=0;  i<photoaddress.size(); i++)
                {
                    Log.i("Address", photoaddress.get(i));

                    String name = photoaddress.get(i);
                    int previousindex = 0;
                    for(int j=0; j<name.length(); j++)
                    {
                        if(name.charAt(j) == '/' && j > previousindex)
                        {
                            previousindex =j;
                        }
                    }
                    name =  name.substring(previousindex+1,name.length());
                   // filename.add(name);
                    groupUploadProgressAdapter.insert(filename.size() , name);
                    alladdress.add(photoaddress.get(i));

                }

                for(int i=0;  i<fileaddress.size(); i++)
                {
                    Log.i("Address", fileaddress.get(i));
                    String name = fileaddress.get(i);
                    int previousindex = 0;
                    for(int j=0; i<name.length(); j++)
                    {
                        if(name.charAt(j) == '/' && i > previousindex)
                        {
                            previousindex =i;
                        }
                    }
                    name =  name.substring(previousindex+1,name.length());
                 //   filename.add(name);
                    groupUploadProgressAdapter.insert(filename.size() , name);
                    alladdress.add(fileaddress.get(i));

                }
                for(int i=0;  i<musicaddress.size(); i++)
                {
                    Log.i("Address", musicaddress.get(i));
                    String name = musicaddress.get(i);
                    int previousindex = 0;
                    for(int j=0; i<name.length(); j++)
                    {
                        if(name.charAt(j) == '/' && i > previousindex)
                        {
                            previousindex =i;
                        }
                    }
                    name =  name.substring(previousindex+1,name.length());
                   groupUploadProgressAdapter.insert(filename.size(), name);

                    alladdress.add(musicaddress.get(i));
                }

                completedata.add(alladdress);

                completedata.add(position);
                GroupUploadAsync groupUploadAsync = new GroupUploadAsync(this,recyclerView, groupUploadProgressAdapter);
                groupUploadAsync.execute(completedata);




            }
        }
    }



////////////////////////////FOR MAKING NEW TEAMS
public void createteam(View v)
{
    addteam();
}

    public void addteam(){

        if(hasInternetAccess(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Create Group");

// Set up the input
            final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();

                    int j=0;
                    for( int i=0 ; i<teamname.size(); i++)
                    {
                        if(teamname.get(i).equals(m_Text))
                        {
                            j=100;
                        }
                    }
                    if(j==0) {
                        GroupAddTeamAsync groupAddTeamAsync = new GroupAddTeamAsync(GroupActivity.this,"team", new GroupAddTeamAsync.returnid(){
                            @Override
                        public  void returnid(String id)
                            {
                                teamid.add(Integer.parseInt(id));
                            }
                        });
                        groupAddTeamAsync.execute(m_Text, SessionManager.getKeyEmail(GroupActivity.this));

                        adapter.insert(m_Text, teamname.size());
                        adapter.notifyDataSetChanged();

                    //    spinner.setSelection(adapter.getPosition(m_Text));
                        //  GroupActivity.this.finish();

                      //  finish();
                    }
                    else
                    {
                        Toast.makeText(GroupActivity.this,"Try with another name. Team with same name exist", Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        else
        {
            Toast.makeText(this,"No Internet Access",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(imageButton.getTag() == "1") {

            Log.i("imagebutton", "working");
            getSupportActionBar().setTitle(item_teamname);
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.group_upload);
            final TextView textView = (TextView) findViewById(R.id.textView_progress);


            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            fab.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            imageButton.setImageResource(R.drawable.addfolder);
            imageButton.setTag("2");

            GroupFileAsync groupFileAsync = (GroupFileAsync) new GroupFileAsync(GroupActivity.this, new GroupFileAsync.AsyncResponse() {
                @Override
                public void process(ArrayList<ArrayList> files) {

                    filename.clear();
                    filetype.clear();
                    fileuploaded.clear();
                    filename = files.get(0);
                    filetype = files.get(1);
                    fileuploaded = files.get(2);
                    groupUploadProgressAdapter = new GroupUploadProgressAdapter(GroupActivity.this, filename, filetype, fileuploaded);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mcontext, 2);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                    recyclerView.setAdapter(groupUploadProgressAdapter);
                    Log.i("here", "after adapter");
                    progressBar.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    Log.i("here", "after progressbargone");
                    recyclerView.setVisibility(View.VISIBLE);
                    Log.i("here", "after recycler");

                }
            }).execute("stowstartfolder", autoid + "");


        }
        else
        {
            Intent myIntent = new Intent(GroupActivity.this, UploadDownloadActivity.class);
            startActivity(myIntent);
            finish();


        }
        return true;
    }
/////////////////////////////////ADDMEMBERS

    public void addmembers(View v)
    {
        if(hasInternetAccess(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add a Member");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setHint("Enter member mail");
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();


                        final ProgressBar progressBarcircle = (ProgressBar)findViewById(R.id.progresscirclelist);
                        progressBarcircle.setVisibility(View.VISIBLE);
                        GroupAddTeamAsync groupAddTeamAsync = (GroupAddTeamAsync) new GroupAddTeamAsync(GroupActivity.this, "members", new GroupAddTeamAsync.returnid() {
                            @Override
                            public void returnid(String output) {
                                if(output.equals(""))
                                {
                                    Log.i("output",output);
                                    progressBarcircle.setVisibility(View.GONE);
                                    Toast.makeText(mcontext, "This member has no account", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    membername.add(output);
                                    memberemail.add(m_Text);
                                    memberAdapter = new MemberAdapter(mcontext, R.layout.members_info, membername, memberemail);
                                    listView = (ListView) findViewById(R.id.MenuList);
                                    listView.setAdapter(memberAdapter);
                                    progressBarcircle.setVisibility(View.GONE);
                                }
                            }
                        }).execute(autoid + "", item_teamname, m_Text);
                       // groupUploadProgressAdapter.insertfolder(filename.size(), m_Text);
                        Log.i("Folder", "Created");
                    }


            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        else
        {
            Toast.makeText(this,"No Internet Access",Toast.LENGTH_LONG).show();
        }
    }



////////////////////////////////////SPINNER ITEM SELECTED
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final  ProgressBar progressBar = (ProgressBar)findViewById(R.id.group_upload);
        final  TextView textView = (TextView)findViewById(R.id.textView_progress);
        final  TextView textView2 = (TextView)findViewById(R.id.textView_nogroup);

        recyclerView.setVisibility(View.GONE);
        // On selecting a spinner item
        Log.i("we are at", "on item selected");
        item_teamname = parent.getItemAtPosition(position).toString();
        if(item_teamname.equals("Select your Group"))
        {

          //  fab.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Groups");


            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            textView2.setVisibility(View.VISIBLE);
        }
        else{
            Log.i("item", item_teamname);
            int index = teamname.indexOf(item_teamname);
            autoid = teamid.get(index);

            fab.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);

            Log.i("id", autoid + "");
//  members=getmembers(autoid);



            GroupFileAsync groupFileAsync =(GroupFileAsync) new GroupFileAsync(this, new GroupFileAsync.AsyncResponse() {
                @Override
                public void process(ArrayList<ArrayList> files) {

                    filename = files.get(0);
                    filetype = files.get(1);
                    fileuploaded =files.get(2);
                    groupUploadProgressAdapter = new GroupUploadProgressAdapter(GroupActivity.this, filename, filetype,fileuploaded);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager( mcontext, 2);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    getSupportActionBar().setTitle(item_teamname);
                    progressBar.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    textView2.setVisibility(View.GONE);
                    recyclerView.setAdapter(groupUploadProgressAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }).execute("stowstartfolder",autoid+"");

            GroupDataAsync groupDataAsync = (GroupDataAsync) new GroupDataAsync(this, new GroupDataAsync.AsyncResponse() {

                @Override
                public void process(ArrayList<ArrayList> members) {
                    //Here you will receive the result fired from async class
                    //of onPostExecute(result) method.
                    membername = members.get(0);
                    memberemail = members.get(1);
                    memberAdapter = new MemberAdapter(mcontext, R.layout.members_info, membername, memberemail);
                    listView = (ListView) findViewById(R.id.MenuList);
                    listView.setAdapter(memberAdapter);
                }
            }).execute(autoid);


        }


    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i("we are at", "on nothing selected");

        String item= parent.getItemAtPosition(parent.getSelectedItemPosition()).toString();
        int index = teamname.indexOf(item);
        int autoid = teamid.get(index);
//  members=getmembers(autoid);
        GroupDataAsync groupDataAsync = (GroupDataAsync) new GroupDataAsync(this,new GroupDataAsync.AsyncResponse(){

            @Override
            public void process(ArrayList<ArrayList> members){
                //Here you will receive the result fired from async class
                //of onPostExecute(result) method.

                membername= members.get(0);
                memberemail=members.get(1);
            }
        }).execute(autoid);


        MemberAdapter memberAdapter = new MemberAdapter(this,R.layout.members_info,membername,memberemail);


        ListView listView = (ListView)findViewById(R.id.MenuList);
        listView.setAdapter(memberAdapter);

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

}
