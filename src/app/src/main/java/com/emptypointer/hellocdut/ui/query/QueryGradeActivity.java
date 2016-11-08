package com.emptypointer.hellocdut.ui.query;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.customer.EPRecyclerAdapter;
import com.emptypointer.hellocdut.customer.EPRecyclerViewHolder;
import com.emptypointer.hellocdut.model.DataCache;
import com.emptypointer.hellocdut.model.GradeInfo;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.CacheService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.ui.basic.RefreshableActivity;
import com.emptypointer.hellocdut.ui.main.CourseScheduleAdapter;
import com.emptypointer.hellocdut.utils.DensityUtil;
import com.emptypointer.hellocdut.utils.JsonUtil;
import com.emptypointer.hellocdut.utils.StringChecker;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.emptypointer.hellocdut.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;

public class QueryGradeActivity extends RefreshableActivity<GradeInfo> {
    private static final String CACHE_KEY_GRADE = "grade";
    private EPRecyclerAdapter<GradeInfo> mAdapter;
    private double mCgpa;
    private double mGpa;
    private CacheService cacheService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromServer();
            }
        });

        mAdapter = new EPRecyclerAdapter<GradeInfo>(R.layout.row_grade, mDataSet) {
            @Override
            protected void onBindData(EPRecyclerViewHolder holder, int position, GradeInfo item) {
                holder.setText(R.id.textView_coursename, item.getName());
                holder.setText(R.id.textView_update_time, getString(R.string.str_format_insert_time,
                        item.getUpdateTime()));
                holder.setText(R.id.textView_semester, getString(R.string.str_format_semester,
                        item.getSemester()));
                holder.setText(R.id.textView_course_id, getString(R.string.str_format_course_id,
                        item.getCourseID()));
                holder.setText(R.id.textView_credits, getString(R.string.str_format_credits,
                        String.valueOf(item.getCredits())));
                holder.setText(R.id.textView_teacher, getString(R.string.str_format_teacher,
                        item.getTeacherName()));
                if(StringChecker.isFloat(item.getGpa())) {
                    float gpa = Float.valueOf(item.getGpa());
                    holder.setText(R.id.tvGPA, String.format("%.2f",gpa));
                }else{
                    holder.setText(R.id.tvGPA,"unknown");
                }
                holder.setText(R.id.textView_score, item.getScore());
                String strScore = item.getScore();
//                float size= getResources().getDisplayMetrics().widthPixels/15;
                float size = DensityUtil.sp2px(QueryGradeActivity.this, 18);
                if (StringChecker.isFloat(strScore) && strScore.length() > 1) {
                    size = size / (strScore.length() / 2);
                } else {
                    size = size / strScore.length();
                }
                holder.setTextSize(R.id.textView_score, size);
                if (StringChecker.isFloat(strScore)) {
                    float score = Float.valueOf(strScore);
                    if (score >= 90) {
                        holder.setCardViewBackgroud(R.id.cardViewScore, Color.parseColor(CourseScheduleAdapter.COLOR_TABLE[3]));
                    } else if (score >= 80) {
                        holder.setCardViewBackgroud(R.id.cardViewScore, Color.parseColor(CourseScheduleAdapter.COLOR_TABLE[16]));
                    } else if (score >= 70) {
                        holder.setCardViewBackgroud(R.id.cardViewScore, Color.parseColor(CourseScheduleAdapter.COLOR_TABLE[7]));
                    } else if (score >= 60) {
                        holder.setCardViewBackgroud(R.id.cardViewScore, Color.parseColor(CourseScheduleAdapter.COLOR_TABLE[8]));
                    } else {
                        holder.setCardViewBackgroud(R.id.cardViewScore, Color.parseColor(CourseScheduleAdapter.COLOR_TABLE[14]));
                    }
                } else {
                    switch (strScore) {
                        case "优":
                            holder.setCardViewBackgroud(R.id.cardViewScore, Color.parseColor(CourseScheduleAdapter.COLOR_TABLE[3]));
                            break;
                        case "良":
                            holder.setCardViewBackgroud(R.id.cardViewScore, Color.parseColor(CourseScheduleAdapter.COLOR_TABLE[8]));
                            break;
                        case "中":
                            holder.setCardViewBackgroud(R.id.cardViewScore, Color.parseColor(CourseScheduleAdapter.COLOR_TABLE[8]));
                            break;
                        case "及格":
                            holder.setCardViewBackgroud(R.id.cardViewScore, Color.parseColor(CourseScheduleAdapter.COLOR_TABLE[8]));
                            break;
                        case "差":
                            holder.setCardViewBackgroud(R.id.cardViewScore, Color.parseColor(CourseScheduleAdapter.COLOR_TABLE[14]));
                            break;
                        default:
                            holder.setCardViewBackgroud(R.id.cardViewScore, Color.parseColor(CourseScheduleAdapter.COLOR_TABLE[0]));
                            break;
                    }
                }
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        cacheService = CacheService.getInstance(this);
        DataCache dataCache = cacheService.getDataCache(CACHE_KEY_GRADE);
        if (dataCache != null)

        {
            jsonParse(dataCache.getDataJson());
        }

        loadDataFromServer();
    }

    private void loadDataFromServer() {
        Map<String, String> params = new HashMap<>();
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(this).getUserName()));
        params.put("user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(this)
                        .getToken()));
        params.put("action", "queryGrade");
        EPJsonObjectRequest request = new EPJsonObjectRequest(this, params, false, new EPJsonResponseListener(this) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (!result) {
                    return;
                }
                cacheService.setDataCache(CACHE_KEY_GRADE, response.toJSONString());
                jsonParse(response);
                UIUtils.makeSnake(mFab, getString(R.string.message_get_json_sucess));
//                Snackbar.make(mSwipeContainer, "数据更新成功", Snackbar.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected void onFinish() {
                mSwipeContainer.setRefreshing(false);
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

    private void jsonParse(JSONObject obj) {
        JSONArray array = obj.getJSONArray("subject");
        mDataSet.clear();
        mCgpa = JsonUtil.getDoublefaiOver(obj,"cgpa");
        mGpa = JsonUtil.getDoublefaiOver(obj,"gpa");
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String semester = object.getString("semester");
                String courseID = object.getString("course_code");
                String courseName = object.getString("course_name");
                String teacherName = object.getString("teacher");
                double credits= JsonUtil.getDoublefaiOver(object,"credits");
                String score = object.getString("score");
                String updateTime = object.getString("storage_time");
                String gpa = object.getString("gpa");
                String type = object.getString("type");
                GradeInfo grade = new GradeInfo(courseName, score, updateTime,
                        semester, courseID, credits, teacherName, gpa, type);
                mDataSet.add(grade);
            }
        }
        mAdapter.notifyDataSetChanged();

    }
}
