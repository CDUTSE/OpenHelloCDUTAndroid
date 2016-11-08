package com.emptypointer.hellocdut.ui.basic;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.emptypointer.hellocdut.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RESOUCE_ID = "param1";

    // TODO: Rename and change types of parameters
    private int resourceID;



    // TODO: Rename and change types and number of parameters
    public static ImageFragment newInstance(int param1) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RESOUCE_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            resourceID = getArguments().getInt(ARG_RESOUCE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (resourceID != R.layout.fragment_drawable_with_bottom_button) {
            // TODO Auto-generated method stub
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setBackgroundResource(resourceID);
            return imageView;
        } else {
            View view = inflater.inflate(R.layout.fragment_drawable_with_bottom_button,container, false);
            Button button = (Button) view.findViewById(R.id.button_quit);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    getActivity().finish();
                }
            });
            button.setVisibility(View.VISIBLE);
            AlphaAnimation animation = new AlphaAnimation(0.3f,
                    1.0f);
            animation.setDuration(1000);
            button.startAnimation(animation);
            return view;
        }
    }
}
