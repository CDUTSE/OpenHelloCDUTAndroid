package com.emptypointer.hellocdut.service;

/**
 * Created by Sequarius on 2015/11/12.
 */

import android.Manifest;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import com.alibaba.fastjson.JSONObject;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.DialogButtonClickListner;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class EPUpdateService {
    private static EPUpdateService instance;
    private Context mContext;
    private String DOWNLOAD_FOLDER_NAME = "hello_cdut/apk";

    private EPUpdateService(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public static EPUpdateService getInstance(Context context) {
        instance = new EPUpdateService(context);
        return instance;
    }

    /**
     * 前臺更新
     */
    public void cheakVersionFront() {

        loadDataFromServer(true);

    }

    /**
     * 後臺更新
     */
    public void cheakVersionBack() {

        loadDataFromServer(false);

    }

    private void loadDataFromServer(boolean showdialog) {

        Map<String, String> params = new HashMap<>();
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(mContext).getUserName()));
        params.put(
                "user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(mContext).getToken()));
        String appVersion = getAppVersion(mContext);
        params.put("version", appVersion);
        params.put("client", "0");
        params.put("action", "queryUpdate");
        params.put("flag", "1");
        EPJsonObjectRequest request = new EPJsonObjectRequest(mContext, params, showdialog, new EPJsonResponseListener(mContext) {
            @Override
            public void onResponse(final JSONObject response) {
                super.onResponse(response);
                if (result) {
                    Builder builder = new Builder(mContext);
                    StringBuffer sb = new StringBuffer();
                    sb.append(mContext.getString(R.string.str_update_detail))
                            .append("\n")
                            .append(response.getString("update_details"))
                            .append("\n")
                            .append(mContext.getString(R.string.str_update_time))
                            .append("\n")
                            .append(response.getString("update_time"));
                    UIUtils.showCommonDialog(mContext, mContext.getString(
                            R.string.str_format_has_new_version,
                            response.getString("version")), sb.toString(), new DialogButtonClickListner() {
                        @Override
                        public void onclick(int index) {
                            Dexter.checkPermission(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response1) {
                                    downloadApp(
                                            response.getString("client_url"),
                                            response.getString("version"));
                                    UIUtils.makeSnake(mContext, mContext.getString(R.string.str_on_start_updating));
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {
                                    UIUtils.makeSnake(mContext, mContext.getString(R.string.str_on_denied));

                                    return;
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                        }
                    }, null, mContext.getString(R.string.str_update_immediately), mContext.getString(R.string.str_update_delay));
                }
            }
        });
        request.setTag(mContext);
        VolleyUtil.getQueue(mContext).add(request);
    }

    public static String getAppVersion(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public void downloadApp(String url, String version) {
        // uri 是你的下载地址，可以使用Uri.parse("http://")包装成Uri对象
        if (!CommonUtils.isExitsSdcard()) {
            Intent intentByBrowser = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url));
            mContext.startActivity(intentByBrowser);
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

        req.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME,
                mContext.getString(R.string.str_app_file_name, version));
        // req.setDestinationInExternalPublicDir(mContext, DOWNLOAD_FOLDER_NAME,
        // mContext.getString(R.string.str_app_file_name, version));
        // 再通知欄顯示
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // req.setShowRunningNotification(true);
        // 设置一些基本显示信息
        req.setTitle(mContext.getString(R.string.str_format_app_name, version));
        req.setDescription(mContext.getString(R.string.str_updating_compelet,
                version));
        req.setMimeType("application/vnd.android.package-archive");

        // Ok go!
        DownloadManager dm = (DownloadManager) mContext
                .getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = dm.enqueue(req);
        SharedPreferences preferences = mContext.getSharedPreferences(
                Constant.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putLong("download_id", downloadId);
        editor.commit();
    }

}
