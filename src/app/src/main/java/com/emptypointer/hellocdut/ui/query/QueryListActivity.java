package com.emptypointer.hellocdut.ui.query;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.ui.basic.SimpleBrowserActivity;
import com.emptypointer.hellocdut.utils.UIUtils;

public class QueryListActivity extends EPActivity implements View.OnClickListener {


    private android.widget.ImageView imGrade;
    private android.widget.LinearLayout layoutquerygrade;
    private android.widget.ImageView imNationnalGrade;
    private android.widget.LinearLayout layoutquerynationalexam;
    private android.widget.ImageView imEmptyClassRoom;
    private android.widget.LinearLayout layoutqueryclassroom;
    private android.widget.ImageView imTel;
    private android.widget.LinearLayout layoutquerytel;
    private android.widget.ImageView imTeachingPlan;
    private android.widget.LinearLayout layoutqueryteachplan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_list);
        this.layoutqueryteachplan = (LinearLayout) findViewById(R.id.layout_query_teach_plan);
        this.imTeachingPlan = (ImageView) findViewById(R.id.imTeachingPlan);
        this.layoutquerytel = (LinearLayout) findViewById(R.id.layout_query_tel);
        this.imTel = (ImageView) findViewById(R.id.imTel);
        this.layoutqueryclassroom = (LinearLayout) findViewById(R.id.layout_query_class_room);
        this.imEmptyClassRoom = (ImageView) findViewById(R.id.imEmptyClassRoom);
        this.layoutquerynationalexam = (LinearLayout) findViewById(R.id.layout_query_national_exam);
        this.imNationnalGrade = (ImageView) findViewById(R.id.imNationnalGrade);
        this.layoutquerygrade = (LinearLayout) findViewById(R.id.layout_query_grade);
        this.imGrade = (ImageView) findViewById(R.id.imGrade);
//        imGrade.setColorFilter(getResources().getColor(R.color.colorPrimary));
//        imNationnalGrade.setColorFilter(getResources().getColor(R.color.colorPrimary));
//        imEmptyClassRoom.setColorFilter(getResources().getColor(R.color.colorPrimary));
//        imTel.setColorFilter(getResources().getColor(R.color.colorPrimary));
//        imTeachingPlan.setColorFilter(getResources().getColor(R.color.colorPrimary));
        layoutqueryclassroom.setOnClickListener(this);
        layoutquerygrade.setOnClickListener(this);
        layoutquerynationalexam.setOnClickListener(this);
        layoutquerytel.setOnClickListener(this);
        layoutqueryteachplan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.layout_query_grade:
                if (UserInfo.getInstance(this).getUserStatus() > UserInfo.USER_STATUS_NORMAL) {

                    intent.setClass(this,QueryGradeActivity.class);
                    startActivity(intent);
                } else {
                    UIUtils.createTObindDiglog(getString(R.string.str_aao), this);
                }
                break;
            case R.id.layout_query_national_exam:
                intent.setClass(this, SimpleBrowserActivity.class);
                intent.putExtra(SimpleBrowserActivity.INTENT_URL, "http://www.hellocdut.com/h5/");
                intent.putExtra(SimpleBrowserActivity.INTENT_TITTLE,getString(R.string.str_query_national_exam));
                startActivity(intent);
//                intent.setAction(GlobalVariables.ACTION_QUERY_NATIONAL_EXAM);
                break;
            case R.id.layout_query_class_room:
                if (UserInfo.getInstance(this).getUserStatus() > UserInfo.USER_STATUS_NORMAL) {
                    intent.setClass(this,QueryClassroomActivity.class);
                    startActivity(intent);
                } else {
                    UIUtils.createTObindDiglog(getString(R.string.str_aao), this);
                }

                break;
            case R.id.layout_query_tel:
                intent.setClass(this, QueryTelActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_query_teach_plan:
                intent.setClass(this,QueryTeachingPlanActivity.class);
                startActivity(intent);
                break;


            default:
                break;
        }
    }
}
