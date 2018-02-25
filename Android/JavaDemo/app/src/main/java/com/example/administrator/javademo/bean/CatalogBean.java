package com.example.administrator.javademo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/2/1 0001.
 */

public class CatalogBean implements Serializable{


    /**
     * id : 1
     * project : 0
     * tag : 1
     * title : java语言基础
     */

    private int id;
    private int project;
    private int tag;
    private String title;
    private List<VideoBean> video;
    private List<PracticeBean> practice;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<VideoBean> getVideo() {
        return video;
    }

    public void setVideo(List<VideoBean> video) {
        this.video = video;
    }

    public List<PracticeBean> getPractice() {
        return practice;
    }

    public void setPractice(List<PracticeBean> practice) {
        this.practice = practice;
    }

    @Override
    public String toString() {
        return "CatalogBean{" +
                "id=" + id +
                ", project=" + project +
                ", tag=" + tag +
                ", title='" + title + '\'' +
                '}';
    }

}
