package com.emptypointer.hellocdut.customer;

/**
 * Created by Sequarius on 2015/10/28.
 */
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Sequarius on 2015/9/11.
 */
public class EPCommonViewHolder {
    /**
     * 一个储存ID和控件的映射关系的容器
     */
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;
    public EPCommonViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mViews = new SparseArray<View>();
        this.mPosition = position;
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }
    /**
     * 拿到一个ViewHolder对象
     */
    public static EPCommonViewHolder getHolder(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new EPCommonViewHolder(context, parent, layoutId, position);
        } else {
            EPCommonViewHolder holder = (EPCommonViewHolder) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }
    }
    /**
     * 通过ID获取控件
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
    public View getConvertView() {
        return mConvertView;
    }
    /**
     * 设置TextView内容
     */
    public EPCommonViewHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    public EPCommonViewHolder setTextVisbility(int viewId, int visibility) {
        TextView textView = getView(viewId);
        textView.setVisibility(visibility);
        return this;
    }

    /**
     * 设置图片
     */
    public EPCommonViewHolder setImage(int viewId, int resId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }
}

