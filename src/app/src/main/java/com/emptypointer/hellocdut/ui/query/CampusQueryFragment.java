package com.emptypointer.hellocdut.ui.query;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.utils.CommonUtils;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.widget.Button;
import com.rey.material.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class CampusQueryFragment extends Fragment {

    public static final String[] INTENT_TYPE_QUERYDEPOSITINFO = {
            "queryDepositInfo", "queryBankInfo", "queryConsumeInfo",
            "queryCustStateInfo"};
    public static final String INTENT_ACTION = "aciton";
    public static final String INTENT_START_DATE = "start_time";
    public static final String INTENT_END_DATE = "end_time";
    private static final int ACTIVITY_REQUEST_CODE = 0x0050FF;
    public static final java.lang.String ACTIVITY_RESULT_KEY = "intent_result";


    private com.rey.material.widget.Spinner mSpinner;
    private android.widget.TextView tvStartDate;
    private android.widget.ImageView imStartCalender;
    private android.widget.TextView tvEndDate;
    private android.widget.ImageView imEndCalender;
    private android.widget.LinearLayout layoutEndDate;
    private android.widget.LinearLayout layoutStartDate;
    private com.rey.material.widget.Button btnCommit;
    private static final long MILLIS_A_MONTH = 2592000000L;

    public CampusQueryFragment() {
        // Required empty public constructor
    }


    public static CampusQueryFragment newInstance() {
        return new CampusQueryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_campus_query, container, false);
        this.btnCommit = (Button) view.findViewById(R.id.btnCommit);
        this.layoutStartDate = (LinearLayout) view.findViewById(R.id.layoutStartDate);
        this.layoutEndDate = (LinearLayout) view.findViewById(R.id.layoutEndDate);
        this.imEndCalender = (ImageView) view.findViewById(R.id.imEndCalender);
        this.tvEndDate = (TextView) view.findViewById(R.id.tvEndDate);
        this.imStartCalender = (ImageView) view.findViewById(R.id.imStartCalender);
        this.tvStartDate = (TextView) view.findViewById(R.id.tvStartDate);
        this.mSpinner = (Spinner) view.findViewById(R.id.mSpinner);

        imEndCalender.setColorFilter(getResources().getColor(R.color.colorPrimary));
        imStartCalender.setColorFilter(getResources().getColor(R.color.colorPrimary));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] array= getResources().getStringArray(R.array.spinner_query_campus_card);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.row_spin_light, array);
        adapter.setDropDownViewResource(R.layout.row_spn_dropdown_light);
        mSpinner.setAdapter(adapter);

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        tvStartDate.setText(formatter.format(System.currentTimeMillis()- MILLIS_A_MONTH));
        tvEndDate.setText(formatter.format(System.currentTimeMillis()));

        layoutStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.Builder builder = new DatePickerDialog.Builder() {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                        String date = dialog.getFormattedDate(formatter);
                        tvStartDate.setText(date);
                        super.onPositiveActionClicked(fragment);
                    }
                };

                builder.positiveAction(getString(R.string.str_comfirm))
                        .negativeAction(getString(R.string.str_cancel));
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getFragmentManager(), null);
            }
        });

        layoutEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.Builder builder = new DatePickerDialog.Builder() {
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                        String date = dialog.getFormattedDate(formatter);
                        tvEndDate.setText(date);
                        super.onPositiveActionClicked(fragment);
                    }
                };
                builder.positiveAction(getString(R.string.str_comfirm))
                        .negativeAction(getString(R.string.str_cancel));
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getFragmentManager(), null);
            }
        });

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitQuery();
            }
        });
    }
    private void commitQuery(){
        Date dateStart = null;
        Date dateEnd = null;
        Intent intent = new Intent(getActivity(),
                CampusQueryResultActivity.class);
        int position = mSpinner.getSelectedItemPosition();
        intent.putExtra(INTENT_ACTION,
                INTENT_TYPE_QUERYDEPOSITINFO[position]);
        String strStartTime = tvStartDate.getText().toString();
        String strEndTime = tvEndDate.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            dateStart = dateFormat.parse(strStartTime);
            dateEnd = dateFormat.parse(strEndTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (dateStart.getTime() > dateEnd.getTime()) {
            UIUtils.makeSnake(getActivity(), getString(R.string.message_wrong_date_pick));
            return;
        }
        intent.putExtra(INTENT_START_DATE, strStartTime);
        intent.putExtra(INTENT_END_DATE, strEndTime);
        startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (!data.getExtras().getBoolean(ACTIVITY_RESULT_KEY)) {
                ((CampusCardActivity)getActivity()).showCaptchaDialog();
            }
        }
    }
}
