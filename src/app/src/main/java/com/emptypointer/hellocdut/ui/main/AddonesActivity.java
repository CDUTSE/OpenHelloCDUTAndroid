package com.emptypointer.hellocdut.ui.main;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.customer.EPRecyclerAdapter;
import com.emptypointer.hellocdut.customer.EPRecyclerViewHolder;
import com.emptypointer.hellocdut.model.Addone;
import com.emptypointer.hellocdut.model.DataCache;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.CacheService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.ui.basic.RefreshableActivity;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.emptypointer.hellocdut.utils.VolleyUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddonesActivity extends RefreshableActivity<Addone> {

    private static final String DOWNLOAD_FOLDER_NAME = "hello_cdut/addones";
    private static final String TAG = "AddonesActivity";
    private List<String> mPackageNames;


    private static final String CACHE_ADDONES = "addones";


    private CacheService mCacheService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new AddonesAdapter(R.layout.row_addones, mDataSet);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromServer();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mCacheService = CacheService.getInstance(this);
        DataCache cache=mCacheService.getDataCache(CACHE_ADDONES);
        if(cache!=null){
            jsonParse(cache.getDataJson());
        }else{
            setRefreshing(true);
            loadDataFromServer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new AddonesAdapter(R.layout.row_addones, mDataSet);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private class AddonesAdapter extends EPRecyclerAdapter<Addone> {

        private List<PackageInfo> mApps;

        public AddonesAdapter(int layoutId, List<Addone> datas) {
            super(layoutId, datas);
            mApps = new ArrayList<>();
            PackageManager pManager = getPackageManager();
            mApps = pManager.getInstalledPackages(0);
            mPackageNames = new ArrayList<>();
            for (PackageInfo info : mApps) {
                String packageName = info.packageName;
                mPackageNames.add(packageName);
            }
        }

        @Override
        protected void onBindData(EPRecyclerViewHolder viewHolder, int position, final Addone addone) {

            viewHolder.setText(R.id.textView_name, addone.getName());
            viewHolder.setText(R.id.textView_author, getString(R.string.str_format_author,
                    addone.getAuthor()));
            String updateTime = addone.getUpdateTime();
            viewHolder.setText(R.id.textView_update_time, getString(R.string.str_format_publish_time,
                    updateTime.substring(0, 10)));
            viewHolder.setText(R.id.textView_version, getString(R.string.str_format_adone_version,
                    addone.getVersion()));
            viewHolder.setText(R.id.textView_introduction, getString(R.string.str_format_introduction,
                    addone.getIntroduction()));
            viewHolder.setImageURL(R.id.imageView_avatar, addone.getIcUrl());

            if (hasInstall(addone.getPackageName())) {
                viewHolder.setClickable(R.id.layout_list_item, true);
                viewHolder.setOnclickListenner(R.id.layout_list_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "clicked action==" + addone.getAction());
                        Intent intent = new Intent(addone.getAction());
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                    }
                });
                viewHolder.setText(R.id.button_function, getString(R.string.uninstall));
                viewHolder.setViewEnable(R.id.layout_list_item, true);
                viewHolder.setTextColor(R.id.button_function, getResources().getColor(R.color.color_ep_red));
                viewHolder.setOnclickListenner(R.id.button_function, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Uri packageURI = Uri.parse("package:"
                                    + addone.getPackageName());

                            Intent uninstallIntent = new Intent(
                                    Intent.ACTION_DELETE, packageURI);
                            startActivity(uninstallIntent);
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                viewHolder.setClickable(R.id.layout_list_item, false);
                viewHolder.setViewEnable(R.id.layout_list_item, false);
                viewHolder.setText(R.id.button_function, getString(R.string.install));
                viewHolder.setTextColor(R.id.button_function, getResources().getColor(R.color.colorPrimary));
                viewHolder.setOnclickListenner(R.id.button_function, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isExitsSdcard()) {
                            // TODO Auto-generated method stub
                            downloadApp(addone.getAddoneUrl(), addone.getVersion(),
                                    addone.getName());
                            UIUtils.makeSnake(mFab,getString(R.string.message_start_download_addones));
                        } else {
                            UIUtils.makeSnake(mFab, getString(R.string.message_wrong_unexsisted_sd_card)
                                    , true);
                        }
                    }
                });
            }
        }

    }

    private void loadDataFromServer() {
        Map<String, String> params = new HashMap<>();
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(this).getUserName()));
        params.put("user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(this).getToken()));
        params.put("action", "queryAddons");
        EPJsonObjectRequest request = new EPJsonObjectRequest(this, params, false, new EPJsonResponseListener(this) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (!result) {
                    return;
                }
                jsonParse(response);
                mCacheService.setDataCache(CACHE_ADDONES, response.toJSONString());
                UIUtils.makeSnake(mFab, getString(R.string.message_get_json_sucess), false);
            }
        }) {
            @Override
            protected void onFinish() {
                refreshingComplete();
                super.onFinish();
            }
        };
        request.setTag(this);
        VolleyUtil.getQueue(this).add(request);
    }

    @Override
    protected void onDestroy() {
        VolleyUtil.getQueue(this).cancelAll(this);
        super.onDestroy();
    }

    private void downloadApp(String url, String version, String fileName) {
        // uri 是你的下载地址，可以使用Uri.parse("http://")包装成Uri对象
        if (!CommonUtils.isExitsSdcard()) {
            Intent intentByBrowser = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url));
            startActivity(intentByBrowser);
            return;
        }
        DownloadManager.Request req = new DownloadManager.Request(
                Uri.parse(url));

        // 通过setAllowedNetworkTypes方法可以设置允许在何种网络下下载，
        // 也可以使用setAllowedOverRoaming方法，它更加灵活
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE);

        // 此方法表示在下载过程中通知栏会一直显示该下载，在下载完成后仍然会显示，
        // 直到用户点击该通知或者消除该通知。还有其他参数可供选择
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // 设置下载文件存放的路径，同样你可以选择以下方法存放在你想要的位置。
        // setDestinationUri
        // setDestinationInExternalPublicDir

        File folder = Environment
                .getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
        // 先删除
        if (folder.exists()) {
            folder.delete();
        }

        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        req.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, fileName
                + ".apk");
        // req.setDestinationInExternalPublicDir(mContext, DOWNLOAD_FOLDER_NAME,
        // mContext.getString(R.string.str_app_file_name, version));
        // 再通知欄顯示
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // req.setShowRunningNotification(true);
        // 设置一些基本显示信息
        req.setTitle(getString(R.string.str_on_dowanload_addone,
                fileName));
        req.setDescription(getString(R.string.str_updating_compelet,
                version));
        req.setMimeType("application/vnd.android.package-archive");

        // Ok go!
        DownloadManager dm = (DownloadManager)
                getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = dm.enqueue(req);
        SharedPreferences preferences = getSharedPreferences(
                "setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("download_id", downloadId);
        editor.commit();
    }


    private boolean hasInstall(String packageName) {
        return mPackageNames.contains(packageName);
    }

    private void jsonParse(JSONObject obj) {
        JSONArray array = obj.getJSONArray("addon_list");
        mDataSet.clear();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                String packageName = object.getString("package");
                String action = object.getString("action");
                String author = object.getString("author");
                String updateTime = object.getString("update_time");
                String version = object.getString("version");
                String dowloadCount = object.getString("download_count");
                String icUrl = object.getString("ic_url");
                String addoneUrl = object.getString("addon_url");
                String introduction = object.getString("introduction");
                mDataSet.add(new Addone(name, packageName, action, author,
                        updateTime, version, dowloadCount, icUrl, addoneUrl,
                        introduction));
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
