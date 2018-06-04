package com.liebao.zzj.tbsexample.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.liebao.zzj.tbsexample.Dao.ThreadDao;
import com.liebao.zzj.tbsexample.Dao.ThreadDaoImpl;
import com.liebao.zzj.tbsexample.bean.MzDownloadBean;
import com.liebao.zzj.tbsexample.bean.MzThreadinfoBean;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MzDownloadTask {
    private MzDownloadBean mzDownloadBean;
    private Context mcontext;
    private ThreadDao threadDao;
    public boolean isPause = false;

    public MzDownloadTask(MzDownloadBean mzDownloadBean, Context mcontext) {
        this.mzDownloadBean = mzDownloadBean;
        this.mcontext = mcontext;
        threadDao = new ThreadDaoImpl(mcontext);
    }

    public void download() {
        //读取数据库的线程信息
        ArrayList<MzThreadinfoBean> threads = threadDao.getThreads(mzDownloadBean.getUrl());
        MzThreadinfoBean mzThreadinfoBean = null;
        if (threads.size() == 0) {
            mzThreadinfoBean = new MzThreadinfoBean(mzDownloadBean.getId(), mzDownloadBean.getUrl(), 0, mzDownloadBean.getFsize(), 0);
        } else {
            mzThreadinfoBean = threads.get(0);
        }
        new DownloadThread(mzThreadinfoBean).start();
    }


    public class DownloadThread extends Thread {
        private MzThreadinfoBean mzThreadinfoBean;

        public DownloadThread(MzThreadinfoBean mzThreadinfoBean) {
            this.mzThreadinfoBean = mzThreadinfoBean;
        }

        @Override
        public void run() {
            //For Android 7.0+
            if (ContextCompat.checkSelfPermission(mcontext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //第一请求权限被取消显示的判断，一般可以不写
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mcontext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Log.i("readTosdCard", "我们需要这个权限给你提供存储服务");
                } else {
                    //2、申请权限: 参数二：权限的数组；参数三：请求码
                    ActivityCompat.requestPermissions((Activity) mcontext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }

            //向数据库插入下载信息
            if (!threadDao.isExists(mzThreadinfoBean.getUrl(), mzThreadinfoBean.getId())) {
                threadDao.insertThread(mzThreadinfoBean);
            }
            HttpURLConnection conn = null;
            InputStream inputStream = null;
            RandomAccessFile raf = null;
            try {
                URL url = new URL(mzThreadinfoBean.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);

                //设置下载位置
                int start = mzThreadinfoBean.getStartpoint() + mzThreadinfoBean.getFinished();
                int mfinished = 0;
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mzThreadinfoBean.getEndpoint());

                //设置文件写入位置
                File file = new File(MzDownloadService.DOWNLOAD_PATH, mzDownloadBean.getFname());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);

                //开始下载
                if (conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    //读取数据
                    inputStream = conn.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    Intent intent = new Intent(MzDownloadService.ACTION_UPDATE);
                    mfinished += mzThreadinfoBean.getFinished();
                    long time = System.currentTimeMillis();
                    while ((len = inputStream.read(buffer)) != -1) {
                        //写入文件
                        raf.write(buffer, 0, len);
                        mfinished += len;
                        //广播给Activity更新完成进度
                        if ((System.currentTimeMillis() - time) > 500 || mfinished == mzDownloadBean.getFsize()) {
                            time = System.currentTimeMillis();
                            intent.putExtra("finished", mfinished * 100 / mzDownloadBean.getFsize());
                            intent.putExtra("downloadbeanid", mzDownloadBean.getId());
                            mcontext.sendBroadcast(intent);
                        }
                        //暂停下载
                        if (isPause) {
                            threadDao.updateThread(mzThreadinfoBean.getUrl(), mzThreadinfoBean.getId(), mfinished);
                            return;
                        }
                    }

                    //删除线程信息
                    threadDao.deleteThread(mzThreadinfoBean.getUrl(), mzThreadinfoBean.getId());
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    raf.close();
                    inputStream.close();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}