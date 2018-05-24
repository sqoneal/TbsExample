package com.liebao.zzj.tbsexample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liebao.zzj.tbsexample.utils.MzSqLiteOpenHelper;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class MainActivity extends Activity implements OnClickListener {

    private static final int WEBVIEWGOBACK = 0X123;
    private static final String APP_NAME_UA = " XiaoMi/MiuiBrowser/Zcom/1.0";
    public static final int FROMBOOKMARK = 0x211;

    private EditText mz_edittext;
    //WebView mz_tbs_webview;
    private FrameLayout mz_web_framelayout;
    private LinearLayout mz_tool_layout;
    private Animation mz_toollayout_animation, mz_toollayout_animation2, mz_bookmark_animation, mz_animation;
    private ImageView mz_back_imageview, mz_forward_imageview, mz_add_imageview,
            mz_share_imageview, mz_bookmark_imageview;
    private boolean mz_ischangebookmarkimage = false;
    private ImageView mz_imageview;
    private String mz_url;
    private RelativeLayout mz_llayout1;
    private int mz_llayout1place[];
    private ProgressBar mz_pb;
    private int clipindex = 0;

    private final static int mz_webviewsum = 10;
    private WebView mz_child_webview[] = new WebView[mz_webviewsum];
    private int mz_childcurrentinder = 0;
    private LinearLayout mz_newtab_layout;
    private String mz_child_title[] = new String[mz_webviewsum];
    private String mz_child_url[] = new String[mz_webviewsum];
    private TextView mz_child_textview[] = new TextView[mz_webviewsum];
    private TextView mz_newtab_textview, mz_managebookmark_textview, mz_closetab_textview, mz_managedownload_textview;

    private MzSqLiteOpenHelper mzSqLiteOpenHelper;
    private SQLiteDatabase mzdb;

    Handler mz_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WEBVIEWGOBACK:
                    if (mz_child_webview[mz_childcurrentinder].canGoBack()) {
                        mz_child_webview[mz_childcurrentinder].goBack();
                        mz_edittext.setText(mz_child_webview[mz_childcurrentinder].getUrl());
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
    protected void onResume() {
        if (getIntent().getStringExtra("url") != null) {
            mz_url = getIntent().getStringExtra("url");
            /*mz_child_webview[mz_childcurrentinder].loadUrl(mz_url);*/
            newChildWebView(mz_url);
        }
        clipUrltip();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mzdb.close();
        mzSqLiteOpenHelper.close();
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
        mzSqLiteOpenHelper = new MzSqLiteOpenHelper(this, "mz.db", null, 1);

        mz_imageview = (ImageView) this.findViewById(R.id.mzImageView);
        mz_edittext = (EditText) this.findViewById(R.id.mzEditText);
        mz_pb = (ProgressBar) this.findViewById(R.id.mzprogressBar1);
        mz_add_imageview = (ImageView) this.findViewById(R.id.mz_add_imageview);
        mz_back_imageview = (ImageView) this.findViewById(R.id.mz_back_imageview);
        mz_forward_imageview = (ImageView) this.findViewById(R.id.mz_forward_imageview);
        mz_share_imageview = (ImageView) this.findViewById(R.id.mz_share_imageview);
        mz_bookmark_imageview = (ImageView) this.findViewById(R.id.mz_bookmark_imageview);
        mz_tool_layout = (LinearLayout) this.findViewById(R.id.mzToollayout);
        mz_toollayout_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_translate);
        mz_toollayout_animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        mz_bookmark_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_bookmark_rotate_scale_set);
        mz_web_framelayout = (FrameLayout) this.findViewById(R.id.mzwebframelayout);

        mz_add_imageview.setOnClickListener(this);
        mz_back_imageview.setOnClickListener(this);
        mz_forward_imageview.setOnClickListener(this);
        mz_share_imageview.setOnClickListener(this);
        mz_bookmark_imageview.setOnClickListener(this);
        mz_imageview.setOnClickListener(this);
        mz_edittext.setOnClickListener(this);
        mz_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate);
        mz_animation.setRepeatMode(Animation.RESTART);

        mz_llayout1 = (RelativeLayout) this.findViewById(R.id.llayout1);
        mz_newtab_layout = (LinearLayout) this.findViewById(R.id.mznewtablayout);
        mz_newtab_textview = (TextView) this.findViewById(R.id.mznewtabtextview);
        mz_newtab_textview.setOnClickListener(this);
        mz_closetab_textview = (TextView) this.findViewById(R.id.mzclosetextview);
        mz_closetab_textview.setOnClickListener(this);
        mz_managebookmark_textview = (TextView) this.findViewById(R.id.mzmanagebookmarktextview);
        mz_managebookmark_textview.setOnClickListener(this);
        mz_managedownload_textview = (TextView) this.findViewById(R.id.mzmanagedownloadtextview);
        mz_managedownload_textview.setOnClickListener(this);

        clipUrltip();
        if (mz_url == null) {
            mz_url = mz_edittext.getText().toString();
        }
        newChildWebView(mz_url);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void switchChildWebView(int switchindex) {
        mz_childcurrentinder = switchindex;
        for (int i = 0; i < mz_webviewsum; i++) {
            if (mz_child_webview[i] != null && mz_child_textview[i] != null && i != switchindex) {
                mz_child_webview[i].setVisibility(View.GONE);
                mz_child_textview[i].setTextColor(Color.WHITE);
            }
        }
        mz_child_webview[switchindex].setVisibility(View.VISIBLE);
        mz_edittext.setText(mz_child_url[switchindex]);
        if (mz_child_textview[switchindex] != null) {
            if (Build.VERSION.SDK_INT < 23) {
                mz_child_textview[mz_childcurrentinder].setTextColor(Color.BLUE);
            } else {
                mz_child_textview[switchindex].setTextColor(getApplicationContext().getColor(R.color.currenttext));
            }
        }

        mzdb = mzSqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = mzdb.query("bookmarks", null, "url='" + mz_child_url[mz_childcurrentinder] + "'"
                , null, null, null, null);
        if (cursor.moveToFirst()) {
            mz_bookmark_imageview.setImageResource(R.drawable.bookmark2);
            mz_ischangebookmarkimage = true;
        } else {
            mz_bookmark_imageview.setImageResource(R.drawable.bookmark1);
            mz_ischangebookmarkimage = false;
        }
        cursor.close();
    }

    private int closeChildWebView() {
        mz_web_framelayout.removeView(mz_child_webview[mz_childcurrentinder]);
        mz_newtab_layout.removeView(mz_child_textview[mz_childcurrentinder]);
        mz_child_webview[mz_childcurrentinder] = null;
        mz_child_textview[mz_childcurrentinder] = null;
        mz_child_title[mz_childcurrentinder] = null;
        mz_child_url[mz_childcurrentinder] = null;

        for (int i = mz_childcurrentinder - 1; i >= 0; i--) {
            if (mz_child_webview[i] != null) {
                switchChildWebView(i);
                return i;
            }
        }

        for (int i = mz_childcurrentinder + 1; i < mz_webviewsum; i++) {
            if (mz_child_webview[i] != null) {
                switchChildWebView(i);
                return i;
            }
        }

        newChildWebView(mz_url);
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean newChildWebView(String childmurl) {
        for (int i = 0; i < mz_webviewsum; i++) {
            if (mz_child_webview[i] == null) {
                mz_childcurrentinder = i;
                break;
            }
            if (i == mz_webviewsum - 1) {
                Toast.makeText(this, "已超过最大新建页面数", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        mz_child_webview[mz_childcurrentinder] = new WebView(this);
        switchChildWebView(mz_childcurrentinder);
        mz_web_framelayout.addView(mz_child_webview[mz_childcurrentinder]);

        //设置UA
        mz_child_webview[mz_childcurrentinder].getSettings().setUserAgentString(mz_child_webview[mz_childcurrentinder].getSettings().getUserAgentString() + APP_NAME_UA);
        //设置WebView属性，能够执行Javascript脚本
        mz_child_webview[mz_childcurrentinder].getSettings().setJavaScriptEnabled(true);
        //设置WebView 可以加载更多格式页面
        mz_child_webview[mz_childcurrentinder].getSettings().setLoadWithOverviewMode(true);
        //设置WebView使用广泛的视窗
        mz_child_webview[mz_childcurrentinder].getSettings().setUseWideViewPort(true);
        //支持手势缩放
        mz_child_webview[mz_childcurrentinder].getSettings().setBuiltInZoomControls(true);
        //支持2.2以上所有版本
        mz_child_webview[mz_childcurrentinder].getSettings().setPluginState(WebSettings.PluginState.ON);
        //告诉webview启用应用程序缓存api。
        mz_child_webview[mz_childcurrentinder].getSettings().setAppCacheEnabled(true);
        //设置是否启用了DOM storage API。
        mz_child_webview[mz_childcurrentinder].getSettings().setDomStorageEnabled(true);
        //自动打开窗口
        mz_child_webview[mz_childcurrentinder].getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 没有的话会黑屏 支持插件
        mz_child_webview[mz_childcurrentinder].getSettings().setPluginsEnabled(true);

        /*
         * setAllowFileAccess 启用或禁止WebView访问文件数据 setBlockNetworkImage 是否显示网络图像
         * setBuiltInZoomControls 设置是否支持缩放 setCacheMode 设置缓冲的模式
         * setDefaultFontSize 设置默认的字体大小 setDefaultTextEncodingName 设置在解码时使用的默认编码
         * setFixedFontFamily 设置固定使用的字体 setJavaSciptEnabled 设置是否支持Javascript
         * setLayoutAlgorithm 设置布局方式 setLightTouchEnabled 设置用鼠标激活被选项
         * setSupportZoom 设置是否支持变焦
         * */
        mz_child_webview[mz_childcurrentinder].getSettings().setAllowFileAccess(true);
        mz_child_webview[mz_childcurrentinder].getSettings().setBuiltInZoomControls(true);
        mz_child_webview[mz_childcurrentinder].getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mz_child_webview[mz_childcurrentinder].getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        mz_child_webview[mz_childcurrentinder].loadUrl(childmurl);

        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();

        mz_child_webview[mz_childcurrentinder].setWebChromeClient(new WebChromeClient() {
            // 一个回调接口使用的主机应用程序通知当前页面的自定义视图已被撤职
            IX5WebChromeClient.CustomViewCallback customViewCallback;

            @Override
            public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
                return super.onJsConfirm(webView, s, s1, jsResult);
            }

            // 进入全屏的时候
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
                // 赋值给callback
                customViewCallback = callback;
                // 设置webView隐藏
                mz_web_framelayout.setVisibility(View.GONE);
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
                mz_web_framelayout.setVisibility(View.VISIBLE);
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
        mz_child_webview[mz_childcurrentinder].setWebViewClient(new WebViewClient() {
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
                mz_newtab_layout.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                mz_child_url[mz_childcurrentinder] = mz_child_webview[mz_childcurrentinder].getUrl();
                mz_child_title[mz_childcurrentinder] = mz_child_webview[mz_childcurrentinder].getTitle();
                mz_edittext.setText(mz_child_url[mz_childcurrentinder]);
                if (mz_child_textview[mz_childcurrentinder] == null) {
                    mz_child_textview[mz_childcurrentinder] = new TextView(getApplicationContext());
                    mz_child_textview[mz_childcurrentinder].setTextSize(20);
                    mz_child_textview[mz_childcurrentinder].setMaxLines(1);
                    mz_child_textview[mz_childcurrentinder].setGravity(Gravity.CENTER);
                    mz_child_textview[mz_childcurrentinder].setOnClickListener(MainActivity.this);
                    mz_newtab_layout.addView(mz_child_textview[mz_childcurrentinder]);
                }
                mz_child_textview[mz_childcurrentinder].setText(mz_child_title[mz_childcurrentinder]);
                switchChildWebView(mz_childcurrentinder);

                if (mz_llayout1place == null) {
                    mz_llayout1place = new int[]{mz_llayout1.getTop(), mz_llayout1.getBottom(),
                            mz_tool_layout.getTop(), mz_tool_layout.getBottom()};
                }

                mz_llayout1.setTop(mz_llayout1place[0]);
                mz_llayout1.setBottom(mz_llayout1place[1]);


                addJsinfobar(mz_child_webview[mz_childcurrentinder], mz_llayout1place[1]);
                mz_imageview.clearAnimation();
            }
        });

        mz_child_webview[mz_childcurrentinder].setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mz_newtab_layout.setVisibility(View.GONE);
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
                    }
                }
            }
        });

        mz_child_webview[mz_childcurrentinder].setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                Log.e("test", "onDownloadStart" + "s:" + s + ",s1:" + s1 + ",s2:" + s2 + ",s3:" + s3 + ",l:" + l);
                mz_child_webview[mz_childcurrentinder].loadUrl(
                        "javascript:" + "var r=confirm('下载链接：" + s + "\n文件大小" + (l / 1024 / 1024) + "MB');" +
                                "if (r==true)" +
                                "{" +
                                "mz_javascript.AddToDownload('" + s + "','" + l + "')" +
                                "}"
                );

                Log.e("test", "okokok");
            }
        });

        mz_child_webview[mz_childcurrentinder].addJavascriptInterface(new MzJsInterface(), "mz_javascript");
        return true;
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

    public void addJsinfobar(WebView mwebview, int height) {
        mwebview.loadUrl("javascript:" +
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FROMBOOKMARK) {
            newChildWebView(data.getStringExtra("url"));
        }
    }

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
            mz_child_webview[mz_childcurrentinder].loadUrl(mz_url);
            mz_url = this.getResources().getString(R.string.index_site);//还原配置的主页
        } else if (v == mz_edittext) {
            mz_edittext.setSelection(0, mz_edittext.getText().length());
        } else if (v == mz_back_imageview) {
            if (mz_child_webview[mz_childcurrentinder].canGoBack()) {
                mz_child_webview[mz_childcurrentinder].goBack();
            }
        } else if (v == mz_forward_imageview) {
            if (mz_child_webview[mz_childcurrentinder].canGoForward()) {
                mz_child_webview[mz_childcurrentinder].goForward();
            }
        } else if (v == mz_share_imageview) {
            shareapp();
        } else if (v == mz_newtab_textview) {
            mz_url = this.getResources().getString(R.string.index_site);
            newChildWebView(mz_url);
        } else if (v == mz_closetab_textview) {
            closeChildWebView();
        } else if (v == mz_bookmark_imageview) {
            /*if (Build.VERSION.SDK_INT > 23){
                if (mz_bookmark_imageview.getDrawable().getCurrent().getConstantState().equals
                        (getResources().getDrawable(R.drawable.bookmark1).getConstantState())) {
                    mz_bookmark_imageview.setImageResource(R.drawable.bookmark2);
                } else {
                    mz_bookmark_imageview.setImageResource(R.drawable.bookmark1);
                }
            }*/
            if (mz_ischangebookmarkimage) {
                mzdb = mzSqLiteOpenHelper.getWritableDatabase();
                String sqlstr = "delete from bookmarks where url = '" + mz_child_url[mz_childcurrentinder] + "'";
                mzdb.execSQL(sqlstr);

                mz_bookmark_imageview.setImageResource(R.drawable.bookmark1);
                mz_ischangebookmarkimage = false;
                Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
            } else {
                mzdb = mzSqLiteOpenHelper.getWritableDatabase();
                String sqlstr = "insert into bookmarks(title,url) values('" + mz_child_title[mz_childcurrentinder]
                        + "','" + mz_child_url[mz_childcurrentinder] + "')";
                mzdb.execSQL(sqlstr);

                mz_bookmark_imageview.setImageResource(R.drawable.bookmark2);
                mz_ischangebookmarkimage = true;
                Toast.makeText(this, "添加收藏", Toast.LENGTH_SHORT).show();
            }
            mz_bookmark_imageview.startAnimation(mz_bookmark_animation);
        } else if (v == mz_managebookmark_textview) {
            Intent intent = new Intent(this, SettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("page", MzFragmentLeft.TOBOOKMARK);
            startActivityForResult(intent, FROMBOOKMARK);
        } else if (v == mz_managedownload_textview) {
            Intent intent = new Intent(this, SettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("page", MzFragmentLeft.TODOWNLOAD);
            startActivity(intent);
        } else {
            for (int i = 0; i < mz_webviewsum; i++) {
                if (mz_child_textview[i] != null && v == mz_child_textview[i]) {
                    switchChildWebView(i);
                }
            }
        }
        //下面分开处理使每个点击事件都隐藏新建页面layout层，而+号键判断处理层隐藏和出现
        if (v == mz_add_imageview) {
            if (mz_newtab_layout.getVisibility() == View.GONE) {
                mz_newtab_layout.setVisibility(View.VISIBLE);
            } else if (mz_newtab_layout.getVisibility() == View.VISIBLE) {
                mz_newtab_layout.setVisibility(View.GONE);
            }
        } else {
            mz_newtab_layout.setVisibility(View.GONE);
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

    private void clipUrltip() {
        //获取判断剪切板是否有网址
        final ClipboardManager mz_clipboardmanager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData.Item mz_item;
        if (mz_clipboardmanager.hasPrimaryClip() && clipindex == 0) {
            mz_item = mz_clipboardmanager.getPrimaryClip().getItemAt(clipindex);
            clipindex++;
            final String tempurl = mz_item.getText().toString();
            if ((tempurl.startsWith("http://") || tempurl.startsWith("https://"))) {
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
                                mz_child_webview[mz_childcurrentinder].loadUrl(mz_url);
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
                                mz_child_webview[mz_childcurrentinder].loadUrl(mz_url);
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();

            }
        }
    }

    @SuppressWarnings("unused")
    private final class MzJsInterface {
        @JavascriptInterface
        public void AddToDownload(String url, String size) {
            int index = url.lastIndexOf("/");
            String fname = url.substring(index + 1, url.length());
            int fsize = Integer.parseInt(size);
            mzdb = mzSqLiteOpenHelper.getWritableDatabase();
            mzdb.execSQL("insert into downloads(fname,url,fsize,status) values('" + fname + "','" + url + "','" + size + "','" + MzSqLiteOpenHelper.DOWNLOADSTATUS_NOFINISH + "')");
        }
    }
}
