package com.emptypointer.hellocdut.ui.query;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.customer.EPPagerAdapter;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.CacheService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.StringChecker;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.rey.material.app.Dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CampusCardActivity extends EPActivity {

    public static final String CACHE_COMPUSCARD_BASIC = "cache_compuscard_basic";
    private android.support.design.widget.TabLayout tbIndicator;
    private android.support.v4.view.ViewPager viewPager;
    private LinearLayout layoutRoot;
    private EPPagerAdapter mPagerAdapter;
    private EditText etCaptcha;
    private ImageButton imRefresh;
    private ImageView imCaptcha;
    private boolean mNeedCaptcha = true;
    private static final String PRE_CAPTCHA = "need_captcha";
    private Dialog mDialog;
    private CampusOverviewFragment mOverviewFragment;

    private boolean isDialogShowing = false;
    private CampusRecordFragment mRecordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_pager_indicator);
        this.layoutRoot = (LinearLayout) findViewById(R.id.main_content);
        this.viewPager = (ViewPager) findViewById(R.id.container);
        this.tbIndicator = (TabLayout) findViewById(R.id.tbIndicator);
        List<String> title = Arrays.asList(getResources().getStringArray(R.array.indicator_campus_card));
        List<Fragment> fragments = new ArrayList<>();
        mOverviewFragment = CampusOverviewFragment.newInstance();
        fragments.add(mOverviewFragment);
        mRecordFragment = CampusRecordFragment.newInstance();
        fragments.add(mRecordFragment);
        fragments.add(CampusQueryFragment.newInstance());
        mPagerAdapter = new EPPagerAdapter(getSupportFragmentManager(), title, fragments);
        viewPager.setAdapter(mPagerAdapter);
        tbIndicator.setupWithViewPager(viewPager);
        tbIndicator.setTabMode(TabLayout.MODE_FIXED);
        mNeedCaptcha = getSharedPreferences(Constant.SHARED_PERFERENCR_CATCH, MODE_PRIVATE).getBoolean(PRE_CAPTCHA, true);
        if (mNeedCaptcha) {
            showCaptchaDialog();
        }
        viewPager.setOffscreenPageLimit(5);
    }

    private void getCaptchaFromServer() {
        imRefresh.setEnabled(false);
        Map<String, String> params = new HashMap<>();
        params.put("user_login_token", EPSecretService.encryptByPublic(UserInfo.getInstance(this).getToken()));
        params.put("user_name", EPSecretService.encryptByPublic(UserInfo.getInstance(this).getUserName()));
        params.put("action", "getAuthCode");
        final EPJsonObjectRequest request = new EPJsonObjectRequest(this, params, false, new EPJsonResponseListener(this) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (!result) {
                    return;
                }
                String imCode = response.getString("captcha");
                Bitmap bitmap = CommonUtils.decodeBase64(imCode);
                imCaptcha.setImageBitmap(bitmap);

            }
        }) {
            @Override
            protected void onFinish() {
                super.onFinish();
                imRefresh.setEnabled(true);
            }
        };
        request.setTag(this);
        VolleyUtil.getQueue(this).add(request);
    }

    @Override
    protected void onDestroy() {
        VolleyUtil.getQueue(this).cancelAll(this);
        super.onDestroy();
    }

    public void showCaptchaDialog() {
        if (isDialogShowing) {
            return;
        }
        if (getSharedPreferences(Constant.SHARED_PERFERENCR_CATCH, MODE_PRIVATE).edit().putBoolean(PRE_CAPTCHA, true).commit()) {
            mNeedCaptcha = true;
        }
        mDialog = new Dialog(this);
        mDialog.setTitle(getString(R.string.hint_need_input_captcha));
        LinearLayout contentViewLayout = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.diloag_campuscard_captcha, null);
        imRefresh = (ImageButton) contentViewLayout.findViewById(R.id.button_refresh);
        etCaptcha = (EditText) contentViewLayout.findViewById(R.id.editText_captcha);
        imCaptcha = (ImageView) contentViewLayout.findViewById(R.id.imageView_captcha);
        getCaptchaFromServer();
        imRefresh.setColorFilter(getResources().getColor(R.color.colorPrimary));
        mDialog.setContentView(contentViewLayout);
        mDialog.negativeAction(getString(R.string.str_quit)).positiveAction(getString(R.string.str_comfirm));

        mDialog.negativeActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CampusCardActivity.this.finish();
            }
        });
        mDialog.positiveActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitCaptcha(etCaptcha.getText().toString());
            }
        });
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCaptchaFromServer();
            }
        });
        mDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mDialog.setCancelable(false);
        mDialog.show();
        isDialogShowing = true;
    }

    private void commitCaptcha(String captcha) {
        if (!StringChecker.isChaptcha(captcha)) {
            etCaptcha.setError(null);
            etCaptcha.setError(getString(R.string.message_wrong_captcha_format));
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("user_login_token", EPSecretService.encryptByPublic(UserInfo.getInstance(this).getToken()));
        params.put("user_name", EPSecretService.encryptByPublic(UserInfo.getInstance(this).getUserName()));
        params.put("action", "campusUserLogin");
        params.put("captcha", captcha);
        EPJsonObjectRequest request = new EPJsonObjectRequest(this, params, new EPJsonResponseListener(this) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (!result) {
                    getCaptchaFromServer();
                    return;
                } else {
                    if (getSharedPreferences(Constant.SHARED_PERFERENCR_CATCH, MODE_PRIVATE).edit().putBoolean(PRE_CAPTCHA, false).commit()) {
                        mNeedCaptcha = false;
                        mDialog.dismiss();
                        isDialogShowing = false;
                        CacheService.getInstance(CampusCardActivity.this).setDataCache(CACHE_COMPUSCARD_BASIC, response.toJSONString());
                        mOverviewFragment.onCaptchaUpdate();
                        mRecordFragment.onCaptchaUpdate();
                    }

                }
            }
        });
        request.setTag(this);
        VolleyUtil.getQueue(this).add(request);
    }
}
