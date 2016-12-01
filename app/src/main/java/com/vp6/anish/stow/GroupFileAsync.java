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
 * Created by anish on 11-07-2016.
 */

public class GroupFileAsync extends AsyncTask<String,Void, ArrayList<ArrayList>> {

    public interface AsyncResponse {
        void process(ArrayList<ArrayList>members);
    }

    public AsyncResponse delegate = null;
    public Context mcontext;
    public ProgressDialog progressDialog;

    public GroupFileAsync(Context cntext,AsyncResponse delegate){
        this.delegate = delegate;
        this.mcontext = cntext;
        progressDialog = new ProgressDialog(mcontext, R.style.MyMaterialTheme);
    }
    @Override
    protected void onPreExecute() {
//        //set message of the dialog
//        progressDialog.setIndeterminate(true);
//
//        progressDialog.setMessage("Loading files....");
//        progressDialog.show();

        super.onPreExecute();
    }



    @Override
    protected void onPostExecute(ArrayList<ArrayList>files) {
       // progressDialog.dismiss();


        super.onPostExecute(files);
        delegate.process(files);
    }

    @Override
    protected ArrayList<ArrayList> doInBackground(String... foldername) {

        return getfiles(foldername[0],foldername[1]);
    }



    public ArrayList<ArrayList> getfiles(String foldername, String autoid) {

        ArrayList<ArrayList> files = new ArrayList<>();

        ArrayList<String> filename= new ArrayList<>();
        ArrayList<Boolean> fileuploaded = new ArrayList<>();
//        ArrayList<String> fileaddress = new ArrayList<>();
        ArrayList<String> filetype = new ArrayList<>();
        Log.i("Android", " getfiles.");
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

            ResultSet fileset = stmt.executeQuery("Select * from dbo.teamfiles where Team_Id='" + autoid +"' and Parent_Folder_Name='"+foldername+"'");
            while(fileset.next())
            {
                filename.add(fileset.getString(1));
                filetype.add(fileset.getString(2));

                fileuploaded.add(true);
            }


            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }
        files.add(filename);
        files.add(filetype);
        files.add(fileuploaded);
        return files;
    }

}
