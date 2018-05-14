package com.liebao.zzj.tbsexample.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liebao.zzj.tbsexample.R;
import com.liebao.zzj.tbsexample.bean.MzBookmarkBean;

import java.util.ArrayList;

public class MzAdapter extends BaseAdapter {
    private LayoutInflater mz_layoutinflater;
    private ArrayList<MzBookmarkBean> mz_data;
    private Activity mz_activity;
    MzOkhttpHelper mzOkhttpHelper;

    public MzAdapter(Activity activity, ArrayList<MzBookmarkBean> lData) {
        mz_layoutinflater = LayoutInflater.from(activity);
        this.mz_data = lData;
        this.mz_activity = activity;
        mzOkhttpHelper = new MzOkhttpHelper(mz_activity);
    }

    @Override
    public int getCount() {
        return this.mz_data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder vh;
        if (rowView == null) {
            rowView = mz_layoutinflater.inflate(R.layout.listlayout, null);
            vh = new ViewHolder();
            rowView.setTag(vh);
        } else {
            vh = (ViewHolder) rowView.getTag();
        }

        vh = new ViewHolder();
        vh.mz_imageview = (ImageView) rowView.findViewById(R.id.fg_icon_imageview);
        vh.mz_textview = (TextView) rowView.findViewById(R.id.fg_title_textview);
        vh.mz_textview.setTextColor(Color.BLACK);
        vh.mz_textview.setText(mz_data.get(position).getTitle());
        mzOkhttpHelper.setImageViewBitmapByUrl(mzOkhttpHelper.ParseUrltofavicon(mz_data.get(position).getUrl()), vh.mz_imageview);

        return rowView;
    }

    public final class ViewHolder {
        public ImageView mz_imageview;
        public TextView mz_textview;
    }
}