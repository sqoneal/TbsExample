package com.liebao.zzj.tbsexample.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liebao.zzj.tbsexample.R;
import com.liebao.zzj.tbsexample.bean.MzBookmarkBean;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * 原文地址: http://blog.csdn.net/guolin_blog/article/details/45586553
 *
 * @author guolin
 */
public class MzDataAdapter extends BaseAdapter {

    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */
    private LruCache<String, BitmapDrawable> mMemoryCache;
    private ArrayList<MzBookmarkBean> mz_data;
    private Context mzcontext;

    public MzDataAdapter(Context context, ArrayList<MzBookmarkBean> data) {
        this.mzcontext = context;
        this.mz_data = data;
        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, BitmapDrawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, BitmapDrawable drawable) {
                if (drawable.getBitmap() != null) {
                    return drawable.getBitmap().getByteCount();
                } else {
                    return 0;
                }
                //return drawable.getBitmap().getByteCount();
            }
        };
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
    public View getView(int position, View convertView, ViewGroup parent) {
        String url = ParseUrltofavicon(mz_data.get(position).getUrl());
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(mzcontext).inflate(R.layout.listlayout, null);
        } else {
            view = convertView;
        }
        TextView tv = (TextView) view.findViewById(R.id.fg_title_textview);
        tv.setText(mz_data.get(position).getTitle());
        ImageView image = (ImageView) view.findViewById(R.id.fg_icon_imageview);
        BitmapDrawable drawable = getBitmapFromMemoryCache(url);
        if (drawable != null) {
            image.setImageDrawable(drawable);
        } else {
            BitmapWorkerTask task = new BitmapWorkerTask(image);
            task.execute(url);
        }
        return view;
    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key      LruCache的键，这里传入图片的URL地址。
     * @param drawable LruCache的值，这里传入从网络上下载的BitmapDrawable对象。
     */
    public void addBitmapToMemoryCache(String key, BitmapDrawable drawable) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, drawable);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的BitmapDrawable对象，或者null。
     */
    public BitmapDrawable getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * 异步下载图片的任务。
     *
     * @author guolin
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, BitmapDrawable> {

        private ImageView mImageView;

        public BitmapWorkerTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected BitmapDrawable doInBackground(String... params) {
            String imageUrl = params[0];
            BitmapDrawable drawable;
            // 在后台开始下载图片
            Bitmap bitmap = downloadBitmap(imageUrl);
            if (bitmap != null){
                drawable = new BitmapDrawable(mzcontext.getResources(), bitmap);
            }else{
                //若请求回来的bitmap为null，则让drawable初始化为appicon
                drawable = (BitmapDrawable) mzcontext.getDrawable(R.mipmap.ic_launcher);
            }

            addBitmapToMemoryCache(imageUrl, drawable);
            return drawable;
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            if (mImageView != null && drawable != null) {
                mImageView.setImageDrawable(drawable);
            }
        }

        /**
         * 建立HTTP请求，并获取Bitmap对象。
         *
         * @param imageUrl 图片的URL地址
         * @return 解析后的Bitmap对象
         */
        private Bitmap downloadBitmap(String imageUrl) {
            Bitmap bitmap = null;
            HttpURLConnection con = null;
            try {
                URL url = new URL(imageUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(10 * 1000);
                bitmap = BitmapFactory.decodeStream(con.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return bitmap;
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