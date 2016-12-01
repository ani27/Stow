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
 * Created by anish on 09-07-2016.
 */
public class GroupTeamAsync extends AsyncTask<String,Void, ArrayList<ArrayList>> {
    public interface AsyncResponse {
        void teamlist(ArrayList<ArrayList>teamlist);
    }

    public AsyncResponse delegate = null;
    public Context mcontext;
    public ProgressDialog progressDialog;

    public GroupTeamAsync(Context cntext,AsyncResponse delegate){
        this.delegate = delegate;
        this.mcontext = cntext;
        progressDialog = new ProgressDialog(mcontext, R.style.MyMaterialTheme);
    }
    @Override
    protected void onPreExecute() {
        //set message of the dialog
        progressDialog.setIndeterminate(true);

        progressDialog.setMessage("Loading content....");
        progressDialog.show();

        super.onPreExecute();
    }



    @Override
    protected void onPostExecute(ArrayList<ArrayList>teamlist) {
        progressDialog.dismiss();


        super.onPostExecute(teamlist);
        delegate.teamlist(teamlist);
    }

    @Override
    protected ArrayList<ArrayList> doInBackground(String... email) {

        return checkaccount(email[0]);
    }


    public ArrayList<ArrayList> checkaccount(String mail) {

        ArrayList<ArrayList> teams = new ArrayList<>();
        ArrayList<String> teamname = new ArrayList<>();
        ArrayList<Integer> teamid = new ArrayList<>();

        Log.i("Android", " checkaccount.");
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
            ResultSet reset = stmt.executeQuery("SELECT * FROM dbo.teammembers WHERE members='" + mail +"'");

            teamname.add("Select your Group");
            teamid.add(0);

            while(reset.next())
            {
                teamname.add(reset.getString(3));
                teamid.add(Integer.parseInt(reset.getString(1)));
                Log.i("teamname " + reset.getString(3), "id " + reset.getString(1));
            }



            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }
        teams.add(teamname);
        teams.add(teamid);
        return teams;
    }
}
