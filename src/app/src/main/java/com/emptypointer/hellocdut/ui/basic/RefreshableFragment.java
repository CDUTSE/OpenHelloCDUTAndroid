package com.emptypointer.hellocdut.ui.basic;


import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPRecyclerAdapter;
import com.mcxiaoke.next.recycler.EndlessRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RefreshableFragment#} factory method to
 * create an instance of this fragment.
 */
public class RefreshableFragment<T> extends Fragment {
    protected EPRecyclerAdapter<T> mAdapter;
    private android.support.v7.widget.Toolbar mToolbar;
    protected EndlessRecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeContainer;
    protected List<T> mDataSet;
    protected android.support.design.widget.FloatingActionButton mFab;

    public RefreshableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_with_up_fab_and_listview, container, false);
        mRecyclerView = (EndlessRecyclerView) view.findViewById(R.id.mRecyclerView);
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        this.mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        this.mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.scrollToPosition(0);
            }
        });
        mDataSet=new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TypedArray array=getResources().obtainTypedArray(R.array.progress_colors_light);
        int length=array.length();
        int[] resID=new int[length];
        for(int i=0;i<array.length();i++){
            resID[i]=array.getResourceId(i,0);
        }
        array.recycle();
        mSwipeContainer.setColorSchemeResources(resID);
        mRecyclerView.enable(false);
        return view;
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
        },50);
    }


}
