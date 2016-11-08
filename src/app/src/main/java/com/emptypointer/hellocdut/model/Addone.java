package com.emptypointer.hellocdut.model;

/**
 * Created by Sequarius on 2015/11/7.
 */
public class Addone {
    private String name;
    private String packageName;
    private String action;
    private String author;
    private String updateTime;
    private String Version;
    private String dowloadCount;
    private String icUrl;
    private String addoneUrl;
    private String introduction;

    public Addone(String name, String packageName, String action,
                  String author, String updateTime, String version,
                  String dowloadCount, String icUrl, String addoneUrl,
                  String introduction) {
        super();
        this.name = name;
        this.packageName = packageName;
        this.action = action;
        this.author = author;
        this.updateTime = updateTime;
        Version = version;
        this.dowloadCount = dowloadCount;
        this.icUrl = icUrl;
        this.addoneUrl = addoneUrl;
        this.introduction = introduction;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAction() {
        return action;
    }

    public String getAuthor() {
        return author;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getVersion() {
        return Version;
    }

    public String getDowloadCount() {
        return dowloadCount;
    }

    public String getIcUrl() {
        return icUrl;
    }

    public String getAddoneUrl() {
        return addoneUrl;
    }

    public String getIntroduction() {
        return introduction;
    }


}