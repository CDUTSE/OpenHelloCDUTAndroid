package com.emptypointer.hellocdut.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.StringChecker;
import com.emptypointer.hellocdut.utils.VolleyUtil;



import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ResetPwdByAAOFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPwdByAAOFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ResetPwdByAAOFragment";

    // TODO: Rename and change types of parameters

    private String stuID;
    private EditText etUsername;
    private EditText etAAOPassword;
    private android.widget.ImageButton imVisible;
    private EditText etNewPwd;
    private android.widget.ImageButton imVisibleNewPwd;
    private com.rey.material.widget.Button btnCommit;


    private boolean mPwdVisiable=false;
    private boolean mNewPwdVisiable=false;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ResetPwdByAAOFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetPwdByAAOFragment newInstance() {
        ResetPwdByAAOFragment fragment = new ResetPwdByAAOFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ResetPwdByAAOFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_pwd_by_aao, container, false);
        this.btnCommit = (com.rey.material.widget.Button) view.findViewById(R.id.btnCommit);
        this.imVisibleNewPwd = (ImageButton) view.findViewById(R.id.imVisibleNewPwd);
        this.etNewPwd = (EditText) view.findViewById(R.id.etNewPwd);
        this.imVisible = (ImageButton) view.findViewById(R.id.imVisible);
        this.etAAOPassword = (EditText) view.findViewById(R.id.etPassword);
        this.etUsername = (EditText) view.findViewById(R.id.etUsername);
        imVisible.setOnClickListener(this);
        imVisible.setColorFilter(getResources().getColor(R.color.colorPrimary));
        imVisibleNewPwd.setOnClickListener(this);
        imVisibleNewPwd.setColorFilter(getResources().getColor(R.color.colorPrimary));
        btnCommit.setOnClickListener(this);
        changeNewPwdVisibility();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCommit:
                loadDataFromServer();
                break;
            case R.id.imVisible:
                changePwdVisibility();
                break;
            case R.id.imVisibleNewPwd:
                changeNewPwdVisibility();
                break;
            default:
                break;
        }
    }

    private void changePwdVisibility() {
        if (mPwdVisiable) {
            imVisible.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_off));
            etAAOPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            imVisible.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye));
            etAAOPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
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


    private void loadDataFromServer() {
        etNewPwd.setError(null);
        etUsername.setError(null);
        Map<String, String> params = new HashMap<>();
        params.put("action", "resetUserPasswordByAAO");
        params.put("aao_password", EPSecretService.encryptByPublic(etAAOPassword.getText().toString()));
        stuID = etUsername.getText().toString();
        if(!StringChecker.isLegalStudentID(stuID)){
            etUsername.setError(getString(R.string.message_wrong_stu_id_format));
            return;
        }
        String account = EPSecretService.encryptByPublic(stuID);
        Log.d(TAG, account);
        params.put("aao_account", account);
        String newPwd = etNewPwd.getText().toString();
        if(!StringChecker.isLegalPassword(newPwd)){
            etNewPwd.setError(getString(R.string.error_invalid_password));
            return;
        }
        params.put("new_password", EPSecretService.encryptByPublic(newPwd));
        EPJsonObjectRequest request=new EPJsonObjectRequest(getActivity(),params,new EPJsonResponseListener(getActivity()){
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if(result) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity()!=null) {
                                getActivity().finish();
                            }
                        }
                    },1000);
//
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
