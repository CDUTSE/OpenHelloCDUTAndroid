package com.emptypointer.hellocdut.ui.basic;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;

public class SimpleBrowserActivity extends EPActivity {

    private static final String TAG = "SimpleBrowserActivity";
    private WebView webView;
    private android.support.v4.widget.SwipeRefreshLayout swipeContainer;
    public static String INTENT_TITTLE="title";
    public static String INTENT_URL="url";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_browser);
        this.swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        this.webView = (WebView) findViewById(R.id.webView);
        TypedArray array=getResources().obtainTypedArray(R.array.progress_colors_light);
        int length=array.length();
        int[] resID=new int[length];
        for(int i=0;i<array.length();i++){
            resID[i]=array.getResourceId(i,0);
        }
        array.recycle();
        swipeContainer.setColorSchemeResources(resID);
        swipeContainer.setEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);

        swipeContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
            }
        },500);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.i(TAG, "progress==" + newProgress);
                if (newProgress == 100) {
                    swipeContainer.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeContainer.setRefreshing(false);
                        }
                    }, 500);
                }
            }
        });
        getSupportActionBar().setTitle(getIntent().getStringExtra(INTENT_TITTLE));
        webView.loadUrl(getIntent().getStringExtra(INTENT_URL));
    }
}
