package com.emptypointer.hellocdut.model;

/**
 * Created by Sequarius on 2015/11/2.
 */
public class TeachingPlan {
    private String name;
    private String majro;
    private String makedate;
    private long id;
    private String url;

    public TeachingPlan(String name, String majro, String makedate, long id, String url) {
        this.name = name;
        this.majro = majro;
        this.makedate = makedate;
        this.id = id;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getMajro() {
        return majro;
    }

    public String getMakedate() {
        return makedate;
    }

    public String getUrl() {
        return url;
    }
}