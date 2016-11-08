package com.emptypointer.hellocdut.ui.basic;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;

import com.emptypointer.hellocdut.R;

public class GuideActivity extends FragmentActivity {

    private ViewPager mPager;
    private GuidePagerAdapter mAdapter;

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //
        int currentPage = mPager.getCurrentItem();
        if (currentPage < mAdapter.getCount() - 1) {
            mPager.setCurrentItem(currentPage + 1);
        } else {
            this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        setContentView(R.layout.activity_guide);

        mPager = (ViewPager) findViewById(R.id.viewpager_guide);
        mAdapter = new GuidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setPageTransformer(true, new DepthPageTransformer());
    }

    /**
     * viewpager切换动画
     *
     * @author Sequarius
     */
    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
                        * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}