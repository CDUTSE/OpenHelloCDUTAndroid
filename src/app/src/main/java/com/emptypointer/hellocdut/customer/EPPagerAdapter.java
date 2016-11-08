package com.emptypointer.hellocdut.customer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.widget.BadgeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sequarius on 2015/10/28.
 */
public class EPPagerAdapter extends FragmentPagerAdapter {
    private List<String> mTitles;
    private List<Fragment> mFragments;
    private List<BadgeView> mBageViews;
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    public BadgeView getBageView(int position){
        return mBageViews.get(position);
    }

    public EPPagerAdapter(FragmentManager fm, List<String> mTitles, List<Fragment> mFragments) {
        super(fm);
        this.mTitles = mTitles;
        this.mFragments = mFragments;
        mBageViews=new ArrayList<>();
    }

    public EPPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    public View getTabView(int position,Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.main_tab_item, null);
        TextView tv= (TextView) view.findViewById(R.id.tvTitle);
        tv.setText(mTitles.get(position));
        mBageViews.add((BadgeView)view.findViewById(R.id.badgeView));
//        view.setOnClickListener();
//        ImageView img = (ImageView) view.findViewById(R.id.imageView);
//        img.setImageResource(imageResId[position]);
        return view;
    }
}
