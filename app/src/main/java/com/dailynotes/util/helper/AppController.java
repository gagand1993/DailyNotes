package com.dailynotes.util.helper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;



public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();

    private static AppController mInstance;
    SharedPreferences preferences;
    SharedPreferences prefToken;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editorToken;
    public static final String PREF_TOKEN = "DailyNotes";




    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initializePreferences();
        initializePreferencesToken();





        //  Places.initialize(this, "AIzaSyCwz_1qgkyzjhPml3CSMPJ03ONDfDLFT1o");
    }


    public boolean checkIfHasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }



    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    // initialize shared preferences
    private void initializePreferences() {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
    }

    // initialize shared preferences for token
    private void initializePreferencesToken() {
        prefToken = getSharedPreferences(PREF_TOKEN, Context.MODE_PRIVATE);
        editorToken = prefToken.edit();
    }

    public void setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public void setInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }


    public void clearData() {
        preferences.edit().clear().commit();
    }

    public void setDeviceToken(String key, String value) {
        editorToken.putString(key, value);
        editorToken.commit();
    }

    public String getDeviceToken(String key, String def_value) {
        return prefToken.getString(key, def_value);
    }


    public void setSaveREMEMBER(String key, String value) {
        editorToken.putString(key, value);
        editorToken.commit();
    }

    public String getSaveLREMEMBER(String key, String def_value) {
        return prefToken.getString(key, def_value);
    }







}