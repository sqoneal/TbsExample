package com.liebao.zzj.tbsexample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    LinearLayout mz_tool_layout;
    Animation mz_toollayout_animation;
    Animation mz_toollayout_animation2;
    ImageView mz_back_imageview;
    ImageView mz_forward_imageview;
    ImageView mz_add_imageview;
    ImageView mz_share_imageview;
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
        if (getIntent().getData() != null) {
            Log.e("intent",getIntent().getDataString());
            mz_url = getIntent().getDataString();
        }
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
        mz_add_imageview = (ImageView) this.findViewById(R.id.mz_add_imageview);
        mz_back_imageview = (ImageView) this.findViewById(R.id.mz_back_imageview);
        mz_forward_imageview = (ImageView) this.findViewById(R.id.mz_forward_imageview);
        mz_share_imageview = (ImageView) this.findViewById(R.id.mz_share_imageview);
        mz_tool_layout = (LinearLayout) this.findViewById(R.id.mzToollayout);
        mz_toollayout_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_translate);
        mz_toollayout_animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);

        mz_add_imageview.setOnClickListener(this);
        mz_back_imageview.setOnClickListener(this);
        mz_forward_imageview.setOnClickListener(this);
        mz_share_imageview.setOnClickListener(this);
        mz_imageview.setOnClickListener(this);
        mz_edittext.setOnClickListener(this);
        mz_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate);
        mz_animation.setRepeatMode(Animation.RESTART);

        mz_llayout1 = (RelativeLayout) this.findViewById(R.id.llayout1);

        mz_tbs_webview = (WebView) this.findViewById(R.id.mzTSBWebView);
        mz_tbs_webview.getSettings().setUserAgentString(mz_tbs_webview.getSettings().getUserAgentString() + APP_NAME_UA);

        if (mz_url == null) {
            mz_url = mz_edittext.getText().toString();
        }
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
                    mz_llayout1place = new int[]{mz_llayout1.getTop(), mz_llayout1.getBottom(),
                            mz_tool_layout.getTop(), mz_tool_layout.getBottom()};
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
                    int scroll_diff = scrollY - oldScrollY;
                    if (mz_llayout1.getBottom() - scroll_diff < 0) {
                        mz_llayout1.setTop(-mz_llayout1place[1]);
                        mz_llayout1.setBottom(0);
                    } else {
                        mz_llayout1.setTop(mz_llayout1.getTop() - scroll_diff);
                        mz_llayout1.setBottom(mz_llayout1.getBottom() - scroll_diff);
                    }
                    if (mz_tool_layout.getVisibility() != View.GONE) {
                        mz_tool_layout.startAnimation(mz_toollayout_animation);
                        mz_tool_layout.setVisibility(View.GONE);
                    }
                }
                if (scrollY < oldScrollY) {
                    int scroll_diff = oldScrollY - scrollY;
                    if (mz_llayout1.getTop() + scroll_diff > 0) {
                        mz_llayout1.setTop(0);
                        mz_llayout1.setBottom(mz_llayout1place[1]);
                    } else {
                        mz_llayout1.setTop(mz_llayout1.getTop() + scroll_diff);
                        mz_llayout1.setBottom(mz_llayout1.getBottom() + scroll_diff);
                    }

                    if (mz_tool_layout.getVisibility() == View.GONE) {
                        mz_tool_layout.setVisibility(View.VISIBLE);
                        mz_tool_layout.startAnimation(mz_toollayout_animation2);
                        /*mz_add_imageview.startAnimation(mz_toollayout_animation2);
                        mz_back_imageview.startAnimation(mz_toollayout_animation2);
                        mz_forward_imageview.startAnimation(mz_toollayout_animation2);
                        mz_share_imageview.startAnimation(mz_toollayout_animation2);*/
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
        } else if (v == mz_back_imageview) {
            if (mz_tbs_webview.canGoBack()) {
                mz_tbs_webview.goBack();
            }
        } else if (v == mz_forward_imageview) {
            if (mz_tbs_webview.canGoForward()) {
                mz_tbs_webview.goForward();
            }
        } else if (v == mz_share_imageview) {
            shareapp();
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

    private void shareapp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "share");
        intent.putExtra(Intent.EXTRA_TEXT, "This is my browser.");
        startActivity(Intent.createChooser(intent, "choose the share way"));
    }
}
