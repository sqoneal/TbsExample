package com.liebao.zzj.tbsexample.bean;

import java.io.Serializable;

public class MzDownloadBean implements Serializable {
    private int id;
    private String fname;
    private String url;
    private int fsize;
    private int status;

    public MzDownloadBean() {
    }

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

    public void setId(int id) {
        this.id = id;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFsize(int fsize) {
        this.fsize = fsize;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "MzDownloadBean{" +
                "id=" + id +
                ", fname='" + fname + '\'' +
                ", url='" + url + '\'' +
                ", fsize=" + fsize +
                ", status=" + status +
                '}';
    }
}