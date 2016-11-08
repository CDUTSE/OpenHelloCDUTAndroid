package com.emptypointer.hellocdut.customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.ui.account.LoginActivity;
import com.emptypointer.hellocdut.ui.main.EvaluationActivity;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.DialogButtonClickListner;
import com.emptypointer.hellocdut.utils.UIUtils;


/**
 * Created by Sequarius on 2015/10/25.
 */
public class EPBaseJsonResponeseListener implements Response.Listener<JSONObject> {
    protected boolean result;
    private Context mContext;
    protected String mMessage;
    private String[] mIgnoreMessage = {"登陆成功"};

    public EPBaseJsonResponeseListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            result = response.getBoolean("result");
            if (!result) {
                mMessage = response.getString("message");
                UIUtils.makeSnake(mContext, mMessage, true);
                if (mMessage.equals(Constant.ERROR_MESSAGE_RELOG)) {
//                    EMHelper.getInstance().logout(false, new EMCallBack() {
//                        @Override
//                        public void onSuccess() {
//                        }
//
//                        @Override
//                        public void onError(int i, String s) {
//
//                        }
//
//                        @Override
//                        public void onProgress(int i, String s) {
//
//                        }
//                    });
                    UserInfo.getInstance(mContext).setToken("");
                    mContext.getSharedPreferences(com.emptypointer.hellocdut.utils.Constant.SHARED_PERFERENCR_CATCH, Context.MODE_APPEND).edit().clear();
                    mContext.getSharedPreferences(com.emptypointer.hellocdut.utils.Constant.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE).edit().clear();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(mContext,
                                    LoginActivity.class);
                            mContext.startActivity(intent);
                            EPApplication.getInstance().clearActivities();
                        }
                    }, 1200);
                } else if (mMessage.contains("教学评价再查询")) {
                    UIUtils.showCommonDialog(mContext, mContext.getString(R.string.prompt), mMessage, new DialogButtonClickListner() {
                        @Override
                        public void onclick(int index) {
                            Intent intent = new Intent(mContext, EvaluationActivity.class);
                            mContext.startActivity(intent);
                        }
                    }, null);
                }
                return;
            }
            if (!response.containsKey("message")) {
                return;
            }
            mMessage = response.getString("message");
            for (String igMsg : mIgnoreMessage) {
                if (igMsg.equals(mMessage)) {
                    return;
                }
            }
            UIUtils.makeSnake(mContext, mMessage, true);
        } catch (JSONException e) {
            result = false;
            e.printStackTrace();
            UIUtils.makeSnake(mContext, mContext.getString(R.string.message_unparsed_json), false);
        }
    }
}
