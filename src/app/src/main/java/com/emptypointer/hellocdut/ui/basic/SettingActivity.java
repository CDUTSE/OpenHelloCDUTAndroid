package com.emptypointer.hellocdut.ui.basic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;

import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPUpdateService;
import com.emptypointer.hellocdut.ui.account.ChangePasswordActivity;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.rey.material.widget.Switch;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends EPActivity implements View.OnClickListener, Switch.OnCheckedChangeListener {

    private static final int REQUEST_FEED_BACK = 0x50FF;
    private static final String TAG = "SettingActivity";
    private com.rey.material.widget.Switch switchShowNotification;
    private android.widget.RelativeLayout layoutShowNotification;
    private com.rey.material.widget.Switch switchRing;
    private android.widget.RelativeLayout layoutRing;
    private com.rey.material.widget.Switch switchBeep;
    private android.widget.RelativeLayout layoutBeep;
    private com.rey.material.widget.Switch switchOutPlay;
    private android.widget.RelativeLayout layoutOutplay;
    private android.widget.RelativeLayout layoutChangePassword;
    private android.widget.RelativeLayout layoutIgnoreManage;
    private android.widget.RelativeLayout layoutFeedBack;
    private android.widget.RelativeLayout layoutAbout;

//    private DemoModel settingsModel;

//    private EMChatOptions chatOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        this.layoutAbout = (RelativeLayout) findViewById(R.id.layoutAbout);
        this.layoutFeedBack = (RelativeLayout) findViewById(R.id.layoutFeedBack);
        this.layoutIgnoreManage = (RelativeLayout) findViewById(R.id.layoutIgnoreManage);
        this.layoutChangePassword = (RelativeLayout) findViewById(R.id.layoutChangePassword);
        this.layoutOutplay = (RelativeLayout) findViewById(R.id.layoutOutplay);
        this.switchOutPlay = (Switch) findViewById(R.id.switchOutPlay);
        this.layoutBeep = (RelativeLayout) findViewById(R.id.layoutBeep);
        this.switchBeep = (Switch) findViewById(R.id.switchBeep);
        this.layoutRing = (RelativeLayout) findViewById(R.id.layoutRing);
        this.switchRing = (Switch) findViewById(R.id.switchRing);
        this.layoutShowNotification = (RelativeLayout) findViewById(R.id.layoutShowNotification);
        this.switchShowNotification = (Switch) findViewById(R.id.switchShowNotification);


        this.layoutAbout.setOnClickListener(this);
        this.layoutFeedBack.setOnClickListener(this);
        this.layoutIgnoreManage.setOnClickListener(this);
        this.layoutChangePassword.setOnClickListener(this);
        this.layoutOutplay.setOnClickListener(this);
        this.layoutBeep.setOnClickListener(this);
        this.layoutRing.setOnClickListener(this);
        this.layoutShowNotification.setOnClickListener(this);

        switchShowNotification.setOnCheckedChangeListener(this);
        switchBeep.setOnCheckedChangeListener(this);
        switchOutPlay.setOnCheckedChangeListener(this);
        switchRing.setOnCheckedChangeListener(this);

//        settingsModel = EMHelper.getInstance().getModel();
//        chatOptions = EMChatManager.getInstance().getChatOptions();
//        // 震动和声音总开关，来消息时，是否允许此开关打开
//        // the vibrate and sound notification are allowed or not?
//        switchShowNotification.setChecked(settingsModel.getSettingMsgNotification());
//        switchBeep.setChecked(settingsModel.getSettingMsgVibrate());
//        switchOutPlay.setChecked(settingsModel.getSettingMsgSpeaker());
//        switchRing.setChecked(settingsModel.getSettingMsgSound());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutAbout:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.layoutFeedBack:
                Intent intent = new Intent(this, ModifyTextActivity.class);
                intent.putExtra(ModifyTextActivity.INTENT_TITLE, getString(R.string.str_feedback));
                intent.putExtra(ModifyTextActivity.INTENT_HINT, getString(R.string.message_feed_back));
                intent.putExtra(ModifyTextActivity.INTENT_MAX_LENGTH, 150);
                intent.putExtra(ModifyTextActivity.INTENT_MAX_LINE, 5);
                startActivityForResult(intent, REQUEST_FEED_BACK);
                break;
            case R.id.layoutChangePassword:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.layoutOutplay:
                switchOutPlay.setChecked(!switchOutPlay.isChecked());
                break;
            case R.id.layoutBeep:
                switchBeep.setChecked(!switchBeep.isChecked());
                break;
            case R.id.layoutRing:
                switchRing.setChecked(!switchRing.isChecked());
                break;
            case R.id.layoutShowNotification:
                switchShowNotification.setChecked(!switchRing.isChecked());
                break;

            default:
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }

        String result = data.getExtras().getString(ModifyTextActivity.INTENT_RESULT);
        if (requestCode == REQUEST_FEED_BACK) {
            Log.i(TAG, "result" + result);
            commitFeedBack(result);
        }
    }

    private void commitFeedBack(String advice) {
        Map<String, String> params = new HashMap<>();
        params.put("action", "feedbackAdvice");
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(this).getUserName()));
        params.put("user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(this).getToken()));
        params.put("advice", advice);
        params.put("version ", EPUpdateService.getAppVersion(this));
        params.put("client", "0");
        EPJsonObjectRequest request = new EPJsonObjectRequest(this, params, false, new EPJsonResponseListener(this));
        request.setTag(this);
        VolleyUtil.getQueue(this).add(request);
    }

    @Override
    protected void onDestroy() {
        VolleyUtil.getQueue(this).cancelAll(this);
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(Switch view, boolean checked) {
        switch (view.getId()) {
            case R.id.switchBeep:
//                settingsModel.setSettingMsgVibrate(checked);
                break;
            case R.id.switchOutPlay:
//                settingsModel.setSettingMsgSpeaker(checked);
                break;
            case R.id.switchRing:
//                settingsModel.setSettingMsgSound(checked);
                break;
            case R.id.switchShowNotification:
//                settingsModel.setSettingMsgNotification(checked);
                break;
            default:
                break;
        }
    }
}
