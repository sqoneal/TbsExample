package com.liebao.zzj.tbsexample;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MzFragmentLeft extends Fragment implements View.OnClickListener {
    public static final int TOBOOKMARK = 0X0011;
    public static final int TODOWNLOAD = 0X0012;
    public static final int TOABOUT = 0X0013;
    private TextView mz_fg_left_bookmark_textview;
    private TextView mz_fg_left_about_textview;
    private TextView mz_fg_left_download_textview;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private MzFragmentRight mzFragmentRight;

    public MzFragmentLeft(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_left, container, false);
        mz_fg_left_bookmark_textview = (TextView) view.findViewById(R.id.fg_bookmark_textview);
        mz_fg_left_bookmark_textview.setOnClickListener(this);
        mz_fg_left_about_textview = (TextView) view.findViewById(R.id.fg_about_textview);
        mz_fg_left_about_textview.setOnClickListener(this);
        mz_fg_left_download_textview = (TextView) view.findViewById(R.id.fg_download_textview);
        mz_fg_left_download_textview.setOnClickListener(this);

        int key = this.getActivity().getIntent().getIntExtra("page", 0);
        if (key == TOBOOKMARK) {
            mz_fg_left_bookmark_textview.performClick();
        } else if (key == TODOWNLOAD) {
            mz_fg_left_download_textview.performClick();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == mz_fg_left_bookmark_textview) {
            drumpRightPage(TOBOOKMARK);
        } else if (v == mz_fg_left_about_textview) {
            drumpRightPage(TOABOUT);
        } else if (v == mz_fg_left_download_textview) {
            drumpRightPage(TODOWNLOAD);
        }
    }

    private void drumpRightPage(int key) {
        fragmentTransaction = fragmentManager.beginTransaction();
        mzFragmentRight = new MzFragmentRight();
        Bundle bd = new Bundle();
        bd.putInt("page", key);
        mzFragmentRight.setArguments(bd);
        fragmentTransaction.replace(R.id.llayout_setting_right, mzFragmentRight);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}