package com.emptypointer.hellocdut.customer;

/**
 * Created by Sequarius on 2015/11/3.
 */
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * 适用于RecyclerView的ViewHolder
 * Created by mrsimple on 25/9/15.
 */
public class EPRecyclerViewHolder extends RecyclerView.ViewHolder {
    /**
     * ViewHolder实现类,桥接模式适配AbsListView与RecyclerView的二维变化
     */
    EPViewHolderImpl mHolderImpl ;

    /**
     *
     * @param itemView
     */
    public EPRecyclerViewHolder(View itemView) {
        super(itemView);
        mHolderImpl = new EPViewHolderImpl( itemView ) ;
    }

    public Context getContext() {
        return  mHolderImpl.mItemView.getContext() ;
    }

    /**
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T findViewById(int viewId) {
        return mHolderImpl.findViewById(viewId);
    }

    public View getItemView() {
        return mHolderImpl.getItemView();
    }

    public EPRecyclerViewHolder setText(int viewId, int stringId) {
        mHolderImpl.setText(viewId, stringId);
        return this;
    }

    public EPRecyclerViewHolder setText(int viewId, String text) {
        mHolderImpl.setText(viewId, text);
        return this;
    }

    public EPRecyclerViewHolder setTextColor(int viewId, int color) {
        mHolderImpl.setTextColor(viewId, color);
        return this;
    }

    public EPRecyclerViewHolder setCardViewBackgroud(int viewID,int color){
        mHolderImpl.setCardViewBaground(viewID, color);
        return this;
    }

    public EPRecyclerViewHolder setOnclickListenner(int viewID,View.OnClickListener onClickListener){
        mHolderImpl.setOnclickListenner(viewID, onClickListener);
        return this;
    }

    /**
     * @param viewId
     * @param color
     */
    public EPRecyclerViewHolder setBackgroundColor(int viewId, int color) {
        mHolderImpl.setBackgroundColor(viewId, color);
        return this;
    }
    public EPRecyclerViewHolder setViewEnable(int viewId, boolean enable) {
        mHolderImpl.setViewEnable(viewId, enable);
        return this;
    }


    /**
     * @param viewId
     * @param resId
     */
    public EPRecyclerViewHolder setBackgroundResource(int viewId, int resId) {
        mHolderImpl.setBackgroundResource(viewId, resId);
        return this;
    }


    /**
     * @param viewId
     * @param drawable
     */
    public EPRecyclerViewHolder setBackgroundDrawable(int viewId, Drawable drawable) {
        mHolderImpl.setBackgroundDrawable(viewId, drawable);
        return this;
    }

    /**
     * @param viewId
     * @param drawable
     */
    @TargetApi(16)
    public EPRecyclerViewHolder setBackground(int viewId, Drawable drawable) {
        mHolderImpl.setBackground(viewId, drawable);
        return this;
    }


    public EPRecyclerViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        mHolderImpl.setImageBitmap(viewId, bitmap);
        return this;
    }


    public EPRecyclerViewHolder setImageResource(int viewId, int resId) {
        mHolderImpl.setImageResource(viewId, resId);
        return this;
    }

    public EPRecyclerViewHolder setImageDrawable(int viewId, Drawable drawable) {
        mHolderImpl.setImageDrawable(viewId, drawable);
        return this;
    }


    public EPRecyclerViewHolder setImageDrawable(int viewId, Uri uri) {
        mHolderImpl.setImageDrawable(viewId, uri);
        return this;
    }


    @TargetApi(16)
    public EPRecyclerViewHolder setImageAlpha(int viewId, int alpha) {
        mHolderImpl.setImageAlpha(viewId, alpha);
        return this;
    }

    public EPRecyclerViewHolder setChecked(int viewId, boolean checked) {
        mHolderImpl.setChecked(viewId, checked);
        return this;
    }


    public EPRecyclerViewHolder setProgress(int viewId, int progress) {
        mHolderImpl.setProgress(viewId, progress);
        return this;
    }

    public EPRecyclerViewHolder setProgress(int viewId, int progress, int max) {
        mHolderImpl.setProgress(viewId, progress, max);
        return this;
    }

    public EPRecyclerViewHolder setMax(int viewId, int max) {
        mHolderImpl.setMax(viewId, max);
        return this;
    }

    public EPRecyclerViewHolder setRating(int viewId, float rating) {
        mHolderImpl.setRating(viewId, rating);
        return this;
    }


    public EPRecyclerViewHolder setRating(int viewId, float rating, int max) {
        mHolderImpl.setRating(viewId, rating, max);
        return this;
    }

    public EPRecyclerViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        mHolderImpl.setOnClickListener(viewId, listener);
        return this;
    }

    public EPRecyclerViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        mHolderImpl.setOnTouchListener(viewId, listener);
        return this;
    }


    public EPRecyclerViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        mHolderImpl.setOnLongClickListener(viewId, listener);
        return this;
    }

    public EPRecyclerViewHolder setOnItemClickListener(int viewId, AdapterView.OnItemClickListener listener) {
        mHolderImpl.setOnItemClickListener(viewId, listener);
        return this;
    }


    public EPRecyclerViewHolder setOnItemLongClickListener(int viewId, AdapterView.OnItemLongClickListener listener) {
        mHolderImpl.setOnItemLongClickListener(viewId, listener);
        return this;
    }


    public EPRecyclerViewHolder setOnItemSelectedClickListener(int viewId, AdapterView.OnItemSelectedListener listener) {
        mHolderImpl.setOnItemSelectedClickListener(viewId, listener);
        return this;
    }

    public EPRecyclerViewHolder setImageURL(int viewId,String url) {
        mHolderImpl.setImageURL(viewId, url);
        return this;
    }

    public EPRecyclerViewHolder setViewVisibility(int viewId,int visiable) {
        mHolderImpl.setViewVisibility(viewId, visiable);
        return this;
    }



    public EPRecyclerViewHolder setClickable(int viewId, boolean clickable) {
        mHolderImpl.setClickable(viewId, clickable);
        return this;
    }

    public EPRecyclerViewHolder setTextSize(int viewId, float size) {
        mHolderImpl.setTextSize(viewId,size);
        return this;
    }
}
