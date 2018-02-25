package com.baisha.javademo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baisha.javademo.bean.Comment;
import com.baisha.javademo.bean.Video;
import com.baisha.javademo.dao.CommentDAO;
import com.baisha.javademo.dao.UserDAO;
import com.baisha.javademo.dao.VideoDAO;
import com.baisha.javademo.util.AppConstants;

@RestController
@RequestMapping("comment")
public class CommentController {

	private static final String VIDEO = "video";

	private static final String USER = "user";

	private static final String CONTENT = "content";

	private static final String COMMENT = "comment";

	private static final String STATE = "state";

	@Autowired
	VideoDAO videoDao;
	
	@Autowired
	UserDAO userDao;
	
	@Autowired
	CommentDAO commentDao;
	
	/**
	 * 按videoId寻找所有评论
	 * @param videoId
	 * @return
	 */
	@PostMapping("findComment")
	public String findComment(@RequestParam(VIDEO) Integer videoId){
		try{
			Video video = videoDao.findOne(videoId);
			List<Comment> comments = video.getComment();
			return JSON.toJSONString(comments, SerializerFeature.DisableCircularReferenceDetect);
		}catch(Exception e){
			
		}
		return AppConstants.FAIL;
	}
	
	/** 
	 * 为videoId添加评论
	 * @param videoId
	 * @param userId
	 * @param content
	 * @return
	 */
	@PostMapping("saveComment")
	public String addComment(@RequestParam(VIDEO) Integer videoId,
	@RequestParam(USER) Integer userId,@RequestParam(CONTENT)String content){
		try{
			Video video = videoDao.findOne(videoId);
			Comment comment = new Comment(userDao.findOne(userId),videoId, content,0);
			video.addComment(comment);
			videoDao.save(video);
			return AppConstants.SUCCESS;
		}catch(Exception e){
			
		}
		return AppConstants.FAIL;
	}
	
	/**
	 * 删除评论
	 * @param videoId
	 * @param commentId
	 * @return
	 */
	@PostMapping("deleteComment")
	public String deleteComment(@RequestParam(VIDEO) Integer videoId,
			@RequestParam(COMMENT) Integer commentId){
		try{
			Video video = videoDao.findOne(videoId);
			video.removeComment(commentId);
			commentDao.delete(commentId);
			videoDao.save(video);
			return AppConstants.SUCCESS;
		}catch(Exception e){}
		return AppConstants.FAIL;
	}
	
	/**
	 * 更改评论状态
	 * @param videoId
	 * @param commentId
	 * @param state
	 * @return
	 */
	@PostMapping("updateState")
	public String updateState(@RequestParam(VIDEO) Integer videoId,
			@RequestParam(COMMENT) Integer commentId,@RequestParam(STATE) Integer state){
		try{
			Video video = videoDao.findOne(videoId);
			video.updateCommentState(commentId,state);	
			videoDao.save(video);		
			return AppConstants.SUCCESS;
		}catch(Exception e){}
		return AppConstants.FAIL;
	}
}
