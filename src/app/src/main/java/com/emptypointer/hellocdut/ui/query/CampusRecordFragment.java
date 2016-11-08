package com.emptypointer.hellocdut.ui.query;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.customer.EPRecyclerAdapter;
import com.emptypointer.hellocdut.customer.EPRecyclerViewHolder;
import com.emptypointer.hellocdut.model.DataCache;
import com.emptypointer.hellocdut.model.TradeItem;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.CacheService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.ui.basic.RefreshableFragment;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.mcxiaoke.next.recycler.EndlessRecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sequarius on 2015/11/8.
 */
public class CampusRecordFragment extends RefreshableFragment<TradeItem> {

    private int mCurrentPage=0;
    private int mEndPage=1;
    
    private CacheService mCacheService;
    public static final String CACHE_RECORD = "cache_campus_record";
    public static CampusRecordFragment newInstance(){
        return new CampusRecordFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter=new EPRecyclerAdapter<TradeItem>(R.layout.row_trade_record,mDataSet) {
            @Override
            protected void onBindData(EPRecyclerViewHolder viewHolder, int position, TradeItem item) {
                viewHolder.setText(R.id.textView_name,item.getName());
                viewHolder.setText(R.id.textView_amount,getString(R.string.str_format_amount, item.getAmount()));
                viewHolder.setText(R.id.textView_balance,getString(R.string.str_format_balance, item.getBalance()));
                viewHolder.setText(R.id.textView_operator,getString(R.string.str_format_opetater, item.getOpertator()));
                viewHolder.setText(R.id.textView_location,getString(R.string.str_format_location, item.getLocation()));
                viewHolder.setText(R.id.textView_operate_time,getString(R.string.str_format_time, item.getTime()));
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mCacheService=mCacheService.getInstance(getActivity());
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromServer();
            }
        });
        mRecyclerView.enable(true);
        mRecyclerView.setOnLoadMoreListener(new EndlessRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore(EndlessRecyclerView view) {
                if (mCurrentPage < mEndPage) {
                    loadDataFromServer();
                } else {
                    UIUtils.makeSnake(mFab, getString(R.string.message_hasnot_more));
                    refreshingComplete();
                }
            }
        });
        DataCache cache=mCacheService.getDataCache(CACHE_RECORD);
        if(cache!=null){
            jsonParse(cache.getDataJson(),false);
        }else{
            loadDataFromServer();
            setRefreshing(true);
        }
    }

    private void loadDataFromServer() {

        Map<String,String> params=new HashMap<>();
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(getActivity()).getUserName()));
        params.put("user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(getActivity())
                        .getToken()));
        params.put("action", "queryConsumeInfo");
        params.put("start_time", "2014-09-01");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String endTime = format
                .format(new Date(System.currentTimeMillis()));
        params.put("end_time", endTime);
        if(mCurrentPage!=0){
            params.put("jump_page", String
                    .valueOf(mCurrentPage+1));
        }
        EPJsonObjectRequest request=new EPJsonObjectRequest(getActivity(),params,false,new EPJsonResponseListener(getActivity()){
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if(!result){
                    if (mMessage.equals("time_out")) {
                        ((CampusCardActivity) getActivity()).showCaptchaDialog();
                    }
                }
                if(mCurrentPage==0){
                    mCacheService.setDataCache(CACHE_RECORD,response.toJSONString());
                    jsonParse(response, false);
                }else{
                    jsonParse(response,true);
                }
                UIUtils.makeSnake(mFab,getString(R.string.message_get_json_sucess));
            }
        }){
            @Override
            protected void onFinish() {
                super.onFinish();
                refreshingComplete();
            }
        };
        request.setTag(this);
        VolleyUtil.getQueue(getActivity()).add(request);
    }

    public void onCaptchaUpdate(){
        setRefreshing(true);
        loadDataFromServer();
    }

    @Override
    public void onDestroy() {
        VolleyUtil.getQueue(getActivity()).cancelAll(this);
        super.onDestroy();
    }

    private void jsonParse(JSONObject obj, boolean appendable) {
        if (!appendable) {
            mDataSet.clear();
        }

        mCurrentPage = obj.getIntValue("current_page");
        int endPage = obj.getIntValue("total_page");
        mEndPage = endPage;
        JSONArray array = obj.getJSONArray("consume_info");
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("item");
                String time = new StringBuilder(object.getString("date"))
                        .append(" ").append(object.getString("time"))
                        .toString();
                String amount = object.getString("money");
                String balance = object.getString("balance");
                String operator = object.getString("operator");
                String location = object.getString("workstation");
                mDataSet.add(new TradeItem(time, amount, balance, operator,
                        location, name));
            }
            mAdapter.notifyDataSetChanged();
        }
    }

}
