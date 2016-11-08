package com.emptypointer.hellocdut.model;

/**
 * Created by Sequarius on 2015/10/28.
 */
public class FunctionItem {
    private int iconResID;
    private String fuctionName;

    public int getIconResID() {
        return iconResID;
    }

    public void setIconResID(int iconResID) {
        this.iconResID = iconResID;
    }

    public String getFuctionName() {
        return fuctionName;
    }

    public void setFuctionName(String fuctionName) {
        this.fuctionName = fuctionName;
    }

    public FunctionItem(int iconResID, String fuctionName) {
        super();
        this.iconResID = iconResID;
        this.fuctionName = fuctionName;
    }
}
