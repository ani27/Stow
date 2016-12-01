package com.vp6.anish.stow;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by anish on 10-07-2016.
 */
public class GroupAddTeamAsync extends AsyncTask<String, String , String> {


   // public AsyncResponse delegate = null;
    public Context mcontext;
    public ProgressDialog progressDialog;
    public String type;
    public GroupUploadProgressAdapter groupUploadProgressAdapter;

    public interface returnid{
        void returnid(String output);
    }

    public  returnid delegate= null;
    public GroupAddTeamAsync(Context cntext, String type,returnid delegate){
        this.delegate = delegate;
        this.mcontext = cntext;
        progressDialog = new ProgressDialog(mcontext, R.style.MyMaterialTheme);
        this.type = type;
    }


    public GroupAddTeamAsync(Context cntext, String type, GroupUploadProgressAdapter groupUploadProgressAdapter){
        // this.delegate = delegate;
        this.mcontext = cntext;
        progressDialog = new ProgressDialog(mcontext, R.style.MyMaterialTheme);
        this.type = type;
        this.groupUploadProgressAdapter = groupUploadProgressAdapter;
    }
    @Override
    protected void onPreExecute() {
        //set message of the dialog


        if(type.equals("team")) {
            progressDialog.setMessage("Adding your group to server....");
            progressDialog.setIndeterminate(true);
            progressDialog.show();

        }

      //  progressDialog.setMessage("Adding folder to your group....");


        super.onPreExecute();
    }



    @Override
    protected void onPostExecute(String result) {

        if(type =="team")
        progressDialog.dismiss();


       super.onPostExecute(result);
        if(delegate!=null)
       delegate.returnid(result);

    }

    @Override
    protected String doInBackground(String... teamname) {
String id="";
        if(type.equals("team"))
        id = addteam(teamname[0], teamname[1]);
        else if(type.equals("folder"))
        {

         id=   addfolder(teamname[0],teamname[1],teamname[2],teamname[3]);
        }
        else if(type.equals("members"))
        {

            id = addmember(teamname[0],teamname[1],teamname[2]);
        }
     //   return null;
        return id;
    }


    @Override
    protected void onProgressUpdate(String... params) {
        groupUploadProgressAdapter.uploadcomplete(Integer.parseInt(params[0]));
    }

        public String addteam(String teamname, String adminmail) {

        String id="0";

        Log.i("Android", " addteam.");
        Connection conn = null;
        try {
            String driver = "net.sourceforge.jtds.jdbc.Driver";
            Class.forName(driver).newInstance();
            String connString = "jdbc:jtds:sqlserver://208.91.198.59/dbstow;encrypt=true;user=vp6stow;password=Vp6@technology123;instance=SQLEXPRESS;";
            String username = "vp6stow";
            String password = "Vp6@technology123";
            conn = DriverManager.getConnection(connString, username, password);
            Log.w("Connection", "open");

            Statement stmt = conn.createStatement();
            Log.i("abve", "driver8");
             stmt.execute("INSERT INTO dbo.estoreteams (name,admin) VALUES ('" + teamname + "','" + adminmail + "')");
Log.i("add team", "inserted");
             ResultSet idset  = stmt.executeQuery("SELECT * FROM dbo.estoreteams WHERE name='"+teamname+"' AND admin ='"+adminmail+"'");
            while(idset.next())
            {
                id = idset.getString(1);
            }
            stmt.execute("INSERT INTO dbo.teammembers VALUES ('" + id + "','" + adminmail + "','" + teamname + "')");



            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }
            return id;

    }




    public String addfolder(String foldername, String membermail, String teamid, String position) {

      //  String id="0";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Log.i("Android", " addteam.");
        Connection conn = null;
        try {
            String driver = "net.sourceforge.jtds.jdbc.Driver";
            Class.forName(driver).newInstance();
            String connString = "jdbc:jtds:sqlserver://208.91.198.59/dbstow;encrypt=true;user=vp6stow;password=Vp6@technology123;instance=SQLEXPRESS;";
            String username = "vp6stow";
            String password = "Vp6@technology123";
            conn = DriverManager.getConnection(connString, username, password);
            Log.w("Connection", "open");

            Statement stmt = conn.createStatement();
            Log.i("abve", "driver8");
//            stmt.execute("INSERT INTO dbo.estoreteams (name,admin) VALUES ('" + teamname + "','" + adminmail + "')");
//            Log.i("add team", "inserted");
//            ResultSet idset  = stmt.executeQuery("SELECT * FROM dbo.estoreteams WHERE name='"+teamname+"' AND admin ='"+adminmail+"'");
//            while(idset.next())
//            {
//                id = idset.getString(1);
//            }
//            stmt.execute("INSERT INTO dbo.teammembers VALUES ('"+ id+"','" + adminmail + "','" + teamname + "')");

            stmt.execute("INSERT INTO dbo.teamfiles VALUES ('" + foldername + "','folder','"+ date + "','unknown','"+membermail + "','stowstartfolder','"+teamid+"')");

            conn.close();
            publishProgress(position);

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }
        return teamid;

    }



    public String addmember(String autoid, String teamname, String membermail) {

        Boolean check = false;

        String name = "";
        Log.i("Android", " addmember.");
        Connection conn = null;
        try {
            String driver = "net.sourceforge.jtds.jdbc.Driver";
            Class.forName(driver).newInstance();
            String connString = "jdbc:jtds:sqlserver://208.91.198.59/dbstow;encrypt=true;user=vp6stow;password=Vp6@technology123;instance=SQLEXPRESS;";
            String username = "vp6stow";
            String password = "Vp6@technology123";
            conn = DriverManager.getConnection(connString, username, password);
            Log.w("Connection", "open");

            Statement stmt = conn.createStatement();
            Log.i("abve", "driver8");
          //  stmt.execute();

            ResultSet idset  = stmt.executeQuery("Select * from dbo.estoreusers where email='"+ membermail+"'");
            while(idset.next())
            {

                check =true;
                name = idset.getString(1)+" "+ idset.getString(2);
            }
            if(check)
            stmt.execute("INSERT INTO dbo.teammembers VALUES ('"+ autoid+"','" + membermail + "','" + teamname + "')");


            else
                Toast.makeText(mcontext, "This member has no account on Stow", Toast.LENGTH_LONG).show();

            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }
        return  name;
    }
}
