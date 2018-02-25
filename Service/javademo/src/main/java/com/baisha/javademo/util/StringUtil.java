package com.baisha.javademo.util;

import java.io.File;

public class StringUtil {
	public static String[] urlSplit(String urls){
		if (urls.endsWith(";")) {
            urls = urls.substring(0, urls.length() - 1);
        }
		return urls.split(";");
	}

	public static void deletePublishFile(String url,String tomcatPath) {
		// TODO Auto-generated method stub
		String[] urlSplit = urlSplit(url);
		if (urlSplit != null && urlSplit.length > 0){
			//循环删除文件
			for (int i = 0; i < urlSplit.length; i++) {
				String fileName = getFileNameByPath(urlSplit[i]);
				File file = new File(tomcatPath,fileName); 
				if(file.exists()){
					file.delete();
				}
			}
		}
	}

    /**
     * 获取文件名
     */
    public static String getFileNameByPath(String filePath){
        int position = filePath.lastIndexOf("/");
        return filePath.substring(position+1);
    }
}
