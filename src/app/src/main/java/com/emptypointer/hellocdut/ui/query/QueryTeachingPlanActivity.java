package com.emptypointer.hellocdut.ui.query;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.customer.EPCommonAdapter;
import com.emptypointer.hellocdut.customer.EPCommonViewHolder;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.customer.EPRecyclerAdapter;
import com.emptypointer.hellocdut.customer.EPRecyclerViewHolder;
import com.emptypointer.hellocdut.model.TeachingPlan;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.ui.basic.SimpleBrowserActivity;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.rey.material.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryTeachingPlanActivity extends EPActivity {

    private android.widget.EditText etKeyword;
    private com.rey.material.widget.Button btnSearch;
    private android.widget.LinearLayout layoutsearch;
    private RecyclerView lvResult;
    private List<TeachingPlan> mPlans;

    private EPRecyclerAdapter<TeachingPlan> mAdpter;

    private static final String PDF_URL_PREFIX="http://mozilla.github.io/pdf.js/web/viewer.html?file=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_teaching_plan);
        this.lvResult = (RecyclerView) findViewById(R.id.lvResult);
        this.layoutsearch = (LinearLayout) findViewById(R.id.layout_search);
        this.btnSearch = (Button) findViewById(R.id.btnSearch);
        this.etKeyword = (EditText) findViewById(R.id.etKeyword);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataFromServer();
            }
        });

        mPlans=new ArrayList<>();

        mAdpter=new EPRecyclerAdapter<TeachingPlan>(R.layout.row_teaching_plan,mPlans) {
            @Override
            protected void onBindData(EPRecyclerViewHolder viewHolder, int position, TeachingPlan item) {
                viewHolder.setText(R.id.tvName, item.getName()).setText(R.id.tvMajor, getString(R.string.str_format_major, item.getMajro())).setText(R.id.tvMaketime, getString(R.string.str_format_make_date, item.getMakedate()));
            }
        };

        lvResult.setAdapter(mAdpter);
        lvResult.setLayoutManager(new LinearLayoutManager(this));

        mAdpter.setOnItemClickListener(new EPRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(QueryTeachingPlanActivity.this, SimpleBrowserActivity.class);
                intent.putExtra(SimpleBrowserActivity.INTENT_TITTLE, mPlans.get(position).getName());
                intent.putExtra(SimpleBrowserActivity.INTENT_URL, PDF_URL_PREFIX+mPlans.get(position).getUrl());
                startActivity(intent);
            }
        });
        UserInfo userInfo=UserInfo.getInstance(this);
        if(!TextUtils.isEmpty(userInfo.getMajorName())){
            etKeyword.setText(userInfo.getMajorName());
            etKeyword.setSelection(userInfo.getMajorName().length());
        }
    }

    private void loadDataFromServer() {
        String keyword = etKeyword.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            etKeyword.setError(null);
            etKeyword.setError(getString(R.string.error_field_required));
            return;
        }
        Map<String,String> params=new HashMap<>();
        params.put("action", "queryTeachingPlan");
        params.put("user_name", EPSecretService.encryptByPublic(UserInfo.getInstance(this).getUserName()));
        params.put("user_login_token", EPSecretService.encryptByPublic(UserInfo.getInstance(this).getToken()));
        params.put("key_words", keyword);
        EPJsonObjectRequest request=new EPJsonObjectRequest(this,params,new EPJsonResponseListener(this){
            @Override
            public void onResponse(com.alibaba.fastjson.JSONObject response) {
                super.onResponse(response);
                mPlans.clear();
                if (result) {
                    try {
                        JSONArray array = response.getJSONArray("pdf_list");
                        for (int i = 0; i < array.size(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String name = object.getString("name");
                            String url = object.getString("url");
                            String major = object.getString("major");
                            long id = object.getLong("id");
                            String makeDate = object.getString("make_date");
                            mPlans.add(new TeachingPlan(name, major, makeDate, id, url));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mAdpter.notifyDataSetChanged();
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
