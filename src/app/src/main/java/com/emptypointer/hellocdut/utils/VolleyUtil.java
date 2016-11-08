package com.emptypointer.hellocdut.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Sequarius on 2015/10/25.
 */
public class VolleyUtil {
    private volatile static RequestQueue requestQueue;

    /** 返回RequestQueue单例 **/
    public static RequestQueue getQueue(Context context) {
        if (requestQueue == null) {
            synchronized (VolleyUtil.class) {
                if (requestQueue == null) {
                    requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                }
            }
        }
        return requestQueue;
    }
}
