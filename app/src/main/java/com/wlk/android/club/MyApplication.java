package com.wlk.android.club;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.wlk.android.club.database.DaoFactory;
import com.wlk.android.club.database.DaoSession;
import com.wlk.android.club.database.TestData;
import com.wlk.android.club.database.TestDataDao;

/**
 * Created by wanglunkui on 2018/1/18.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init(){
        DaoFactory.getInstance().init(this);
        DaoSession session = DaoFactory.getInstance().daoSession;
        TestDataDao dao = session.getTestDataDao();
        TestData data = new TestData();
        dao.insert(data);
        dao.update(data);
        dao.delete(data);
        session.load(TestData.class, 1L);
        Fresco.initialize(this);
//        session.queryRaw(TestData.class, "sql", "args");
    }
}
