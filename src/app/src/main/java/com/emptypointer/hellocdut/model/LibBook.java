package com.emptypointer.hellocdut.model;

/**
 * Created by Sequarius on 2015/11/7.
 */
public class LibBook {
    private String title;
    private String id;
    private String borrowTime;
    private String returnTime;
    private String location;
    private String indexID;
    private String renewURL;
    private String renewTime;

    public LibBook(String title, String id, String reurnTime) {
        super();
        this.title = title;
        this.id = id;
        this.returnTime = reurnTime;
    }

    public LibBook(String title, String id, String borrowTime,
                        String returnTime, String location, String indexID, String renewURL,
                        String renewTime) {
        super();
        this.title = title;
        this.id = id;
        this.borrowTime = borrowTime;
        this.returnTime = returnTime;
        this.location = location;
        this.indexID = indexID;
        this.renewURL = renewURL;
        this.renewTime = renewTime;
    }


    public String getRenewTime() {
        return renewTime;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getBorrowTime() {
        return borrowTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public String getLocation() {
        return location;
    }

    public String getIndexID() {
        return indexID;
    }

    public String getRenewURL() {
        return renewURL;
    }

}
