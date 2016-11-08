package com.emptypointer.hellocdut.customer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;


import com.karumi.dexter.Dexter;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.rey.material.app.ThemeManager;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sequarius on 2015/10/22.
 */
public class EPApplication extends Application {

    private static EPApplication instance;

    private List<Activity> mActivityStack;

    public static EPApplication getInstance(){
        return instance;
    }

    private static DisplayImageOptions mDisplayImageOptions;

    @Override
    public void onCreate() {
        super.onCreate();
        ThemeManager.init(this, 2, 0, null);
        instance=this;
        Dexter.initialize(getBaseContext());

//        Fresco.initialize(instance);
        CrashReport.initCrashReport(instance, "900008333", false);
//        EMHelper.getInstance().init(instance);
        initImageLoader(this);
        getResources();
//        MultiDex.install(this);
    }


    public void pushActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new ArrayList<Activity>();
        }
        mActivityStack.add(activity);
    }

    public void removeActivity(Activity activity) {
        if (mActivityStack.contains(activity) && mActivityStack != null) {
            mActivityStack.remove(activity);
        }
    }

    public void clearActivities() {
        if (mActivityStack != null) {
            for (Activity activity : mActivityStack) {
                if (activity != null) {
                    activity.finish();
                }
            }
        }
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
                context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();


        // config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

    }

    public DisplayImageOptions getDisplayImageOptions(){
        if (mDisplayImageOptions==null){
            mDisplayImageOptions=new DisplayImageOptions.Builder()
                    .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
        }
        return mDisplayImageOptions;
    }
}
