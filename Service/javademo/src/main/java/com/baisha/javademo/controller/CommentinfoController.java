package com.baisha.javademo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baisha.javademo.bean.Commentinfo;
import com.baisha.javademo.bean.Information;
import com.baisha.javademo.dao.CommentinfoDAO;
import com.baisha.javademo.dao.InformationDAO;
import com.baisha.javademo.dao.UserDAO;
import com.baisha.javademo.util.AppConstants;

@RestController
@RequestMapping("commentinfo")
public class CommentinfoController {
	
	@Autowired
	InformationDAO informationDao;
	
	@Autowired
	UserDAO userDao;
	
	@Autowired
	CommentinfoDAO commentinfoDao;
	
	private static final String INFORMATION = "information";

	private static final String USER = "user";

	private static final String COMMENTINFO = "commentinfo";

	private static final String INFO = "info";
	
	/**
	 * 查找评论
	 * @param informationId
	 * @return
	 */
	@PostMapping("findCommentinfo")
	public String findCommentinfo(@RequestParam(INFORMATION)Integer informationId){
		try{
			Information information = informationDao.findOne(informationId);
			List<Commentinfo> list = information.getCommentinfo();
			return JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
		}catch(Exception e){
			e.printStackTrace();
		}
		return AppConstants.FAIL;
	}
	
	@PostMapping("saveCommentinfo")
	public String saveCommentinfo(@RequestParam(INFORMATION)Integer informationId,
			@RequestParam(USER)Integer userId,@RequestParam(INFO)String info){
		try{
			Information information = informationDao.getOne(informationId);
			Commentinfo commentinfo = new Commentinfo(userDao.getOne(userId), informationId, info, 0);
			information.addCommentinfo(commentinfo);
			informationDao.save(information);
			return AppConstants.SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
		}
		return AppConstants.FAIL;
	}
	
	/**
	 * 删除评论
	 * @param informationId
	 * @param commentinfoId
	 * @return
	 */
	@PostMapping("deleteCommentinfo")
	public String deleteCommentinfo(@RequestParam(INFORMATION)Integer informationId,
			@RequestParam(COMMENTINFO)Integer commentinfoId){
		try{
			Information information = informationDao.findOne(informationId);
			information.removeCommentinfo(commentinfoId);
			commentinfoDao.delete(commentinfoId);
			informationDao.save(information);
			return AppConstants.SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
		}
		return AppConstants.FAIL;
	}
}
