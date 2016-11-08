package com.emptypointer.hellocdut.service;

import android.content.Context;

import com.emptypointer.hellocdut.db.DataCacheDao;
import com.emptypointer.hellocdut.model.DataCache;

/**
 * Created by Sequarius on 2015/11/2.
 */
public class CacheService {
    private DataCacheDao mDao;

    private static CacheService instance;
    public DataCache getDataCache(String key){
        return mDao.getCache(key);
    }

    public void setDataCache(DataCache cache) {
        mDao.saveCache(cache.getKey(), cache.getDate());
    }

    public void setDataCache(String key,String data) {
        mDao.saveCache(key, data);
    }

    public static CacheService getInstance(Context context){
        if(instance==null){
            instance=new CacheService(context);
        }
        return instance;
    }

    public void cleanCache(){
        mDao.cleanData();
    }

    private CacheService(Context context){
        mDao=new DataCacheDao(context);
    }

}
