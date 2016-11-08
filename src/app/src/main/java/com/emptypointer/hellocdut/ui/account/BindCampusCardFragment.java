package com.emptypointer.hellocdut.ui.account;

/**
 * Created by Sequarius on 2015/10/31.
 */

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.StringChecker;
import com.emptypointer.hellocdut.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;

public class BindCampusCardFragment extends Fragment {
    private static final String TAG = "BindCampusCardFragment";
    private EditText mEtUserName, mEtPassWord, mEtCaptcha;
    private Button mButtonCommit;
    private ImageView mImgeView,mButtonRefresh;
    private boolean isOnLoadCaptcha = false;
    private ImageButton imVisible;
    private boolean isPwdvisiable=false;


    public static BindCampusCardFragment newInstance(){
        return new BindCampusCardFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = View.inflate(getActivity(),
                R.layout.fragment_bind_campus_card, null);
        mEtUserName = (EditText) view.findViewById(R.id.etUsername);
        mEtPassWord = (EditText) view.findViewById(R.id.etPassword);
        mEtCaptcha = (EditText) view.findViewById(R.id.etCaptcha);
        mButtonCommit = (Button) view.findViewById(R.id.btnCommit);
        mButtonCommit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                attemptBind();
            }
        });
        mButtonRefresh = (ImageButton) view.findViewById(R.id.button_refresh);
        mButtonRefresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!isOnLoadCaptcha) {
                    getChaptcha();
                }

            }
        });

        mImgeView = (ImageView) view.findViewById(R.id.imageView_captcha);
//        new GetChaptchaTask().execute();
        imVisible = (ImageButton) view.findViewById(R.id.imVisible);
        imVisible.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                changePwdVisibility();
            }
        });
        imVisible.setColorFilter(getResources().getColor(R.color.colorPrimary));
        mButtonRefresh.setColorFilter(getResources().getColor(R.color.colorPrimary));
        getChaptcha();
//        changePwdVisibility();
        return view;
    }

    private void changePwdVisibility() {
        if (isPwdvisiable) {
            imVisible.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_off));
            mEtPassWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            imVisible.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye));
            mEtPassWord.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        isPwdvisiable = !isPwdvisiable;
    }


    private void getChaptcha() {
        isOnLoadCaptcha=true;
        mButtonRefresh.setClickable(false);
        mButtonRefresh.setColorFilter(getResources().getColor(R.color.color_ep_gray));
        Map<String, String> params = new HashMap<>();

        params.put("user_login_token", EPSecretService.encryptByPublic(UserInfo.getInstance(getActivity()).getToken()));
        params.put("user_name", EPSecretService.encryptByPublic(UserInfo.getInstance(getActivity()).getUserName()));
        params.put("action", "bindCampus");
        EPJsonObjectRequest request = new EPJsonObjectRequest(getActivity(), params, false,new EPJsonResponseListener(getActivity()) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (!result) {
                    return;
                }
                String captcha = response.getString("captcha");
                Bitmap bitmap = CommonUtils.decodeBase64(captcha);
                mImgeView.setImageBitmap(bitmap);
                isOnLoadCaptcha = false;
                mButtonRefresh.setClickable(true);
                mEtCaptcha.setText("");
            }

        }){
            @Override
            protected void onFinish() {
                super.onFinish();
                isOnLoadCaptcha=false;
                mButtonRefresh.setClickable(true);
                mButtonRefresh.setColorFilter(getResources().getColor(R.color.colorPrimary));
            }
        };
        request.setTag(this);
        VolleyUtil.getQueue(getActivity()).add(request);

    }

    private void attemptBind() {
        mEtUserName.setError(null);
        mEtPassWord.setError(null);
        mEtCaptcha.setError(null);
        String account = mEtUserName.getText().toString();
        String password = mEtPassWord.getText().toString();
        String captcha = mEtCaptcha.getText().toString();
        if (!StringChecker.isLegalStudentID(account)) {
            mEtUserName.setError(getString(R.string.message_wrong_stu_id_format));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mEtPassWord.setError(getString(R.string.error_field_required));
            return;
        }
        if (TextUtils.isEmpty(captcha)) {
            mEtCaptcha.setError(getString(R.string.error_field_required));
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("user_login_token", EPSecretService.encryptByPublic(UserInfo.getInstance(getActivity()).getToken()));
        params.put("user_name", EPSecretService.encryptByPublic(UserInfo.getInstance(getActivity()).getUserName()));
        params.put("action", "bindCampus");
        params.put("flag", "true");
        params.put("account", EPSecretService
                .encryptByPublic(account));
        params.put("password", EPSecretService
                .encryptByPublic(password));
        params.put("captcha", captcha);

        EPJsonObjectRequest request = new EPJsonObjectRequest(getActivity(), params, new EPJsonResponseListener(getActivity()) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (result) {
                    UserInfo.getInstance(getActivity()).setUserCampusStatus(
                            UserInfo.USER_STATUS_CERTIFICATE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().finish();
                        }
                    }, Constant.DELAY_ACTIVITY_FINISH);
                } else {
                    getChaptcha();
                }
            }
        });
        request.setTag(this);
        VolleyUtil.getQueue(getActivity()).add(request);
    }

    @Override
    public void onDestroy() {
        VolleyUtil.getQueue(getActivity()).cancelAll(this);
        super.onDestroy();
    }
}
