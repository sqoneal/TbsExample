package com.liebao.zzj.tbsexample.bean;

public class MzDownloadBean {
    private int id;
    private String fname;
    private String url;
    private int fsize;
    private int status;

    public MzDownloadBean(int id, String fname, String url, int fsize, int status) {
        this.id = id;
        this.fname = fname;
        this.url = url;
        this.fsize = fsize;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getUrl() {
        return url;
    }

    public int getFsize() {
        return fsize;
    }

    public int getStatus() {
        return status;
    }
}