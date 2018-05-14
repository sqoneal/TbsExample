package com.liebao.zzj.tbsexample;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.liebao.zzj.tbsexample.bean.MzBookmarkBean;
import com.liebao.zzj.tbsexample.utils.MzAdapter;
import com.liebao.zzj.tbsexample.utils.MzSqLiteOpenHelper;

import java.util.ArrayList;

public class MzFgBookmark extends Fragment {
    private MzAdapter mzAdapter;
    private ListView mz_listview;
    MzBookmarkBean mzBookmarkBean;
    private ArrayList<MzBookmarkBean> mz_data;
    private MzSqLiteOpenHelper mzSqLiteOpenHelper;
    private SQLiteDatabase mzdb;
    private Cursor cursor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_bookmark, container, false);
        mz_listview = (ListView) view.findViewById(R.id.mzfgbookmarklistview);
        mz_data = new ArrayList<>();
        mzSqLiteOpenHelper = new MzSqLiteOpenHelper(getActivity().getApplication(), "mz.db", null, 1);
        mzdb = mzSqLiteOpenHelper.getReadableDatabase();
        cursor = mzdb.query("bookmarks", null, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            mzBookmarkBean = new MzBookmarkBean(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            mz_data.add(mzBookmarkBean);
        }
        mzAdapter = new MzAdapter(getActivity(), mz_data);
        mz_listview.setAdapter(mzAdapter);

        mz_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String murl = mz_data.get(position).getUrl();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("url", murl);
                getActivity().setResult(MainActivity.FROMBOOKMARK, intent);
                getActivity().finish();
            }
        });
        mz_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                        .setTitle("是否删除收藏？")//设置对话框的标题
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
                                mzdb = mzSqLiteOpenHelper.getWritableDatabase();
                                String sqlstr = "delete from bookmarks where id = '" + mz_data.get(position).getId() + "'";
                                mzdb.execSQL(sqlstr);
                                mz_data.remove(position);
                                mz_listview.deferNotifyDataSetChanged();
                                mz_listview.setSelection(position);
                                Toast.makeText(getActivity(), "已删除收藏", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
                return true;
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        cursor.close();
        mzdb.close();
        mzSqLiteOpenHelper.close();
        super.onDetach();
    }
}