package com.emptypointer.hellocdut.ui.query;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.model.DataCache;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.CacheService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.emptypointer.hellocdut.utils.VolleyUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CampusOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CampusOverviewFragment extends Fragment {


    private static final String CACHE_CAMPUS_OVERVIRW = "cache_campus_overvirw";

    private CacheService mCacheService;
    private TextView textViewstatus;
    private TextView textViewbalance;
    private TextView textViewsubsidybalance;
    private TextView textViewmeal;
    private TextView textViewshower;
    private TextView textViewshopping;
    private TextView textViewbus;
    private TextView textViewtotal;
    private android.support.v4.widget.SwipeRefreshLayout swipeContainer;
    private android.support.v7.widget.CardView cardViewBasicInfo;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CampusOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CampusOverviewFragment newInstance() {
        CampusOverviewFragment fragment = new CampusOverviewFragment();
        return fragment;
    }

    public CampusOverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_campus_overview, container, false);
        this.cardViewBasicInfo = (CardView) view.findViewById(R.id.cardViewBasicInfo);
        this.swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        this.textViewtotal = (TextView) view.findViewById(R.id.textView_total);
        this.textViewbus = (TextView) view.findViewById(R.id.textView_bus);
        this.textViewshopping = (TextView) view.findViewById(R.id.textView_shopping);
        this.textViewshower = (TextView) view.findViewById(R.id.textView_shower);
        this.textViewmeal = (TextView) view.findViewById(R.id.textView_meal);
        this.textViewsubsidybalance = (TextView) view.findViewById(R.id.textView_subsidy_balance);
        this.textViewbalance = (TextView) view.findViewById(R.id.textView_balance);
        this.textViewstatus = (TextView) view.findViewById(R.id.textView_status);
        return view;
    }

    private void updateBasicInfo() {
        DataCache cache = mCacheService.getDataCache(CampusCardActivity.CACHE_COMPUSCARD_BASIC);
        if (cache == null) {
            cardViewBasicInfo.setVisibility(View.GONE);
            return;
        }
        cardViewBasicInfo.setVisibility(View.VISIBLE);
        JSONObject object = cache.getDataJson();
        String status = object.getString("user_card_status");
        String balance = object.getString("user_card_balance");
        String subsidyBanlance = object.getString("user_subsidy_balance");
        textViewstatus.setText(status);
        textViewbalance.setText(balance);
        textViewsubsidybalance.setText(subsidyBanlance);

    }

    public void onCaptchaUpdate(){
        updateBasicInfo();
        setRefreshing(true);
        loadDataFromServer();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromServer();
            }
        });
        mCacheService = CacheService.getInstance(getActivity());
        DataCache cache = mCacheService.getDataCache(CACHE_CAMPUS_OVERVIRW);
        updateBasicInfo();
        if (cache != null) {
            JsonParse(cache.getDataJson());
        } else {
            setRefreshing(true);
            loadDataFromServer();
        }
    }

    private void setRefreshing(final boolean refresh) {
        swipeContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(refresh);
            }
        }, 50);
    }

    private void loadDataFromServer() {
        Map<String, String> params = new HashMap<>();
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(getActivity()).getUserName()));
        params.put("user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(getActivity()).getToken()));
        params.put("action", "queryCustStateInfo");
        params.put("start_time", "2014-09-01");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String endTime = format
                .format(new Date(System.currentTimeMillis()));
        params.put("end_time", endTime);

        EPJsonObjectRequest request = new EPJsonObjectRequest(getActivity(), params, false, new EPJsonResponseListener(getActivity()) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (!result) {
                    if (mMessage.equals("time_out")) {
                        ((CampusCardActivity) getActivity()).showCaptchaDialog();
                    }
                    return;
                }
                JsonParse(response);
                mCacheService.setDataCache(CACHE_CAMPUS_OVERVIRW, response.toJSONString());
                UIUtils.makeSnake(swipeContainer, getString(R.string.message_get_json_sucess));
            }
        }) {
            @Override
            protected void onFinish() {
                setRefreshing(false);
                super.onFinish();
            }
        };
        request.setTag(this);
        VolleyUtil.getQueue(getActivity()).add(request);

    }

    @Override
    public void onDestroy() {
        VolleyUtil.getQueue(getActivity()).cancelAll(this);
        super.onDestroy();
    }

    private void JsonParse(JSONObject obj) {
        String meal = obj.getString("wallet_deals_amount");
        String shower = obj.getString("shower_amount");
        String shopping = obj.getString("shopping_amount");
        String bus = obj.getString("bus_amount");
        double total = Double.valueOf(meal.replaceAll(",", ""))
                + Double.valueOf(shower.replaceAll(",", ""))
                + Double.valueOf(shopping.replaceAll(",", ""))
                + Double.valueOf(bus.replaceAll(",", ""));
        textViewmeal.setText(meal);
        textViewshower.setText(shower);
        textViewshopping.setText(shopping);
        textViewbus.setText(bus);
        textViewtotal.setText(String
                .format("%.2f", total));
    }
}
