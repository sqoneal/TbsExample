package com.liebao.zzj.tbsexample.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MzSqLiteOpenHelper extends SQLiteOpenHelper {
    public final static int MZDBVERSION = 2;
    public final static int DOWNLOADSTATUS_NOFINISH = 1;
    public final static int DOWNLOADSTATUS_FINISH = 2;

    public MzSqLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "mz.db", null, MZDBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bookmarks(id INTEGER PRIMARY KEY AUTOINCREMENT,title VARCHAR(100),url VARCHAR(100))");
        db.execSQL("CREATE TABLE downloads(id INTEGER PRIMARY KEY AUTOINCREMENT,fname VARCHAR(100),url VARCHAR(300),fsize int(10),status int(3))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1){
            db.execSQL("CREATE TABLE downloads(id INTEGER PRIMARY KEY AUTOINCREMENT,fname VARCHAR(100),url VARCHAR(300),fsize int(10),status int(3))");
        }
    }
}