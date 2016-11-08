package com.emptypointer.hellocdut.ui.query;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.DefaultRetryPolicy;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.customer.EPRecyclerAdapter;
import com.emptypointer.hellocdut.customer.EPRecyclerViewHolder;
import com.emptypointer.hellocdut.model.DataCache;
import com.emptypointer.hellocdut.model.LibBook;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.CacheService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.ui.basic.RefreshableFragment;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.emptypointer.hellocdut.utils.VolleyUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sequarius on 2015/11/7.
 */
public class LibOnBorrowFragment extends RefreshableFragment<LibBook> {

    private static final String CACHE_LIB_ON_BORROW = "catch_lib_on_borrow";
    private CacheService mCacheService;
    private int mPageNum;

    public static LibOnBorrowFragment newInstance() {
        return new LibOnBorrowFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCacheService = CacheService.getInstance(getActivity());
        mDataSet = new ArrayList<>();
        mAdapter = new EPRecyclerAdapter<LibBook>(R.layout.row_lib_on_borrow, mDataSet) {
            @Override
            protected void onBindData(final EPRecyclerViewHolder holder, int position, final LibBook item) {

                holder.setText(R.id.textView_title, item.getTitle());
                holder.setText(R.id.textView_id, getString(R.string.str_format_book_id, item.getId()));

                holder.setText(R.id.textView_index_id, getString(R.string.str_format_book_index_id,
                        item.getIndexID()));
                holder.setText(R.id.textView_borrow_time, getString(R.string.str_format_borrow_time,
                        item.getBorrowTime()));
                holder.setText(R.id.textView_return_time, getString(
                        R.string.str_format_return_time, item.getReturnTime()));
                holder.setText(R.id.textView_location, getString(R.string.str_format_book_location,
                        item.getLocation()));
                holder.setText(R.id.textView_renew_time, getString(R.string.str_format_renew_time,
                        item.getRenewTime()));
                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    long returnTimeMillis = fm.parse(item.getReturnTime()).getTime();
                    if (returnTimeMillis < System.currentTimeMillis()) {
                        holder.setText(R.id.button_renew, getString(R.string.str_over_dead_line));
                        holder.setViewEnable(R.id.button_renew, false);
                    } else {
                        holder.setOnClickListener(R.id.button_renew, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                renewBook(item.getRenewURL(), holder,item.getTitle());
                            }
                        });
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBorrowListFromServer();
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        DataCache cache = mCacheService.getDataCache(CACHE_LIB_ON_BORROW);
        if (cache != null) {
            jsonParse(cache.getDataJson(), false);
        }else{
            setRefreshing(true);
            getBorrowListFromServer();
        }
    }


    private void renewBook(String renewUrl, final EPRecyclerViewHolder holder, final String title) {
        Map<String, String> params = new HashMap<>();
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(getActivity()).getUserName()));
        params.put("user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(getActivity()).getToken()));
        params.put("action", "queryLibInfo");
        params.put("flag", "4");
        params.put("renew_href", renewUrl);

        EPJsonObjectRequest request = new EPJsonObjectRequest(getActivity(), params, new EPJsonResponseListener(getActivity()) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (result) {
                    String strRetrunTime = response.getString("back_time");
                    holder.setText(R.id.textView_return_time, getString(
                            R.string.str_format_return_time, strRetrunTime));
                    UIUtils.makeSnake(mFab, getString(R.string.message_format_renew_book_finish,title,strRetrunTime));
                }
            }
        });
        request.setTag(this);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.LONG_CONNECT_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyUtil.getQueue(getActivity()).add(request);

    }

    private void getBorrowListFromServer() {
        Map<String, String> params = new HashMap<>();
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(getActivity()).getUserName()));
        params.put("user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(getActivity()).getToken()));
        params.put("action", "queryLibInfo");
        params.put("flag", "2");
        if (mPageNum != 0) {
            params.put("jump_page", String
                    .valueOf(mPageNum));
        }

        EPJsonObjectRequest request = new EPJsonObjectRequest(getActivity(), params, false, new EPJsonResponseListener(getActivity()) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (!result) {
                    return;
                }
                if (mPageNum == 0) {
                    mCacheService.setDataCache(CACHE_LIB_ON_BORROW, response.toJSONString());
                    jsonParse(response, false);
                    UIUtils.makeSnake(mFab,getString(R.string.message_get_json_sucess));
                } else {
                    jsonParse(response, true);
                }

            }
        }) {
            @Override
            protected void onFinish() {
                super.onFinish();
                refreshingComplete();
            }
        };
        request.setTag(this);
        request.setTimeOut(
                Constant.LONG_CONNECT_TIME_OUT);
        VolleyUtil.getQueue(getActivity()).add(request);
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
        // mCurrentPage = obj.getIntValue("current_page");
        // mEndPage = obj.getIntValue("total_page");
        JSONArray array = obj.getJSONArray("borrowed_book");
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String title = object.getString("title");
                String id = object.getString("barCode");
                String returnTime = object.getString("returnTime");
                String borrowTime = object.getString("borrowTime");
                String renewTime = object.getString("renewTime");
                String indexID = object.getString("bookIndexCode");
                String location = object.getString("bookLocation");
                String renewURL = object.getString("bookRenewHref");

                LibBook item = new LibBook(title, id, borrowTime,
                        returnTime, location, indexID, renewURL, renewTime);
                mDataSet.add(item);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
