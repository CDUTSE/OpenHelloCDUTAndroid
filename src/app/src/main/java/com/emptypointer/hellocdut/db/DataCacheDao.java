package com.emptypointer.hellocdut.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emptypointer.hellocdut.model.DataCache;

/**
 * Created by Sequarius on 2015/11/2.
 */
public class DataCacheDao {

    private EPDataBaseHelper dbHelper;

    public DataCacheDao(Context context) {
        dbHelper = EPDataBaseHelper.getInstance(context);
    }

    public void saveCache(String key, String data) {
        if (getCache(key) == null) {
            insertData(key, data);
        } else {
            updateData(key, data);
        }
    }

    public DataCache getCache(String key) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            if (db.isOpen()) {
                Cursor cursor = db.rawQuery(
                        "SELECT data,update_time FROM data_cache WHERE key=?",
                        new String[]{key});
                if (cursor.moveToNext()) {
                    String data = cursor.getString(cursor
                            .getColumnIndex("data"));
                    long time = cursor.getLong(cursor
                            .getColumnIndex("update_time"));
                    cursor.close();
                    if (data.isEmpty() || data.equals("null")) {
                        return null;
                    } else {
                        return new DataCache(key, data, time);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void updateData(String key, String data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL(
                    "UPDATE [data_cache] SET data=?,update_time=? where key=?",
                    new Object[]{data, System.currentTimeMillis(), key});
        }
    }

    private void insertData(String key, String data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL(
                    "INSERT INTO [data_cache]([key],[data],[update_time]) VALUES(?,?,?)",
                    new Object[]{key, data, System.currentTimeMillis()});
        }
    }

    public void cleanData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("DELETE FROM [data_cache]");
        }
    }
}
