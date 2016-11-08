package com.emptypointer.hellocdut.utils;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPProgressDialog;
import com.emptypointer.hellocdut.ui.account.RelativeAccountActivity;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

/**
 * Created by Sequarius on 2015/10/25.
 */
public class UIUtils {
    public static void makeSnake(Context context, String message, boolean longable) {
        View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        int time = longable ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT;
        Snackbar.make(rootView, message, time).show();
    }

    public static void makeSnake(View view, String message,boolean longable) {
        int time = longable ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT;
        Snackbar.make(view, message, time).show();
    }

    public static void makeSnake(View view,String message) {
        makeSnake(view, message, true);
    }
    public static void makeSnake(Context context, String message) {
        makeSnake(context, message, true);
    }

    public static void makeSnake(Context context, int resID, boolean longable) {
        makeSnake(context, context.getString(resID), longable);
    }

    public static void makeSnake(Context context, int resID) {
        makeSnake(context, resID, true);
    }

    public static Dialog.Builder showSingleChoiceDialog(final FragmentManager fragmentManager, String[] items, int defaulIndex, String title, final DialogButtonClickListner listner) {
        Dialog.Builder builder = new SimpleDialog.Builder() {

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
//                Toast.makeText(mActivity, "You have selected " + getSelectedValue() + " as phone ringtone.", Toast.LENGTH_SHORT).show();
//                super.onPositiveActionClicked(fragment);
                if (listner != null) {
                    listner.onclick(getSelectedIndex());
                }
                super.onPositiveActionClicked(fragment);
            }

//            @Override
//            public void onNegativeActionClicked(DialogFragment fragment) {
//                Toast.makeText(mActivity, "Cancelled" , Toast.LENGTH_SHORT).show();
//                super.onNegativeActionClicked(fragment);
//            }
        };
        ((SimpleDialog.Builder) builder).items(items, defaulIndex)
                .title(title)
                .positiveAction("确定")
                .negativeAction("取消");
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(fragmentManager, null);
        return builder;
    }

    public static Dialog.Builder showCommonDialog(Context context, String title, String message, final DialogButtonClickListner positiveListner, final DialogButtonClickListner negativeListener){
        return showCommonDialog(context,title,message,positiveListner,negativeListener,null,null);
    }

    public static Dialog.Builder showCommonDialog(Context context, String title, String message, final DialogButtonClickListner positiveListner, final DialogButtonClickListner negativeListener,String postiveLabel,String nageTiveLabel) {
        SimpleDialog.Builder builder = new SimpleDialog.Builder() {

            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                if (positiveListner != null) {
                    positiveListner.onclick(getSelectedIndex());
                }
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                if (negativeListener != null) {
                    negativeListener.onclick(getSelectedIndex());
                }
                super.onPositiveActionClicked(fragment);
            }
        };
        if(postiveLabel==null){
            postiveLabel="确定";
        }
        if(nageTiveLabel==null){
            nageTiveLabel="取消";
        }
        builder.title(title)
                .positiveAction(postiveLabel)
                .negativeAction(nageTiveLabel);
        if (!TextUtils.isEmpty(message)) {
            builder.message(message);
        }
        DialogFragment fragment = DialogFragment.newInstance(builder);
        try {
            FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.add(fragment, "update");
            transaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return builder;
    }

    public static Dialog.Builder showCommonDialog(Context context, int title, int message, final DialogButtonClickListner positiveListner, final DialogButtonClickListner negativeListener) {
        return showCommonDialog(context, context.getString(title), context.getString(message), positiveListner, negativeListener);
    }

    public static void createTObindDiglog(String BindType, final Context context) {
        SimpleDialog.Builder builder = new SimpleDialog.Builder() {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                context.startActivity(new Intent(context, RelativeAccountActivity.class));
            }
        };
        builder.title(context.getString(R.string.str_need_to_bind));
        builder.message(context.getString(R.string.message_need_to_bind, BindType));
        builder.negativeAction(context.getString(R.string.str_cancel)).positiveAction(context.getString(R.string.str_comfirm_bind));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(((FragmentActivity) context).getSupportFragmentManager(), null);

    }

    public static EPProgressDialog showProgressDialog(Context context) {

        EPProgressDialog dialog = new EPProgressDialog();
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), null);
        return dialog;
    }
}
