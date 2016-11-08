package com.emptypointer.hellocdut.ui.account;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;

public class BindActivity extends EPActivity {
    private int mBindCategory;
    public static final int MODE_AAO = 1;
    public static final int MODE_CAMPUS_CARD = 2;
    public static final int MODE_LIB = 3;
    public static final int MODE_MAIL = 4;
    public static final String INTETN_BINDE_MODE="bindmode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whith_container_of_fragment);
        Intent intent = getIntent();
        mBindCategory = intent.getIntExtra(INTETN_BINDE_MODE, -1);
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        switch (mBindCategory) {

            case -1:
                transaction.commit();
                this.finish();
                break;
            case MODE_AAO:

                transaction.replace(R.id.fragment_beach, BasicBindFragment.newInstance(BasicBindFragment.ACTION_BIND_AAO));
                transaction.commit();
                break;
            case MODE_CAMPUS_CARD:
                transaction.replace(R.id.fragment_beach, BindCampusCardFragment.newInstance());
                transaction.commit();
                break;
            case MODE_LIB:
                transaction.replace(R.id.fragment_beach,BasicBindFragment.newInstance(BasicBindFragment.ACTION_BIND_LIB));
                transaction.commit();
                break;
            case MODE_MAIL:
//                transaction.replace(R.id.fragment_beach, new BindMailFragment());
                transaction.commit();
                break;

            default:
                transaction.commit();
                this.finish();
                break;
        }
    }
}
