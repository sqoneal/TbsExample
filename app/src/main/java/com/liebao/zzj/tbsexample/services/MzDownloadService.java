package com.liebao.zzj.tbsexample.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.liebao.zzj.tbsexample.bean.MzDownloadBean;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class MzDownloadService extends Service {
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final int DOWNLOADINIT = 0X001;

    public MzDownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_START.equals(intent.getAction())) {
            MzDownloadBean mzDownloadBean = (MzDownloadBean) intent.getSerializableExtra("downloadbean");
            Log.e("test", mzDownloadBean.toString());
            InitDLThread initDLThread = new InitDLThread(mzDownloadBean);
            initDLThread.start();
        } else if (ACTION_STOP.equals(intent.getAction())) {
            MzDownloadBean mzDownloadBean = (MzDownloadBean) intent.getSerializableExtra("downloadbean");
            Log.e("test", mzDownloadBean.toString());
        }

        return super.onStartCommand(intent, flags, startId);
    }

    Handler mzhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DOWNLOADINIT:
                    MzDownloadBean mzDownloadBean = (MzDownloadBean) msg.obj;
                    Log.e("test1",mzDownloadBean.toString());
                    break;
            }
        }
    };

    public class InitDLThread extends Thread {
        MzDownloadBean mzDownloadBean;
        HttpURLConnection conn = null;
        RandomAccessFile raf;

        public InitDLThread() {

        }

        public InitDLThread(MzDownloadBean mzDownloadBean) {
            this.mzDownloadBean = mzDownloadBean;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(mzDownloadBean.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int length = -1;
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    length = conn.getContentLength();
                }
                if (length <= 0) {
                    return;
                }

                File dir = new File(DOWNLOAD_PATH);
                if (!dir.exists()){
                    dir.mkdir();
                }
                File mfile = new File(dir,mzDownloadBean.getFname());
                raf = new RandomAccessFile(mfile,"rwd");
                mzDownloadBean.setFsize(length);
                raf.setLength(length);
                mzhandler.obtainMessage(DOWNLOADINIT,mzDownloadBean).sendToTarget();

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    conn.disconnect();
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
