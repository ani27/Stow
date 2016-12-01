package com.vp6.anish.stow;

import android.app.NotificationManager;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by anish on 17-07-2016.
 */
public class ContactsSyncAsync extends AsyncTask<String,String,Boolean> {


    public interface AsyncResponse {
        void Finish(Boolean output);

    }


    public AsyncResponse delegate = null;

    Context mcontext;
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    public ContactsSyncAsync (Context context,AsyncResponse delegate)
    {
        this.mcontext = context;

        this.delegate = delegate;
    }



    @Override
    protected Boolean doInBackground(String... params) {

        publishProgress("1");

//        if(uploadcontacts());
//        {publishProgress("2");}
//        else{
//            publishProgress("0");
//        }
        if(uploadcontacts())
        {
         publishProgress("2");
        }
        else
        {
            publishProgress("0");
            return false;
        }
        if(deleteduplicates())
        {
            publishProgress("3");
        }
        else
        {
            publishProgress("0");
            return false;
        }
        //deleteduplicates();
        //publishProgress("3");
        if(contactsdownload())
        {
            publishProgress("4");
        }
        else
        {
            publishProgress("0");
            return false;
        }
//        contactsdownload();
//        publishProgress("4");

        return true;
    }

    @Override
    protected void onProgressUpdate(String...result )
    {
        int id=1;

        mNotifyManager = (NotificationManager) mcontext.getSystemService(mcontext.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mcontext);
        mBuilder.setSmallIcon(R.drawable.contactsync);
        mBuilder.setContentTitle("Syncing Contacts");

        if(result[0].equals("1")) {
            mBuilder.setContentText("Uploading Contacts");
            mBuilder.setOngoing(true);
            mNotifyManager.notify(id, mBuilder.build());
        }

        if(result[0].equals("2")) {
            mBuilder.setContentText("Deleting Duplicate Contacts");
            mBuilder.setOngoing(true);
            mNotifyManager.notify(id, mBuilder.build());
        }
        if(result[0].equals("3")) {
            mBuilder.setContentText("Downloading Contacts");
            mBuilder.setOngoing(true);
            mNotifyManager.notify(id, mBuilder.build());
        }
        if(result[0].equals("4")) {
            mBuilder.setContentText("Sync successful");
            mBuilder.setOngoing(false);
            mNotifyManager.notify(id, mBuilder.build());
        }


        if(result[0].equals("0")) {
            mBuilder.setContentText("Sync Failed");
            mBuilder.setOngoing(false);
            mNotifyManager.notify(id, mBuilder.build());
        }


    }

    @Override
    protected void onPostExecute(Boolean result) {
        //hide the dialog
     //   progressDialog.dismiss();


        super.onPostExecute(result);
        delegate.Finish(result);

    }




    public boolean uploadcontacts(){
        Statement stmt=null;
        String lastnumber="0000000000";
        Long Date=100L;
        Long LastSync = SessionManager.getLastSync(mcontext);
        Connection conn = null;
        String newmail = SessionManager.getKeyEmail(mcontext).replace(".", "dot");
        try {

            String driver = "net.sourceforge.jtds.jdbc.Driver";
            Class.forName(driver).newInstance();
            String connString = "jdbc:jtds:sqlserver://208.91.198.59/dbstow;encrypt=true;user=vp6stow;password=Vp6@technology123;instance=SQLEXPRESS;";
            String username = "vp6stow";
            String password = "Vp6@technology123";
            conn = DriverManager.getConnection(connString, username, password);
            Log.i("Connection", "open");
            stmt = conn.createStatement();
        }
        catch (Exception e)
        {
            Log.i("error", e+"");
        }

        Cursor cursor = mcontext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Integer contactsCount = cursor.getCount(); // get how many contacts you have in your contacts list
        if (contactsCount > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if(Build.VERSION.SDK_INT >= 18)
                Date= Long.parseLong(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)));


                if(LastSync < Date) {
                    Log.i("id contact name date", id + " " + contactName + " " + Date);
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCursor = mcontext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCursor.moveToNext()) {
                            int phoneType = pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            String phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                          //  Log.i("last number", lastnumber);
                            if (!phoneNo.equals("null") && !phoneNo.equals(lastnumber)) {
                                //if(phoneNo != null){

                                lastnumber = phoneNo;
                                //if(phoneNo != ""){
                                switch (phoneType) {
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                        Log.e(contactName + ": TYPE_MOBILE", " " + phoneNo);

                                        try {
                                            //  if(stmt.execute("INSERT INTO usercontacts (deviceid , name , numbermobile , numberhome , numberwork , numberworkmobile , numberothers , numberfaxwork) VALUES('" + id + "','" + contactName + "','" + mobile + "','" + home + "','" + work + "','"  + workmobile + "','" + others + "','" + faxwork + "')"))
                                            if (stmt.execute("INSERT INTO " + newmail + "contacts (deviceid, name, number, type) VALUES('" + id + "','" + contactName + "','" + phoneNo + "','1')"))
                                                ;
                                            Log.i("abve", "inserted");

                                        } catch (Exception e) {
                                            Log.w("Error connectio0n", "" + e);
                                            return false;
                                        }
                                        //   mobile = phoneNo;
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:

                                        Log.e(contactName + ": TYPE_HOME", " " + phoneNo);
                                        try {
                                            //  if(stmt.execute("INSERT INTO usercontacts (deviceid , name , numbermobile , numberhome , numberwork , numberworkmobile , numberothers , numberfaxwork) VALUES('" + id + "','" + contactName + "','" + mobile + "','" + home + "','" + work + "','"  + workmobile + "','" + others + "','" + faxwork + "')"))
                                            if (stmt.execute("INSERT INTO " + newmail + "contacts (deviceid, name, number, type) VALUES('" + id + "','" + contactName + "','" + phoneNo + "','2')"))
                                                ;
                                            Log.i("abve", "inserted");

                                        } catch (Exception e) {
                                            Log.w("Error connectio0n", "" + e);
                                            return false;
                                        }

                                        // home = phoneNo;
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                        Log.e(contactName + ": TYPE_WORK", " " + phoneNo);
                                        try {
                                            //  if(stmt.execute("INSERT INTO usercontacts (deviceid , name , numbermobile , numberhome , numberwork , numberworkmobile , numberothers , numberfaxwork) VALUES('" + id + "','" + contactName + "','" + mobile + "','" + home + "','" + work + "','"  + workmobile + "','" + others + "','" + faxwork + "')"))
                                            if (stmt.execute("INSERT INTO " + newmail + "contacts (deviceid, name, number, type) VALUES('" + id + "','" + contactName + "','" + phoneNo + "','3')"))
                                                ;
                                            Log.i("abve", "inserted");

                                        } catch (Exception e) {
                                            Log.w("Error connectio0n", "" + e);
                                            return false;
                                        }

                                        // work = phoneNo;
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                                        Log.e(contactName + ": TYPE_WORK_MOBILE", " " + phoneNo);
                                        // workmobile = phoneNo;

                                        try {                                        //  if(stmt.execute("INSERT INTO usercontacts (deviceid , name , numbermobile , numberhome , numberwork , numberworkmobile , numberothers , numberfaxwork) VALUES('" + id + "','" + contactName + "','" + mobile + "','" + home + "','" + work + "','"  + workmobile + "','" + others + "','" + faxwork + "')"))
                                            if (stmt.execute("INSERT INTO " + newmail + "contacts (deviceid, name, number, type) VALUES('" + id + "','" + contactName + "','" + phoneNo + "','4')"))
                                                ;
                                            Log.i("abve", "inserted");

                                        } catch (Exception e) {
                                            Log.w("Error connectio0n", "" + e);
                                            return false;
                                        }

                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                                        Log.e(contactName + ": TYPE_OTHER", " " + phoneNo);
                                        // others = phoneNo;
                                        try {
                                            //  if(stmt.execute("INSERT INTO usercontacts (deviceid , name , numbermobile , numberhome , numberwork , numberworkmobile , numberothers , numberfaxwork) VALUES('" + id + "','" + contactName + "','" + mobile + "','" + home + "','" + work + "','"  + workmobile + "','" + others + "','" + faxwork + "')"))
                                            if (stmt.execute("INSERT INTO " + newmail + "contacts (deviceid, name, number, type) VALUES('" + id + "','" + contactName + "','" + phoneNo + "','5')"))
                                                ;
                                            Log.i("abve", "inserted");

                                        } catch (Exception e) {
                                            Log.w("Error connectio0n", "" + e);
                                            return false;
                                        }

                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                                        Log.e(contactName + ": TYPE_OTHER", " " + phoneNo);
                                        // faxwork = phoneNo;
                                        try {
                                            //  if(stmt.execute("INSERT INTO usercontacts (deviceid , name , numbermobile , numberhome , numberwork , numberworkmobile , numberothers , numberfaxwork) VALUES('" + id + "','" + contactName + "','" + mobile + "','" + home + "','" + work + "','"  + workmobile + "','" + others + "','" + faxwork + "')"))
                                            if (stmt.execute("INSERT INTO " + newmail + "contacts (deviceid, name, number, type) VALUES('" + id + "','" + contactName + "','" + phoneNo + "','6')"))
                                                ;
                                            Log.i("abve", "inserted");

                                        } catch (Exception e) {
                                            Log.w("Error connectio0n", "" + e);
                                            return false;
                                        }

                                        break;
                                    default:
                                        break;
                                }


                            }
                        }
                        pCursor.close();

                    }

                }
            }
            cursor.close();
            try {
                conn.close();


            }
            catch (Exception e)
            {
                Log.i("exception", e+"");
                return false;
            }
        }


return true;
    }

    public boolean deleteduplicates(){

        String newmail = SessionManager.getKeyEmail(mcontext).replace(".", "dot");


        Log.i("Android"," MySQL Connect Example.");
        Connection conn = null;
        try {
            String driver = "net.sourceforge.jtds.jdbc.Driver";
            Class.forName(driver).newInstance();
            String connString = "jdbc:jtds:sqlserver://208.91.198.59/dbstow;encrypt=true;user=vp6stow;password=Vp6@technology123;instance=SQLEXPRESS;";
            String username = "vp6stow";
            String password = "Vp6@technology123";
            conn = DriverManager.getConnection(connString, username, password);
            Log.w("Connection","open");
            Statement stmt = conn.createStatement();
            Boolean check=stmt.execute("DELETE FROM "+newmail+"contacts WHERE autoid NOT IN (SELECT MIN(autoid) FROM "+newmail+"contacts GROUP BY name , number)");
            if(check)
                Log.i("abve","deleted");

            conn.close();

        } catch (Exception e)
        {
            Log.w("Error connection", "" + e);
            return false;
        }
        return true;
    }



    public Boolean contactsdownload() {
        String newmail = SessionManager.getKeyEmail(mcontext).replace(".", "dot");

        Log.i("Android", " MySQL Connect Example.");
        Connection conn = null;
        ArrayList<ContentProviderOperation> ops = null;
        try {

            String driver = "net.sourceforge.jtds.jdbc.Driver";

            Class.forName(driver).newInstance();
            String connString = "jdbc:jtds:sqlserver://208.91.198.59/dbstow;encrypt=true;user=vp6stow;password=Vp6@technology123;instance=SQLEXPRESS;";
            String username = "vp6stow";
            String password = "Vp6@technology123";
            conn = DriverManager.getConnection(connString, username, password);
            Log.w("Connection", "open");
            Statement stmt = conn.createStatement();
            ResultSet cursor = stmt.executeQuery("Select * from "+newmail+"contacts");
            Log.i("abve", "driver9");


            while (cursor.next()) {

                String DisplayName = cursor.getString(3);
//                String MobileNumber = cursor.getString(4);
//                String HomeNumber = cursor.getString(5);
//                String WorkNumber = cursor.getString(6);
//                String WorkMobileNumber = cursor.getString(7);
//                String Others = cursor.getString(8);
//                String FaxWork = cursor.getString(9);
                String Number = cursor.getString(4);
                String Type = cursor.getString(5);

//                Log.i("contact", DisplayName + " " + MobileNumber + " " + HomeNumber + " " + WorkNumber + " " + WorkMobileNumber + " " + Others + " " + FaxWork);
                ops = new ArrayList<>();

                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());


                if (DisplayName != null && DisplayName != "") {
                    //------------------------------------------------------ Names

                    ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(
                                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    DisplayName).build());

                    //------------------------------------------------------ Mobile Number
                    if (Type.equals("1")) {
                        ops.add(ContentProviderOperation.
                                newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, Number)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                .build());
                    }


                    //------------------------------------------------------ Home Numbers
                    if (Type.equals("2")) {
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, Number)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                                .build());
                    }

                    //------------------------------------------------------ Work Numbers
                    if (Type.equals("3")) {
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, Number)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                                .build());
                    }


                    if (Type.equals("4")) {
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, Number)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE)
                                .build());
                    }

                    if (Type.equals("5")) {
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, Number)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)
                                .build());
                    }


                    if (Type.equals("6")) {
                        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, Number)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                        ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK)
                                .build());
                    }

                }


                // Asking the Contact provider to create a new contact
                try {
                   mcontext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    Log.i(DisplayName," "+Number);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mcontext, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            conn.close();
        }
        catch(Exception e){
            Log.w("Error connectio0n", "" + e);

            return false;
        }
        return  true;
    }



}
