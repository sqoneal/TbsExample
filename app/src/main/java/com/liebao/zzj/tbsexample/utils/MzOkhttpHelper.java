package com.liebao.zzj.tbsexample.utils;

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MzOkhttpHelper {

    boolean DownloadFileByUrl(String url) {
        if (url == null) {
            return false;
        }
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("test", "download failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileInputStream fileInputStream = (FileInputStream) response.body().byteStream();

            }
        });
        return true;
    }
}