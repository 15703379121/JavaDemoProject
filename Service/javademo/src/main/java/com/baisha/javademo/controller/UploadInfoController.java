package com.baisha.javademo.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baisha.javademo.util.AppConstants;

@RestController
@RequestMapping("fileInfo")
public class UploadInfoController {
	
	/**
	 * 上传多张图片
	 * @param request
	 * @param files
	 * @return
	 */
	@RequestMapping(value = "/multiPic", method = RequestMethod.POST)
	@ResponseBody 
	public String multiPic(HttpServletRequest request, @RequestParam("files") List<MultipartFile> files) {
		String rootPath = AppConstants.TOMCAT_PUBLISH_PIC_PATH;
		String urlPath = AppConstants.URL_PUBLISH_PIC_PATH;
		return saveFiles(files, rootPath, urlPath);
	}
	
	/**
	 * 上传视频和图片
	 * @param request
	 * @param files
	 * @return
	 */
	@RequestMapping(value = "/multiVideo", method = RequestMethod.POST)
	@ResponseBody 
	public String multiVideo(HttpServletRequest request, @RequestParam("files") List<MultipartFile> files) {
		String rootPath = AppConstants.TOMCAT_PUBLISH_VIDEO_PATH;
		String urlPath = AppConstants.URL_PUBLISH_VIDEO_PATH;
		return saveFiles(files, rootPath, urlPath);
	}
	
	/**
	 * 
	 * 多文件具体上传时间，主要是使用了MultipartHttpServletRequest和MultipartFile
	 * 
	 * @param request
	 * 
	 * @return
	 * 
	 */
	private String saveFiles(List<MultipartFile> files, String rootPath, String urlPath) {
		MultipartFile file = null;
		File fileRoot = new File(rootPath);
		if (!fileRoot.exists()) {
			fileRoot.mkdirs();
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < files.size(); ++i) {
			file = files.get(i);
			if (!file.isEmpty()) {
				try {
					String originalFilename = file.getOriginalFilename();
					String[] split = originalFilename.split("\\.");
					String fileName = "";
					if (split != null) {
						fileName = System.currentTimeMillis() + "." + split[(split.length - 1)];
						buffer.append(urlPath + fileName + ";");
					}
					File dest = new File(rootPath, fileName);
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
					System.out.println(file.getBytes());
					out.write(file.getBytes());
					out.flush();
					out.close();
				} catch (Exception e) {
					return AppConstants.FAIL;
				}
			} else {
				return AppConstants.FAIL;
			}
		}
		return buffer.toString();
	}

	/**
	 * 单文件上传
	 * 
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public String uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName) {
		if (!file.isEmpty()) {
			try {
				String rootPath = AppConstants.TOMCAT_PUBLISH_VIDEO_PATH;
				String originalFilename = file.getOriginalFilename();
				String[] split = originalFilename.split("\\.");
				String filePath = "";
				if (split != null) {
					filePath = System.currentTimeMillis() + "." + split[(split.length - 1)];
				}
				File dest = new File(rootPath, filePath);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
				out.write(file.getBytes());
				out.flush();
				out.close();

				return AppConstants.URL_PUBLISH_VIDEO_PATH + filePath;
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


}
