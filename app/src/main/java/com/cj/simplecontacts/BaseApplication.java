package com.cj.simplecontacts;

import android.app.Application;

import com.cj.simplecontacts.enity.DaoMaster;
import com.cj.simplecontacts.enity.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created by chenjun on 17-7-25.
 */

public class BaseApplication extends Application {
    private static DaoSession daoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        //配置数据库
        setupDatabase();
    }
    /**
     * 配置数据库
     */
    private void setupDatabase() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(),"NumAttribution.db",null);
        Database writableDb = devOpenHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(writableDb);
        daoSession = daoMaster.newSession();
    }
    public static DaoSession getDaoInstant() {
        return daoSession;
    }
}
