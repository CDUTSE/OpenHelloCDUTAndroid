package com.emptypointer.hellocdut.model;

/**
 * Created by Sequarius on 2015/11/1.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;

public class Course implements Serializable {
    private int id;
    private String weekNum;
    private int whatDay;
    private int begin;
    private int section;
    private String fullName;
    private String categroy;
    private int overLapStatus;
    private String credits;
    private String teacher;
    private String period;
    private String room;
    private String note;

    public Course(int id, String weekNum, int whatDay, int begin, int section,
                  String fullName, String categroy, int overLapStatus,
                  String credits, String teacher, String period, String room,
                  String note) {
        super();
        this.id = id;
        this.weekNum = weekNum;
        this.whatDay = whatDay;
        this.begin = begin;
        this.section = section;
        this.fullName = fullName;
        this.categroy = categroy;
        this.overLapStatus = overLapStatus;
        this.credits = credits;
        this.teacher = teacher;
        this.period = period;
        this.room = room;
        this.note = note;
    }

    public Course(String weekNum, int whatDay, int begin, int section,
                  String fullName, String categroy, int overLapStatus,
                  String credits, String teacher, String period, String room,
                  String note) {
        super();
        this.weekNum = weekNum;
        this.whatDay = whatDay;
        this.begin = begin;
        this.section = section;
        this.fullName = fullName;
        this.categroy = categroy;
        this.overLapStatus = overLapStatus;
        this.credits = credits;
        this.teacher = teacher;
        this.period = period;
        this.room = room;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public String getWeekNum() {
        return weekNum;
    }

    public int getWhatDay() {
        return whatDay;
    }

    public int getBegin() {
        return begin;
    }

    public int getSection() {
        return section;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCategroy() {
        return categroy;
    }

    public int getOverLapStatus() {
        return overLapStatus;
    }

    public String getCredits() {
        return credits;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getPeriod() {
        return period;
    }

    public String getRoom() {
        return room;
    }

    public String getNote() {
        return note;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((fullName == null) ? 0 : fullName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Course other = (Course) obj;
        if (fullName == null) {
            if (other.fullName != null)
                return false;
        } else if (!fullName.equals(other.fullName))
            return false;
        return true;
    }

    public List<Integer> getCourseWeek() {
        List<Integer> weeks = new ArrayList<Integer>();
        JSONArray array = JSONArray.parseArray(weekNum);
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                Integer num = array.getInteger(i);
                weeks.add(num);
            }
        }
        return weeks;
    }

}

