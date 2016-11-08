package com.emptypointer.hellocdut.ui.query;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.customer.EPRecyclerAdapter;
import com.emptypointer.hellocdut.customer.EPRecyclerViewHolder;
import com.emptypointer.hellocdut.model.TradeItem;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.ui.basic.RefreshableActivity;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.mcxiaoke.next.recycler.EndlessRecyclerView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sequarius on 2015/11/9.
 */
public class CampusQueryResultActivity extends RefreshableActivity<TradeItem> {
    private static final String TAG = "CampusQueryResult";
    private int mCurrentType = -1;
    private int mCurrentPage = 0;
    private int mEndPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String intentDate = getIntent().getStringExtra(
                CampusQueryFragment.INTENT_ACTION);
        for (int i = 0; i < CampusQueryFragment.INTENT_TYPE_QUERYDEPOSITINFO.length; i++) {
            if (intentDate
                    .equals(CampusQueryFragment.INTENT_TYPE_QUERYDEPOSITINFO[i])) {
                mCurrentType = i;
                String[] labels = getResources().getStringArray(
                        R.array.trade_query_type);
                getSupportActionBar().setTitle(labels[i]);
                break;
            }
        }
        if (mCurrentType == 3) {
            mRecyclerView.setEnabled(false);
            mRecyclerView.setClickable(false);
            mAdapter=new EPRecyclerAdapter<TradeItem>(R.layout.card_view_consume_count,mDataSet) {
                @Override
                protected void onBindData(EPRecyclerViewHolder viewHolder, int position, TradeItem item) {
                    Log.i(TAG,"into here");
                    String name = item.getName();
                    JSONObject obj = JSONObject.parseObject(name);
                    String meal = obj.getString("wallet_deals_amount");
                    String shower = obj.getString("shower_amount");
                    String shopping = obj.getString("shopping_amount");
                    String bus = obj.getString("bus_amount");
                    double total = Double.valueOf(meal.replaceAll(",", ""))
                            + Double.valueOf(shower.replaceAll(",", ""))
                            + Double.valueOf(shopping.replaceAll(",", ""))
                            + Double.valueOf(bus.replaceAll(",", ""));
                    viewHolder.setText(R.id.textView_meal, meal);
                    viewHolder.setText(R.id.textView_shower, shower);
                    viewHolder.setText(R.id.textView_shopping, shopping);
                    viewHolder.setText(R.id.textView_total, String.format("%.2f", total));
                    viewHolder.setText(R.id.textView_bus, String.format(bus));
                }
            };
            mFab.setVisibility(View.GONE);
        }else{
            mAdapter = new EPRecyclerAdapter<TradeItem>(R.layout.row_trade_record, mDataSet) {
                @Override
                protected void onBindData(EPRecyclerViewHolder viewHolder, int position, TradeItem item) {
                    switch (mCurrentType) {
                        case 0:
                            viewHolder.setText(R.id.textView_name, item.getName());
                            viewHolder.setText(R.id.textView_amount, getString(R.string.str_format_amount,
                                    item.getAmount()));
                            viewHolder.setText(R.id.textView_balance, getString(R.string.str_format_balance,
                                    item.getBalance()));
                            viewHolder.setText(R.id.textView_operator, getString(
                                    R.string.str_format_terminal_name,
                                    item.getOpertator()));
                            viewHolder.setText(R.id.textView_location, getString(R.string.str_format_location,
                                    item.getLocation()));
                            viewHolder.setText(R.id.textView_operate_time, getString(R.string.str_format_time,
                                    item.getTime()));
                            break;
                        case 1:
                            viewHolder.setText(R.id.textView_name, item.getName());
                            viewHolder.setText(R.id.textView_amount, getString(
                                    R.string.str_format_back_amount, item.getAmount()));
                            viewHolder.setViewVisibility(R.id.textView_balance, View.GONE);
                            viewHolder.setText(R.id.textView_operator, getString(R.string.str_format_bank_type,
                                    item.getOpertator()));
                            viewHolder.setText(R.id.textView_location, getString(R.string.str_format_result,
                                    item.getDescribe()));
                            viewHolder.setText(R.id.textView_operate_time, getString(R.string.str_format_time,
                                    item.getTime()));
                            break;
                        case 2:

                            viewHolder.setText(R.id.textView_name, item.getName());
                            viewHolder.setText(R.id.textView_amount, getString(R.string.str_format_amount,
                                    item.getAmount()));
                            viewHolder.setText(R.id.textView_balance, getString(R.string.str_format_balance,
                                    item.getBalance()));
                            viewHolder.setText(R.id.textView_operator, getString(R.string.str_format_opetater,
                                    item.getOpertator()));
                            viewHolder.setText(R.id.textView_location, getString(R.string.str_format_location,
                                    item.getLocation()));
                            viewHolder.setText(R.id.textView_operate_time, getString(R.string.str_format_time,
                                    item.getTime()));
                            break;

                        case 3:
//                        convertView = View.inflate(mContext,
//                                R.layout.layout_consume_overview, null);
//                        String name = item.getName();
//                        JSONObject obj = JSONObject.parseObject(name);
//                        String meal = obj.getString("wallet_deals_amount");
//                        String shower = obj.getString("shower_amount");
//                        String shopping = obj.getString("shopping_amount");
//                        String bus = obj.getString("bus_amount");
//                        double total = Double.valueOf(meal.replaceAll(",", ""))
//                                + Double.valueOf(shower.replaceAll(",", ""))
//                                + Double.valueOf(shopping.replaceAll(",", ""))
//                                + Double.valueOf(bus.replaceAll(",", ""));
//                        viewHolder.setText(R.id.textView_meal,(meal);
//                        viewHolder.setText(R.id.textView_shower,shower);
//                        viewHolder.setText(R.id.textView_shopping,shopping);
//                        viewHolder.setText(R.id.textView_total,String.format("%.2f", total));
//                        viewHolder.setText(R.id.textView_bus,String.format(bus));
                            break;
                        default:
                            break;
                    }
                }
            };
            mRecyclerView.enable(true);
            mRecyclerView.setOnLoadMoreListener(new EndlessRecyclerView.OnLoadMoreListener() {
                @Override
                public void onLoadMore(EndlessRecyclerView view) {
                    if (mCurrentPage < mEndPage) {
                        loadDataFromServer();
                    } else {
                        UIUtils.makeSnake(CampusQueryResultActivity.this, getString(R.string.message_hasnot_more));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                refreshingComplete();
                            }
                        }, Constant.DELAY_ACTIVITY_FINISH);
                    }
                }
            });
        }


        mRecyclerView.setAdapter(mAdapter);
        
        
        mSwipeContainer.setEnabled(false);
        loadDataFromServer();
        setRefreshing(true);
    }

    private void loadDataFromServer() {
        Map<String, String> params = new HashMap<>();
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(this).getUserName()));
        params.put("user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(this).getToken()));
        String strAction = getIntent().getStringExtra(
                CampusQueryFragment.INTENT_ACTION);
        params.put("action", strAction);
        params.put("start_time", getIntent()
                .getStringExtra(CampusQueryFragment.INTENT_START_DATE));
        params.put("end_time", getIntent()
                .getStringExtra(CampusQueryFragment.INTENT_END_DATE));

        if (mCurrentPage != 0) {
            params.put("jump_page", String
                    .valueOf(mCurrentPage + 1));
        }
        EPJsonObjectRequest request = new EPJsonObjectRequest(this, params, false, new EPJsonResponseListener(this) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (!result) {
                    if (mMessage.equals("time_out")) {
                        Intent intent = new Intent();
                        intent.putExtra(CampusQueryFragment.ACTIVITY_RESULT_KEY,
                                false);
                        // 设置返回数据
                        CampusQueryResultActivity.this.setResult(RESULT_OK, intent);
                        // 关闭Activity
                        CampusQueryResultActivity.this.finish();
                    }
                    return;
                }
                switch (mCurrentType) {
                    case 0:
                        if (mCurrentPage == 0) {

                            jsonParseDeposit(response, false);

                        } else {
                            jsonParseDeposit(response, true);
                        }

                        break;
                    case 1:
                        if (mCurrentPage == 0) {

                            jsonParseBank(response, false);

                        } else {
                            jsonParseBank(response, true);
                        }

                        break;
                    case 2:
                        if (mCurrentPage == 0) {

                            jsonParseConSumen(response, false);

                        } else {
                            jsonParseConSumen(response, true);
                        }

                        break;
                    case 3:
                        jsonParseTotal(response);
                        break;

                    default:
                        break;
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
        VolleyUtil.getQueue(this).

                add(request);

    }

    private void jsonParseTotal(JSONObject response) {
        mDataSet.clear();
        mDataSet.add(new TradeItem(null, null, null, null, response.toString()));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        VolleyUtil.getQueue(this).cancelAll(this);
        super.onDestroy();
    }

    /**
     * 交易记录查询Json解析
     *
     * @param obj
     * @param appendable
     */
    private void jsonParseConSumen(JSONObject obj, boolean appendable) {
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

    /**
     * 存款记录查询Json解析
     *
     * @param obj
     * @param appendable
     */
    private void jsonParseDeposit(JSONObject obj, boolean appendable) {
        if (!appendable) {
            mDataSet.clear();
        }

        mCurrentPage = obj.getIntValue("current_page");
        int endPage = obj.getIntValue("total_page");
        mEndPage = endPage;
        JSONArray array = obj.getJSONArray("deposit_info");
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("terminal");
                String time = new StringBuilder(object.getString("date"))
                        .append(" ").append(object.getString("time"))
                        .toString();
                String amount = object.getString("money");
                String balance = object.getString("balance");
                String operator = object.getString("terminal_name");
                String location = object.getString("workstation");
                mDataSet.add(new TradeItem(time, amount, balance, operator,
                        location, name));
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 交易记录查询Json解析
     *
     * @param obj
     * @param appendable
     */
    private void jsonParseBank(JSONObject obj, boolean appendable) {
        if (!appendable) {
            mDataSet.clear();
        }

        mCurrentPage = obj.getIntValue("current_page");
        int endPage = obj.getIntValue("total_page");
        mEndPage = endPage;

        JSONArray array = obj.getJSONArray("bank_info");
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = "圈存记录";
                String time = new StringBuilder(object.getString("date"))
                        .append(" ").append(object.getString("time"))
                        .toString();
                String amount = object.getString("balance");
                String tpye = object.getString("terminal");
                String result = object.getString("description");
                mDataSet.add(new TradeItem(time, result, amount, tpye, name));
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}



