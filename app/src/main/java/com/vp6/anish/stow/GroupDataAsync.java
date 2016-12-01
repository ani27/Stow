package com.vp6.anish.stow;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by anish on 04-07-2016.
 */
public class GroupDataAsync extends AsyncTask<Integer,Void, ArrayList<ArrayList>> {

    public interface AsyncResponse {
        void process(ArrayList<ArrayList>members);
    }

    public AsyncResponse delegate = null;
    public Context mcontext;
    public ProgressDialog progressDialog;

    public GroupDataAsync(Context cntext,AsyncResponse delegate){
        this.delegate = delegate;
        this.mcontext = cntext;
        progressDialog = new ProgressDialog(mcontext, R.style.MyMaterialTheme);
    }
    @Override
    protected void onPreExecute() {
        //set message of the dialog
//        progressDialog.setIndeterminate(true);
//
//        progressDialog.setMessage("Loading content....");
//        progressDialog.show();

        super.onPreExecute();
    }



    @Override
    protected void onPostExecute(ArrayList<ArrayList>members) {
       // progressDialog.dismiss();


        super.onPostExecute(members);
        delegate.process(members);
    }

    @Override
    protected ArrayList<ArrayList> doInBackground(Integer... autoid) {

        return getmembers(autoid[0]);
    }



    public ArrayList<ArrayList> getmembers(Integer autoid) {

        ArrayList<ArrayList> members = new ArrayList<>();

        ArrayList<String>  membername = new ArrayList<>();
        ArrayList<String> memberemail = new ArrayList<>();

        ArrayList<String> filename= new ArrayList<>();
        ArrayList<String> fileaddress = new ArrayList<>();
        ArrayList<String> filetype = new ArrayList<>();
        Log.i("Android", " getmembers.");
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


//            ResultSet fileset = stmt.executeQuery("Select * from dbo.teamfiles where Team_Id='" + autoid +"' and Parent_Folder_Name='stowstartfolder'");
//            while(fileset.next())
//            {
//                filename.add(fileset.getString(1));
//                filetype.add(fileset.getString(2));
//
//            }

            ResultSet reset = stmt.executeQuery("SELECT * FROM dbo.teammembers WHERE autoid='" + autoid +"'");

            while(reset.next())
            {
                memberemail.add(reset.getString(2));
                Log.i("email",reset.getString(2));
            }
            Log.i("abve", "driver9");

            for(int i=0; i<memberemail.size(); i++)
            {
                ResultSet set = stmt.executeQuery("Select * from dbo.estoreusers where email='"+memberemail.get(i)+"'");
                while(set.next())
                {
                    membername.add(set.getString(1)+" "+ set.getString(2));
                    Log.i("name",set.getString(1)+" "+ set.getString(2) );
                }
            }

//
//            ResultSet fileset = stmt.executeQuery("Select * from dbo.teamfiles where autoid='" + autoid +"'");
//            while(reset.next())
//            {
//                filename.add(fileset.getString(1));
//                filetype.add()
//
//            }


            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }
        members.add(membername);
        members.add(memberemail);
//        members.add(filename);
//        members.add(filetype);
       return members;
    }

}










