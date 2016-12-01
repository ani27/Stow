package com.vp6.anish.stow;

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
public class UserRegisterAsync extends AsyncTask<ArrayList<String>,Void,Boolean> {



    ProgressDialog progressDialog;
//    String typeStatus;



    public interface AsyncResponse {
    void Finish(Boolean output);

    }


    public AsyncResponse delegate = null;

    private Context mcontext;

    public UserRegisterAsync(Context context,AsyncResponse delegate)
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

        if(checkifaccountexist(credentials[0].get(2))){
            Log.i("doinbackground","here");
           return registeruser(credentials[0].get(0),credentials[0].get(1),credentials[0].get(2),credentials[0].get(3),credentials[0].get(4));
        }
        else
        {
            return false;
        }




    }

    @Override
    protected void onPostExecute(Boolean result) {
        //hide the dialog
        progressDialog.dismiss();


        super.onPostExecute(result);
        delegate.Finish(result);

    }


    public boolean checkifaccountexist(String mail) {


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
            ResultSet reset = stmt.executeQuery("SELECT * FROM dbo.estoreusers  WHERE email='" + mail +"'");

            if(!reset.isBeforeFirst())
            {
                valid=true;
            }
            else
            {
                Toast.makeText(mcontext, "Mail  ID already exist", Toast.LENGTH_LONG).show();
                valid=false;
            }
            Log.i("abve", "driver9");

            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }

        return valid;
    }



    public boolean registeruser(String first,String last,String mail, String userpassword,String number) {


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

            String newmail = mail.replace(".","dot");
            Log.i("here",newmail);
            Statement stmt = conn.createStatement();
            Log.i("abve", "CREATE TABLE [dbo].[" + newmail + "files] ([File_Name] VARCHAR(50)  NOT NULL  , [File_Type] VARCHAR(50) NOT NULL ,[Date] DATE NOT NULL, [File_Size] VARCHAR(50) NOT NULL , [email] VARCHAR(50) NOT NULL, [Parent_Folder_Name] VARCHAR(MAX) NOT NULL ,[src] VARCHAR(50) );");
            stmt.execute("CREATE TABLE " + newmail + "files (File_Name VARCHAR(50) , File_Type VARCHAR(50) , Date DATE , File_Size VARCHAR(50) , email VARCHAR(50), Parent_Folder_Name VARCHAR(MAX) ,src VARCHAR(50), Parent_Folder_id VARCHAR(50))");
            Log.i("filetable created", newmail);
            stmt.execute("CREATE TABLE " + newmail + "contacts (autoid INT IDENTITY(1,1), deviceid TEXT , name VARCHAR(MAX), number VARCHAR(MAX), type VARCHAR(50))"); // numbermobile VARCHAR(50), numberhome VARCHAR(50), numberwork VARCHAR(50), numberworkmobile VARCHAR(50), numberothers VARCHAR(50), numberfaxwork VARCHAR(50))");
            Log.i("contacts", newmail);
            stmt.execute("Create TABLE " + newmail + "folders  (Folder_Name VARCHAR(50), Date DATE, Size VARCHAR(50), Parent_Folder_Name VARCHAR(MAX), Parent_Folder_id int Identity(1,1))");
           Log.i("Folder", newmail);
            stmt.execute("Insert into dbo.estoreusers values('" + first + "','" + last + "','" + mail + "','" + userpassword + "','" + number + "')");
            Log.i("insert", "on register");

            //sessionManager.createLoginSession(reset.getString(1), reset.getString(2), reset.getString(5), reset.getString(4), reset.getString(3));
            SessionManager.setUserFirstName(mcontext, first);
            SessionManager.setUserLastName(mcontext, last);
            SessionManager.setKeyNumber(mcontext, number);
            SessionManager.setKeyEmail(mcontext, mail);
            SessionManager.setKeyPassword(mcontext, userpassword);
            SessionManager.setIsUserLogin(mcontext, true);

            valid=true;
            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }

     return  valid;
    }

}
