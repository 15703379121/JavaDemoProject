package com.example.administrator.javademo.bean;

/**
 * Created by Administrator on 2018/1/24 0024.
 */

public class ProjectBean {
    private int img;
    private String title;

    public ProjectBean(){}

    public ProjectBean(int img, String title) {
        this.img = img;
        this.title = title;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
