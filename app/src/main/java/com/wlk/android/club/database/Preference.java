package com.wlk.android.club.database;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by wanglunkui on 2018/1/18.
 */

public class Preference {
    Application application;
    ContentResolver contentResolver;
    public static final String AUTHORITY = "com.wlk.ClubxProvider";
    public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/clubx_preference_setting");

    public Preference(){
        contentResolver = getApplication().getContentResolver();
    }

    public void save(String key, String value, String uid){
        if(value == null) return;
        ContentValues contentValues = new ContentValues();
        contentValues.put("k", key);
        contentValues.put("v", value);
        contentValues.put("uid", uid);
        contentResolver.insert(URI, contentValues);
    }

    public String get(String key, String uid){
        if(key == null) return null;
        //LogUtil.i("prefrence", "key:"+key+", userId:"+userId);
        Cursor cursor = contentResolver.
                query(URI, null, null, new String[]{"", key}, null);
        String value = null;
        if (cursor == null) return null;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            value = cursor.getString(cursor.getColumnIndex("v"));
        }
        //LogUtil.i("prefrence", "get from provider " + key + " -- " + value);

        cursor.close();
        return value;
    }


    public void saveGlobal(String key, String value) {
        save(key, value, "");
    }

    public void getGlobal(String key){
        get(key, "");
    }

    protected Application getApplication() {
        if (application == null) {
            try {
                application = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
            } catch (Exception e) {
                try {
                    application = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null, (Object[]) null);
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }

        }
        return application;
    }
}
