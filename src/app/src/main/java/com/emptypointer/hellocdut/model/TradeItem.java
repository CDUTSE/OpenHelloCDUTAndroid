package com.emptypointer.hellocdut.model;

/**
 * Created by Sequarius on 2015/11/8.
 */
public class TradeItem {
    private String time;
    private String describe;
    private String amount;
    private String balance;
    private String opertator;
    private String location;
    private String name;

    public String getTime() {
        return time;
    }

    public String getDescribe() {
        return describe;
    }

    public String getAmount() {
        return amount;
    }

    public String getBalance() {
        return balance;
    }

    public String getOpertator() {
        return opertator;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public TradeItem(String time, String amount, String balance,
                     String opertator, String location, String name) {
        super();
        this.time = time;
        this.amount = amount;
        this.balance = balance;
        this.opertator = opertator;
        this.location = location;
        this.name = name;
    }

    public TradeItem(String time, String describe, String amount,
                     String opertator, String name) {
        super();
        this.time = time;
        this.describe = describe;
        this.amount = amount;
        this.opertator = opertator;
        this.name = name;
    }


}

