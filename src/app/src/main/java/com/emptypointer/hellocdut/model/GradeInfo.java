package com.emptypointer.hellocdut.model;

/**
 * Created by Sequarius on 2015/11/2.
 */
public class GradeInfo {
    private String name;
    private String score;
    private String updateTime;
    private String semester;
    private String CourseID;
    private double credits;
    private String teacherName;
    private String gpa;

    private String type;

    public String getGpa() {
        return gpa;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getSemester() {
        return semester;
    }

    public String getCourseID() {
        return CourseID;
    }

    public double getCredits() {
        return credits;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public GradeInfo(String name, String score, String updateTime,
                     String semester, String courseID, double credits, String teacherName,String gpa,String type) {
        super();
        this.name = name;
        this.score = score;
        this.updateTime = updateTime;
        this.semester = semester;
        CourseID = courseID;
        this.credits = credits;
        this.teacherName = teacherName;
        this.gpa=gpa;
        this.type=type;
    }

}
