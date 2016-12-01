package com.vp6.anish.stow;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by anish on 04-08-2016.
 */
public class DownloadAsync extends AsyncTask<String,String,Void> {
    int downloadedSize = 0;
    int totalSize = 0;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    Context mcontext;

    public DownloadAsync(Context context) {
        this.mcontext = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        if(params[1].equals("download")) {
            downloadFile("http://stow.netai.net/uploads/download.php", params[0], params[2]);
        }
        else if(params[1].equals("rename")){
            rename(params[0],params[2],params[3],params[4]);

            if(params[3].equals("files"))
            {
                Log.i("here","Rename from server");
                renameOnServer("http://stow.netai.net/uploads/rename.php", params[0],params[2],params[4]+"."+params[5]);
            }
        }
        else if(params[1].equals("delete")){
            delete(params[0],params[2], params[3]);
            Log.i("typeof",params[2]);
            if(params[2].equals("files"))
            {
                Log.i("here","Delete from server");
                deleteFromServer("http://stow.netai.net/uploads/delete.php", params[0], params[3] +"."+ params[4]);
            }
        }
        return null;
    }


    public void downloadFile(String param, String name, String id) {

        final String lineEnd = "\r\n";
        final String twoHyphens = "--";
        final String boundary = "*****";
        DataOutputStream dos;
        try {
            URL url = new URL(param);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            // Allow Outputs
            urlConnection.setDoOutput(false);
            // Don't use a cached copy.
            urlConnection.setUseCaches(false);

            // Use a post method.
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            //urlConnection.setRequestProperty("filephp", );


            //connect
            urlConnection.connect();
            dos = new DataOutputStream(urlConnection.getOutputStream());
            String filephp = SessionManager.getKeyEmail(mcontext) + "data/"+id+"/" + name;


            Log.i("name", filephp);
            //create folder :
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"filephp\";" + lineEnd);
            dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
            dos.writeBytes(lineEnd + filephp + lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            //set the path where we want to save the file
            File SDCardRoot = new File(SessionManager.hasfolderchoosed(mcontext));
            //create a new file, to save the downloaded file
            File file = new File(SDCardRoot, name);

            FileOutputStream fileOutput = new FileOutputStream(file);

            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            //String echo = readStream(inputStream);
           //Log.i("phpecho", echo);
            //this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();

            Log.i("total size", totalSize + "");
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {

                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                Log.i("download size", downloadedSize + "");
                publishProgress(downloadedSize + "", totalSize + "", name);

            }

//
            fileOutput.close();

        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);
            e.printStackTrace();
        } catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
    }

    public void showError(final String err) {
        Log.i("error", err);
    }


    public boolean rename(String oldname, String newname, String type, String id) {

        boolean valid = false;
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
            String mail = SessionManager.getKeyEmail(mcontext);
            mail = mail.replace(".", "dot");

            mail = mail + type;
            Statement stmt = conn.createStatement();
            Log.i("above", "rename");
          //  ResultSet reset = stmt.executeQuery("update " +mail+" set Name='"+newname +"' where Name ='"+ oldname+"'");

           stmt.executeUpdate("update " +mail+" set Name='"+newname +"' where ID ='"+id +"'");
            valid = true;

            Log.i("below", "rename");

            conn.close();
            return true;

        }
        catch(Exception e)

        {
            Log.w("Error connection", "" + e);

        }

        return false;

    }





    public boolean delete(String name, String type, String id) {

        boolean valid = false;
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
            String mail = SessionManager.getKeyEmail(mcontext);
            mail = mail.replace(".", "dot");

            mail = mail + type;
            Statement stmt = conn.createStatement();
            Log.i("above", "rename");
            //  ResultSet reset = stmt.executeQuery("update " +mail+" set Name='"+newname +"' where Name ='"+ oldname+"'");

            stmt.executeUpdate("delete from " +mail+" where ID='"+id +"'");
            valid = true;

            Log.i("below", "rename");

            conn.close();
            return true;

        }
        catch(Exception e)

        {
            Log.w("Error connection", "" + e);

        }

        return false;

    }





    public void deleteFromServer(String param, String name,String id_extension) {

        final String lineEnd = "\r\n";
        final String twoHyphens = "--";
        final String boundary = "*****";
        DataOutputStream dos;
        try {
            URL url = new URL(param);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            Log.i("try","Request Method");
             urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            // Allow Outputs
            urlConnection.setDoOutput(true);
            // Don't use a cached copy.
            urlConnection.setUseCaches(false);

            // Use a post method.
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            //urlConnection.setRequestProperty("filephp", );


            //connect
            urlConnection.connect();
            dos = new DataOutputStream(urlConnection.getOutputStream());
            String filephp = SessionManager.getKeyEmail(mcontext) + "data/" + id_extension;


            Log.i("name", filephp);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"filephp\"" + lineEnd);


            dos.writeBytes(lineEnd);
            dos.writeBytes(filephp + lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);


            dos.flush();
            dos.close();
            Log.i("Server response", urlConnection.getResponseCode() + "  " + urlConnection.getResponseMessage());

        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);
            e.printStackTrace();
        } catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
    }






    public void renameOnServer(String param, String oldname, String newname,String id_extension) {

        final String lineEnd = "\r\n";
        final String twoHyphens = "--";
        final String boundary = "*****";
        DataOutputStream dos;
        try {
            URL url = new URL(param);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            Log.i("try", "Request Method");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            // Allow Outputs
            urlConnection.setDoOutput(true);
            // Don't use a cached copy.
            urlConnection.setUseCaches(false);

            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            urlConnection.connect();
            dos = new DataOutputStream(urlConnection.getOutputStream());
            String filephp = SessionManager.getKeyEmail(mcontext) + "data/"+id_extension+"/" + oldname;



            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"filephp\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(filephp + lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);


            String newfilephp = SessionManager.getKeyEmail(mcontext) + "data/"+id_extension +"/"+ newname;

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"newfilephp\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(newfilephp + lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);


            dos.flush();
            dos.close();
            Log.i("Server response", urlConnection.getResponseCode()+"  "+urlConnection.getResponseMessage());

        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);
            e.printStackTrace();
        } catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
    }


    @Override
    protected void onProgressUpdate(String... params) {
        int downloaded = Integer.parseInt(params[0]);
        int totalsize = Integer.parseInt(params[1]);
        String name = (params[2]);
        int progress = (downloaded*100)/totalsize;

        int id = 1;

          //  Log.i("First", "Notification");


            mNotifyManager = (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(mcontext);


            if (progress == 100) {
                mBuilder.setProgress(0, 0, false);
                mBuilder.setContentText("Download Successful");
                mBuilder.setSmallIcon(R.drawable.uploadfile);
                mBuilder.setContentTitle(name);
                mBuilder.setOngoing(false);
                mNotifyManager.notify(id, mBuilder.build());
    //            updateProgressAdapter.uploadcomplete(position);
            } else {

                mBuilder.setContentTitle(name+"")
                        .setContentText("Download in progress...")
                        .setSmallIcon(R.drawable.uploadfile)
                        .setOngoing(true);

                mBuilder.setProgress(100, progress, false);

                mNotifyManager.notify(id, mBuilder.build());
            }




        }



}
