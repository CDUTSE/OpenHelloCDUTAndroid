package com.emptypointer.hellocdut.ui.basic;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.customer.EPRecyclerAdapter;
import com.mcxiaoke.next.recycler.EndlessRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sequarius on 2015/11/4.
 */
public class RefreshableActivity<T> extends EPActivity {
    private android.support.v7.widget.Toolbar mToolbar;
    protected EndlessRecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeContainer;
    protected List<T> mDataSet;
    protected android.support.design.widget.FloatingActionButton mFab;
    protected EPRecyclerAdapter<T> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.layout_with_up_fab_and_listview);
        mRecyclerView = (EndlessRecyclerView) findViewById(R.id.mRecyclerView);
        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        this.mFab = (FloatingActionButton) findViewById(R.id.fab);
        this.mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.scrollToPosition(0);
            }
        });
        mDataSet=new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TypedArray array=getResources().obtainTypedArray(R.array.progress_colors_light);
        int length=array.length();
        int[] resID=new int[length];
        for(int i=0;i<array.length();i++){
            resID[i]=array.getResourceId(i,0);
        }
        array.recycle();
        mSwipeContainer.setColorSchemeResources(resID);
        mRecyclerView.enable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    protected void refreshingComplete(){
        setRefreshing(false);
        mRecyclerView.onComplete();
    }

    protected void setRefreshing(final boolean refreshing){
        mSwipeContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeContainer.setRefreshing(refreshing);
            }
        }, 50);
    }
}
