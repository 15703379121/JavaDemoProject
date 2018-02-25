package com.example.administrator.javademo.util;

import android.util.Log;

import com.example.administrator.javademo.bean.UserBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/30 0030.
 */

public class FileUtil {
    /**
     * 切割文件为合适格式
     */
    public static List<UserBean> splitFile(String filePath,String adminType) {
        List<UserBean> list = new ArrayList<>();
        try {
            InputStream instream = new FileInputStream(filePath);
            if (instream != null) {
                InputStreamReader inputreader =
                        new InputStreamReader(instream, "utf-8");
                BufferedReader buffreader =
                        new BufferedReader(inputreader);
                String line = "";
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    //读取到的每一行
                    String[] split = line.split("###");
                    if (split != null && split.length >= 3) {
                        UserBean userBean = new UserBean(split[0].trim(), split[1].trim(), split[2].trim(), adminType);
                        list.add(userBean);
                    }
                }
                LogUtil.e("读取到的大小------" + list.size());
                LogUtil.e("读取到的内容------" + list);
                instream.close();
            }
         }catch (Exception e)  {
            Log.d("TestFile", e.getMessage());
        }
        return list;
    }

    /**
     * 获取文件名
     */
    public static String getName(String fileName){
        try{

            int position = fileName.lastIndexOf(".");
            return fileName.substring(0,position);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 获取文件后缀名
     */
    public static String getSuffix(String fileName){
        int position = fileName.lastIndexOf(".");
        return fileName.substring(position);
    }

    /**
     *
     * @param oldPath String 原文件路径
     * @param newPath String 剪切后路径
     * @return boolean
     */
    public static void cutFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                oldfile.delete();
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }
    /**
     * 获取文件名
     */
    public static String getFileNameByPath(String filePath){
        int position = filePath.lastIndexOf("/");
        return filePath.substring(position+1);
    }
    /**
     * 获取文件路径
     */
    public static String getDirectoryNameByPath(String filePath){
        int position = filePath.lastIndexOf("/");
        return filePath.substring(0,position);
    }
}
