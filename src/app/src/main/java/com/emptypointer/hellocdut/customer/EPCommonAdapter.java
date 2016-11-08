package com.emptypointer.hellocdut.customer;

/**
 * Created by Sequarius on 2015/10/28.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Sequarius on 2015/9/11.
 */
public abstract class EPCommonAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected int mItemLayoutId;

    public EPCommonAdapter(Context context, int itemLayoutId, List<T> datas) {
        this.mContext = context;
        this.mDatas = datas;
        this.mItemLayoutId = itemLayoutId;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EPCommonViewHolder holder = EPCommonViewHolder.getHolder(mContext, convertView, parent, mItemLayoutId, position);
        convert(holder, getItem(position));
        return holder.getConvertView();
    }

    public abstract void convert(EPCommonViewHolder holder, T item);

    private EPCommonViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
        return EPCommonViewHolder.getHolder(mContext, convertView, parent, mItemLayoutId,
                position);
    }
}

