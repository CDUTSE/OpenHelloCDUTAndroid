package com.emptypointer.hellocdut.customer;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.utils.Constant;
import com.emptypointer.hellocdut.utils.UIUtils;


import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Sequarius on 2015/10/25.
 */
public class EPJsonObjectRequest extends Request<JSONObject> {
    private static final String TAG = "EPJsonObjectRequest";
    private Context mContext;

    private Map<String, String> mParams;

    private EPProgressDialog dialog;
    private boolean mShowDilog = true;
    private Response.Listener<JSONObject> mListener;

    public EPJsonObjectRequest(final Context context, Map<String, String> params, EPJsonResponseListener listener) {
        super(Method.POST, Constant.SERVICE_HOST, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIUtils.makeSnake(context, context.getString(R.string.message_weak_internet), true);
            }
        });
        UIStartProgress(context, params, listener);
    }

    private void UIStartProgress(Context context, Map<String, String> params, Response.Listener<JSONObject> listener) {
        mListener = listener;
        mContext = context;
        mParams = params;
        if (mShowDilog) {
            dialog = new EPProgressDialog();
//            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), null);
            FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.add(dialog, "loading");
            transaction.commitAllowingStateLoss();
        }
//        LinearLayout content=(LinearLayout) View.inflate(mContext,R.layout.layout_loading_dialog,null);
//                (LinearLayout) ((Activity)mContext).getLayoutInflater().inflate(R.layout.layout_dialog_love_status, null);
//        Dialog.Builder builder=.contentView(R.layout.layout_loading_dialog);
//        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), null);
    }

    public EPJsonObjectRequest(final Context context, Map<String, String> params, boolean showdialog, Response.Listener<JSONObject> listener) {
        super(Method.POST, Constant.SERVICE_HOST, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIUtils.makeSnake(context, context.getString(R.string.message_weak_internet) + error.getLocalizedMessage(), true);
            }
        });
        mShowDilog = showdialog;
        UIStartProgress(context, params, listener);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }


    //此处因为response返回值需要json数据,和JsonObjectRequest类一样即可
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(JSONObject.parseObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }

    @Override
    protected void onFinish() {
        if (mShowDilog && mContext != null) {
            dialog.dismissAllowingStateLoss();
        }
        super.onFinish();
    }

    public void setTimeOut(int timeOut) {
        this.setRetryPolicy(new DefaultRetryPolicy(
                timeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
