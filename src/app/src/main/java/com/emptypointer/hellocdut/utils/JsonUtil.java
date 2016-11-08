package com.emptypointer.hellocdut.utils;


import com.alibaba.fastjson.JSONObject;

/**
 * Created by Sequarius on 215/12/1.
 */
public class JsonUtil {
    public static double getDoublefaiOver(JSONObject obj,String key,double defaultValue){
        if(StringChecker.isFloat(obj.getString(key))){
            return obj.getDoubleValue(key);
        }else{
            return defaultValue;
        }
    }
    public static double getDoublefaiOver(JSONObject obj,String key){
        return getDoublefaiOver( obj, key, 0);
    }
}
