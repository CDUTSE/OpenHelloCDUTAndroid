package com.emptypointer.hellocdut.ui.account;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.customer.EPProgressDialog;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.ui.basic.SimpleBrowserActivity;
import com.emptypointer.hellocdut.ui.main.MainActivity;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.StringChecker;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {


    private static final String TAG ="LoginActivity" ;
    // UI references.
    private EditText etUsername;
    private CheckBox cbAgreement;
    private TextView tvAgreement;
    private EditText etPassword;
    private boolean mPwdVisiable = false;
    private boolean mInLogin = true;
    private ImageButton imVisible;
    private Button btnComplete;
    private Button btnToSignUp;
    private LinearLayout layoutRoot;
    private LinearLayout layoutAgreement;
    private static final int MENU_ID_RESET_PWD = 0xFF2355;
    private UserInfo mUserInfo;
    private boolean mActivityOnBack = true;
    private EPProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mUserInfo = UserInfo.getInstance(this);
        if (mUserInfo.getUserStatus() > UserInfo.USER_STATUS_UNLOGIN && !TextUtils.isEmpty(mUserInfo.getToken())) {
            startMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);
        // Set up the login form.
        etUsername = (EditText) findViewById(R.id.etUsername);

        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attempCommit();
                    return true;
                }
                return false;
            }
        });

        btnComplete = (Button) findViewById(R.id.btnCommit);
        btnComplete.setOnClickListener(this);

        imVisible = (ImageButton) findViewById(R.id.imVisible);
        imVisible.setColorFilter(getResources().getColor(R.color.colorPrimary));
        imVisible.setOnClickListener(this);

        btnToSignUp = (Button) findViewById(R.id.btnToSignUp);
        btnToSignUp.setOnClickListener(this);
        layoutRoot = (LinearLayout) findViewById(R.id.layoutRoot);
        layoutAgreement = (LinearLayout) findViewById(R.id.layoutAgreement);
        cbAgreement = (CheckBox) findViewById(R.id.cbAgreement);
        tvAgreement = (TextView) findViewById(R.id.tvAgrement);

        tvAgreement.setOnClickListener(this);
        UserInfo info = UserInfo.getInstance(this);
        String username = info.getUserName();
        if (!TextUtils.isEmpty(username)) {
            etUsername.setText(username);
        }
        String infoPassWord = info.getPassWord();
        if (!TextUtils.isEmpty(infoPassWord)) {
            etPassword.setText(infoPassWord);
        }
//        Dexter.checkPermission(new PermissionListener() {
//            @Override
//            public void onPermissionGranted(PermissionGrantedResponse response1) {
//                Log.i(TAG,"GRANED");
//            }
//
//            @Override
//            public void onPermissionDenied(PermissionDeniedResponse response) {
//                Log.i(TAG,"DENIED");
//
//                return;
//            }
//
//            @Override
//            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
////                Log.i(TAG,"BESHOWN");
//                token.continuePermissionRequest();
//
//
//            }
//        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == MENU_ID_RESET_PWD) {
            Intent intent = new Intent(this, ResetPwdActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ID_RESET_PWD, 000, R.string.str_forgotten_pwd);
        menu.getItem(0).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    private void changeRegLoginUI() {
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1000);
        layoutRoot.startAnimation(animation);
        ActionBar actionBar = getSupportActionBar();
        if (mInLogin) {
            etUsername.setText("");
            etPassword.setText("");
            btnComplete.setText(R.string.str_sign_up_immediately);
            btnToSignUp.setVisibility(View.GONE);
            actionBar.setTitle(R.string.str_sign_up);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (!mPwdVisiable) {
                changePwdVisibility();
            }
            layoutAgreement.setVisibility(View.VISIBLE);
        } else {
            actionBar.setTitle(R.string.action_sign_in);
            actionBar.setDisplayHomeAsUpEnabled(false);
            btnComplete.setText(R.string.action_sign_in);
            btnToSignUp.setVisibility(View.VISIBLE);
            if (mPwdVisiable) {
                changePwdVisibility();
            }
            layoutAgreement.setVisibility(View.GONE);
        }
        mInLogin = !mInLogin;
    }

    @Override
    public void onBackPressed() {
        if (mInLogin) {
            super.onBackPressed();
        } else {
            changeRegLoginUI();
        }
    }

    @Override
    protected void onDestroy() {
        VolleyUtil.getQueue(this).cancelAll(this);
        super.onDestroy();
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

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attempCommit() {
        // Reset errors.
        etUsername.setError(null);
        etPassword.setError(null);

        // Store values at the time of the login attempt.
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!StringChecker.isLegalPassword(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            etUsername.setError(getString(R.string.error_field_required));
            focusView = etUsername;
            cancel = true;
        } else if (!StringChecker.isLegalUserName(username) && !mInLogin) {
            etUsername.setError(getString(R.string.error_invalid_email));
            focusView = etUsername;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (mInLogin) {
                doSignIn(username, password);
            } else {
                doSignUp(username, password);
            }
        }
    }

    private void doSignIn(final String username, String password) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        this.finish();
        Map<String, String> params = new HashMap<>();
        params.put("user_name", EPSecretService.encryptByPublic(username));
        params.put("user_password", EPSecretService.encryptByPublic(password));
        params.put("action", "userLogin");


        EPJsonObjectRequest request = new EPJsonObjectRequest(this, params, new EPJsonResponseListener(this) {
            @Override
            public void onResponse(final JSONObject response) {
                super.onResponse(response);
                try {
                    if (!result) {
                        return;
                    }
                    String chatToken = EPSecretService.decryptByPublic(response.getString("user_chat_token"));
                    final String chatUsername = response.getString("user_name");
                    if (!mActivityOnBack) {
                        mProgressDialog = new EPProgressDialog();
                        mProgressDialog.show(getSupportFragmentManager(), null);
                    }
//                    EMChatManager.getInstance().login(chatUsername, chatToken, new EMCallBack() {
//                        @Override
//                        public void onSuccess() {
//                            EMHelper.getInstance().setCurrentUserName(chatUsername);
//                            // 注册群组和联系人监听
//                            EMHelper.getInstance().registerGroupAndContactListener();
//                            // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
//                            // ** manually load all local groups and
//                            EMGroupManager.getInstance().loadAllGroups();
//                            EMChatManager.getInstance().loadAllConversations();
//                            if (!initUserInfoByJson(response)) {
//                                UIUtils.makeSnake(LoginActivity.this, getString(R.string.erro_json_parse));
//                                dismissDialog();
//                                return;
//                            }
//                            if (!EMChatManager.getInstance().updateCurrentUserNick(
//                                    mUserInfo.getNickName())) {
//                                dismissDialog();
//                                UIUtils.makeSnake(LoginActivity.this, getString(R.string.erro_cannot_update_nickname));
//                                return;
//                            }
//                            //异步获取用户资料
//                            EMHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
//                            dismissDialog();
//                                startMainActivity();
//
//                        }
//
//                        private void dismissDialog() {
//                            if(mProgressDialog==null){
//                                return;
//                            }
//                            if (mProgressDialog.isVisible() && !LoginActivity.this.isFinishing() && !mActivityOnBack) {
//                                mProgressDialog.dismiss();
//                            }
//                        }
//
//                        @Override
//                        public void onError(int i, String s) {
//                            dismissDialog();
//                            UIUtils.makeSnake(LoginActivity.this, getString(R.string.str_format_erro, s + i), true);
//                        }
//
//                        @Override
//                        public void onProgress(int i, String s) {
//                        }
//                    });
                    initUserInfoByJson(response);
                    startMainActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        request.setTag(this);
        VolleyUtil.getQueue(this).add(request);
    }


    public void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }


    private void doSignUp(String username, String password) {
        if (!cbAgreement.isChecked()) {
            UIUtils.makeSnake(this, getString(R.string.message_need_acept_agreement), true);
            return;
        }
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        Map<String, String> params = new HashMap<>();
        params.put("user_name", EPSecretService.encryptByPublic(username));
        params.put("user_password", EPSecretService.encryptByPublic(password));
        params.put("user_device_code", EPSecretService.encryptByPublic(tm.getDeviceId()));
        params.put("action", "registerUser");
        EPJsonObjectRequest request = new EPJsonObjectRequest(this, params, new EPJsonResponseListener(this) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (result) {
                    changeRegLoginUI();
                } else {
                    if (mMessage.equals("用户名已经存在")) {
                        etUsername.setError(mMessage);
                        etUsername.requestFocus();
                    }
                }
            }
        });
        request.setTag(this);
        VolleyUtil.getQueue(this).add(request);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnToSignUp:
                changeRegLoginUI();
                break;
            case R.id.imVisible:
                changePwdVisibility();
                break;
            case R.id.btnCommit:
                attempCommit();
                break;
            case R.id.tvAgrement:
                Intent intent = new Intent(this, SimpleBrowserActivity.class);
                intent.putExtra(SimpleBrowserActivity.INTENT_TITTLE, getString(R.string.str_user_agreement));
                intent.putExtra(SimpleBrowserActivity.INTENT_URL, Constant.SERVICE_HOST_AGREEMENT);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private boolean initUserInfoByJson(JSONObject JsonObject) {
        UserInfo info = UserInfo.getInstance(LoginActivity.this);
        try {
            info.setChatToken(JsonObject
                    .getString("user_chat_token"));
            info.setUserName(JsonObject.getString("user_name"));
            info.setToken(JsonObject.getString("user_login_token"));
            info.setUserStatus(JsonObject
                    .getIntValue("user_aao_status"));
            info.setUserCampusStatus(JsonObject
                    .getIntValue("user_campus_status"));
            info.setUserLibStatus(JsonObject
                    .getIntValue("user_lib_status"));
            info.setMAILsStatus(JsonObject
                    .getIntValue("user_email_status"));
            info.setNickName(JsonObject.getString("user_nick_name"));
            info.setUserName(JsonObject.getString("user_name"));
            info.setPassWord(JsonObject
                    .getString("user_password_hash"));
            info.setStudentID(JsonObject.getString("user_stu_id"));
            info.setMail(JsonObject.getString("user_email"));
            info.setGender(JsonObject.getIntValue("user_gender"));
            info.setMetto(JsonObject.getString("user_motto"));
            info.setLoveStatus(JsonObject.getIntValue("user_love_status"));
            info.setSexOrientation(JsonObject
                    .getIntValue("user_sex_orientation"));
            info.setRealName(JsonObject.getString("user_real_name"));
            info.setBirthDate(JsonObject.getString("user_birthdate"));
            info.setInstituteName(JsonObject.getString("user_institute"));
            info.setMajorName(JsonObject.getString("user_major"));
            info.setClassID(JsonObject.getString("user_class_id"));
            info.setEntryYear(JsonObject.getString("user_entrance_year"));
            info.setImageURL(JsonObject.getString("user_avatar_url"));
            info.setPermissionLoveStatus(JsonObject
                    .getIntValue("user_love_status_permission"));
            info.setPermissionSexOrientation(JsonObject
                    .getIntValue("user_sex_orientation_permission"));
            info.setPermissionRealName(JsonObject
                    .getIntValue("user_real_name_permission"));
            info.setPermissionBirthDate(JsonObject
                    .getIntValue("user_birthdate_permission"));
            info.setPermissionStudentID(JsonObject
                    .getIntValue("user_stu_num_permission"));
            info.setPermissionInstitute(JsonObject
                    .getIntValue("user_institute_id_permission"));
            info.setPermissionMajor(JsonObject
                    .getIntValue("user_major_permission"));
            info.setPermissionClass(JsonObject
                    .getIntValue("user_class_id_permission"));
            info.setPermissionEntryYear(JsonObject
                    .getIntValue("user_entrance_year_permission"));
            info.setPermissionSchedule(JsonObject
                    .getIntValue("user_schedule_permission"));
            info.setPermissionGroupSchedule(JsonObject
                    .getIntValue("user_group_schedule_permission"));
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    protected void onPause() {
        mActivityOnBack = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivityOnBack = false;
    }
}

