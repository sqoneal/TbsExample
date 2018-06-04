package com.liebao.zzj.tbsexample.utils;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.liebao.zzj.tbsexample.R;
import com.liebao.zzj.tbsexample.bean.MzDownloadBean;
import com.liebao.zzj.tbsexample.services.MzDownloadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class MzDownloadAdapter extends BaseAdapter {
    ArrayList<MzDownloadBean> mz_data;
    Context mz_context;
    //final DownloadTask[] downloadTask;
    public final static String download_path = Environment.getExternalStorageDirectory().getPath() + "/download/";
    final boolean[] isdownload;

    public MzDownloadAdapter(Context context, ArrayList<MzDownloadBean> data) {
        this.mz_data = data;
        this.mz_context = context;
        //downloadTask = new DownloadTask[getCount()];
        isdownload = new boolean[getCount()];
    }

    @Override
    public int getCount() {
        return mz_data.size();
    }

    @Override
    public Object getItem(int position) {
        return mz_data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        isdownload[position] = false;
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(mz_context).inflate(R.layout.downlistlayout, null);
        } else {
            view = convertView;
        }

        final ViewHolder vh = new ViewHolder();
        vh.mz_start_imageview = (ImageView) view.findViewById(R.id.mzstartimageview);
        vh.mz_fname_textview = (TextView) view.findViewById(R.id.mzfnametextview);
        vh.mz_fsize_textview = (TextView) view.findViewById(R.id.mzfsizetextview);
        vh.mz_down_pg = (ProgressBar) view.findViewById(R.id.mzdlprogressBar);
        vh.mz_down_pg.setMax(100);

        vh.mz_fname_textview.setText(mz_data.get(position).getFname());

        double d1 = new BigDecimal(((float) mz_data.get(position).getFsize() / (float) 1048576)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        vh.mz_fsize_textview.setText(d1 + "MB");

        vh.mz_start_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (downloadTask[position] == null) {
                    downloadTask[position] = new DownloadTask(vh, position);
                    downloadTask[position].execute(mz_data.get(position).getUrl(), mz_data.get(position).getFname(), String.valueOf(mz_data.get(position).getFsize()));
                }*/
                if (!isdownload[position]) {
                    vh.mz_start_imageview.setImageResource(R.drawable.pause);
                    MzDownloadBean mzDownloadBean = mz_data.get(position);
                    Intent intent = new Intent(mz_context, MzDownloadService.class);
                    intent.setAction(MzDownloadService.ACTION_START);
                    intent.putExtra("downloadbean", (Serializable) mzDownloadBean);
                    mz_context.startService(intent);
                    isdownload[position] = true;
                } else {
                    vh.mz_start_imageview.setImageResource(R.drawable.play);
                    MzDownloadBean mzDownloadBean = mz_data.get(position);
                    Intent intent = new Intent(mz_context, MzDownloadService.class);
                    intent.setAction(MzDownloadService.ACTION_STOP);
                    intent.putExtra("downloadbean", (Serializable) mzDownloadBean);
                    mz_context.startService(intent);
                    isdownload[position] = false;
                }
            }
        });

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MzDownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                    if (mz_data.get(position).getId() == intent.getIntExtra("downloadbeanid", -1)) {
                        int finished = intent.getIntExtra("finished", 0);

                        vh.mz_down_pg.setProgress(finished);
                        double d1 = new BigDecimal(((float) mz_data.get(position).getFsize() / (float) 1048576)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        double d2 = new BigDecimal((finished * d1) / (float) 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        vh.mz_fsize_textview.setText(d2 + "MB/" + d1 + "MB");

                        if (100 == finished) {
                            vh.mz_start_imageview.setImageResource(R.drawable.play);
                            isdownload[position] = false;
                        } else {
                            vh.mz_start_imageview.setImageResource(R.drawable.pause);
                            isdownload[position] = true;
                        }
                    }

                }
            }
        };
        mz_context.registerReceiver(broadcastReceiver, new IntentFilter(MzDownloadService.ACTION_UPDATE));

        return view;
    }

    public class ViewHolder {
        public ImageView mz_start_imageview;
        public TextView mz_fname_textview;
        public TextView mz_fsize_textview;
        public ProgressBar mz_down_pg;
    }

    /*“启动任务执行的输入参数分别为：url,文件名,文件大小”、“后台任务执行的进度”、“后台计算结果的类型”*/
    class DownloadTask extends AsyncTask<String, Integer, Boolean> {
        ViewHolder vh;
        int filesize = 0;
        String fname;

        public DownloadTask(ViewHolder viewHolder, int pos) {
            this.vh = viewHolder;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                MzSqLiteOpenHelper mzSqLiteOpenHelper = new MzSqLiteOpenHelper(mz_context);
                SQLiteDatabase mz_db = mzSqLiteOpenHelper.getWritableDatabase();
                mz_db.execSQL("update downloads set status='" + MzSqLiteOpenHelper.DOWNLOADSTATUS_FINISH + "' where fname='" + fname + "'");
                mz_db.close();
                mzSqLiteOpenHelper.close();
                Toast.makeText(mz_context, "完成一项下载", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mz_context, "失败一项下载", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            vh.mz_down_pg.setProgress(values[0]);
            double d1 = new BigDecimal(((float) filesize / (float) 1048576)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            double d2 = new BigDecimal((values[0] * d1) / (float) 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            vh.mz_fsize_textview.setText(d2 + "MB/" + d1 + "MB");
        }

        @Override
        protected void onCancelled() {
            Log.e("test", "onCancelled");
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(final String... params) {
            boolean result;
            fname = params[1];
            if (ContextCompat.checkSelfPermission(mz_context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //第一请求权限被取消显示的判断，一般可以不写
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mz_context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Log.i("readTosdCard", "我们需要这个权限给你提供存储服务");
                } else {
                    //2、申请权限: 参数二：权限的数组；参数三：请求码
                    ActivityCompat.requestPermissions((Activity) mz_context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
            File mzfile = new File(download_path + fname);
            URL murl;
            HttpURLConnection conn = null;
            InputStream inputStream;
            FileOutputStream fileOutputStream;
            try {
                murl = new URL(params[0]);
                // 记住使用的是HttpURLConnection类
                conn = (HttpURLConnection) murl.openConnection();
                conn.setRequestMethod("GET");
                // 如果运行超过5秒会自动失效 这是android规定
                conn.setConnectTimeout(5 * 1000);
                conn.setReadTimeout(10 * 1000);
                inputStream = conn.getInputStream();
                fileOutputStream = new FileOutputStream(mzfile);
                byte[] buf = new byte[1024];
                int count = 0;
                int length;
                filesize = parseInt(params[2]);
                while ((length = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, length);
                    count += length;
                    publishProgress((int) ((count / (float) filesize) * 100));
                }
                inputStream.close();
                fileOutputStream.close();
                result = true;
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return result;
        }
    }
}