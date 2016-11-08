package com.emptypointer.hellocdut.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPCommonAdapter;
import com.emptypointer.hellocdut.customer.EPCommonViewHolder;
import com.emptypointer.hellocdut.model.FunctionItem;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.ui.query.CampusCardActivity;
import com.emptypointer.hellocdut.ui.query.LibraryActivity;
import com.emptypointer.hellocdut.ui.query.QueryListActivity;
import com.emptypointer.hellocdut.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FunctionFragment extends Fragment {
    private int[] mIconID = {R.drawable.ic_function_schedule,
            R.drawable.ic_function_search,R.drawable.ic_teaching_evaluation,
            R.drawable.ic_function_library, R.drawable.ic_functioncampus_card,
            R.drawable.ic_function_aad_more};

    private GridView gridView;

    public FunctionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_function, container, false);
        this.gridView = (GridView) view.findViewById(R.id.gridView);
        String[] functionNames = getResources().getStringArray(
                R.array.indicator_function);
        final List<FunctionItem> items = new ArrayList<>();
        for (int i = 0; i < functionNames.length; i++) {
            items.add(new FunctionItem(mIconID[i], functionNames[i]));
        }
        BaseAdapter adapter = new EPCommonAdapter<FunctionItem>(getActivity(), R.layout.row_fuction_view, items) {

            @Override
            public void convert(EPCommonViewHolder holder, FunctionItem item) {
                holder.setImage(R.id.imAvatar, item.getIconResID()).setText(R.id.tvName, item.getFuctionName());
            }
        };
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                switch (position) {
                    case 0:
                        if (UserInfo.getInstance(getActivity()).getUserStatus() < UserInfo.USER_STATUS_CERTIFICATE) {
                            UIUtils.createTObindDiglog(getString(R.string.str_stu_id), getActivity());
                        } else {
                            intent.setClass(getActivity(), ScheduleActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 1:
                        intent.setClass(getActivity(), QueryListActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(getContext(), EvaluationActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        if (UserInfo.getInstance(getActivity()).getUserLibStatus() < UserInfo.USER_STATUS_CERTIFICATE) {
                            UIUtils.createTObindDiglog(getString(R.string.str_lib), getActivity());
                        } else {
                            intent.setClass(getActivity(), LibraryActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 4:
                        if (UserInfo.getInstance(getActivity()).getUserCampusStatus() < UserInfo.USER_STATUS_CERTIFICATE) {
                            UIUtils.createTObindDiglog(getString(R.string.str_campus_card), getActivity());
                        } else {
                            intent.setClass(getActivity(), CampusCardActivity.class);
                            startActivity(intent);
                        }
                        break;
                    case 5:
                        intent.setClass(getActivity(), AddonesActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
        return view;

    }

    public static Fragment newInstance() {
        return new FunctionFragment();
    }

}
