package com.emptypointer.hellocdut.customer;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;


/**
 * Created by Sequarius on 2015/10/25.
 */
public class EPJsonResponseListener extends EPBaseJsonResponeseListener{

    private static final String TAG = "EPJsonResponseListener";

    public EPJsonResponseListener(Context mContext) {
        super(mContext);
    }

    @Override
    public void onResponse(JSONObject response) {
        super.onResponse(response);
        Log.i(TAG,"get Json Success ,content:"+response.toString());
    }
}
