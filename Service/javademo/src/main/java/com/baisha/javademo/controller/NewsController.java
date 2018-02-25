package com.baisha.javademo.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baisha.javademo.bean.Comment;
import com.baisha.javademo.bean.CommentSecond;
import com.baisha.javademo.bean.Commentinfo;
import com.baisha.javademo.bean.Information;
import com.baisha.javademo.bean.News;
import com.baisha.javademo.bean.User;
import com.baisha.javademo.bean.Video;
import com.baisha.javademo.bean.Vote;
import com.baisha.javademo.bean.Voteinfo;
import com.baisha.javademo.dao.CommentDAO;
import com.baisha.javademo.dao.CommentSecondDAO;
import com.baisha.javademo.dao.CommentinfoDAO;
import com.baisha.javademo.dao.InformationDAO;
import com.baisha.javademo.dao.UserDAO;
import com.baisha.javademo.dao.VideoDAO;
import com.baisha.javademo.dao.VoteDAO;
import com.baisha.javademo.dao.VoteinfoDAO;
import com.baisha.javademo.util.AppConstants;
import com.baisha.javademo.util.StringUtil;

@Controller
@RestController
@RequestMapping("news")
public class NewsController {
	private static final String USER = "user";

	@Autowired
	UserDAO userDao;

	@Autowired
	CommentDAO commentDao;

	@Autowired
	CommentinfoDAO commentinfoDao;

	@Autowired
	CommentSecondDAO commentSecondDao;

	@Autowired
	VoteDAO voteDao;

	@Autowired
	VoteinfoDAO voteinfoDao;

	@Autowired
	InformationDAO informationDao;
	
	@Autowired
	VideoDAO videoDao;
	
	@RequestMapping("findAll")
	public String findAll(@RequestParam(USER) Integer userId) {
		try{
			User user = userDao.findOne(userId);
			//定义所有集合
			List<Comment> commentList = new ArrayList<>();
			List<Vote> voteList = new ArrayList<>();
			List<Commentinfo> commentinfoList = new ArrayList<>();
			List<Voteinfo> voteinfoList = new ArrayList<>();
//			List<CommentSecond> commentSecondList = new ArrayList<>();
			//说说下的评论及点赞
			List<Information> informationList = informationDao.findByUser(user);
			if (informationList != null && informationList.size() > 0) {
				Information information = null;
				for (int i = 0; i < informationList.size(); i++) {
					information = informationList.get(i);
					//说说下的评论
					List<Commentinfo> commentinfos = information.getNewCommentinfo();
					if(commentinfos != null && commentinfos.size() > 0){
						commentinfoList.addAll(commentinfos);
					}
					//说说下的点赞
					List<Voteinfo> voteinfos = information.getNewVoteinfo();
					if(voteinfos != null && voteinfos.size() > 0){
						voteinfoList.addAll(voteinfos);
					}
				}
			}

			//说说下的二次评论
			List<CommentSecond> commentSecondList = commentSecondDao.findByUReceiveAndState(user, 0);

			//视频下的评论及点赞
			List<Video> videoList = videoDao.findByUser(user);
			if(videoList != null && videoList.size() > 0){
				for (int i = 0; i < videoList.size(); i++) {
					Video video = videoList.get(i);
					//视频下的评论
					List<Comment> comments = video.getNewComment();
					if(comments != null && comments.size() > 0){
						commentList.addAll(comments);
					}
					//视频下的点赞
					List<Vote> votes = video.getNewVote();
					if(votes != null && votes.size() > 0){
						voteList.addAll(votes);
					}
				}
			}

			//合并转为新集合
			List<News> newsList = new ArrayList<>();
			News news = null;
			// comment
			if (commentList != null && commentList.size() > 0) {
				Comment comment = null;
				for (int i = 0; i < commentList.size(); i++) {
					comment = commentList.get(i);
					news = new News(0, comment.getContent(), comment.getState(), comment.getUser(), comment.getCreateTime().getTime(),
							comment.getVideoId(),videoDao.findOne(comment.getVideoId()).getUrl());

					newsList.add(news);
				}
			}
			// vote
			if (voteList != null && voteList.size() > 0) {
				Vote vote = null;
				for (int i = 0; i < voteList.size(); i++) {
					vote = voteList.get(i);
					news = new News(1, "", vote.getState(), vote.getUser(), vote.getCreateTime().getTime(), vote.getVideoId(),videoDao.findOne(vote.getVideoId()).getUrl());
					newsList.add(news);
				}
			}
			// commentinfo
			if (commentinfoList != null && commentinfoList.size() > 0) {
				Commentinfo commentinfo = null;
				for (int i = 0; i < commentinfoList.size(); i++) {
					commentinfo = commentinfoList.get(i);
					Information information = informationDao.getOne(commentinfo.getInformationId());
					String url = information.getUrl();
					if(information.getType() == 0){
						//图片
						url = StringUtil.urlSplit(url)[0];
					}else{
						//视频 
						url = StringUtil.urlSplit(url)[1];
					}
					news = new News(2, commentinfo.getContent(), commentinfo.getState(), commentinfo.getUser(), commentinfo.getCreateTime().getTime(),
							commentinfo.getInformationId(),url);
					newsList.add(news);
				}
			}
			// voteinfo
			if (voteinfoList != null && voteinfoList.size() > 0) {
				Voteinfo voteinfo = null;
				for (int i = 0; i < voteinfoList.size(); i++) {
					voteinfo = voteinfoList.get(i);
					Information information = informationDao.getOne(voteinfo.getInformationId());
					String url = information.getUrl();
					if(information.getType() == 0){
						//图片
						url = StringUtil.urlSplit(url)[0];
					}else if(information.getType() == 1){
						//视频 
						url = StringUtil.urlSplit(url)[1];
					}
					news = new News(3, "", voteinfo.getState(), voteinfo.getUser(), voteinfo.getCreateTime().getTime(), voteinfo.getInformationId(),url);
					newsList.add(news);
				}
			}
			// commentSecond
			if (commentSecondList != null && commentSecondList.size() > 0) {
				CommentSecond commentSecond = null;
				for (int i = 0; i < commentSecondList.size(); i++) {
					commentSecond = commentSecondList.get(i);
					Commentinfo commentinfo = commentinfoDao.findOne(commentSecond.getCommentinfoId());
					Information information = informationDao.getOne(commentinfo.getInformationId());
					String url = information.getUrl();
					if(information.getType() == 0){
						//图片
						url = StringUtil.urlSplit(url)[0];
					}else{
						//视频 
						url = StringUtil.urlSplit(url)[1];
					}
					
					news = new News(4, commentSecond.getContent(), commentSecond.getState(), commentSecond.getuSend(), commentSecond.getCreateTime().getTime(),
							commentSecond.getCommentinfoId(),url);
					newsList.add(news);
				}
			}
			Collections.sort(newsList, Collections.reverseOrder());
			return JSON.toJSONString(newsList,SerializerFeature.DisableCircularReferenceDetect);
		}catch(Exception e){
			e.printStackTrace();
		}
		return AppConstants.FAIL;
	}
	
	@RequestMapping("findNews")
	public String findNews(@RequestParam(USER) Integer userId) {
		try{
			User user = userDao.findOne(userId);

			//定义所有集合
			long newCommentinfoSize = 0;
			long newVoteinfoSize = 0;
			long newCommentSize = 0;
			long newVoteSize = 0;
			
			//说说下的评论及点赞
			List<Information> informationList = informationDao.findByUser(user);
			if (informationList != null && informationList.size() > 0) {
				Information information = null;
				for (int i = 0; i < informationList.size(); i++) {
					information = informationList.get(i);
					//说说下的评论
					newCommentinfoSize += information.getNewCommentinfoSize();
					//说说下的点赞
					newVoteinfoSize += information.getNewVoteinfoSize();
				}
			}
			
			//说说下的二次评论
			List<CommentSecond> commentSecondList = commentSecondDao.findByUReceiveAndState(user,0);
			long newCommentSecondSize = commentSecondList.size();
			
			//视频下的评论及点赞
			List<Video> videoList = videoDao.findByUser(user);
			System.out.println("videoList.size()--"+videoList.size());
			if(videoList != null && videoList.size() > 0){
				for (int i = 0; i < videoList.size(); i++) {
					Video video = videoList.get(i);
					//视频下的评论
					newCommentSize += video.getNewCommentSize();
					//视频下的点赞
					newVoteSize += video.getNewVoteSize();
				}
			}
			
			//评论及点赞总个数
			long newSize = newCommentSecondSize + newCommentinfoSize + newCommentSize + newVoteSize + newVoteinfoSize;
			System.out.println("新消息个数---"+newSize);
			return AppConstants.SUCCESS + ":" + newSize;
		}catch(Exception e){
			e.printStackTrace();
		}
		return AppConstants.FAIL;
	}
	
	@RequestMapping("updateNewsState")
	public @ResponseBody String updateNewsState(@RequestParam(USER)Integer userId){
		try{
			User user = userDao.findOne(userId);
			
			//说说下的评论及点赞
			List<Information> informationList = informationDao.findByUser(user);
			if (informationList != null && informationList.size() > 0) {
				Information information = null;
				for (int i = 0; i < informationList.size(); i++) {
					information = informationList.get(i);
					//说说下的评论
					information.updateCommentinfoState();
					//说说下的点赞
					information.updatevoteinfoState();
					informationDao.save(information);
				}
			}
			
			//说说下的二次评论
			List<CommentSecond> commentSecondList = commentSecondDao.findByUReceiveAndState(user,0);
			for (int i = 0; i < commentSecondList.size(); i++) {
				CommentSecond commentSecond = commentSecondList.get(i);
				commentSecond.setState(1);
				commentSecondDao.save(commentSecond);
			}
			
			//视频下的评论及点赞
			List<Video> videoList = videoDao.findByUser(user);
			if(videoList != null && videoList.size() > 0){
				Video video = null;
				for (int i = 0; i < videoList.size(); i++) {
					video = videoList.get(i);
					//视频下的评论
					video.updateCommentState();
					//视频下的点赞
					video.updatevoteinfoState();
					videoDao.save(video);
				}				
			}
			return AppConstants.SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
		}
		return AppConstants.FAIL;
	}
}
