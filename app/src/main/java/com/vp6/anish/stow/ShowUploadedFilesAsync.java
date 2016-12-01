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
import java.util.Date;

/**
 * Created by anish on 29-07-2016.
 */
public class  ShowUploadedFilesAsync extends AsyncTask<String,Void, ArrayList<ArrayList>> {

    public interface AsyncResponse {
        void process(ArrayList<ArrayList>Data);
    }



    public AsyncResponse delegate = null;
    public Context mcontext;
    //public ProgressDialog progressDialog;

    public ShowUploadedFilesAsync(Context cntext,AsyncResponse delegate){
        this.delegate = delegate;
        this.mcontext = cntext;
      //  progressDialog = new ProgressDialog(mcontext, R.style.MyMaterialTheme);
    }








    @Override
    protected void onPostExecute(ArrayList<ArrayList>files) {
        // progressDialog.dismiss();


        super.onPostExecute(files);
        delegate.process(files);
    }

    @Override
    protected ArrayList<ArrayList> doInBackground(String... params) {

        return getData(params[0],params[1]);
    }




    public ArrayList<ArrayList> getData(String email, String id) {

        ArrayList<ArrayList> files = new ArrayList<>();

        ArrayList<String>foldername = new ArrayList<>();
        ArrayList<String>foldercreated = new ArrayList<>();
        ArrayList<String>folderid = new ArrayList<>();
        ArrayList<String> filesize = new ArrayList<>();
        ArrayList<String> filecreated = new ArrayList<>();
         ArrayList<String> filename= new ArrayList<>();
        ArrayList<Boolean> fileuploaded = new ArrayList<>();
       ArrayList<String> foldersize = new ArrayList<>();
        ArrayList<String> filetype = new ArrayList<>();
        ArrayList<String> fileid = new ArrayList<>();
        Log.i("Download", " getfiles.");
        Connection conn = null;
        try {
            String driver = "net.sourceforge.jtds.jdbc.Driver";
            Class.forName(driver).newInstance();
            String connString = "jdbc:jtds:sqlserver://208.91.198.59/dbstow;encrypt=true;user=vp6stow;password=Vp6@technology123;instance=SQLEXPRESS;";
            String username = "vp6stow";
            String password = "Vp6@technology123";
            conn = DriverManager.getConnection(connString, username, password);
            Log.w("Connection", "open");

            String newmail = email.replace(".","dot");
            Statement stmt = conn.createStatement();
            Log.i("abve", "driver8");


                ResultSet folderset = stmt.executeQuery("Select * from "+newmail+"folders where Parent_Folder_Id='"+id+"'");
                while(folderset.next())
                {
                    foldername.add(folderset.getString(1));
                    folderid.add(folderset.getString(6));
                    foldercreated.add(folderset.getString(2));
                    foldersize.add(folderset.getString(3));


                    Log.i("Folder Name", folderset.getString(1));
                }



            ResultSet fileset = stmt.executeQuery("Select * from "+newmail+"files where Parent_Folder_Id='"+id+"'");
            while(fileset.next())
            {
                filename.add(fileset.getString(1));
                filetype.add(fileset.getString(2));
                filecreated.add(fileset.getString(3));
                filesize.add(fileset.getString(4));
//                fileaddress.add(fileset.getString(7));
                fileid.add(fileset.getString(7));


                fileuploaded.add(true);
                Log.i("File Name", fileset.getString(1));
            }


            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }
        files.add(filename);
        files.add(filetype);
        files.add(filecreated);
        files.add(filesize);
       // files.add(fileaddress);
        files.add(foldername);
        files.add(foldercreated);
        files.add(fileuploaded);
        files.add(folderid);
        files.add(fileid);
        files.add(foldersize);
        return files;
    }

}
