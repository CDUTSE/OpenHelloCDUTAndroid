package com.emptypointer.hellocdut.model;

/**
 * Created by Sequarius on 2016/4/15.
 */
public class EvaluationItem {
    private String className;
    private String id;
    private String classId;
    private String teacher;
    private boolean evaluated;

    public EvaluationItem() {
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    public EvaluationItem(String className, String id, String classId, String teacher, boolean evaluated) {
        this.className = className;
        this.id = id;
        this.classId = classId;
        this.teacher = teacher;
        this.evaluated = evaluated;
    }

    public String getClassName() {
        return className;
    }

    public String getId() {
        return id;
    }

    public String getClassId() {
        return classId;
    }

    public String getTeacher() {
        return teacher;
    }

    public boolean isEvaluated() {
        return evaluated;
    }
}
