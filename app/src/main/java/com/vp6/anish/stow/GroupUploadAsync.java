package com.vp6.anish.stow;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ListView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by anish on 10-07-2016.
 */
public class GroupUploadAsync extends AsyncTask<ArrayList<ArrayList>, String, Boolean> {


    public Context mcontext;
    public RecyclerView listView;
    int count = 0;
    public GroupUploadProgressAdapter updateProgressAdapter;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
//
//    public interface AsyncResponse {
//        void processFinish(Boolean output);
//    }
//
//
//    public AsyncResponse delegate = null;
//
   public GroupUploadAsync(Context context, RecyclerView listView, GroupUploadProgressAdapter groupUploadProgressAdapter) {
        this.mcontext = context;
        this.listView = listView;
        //this.delegate = delegate;
        this.updateProgressAdapter = groupUploadProgressAdapter;
    }

    @Override
    protected Boolean doInBackground(ArrayList<ArrayList>... params) {


        for (int i = 0; i < params[0].get(0).size(); i++) {
            count = 0;
            int position = Integer.parseInt(params[0].get(1).get(0).toString())+i;
            uploadFile(params[0].get(0).get(i).toString(), position+"");


        }


        return true;
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
                updateProgressAdapter.uploadcomplete(position);
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
                updateProgressAdapter.uploadcomplete(position);

                mNotifyManager.notify(id, mBuilder.build());
            } else {
                mBuilder.setProgress(100, progress, false);
                mBuilder.setOngoing(true);
                mNotifyManager.notify(id, mBuilder.build());
            }

        }
    }


    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        //delegate.processFinish(result);
    }

    public void uploadFile(String sourceFileUri, String pos) {


        String fileName = sourceFileUri;

        int previousindex = 0;

        for (int i = 0; i < sourceFileUri.length(); i++) {
            if (sourceFileUri.charAt(i) == '/' && i > previousindex) {
                previousindex = i;
            }
        }

        String nameoffile = sourceFileUri.substring(previousindex + 1, sourceFileUri.length());
        String upLoadServerUri = "http://vp6.in/upload_audio_test/upload_audio.php";

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
                        progress = (uploaded * 100) / total;
                        publishProgress(progress + "", pos + "", nameoffile);
                    } else
                        publishProgress("100", pos + "", nameoffile);
                    Log.i("async", "third in loop  " + bytesAvailable + " " + bytesRead);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                Log.i("async", "fourt");
                // Responses from the server (code and message)
                int serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                Log.i("Input error message", conn.getErrorStream().toString());

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
}
