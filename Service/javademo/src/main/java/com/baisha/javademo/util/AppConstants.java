package com.baisha.javademo.util;

/**
 * Created by Administrator on 2018/1/24 0024.
 */

public class AppConstants {

    /*************关于返回值************************/
    public static final String SUCCESS = "success";//返回成功
    public static final String FAIL = "fail";//返回失败
    public static final String FILE_EXISTS = "file is exists";//返回文件已存在

    /*************关于存储路径************************/
    public static final String TOMCAT_ROOT_PATH = "D://Program_Files/Tomcat/apache-tomcat-7.0.82/webapps/ROOT/javademo/upload/";
    public static final String TOMCAT_VIDEO_PATH = TOMCAT_ROOT_PATH + "video";
    public static final String TOMCAT_PRACTICE_PATH = TOMCAT_ROOT_PATH + "practice";
    public static final String TOMCAT_PUBLISH_PATH = TOMCAT_ROOT_PATH + "publish/";
    public static final String TOMCAT_PUBLISH_VIDEO_PATH = TOMCAT_PUBLISH_PATH + "video";
    public static final String TOMCAT_PUBLISH_PIC_PATH = TOMCAT_PUBLISH_PATH + "pic";
    


    /*************关于返回url路径************************/
    public static final String URL_ROOT_PATH = "http://192.168.43.248:8080/javademo/upload/";
    public static final String URL_VIDEO_PATH = URL_ROOT_PATH + "video/";
    public static final String URL_PRACTICE_PATH = URL_ROOT_PATH + "practice/";
    public static final String URL_PUBLISH_PATH = URL_ROOT_PATH + "publish/";
    public static final String URL_PUBLISH_VIDEO_PATH = URL_PUBLISH_PATH + "video/";
    public static final String URL_PUBLISH_PIC_PATH = URL_PUBLISH_PATH + "pic/";


    /*************关于视频习题类型************************/
    public static final String TYPE_VIDEO = "0";//视频
    public static final String TYPE_PRACTICE = "1";//习题
    
    
    /*************极光推送************************/
	public static final String MASTER_SECRET = "2b90c61fbec8e36c15d68314";
	public static final String APP_KEY = "76765a5fc668120d0a5ff333";
    
    
    
}
