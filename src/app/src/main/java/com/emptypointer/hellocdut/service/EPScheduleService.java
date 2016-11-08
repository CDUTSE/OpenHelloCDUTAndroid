package com.emptypointer.hellocdut.service;

/**
 * Created by Sequarius on 2015/11/1.
 */
import android.content.Context;
import android.util.Log;

import com.emptypointer.hellocdut.db.ScheduleDao;
import com.emptypointer.hellocdut.model.Course;
import com.emptypointer.hellocdut.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EPScheduleService {
    private static final String TAG = "EPScheduleService";
    private ScheduleDao mDao;
    private List<Course> mCourses;

    public EPScheduleService(Context mContext) {
        super();
        mDao = new ScheduleDao(mContext);
        mCourses = mDao.getAllCourse();
    }

    public void notifyDataSetChanged() {
        mCourses = mDao.getAllCourse();
    }

    public void clearSchedule() {
        if (!isEmptyShedule()) {
            mDao.deleteAll();
        }
    }

    /**
     * 获取对应星期的所有课程
     *
     * @param week
     * @return
     */
    public List<Course> getCourseByWeek(int week) {
        List<Course> resultList = new ArrayList<Course>();
        for (Course course : mCourses) {
            List<Integer> weeks = course.getCourseWeek();
            if (weeks.contains(week)) {
                resultList.add(course);
            }

        }
        return resultList;
    }

    /**
     * 获取所有课程的最大周数
     *
     * @return
     */
    public int getMaxWeek() {
        int max = 0;
        for (Course course : mCourses) {
            List<Integer> weeks = course.getCourseWeek();
            int temp = weeks.get(weeks.size() - 1);
            if (temp > max) {
                max = temp;
            }
        }
        return max;
    }

    /**
     * 判断中午是否有课
     *
     * @return
     */
    public boolean hasNoonClass() {
        for (Course cours : mCourses) {
            if (cours.getBegin() == 5)
                return true;
        }
        return false;
    }

    public void getCourseListByWeek(int week, List<List<Course>> couseList) {
        couseList.clear();
        List<Course> monday, tuesday, wednesday, thursday, friday, saturday, sunday;

        monday = new ArrayList<Course>();
        tuesday = new ArrayList<Course>();
        wednesday = new ArrayList<Course>();
        thursday = new ArrayList<Course>();
        friday = new ArrayList<Course>();
        saturday = new ArrayList<Course>();
        sunday = new ArrayList<Course>();
        for (Course course : mCourses) {
            List<Integer> weeks = course.getCourseWeek();
            int day = course.getWhatDay();
            if (weeks.contains(week)) {
                switch (day) {
                    case 1:
                        monday.add(course);
                        break;
                    case 2:
                        tuesday.add(course);
                        break;
                    case 3:
                        wednesday.add(course);
                        break;
                    case 4:
                        thursday.add(course);
                        break;
                    case 5:
                        friday.add(course);
                        break;
                    case 6:
                        saturday.add(course);
                        break;
                    case 7:
                        sunday.add(course);
                        break;

                    default:
                        break;
                }
            }
        }
        couseList.add(new ArrayList<Course>());
        couseList.add(monday);
        couseList.add(tuesday);
        couseList.add(wednesday);
        couseList.add(thursday);
        couseList.add(friday);
        couseList.add(saturday);
        couseList.add(sunday);
        Log.i(TAG, "course list size is===" + couseList.size());
    }

    /**
     * 获取指定星期的所有课程
     *
     * @param courseInfoMap
     * @param week
     * @return
     */
    public Map<String, List<Course>> getAllByWeek(
            Map<String, List<Course>> courseInfoMap, int week) {
        List<Course> item1, item2, item3, item4, item5, item6, item7;
        item1 = new ArrayList<Course>();
        item2 = new ArrayList<Course>();
        item3 = new ArrayList<Course>();
        item4 = new ArrayList<Course>();
        item5 = new ArrayList<Course>();
        item6 = new ArrayList<Course>();
        item7 = new ArrayList<Course>();
        for (Course course : mCourses) {
            List<Integer> weeks = course.getCourseWeek();
            int day = course.getWhatDay();
            if (weeks.contains(week)) {
                switch (day) {
                    case 1:
                        item1.add(course);
                        break;
                    case 2:
                        item2.add(course);
                        break;
                    case 3:
                        item3.add(course);
                        break;
                    case 4:
                        item4.add(course);
                        break;
                    case 5:
                        item5.add(course);
                        break;
                    case 6:
                        item6.add(course);
                        break;
                    case 7:
                        item7.add(course);
                        break;

                    default:
                        break;
                }
            }
        }
        courseInfoMap.put("monday", item1);
        courseInfoMap.put("tuesday", item2);
        courseInfoMap.put("wednesday", item3);
        courseInfoMap.put("thursday", item4);
        courseInfoMap.put("friday", item5);
        courseInfoMap.put("saturday", item6);
        courseInfoMap.put("sunday", item7);
        return courseInfoMap;
    }

    public Map<String, Integer> getColorMap(Map<String, Integer> colorMap) {
        int id = 0;
        String fullName = "";
        for (Course course : mCourses) {
            String courseMd5 = CommonUtils.getStringMD5(course.getFullName());
            if (!fullName.contains(course.getFullName())) {
                colorMap.put(courseMd5, id);
                id++;
                fullName += course.getFullName();
                if (id == 19) {
                    id = id - 19;
                }
            }
        }
        return colorMap;
    }

    public Map<String, Integer> getColorMap2() {
        Map<String, Integer> colorMap = new HashMap<String, Integer>();
        for (Course course : mCourses) {
            String courseName = course.getFullName();
            int length = courseName.length() > 20 ? 1 : courseName.length();
            while (colorMap.values().contains(length)) {
                length++;
                if (length == 20) {
                    length = 1;
                }

            }
            colorMap.put(courseName, length);

        }
        return colorMap;
    }

    public boolean isEmptyShedule() {
        return mDao.getAllCourse().size() == 0;
    }
}
