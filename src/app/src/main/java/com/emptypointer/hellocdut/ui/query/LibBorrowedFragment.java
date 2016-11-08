package com.emptypointer.hellocdut.ui.query;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.mcxiaoke.next.recycler.EndlessRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sequarius on 2015/11/7.
 */
public class LibBorrowedFragment extends RefreshableFragment<LibBook> {

    private static final String CACHE_LIB_BORROWED = "cache_book_lib_borrowed";

    private int mCurrentPage = 0;
    private int mEndPage = 1;

    private CacheService mCacheService;

    public static LibBorrowedFragment newInstance() {
        return new LibBorrowedFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCacheService = CacheService.getInstance(getActivity());
        mDataSet = new ArrayList<>();

        mAdapter = new EPRecyclerAdapter<LibBook>(R.layout.row_borrowed_history, mDataSet) {
            @Override
            protected void onBindData(EPRecyclerViewHolder viewHolder, int position, LibBook item) {
                viewHolder.setText(R.id.textView_title, item
                        .getTitle());
                viewHolder.setText(R.id.textView_id, getString(R.string.str_format_book_id,
                        item.getId()));
                viewHolder.setText(R.id.textView_operate_time, getString(R.string.str_format_return_time,
                        item.getReturnTime()));
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnLoadMoreListener(new EndlessRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore(EndlessRecyclerView view) {

                if (mCurrentPage < mEndPage) {
                    loadDataFromServer();
                } else {
                    refreshingComplete();
                    UIUtils.makeSnake(mFab, getString(R.string.message_hasnot_more));
                }
            }
        });
        mRecyclerView.enable(true);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage = 0;
                loadDataFromServer();
            }
        });

        DataCache cache=mCacheService.getDataCache(CACHE_LIB_BORROWED);
        if(cache!=null){
            jsonParse(cache.getDataJson(),false);
        }else{
            setRefreshing(true);
            loadDataFromServer();
        }

    }

    private void loadDataFromServer() {
        Map<String, String> params = new HashMap<>();
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(getActivity()).getUserName()));
        params.put("user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(getActivity())
                        .getToken()));
        params.put("action", "queryLibInfo");
        params.put("flag", "3");
        if (mCurrentPage != 0) {
            params.put("jump_page", String
                    .valueOf(mCurrentPage+1));
        }
        EPJsonObjectRequest request = new EPJsonObjectRequest(getActivity(), params, false, new EPJsonResponseListener(getActivity()) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (!result) {
                    return;
                }
                if (mCurrentPage == 0) {
                    mCacheService.setDataCache(CACHE_LIB_BORROWED, response.toJSONString());
                    jsonParse(response, false);
                } else {
                    jsonParse(response, true);
                }

                UIUtils.makeSnake(mFab,getString(R.string.message_get_json_sucess),false);
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
        JSONObject jsonHistory = obj.getJSONObject("history");
        if (!appendable) {
            mDataSet.clear();
        }
        mCurrentPage = jsonHistory.getIntValue("current_page");
        mEndPage = jsonHistory.getIntValue("total_page");
        JSONArray array = jsonHistory.getJSONArray("borrow_history");
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String title = object.getString("title");
                String id = object.getString("bar_code");
                String returnTime = object.getString("handle_time");
                LibBook item = new LibBook(title, id, returnTime);
                mDataSet.add(item);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
