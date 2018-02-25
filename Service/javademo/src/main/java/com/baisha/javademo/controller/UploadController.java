package com.baisha.javademo.controller;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baisha.javademo.util.AppConstants;  
  
/** 
 * <p>ClassName:     UploadController 
 * <p>Description:   TODO 
 * <p>Author         maqp 
 * <p>Version        V1.0 
 * <p>Date           2017/1/17 
 */  
@RestController  
@RequestMapping("file")  
public class UploadController {  
	/**
     * 文件上传具体实现方法（单文件上传）
     *
     * @param file
     * @return
     * 
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file,
    		@RequestParam("fileName")String fileSuffix) {
        if (!file.isEmpty()) {
            try {
                String tomcatPath = AppConstants.TOMCAT_PRACTICE_PATH;
                String fileName = System.currentTimeMillis()+fileSuffix;
            	File dest = new File(tomcatPath,fileName);
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                file.transferTo(dest);
                return AppConstants.URL_PRACTICE_PATH+fileName;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return AppConstants.FAIL;
            } catch (IOException e) {
                e.printStackTrace();
                return AppConstants.FAIL;
            }
        } else {
            return AppConstants.FAIL;
        }
    }
    /**
     * 文件上传具体实现方法（单文件上传）
     *
     * @param file
     * @return
     * 
     */
    @RequestMapping(value = "/uploadVideo", method = RequestMethod.POST)
    @ResponseBody
    public String uploadVideo(@RequestParam("file") MultipartFile file,
    		@RequestParam("fileName")String fileSuffix) {
        if (!file.isEmpty()) {
            try {
            	/*String originalFilename = file.getOriginalFilename();
            	String[] split = originalFilename.split("\\.");
                String fileName = "";
                if(split != null){
                	fileName = System.currentTimeMillis() + "." + split[(split.length-1)];
                }
                String tomcatPath = "";
*/
                /*if(AppConstants.TYPE_VIDEO.equals(type)){
                	tomcatPath = AppConstants.TOMCAT_VIDEO_PATH;
                }else{
                	tomcatPath = AppConstants.TOMCAT_PRACTICE_PATH;
                }*/
            	String tomcatPath = AppConstants.TOMCAT_VIDEO_PATH;
                String fileName = System.currentTimeMillis()+fileSuffix;
            	File dest = new File(tomcatPath,fileName);
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
//                file.transferTo(dest);
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(dest));
                System.out.println(file.getBytes());
                out.write(file.getBytes());
                out.flush();
                out.close();

                return AppConstants.URL_VIDEO_PATH+fileName;
                /*if(AppConstants.TYPE_VIDEO.equals(type)){
                    return AppConstants.URL_VIDEO_PATH+filePath;
                }else{
                    return AppConstants.URL_PRACTICE_PATH+filePath;
                }*/
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return AppConstants.FAIL;
            } catch (IOException e) {
                e.printStackTrace();
                return AppConstants.FAIL;
            }
        } else {
            return AppConstants.FAIL;
        }
    }
    
    /**
	 * Web
	 * @param response
	 * @param integrationId
	 * @return
	 */
    @RequestMapping("download")
	public File getAttendFile(HttpServletResponse response) {
		
		File file = new File(AppConstants.TOMCAT_PRACTICE_PATH,"1517915238631.docx");
		if (file.exists()) { // 判断文件父目录是否存在
			byte[] buffer = new byte[1024];
			FileInputStream fis = null; // 文件输入流
			BufferedInputStream bis = null;
			OutputStream os = null; // 输出流
			try {
				os = response.getOutputStream();
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				int i = bis.read(buffer);
				while (i != -1) {
					os.write(buffer);
					i = bis.read(buffer);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				bis.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
    /**
	 * Web
	 * @param response
	 * @param integrationId
	 * @return
	 */
    @RequestMapping("down")
	public @ResponseBody File getAttendFiles(HttpServletResponse response) {
		
		File file = new File(AppConstants.TOMCAT_PRACTICE_PATH,"1517915238631.docx");
		/*if (file.exists()) { // 判断文件父目录是否存在
			byte[] buffer = new byte[1024];
			FileInputStream fis = null; // 文件输入流
			BufferedInputStream bis = null;
			OutputStream os = null; // 输出流
			try {
				os = response.getOutputStream();
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				int i = bis.read(buffer);
				while (i != -1) {
					os.write(buffer);
					i = bis.read(buffer);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				bis.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		return file;
	}
}  