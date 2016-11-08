package com.emptypointer.hellocdut.ui.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
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
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.StringChecker;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.rey.material.widget.Button;



import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResetPwdByMailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResetPwdByMailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPwdByMailFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ResetPwdByMailFragment";

    private long mLastGetTokenTime;
    private static String PRE_KEY_LAST_GET_TIME = "reset_token_time";
    private static String PRE_KEY_BIND_EMAIL = "reseet_email";

    // TODO: Rename and change types of parameters
    private Handler handler;

    private boolean isActivityDestroy = false;


    private OnFragmentInteractionListener mListener;
    private android.widget.EditText etMail;
    private android.widget.EditText etCaptcha;
    private android.widget.ImageButton imVisible;
    private android.widget.EditText etNewPwd;
    private com.rey.material.widget.Button btnSendCaptcha;
    private com.rey.material.widget.Button btnCommit;
    private android.widget.ScrollView loginform;
    private android.widget.LinearLayout layoutRoot;
    private boolean mPwdVisiable = false;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ResetPwdByMailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetPwdByMailFragment newInstance() {
        ResetPwdByMailFragment fragment = new ResetPwdByMailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ResetPwdByMailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDestroy() {
        isActivityDestroy = true;
        handler.removeCallbacks(runnable);

        VolleyUtil.getQueue(getActivity()).cancelAll(this);
        super.onDestroy();

        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_pwd_by_mail, container, false);
        this.btnCommit = (Button) view.findViewById(R.id.btnCommit);
        this.btnSendCaptcha = (Button) view.findViewById(R.id.btnSendCaptcha);
        this.etNewPwd = (EditText) view.findViewById(R.id.etNewPwd);
        this.imVisible = (ImageButton) view.findViewById(R.id.imVisible);
        this.etCaptcha = (EditText) view.findViewById(R.id.etCaptcha);
        this.etMail = (EditText) view.findViewById(R.id.etMail);
        imVisible.setColorFilter(getResources().getColor(R.color.colorPrimary));
        changePwdVisibility();
        btnSendCaptcha.setOnClickListener(this);
        imVisible.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
        handler = new getBtnHandeler();
        handler.post(runnable);

        mLastGetTokenTime = getActivity().getSharedPreferences(Constant.SHARED_PERFERENCR_CATCH, Context.MODE_PRIVATE).getLong(PRE_KEY_LAST_GET_TIME, Integer.MAX_VALUE);
        String lastEmail = getActivity().getSharedPreferences(Constant.SHARED_PERFERENCR_CATCH, Context.MODE_PRIVATE).getString(PRE_KEY_BIND_EMAIL, "");
        etMail.setText(lastEmail);
        return view;
    }

    private void changePwdVisibility() {
        if (mPwdVisiable) {
            imVisible.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye_off));
            etNewPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            imVisible.setImageDrawable(getResources().getDrawable(R.drawable.ic_eye));
            etNewPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        mPwdVisiable = !mPwdVisiable;
    }

    private void getTokenTask() {
        Map<String, String> params = new HashMap<>();
        params.put("action", "getResetUserPasswordTokenByEmail");
        String mail = etMail.getText().toString();
        if (!StringChecker.isMail(mail)) {
            etMail.setError(getString(R.string.message_wrong_email_format));
            return;
        }
        SharedPreferences preferences = getActivity().getSharedPreferences(Constant.SHARED_PERFERENCR_CATCH, Context.MODE_PRIVATE);
        if (!preferences.edit().putString(PRE_KEY_BIND_EMAIL, mail).commit()) {
            return;
        }
        params.put("user_email", EPSecretService.encryptByPublic(mail));
        EPJsonObjectRequest request = new EPJsonObjectRequest(getActivity(), params, new EPJsonResponseListener(getActivity()) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (result) {
                    SharedPreferences preferences = getActivity().getSharedPreferences(Constant.SHARED_PERFERENCR_CATCH, Context.MODE_PRIVATE);
                    long timeMillis = System.currentTimeMillis() + 1000 * 60;
                    if (preferences.edit().putLong(PRE_KEY_LAST_GET_TIME, timeMillis).commit()) {
                        mLastGetTokenTime = timeMillis;
                    }
                    handler.post(runnable);
                }
            }
        });
        request.setTag(this);
        VolleyUtil.getQueue(getActivity()).add(request);
    }

    private void commitTask() {
        etMail.setError(null);
        etCaptcha.setError(null);
        etNewPwd.setError(null);
        HashMap<String, String> params = new HashMap<>();
        String mail = etMail.getText().toString();
        String token = etCaptcha.getText().toString();
        String newPwd = etNewPwd.getText().toString();
        if (!StringChecker.isMail(mail)) {
            etMail.setError(getString(R.string.message_wrong_email_format));
            return;
        }
        if (token.length() != 4) {
            etCaptcha.setError(getString(R.string.message_wrong_captcha_format));
            return;
        }
        if (!StringChecker.isLegalPassword(newPwd)) {
            etNewPwd.setError(getString(R.string.error_invalid_password));
            return;
        }
        params.put("action", "resetUserPasswordByEmail");
        params.put("user_email", EPSecretService.encryptByPublic(mail));
        params.put("validate_code", EPSecretService.encryptByPublic(token));
        params.put("new_password", EPSecretService.encryptByPublic(newPwd));
        EPJsonObjectRequest request = new EPJsonObjectRequest(getActivity(), params, new EPJsonResponseListener(getActivity()) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (result) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        }
                    }, 1000);

                }
            }
        });
        request.setTag(this);
        VolleyUtil.getQueue(getActivity()).add(request);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCommit:
                commitTask();
                break;
            case R.id.imVisible:
                changePwdVisibility();
                break;
            case R.id.btnSendCaptcha:
                getTokenTask();
                break;
            default:
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class getBtnHandeler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isActivityDestroy) {
                if (msg.what <= 0) {
                    btnSendCaptcha.setClickable(true);
                    btnSendCaptcha.setEnabled(true);
                    btnSendCaptcha.setText(R.string.str_send_captcha);
                } else {
                    btnSendCaptcha.setClickable(false);
                    btnSendCaptcha.setEnabled(false);
                    btnSendCaptcha.setText(getString(R.string.str_format_last_time, msg.what));
                }
            }
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long timeDiffer = (mLastGetTokenTime - System.currentTimeMillis()) / 1000;
            Message message = new Message();
            message.what = (int) timeDiffer;
            handler.sendMessage(message);
            if (timeDiffer < 60 && timeDiffer > 0 && !isActivityDestroy) {
                handler.postDelayed(this, 1000);
            }
        }
    };


}
