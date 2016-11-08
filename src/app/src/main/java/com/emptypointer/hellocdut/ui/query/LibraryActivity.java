package com.emptypointer.hellocdut.ui.query;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.customer.EPPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibraryActivity extends EPActivity {


    private android.support.design.widget.TabLayout tbIndicator;
    private android.support.v4.view.ViewPager viewPager;
    private LinearLayout maincontent;
    private EPPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_pager_indicator);
        this.maincontent = (LinearLayout) findViewById(R.id.main_content);
        this.viewPager = (ViewPager) findViewById(R.id.container);
        this.tbIndicator = (TabLayout) findViewById(R.id.tbIndicator);
        List<String> title= Arrays.asList(getResources().getStringArray(R.array.indicator_lib));
        List<Fragment> fragments=new ArrayList<>();
        fragments.add(LibOnBorrowFragment.newInstance());
        fragments.add(LibBorrowedFragment.newInstance());
        mPagerAdapter = new EPPagerAdapter(getSupportFragmentManager(), title,fragments);
        viewPager.setAdapter(mPagerAdapter);
        tbIndicator.setupWithViewPager(viewPager);
        tbIndicator.setTabMode(TabLayout.MODE_FIXED);
    }
}
