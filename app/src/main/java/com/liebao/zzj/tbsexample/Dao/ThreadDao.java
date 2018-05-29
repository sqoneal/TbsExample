package com.liebao.zzj.tbsexample.Dao;

import com.liebao.zzj.tbsexample.bean.MzThreadinfoBean;

import java.util.ArrayList;

public interface ThreadDao {
    public void insertThread(MzThreadinfoBean mzThreadinfoBean);
    public void deleteThread(String url,int thread_id);
    public void updateThread(String url,int thread_id,int finished);
    public ArrayList<MzThreadinfoBean> getThreads(String url);
    public boolean isExists(String url,int thread_id);
}