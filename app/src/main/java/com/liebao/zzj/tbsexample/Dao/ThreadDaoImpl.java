package com.liebao.zzj.tbsexample.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liebao.zzj.tbsexample.bean.MzThreadinfoBean;
import com.liebao.zzj.tbsexample.utils.MzSqLiteOpenHelper;

import java.util.ArrayList;

public class ThreadDaoImpl implements ThreadDao {
    MzSqLiteOpenHelper mzSqLiteOpenHelper = null;
    SQLiteDatabase db = null;

    public ThreadDaoImpl(Context context) {
        mzSqLiteOpenHelper = new MzSqLiteOpenHelper(context);
    }

    @Override
    public void insertThread(MzThreadinfoBean mzThreadinfoBean) {
        db = mzSqLiteOpenHelper.getWritableDatabase();
        db.execSQL("insert into threadinfo (id,url,startpoint,endpoint,finished) values(?,?,?,?,?)",
                new Object[]{mzThreadinfoBean.getId(), mzThreadinfoBean.getUrl(), mzThreadinfoBean.getStartpoint()
                        , mzThreadinfoBean.getEndpoint(), mzThreadinfoBean.getFinished()});
        db.close();
    }

    @Override
    public void deleteThread(String url, int thread_id) {
        db = mzSqLiteOpenHelper.getWritableDatabase();
        db.execSQL("delete from threadinfo where url=? and id=?", new Object[]{url, thread_id});
        db.close();
    }

    @Override
    public void updateThread(String url, int thread_id, int finished) {
        db = mzSqLiteOpenHelper.getWritableDatabase();
        db.execSQL("update threadinfo set finished=?  where id=? and url=? ", new Object[]{finished, thread_id, url});
        db.close();
    }

    @Override
    public ArrayList<MzThreadinfoBean> getThreads(String url) {
        ArrayList<MzThreadinfoBean> data = new ArrayList<>();
        db = mzSqLiteOpenHelper.getReadableDatabase();
        //Cursor cursor = db.query("threadinfo", null, "url='" + url + "'", null, null, null, null);
        Cursor cursor = db.rawQuery("select * from threadinfo where url = ?",new String[]{url});
        if (cursor.moveToFirst()) {
            do {
                MzThreadinfoBean mzThreadinfoBean = new MzThreadinfoBean(cursor.getInt(0), cursor.getString(1),
                        cursor.getInt(2), cursor.getInt(3), cursor.getInt(4));
                data.add(mzThreadinfoBean);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }

    @Override
    public boolean isExists(String url, int thread_id) {
        boolean b = false;
        db = mzSqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from threadinfo where url = ? and id = ?", new String[]{url, String.valueOf(thread_id)});
        if (cursor.moveToFirst()) {
            b = true;
        } else {
            b = false;
        }
        cursor.close();
        db.close();
        return b;
    }
}