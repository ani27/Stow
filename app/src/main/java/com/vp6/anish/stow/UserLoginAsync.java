package com.vp6.anish.stow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by anish on 30-06-2016.
 */
public class UserLoginAsync extends AsyncTask<ArrayList<String>,Void,Boolean> {

    ProgressDialog progressDialog;
//    String typeStatus;
public interface AsyncResponse {
    void Finish(Boolean output);
}


    public AsyncResponse delegate = null;

    private Context mcontext;

    public UserLoginAsync(Context context,AsyncResponse delegate)
    {
        this.mcontext = context;
        progressDialog = new ProgressDialog(context, R.style.MyMaterialTheme);
        this.delegate = delegate;
    }
    @Override
    protected void onPreExecute() {
        //set message of the dialog
        progressDialog.setIndeterminate(true);

        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(ArrayList<String>...credentials) {

        //don't touch dialog here it'll break the application
        //do some lengthy stuff like calling login webservice

        return (checkaccount(credentials[0].get(0), credentials[0].get(1)));




    }

    @Override
    protected void onPostExecute(Boolean result) {
        //hide the dialog

       progressDialog.dismiss();


        super.onPostExecute(result);
        delegate.Finish(result);
           }

    public boolean checkaccount(String mail, String Password) {


        boolean valid=false;
        Log.i("Android", " MySQL Connect Example.");
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
            ResultSet reset = stmt.executeQuery("SELECT * FROM dbo.estoreusers  WHERE email='" + mail +"' and password ='"+ Password+"'");

            if(!reset.isBeforeFirst())
            {
                valid=false;
            }
            else
            {
                while(reset.next()) {
                    //sessionManager.createLoginSession(reset.getString(1), reset.getString(2), reset.getString(5), reset.getString(4), reset.getString(3));
                    SessionManager.setUserFirstName(mcontext,reset.getString(1));
                    SessionManager.setUserLastName(mcontext, reset.getString(2));
                    SessionManager.setKeyNumber(mcontext, reset.getString(5));
                    SessionManager.setKeyEmail(mcontext, reset.getString(3));
                    SessionManager.setKeyPassword(mcontext, reset.getString(4));
                    SessionManager.setIsUserLogin(mcontext, true);



                    Log.i("details", reset.getString(1) + "||" + reset.getString(2) + "||" + reset.getString(5) + "||" + reset.getString(4) + "||" + reset.getString(3));
                    Log.i("sessiondetails", SessionManager.getUserFirstName(mcontext) +"||" + SessionManager.getUserLastName(mcontext) +"||"+ SessionManager.getKeyEmail(mcontext)+"||"+ SessionManager.getKeyPassword(mcontext)+"||"+SessionManager.getKeyNumber(mcontext)+"||"+ SessionManager.isUserLogIn(mcontext));
                }valid=true;
            }
            Log.i("abve", "driver9");

            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }

        return valid;
    }



}
