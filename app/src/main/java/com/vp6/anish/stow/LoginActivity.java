package com.vp6.anish.stow;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

import net.sourceforge.jtds.jdbc.*;
import net.sourceforge.jtds.jdbc.Driver;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        if(SessionManager.isUserLogIn(LoginActivity.this))
        {
            Intent intent = new Intent(LoginActivity.this, UploadDownloadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            LoginActivity.this.finish();
        }
        Context mcontext = LoginActivity.this;
        Log.i("sessiondetails", SessionManager.getUserFirstName(mcontext) +"||" + SessionManager.getUserLastName(mcontext) +"||"+ SessionManager.getKeyEmail(mcontext)+"||"+ SessionManager.getKeyPassword(mcontext)+"||"+SessionManager.getKeyNumber(mcontext)+"||"+ SessionManager.isUserLogIn(mcontext));

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");
       // _loginButton.setEnabled(false);
        if (hasInternetAccess(getApplicationContext())) {
            if (!validate()) {
                onLoginFailed();
                return;
            }


            Log.i("here", "prgress started");


           Log.i("here", "checkaccountstarted");
            String email = _emailText.getText().toString();
            String password = _passwordText.getText().toString();

            ArrayList<String> credentials= new ArrayList<>();
            credentials.add(email);
            credentials.add(password);

            UserLoginAsync userLoginAsync = new UserLoginAsync(LoginActivity.this, new UserLoginAsync.AsyncResponse() {
                @Override
                public void Finish(Boolean output) {
                    if(output)
                    {

                        onLoginSuccess();

                    }
                    else
                    {
                        onLoginFailed();
                    }

                }
            });
            userLoginAsync.execute(credentials);
        }
        else {
            Toast.makeText(this,"No Internet Access",Toast.LENGTH_LONG).show();
        }
        //_loginButton.setEnabled(true);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            Log.i("hrere", "reqyvsignup");
            if (resultCode == RESULT_OK) {
Log.i("fdf","resultok");
ArrayList<String> details = data.getStringArrayListExtra("Details");

                UserRegisterAsync userRegisterAsync = new UserRegisterAsync(LoginActivity.this, new UserRegisterAsync.AsyncResponse() {
                    @Override
                    public void Finish(Boolean output) {
                        if(output)
                        {

                            onSignupSuccess();
                        }

                    }
                });
                userRegisterAsync.execute(details);


                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically

            }
        }
    }




    public void onSignupSuccess(){

        Intent intent = new Intent(this, UploadDownloadActivity.class);
        this.startActivity(intent);
        this.finish();

    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }



    public void onLoginSuccess() {

        Intent intent = new Intent(this, UploadDownloadActivity.class);
        this.startActivity(intent);

        this.finish();

    }

    public void onLoginFailed() {
        Toast.makeText(this, "Wrong Id or Password", Toast.LENGTH_LONG).show();

        //_loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError("must be greater than 4 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }



        return valid;
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



}



