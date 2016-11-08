package com.emptypointer.hellocdut.ui.main;

import android.app.UiModeManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPProgressDialog;
import com.emptypointer.hellocdut.customer.EPRecyclerAdapter;
import com.emptypointer.hellocdut.customer.EPRecyclerViewHolder;
import com.emptypointer.hellocdut.model.DataCache;
import com.emptypointer.hellocdut.model.EvaluationItem;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.CacheService;
import com.emptypointer.hellocdut.ui.basic.RefreshableActivity;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.DialogButtonClickListner;
import com.emptypointer.hellocdut.utils.UIUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EvaluationActivity extends RefreshableActivity<EvaluationItem> {
    public static final String API_EVALUATION_LIST = "/api/evaluation_list";
    private static final String TAG = "EvaluationActivity";
    private static final String CACHE_EVALUATION_LIST = "cache_evaluation_list";
    public static final String API_EVALUATION_TEACHING = "/api/evaluation_teaching";
    private static final int MENU_ID_ONE_KEY_EVALUATION = 0x231;
    private OkHttpClient client = new OkHttpClient();
    private CacheService mCacheService;
    public static final MediaType TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final String[] RANK_TABLE = new String[]{"A", "B", "C", "D", "E"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataSet = new ArrayList<>();
        mAdapter = new EPRecyclerAdapter<EvaluationItem>(R.layout.row_evaluation_teaching, mDataSet) {
            @Override
            protected void onBindData(EPRecyclerViewHolder viewHolder, final int position, EvaluationItem item) {
                viewHolder.setText(R.id.textView_name, item
                        .getClassName());
                viewHolder.setText(R.id.textView_teacher, item.getTeacher());
                viewHolder.setOnclickListenner(R.id.button_function, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<EvaluationItem> evaluationItems = new ArrayList<>();
                        evaluationItems.add(mDataSet.get(position));
                        createEvaluationLevelDialog(evaluationItems);
                    }
                });

                viewHolder.setViewEnable(R.id.button_function, !item.isEvaluated());
                viewHolder.setClickable(R.id.button_function, !item.isEvaluated());
                viewHolder.setText(R.id.button_function, item.isEvaluated() ? "已评价" : "评教");

            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromServer();
            }
        });
        mCacheService = CacheService.getInstance(this);
        DataCache cache = mCacheService.getDataCache(CACHE_EVALUATION_LIST);
        if (cache != null) {
            Jsonparse(cache.getDataJson());
        } else {
            setRefreshing(true);
            loadDataFromServer();
        }
        mFab.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == MENU_ID_ONE_KEY_EVALUATION) {
            Log.d(TAG, "into");
//            evaluation("A",mDataSet);
            int count=0;
            for(EvaluationItem itemEvalation:mDataSet){
                if(!itemEvalation.isEvaluated()){
                    count++;
                }
            }
            if(count>0) {
                createEvaluationLevelDialog(mDataSet);
            }else{
                UIUtils.makeSnake(this,"恭喜，你已完成所有科目的评教！");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ID_ONE_KEY_EVALUATION, 000, R.string.str_one_key_evaluation);
        menu.getItem(0).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    private void loadDataFromServer() {
        StringBuilder sb = new StringBuilder(Constant.SERVER_QCLOUD).append(API_EVALUATION_LIST)
                .append("/").append(UserInfo.getInstance(getBaseContext()).getUserName())
                .append("/").append(UserInfo.getInstance(getBaseContext()).getToken());
        Request request = new Request.Builder().url(sb.toString()).get().tag(this).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIUtils.makeSnake(EvaluationActivity.this, getString(R.string.message_unparsed_json), false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()!=200){
                    UIUtils.makeSnake(EvaluationActivity.this, getString(R.string.message_unparsed_json), false);
                    return;
                }
                JSONObject object = JSONObject.parseObject(response.body().string());
                if (!object.getBoolean("result")) {
                    UIUtils.makeSnake(EvaluationActivity.this, object.getString("message"));
                    return;
                }
                Jsonparse(object);
                mCacheService.setDataCache(CACHE_EVALUATION_LIST, object.toJSONString());
            }
        });
    }

    private void Jsonparse(JSONObject object) {
        mDataSet.clear();
        JSONArray array = object.getJSONArray("evaluation_list");
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            mDataSet.add(JSON.parseObject(obj.toJSONString(), EvaluationItem.class));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                mSwipeContainer.setRefreshing(false);
            }
        });
    }

    private void evaluation(String rank, List<EvaluationItem> items) {
        JSONObject object = new JSONObject();
        object.put("username", UserInfo.getInstance(getBaseContext()).getUserName());
        object.put("token", UserInfo.getInstance(getBaseContext()).getToken());
        object.put("level", rank);
        object.put("evaluation_list", items);
        RequestBody body = RequestBody.create(TYPE_JSON, object.toJSONString());
        final EPProgressDialog dialog = new EPProgressDialog();
        dialog.show(getSupportFragmentManager(), TAG);
        Request request = new Request.Builder().tag(this).url(Constant.SERVER_QCLOUD + API_EVALUATION_TEACHING).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismissAllowingStateLoss();
                UIUtils.makeSnake(EvaluationActivity.this, getString(R.string.message_unparsed_json), false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()!=200){
                    UIUtils.makeSnake(EvaluationActivity.this, getString(R.string.message_unparsed_json), false);
                    return;
                }
                dialog.dismissAllowingStateLoss();
                JSONObject jsonObject = JSON.parseObject(response.body().string());
                if (jsonObject.getBoolean("result")) {
                    UIUtils.makeSnake(EvaluationActivity.this, "评教成功！");
                    loadDataFromServer();
                } else {
                    UIUtils.makeSnake(EvaluationActivity.this, jsonObject.getString("message"), false);
                }
            }
        });
    }

    private void createEvaluationLevelDialog(final List<EvaluationItem> items) {
        UIUtils.showSingleChoiceDialog(getSupportFragmentManager(), new String[]{"优", "良", "中", "一般", "差"}, 0, getString(R.string.str_evaluation_level), new DialogButtonClickListner() {
            @Override
            public void onclick(int index) {
                Log.d(TAG, index + "");
                evaluation(RANK_TABLE[index], items);
            }
        });

    }
}
