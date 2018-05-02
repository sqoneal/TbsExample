package com.liebao.zzj.tbsexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeActivity extends Activity implements View.OnClickListener {
    private TextView mz_skipTextView;
    private ImageView mz_wcImageView;
    private int mz_skipsecond = 5;
    private static final int SETSKIPTEXTVIEW = 0X110;
    private static final int BREAKTHETHREAD = -1;
    private Thread mthread;
    Animation mz_alphaAnimation;

    Handler mz_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SETSKIPTEXTVIEW:
                    mz_skipTextView.setText(String.valueOf(mz_skipsecond) + " " + getApplicationContext().getString(R.string.skip));
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_welcome);

        initview();
    }

    private void initview() {
        mz_skipTextView = (TextView) findViewById(R.id.mzskipTextView);
        mz_wcImageView = (ImageView) findViewById(R.id.mzwcImageView);

        mz_handler.sendEmptyMessage(SETSKIPTEXTVIEW);
        mz_skipTextView.setOnClickListener(this);

        mthread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                    mz_skipsecond = mz_skipsecond - 1;
                    if (mz_skipsecond == 0) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        break;
                    } else if (mz_skipsecond == BREAKTHETHREAD) {
                        break;
                    }else if(mz_skipsecond > 0){
                        mz_handler.sendEmptyMessage(SETSKIPTEXTVIEW);
                    }
                }
                finish();
            }
        };
        mthread.start();

        mz_alphaAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.welcomeimage_alpha);
        mz_wcImageView.startAnimation(mz_alphaAnimation);
    }

    @Override
    public void onClick(View v) {
        if (v == mz_skipTextView) {
            mz_skipsecond = BREAKTHETHREAD;
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            mthread.interrupt();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        mthread.interrupt();
        finish();
    }
}
