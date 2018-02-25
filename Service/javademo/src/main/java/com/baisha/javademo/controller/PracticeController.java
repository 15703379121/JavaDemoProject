package com.baisha.javademo.controller;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baisha.javademo.bean.Catalog;
import com.baisha.javademo.bean.Practice;
import com.baisha.javademo.dao.CatalogDAO;
import com.baisha.javademo.dao.PracticeDAO;
import com.baisha.javademo.dao.UserDAO;
import com.baisha.javademo.util.AppConstants;

@RestController
@RequestMapping("practice")
public class PracticeController {
	
	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private CatalogDAO catalogDao;
	
	@Autowired
	private PracticeDAO practiceDao;
	
	private static final String PRACTICE = "practice";
	private static final String TITLE = "title";
	private static final String INFO = "info";
	private static final String URL = "url";
	private static final String TID = "tid";
	private static final String CID = "cid";

	private static final String CATALOG = "catalog";
	
	/**
	 * 按cid查找习题
	 */
	@PostMapping("findPractice")
	public String findVideo(@RequestParam(CID)Integer cid){
		try{
			Catalog catalog = catalogDao.findOne(cid);
			if(catalog != null){
				List<Practice> practices = catalog.getPractice();
				String userStr = JSON.toJSONString(practices,SerializerFeature.DisableCircularReferenceDetect);
				System.out.println("userStr--------------"+userStr);
				return userStr;
			}
		}catch(Exception e){
			
		}
		return AppConstants.FAIL;
	}
	/**
	 * 上传习题
	 */
	@PostMapping("savePractice")
	public String savePractice(@RequestParam(TITLE)String title,
			@RequestParam(INFO)String info,@RequestParam(URL)String url,
			@RequestParam(CID)Integer cid,@RequestParam(TID)Integer tid){
		try{
			Catalog catalog = catalogDao.findOne(cid);
			Practice practice = new Practice(title, info, url, userDao.findOne(tid));
			catalog.addPractice(practice);
			catalogDao.save(catalog);
			return AppConstants.SUCCESS;
		}catch(Exception e){
			return AppConstants.FAIL;
		}
	}
	
	/**
	 * 删除习题
	 */
	@PostMapping("deletePractice")
	public String deletePractice(@RequestParam(CATALOG)Integer catalogId,
			@RequestParam(PRACTICE)Integer practiceId){
		try{
			Catalog catalog = catalogDao.findOne(catalogId);
			Practice findPractice = catalog.findPractice(practiceId);
			if(findPractice != null){
				//删除习题文件
				String url = findPractice.getUrl();
				String[] split = url.split(AppConstants.URL_PRACTICE_PATH);
				String fileName = "";
				if(split != null && split.length > 0){
					fileName = split[split.length - 1];
				}
				File file = new File(AppConstants.TOMCAT_PRACTICE_PATH,fileName);  
			    if (file.delete()) {  
					catalog.removePractice(practiceId);
					catalogDao.save(catalog);
					practiceDao.delete(findPractice);
					return AppConstants.SUCCESS;
			    }
			}
		}catch(Exception e){
			System.out.println("异常-----"+e.getMessage().toString());
		}
		return AppConstants.FAIL;
	}

	
	/**
	 * 更新视频信息
	 *//*
	@PostMapping("updatePractice")
	public String updatePractice(@RequestParam(Practice)String PracticeJson){
		Practice Practice = JSON.parseObject(PracticeJson, new TypeReference<Practice>() {});
		try{
			if(PracticeDao.save(Practice) != null){
				return AppConstants.SUCCESS;
			}
		}catch(Exception e){
			System.out.println("异常-----"+e.getMessage().toString());
			return AppConstants.FAIL;
		}
		return AppConstants.FAIL;
	}
	*/
}
