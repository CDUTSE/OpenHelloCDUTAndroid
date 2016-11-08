package com.emptypointer.hellocdut.ui.account;

import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.StringChecker;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.rey.material.widget.Button;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends EPActivity implements View.OnClickListener{

    private android.widget.EditText etPassword;
    private android.widget.ImageButton imVisible;
    private android.widget.EditText etNewPwd;
    private android.widget.ImageButton imVisibleNewPwd;
    private com.rey.material.widget.Button btnCommit;
    private android.widget.LinearLayout layoutRoot;
    private boolean mPwdVisiable=false;
    private boolean mNewPwdVisiable=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chang_password);
        this.layoutRoot = (LinearLayout) findViewById(R.id.layoutRoot);
        this.btnCommit = (Button) findViewById(R.id.btnCommit);
        this.imVisibleNewPwd = (ImageButton) findViewById(R.id.imVisibleNewPwd);
        this.etNewPwd = (EditText) findViewById(R.id.etNewPwd);
        this.imVisible = (ImageButton) findViewById(R.id.imVisible);
        this.etPassword = (EditText) findViewById(R.id.etPassword);

        imVisibleNewPwd.setColorFilter(getResources().getColor(R.color.colorPrimary));
        imVisible.setColorFilter(getResources().getColor(R.color.colorPrimary));
        imVisibleNewPwd.setOnClickListener(this);
        imVisible.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
        changeNewPwdVisibility();

    }

    private void changePassWord(){
        etNewPwd.setError(null);
        etPassword.setError(null);
        String password=etPassword.getText().toString().trim();
        if(!password.equals(UserInfo.getInstance(this).getPassWord())){
            etPassword.setError("输入内容非当前密码");
            return;
        }
        String newPassword=etNewPwd.getText().toString().trim();
        if(!StringChecker.isLegalPassword(newPassword)){
            etNewPwd.setError(getString(R.string.error_invalid_password));
            return;
        }
        Map<String,String> params=new HashMap<>();
        params.put("action", "modifyUserPassword");
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(this).getUserName()));
        params.put("user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(this)
                        .getToken()));
        params.put("user_new_password", EPSecretService.encryptByPublic(newPassword));
        EPJsonObjectRequest request=new EPJsonObjectRequest(this,params,new EPJsonResponseListener(this){
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if(!result){
                    return;
                }
                UserInfo.getInstance(ChangePasswordActivity.this).setPassWord(response.getString("user_password_hash"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ChangePasswordActivity.this.finish();
                    }
                }, Constant.DELAY_ACTIVITY_FINISH);
            }
        });
        request.setTag(this);
        VolleyUtil.getQueue(this).add(request);

    }

    @Override
    protected void onDestroy() {
        VolleyUtil.getQueue(this).cancelAll(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imVisible:
                changePwdVisibility();
                break;
            case R.id.imVisibleNewPwd:
                changeNewPwdVisibility();
                break;
            case R.id.btnCommit:
                changePassWord();
                break;
            default:
                break;
        }
    }

    private void changePwdVisibility() {
        if (mPwdVisiable) {
            imVisible.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_off));
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            imVisible.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye));
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        mPwdVisiable = !mPwdVisiable;
    }

    private void changeNewPwdVisibility() {
        if (mNewPwdVisiable) {
            imVisibleNewPwd.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_off));
            etNewPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            imVisibleNewPwd.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye));
            etNewPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        mNewPwdVisiable = !mNewPwdVisiable;
    }
}
