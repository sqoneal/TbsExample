package com.liebao.zzj.tbsexample.utils;

import android.content.Context;
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
    //private Context mz_context;

    public MzAdapter(Context context, ArrayList<MzBookmarkBean> lData) {
        mz_layoutinflater = LayoutInflater.from(context);
        this.mz_data = lData;
        //this.mz_context = context;
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
        convertView = mz_layoutinflater.inflate(R.layout.listlayout, null);
        ViewHolder vh = null;
        if (vh == null) {
            vh = new ViewHolder();
            vh.mz_imageview = (ImageView) convertView.findViewById(R.id.fg_icon_imageview);
            vh.mz_textview = (TextView) convertView.findViewById(R.id.fg_title_textview);
            vh.mz_textview.setTextColor(Color.BLACK);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.mz_imageview.setImageResource(R.mipmap.ic_launcher);
        vh.mz_textview.setText(mz_data.get(position).getTitle());
        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String murl = mz_data.get(position).getUrl();
                Intent intent = new Intent(mz_context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("url", murl);
                mz_context.setResult(MainActivity.FROMBOOKMARK, intent);
                mz_context.finish();
            }
        });*/
        return convertView;
    }

    public final class ViewHolder {
        public ImageView mz_imageview;
        public TextView mz_textview;
    }
}