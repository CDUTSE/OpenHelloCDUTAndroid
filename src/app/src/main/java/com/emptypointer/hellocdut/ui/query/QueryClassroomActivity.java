package com.emptypointer.hellocdut.ui.query;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.DefaultRetryPolicy;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.customer.EPRecyclerAdapter;
import com.emptypointer.hellocdut.customer.EPRecyclerViewHolder;
import com.emptypointer.hellocdut.model.ClassRoom;
import com.emptypointer.hellocdut.model.DataCache;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.CacheService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.ui.main.CourseScheduleAdapter;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.widget.Button;
import com.rey.material.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryClassroomActivity extends EPActivity {
    private static final String CACHE_EMPTY_CLASS_ROOM = "empty_class_room";
    private static final String TAG = "QueryClassroomActivity";


    public static final String PRE_FINAL_TIME = "final_search_class_room_time";
    public static final String PRE_FINAL_BUILDING = "final_search_class_room_room";
    private com.rey.material.widget.Spinner mSpinner;
    private android.widget.TextView textViewdate;
    private com.rey.material.widget.Button btnCommit;
    private android.support.v7.widget.RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private String[] BUILDING_NAME_TABLE;

    private CoordinatorLayout mLayout;

    private List<ClassRoom> mDataSet;

    private final String[] BUILDING_TABLE = {"1", "2", "3", "4", "5", "6a",
            "6b", "6c", "7", "8", "9", "e1", "e2", "art"};

    private String[] background = {CourseScheduleAdapter.COLOR_TABLE[6],
            CourseScheduleAdapter.COLOR_TABLE[1], CourseScheduleAdapter.COLOR_TABLE[2],
            CourseScheduleAdapter.COLOR_TABLE[5]};

    private String[] statuMaps = {"空闲", "教学", "借用", "考试"};


    private android.widget.ImageView imCalender;
    private android.widget.LinearLayout layoutCalender;

    private int mCurrentBuilding;

    private EPRecyclerAdapter<ClassRoom> mAdapetr;

    private CacheService mCacheService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_classroom);
        this.layoutCalender = (LinearLayout) findViewById(R.id.layoutCalender);
        this.imCalender = (ImageView) findViewById(R.id.imCalender);
        this.mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        this.btnCommit = (Button) findViewById(R.id.btnCommit);
        this.textViewdate = (TextView) findViewById(R.id.textView_date);
        this.mSpinner = (Spinner) findViewById(R.id.mSpinner);
        mLayout=(CoordinatorLayout)findViewById(R.id.mLayoutRoot);
        mFab=(FloatingActionButton)findViewById(R.id.fab);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        BUILDING_NAME_TABLE = getResources().getStringArray(
                R.array.building_name_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.row_spin_light, BUILDING_NAME_TABLE);
        adapter.setDropDownViewResource(R.layout.row_spn_dropdown_light);
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        SharedPreferences preferences = getSharedPreferences(Constant.SHARED_PERFERENCR_CATCH, MODE_PRIVATE);
        mCurrentBuilding = preferences.getInt(PRE_FINAL_BUILDING, 5);
        String lastQueryDate = preferences.getString(PRE_FINAL_TIME, "");
        if (TextUtils.isEmpty(lastQueryDate)) {
            textViewdate.setText(formatter.format(System.currentTimeMillis()));
        } else {
            textViewdate.setText(lastQueryDate);
        }
        imCalender.setColorFilter(getResources().getColor(R.color.colorPrimary));
        mDataSet = new ArrayList<>();

        mSpinner.setAdapter(adapter);

        mSpinner.setSelection(mCurrentBuilding);


        layoutCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.Builder builder = new DatePickerDialog.Builder() {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();

                        String date = dialog.getFormattedDate(formatter);
                        textViewdate.setText(date);
                        super.onPositiveActionClicked(fragment);
                    }
                };

                builder.positiveAction(getString(R.string.str_comfirm))
                        .negativeAction(getString(R.string.str_cancel));
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), null);
            }
        });

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataFromServer();
            }
        });

        mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                mCurrentBuilding = position;
            }
        });


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapetr = new EPRecyclerAdapter<ClassRoom>(R.layout.row_classrom, mDataSet) {
            @Override
            protected void onBindData(EPRecyclerViewHolder viewHolder, int position, ClassRoom room) {
                viewHolder.setText(R.id.textView_class_name, room.getName());
                viewHolder.setText(R.id.textView_seat_num, room.getSeatNum());
                viewHolder.setText(R.id.textView_class1_2, statuMaps[room.getStatus()[0]]).setBackgroundColor(R.id.textView_class1_2, Color.parseColor(background[room.getStatus()[0]]));
                viewHolder.setText(R.id.textView_class3_4, statuMaps[room.getStatus()[1]]).setBackgroundColor(R.id.textView_class3_4, Color.parseColor(background[room.getStatus()[0]]));
                viewHolder.setText(R.id.textView_class5_6, statuMaps[room.getStatus()[2]]).setBackgroundColor(R.id.textView_class5_6, Color.parseColor(background[room.getStatus()[0]]));
                viewHolder.setText(R.id.textView_class7_8, statuMaps[room.getStatus()[3]]).setBackgroundColor(R.id.textView_class7_8, Color.parseColor(background[room.getStatus()[0]]));
                viewHolder.setText(R.id.textView_class9_11, statuMaps[room.getStatus()[4]]).setBackgroundColor(R.id.textView_class9_11, Color.parseColor(background[room.getStatus()[0]]));
                if (position % 2 == 0) {
                    viewHolder.setBackgroundColor(R.id.textView_class_name, getResources().getColor(R.color.color_ep_white));
                    viewHolder.setBackgroundColor(R.id.textView_seat_num, getResources().getColor(R.color.color_ep_gray));
                } else {
                    viewHolder.setBackgroundColor(R.id.textView_class_name, getResources().getColor(R.color.color_ep_gray));
                    viewHolder.setBackgroundColor(R.id.textView_seat_num, getResources().getColor(R.color.color_ep_white));
                }
            }
        };



        mRecyclerView.setAdapter(mAdapetr);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.scrollToPosition(0);
            }
        });


        mCacheService = CacheService.getInstance(this);
        DataCache cache = mCacheService.getDataCache(CACHE_EMPTY_CLASS_ROOM);
        if (cache != null) {
            jsonParse(cache.getDataJson());
        }
    }

    private void loadDataFromServer() {
        Map<String, String> params = new HashMap<>();
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(this).getUserName()));
        params.put("user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(this).getToken()));
        params.put("action", "queryEmptyRoom");
        params.put("building_num", BUILDING_TABLE[mCurrentBuilding]);
        params.put("query_date", textViewdate.getText().toString());

        EPJsonObjectRequest request = new EPJsonObjectRequest(this, params, new EPJsonResponseListener(this) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (!result) {
                    return;
                }
                mCacheService.setDataCache(CACHE_EMPTY_CLASS_ROOM,response.toJSONString());
                getSharedPreferences(Constant.SHARED_PERFERENCR_CATCH, MODE_PRIVATE).edit().putInt(PRE_FINAL_BUILDING, mCurrentBuilding).putString(PRE_FINAL_TIME, textViewdate.getText().toString()).commit();
                jsonParse(response);
                UIUtils.makeSnake(mFab,getString(R.string.message_get_json_sucess));
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.LONG_CONNECT_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag(this);
        VolleyUtil.getQueue(this).add(request);
    }

    @Override
    protected void onDestroy() {
        VolleyUtil.getQueue(this).cancelAll(this);
        super.onDestroy();
    }

    /**
     * json解析
     */
    private void jsonParse(JSONObject object) {
        JSONArray array = object.getJSONArray("rooms");
        try {
            mDataSet.clear();
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String roomName = obj.getString("roomName");
                String seatNum = obj.getString("seatNum");
                JSONArray statusArray = obj.getJSONArray("status");
                int[] a = new int[5];
                if (statusArray != null) {
                    for (int j = 0; j < statusArray.size(); j++) {
                        a[j] = statusArray.getIntValue(j);
                    }
                }
                mDataSet.add(new ClassRoom(roomName, seatNum, a));
            }
            mAdapetr.notifyDataSetChanged();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
