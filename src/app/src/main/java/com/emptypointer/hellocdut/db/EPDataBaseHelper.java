package com.emptypointer.hellocdut.db;

/**
 * Created by Sequarius on 2015/11/1.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 统一的数据库表单创建类
 *
 * @author Sequarius
 */
public class EPDataBaseHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "hellocdut.db";
    private static final int VERSION = 5;
    private static EPDataBaseHelper instance;

    private static final String SCHEDULE_TABLE_CREATE = "CREATE TABLE [schedule] ("
            + "[id] INTEGER  NOT NULL PRIMARY KEY autoincrement,"
            + "[week_num] VARCHAR(50),"
            + "[what_day] INTEGER,"
            + "[begin] INTEGER,"
            + "[section] INTEGER,"
            + "[full_name] VARCHAR(50),"
            + "[category] VARCHAR(10),"
            + "[is_OverLap_Class] INTEGER,"
            + "[credits] CHAR(4),"
            + "[teacher] VARCHAR(10),"
            + "[period] CHAR(3),"
            + "[room] VARCHAR(10)," + "[note] VARCHAR(50));";
    // json緩存
    private static final String CACHE_DATA = "CREATE TABLE [data_cache] ("
            + "[key] VARCHAR(40) NOT NULL," + "[data] TEXT,"
            + "[update_time] BIGINT);";

    // ////
    private static String getUserDatabaseName() {
        return DBNAME;
    }

    // ///////
    public static EPDataBaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new EPDataBaseHelper(context);
        }
        return instance;
    }

    private EPDataBaseHelper(Context context) {
        super(context, getUserDatabaseName(), null, VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCHEDULE_TABLE_CREATE);
        db.execSQL(CACHE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        if (oldVersion < 5) {
//            db.execSQL("DROP TABLE " + DBNAME + ";");
            db.execSQL(SCHEDULE_TABLE_CREATE);
            db.execSQL(CACHE_DATA);
        }

    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

}
