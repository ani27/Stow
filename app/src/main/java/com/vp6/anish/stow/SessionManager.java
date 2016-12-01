package com.vp6.anish.stow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.widget.NumberPicker;

import java.util.HashMap;

/**
 * Created by anish on 29-06-2016.
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "Stow";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_RECORDING = "IsRecordingOn";
    private static final String HAS_FOLDER_CREATED = "hasFolderCreated";

    private static final String LAST_SYNC = "LastSync";

    private static final String IS_SYNCING = "IsSyncing";
    // User name (make variable public to access from outside)
    public static final String KEY_FIRST_NAME = "firstname";
    public static final String KEY_LAST_NAME = "lastname";
    public static final String KEY_NUMBER    = "number";
    public static final String KEY_PASSWORD = "password";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";


    public static void setIsUserLogin(Context context, boolean isUserSignUp) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putBoolean(IS_LOGIN, isUserSignUp);
        editor.commit();
    }

    public static boolean isUserLogIn(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME,Context.MODE_APPEND);
        return savedSession.getBoolean(IS_LOGIN, false);
    }











    public static String  hasfolderchoosed(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND);
        return savedSession.getString(HAS_FOLDER_CREATED, Environment.getExternalStorageDirectory().getAbsolutePath()+"/Stow/");
    }

    public static void setFolderChoosed(Context context, String FolderChoosed) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putString(HAS_FOLDER_CREATED,FolderChoosed);
        editor.commit();
    }
















    public static void setIsSyncing(Context context, boolean isSyncing) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putBoolean(IS_SYNCING, isSyncing);
        editor.commit();
    }

    public static boolean isSyncing(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME,Context.MODE_APPEND);
        return savedSession.getBoolean(IS_SYNCING, false);
    }







    public static void setLastSync(Context context, Long lastsync) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_APPEND).edit();
        editor.putLong(LAST_SYNC, lastsync);
        editor.commit();
    }

    public static Long getLastSync(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME,Context.MODE_APPEND);
        return savedSession.getLong(LAST_SYNC, 0);
    }








    public static void setUserFirstName(Context context, String name) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit();
        editor.putString(KEY_FIRST_NAME, name);
        editor.commit();
    }

    public static String getUserFirstName(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        return savedSession.getString(KEY_FIRST_NAME, "");
    }








    public static void setUserLastName(Context context, String name) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit();
        editor.putString(KEY_LAST_NAME, name);
        editor.commit();
    }

    public static String getUserLastName(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        return savedSession.getString(KEY_LAST_NAME, "");
    }










    public static void setKeyEmail(Context context, String email) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit();
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public static String getKeyEmail(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        return savedSession.getString(KEY_EMAIL, "");
    }












    public static void setKeyPassword(Context context, String password) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit();
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    public static String getKeyPassword(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        return savedSession.getString(KEY_PASSWORD, "");
    }

















    public static void setKeyNumber(Context context, String number) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit();
        editor.putString(KEY_NUMBER, number);
        editor.commit();
    }

    public static String getKeyNumber(Context context) {

        SharedPreferences savedSession = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        return savedSession.getString(KEY_NUMBER, "");
    }

}
