package com.emptypointer.hellocdut.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

import java.util.HashMap;

/**
 * Created by Sequarius on 2015/10/31.
 */
public class RelativeAccountActivity extends EPActivity implements View.OnClickListener{
    public static final String ACTION_UNBIND_LIB = "unbindLib";
    public static final String ACTION_UNBIND_AAO = "unbindAAO";
    public static final String ACTION_UNBIND_CAMPUS_CARD = "unbindCampus";
    private Button mBtnUnbindAAO, mBtnUnBindLib, mBtnUnbindCampusCard;
    private TextView mTvStuID, mTvLibID, mTvCampusID,mtvMailAccount;
    private TextView mTvBindHintCampus, mTvBindHintStuID, mTvBindHintLib,
            mTvBindHintMail;
    private LinearLayout mLayoutAAO;
    private LinearLayout mLayoutCampus;
    private LinearLayout mLayoutMail;
    private LinearLayout mLayoutLib;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_ralative_account);
        mLayoutAAO = (LinearLayout) findViewById(R.id.layout_bind_aao);
        mLayoutCampus = (LinearLayout) findViewById(R.id.layout_bind_campus_card);
        mLayoutMail = (LinearLayout) findViewById(R.id.layout_bind_mail);
        mLayoutLib = (LinearLayout) findViewById(R.id.layout_bind_lib);

        mTvBindHintCampus = (TextView) findViewById(R.id.textView_bind_hint_campus_card);
        mTvBindHintStuID = (TextView) findViewById(R.id.textView_bind_hint_stu_id);
        mTvBindHintLib = (TextView) findViewById(R.id.textView_bind_hint_lib);
        mTvBindHintMail = (TextView) findViewById(R.id.textView_bind_hint_mail);

        mBtnUnbindAAO = (Button) findViewById(R.id.button_unbind_aao);
        mBtnUnBindLib = (Button) findViewById(R.id.button_unbind_lib);
        mBtnUnbindCampusCard = (Button) findViewById(R.id.button_unbind_campus_card);
        mLayoutAAO.setOnClickListener(this);
        mLayoutCampus.setOnClickListener(this);
        mLayoutMail.setOnClickListener(this);
        mLayoutLib.setOnClickListener(this);

        mTvStuID = (TextView) findViewById(R.id.textView_stu_id);
        mTvLibID = (TextView) findViewById(R.id.textView_lib_id);
        mTvCampusID = (TextView) findViewById(R.id.textView_campus_id);
        mtvMailAccount=(TextView)findViewById(R.id.textView_mail_account);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (UserInfo.getInstance(this).getUserStatus() == UserInfo.USER_STATUS_CERTIFICATE) {
            mBtnUnbindAAO.setVisibility(View.VISIBLE);
            mBtnUnbindAAO.setOnClickListener(this);
            mTvStuID.setVisibility(View.VISIBLE);
            mTvStuID.setText(UserInfo.getInstance(getApplicationContext())
                    .getStudentID());
            mTvBindHintStuID.setVisibility(View.VISIBLE);
            mLayoutAAO.setClickable(false);

        }
        if (UserInfo.getInstance(this).getUserLibStatus() == UserInfo.USER_STATUS_CERTIFICATE) {
            mBtnUnBindLib.setVisibility(View.VISIBLE);
            mBtnUnBindLib.setOnClickListener(this);
            mTvLibID.setVisibility(View.VISIBLE);
            mTvLibID.setText(UserInfo.getInstance(getApplicationContext())
                    .getStudentID());
            mTvBindHintLib.setVisibility(View.VISIBLE);
            mLayoutLib.setClickable(false);
        }
        if (UserInfo.getInstance(this).getUserCampusStatus() == UserInfo.USER_STATUS_CERTIFICATE) {
            mTvCampusID.setVisibility(View.VISIBLE);
            mTvCampusID.setText(UserInfo.getInstance(getApplicationContext())
                    .getStudentID());
            mBtnUnbindCampusCard.setVisibility(View.VISIBLE);
            mBtnUnbindCampusCard.setOnClickListener(this);
            mTvBindHintCampus.setVisibility(View.VISIBLE);
            mLayoutCampus.setClickable(false);

        }
        if (UserInfo.getInstance(this).getMailStatus() == UserInfo.USER_STATUS_CERTIFICATE) {
            mTvBindHintMail.setVisibility(View.VISIBLE);
            mtvMailAccount.setText(UserInfo.getInstance(getApplicationContext())
                    .getMail());
//            mBtnUnbindCampusCard.setVisibility(View.VISIBLE);
//            mBtnUnbindCampusCard.setOnClickListener(this);
            mTvBindHintMail.setVisibility(View.VISIBLE);
            mLayoutMail.setClickable(false);

        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
//        Intent intent = new Intent(GlobalVariables.ACTION_BIND);
        switch (v.getId()) {
            case R.id.button_unbind_aao:
                craateUnbindDialog(ACTION_UNBIND_AAO);
                break;
            case R.id.button_unbind_lib:
                craateUnbindDialog(ACTION_UNBIND_LIB);
                break;
            case R.id.button_unbind_campus_card:
                craateUnbindDialog(ACTION_UNBIND_CAMPUS_CARD);
                break;
            case R.id.layout_bind_aao:
                Intent intent=new Intent(this,BindActivity.class);
                intent.putExtra(BindActivity.INTETN_BINDE_MODE,
                        BindActivity.MODE_AAO);
                startActivity(intent);
                break;
            case R.id.layout_bind_campus_card:
                Intent  intentCapus=new Intent(this,BindActivity.class);
                intentCapus.putExtra(BindActivity.INTETN_BINDE_MODE,
                        BindActivity.MODE_CAMPUS_CARD);
                startActivity(intentCapus);
                break;
            case R.id.layout_bind_mail:
                Intent  intentMail=new Intent(this,BindActivity.class);
                intentMail.putExtra(BindActivity.INTETN_BINDE_MODE,
                        BindActivity.MODE_MAIL);
                startActivity(intentMail);
                break;
            case R.id.layout_bind_lib:
                Intent intentLib=new Intent(this,BindActivity.class);
                intentLib.putExtra(BindActivity.INTETN_BINDE_MODE,
                        BindActivity.MODE_LIB);
                startActivity(intentLib);
            default:
                break;
        }
    }

    private void craateUnbindDialog(final String action) {
        // TODO Auto-generated method stub
        SimpleDialog.Builder builder = new SimpleDialog.Builder(){
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                unBindAccount(action);
                super.onPositiveActionClicked(fragment);
            }
        };
        builder.title(getString(R.string.hint_cancel_bind_sck));
        if (action.equals(ACTION_UNBIND_AAO)) {
            builder.message(getString(R.string.message_unbind_aao));
        } else if (action.equals(ACTION_UNBIND_LIB)) {
            builder.message(getString(R.string.message_unbind_lib));
        } else if (action.equals(ACTION_UNBIND_CAMPUS_CARD)) {
            builder.message(getString(R.string.message_unbind_campus));
        }
        builder.positiveAction(getString(R.string.str_cancel_bind))
                .negativeAction(getString(R.string.str_cancel));
        DialogFragment fragment=DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(),null);


    }

    private void unBindAccount(final String action){
        HashMap<String,String> params=new HashMap<>();
        params.put("user_name", EPSecretService.encryptByPublic(UserInfo
                .getInstance(this).getUserName()));
        params.put("user_login_token", EPSecretService.encryptByPublic(UserInfo
                .getInstance(this).getToken()));
        params.put("action", action);

        EPJsonObjectRequest request=new EPJsonObjectRequest(this,params,new EPJsonResponseListener(this){
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (action.equals(ACTION_UNBIND_AAO)) {
                    UserInfo.getInstance(RelativeAccountActivity.this).setUserStatus(
                            UserInfo.USER_STATUS_NORMAL);
                    mBtnUnbindAAO.setVisibility(View.GONE);
                    mTvBindHintStuID.setVisibility(View.GONE);
                    mTvStuID.setVisibility(View.GONE);
                    mLayoutAAO.setClickable(true);

                    UserInfo info = UserInfo
                            .getInstance(getApplicationContext());
                    info.setRealName(UserInfo.INITED_STRING);
                    info.setBirthDate(UserInfo.INITED_STRING);
                    info.setStudentID(UserInfo.INITED_STRING);
                    info.setInstituteName(UserInfo.INITED_STRING);
                    info.setMajorName(UserInfo.INITED_STRING);
                    info.setClassID(UserInfo.INITED_STRING);
                    info.setEntryYear(UserInfo.INITED_STRING);
                } else if (action.equals(ACTION_UNBIND_LIB)) {
                    UserInfo.getInstance(RelativeAccountActivity.this).setUserLibStatus(
                            UserInfo.USER_STATUS_NORMAL);
                    mBtnUnBindLib.setVisibility(View.GONE);
                    mTvLibID.setVisibility(View.GONE);
                    mTvBindHintLib.setVisibility(View.GONE);
                    mLayoutLib.setClickable(true);
                } else if (action.equals(ACTION_UNBIND_CAMPUS_CARD)) {
                    UserInfo.getInstance(RelativeAccountActivity.this).setUserCampusStatus(
                            UserInfo.USER_STATUS_NORMAL);
                    mBtnUnbindCampusCard.setVisibility(View.GONE);
                    mTvBindHintCampus.setVisibility(View.GONE);
                    mTvCampusID.setVisibility(View.GONE);
                    mLayoutCampus.setClickable(true);

                }
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
}
