package com.emptypointer.hellocdut.ui.query;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.customer.EPCommonAdapter;
import com.emptypointer.hellocdut.customer.EPCommonViewHolder;
import com.emptypointer.hellocdut.model.CallLogItem;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.DialogButtonClickListner;
import com.emptypointer.hellocdut.utils.StringChecker;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rey.material.widget.Button;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class QueryTelActivity extends EPActivity {

    private android.widget.EditText etPhoneNum;
    private android.widget.ImageButton imageViewopencalllog;
    private com.rey.material.widget.Button btnCommit;
    static final int PICK_CONTACT_REQUEST = 1;
    private static final String DESTINATION_ADDRESS = "10086086";
    private List<CallLogItem> mItems;
    private int CALL_LOG_MAX_COUNT = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_tel);
        this.btnCommit = (Button) findViewById(R.id.btnCommit);
        this.imageViewopencalllog = (ImageButton) findViewById(R.id.imageView_open_call_log);
        this.etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
        imageViewopencalllog.setColorFilter(getResources().getColor(R.color.colorPrimary));
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.checkPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response1) {
                        attemptQuery();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        UIUtils.makeSnake(QueryTelActivity.this, getString(R.string.str_on_denied));
                        return;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }, Manifest.permission.SEND_SMS);
            }


        });
        imageViewopencalllog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItems == null) {

                    Dexter.checkPermission(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response1) {
                            pickContact();
                            showDialog();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            UIUtils.makeSnake(QueryTelActivity.this, getString(R.string.str_on_denied));
                            return;
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }, Manifest.permission.READ_CALL_LOG);
                }
            }
        });

    }

    private void pickContact() {
        mItems = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                UIUtils.makeSnake(this, getString(R.string.message_wrong_missing_permission_to_call_logs));
                return;
            }
        }

        final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
                        CallLog.Calls.TYPE, CallLog.Calls.DATE,
                        CallLog.Calls.DURATION}, null, null,
                CallLog.Calls.DEFAULT_SORT_ORDER);
        for (int i = 0; i < cursor.getCount()
                && mItems.size() < CALL_LOG_MAX_COUNT; i++) {
            cursor.moveToPosition(i);
            String number = cursor.getString(0);// 电话号码
            String name = cursor.getString(1);// 名字
            int type = cursor.getInt(2);// 类型
            long calltime = Long.parseLong(cursor.getString(3));// 打电话的时间
            long duration = cursor.getLong(4);
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = new Date(calltime);// 打电话的日期
            String sCallTime = sfd.format(date);
            CallLogItem item = new CallLogItem(number, name, sCallTime,
                    duration, type);
            if (!mItems.contains(item)) {
                mItems.add(item);
            }
        }
        cursor.close();
    }


    private void attemptQuery() {
        String phoneNum = etPhoneNum.getText().toString();
        if (StringChecker.isPhoneNum(phoneNum) || StringChecker.isShortNum(phoneNum)) {
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(DESTINATION_ADDRESS, null, "CX"
                    + phoneNum, null, null);

            UIUtils.makeSnake(this, getString(R.string.message_send_succes));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    QueryTelActivity.this.finish();
                }
            }, Constant.DELAY_ACTIVITY_FINISH);

        } else {
            etPhoneNum.setError(null);
            etPhoneNum.setError(getString(R.string.message_wrong_erro_num_format));
        }
    }

    private void showDialog() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(getString(R.string.str_call_log));
        EPCommonAdapter<CallLogItem> adapter = new EPCommonAdapter<CallLogItem>(this, R.layout.row_call_log, mItems) {
            @Override
            public void convert(EPCommonViewHolder holder, CallLogItem item) {
                String name = item.getName();
                if (!TextUtils.isEmpty(name)) {
                    holder.setText(R.id.textView_name, item.getName());
                }
                holder.setText(R.id.textView_name, item.getNumber());
                holder.setText(R.id.textView_calltime, item.getCallTime());
                if (item.getStatus() == 3) {
                    holder.setTextVisbility(R.id.textView_type, View.GONE);
                }
            }
        };
        builderSingle.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(adapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etPhoneNum.setText(mItems.get(which).getNumber());
                    }
                });
        builderSingle.show();
    }
}
