package com.emptypointer.hellocdut.customer;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.emptypointer.hellocdut.R;
import com.rey.material.app.Dialog;
import com.rey.material.widget.ProgressView;


/**
 * Created by Sequarius on 2015/10/25.
 */
public class EPProgressDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dialog dialog = new Dialog(getActivity());
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.layout_loading_dialog, null);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        ((ProgressView)view.findViewById(R.id.progress_pv_circular_colors)).start();
        return dialog;
    }
}
