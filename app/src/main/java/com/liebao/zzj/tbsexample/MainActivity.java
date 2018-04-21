package com.liebao.zzj.tbsexample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class MainActivity extends Activity implements OnClickListener {

    private static final int WEBVIEWGOBACK = 0X123;
    private static final String APP_NAME_UA = " XiaoMi/MiuiBrowser/Zcom/1.0";

    EditText mz_edittext;
    WebView mz_tbs_webview;
    //Button mz_button;
    ImageView mz_imageview;
    String mz_url;
    RelativeLayout mz_llayout1;
    int mz_llayout1place[];
    Animation mz_animation = null;
    ProgressBar mz_pb;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initview() {
        mz_imageview = (ImageView) this.findViewById(R.id.mzImageView);
        mz_edittext = (EditText) this.findViewById(R.id.mzEditText);
        mz_pb = (ProgressBar) this.findViewById(R.id.mzprogressBar1);
        mz_imageview.setOnClickListener(this);
        mz_edittext.setOnClickListener(this);
        mz_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate);
        mz_animation.setRepeatMode(Animation.RESTART);

        mz_llayout1 = (RelativeLayout) this.findViewById(R.id.llayout1);

        mz_tbs_webview = (WebView) this.findViewById(R.id.mzTSBWebView);
        mz_tbs_webview.getSettings().setUserAgentString(mz_tbs_webview.getSettings().getUserAgentString() + APP_NAME_UA);

        mz_url = mz_edittext.getText().toString();
        mz_tbs_webview.getSettings().setJavaScriptEnabled(true);
        mz_tbs_webview.loadUrl(mz_url);

        mz_tbs_webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if (i == 100) {
                    mz_pb.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    mz_pb.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    mz_pb.setProgress(i);//设置进度值
                }
            }

            @Override
            public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
                return super.onJsAlert(webView, s, s1, jsResult);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return super.onConsoleMessage(consoleMessage);
            }
        });
        mz_tbs_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                return super.shouldOverrideUrlLoading(webView, s);
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                mz_imageview.startAnimation(mz_animation);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);

                mz_edittext.setText(mz_tbs_webview.getUrl());
                if (mz_llayout1place == null) {
                    mz_llayout1place = new int[]{mz_llayout1.getTop(), mz_llayout1.getBottom()};
                }
                mz_llayout1.setTop(mz_llayout1place[0]);
                mz_llayout1.setBottom(mz_llayout1place[1]);

                addJsinfobar(mz_llayout1place[1]);
                //mz_tbs_webview.setTop(mz_llayout1place[1]);
                mz_imageview.clearAnimation();
            }
        });

        mz_tbs_webview.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    //mz_llayout1.setVisibility(View.INVISIBLE);
                    int scroll_diff = scrollY - oldScrollY;
                    if (mz_llayout1.getBottom() - scroll_diff < 0) {
                        mz_llayout1.setTop(-mz_llayout1place[1]);
                        mz_llayout1.setBottom(0);
                        //setJsinobar(0);
                    } else {
                        mz_llayout1.setTop(mz_llayout1.getTop() - scroll_diff);
                        mz_llayout1.setBottom(mz_llayout1.getBottom() - scroll_diff);
                        //setJsinobar(mz_llayout1.getBottom() - scroll_diff);
                    }

                }
                if (scrollY < oldScrollY) {
                    //mz_llayout1.setVisibility(View.VISIBLE);
                    int scroll_diff = oldScrollY - scrollY;
                    if (mz_llayout1.getTop() + scroll_diff > 0) {
                        mz_llayout1.setTop(0);
                        mz_llayout1.setBottom(mz_llayout1place[1]);
                        //setJsinobar(mz_llayout1place[1]);
                    } else {
                        mz_llayout1.setTop(mz_llayout1.getTop() + scroll_diff);
                        mz_llayout1.setBottom(mz_llayout1.getBottom() + scroll_diff);
                        //setJsinobar(mz_llayout1.getBottom() + scroll_diff);
                    }
                }
            }
        });
    }

    public void addJsinfobar(int height) {
        this.mz_tbs_webview.loadUrl("javascript:" +
                "if(document.getElementById(\"tsbexamplehide\")) \n" +
                "{ \n" +
                "}else{\n" +
                "\tvar para=document.createElement(\"div\");\n" +
                "\tpara.id = \"tsbexamplehide\";\n" +
                "\tpara.style.height = '" + (height / 2.7) + "px';\n" +
                "\tvar first=document.body.firstChild;\n" +
                "\tdocument.body.insertBefore(para,first);\n" +
                "}");
        //this.mz_llayout1.setVisibility(View.GONE);
    }

   /* public void setJsinobar(int height) {
        Log.e("test", "" + height + "   " + mz_llayout1.getBottom());

        *//*this.mz_tbs_webview.loadUrl("javascript:" +
                "document.getElementById(\"tsbexamplehide\").style.height = '" + (height / 2) + "';");*//*
    }*/

    @Override
    public void onClick(View v) {
        if (v == mz_imageview) {
            mz_url = mz_edittext.getText().toString();
            if (mz_url == null) {
                mz_url = this.getResources().getString(R.string.index_site);
            }
            if (!(mz_url.startsWith("http://") || mz_url.startsWith("https://"))) {
                mz_url = "http://" + mz_url;
            }
            mz_tbs_webview.loadUrl(mz_url);
        } else if (v == mz_edittext) {
            mz_edittext.setSelection(0, mz_edittext.getText().length());
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
