package com.emptypointer.hellocdut.ui.account;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.StringChecker;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.rey.material.widget.Button;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BasicBindFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BasicBindFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ACTION_BIND_AAO = "bindAAO";
    public static final String ACTION_BIND_LIB = "bindLib";

    private static final String ARG_PARAM_BINDACTION = "action";

    // TODO: Rename and change types of parameters
    private String mBindAction;
    private android.widget.EditText etUsername;
    private android.widget.EditText etPassword;
    private android.widget.ImageButton imVisible;
    private com.rey.material.widget.Button btnCommit;
    private android.widget.LinearLayout layoutRoot;
    private boolean mPwdVisiable = false;


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
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param bindaction Parameter 1.
     * @return A new instance of fragment BasicBindFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BasicBindFragment newInstance(String bindaction) {
        BasicBindFragment fragment = new BasicBindFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_BINDACTION, bindaction);
        fragment.setArguments(args);
        return fragment;
    }

    public BasicBindFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBindAction = getArguments().getString(ARG_PARAM_BINDACTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_basic_bind, container, false);
        this.btnCommit = (Button) view.findViewById(R.id.btnCommit);
        this.imVisible = (ImageButton) view.findViewById(R.id.imVisible);
        this.etPassword = (EditText) view.findViewById(R.id.etPassword);
        this.etUsername = (EditText) view.findViewById(R.id.etUsername);
        imVisible.setColorFilter(getResources().getColor(R.color.colorPrimary));
//        changePwdVisibility();
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptToBind();
            }
        });
        imVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePwdVisibility();
            }
        });
        return view;
    }

    private void attemptToBind() {
        etUsername.setError(null);
        etPassword.setError(null);
        String userName = etUsername.getText().toString().trim();
        if (!StringChecker.isLegalStudentID(userName)) {
            etUsername.setError(getString(R.string.message_wrong_stu_id_format));
            return;
        }
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            return;
        }

        Map<String,String> params=new HashMap<>();
        params.put("user_login_token", EPSecretService.encryptByPublic(UserInfo.getInstance(getActivity()).getToken()));
        params.put("user_name", EPSecretService.encryptByPublic(UserInfo.getInstance(getActivity()).getUserName()));
        params.put("account", EPSecretService.encryptByPublic(userName));
        params.put("password", EPSecretService.encryptByPublic(password));
        params.put("action", mBindAction);
        EPJsonObjectRequest request=new EPJsonObjectRequest(getActivity(),params,new EPJsonResponseListener(getActivity()){
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if(!result){
                    return;
                }
                if (mBindAction.equals(ACTION_BIND_AAO)) {
                    UserInfo info = UserInfo.getInstance(getActivity());
                    info.setUserStatus(
                            UserInfo.USER_STATUS_CERTIFICATE);
                    info.setRealName(response.getString("user_real_name"));
                    info.setGender(response.getIntValue("user_gender"));
                    info.setBirthDate(response.getString("user_birthdate"));
                    info.setStudentID(response.getString("user_stu_id"));
                    info.setInstituteName(response.getString("user_institute"));
                    info.setMajorName(response.getString("user_major"));
                    info.setClassID(response.getString("user_class_id"));
                    info.setEntryYear(response.getString("user_entrance_year"));

                } else if (mBindAction.equals(ACTION_BIND_LIB)) {
                    UserInfo.getInstance(getActivity()).setUserLibStatus(
                            UserInfo.USER_STATUS_CERTIFICATE);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().finish();
                    }
                }, Constant.DELAY_ACTIVITY_FINISH);
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
