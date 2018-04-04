package com.liebao.zzj.tbsexample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final int WEBVIEWGOBACK = 0X123;
    private static final String APP_NAME_UA = " XiaoMi/MiuiBrowser/Zcom/1.0";

    EditText mz_edittext;
    WebView mz_tbs_webview;
    Button mz_button;
    String mz_url;


    Handler mz_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WEBVIEWGOBACK:
                    if (mz_tbs_webview.canGoBack()){
                        mz_tbs_webview.goBack();
                        mz_edittext.setText(mz_tbs_webview.getUrl());
                    }else{
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

    public void initview(){
        mz_button = (Button) this.findViewById(R.id.mzButton);
        mz_edittext = (EditText) this.findViewById(R.id.mzEditText);
        mz_tbs_webview = (WebView) this.findViewById(R.id.mzTSBWebView);
        mz_tbs_webview.getSettings().setUserAgentString(mz_tbs_webview.getSettings().getUserAgentString()+APP_NAME_UA);
        //mz_tbs_webview.getSettings().setUserAgentString(APP_NAME_UA);
        mz_button.setOnClickListener(this);
        mz_url = mz_edittext.getText().toString();
        mz_tbs_webview.getSettings().setJavaScriptEnabled(true);
        mz_tbs_webview.loadUrl(mz_url);
        mz_tbs_webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                return super.shouldOverrideUrlLoading(webView, s);
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                mz_edittext.setText(mz_tbs_webview.getUrl());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mz_button){
            mz_url = mz_edittext.getText().toString();
            if (mz_url == null){
                mz_url = this.getResources().getString(R.string.index_site);
            }
            if (!(mz_url.startsWith("http://")||mz_url.startsWith("https://"))){
                mz_url = "http://" + mz_url;
            }
            mz_tbs_webview.loadUrl(mz_url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            mz_handler.sendEmptyMessage(WEBVIEWGOBACK);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
