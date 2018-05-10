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
    private TextView mz_fg_left_bookmark_textview;
    private FragmentManager fragmentManager;

    public MzFragmentLeft(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_left, container, false);
        mz_fg_left_bookmark_textview = (TextView) view.findViewById(R.id.fg_bookmark_textview);
        mz_fg_left_bookmark_textview.setOnClickListener(this);

        mz_fg_left_bookmark_textview.performClick();
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == mz_fg_left_bookmark_textview) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            MzFgBookmark mzFgBookmark = new MzFgBookmark();
            Bundle bd = new Bundle();
            bd.putString("page", "bookmarks");
            mzFgBookmark.setArguments(bd);

            fragmentTransaction.replace(R.id.llayout_setting_right, mzFgBookmark);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}