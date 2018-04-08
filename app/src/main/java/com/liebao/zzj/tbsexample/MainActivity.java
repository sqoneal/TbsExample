package com.liebao.zzj.tbsexample;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final int WEBVIEWGOBACK = 0X123;
    private static final String APP_NAME_UA = " XiaoMi/MiuiBrowser/Zcom/1.0";

    EditText mz_edittext;
    WebView mz_tbs_webview;
    Button mz_button;
    String mz_url;
    LinearLayout mz_llayout1;
    int mz_llayout1place[];

    Handler mz_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WEBVIEWGOBACK:
                    if (mz_tbs_webview.canGoBack()) {
                        mz_tbs_webview.goBack();
                        mz_edittext.setText(mz_tbs_webview.getUrl());
                    } else {
                        finish();
                    }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        Log.e("top",mz_llayout1.getTop()+"");
        Log.e("Bottom",mz_llayout1.getBottom()+"");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initview() {
        mz_button = (Button) this.findViewById(R.id.mzButton);
        mz_edittext = (EditText) this.findViewById(R.id.mzEditText);
        mz_button.setOnClickListener(this);

        mz_llayout1 = (LinearLayout) this.findViewById(R.id.llayout1);

        mz_tbs_webview = (WebView) this.findViewById(R.id.mzTSBWebView);
        mz_tbs_webview.setOnClickListener(this);
        mz_tbs_webview.getSettings().setUserAgentString(mz_tbs_webview.getSettings().getUserAgentString() + APP_NAME_UA);

        mz_url = mz_edittext.getText().toString();
        mz_tbs_webview.getSettings().setJavaScriptEnabled(true);
        mz_tbs_webview.loadUrl(mz_url);
        mz_tbs_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                return super.shouldOverrideUrlLoading(webView, s);
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                mz_edittext.setText(mz_tbs_webview.getUrl());
                Log.e("ftop",mz_llayout1.getTop()+"");
                Log.e("fBottom",mz_llayout1.getBottom()+"");
                if (mz_llayout1place == null){
                    mz_llayout1place = new int[]{mz_llayout1.getTop(), mz_llayout1.getBottom()};
                }
                mz_llayout1.setTop(mz_llayout1place[0]);
                mz_llayout1.setBottom(mz_llayout1place[1]);
            }
        });
        mz_tbs_webview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.e("stop",mz_llayout1.getTop()+"");
                Log.e("sBottom",mz_llayout1.getBottom()+"");
                if (scrollY > oldScrollY) {
                    //mz_llayout1.setVisibility(View.INVISIBLE);
                    int scroll_diff = scrollY - oldScrollY;
                    if (mz_llayout1.getBottom() - scroll_diff < 0) {
                        mz_llayout1.setTop(-mz_llayout1place[1]);
                        mz_llayout1.setBottom(0);
                    } else {
                        mz_llayout1.setTop(mz_llayout1.getTop() - scroll_diff);
                        mz_llayout1.setBottom(mz_llayout1.getBottom() - scroll_diff);
                    }
                }
                if (scrollY < oldScrollY) {
                    //mz_llayout1.setVisibility(View.VISIBLE);
                    int scroll_diff = oldScrollY - scrollY;
                    if (mz_llayout1.getTop() + scroll_diff > 0) {
                        mz_llayout1.setTop(0);
                        mz_llayout1.setBottom(mz_llayout1place[1]);
                    } else {
                        mz_llayout1.setTop(mz_llayout1.getTop() + scroll_diff);
                        mz_llayout1.setBottom(mz_llayout1.getBottom() + scroll_diff);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mz_button) {
            mz_url = mz_edittext.getText().toString();
            if (mz_url == null) {
                mz_url = this.getResources().getString(R.string.index_site);
            }
            if (!(mz_url.startsWith("http://") || mz_url.startsWith("https://"))) {
                mz_url = "http://" + mz_url;
            }
            mz_tbs_webview.loadUrl(mz_url);
        }else if(v == mz_tbs_webview){
            mz_llayout1.setTop(0);
            mz_llayout1.setBottom(48);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mz_handler.sendEmptyMessage(WEBVIEWGOBACK);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
