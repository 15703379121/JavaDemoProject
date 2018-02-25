package com.baisha.javademo.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class UploadUtil {
	/**
	 * 
	 * 多文件具体上传时间，主要是使用了MultipartHttpServletRequest和MultipartFile
	 * 
	 * @param request
	 * 
	 * @return
	 * 
	 */
	public static String saveFiles(List<MultipartFile> files, String rootPath, String urlPath) {
		MultipartFile file = null;
		File fileRoot = new File(rootPath);
		if (!fileRoot.exists()) {
			System.out.println("没有这个文件夹");
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
					System.out.println("uploadUtil出现了异常");
					e.printStackTrace();
					return AppConstants.FAIL;
				}
			} else {
				System.out.println("文件为空");
				return AppConstants.FAIL;
			}
		}
		return buffer.toString();
	}
}
