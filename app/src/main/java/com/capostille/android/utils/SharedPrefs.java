package com.capostille.android.utils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.capostille.android.activities.LoginActivity;

import java.util.HashMap;

public class SharedPrefs
{
    // User name (make variable public to access from outside)


    public static final String IS_PAID = "IS_PAID";
    // Sharedpref file name
    private static final String PREF_NAME = "Capostille";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String LOGIN_DATA="LOGIN_DATA";
    public static final String STATUS_DATA="STATUS_DATA";
    public static final String STATUS_DATA_COMPLETE="STATUS_DATA_COMPLETE";
    public static final String STATUS_DATA_PENDING="STATUS_DATA_PENDING";
    public static final String STATUS_DATA_PROCESS="STATUS_DATA_PROCESS";
    public static final String DEVICE_ID="DEVICE_ID";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    Context context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SharedPrefs(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     *  *
     */

    public void LoginSuccess() {

        editor.putBoolean(IS_LOGIN, true);
        editor.commit();

    }

    public void logOut()
    {
        editor.clear();
        editor.commit();
        Intent i=new Intent(context, LoginActivity.class);
        context.startActivity(i);
    }



    public boolean isLoggedIn()
    {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void saveLoginData(String res)
    {
        editor.putString(LOGIN_DATA, String.valueOf(res));
        editor.commit();
    }
    public HashMap<String, String> getLoginData()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name

        user.put(LOGIN_DATA, pref.getString(LOGIN_DATA, "0"));

        return user;
    }

    public void saveStatus(String res)
    {
        editor.putString(STATUS_DATA, String.valueOf(res));
        editor.commit();
    }


    public void saveCompleteStatus(String res)
    {
        editor.putString(STATUS_DATA_COMPLETE, String.valueOf(res));
        editor.commit();
    }

    public void savePendingteStatus(String res)
    {
        editor.putString(STATUS_DATA_PENDING, String.valueOf(res));
        editor.commit();
    }

    public void saveProcessteStatus(String res)
    {
        editor.putString(STATUS_DATA_PROCESS, String.valueOf(res));
        editor.commit();
    }

    public HashMap<String, String> getStatus()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name

        user.put(STATUS_DATA, pref.getString(STATUS_DATA, ""));

        return user;
    }

    public void saveDeviceId(String res)
    {
        editor.putString(DEVICE_ID, String.valueOf(res));
        editor.commit();
    }

    public HashMap<String, String> getDeviceID()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name

        user.put(DEVICE_ID, pref.getString(DEVICE_ID, ""));

        return user;
    }

    public HashMap<String, String> getCompleteStatus()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name

        user.put(STATUS_DATA_COMPLETE, pref.getString(STATUS_DATA_COMPLETE, ""));

        return user;
    }

    public HashMap<String, String> getPEndingStatus()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name

        user.put(STATUS_DATA_PENDING, pref.getString(STATUS_DATA_PENDING, ""));

        return user;
    }

    public HashMap<String, String> getProcessStatus()
    {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name

        user.put(STATUS_DATA_PROCESS, pref.getString(STATUS_DATA_PROCESS, ""));

        return user;
    }





}

