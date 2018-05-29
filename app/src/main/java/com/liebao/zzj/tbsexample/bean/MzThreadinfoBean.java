package com.liebao.zzj.tbsexample.bean;

public class MzThreadinfoBean {
    private int id;
    private String url;
    private int startpoint;
    private int endpoint;
    private int finished;

    public MzThreadinfoBean(int id, String url, int startpoint, int endpoint, int finished) {
        this.id = id;
        this.url = url;
        this.startpoint = startpoint;
        this.endpoint = endpoint;
        this.finished = finished;
    }

    public MzThreadinfoBean() {
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public int getStartpoint() {
        return startpoint;
    }

    public int getEndpoint() {
        return endpoint;
    }

    public int getFinished() {
        return finished;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setStartpoint(int startpoint) {
        this.startpoint = startpoint;
    }

    public void setEndpoint(int endpoint) {
        this.endpoint = endpoint;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }
}