package com.example.administrator.javademo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/19 0019.
 */

public class PictureUtil {


    /**
     * 压缩多张图片
     * @param result
     * @return
     */
    public static ArrayList compressPicList(List<String> result){
        ArrayList<String> list = new ArrayList<>();
        try{
            //压缩图片
            for (int i = 0; i < result.size(); i++) {
                //LogUtil.e("图片大小为==========压缩前"+new File(result.get(i)).length());
                //压缩后图片保存的路径

                //原图片文件
                File tempFile = new File(result.get(i));
                String picFilePath = "";
                if (tempFile.length() / 1024 > 1024) {//图片大于1M 压缩
                    //压缩图片
                    picFilePath = AppConstants.FILE_PUBLISH_PIC + File.separator + System.currentTimeMillis() + ".png";
                    compressAndGenImage(result.get(i), picFilePath, 1024, false);
                } else {
                    picFilePath = result.get(i);
                }
                File file = new File(picFilePath);//上传的图片文件
                if (!file.getParentFile().exists()) {
                    file.mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                list.add(picFilePath);
            }
        }catch (Exception e){}
        return list;
    }

    /**
     * 压缩图片
     * @param result
     * @return
     */
    public static String compressPic(String result){
        String picFilePath = "";
        try{
                //原图片文件
                File tempFile = new File(result);
                if (tempFile.length() / 1024 > 1024) {//图片大于1M 压缩
                    //压缩图片
                    picFilePath = AppConstants.FILE_PUBLISH_PIC + File.separator + System.currentTimeMillis() + ".png";
                    compressAndGenImage(result, picFilePath, 1024, false);
                } else {
                    picFilePath = result;
                }
                File file = new File(picFilePath);//上传的图片文件
                if (!file.getParentFile().exists()) {
                    file.mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }

        }catch (Exception e){

    }
        return picFilePath;
    }

    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param imgPath
     * @param outPath
     * @param maxSize     target will be compressed to be smaller than this size.(kb)
     * @param needsDelete Whether delete original file after compress
     * @throws IOException
     */
    public static void compressAndGenImage(String imgPath, String outPath, int maxSize, boolean needsDelete) throws IOException {
        compressAndGenImage(getBitmap(imgPath), outPath, maxSize);

        // Delete original file
        if (needsDelete) {
            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * Get bitmap from specified image path
     *
     * @param imgPath
     * @return
     */
    public static Bitmap getBitmap(String imgPath) {
        // Get bitmap through image path
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        // Do not compress
        newOpts.inSampleSize = 1;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(imgPath, newOpts);
    }
    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param image
     * @param outPath
     * @param maxSize target will be compressed to be smaller than this size.(kb)
     * @throws IOException
     */
    public static void compressAndGenImage(Bitmap image, String outPath, int maxSize) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 100;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while (os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
        }

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
    }
}
