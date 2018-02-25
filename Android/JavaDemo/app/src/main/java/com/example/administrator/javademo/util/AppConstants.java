package com.example.administrator.javademo.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2018/1/24 0024.
 */

public class AppConstants {

    /*************关于用户类型************************/
    public static final String USER_STUDENT = "0";//学生
    public static final String USER_TEACHER = "1";//教师
    public static final String USER_ADMIN = "2";//教秘



    /*************关于科目类型************************/
    public static final String PROJECT_TYPE = "project_type";//java
    public static final int PROJECT_JAVA = 0;//java
    public static final int PROJECT_JSP = 1;//jsp
    public static final int PROJECT_JAVAWEB = 2;//javaweb

    /*************关于SP************************/
    public static final String SP_LOGIN = "sp_login";//用户信息
    public static final String VIDEO_DOWNLOAD_LIST = "video_download_list";//文件下载目录
    public static final String SP_CACHE_NEWS = "sp_cache_news"; //消息列表



    /*************关于存储路径************************/
    public static final String SD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"JavaDemo/";//存储根目录
    public static final String FILE_PATH = "file_path";
    public static final String FILE_TXT = SD_ROOT+"FileTxt";//可供添加的txt文件目录
    public static final String FILE_PRACTICE = SD_ROOT+"FilePractice";//可供添加的文档文件目录
    public static final String FILE_DOWN = SD_ROOT+"FileDown/";//文件下载目录
    public static final String FILE_DOWN_VIDEO = FILE_DOWN+"FileVideo";//文件下载目录
    public static final String FILE_DOWN_PRACTICE = FILE_DOWN+"FilePractice";//文件下载目录
    public static final String FILE_DOWN_VIDEO_TEMP = FILE_DOWN_VIDEO+"/Temp";//文件下载目录
    public static final String FILE_DOWN_VIDEO_THUMBNAIL = FILE_DOWN_VIDEO+"/Thumbnail";//文件下载目录
    public static final String FILE_PUBLISH = SD_ROOT+"Publish/";//文件发布目录
    public static final String FILE_PUBLISH_VIDEO = FILE_PUBLISH+"Video";//文件下载目录
    public static final String FILE_PUBLISH_PIC = FILE_PUBLISH+"Pic";//文件下载目录




    /*************关于返回值************************/
    public static final String SUCCESS = "success";//存储根目录
    public static final String FAIL = "fail";//可供添加的txt文件目录
    public static final String FILE_EXISTS = "file is exists";//返回文件已存在


    /*************关于联网************************/
    public static final long TIME_OUT = 3000;//网络请求超时时间
    public static final String SERVE_URL = "http://192.168.43.248:8088/";
    public static final String TOMCAT_URL = "http://192.168.43.248:8080/";

    /*************关于登录************************/
    public static final String USER_URL = SERVE_URL+"user/";
    public static final String USER_CHECK_LOGIN_URL = USER_URL+"checkLogin";//用户登录
    public static final String USER_SAVE_ALL_URL = USER_URL+"saveAll";//添加用户
    public static final String USER_DELETE_ALL_URL = USER_URL+"deleteAll";//删除用户
    public static final String USER_UPDATE_PASSWORD_URL = USER_URL+"updatePassword";//修改密码

    /*************关于目录************************/
    public static final String CATALOG_URL = SERVE_URL+"catalog/";
    public static final String CATALOG_FIND_URL = CATALOG_URL+"findCatalog";//查找目录
    public static final String CATALOG_SAVE_URL = CATALOG_URL+"saveCatalog";//添加目录
    public static final String CATALOG_DELETE_URL = CATALOG_URL+"deleteCatalog";//删除目录
    public static final String CATALOG_UPDATE_URL = CATALOG_URL+"updateCatalog";//更新目录

    /*************关于视频************************/
    public static final String VIDEO_URL = SERVE_URL+"video/";
    public static final String VIDEO_FIND_URL = VIDEO_URL+"findVideo";//查找视频
    public static final String VIDEO_SAVE_URL = VIDEO_URL+"saveVideo";//上传视频
    public static final String VIDEO_DELETE_URL = VIDEO_URL+"deleteVideo";//删除视频
    public static final String VIDEO_FIND_ID_URL = VIDEO_URL+"findVideoById";//删除视频
    public static final String VIDEO_FIND_LIST_URL = VIDEO_URL+"findVideoListById";//删除视频

    /*************关于练习************************/
    public static final String PRACTICE_URL = SERVE_URL+"practice/";
    public static final String PRACTICE_FIND_URL = PRACTICE_URL+"findPractice";//查找视频
    public static final String PRACTICE_SAVE_URL = PRACTICE_URL+"savePractice";//上传视频
    public static final String PRACTICE_DELETE_URL = PRACTICE_URL+"deletePractice";//删除视频

    /*************关于上传************************/
    public static final String FILE_URL = SERVE_URL+"file/";
    public static final String FILE_UPLOAD_PRACTICE_URL = FILE_URL+"uploadFile";//上传文件
    public static final String FILE_UPLOAD_VIDEO_URL = FILE_URL+"uploadVideo";//上传文件
    public static final String FILE_DOWNLOAD_URL = FILE_URL+"download";//下载文件

    /*************关于点赞************************/
    public static final String VOTE_URL = SERVE_URL+"vote/";
    public static final String VOTE_FIND_URL = VOTE_URL+"findVote";//查找赞
    public static final String VOTE_UPDATE_URL = VOTE_URL+"updateVote";//点赞或取消赞



    /*************关于评论************************/
    public static final String COMMENT_URL = SERVE_URL+"comment/";
    public static final String COMMENT_FIND_URL = COMMENT_URL+"findComment";//查找视频
    public static final String COMMENT_SAVE_URL = COMMENT_URL+"saveComment";//上传视频
    public static final String COMMENT_DELETE_URL = COMMENT_URL+"deleteComment";//删除视频



    /*************关于发布消息************************/
    public static final String INFORMATION_URL = SERVE_URL+"information/";
    public static final String INFORMATION_FIND_URL = INFORMATION_URL+"findInformation";//查找视频
    public static final String INFORMATION_SAVE_URL = INFORMATION_URL+"saveInformation";//上传视频
    public static final String INFORMATION_DELETE_URL = INFORMATION_URL+"deleteInformation";//上传视频
    public static final String INFORMATION_FIND_COMMENTINFO_URL = INFORMATION_URL+"findInformationByComemntinfo";//查找视频
    public static final String INFORMATION_FIND_COMMENT_SECOND_URL = INFORMATION_URL+"findInformationByComemntSecond";//查找视频



    /*************关于消息评论************************/
    public static final String COMMENT_INFORMATION_URL = SERVE_URL+"commentinfo/";
    public static final String COMMENT_INFORMATION_FIND_URL = COMMENT_INFORMATION_URL+"findCommentinfo";//查找视频
    public static final String COMMENT_INFORMATION_SAVE_URL = COMMENT_INFORMATION_URL+"saveCommentinfo";//上传视频
    public static final String COMMENT_INFORMATION_DELETE_URL = COMMENT_INFORMATION_URL+"deleteCommentinfo";//删除视频



    /*************关于消息赞************************/
    public static final String VOTE_INFORMATION_URL = SERVE_URL+"voteinfo/";
    public static final String VOTE_INFORMATION_FIND_URL = VOTE_INFORMATION_URL+"findVoteinfo";//查找赞
    public static final String VOTE_INFORMATION_UPDATE_URL = VOTE_INFORMATION_URL+"updateVoteinfo";//点赞或取消赞


    /*************关于二次评论************************/
    public static final String COMMENT_SECOND_URL = SERVE_URL+"commentSecond/";
    public static final String COMMENT_SECOND_FIND_URL = COMMENT_SECOND_URL+"findCommentSecond";//查找赞
    public static final String COMMENT_SECOND_SAVE_URL = COMMENT_SECOND_URL+"saveCommentSecond";//上传视频
    public static final String COMMENT_SECOND_DELETE_URL = COMMENT_SECOND_URL+"deleteCommentSecond";//删除视频


    /*************关于获取新消息************************/
    public static final String NEWS_URL = SERVE_URL+"news/";
    public static final String NEWS_FIND_LIST_URL = NEWS_URL+"findAll";
    public static final String NEWS_FIND_SIZE_URL = NEWS_URL+"findNews";
    public static final String NEWS_UPDATE_URL = NEWS_URL+"updateNewsState";

}
