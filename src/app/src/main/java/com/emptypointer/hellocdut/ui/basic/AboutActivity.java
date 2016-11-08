package com.emptypointer.hellocdut.ui.basic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.customer.EPProgressDialog;
import com.emptypointer.hellocdut.service.CacheService;
import com.emptypointer.hellocdut.service.EPUpdateService;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.DialogButtonClickListner;
import com.emptypointer.hellocdut.utils.UIUtils;

public class AboutActivity extends EPActivity implements View.OnClickListener {

    private android.widget.RelativeLayout layoutabout;
    private android.widget.RelativeLayout layoutagreement;
    private android.widget.RelativeLayout layoutevaluate;
    private android.widget.RelativeLayout layoutcleancache;
    private android.widget.RelativeLayout layoutupdate;
    private TextView TextViewversion;
    private RelativeLayout layoutGuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.layoutGuid = (RelativeLayout) findViewById(R.id.layoutGuid);
        this.TextViewversion = (TextView) findViewById(R.id.TextView_version);
        this.layoutupdate = (RelativeLayout) findViewById(R.id.layout_update);
        this.layoutcleancache = (RelativeLayout) findViewById(R.id.layout_clean_cache);
        this.layoutevaluate = (RelativeLayout) findViewById(R.id.layout_evaluate);
        this.layoutagreement = (RelativeLayout) findViewById(R.id.layout_agreement);
        this.layoutabout = (RelativeLayout) findViewById(R.id.layout_about);
        this.layoutupdate.setOnClickListener(this);
        this.layoutcleancache.setOnClickListener(this);
        this.layoutevaluate.setOnClickListener(this);
        this.layoutagreement.setOnClickListener(this);
        this.layoutabout.setOnClickListener(this);
        layoutGuid.setOnClickListener(this);

        ((TextView) findViewById(R.id.TextView_version))
                .setText(getString(R.string.str_format_version,
                        EPUpdateService.getAppVersion(this)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_update:
                EPUpdateService.getInstance(this).cheakVersionFront();
                break;
            case R.id.layout_clean_cache:
                cleanCache();
                break;
            case R.id.layout_evaluate:
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intentEvaluate = new Intent(Intent.ACTION_VIEW, uri);
                intentEvaluate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentEvaluate);
                break;
            case R.id.layout_agreement:
                Intent intent = new Intent(this, SimpleBrowserActivity.class);
                intent.putExtra(SimpleBrowserActivity.INTENT_TITTLE, getString(R.string.str_user_agreement));
                intent.putExtra(SimpleBrowserActivity.INTENT_URL, Constant.SERVICE_HOST_AGREEMENT);
                startActivity(intent);
                break;
            case R.id.layout_about:
                Intent intentAbout = new Intent(this, SimpleBrowserActivity.class);
                intentAbout.putExtra(SimpleBrowserActivity.INTENT_TITTLE, getString(R.string.str_about));
                intentAbout.putExtra(SimpleBrowserActivity.INTENT_URL, Constant.SERVICE_HOST_ABOUT);
                startActivity(intentAbout);
                break;
            case R.id.layoutGuid:
                startActivity(new Intent(this,GuideActivity.class));
                break;
            default:
                break;
        }
    }

    private void cleanCache() {
        UIUtils.showCommonDialog(this, getString(R.string.ask_clean_cache), getString(R.string.message_clean_cache), new DialogButtonClickListner() {
            @Override
            public void onclick(int index) {
                CacheService.getInstance(AboutActivity.this).cleanCache();
                getSharedPreferences(Constant.SHARED_PERFERENCR_CATCH, MODE_PRIVATE).edit().clear();
                final EPProgressDialog dialog = UIUtils.showProgressDialog(AboutActivity.this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        UIUtils.makeSnake(AboutActivity.this, getString(R.string.message_clean_cache_compelete));
                    }
                }, 500);
            }
        }, null, "立即清理", null);
    }
}
