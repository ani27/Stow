package com.vp6.anish.stow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    SessionManager session;
    @InjectView(R.id.input_name_first) EditText _firstnameText;
    @InjectView(R.id.input_name_last) EditText _lastnameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_number) EditText _numbertext;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);

        //session = new SessionManager(getApplicationContext());
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (hasInternetAccess(getApplicationContext())) {
            if (!validate()) {
                onSignupFailed();
                return;
            }

            //_signupButton.setEnabled(false);
//
//            final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
//                    R.style.AppTheme);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setMessage("Creating Account...");
//            progressDialog.show();

            String firstname = _firstnameText.getText().toString();
            String lastname = _lastnameText.getText().toString();
            String email = _emailText.getText().toString();
            String password = _passwordText.getText().toString();
            String number = _numbertext.getText().toString();

            // TODO: Implement your own signup logic here.

            ArrayList<String> details = new ArrayList<>();
            details.add(firstname);
            details.add(lastname);
            details.add(email);
            details.add(password);
            details.add(number);
            Intent returnIntent = getIntent();
            returnIntent.putStringArrayListExtra("Details", details);
            setResult(Activity.RESULT_OK, returnIntent);
            this.finish();
             Log.i("here", "SignupActivity");
//
//            if(checkifaccountexist(email))
//            {
//                registeruser(firstname, lastname, email, number, password);
//                //session.createLoginSession(firstname,lastname,number,password,email);
//
//            }
//            else
//            {
//                return;
//            }
//            new android.os.Handler().postDelayed(
//                    new Runnable() {
//                        public void run() {
//                            // On complete call either onSignupSuccess or onSignupFailed
//                            // depending on success
//                            onSignupSuccess();
//                            // onSignupFailed();
//                            progressDialog.dismiss();
//                        }
//                    }, 3000);
        }
        else
            Toast.makeText(this,"No Internet Access", Toast.LENGTH_LONG).show();
    }


//    public void onSignupSuccess() {
//        _signupButton.setEnabled(true);
//        setResult(RESULT_OK, null);
//        Intent intent = new Intent(SignUpActivity.this, DisplayActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();


    }

    public boolean validate() {
        boolean valid = true;

        String firstname = _firstnameText.getText().toString();
        String lastname = _lastnameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String phonenumber = _numbertext.getText().toString();

        if (firstname.isEmpty() || firstname.length() < 2) {
            _firstnameText.setError("at least 3 characters");
            valid = false;
        } else {
            _firstnameText.setError(null);
        }

        if (lastname.isEmpty() || lastname.length() < 2) {
            _lastnameText.setError("at least 3 characters");
            valid = false;
        } else {
            _lastnameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError("password must be greater than 4 character");
            valid = false;
        } else {
            _passwordText.setError(null);
        }


        if (phonenumber.isEmpty() || phonenumber.length() != 10) {
            _numbertext.setError("invalid number");
            valid = false;
        } else {
            _numbertext.setError(null);
        }
        return  valid;
    }




    //to check internet access

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
                Toast.makeText(this, "Mail  ID already exist", Toast.LENGTH_LONG).show();
                valid=false;
            }
            Log.i("abve", "driver9");

            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }

        return valid;
    }



    public void registeruser(String first,String last,String mail,String number, String userpassword) {


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
            stmt.execute("Insert into dbo.estoreusers values('" + first + "','" + last + "','" + mail + "','" + userpassword + "','" + number+"')");
            Log.i("insert", "on register");
            stmt.execute("CREATE TABLE dbo." + mail + "files (File_Name VARCHAR(50) , File_Type VARCHAR(50) , Date DATE , File_Size VARCHAR(50) , email VARCHAR(50), Parent_Folder_Name VARCHAR(MAX) ,src VARCHAR(50))");
            Log.i("filetable created", mail);
            stmt.execute("CREATE TABLE dbo." + mail + "contacts (autoid INT IDENTITY(1,1), deviceid TEXT , name VARCHAR(50), numbermobile VARCHAR(50), numberhome VARCHAR(50), numberwork VARCHAR(50), numberworkmobile VARCHAR(50), numberothers VARCHAR(50), numberfaxwork VARCHAR(50))");
            Log.i("contacts", mail);
            stmt.execute("Create TABLE dbo." + mail + "folders  (Folder_Name VARCHAR(50), Date DATE, Size VARCHAR(50), Parent_Folder_Name VARCHAR(MAX))" );

            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }


    }

}