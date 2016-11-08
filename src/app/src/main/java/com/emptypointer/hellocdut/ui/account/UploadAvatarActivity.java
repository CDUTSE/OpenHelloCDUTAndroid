package com.emptypointer.hellocdut.ui.account;

import android.Manifest;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.customer.EPProgressDialog;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.rey.material.app.Dialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadAvatarActivity extends EPActivity implements
        OnClickListener {
    private static final int AFTER_CROP_SIZE = 200;
    private int mMode = MODE_UNDEFINE;
    private static final int MODE_GALLERY = 0;
    private static final int MODE_CAMERA = 1;
    private static final int MODE_AA0 = 2;
    private static final int MODE_CUT = 3;
    private static final int MODE_UNDEFINE = -1;
    private static final String PHOTO_FILE_NAME = "temp_file";
    public static final String TAG = "UploadAvatarActivity";
    private File tempFile;
    private Bitmap bitmap;
    private ImageView mImageView;
    private Dialog mDialog;

    private Button mBtnCamera, mBtnGallery, mBtnAAO, mBtnReSelect, mBtnSubmit,
            mBtnReturn;
    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        setContentView(R.layout.activity_upload_avatar);
        mImageView = (ImageView) findViewById(R.id.imageView_avatar);
        mBtnReSelect = (Button) findViewById(R.id.button_cancel);
        mBtnSubmit = (Button) findViewById(R.id.button_confirm);
        mLayout = (LinearLayout) findViewById(R.id.layout_result);

        mBtnReSelect.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        if (mMode == MODE_UNDEFINE) {
            createModeDialog();
            mLayout.setVisibility(View.GONE);
        }
    }

    private void createModeDialog() {
        Builder builder = new Builder(this);
        builder.setTitle(R.string.str_chose_method_get_photo);
        View contentView = View.inflate(this,
                R.layout.dialog_choose_avatar_origin, null);
        mBtnCamera = (Button) contentView.findViewById(R.id.button_camera);
        mBtnGallery = (Button) contentView
                .findViewById(R.id.button_from_gallery);
        mBtnAAO = (Button) contentView.findViewById(R.id.button_from_aao);
        mBtnReturn = (Button) contentView.findViewById(R.id.button_return);

        mBtnCamera.setOnClickListener(this);
        mBtnGallery.setOnClickListener(this);
        mBtnAAO.setOnClickListener(this);
        mBtnReturn.setOnClickListener(this);
        mDialog = new Dialog(this);
        mDialog.setTitle(R.string.str_chose_method_get_photo);
        mDialog.setContentView(contentView);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.show();

//        builder.setCancelable(false);
//        builder.setView(contentView);
//        mDialog = builder.show();
//        CommonUtils.dialogTitleLineColor(mDialog);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        tempFile = new File(Environment.getExternalStorageDirectory(),
                PHOTO_FILE_NAME);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        switch (v.getId()) {
            case R.id.button_confirm:
                mMode = MODE_UNDEFINE;
                getUploadToken();
                break;
            case R.id.button_cancel:
                mLayout.setVisibility(View.GONE);
                mMode = MODE_UNDEFINE;
                mDialog.show();
                break;
            case R.id.button_camera:
                Dexter.checkPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response1) {
                        getImageFromCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        UIUtils.makeSnake(UploadAvatarActivity.this, getString(R.string.str_on_denied));
                        return;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }, Manifest.permission.CAMERA);

                mMode = MODE_CAMERA;
                break;
            case R.id.button_from_gallery:
                getImageFromgallery();
                mMode = MODE_GALLERY;
                break;
            case R.id.button_from_aao:
                mMode = MODE_AA0;

                getFromAAO();
                break;
            case R.id.button_return:
                mDialog.dismiss();
                this.finish();
                break;

            default:
                break;
        }

    }

    /*
     * 从相册获取
     */
    public void getImageFromgallery() {
        // 激活系统图库，选择一张图片

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, MODE_GALLERY);
    }

    /*
     * 从相机获取
     */
    public void getImageFromCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("camerasensortype", 2); // 调用前置摄像头
        // 判断存储卡是否可以用，可用进行存储
        if (CommonUtils.isExitsSdcard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        startActivityForResult(intent, MODE_CAMERA);
    }

    /**
     * 从教务系统获取
     */
    public void getFromAAO() {
        if (UserInfo.getInstance(this).getUserStatus() > UserInfo.USER_STATUS_NORMAL) {

            getAAOImage();
        } else {
            UIUtils.createTObindDiglog(getString(R.string.str_aao), this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MODE_GALLERY) {
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == MODE_CAMERA) {
            if (CommonUtils.isExitsSdcard()) {
                tempFile = new File(Environment.getExternalStorageDirectory(),
                        PHOTO_FILE_NAME);
                if (tempFile.exists()) {
                    crop(Uri.fromFile(tempFile));
                }
            } else {
                UIUtils.makeSnake(this, "未找到存储卡，无法存储照片！", true);
            }

        } else if (requestCode == MODE_CUT) {
            try {
                if (data != null) {
                    mLayout.setVisibility(View.VISIBLE);
                    bitmap = data.getParcelableExtra("data");
                    mImageView.setImageBitmap(bitmap);
                    mDialog.dismiss();
                    if (tempFile != null) {
                        boolean delete = tempFile.delete();
                        System.out.println("delete = " + delete);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 剪切图片
     */
    private void crop(Uri uri) {
        // 裁剪图片意图

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", AFTER_CROP_SIZE);
        intent.putExtra("outputY", AFTER_CROP_SIZE);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        startActivityForResult(intent, MODE_CUT);
    }


    private void getAAOImage() {
        final EPProgressDialog dialog = new EPProgressDialog();
        dialog.show(getSupportFragmentManager(), null);
        String url = "http://202.115.133.172/public/p/zxs/new/";
        String _str = UserInfo.getInstance(UploadAvatarActivity.this)
                .getStudentID();
        if (!_str.matches("\\d{12}")) {
            UIUtils.makeSnake(this, getString(R.string.message_erro_stu_id_missmatch));
            dialog.dismiss();
            return;
        }
        String _regex = "\\d{6}";
        Pattern _p = Pattern.compile(_regex);
        Matcher _m = _p.matcher(_str);
        _m.find();
        String _add = _m.group();
        url = url + _add + "/" + _str + ".jpg";
        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                tempFile = new File(Environment.getExternalStorageDirectory(),
                        PHOTO_FILE_NAME);
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(tempFile);
                    response.compress(Bitmap.CompressFormat.PNG, 95,
                            outputStream);
                    outputStream.flush();
                    crop(Uri.fromFile(tempFile));
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    UIUtils.makeSnake(UploadAvatarActivity.this, getString(R.string.message_erro_stu_id_missmatch));
                    e.printStackTrace();
                } catch (IOException e) {
                    UIUtils.makeSnake(UploadAvatarActivity.this, getString(R.string.message_erro_stu_id_missmatch));
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            UIUtils.makeSnake(UploadAvatarActivity.this, getString(R.string.message_erro_stu_id_missmatch));
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, AFTER_CROP_SIZE * 2, AFTER_CROP_SIZE * 2, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                UIUtils.makeSnake(UploadAvatarActivity.this, getString(R.string.message_weak_internet));
                dialog.dismiss();
            }
        });

        request.setTag(this);
        VolleyUtil.getQueue(this).add(request);
    }

//    private class getAAOImageTask extends AsyncTask<Void, Void, Boolean> {
//        private ProgressDialog mDialog;
//        private Bitmap mBitmap;
//
//        @Override
//        protected void onPreExecute() {
//            // TODO Auto-generated method stub
//            mDialog = new ProgressDialog(UploadAvatarActivity.this);
//            mDialog.setCanceledOnTouchOutside(false);
//
//            mDialog.setMessage(getString(R.string.str_load_aao_image));
//            mDialog.show();
//
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            // TODO Auto-generated method stub
//            mDialog.dismiss();
//            if (result) {
//
//                crop(Uri.fromFile(tempFile));
//            } else {
//                CommonUtils.showCustomToast(Toast.makeText(UploadAvatarActivity.this,
//                        getString(R.string.message_weak_internet),
//                        Toast.LENGTH_LONG));
//            }
//
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            // TODO Auto-generated method stub
//            /**
//             * 网络过程
//             */
//            String url = "http://202.115.133.172/public/p/zxs/new/";
//            String _str = UserInfo.getInstance(UploadAvatarActivity.this)
//                    .getStudentID();
//            if (!_str.matches("\\d{12}")) {
//                return false;
//            } else {
//                String _regex = "\\d{6}";
//                Pattern _p = Pattern.compile(_regex);
//                Matcher _m = _p.matcher(_str);
//                _m.find();
//                String _add = _m.group();
//                url = url + _add + "/" + _str + ".jpg";
//                try {
//                    mBitmap = EPHttpService.customerGetBitmap(url, null);
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    return false;
//                }
//                /**
//                 * 存储过程
//                 */
//                tempFile = new File(Environment.getExternalStorageDirectory(),
//                        PHOTO_FILE_NAME);
//                FileOutputStream outputStream = null;
//                try {
//                    outputStream = new FileOutputStream(tempFile);
//                    mBitmap.compress(Bitmap.CompressFormat.PNG, 95,
//                            outputStream);
//                    outputStream.flush();
//
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    return false;
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    return false;
//                } finally {
//                    if (outputStream != null) {
//                        try {
//                            outputStream.close();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                            return false;
//                        }
//                    }
//                }
//                return true;
//            }
//        }
//    }

    private void getUploadToken() {
        final EPProgressDialog dialog = new EPProgressDialog();
        dialog.show(getSupportFragmentManager(), null);
        Map<String, String> params = new HashMap<>();
        params.put("action", "getUploadToken");
        params.put("user_name", EPSecretService
                .encryptByPublic(UserInfo.getInstance(this).getUserName()));
        params.put("user_login_token",
                EPSecretService.encryptByPublic(UserInfo.getInstance(this)
                        .getToken()));
        EPJsonObjectRequest request = new EPJsonObjectRequest(this, params, new EPJsonResponseListener(this) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (!result) {
                    return;
                }
                try {
                    String token = response.getString("uploadToken");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UUID uuid = UUID.randomUUID();
                    StringBuilder sb = new StringBuilder();
                    sb.append(UserInfo.getInstance(UploadAvatarActivity.this).getUserName());
                    sb.append('-');
                    sb.append(uuid.toString());
                    // sb.append(".png");
                    String key = sb.toString();
                    final UploadManager uploadManager = new UploadManager();
                    uploadManager.put(data, key, token, new UpCompletionHandler() {

                        @Override
                        public void complete(final String key, ResponseInfo info,
                                             org.json.JSONObject arg2) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    dialog.dismiss();
                                }
                            });
                            if (info.isOK()) {
                                UIUtils.makeSnake(UploadAvatarActivity.this, "上传头像成功", true);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        UserInfo.getInstance(UploadAvatarActivity.this).setImageURL(Constant.STATIC_HOST + key);
                                        UploadAvatarActivity.this.finish();
                                    }
                                }, Constant.DELAY_ACTIVITY_FINISH);
                            } else {
                                UIUtils.makeSnake(UploadAvatarActivity.this, R.string.message_weak_internet, true);
                            }
                        }
                    }, new UploadOptions(null, null, false,
                            new UpProgressHandler() {
                                public void progress(String key, double percent) {
//                                publishProgress(percent);
                                }
                            }, null));

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    UIUtils.makeSnake(UploadAvatarActivity.this, R.string.message_weak_internet, true);
                    e.printStackTrace();
                }
            }
        });
        request.setTag(this);
        VolleyUtil.getQueue(this).add(request);
    }

    @Override
    protected void onResume() {
        VolleyUtil.getQueue(this).cancelAll(this);
        super.onResume();
    }

    //    private void uploadAvatar() {
//        new getUploadTokenTask().execute();
//    }

//    private class getUploadTokenTask extends AsyncTask<Void, Double, Boolean> {
//        private ProgressDialog mDialog;
//        private File mSavePath;
//
//        @Override
//        protected Boolean doInBackground(Void... p) {
//            // TODO Auto-generated method stub
//
//            try {
//                String str = EPHttpService.customerPostString(
//                        GlobalVariables.SERVICE_HOST_FILE_SYSTEM, params);
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        mDialog.setMessage(getString(R.string.str_on_upload));
//                    }
//                });
//                JSONObject jsonObject = JSONObject.parseObject(str);
//                String token = jsonObject.getString("uploadToken");
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                byte[] data = baos.toByteArray();
//                UUID uuid = UUID.randomUUID();
//                StringBuilder sb = new StringBuilder();
//                sb.append(EPApplication.getInstance().getUserName());
//                sb.append('-');
//                sb.append(uuid.toString());
//                // sb.append(".png");
//                String key = sb.toString();
//                UploadManager uploadManager = new UploadManager();
//                uploadManager.put(data, key, token, new UpCompletionHandler() {
//
//                    @Override
//                    public void complete(String key, ResponseInfo info,
//                                         org.json.JSONObject arg2) {
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                mDialog.dismiss();
//                            }
//                        });
//                        if (info.isOK()) {
//                            new SavaAvatarThead().start();
//                        } else {
//                            CommonUtils.showCustomToast(Toast.makeText(UploadAvatarActivity.this,
//                                    getString(R.string.message_weak_internet),
//                                    Toast.LENGTH_LONG));
//                        }
//                    }
//                }, new UploadOptions(null, null, false,
//                        new UpProgressHandler() {
//                            public void progress(String key, double percent) {
//                                publishProgress(percent);
//                            }
//                        }, null));
//                return true;
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//            mSavePath = getDir("user", MODE_PRIVATE);
//            mDialog = new ProgressDialog(UploadAvatarActivity.this);
//            mDialog.setTitle(getString(R.string.str_upload_avatar));
//            mDialog.setMessage(getString(R.string.str_on_execute_upload));
//            mDialog.setProgress(0);
//            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            mDialog.setCancelable(false);
//            mDialog.show();
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//
//        }
//
//        @Override
//        protected void onProgressUpdate(Double... values) {
//            // TODO Auto-generated method stub
//            double progress = values[0] * 100;
//            mDialog.setProgress((int) progress);
//        }
//
//        private class SavaAvatarThead extends Thread {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                File file = new File(mSavePath, EPApplication.getInstance()
//                        .getUserName());
//                FileOutputStream outputStream = null;
//                try {
//                    outputStream = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100,
//                            outputStream);
//                    outputStream.flush();
//
//                } catch (FileNotFoundException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                } finally {
//                    if (outputStream != null) {
//                        try {
//                            outputStream.close();
//                        } catch (IOException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                runOnUiThread(new Runnable() {
//                    public void run() {
//
//                        CommonUtils.showCustomToast(Toast.makeText(UploadAvatarActivity.this,
//                                        getString(R.string.message_complete_avatar_upload), Toast.LENGTH_SHORT)
//                        );
//                        UploadAvatarActivity.this.finish();
//                    }
//                });
//            }
//
//        }
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            this.finish();
//        }
//        return super.onKeyDown(keyCode, event);
//
//    }

}
