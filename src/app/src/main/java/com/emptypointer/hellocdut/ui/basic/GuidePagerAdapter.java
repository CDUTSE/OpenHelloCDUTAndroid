package com.emptypointer.hellocdut.ui.basic;

/**
 * Created by Sequarius on 2015/11/12.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.emptypointer.hellocdut.R;

public class GuidePagerAdapter extends FragmentPagerAdapter {
    protected static final int[] ICONS = new int[]{
            R.drawable.rc_guide_image_1, R.drawable.rc_guide_image_2,
            R.drawable.rc_guide_image_3, R.layout.fragment_drawable_with_bottom_button,

    };

    public GuidePagerAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int arg0) {
        // TODO Auto-generated method stub
        return ImageFragment.newInstance(ICONS[arg0]);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return ICONS.length;
    }

}