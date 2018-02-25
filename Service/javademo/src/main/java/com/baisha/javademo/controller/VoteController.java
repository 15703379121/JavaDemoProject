package com.baisha.javademo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.javademo.bean.Video;
import com.baisha.javademo.bean.Vote;
import com.baisha.javademo.dao.UserDAO;
import com.baisha.javademo.dao.VideoDAO;
import com.baisha.javademo.dao.VoteDAO;
import com.baisha.javademo.util.AppConstants;

@RestController
@RequestMapping("vote")
public class VoteController {

	private static final String VIDEO = "video";

	private static final String USER = "user";

	private static final String VOTE = "vote";

	private static final String STATE = "state";

	@Autowired
	VideoDAO videoDao;
	
	@Autowired
	UserDAO userDao;
	
	@Autowired
	VoteDAO voteDao;
	
	/**
	 * 按videoId寻找所有评论
	 * @param videoId
	 * @return
	 */
	@PostMapping("findVote")
	public String findVote(@RequestParam(VIDEO) Integer videoId){
		try{
			Video video = videoDao.findOne(videoId);
			Integer voteSize = video.getVoteSize();
			return AppConstants.SUCCESS+":"+voteSize;
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
	@PostMapping("saveVote")
	public String addVote(@RequestParam(VIDEO) Integer videoId,
	@RequestParam(USER) Integer userId){
		try{
			Video video = videoDao.findOne(videoId);
			Vote vote = new Vote(userDao.findOne(userId),videoId,0);
			video.addVote(vote);
			videoDao.save(video);
			return AppConstants.SUCCESS;
		}catch(Exception e){
			
		}
		return AppConstants.FAIL;
	}
	
	@PostMapping("deleteVote")
	public String deleteVote(@RequestParam(VIDEO) Integer videoId,
			@RequestParam(VOTE) Integer voteId){
		try{
			Video video = videoDao.findOne(videoId);
			video.removeVote(voteId);
			voteDao.delete(voteId);
			videoDao.save(video);
			return AppConstants.SUCCESS;
		}catch(Exception e){}
		return AppConstants.FAIL;
	}
	
	/**
	 * 更改赞状态
	 * @param videoId
	 * @param commentId
	 * @param state
	 * @return
	 */
	@PostMapping("updateState")
	public String updateState(@RequestParam(VIDEO) Integer videoId,
			@RequestParam(VOTE) Integer voteId,@RequestParam(STATE) Integer state){
		try{
			Video video = videoDao.findOne(videoId);
			video.updateVoteState(voteId,state);	
			videoDao.save(video);		
			return AppConstants.SUCCESS;
		}catch(Exception e){}
		return AppConstants.FAIL;
	}
	
	@PostMapping("updateVote")
	public String updateVote(@RequestParam(VIDEO) Integer videoId,@RequestParam(USER) Integer userId){
		try{
			Video video = videoDao.findOne(videoId);
			Vote findVote = video.findVoteByUser(userId);
			int state = -1;
			if(findVote != null){
				//由赞变无赞
				state = 0;
				video.removeVote(findVote.getId());
				voteDao.delete(findVote);
			}else{
				//由无赞变赞
				state = 1;
				video.addVote(new Vote(userDao.findOne(userId),videoId, 0));
			}
			videoDao.save(video);		
			return AppConstants.SUCCESS+":"+state+":"+video.getVoteSize();
		}catch(Exception e){}
		return AppConstants.FAIL;
	}
	
}
