package com.liebao.zzj.tbsexample.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MzOkhttpHelper {
    private Activity mz_activity;

    public MzOkhttpHelper(Activity activity) {
        this.mz_activity = activity;
    }

    boolean setImageViewBitmapByUrl(String url, final ImageView imageView) {
        if (url == null) {
            return false;
        }
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.showToast(mz_activity, "下载图片失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                final Bitmap mz_bitmap = BitmapFactory.decodeStream(inputStream);
                mz_activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(mz_bitmap);
                    }
                });
            }
        });
        return true;
    }

    public static class ToastUtil {
        public static void showToast(final Activity activity, final String message) {
            if ("main".equals(Thread.currentThread().getName())) {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public String ParseUrltofavicon(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            int index = url.indexOf("/", 9);
            url = url.substring(0, index);
            url = url + "/favicon.ico";
            return url;
        }
        return null;
    }
}