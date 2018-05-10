package com.liebao.zzj.tbsexample;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.LinearLayout;

public class SettingActivity extends Activity {
    private LinearLayout mz_setting_left_llayout;
    private LinearLayout mz_setting_right_llayout;
    private FragmentManager mzfragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

       mz_setting_left_llayout = (LinearLayout) this.findViewById(R.id.llayout_setting_left);
        mz_setting_right_llayout = (LinearLayout) this.findViewById(R.id.llayout_setting_right);

        mzfragmentManager = getFragmentManager();
        MzFragmentLeft mzfragmentleft = new MzFragmentLeft(mzfragmentManager);
        FragmentTransaction ft = mzfragmentManager.beginTransaction();
        ft.replace(R.id.llayout_setting_left, mzfragmentleft);
        ft.commit();
    }
}
