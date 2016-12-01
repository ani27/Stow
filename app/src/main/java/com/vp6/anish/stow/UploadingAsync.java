package com.vp6.anish.stow;

/**
 * Created by anish on 05-08-2016.
 */

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
public class UploadingAsync extends AsyncTask<ArrayList<ArrayList>, String, String> {


    public interface AsyncResponse {
        void process(String id);
    }
    AsyncResponse delegate;
    public Context mcontext;
    public RecyclerView listView;
    int count = 0;
    int position;
    String folder_name;
    String parent_id;
    String parent_name;
    public FileListAdapter fileListAdapter;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    public FolderListAdapter folderListAdapter;
    String type;

    public UploadingAsync(Context context, RecyclerView listView, FileListAdapter fileListAdapter, int position, String parent_id, String parent_name, String type) {
        this.mcontext = context;
        this.listView = listView;
        this.fileListAdapter= fileListAdapter;
        this.position = position;
        this.parent_id = parent_id;
        this.parent_name = parent_name;
        this.type = type;
    }

    public  UploadingAsync(Context context, String folder_name, String parent_id, String parent_name, FolderListAdapter folderListAdapter, String type, AsyncResponse delegate )
    {
        this.folderListAdapter = folderListAdapter;
        this.mcontext = context;
        this.parent_id = parent_id;
        this.parent_name = parent_name;
        this.folder_name = folder_name;
        this.type =type;
        this.delegate =delegate;

    }


    @Override
    protected String doInBackground(ArrayList<ArrayList>... params) {

        if(type.equals("file")) {
            count = 0;
            for (int i = 0; i < params[0].get(0).size(); i++) {

                String name = params[0].get(2).get(i).toString();
                String size = params[0].get(1).get(i).toString();
                String file_address = params[0].get(0).get(i).toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                // int position = Integer.parseInt(params[0].get(1).get(0).toString())+i;
                publishProgress("15", position + "", name, "");
                String filename = updatesql(name, currentDateandTime, size, parent_id, parent_name);
                publishProgress("30", position + "", name, "");
                uploadFile(file_address, position + "", filename);

                position++;
            }
            return "";
        }
        else {
            String folder_name = params[0].get(0).get(0).toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());
            return  updatesql_folder(folder_name,currentDateandTime,"0",parent_id,parent_name);

        }

    }

    @Override
    protected void onProgressUpdate(String... params) {
        int progress = Integer.parseInt(params[0]);
        int position = Integer.parseInt(params[1]);

        int id = 1;
        if (count == 0)

        {
            Log.i("First", "Notification");
            count++;

            mNotifyManager = (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(mcontext);


            if (progress == 100) {
                mBuilder.setProgress(0, 0, false);
                mBuilder.setContentText("Upload Successful");
                mBuilder.setSmallIcon(R.drawable.uploadfile);
                mBuilder.setContentTitle(params[2]);
                mBuilder.setOngoing(false);
                mNotifyManager.notify(id, mBuilder.build());
                fileListAdapter.uploadcomplete(position,params[3]);
            } else {

                mBuilder.setContentTitle(params[2])
                        .setContentText("Upload in progress...")
                        .setSmallIcon(R.drawable.uploadfile)
                        .setOngoing(true);

                mBuilder.setProgress(100, progress, false);

                mNotifyManager.notify(id, mBuilder.build());
            }



        } else {

            if (progress == 100) {
                mBuilder.setProgress(0, 0, false)
                        .setContentText("Upload Successful")
                        .setContentTitle(params[2])
                        .setSmallIcon(R.drawable.uploadfile)
                        .setOngoing(false);
                fileListAdapter.uploadcomplete(position,params[3]);

                mNotifyManager.notify(id, mBuilder.build());
            } else {
                mBuilder.setProgress(100, progress, false);
                mBuilder.setOngoing(true);
                mBuilder.setContentTitle("Upload in progress...");
                mNotifyManager.notify(id, mBuilder.build());
            }

        }
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(!result.equals(""))
        delegate.process(result);
    }

    public void uploadFile(String sourceFileUri, String pos,String name) {


        String fileName = sourceFileUri;

        int previousindex = 0;

        for (int i = 0; i < sourceFileUri.length(); i++) {
            if (sourceFileUri.charAt(i) == '/' && i > previousindex) {
                previousindex = i;
            }
        }

        String nameoffile = name;//
        String file_name= sourceFileUri.substring(previousindex + 1, sourceFileUri.length());
        String upLoadServerUri = "http://stow.netai.net/uploads/upload.php";

        String id = nameoffile.substring(0,nameoffile.lastIndexOf('.')-1);
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 10;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            Log.i("uploadFile", "Source File not exist :");
            return;
        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploadedfile", fileName);

                conn.setChunkedStreamingMode(0);
                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                Log.i("async", "first");
                // read file and write it into frm...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                int total = fileInputStream.available();
                int uploaded = 0;
                int progress;
                Log.i("async", "second");
                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    if (total != 0) {
                        uploaded = total - bytesAvailable;
                        progress = (uploaded * 70) / total + 30;
                        publishProgress(progress + "", pos + "",file_name,id);
                    } else
                        publishProgress("100", pos + "", file_name,id);
                    Log.i("async", "third in loop  " + bytesAvailable + " " + bytesRead);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);


                String folder = SessionManager.getKeyEmail(mcontext)+"data/"+nameoffile;


                //create folder :
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"folder\";" + lineEnd);

                dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                dos.writeBytes(lineEnd + folder + lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

//
//                //filename
//                dos.writeBytes(twoHyphens + boundary + lineEnd);
//                dos.writeBytes("Content-Disposition: form-data; name=\"nameoffile\";" + lineEnd);
//
//                dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
//                dos.writeBytes(lineEnd + nameoffile + lineEnd);
//                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                Log.i("async", "fourt");
                // Responses from the server (code and message)
                int serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                //Log.i("Input error message", conn.getErrorStream().toString());

                if (serverResponseCode == 200) {
                    Log.i("Success", "File Upload Completed.See uploaded file here : " + " serverpath");
                    // Toast.makeText(mcontext, "File Upload Complete.", Toast.LENGTH_SHORT).show();

                }
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                // Toast.makeText(mcontext, "MalformedURLException", Toast.LENGTH_SHORT).show();
                Log.i("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();
                //Toast.makeText(mcontext, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                Log.i("Upload file  : " + e.getMessage(), e + "");
            }


            return;


        }
    }





    public String updatesql(String name, String date, String size, String parent_id, String parent_name) {

        String max_id = "";
        String extension ="";
         //here goes the epic code of life
        // L.I.F.E == life only when you can turn back time so don't stress out
        // just move on to the things L.I.F.E. is very much different from the things which we do in life so beware and just decode this code
        // which is the solution of ur every life promblem
         //                 NEMO NO esaelp -.- doom at every luck and things whic


        Log.i("Android", " update to server");
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
            String newmail = mail.replace(".","dot");
            String tablename = newmail+"files";
             extension = name.substring(name.lastIndexOf('.')+1);

            Statement stmt = conn.createStatement();
            Log.i("Table name", tablename);

            Log.i("Table name", max_id);
           //\
            stmt.execute("Insert into "+tablename+" values('" + name + "','" + extension + "','" + date + "','" + size + "','"+parent_name+"','"+parent_id+"')");
            Log.i("insert", "on upload");

            ResultSet max = stmt.executeQuery("Select MAX(ID) from "+  tablename +"");


            while(max.next())
            {
                max_id = max.getString(1);
            }
            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }

        return  max_id+"."+extension ;
    }






    public String updatesql_folder(String name, String date, String size, String parent_id, String parent_name) {

        String max_id = "";
        ////String extension ="";
        //here goes the epic code of life
        // L.I.F.E == life only when you can turn back time so don't stress out
        // just move on to the things L.I.F.E. is very much different from the things which we do in life so beware and just decode this code
        // which is the solution of ur every life promblem
        //                 NEMO NO esaelp -.- doom at every luck and things whic


        Log.i("Android", " update to server");
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
            String newmail = mail.replace(".","dot");
            String tablename = newmail+"folders";
           // extension = name.substring(name.lastIndexOf('.')+1);

            Statement stmt = conn.createStatement();
            Log.i("Table name", tablename);

            Log.i("Table name", max_id);
            //\
            stmt.execute("Insert into "+tablename+" values('" + name + "','"  + date + "','" + size + "','"+parent_name+"','"+parent_id+"')");
            Log.i("insert", "on upload");

            ResultSet max = stmt.executeQuery("Select MAX(ID) from "+  tablename +"");


            while(max.next())
            {
                max_id = max.getString(1);
            }
            conn.close();

        } catch (Exception e) {
            Log.w("Error connection", "" + e);
        }

        return  max_id ;
    }

}

