package com.emptypointer.hellocdut.db;

/**
 * Created by Sequarius on 2015/11/1.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.emptypointer.hellocdut.model.Course;


/**
 * 课表的DAO
 *
 * @author Sequarius
 */
public class ScheduleDao {
    private EPDataBaseHelper dbHelper;

    public ScheduleDao(Context context) {
        dbHelper = EPDataBaseHelper.getInstance(context);
    }

    public void addCourse(Course course) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            String sql = "INSERT INTO [schedule] ([week_num], [what_day], [begin], [section], [full_name], [category], [is_OverLap_Class], [credits], [teacher], [period], [room], [note]) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
            db.execSQL(
                    sql,
                    new Object[]{course.getWeekNum(), course.getWhatDay(),
                            course.getBegin(), course.getSection(),
                            course.getFullName(), course.getCategroy(),
                            course.getOverLapStatus(), course.getCredits(),
                            course.getTeacher(), course.getPeriod(),
                            course.getRoom(), course.getNote()});
        }
    }

    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("DELETE FROM [schedule]");
        }
    }

    public List<Course> getAllCourse() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Course> courses = new ArrayList<Course>();
        if (db.isOpen()) {
            String sql = "SELECT * FROM [schedule]";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String weekNum = cursor.getString(cursor
                        .getColumnIndex("week_num"));
                int wahtDay = cursor.getInt(cursor.getColumnIndex("what_day"));
                int begin = cursor.getInt(cursor.getColumnIndex("begin")) + 1;
                int section = cursor.getInt(cursor.getColumnIndex("section"));
                String fullName = cursor.getString(cursor
                        .getColumnIndex("full_name"));
                String category = cursor.getString(cursor
                        .getColumnIndex("category"));
                int overLapStatus = cursor.getInt(cursor
                        .getColumnIndex("is_OverLap_Class"));
                String credits = cursor.getString(cursor
                        .getColumnIndex("credits"));
                String teacher = cursor.getString(cursor
                        .getColumnIndex("teacher"));
                String period = cursor.getString(cursor
                        .getColumnIndex("period"));
                String room = cursor.getString(cursor.getColumnIndex("room"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                Course course = new Course(id, weekNum, wahtDay, begin,
                        section, fullName, category, overLapStatus, credits,
                        teacher, period, room, note);
                courses.add(course);

            }
            cursor.close();
        }
        return courses;
    }
}
