package com.baisha.javademo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baisha.javademo.bean.CommentSecond;
import com.baisha.javademo.bean.Commentinfo;
import com.baisha.javademo.dao.CommentSecondDAO;
import com.baisha.javademo.dao.CommentinfoDAO;
import com.baisha.javademo.dao.UserDAO;
import com.baisha.javademo.util.AppConstants;

@RestController
@RequestMapping("commentSecond")
public class CommentSecondController {
	
	private static final String COMMENTINFO = "commentinfo";
	
	private static final String URECEIVE = "uReceive";

	private static final String INFO = "info";

	private static final String COMMENTSECOND = "commentSecond";

	private static final String USEND = "uSend";

	@Autowired
	CommentSecondDAO commentSecondDao;
	
	@Autowired
	CommentinfoDAO commentinfoDao;
	
	@Autowired
	UserDAO userDao;
	
	/**
	 * 查找评论
	 * @param informationId
	 * @return
	 */
	@PostMapping("findCommentSecond")
	public String findCommentSecond(@RequestParam(COMMENTINFO)Integer commentinfoId){
		try{
			Commentinfo commentinfo = commentinfoDao.findOne(commentinfoId);
			List<CommentSecond> list = commentinfo.getCommentSecond();
			return JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
		}catch(Exception e){
			e.printStackTrace();
		}
		return AppConstants.FAIL;
	}
	
	@PostMapping("saveCommentSecond")
	public String saveCommentSecond(@RequestParam(COMMENTINFO)Integer commentinfoId,
			@RequestParam(USEND)Integer uSendId,@RequestParam(URECEIVE)Integer uReceiveId,
			@RequestParam(INFO)String info){
		try{
			Commentinfo commentinfo = commentinfoDao.getOne(commentinfoId);
			CommentSecond commentSecond = new CommentSecond(info, 0,userDao.getOne(uSendId),userDao.getOne(uReceiveId),commentinfoId );
			commentinfo.addCommentSecond(commentSecond);
			commentinfoDao.save(commentinfo);
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
	@PostMapping("deleteCommentSecond")
	public String deleteCommentSecond(@RequestParam(COMMENTINFO)Integer commentinfoId,
			@RequestParam(COMMENTSECOND)Integer commentSecondId){
		try{
			Commentinfo commentinfo = commentinfoDao.findOne(commentinfoId);
			commentinfo.removecommentSecond(commentSecondId);
			commentSecondDao.delete(commentSecondId);
			commentinfoDao.save(commentinfo);
			return AppConstants.SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
		}
		return AppConstants.FAIL;
	}
}
