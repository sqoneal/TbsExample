package com.liebao.zzj.tbsexample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
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
        //初始化X5内核
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核。

            }

            @Override
            public void onViewInitFinished(boolean b) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e("@@", "加载内核是否成功:" + b);
            }
        });

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

        //获取判断剪切板是否有网址
        final ClipboardManager mz_clipboardmanager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData.Item mz_item = mz_clipboardmanager.getPrimaryClip().getItemAt(0);
        final String tempurl = mz_item.getText().toString();
        if (tempurl.startsWith("http://") || tempurl.startsWith("https://")) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                    .setTitle("是否打开复制的网址？")//设置对话框的标题
                    //.setMessage("我是对话框的内容")//设置对话框的内容
                    //设置对话框的按钮
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mz_url = tempurl;
                            mz_tbs_webview.loadUrl(mz_url);
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
        }
        if (tempurl.startsWith("www")) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                    .setTitle("是否打开复制的网址？")//设置对话框的标题
                    //.setMessage("我是对话框的内容")//设置对话框的内容
                    //设置对话框的按钮
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mz_url = "http://" + tempurl;
                            mz_tbs_webview.loadUrl(mz_url);
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();

        }

        if (mz_url == null) {
            mz_url = mz_edittext.getText().toString();
        }

        //设置UA
        mz_tbs_webview.getSettings().setUserAgentString(mz_tbs_webview.getSettings().getUserAgentString() + APP_NAME_UA);
        //设置WebView属性，能够执行Javascript脚本
        mz_tbs_webview.getSettings().setJavaScriptEnabled(true);
        //设置WebView 可以加载更多格式页面
        mz_tbs_webview.getSettings().setLoadWithOverviewMode(true);
        //设置WebView使用广泛的视窗
        mz_tbs_webview.getSettings().setUseWideViewPort(true);
        //支持手势缩放
        mz_tbs_webview.getSettings().setBuiltInZoomControls(true);
        //支持2.2以上所有版本
        mz_tbs_webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        //告诉webview启用应用程序缓存api。
        mz_tbs_webview.getSettings().setAppCacheEnabled(true);
        //设置是否启用了DOM storage API。
        mz_tbs_webview.getSettings().setDomStorageEnabled(true);
        //自动打开窗口
        mz_tbs_webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 没有的话会黑屏 支持插件
        mz_tbs_webview.getSettings().setPluginsEnabled(true);

        /**
         * setAllowFileAccess 启用或禁止WebView访问文件数据 setBlockNetworkImage 是否显示网络图像
         * setBuiltInZoomControls 设置是否支持缩放 setCacheMode 设置缓冲的模式
         * setDefaultFontSize 设置默认的字体大小 setDefaultTextEncodingName 设置在解码时使用的默认编码
         * setFixedFontFamily 设置固定使用的字体 setJavaSciptEnabled 设置是否支持Javascript
         * setLayoutAlgorithm 设置布局方式 setLightTouchEnabled 设置用鼠标激活被选项
         * setSupportZoom 设置是否支持变焦
         * */
        mz_tbs_webview.getSettings().setAllowFileAccess(true);
        mz_tbs_webview.getSettings().setBuiltInZoomControls(true);
        mz_tbs_webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mz_tbs_webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


        mz_tbs_webview.loadUrl(mz_url);

        mz_tbs_webview.setWebChromeClient(new WebChromeClient() {
            // 一个回调接口使用的主机应用程序通知当前页面的自定义视图已被撤职
            IX5WebChromeClient.CustomViewCallback customViewCallback;

            // 进入全屏的时候
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
                Log.e("show", "aaaaaaaaa");
                // 赋值给callback
                customViewCallback = callback;
                // 设置webView隐藏
                mz_tbs_webview.setVisibility(View.GONE);
                mz_llayout1.setVisibility(View.GONE);
                mz_tool_layout.setVisibility(View.GONE);
                // 声明video，把之后的视频放到这里面去
                FrameLayout video = (FrameLayout) findViewById(R.id.mzvideoFrameLayout);
                // 将video放到当前视图中
                video.addView(view);
                // 横屏显示
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                // 设置全屏
                setFullScreen();

                super.onShowCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                if (customViewCallback != null) {
                    // 隐藏掉
                    customViewCallback.onCustomViewHidden();
                    customViewCallback = null;
                }
                // 用户当前的首选方向
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                // 退出全屏
                quitFullScreen();
                // 设置WebView可见
                mz_tbs_webview.setVisibility(View.VISIBLE);
                mz_llayout1.setVisibility(View.VISIBLE);
                mz_tool_layout.setVisibility(View.VISIBLE);

                super.onHideCustomView();
            }

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
            public void onReceivedError(WebView webView, int i, String s, String s1) {

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

    /**
     * 设置全屏
     */
    private void setFullScreen() {
        // 设置全屏的相关属性，获取当前的屏幕状态，然后设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 全屏下的状态码：1098974464
        // 窗口下的状态吗：1098973440
    }

    /**
     * 退出全屏
     */
    private void quitFullScreen() {
        // 声明当前屏幕状态的参数并获取
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
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
            //退出全屏
            quitFullScreen();
            return true;
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
