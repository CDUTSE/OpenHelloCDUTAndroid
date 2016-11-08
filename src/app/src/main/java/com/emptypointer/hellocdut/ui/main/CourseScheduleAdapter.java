package com.emptypointer.hellocdut.ui.main;

/**
 * Created by Sequarius on 2015/11/1.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.model.Course;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CourseScheduleAdapter extends
        Adapter<CourseScheduleAdapter.ViewHolder> {
    private int mWith;
    private List<List<Course>> mData;
    private Map<String, Integer> mColorMap;

    private Context mContext;

    private boolean showNoonClass;

    private boolean isCurrentWeek;

    private static final int[] VIEW_ID = {R.id.textView_row_1,
            R.id.textView_row_2, R.id.textView_row_3, R.id.textView_row_4,
            R.id.textView_row_5, R.id.textView_row_6, R.id.textView_row_7,
            R.id.textView_row_8, R.id.textView_row_9, R.id.textView_row_10,
            R.id.textView_row_11, R.id.textView_row_12, R.id.textView_row_13

    };

    private static final int[] DIVIDER_ID = {R.id.divider_row_1,
            R.id.divider_row_2, R.id.divider_row_3, R.id.divider_row_4,
            R.id.divider_row_5, R.id.divider_row_6, R.id.divider_row_7,
            R.id.divider_row_8, R.id.divider_row_9, R.id.divider_row_10,
            R.id.divider_row_11, R.id.divider_row_12};

    private static String[] NUMBER_TABLE;

    private static final long PER_WEEK = 1000 * 60 * 60 * 24 * 7;

    // private static final int[] DRAWABLE_TABLE = { R.drawable.course_info_01,
    // R.drawable.course_info_02, R.drawable.course_info_03,
    // R.drawable.course_info_04, R.drawable.course_info_05,
    // R.drawable.course_info_06, R.drawable.course_info_07,
    // R.drawable.course_info_08, R.drawable.course_info_09,
    // R.drawable.course_info_10, R.drawable.course_info_11,
    // R.drawable.course_info_12, R.drawable.course_info_13,
    // R.drawable.course_info_14, R.drawable.course_info_15,
    // R.drawable.course_info_16, R.drawable.course_info_17,
    // R.drawable.course_info_18, R.drawable.course_info_19,
    // R.drawable.course_info_20 };

    public static final String[] COLOR_TABLE = {"#99263d96", "#99ff6600",
            "#99EC008C", "#9912AB4A", "#99A32C9B", "#99B6722F", "#9966CFE6",
            "#99B6D300", "#99F1AB00", "#990047B6", "#99836EFF", "#996C1B72",
            "#99CAAD00", "#997E2B42", "#99EBADCD", "#990491D1", "#996ADCA2",
            "#9900626E", "#9994A1E2", "#99B75312",};
    private static final String TAG = "CourseScheduleAdapter";
    private int[] days;
    private int[] months;

    public CourseScheduleAdapter(List<List<Course>> data, int with,
                                 Map<String, Integer> colorMap, boolean showNoonClass,
                                 Context context, long firstDayTimeMillion) {
        super();
        mData = data;
        mWith = with;
        mColorMap = colorMap;
        mContext = context;
        this.showNoonClass = showNoonClass;
        NUMBER_TABLE = mContext.getResources().getStringArray(
                R.array.week_array);
        long timeDiffer = System.currentTimeMillis() - firstDayTimeMillion;
        if (timeDiffer > 0 && timeDiffer < PER_WEEK) {
            isCurrentWeek = true;
        } else {
            isCurrentWeek = false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(firstDayTimeMillion);

        days = new int[8];
        months = new int[8];
        for (int i = 1; i < days.length; i++) {
            days[i] = calendar.get(Calendar.DATE);
            months[i] = calendar.get(Calendar.MONTH) + 1;
            calendar.add(Calendar.DATE, 1);

        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public List<TextView> imTextViews;
        public View imItemView;
        private List<View> imDeviders;
        public TextView imtvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            imItemView = itemView;
            imTextViews = new ArrayList<TextView>();
            imDeviders = new ArrayList<View>();
            for (int i = 0; i < VIEW_ID.length; i++) {
                imTextViews.add((TextView) itemView.findViewById(VIEW_ID[i]));

            }

            imtvDate = (TextView) itemView.findViewById(R.id.textView_date);

            for (int i = 0; i < DIVIDER_ID.length; i++) {
                imDeviders.add((View) itemView.findViewById(DIVIDER_ID[i]));
            }
        }
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
//        Log.i(TAG, "size===" + mData.size());
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // TODO Auto-generated method stub
        // 设置第一栏目
        if (!showNoonClass) {
            holder.imTextViews.get(5).setVisibility(View.GONE);
            holder.imDeviders.get(5).setVisibility(View.GONE);
        }
        if (position == 0) {
            // 設置第一列爲單倍寬度
            holder.itemView.setLayoutParams(new LinearLayout.LayoutParams(
                    mWith, LayoutParams.MATCH_PARENT));
            holder.imtvDate.setText(isCurrentWeek ? mContext
                    .getString(R.string.str_current_weak) : mContext
                    .getString(R.string.str_return_current_week));
            holder.imtvDate.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, 0, 1));
            holder.imtvDate.setTextColor(mContext.getResources().getColor(
                    isCurrentWeek ? R.color.color_ep_text_grean
                            : R.color.color_ep_text_red));
            if (!isCurrentWeek) {
                holder.imtvDate.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        ((ScheduleActivity) mContext).backToCurrentWeek();
                    }
                });
            }
            // 去掉星期行
            holder.imTextViews.get(0).setVisibility(View.GONE);
            for (int i = 1; i < holder.imTextViews.size(); i++) {
                TextView textView = holder.imTextViews.get(i);
                textView.setTextColor(Color.parseColor("#000000"));
                if (i < 5) {
                    textView.setText(String.valueOf(i));
                } else if (i == 5) {
                    textView.setText("午");

                } else {
                    textView.setText(String.valueOf(i - 1));
                }
            }
        } else {
            // 設置星期列爲雙倍寬度
            holder.itemView.setLayoutParams(new LinearLayout.LayoutParams(
                    mWith * 2, LayoutParams.MATCH_PARENT));

            List<Course> courses = mData.get(position);

            Log.i(TAG, "position" + position + "day===" + days[position]
                    + ";month====" + months[position]);
            String strDay = days[position] < 10 ? "0"
                    + String.valueOf(days[position]) : String
                    .valueOf(days[position]);
            holder.imtvDate.setText(months[position] + "-" + strDay);
            holder.imTextViews.get(0).setText(NUMBER_TABLE[position]);
            holder.imTextViews.get(0).setTextColor(Color.parseColor("#000000"));
            // 向下遍历
            for (int i = 1; i < holder.imTextViews.size(); i++) {
                for (final Course course : courses) {
                    // 如果课程存在
                    if (course.getBegin() == i) {
                        TextView textView = holder.imTextViews.get(i);
                        String fullName = course.getFullName();
                        String classrom = course.getRoom();
                        String categroy = course.getCategroy();
                        String strName = categroy.equals("实") ? "[" + categroy
                                + "]" + fullName + "@" + classrom : fullName
                                + "@" + classrom;
                        textView.setText(strName);
                        final int backColor = Color
                                .parseColor(COLOR_TABLE[mColorMap.get(fullName)]);
                        textView.setBackgroundColor(backColor);
                        textView.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                ((ScheduleActivity) mContext)
                                        .showCourseDetail(course, v, backColor);
                            }
                        });
                        int section = course.getSection();
                        // 设置课程权重高度
                        textView.setLayoutParams(new LinearLayout.LayoutParams(
                                LayoutParams.WRAP_CONTENT, 0, section));
                        // 向下遍历 去掉原来存在的textView；
                        for (int j = i + 1; j < i + section; j++) {
                            holder.imTextViews.get(j).setVisibility(View.GONE);
                            if (j < 12) {
                                holder.imDeviders.get(j - 1)
                                        .setBackgroundColor(backColor);
                            }
                        }
                    }
                }

            }

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        View item = LayoutInflater.from(arg0.getContext()).inflate(
                R.layout.row_schedule_item, arg0, false);
        return new ViewHolder(item);
    }
}