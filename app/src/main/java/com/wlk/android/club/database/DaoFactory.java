package com.wlk.android.club.database;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wanglunkui on 2018/1/18.
 */

public class DaoFactory {
    public DaoSession daoSession;
    private static DaoFactory instance;

    public static DaoFactory getInstance(){
        if(instance == null){
            synchronized (DaoFactory.class){
                if(instance == null){
                    instance = new DaoFactory();
                }
            }
        }
        return instance;
    }

    private DaoFactory(){

    }

    public void init(Application application){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(application, "test.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
}
