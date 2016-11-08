package com.emptypointer.hellocdut.ui.account;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.customer.EPPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResetPwdActivity extends EPActivity implements ResetPwdByMailFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private EPPagerAdapter mPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager viewPager;
    private TabLayout tbIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_pager_indicator);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
//        String[] title=getResources().getStringArray(R.array.indicator_reset_password);
        List<Fragment> fragments=new ArrayList<>();
        fragments.add(ResetPwdByAAOFragment.newInstance());
        fragments.add(ResetPwdByMailFragment.newInstance());
        List<String> title=Arrays.asList(getResources().getStringArray(R.array.indicator_reset_password));
        mPagerAdapter = new EPPagerAdapter(getSupportFragmentManager(), title,fragments);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(mPagerAdapter);

        tbIndicator=(TabLayout)findViewById(R.id.tbIndicator);
        tbIndicator.setupWithViewPager(viewPager);
        tbIndicator.setTabMode(TabLayout.MODE_FIXED);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */


}
