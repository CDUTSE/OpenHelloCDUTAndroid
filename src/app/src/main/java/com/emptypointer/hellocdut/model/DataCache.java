package com.emptypointer.hellocdut.model;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by Sequarius on 2015/11/2.
 */
public class DataCache {
    private String key;
    private String date;
    private long time;

    public DataCache(String key, String date) {
        super();
        this.key = key;
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public String getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }

    public DataCache(String key, String date, long time) {
        super();
        this.key = key;
        this.date = date;
        this.time = time;
    }

    public JSONObject getDataJson(){
        return JSONObject.parseObject(date);
    }


}

