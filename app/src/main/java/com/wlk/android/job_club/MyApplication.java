package com.wlk.android.job_club;

import android.app.Application;

import com.wlk.android.job_club.database.DaoFactory;
import com.wlk.android.job_club.database.DaoSession;
import com.wlk.android.job_club.database.TestData;
import com.wlk.android.job_club.database.TestDataDao;

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
        session.queryRaw(TestData.class, "sql", "args");
    }
}
