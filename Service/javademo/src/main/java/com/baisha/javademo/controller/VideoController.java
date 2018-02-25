package com.baisha.javademo.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baisha.javademo.bean.Catalog;
import com.baisha.javademo.bean.Comment;
import com.baisha.javademo.bean.Video;
import com.baisha.javademo.bean.Vote;
import com.baisha.javademo.dao.CatalogDAO;
import com.baisha.javademo.dao.CommentDAO;
import com.baisha.javademo.dao.UserDAO;
import com.baisha.javademo.dao.VideoDAO;
import com.baisha.javademo.dao.VoteDAO;
import com.baisha.javademo.util.AppConstants;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

@RestController
@RequestMapping("video")
public class VideoController {
	
	@Autowired
	private UserDAO userDao;
	
	@Autowired
	private VideoDAO videoDao;
	
	@Autowired
	private CatalogDAO catalogDao;
	
	@Autowired
	private CommentDAO commentDao;
	
	@Autowired
	private VoteDAO voteDao;
	
	private static final String VIDEO = "video";
	private static final String TITLE = "title";
	private static final String INFO = "info";
	private static final String URL = "url";
	private static final String TID = "tid";
	private static final String CID = "cid";

	private static final String CATALOG = "catalog";
	
	/**
	 * 按cid查找视频
	 */
	@PostMapping("findVideo")
	public String findVideo(@RequestParam(CID)Integer cid){
		Catalog catalog = catalogDao.findOne(cid);
		if(catalog != null){		
			List<Video> videos = catalog.getVideo();
			return JSON.toJSONString(videos, SerializerFeature.DisableCircularReferenceDetect);
		}
		return AppConstants.FAIL;
	}

	/**
	 * 按id查找视频
	 */
	@RequestMapping("findVideoById")
	public String findVideoById(@RequestParam(VIDEO)Integer videoId){
		Video video = videoDao.findOne(videoId);
		if(video != null){		
			return JSON.toJSONString(video, SerializerFeature.DisableCircularReferenceDetect);
		}
		return AppConstants.FAIL;
	}

	/**
	 * 按id查找视频
	 */
	@RequestMapping("findVideoListById")
	public String findVideoListById(@RequestParam(VIDEO)Integer videoId){
		Video video = videoDao.findOne(videoId);
		if(video != null){		
			List<Video> videoList = catalogDao.getOne(videoDao.getOne(videoId).getCatalogId()).getVideo();
			return JSON.toJSONString(videoList, SerializerFeature.DisableCircularReferenceDetect);
		}
		return AppConstants.FAIL;
	}
	
	/**
	 * 上传视频 
	 */
	@PostMapping("saveVideo")
	public String saveVideo(@RequestParam(TITLE)String title,
			@RequestParam(INFO)String info,
			@RequestParam(URL)String url,@RequestParam(CID)Integer cid,
			@RequestParam(TID)Integer tid){
		System.out.println("saveVideo---"+title+";"+info+";"+url+";"+cid+";"+tid+";");
		try{
			Catalog catalog = catalogDao.findOne(cid);
			Video video = new Video(title, info, url, userDao.findOne(tid),cid);
			catalog.addVideo(video);
			catalogDao.save(catalog);
			System.out.println("videoDao.findByUrl(url).getId()---"+videoDao.findByUrl(url).getId());
//			String videoJson = JSON.toJSONString(videoDao.findByUrl(url), SerializerFeature.DisableCircularReferenceDetect);
			jPush("校园Java"+":"+"校园Java新发布了一个视频"+":"+videoDao.findByUrl(url).getId());
			return AppConstants.SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			return AppConstants.FAIL;
		}
	}
	
	private void jPush(String alert) {
		// TODO Auto-generated method stub
		JPushClient jpushClient = new JPushClient(AppConstants.MASTER_SECRET, AppConstants.APP_KEY, null, ClientConfig.getInstance());

	    // For push, all you need do is to build PushPayload object.
//	    PushPayload payload = buildPushObject_android_tag_alertWithTitle(map);
		PushPayload payload = buildPushObject_all_alias_alert(alert);
	    try {
	    	System.out.println(payload.toString());  
	        PushResult result = jpushClient.sendPush(payload);
	        System.out.println("Got result - " + result);
//	        LOG.info();

	    } catch (APIConnectionException e) {
	        // Connection error, should retry later
	    	System.err.println("Connection error, should retry later----"+e);
//	        LOG.error();

	    } catch (APIRequestException e) {
	        // Should review the error, and fix the request
	    	e.printStackTrace();
	    	System.err.println("Should review the error, and fix the request----"+e);
	        System.out.println("HTTP Status: " + e.getStatus());
	        System.out.println("Error Code: " + e.getErrorCode());
	        System.out.println("Error Message: " + e.getErrorMessage());
//	        LOG.error("Should review the error, and fix the request", e);
//	        LOG.info("HTTP Status: " + e.getStatus());
//	        LOG.info("Error Code: " + e.getErrorCode());
//	        LOG.info("Error Message: " + e.getErrorMessage());
	    }
	}
	
	public static PushPayload buildPushObject_all_alias_alert(String alter) {  
        return PushPayload.newBuilder()  
                .setPlatform(Platform.all())//设置接受的平台  
                .setAudience(Audience.all())//Audience设置为all，说明采用广播方式推送，所有用户都可以接收到  
                .setNotification(Notification.alert(alter))  
                .build();  
    }  
	
	public static PushPayload buildPushObject_android_tag_alertWithTitle(Map<String, String> map) {
        return PushPayload.newBuilder()
        		.setPlatform(Platform.android())
                .setAudience(Audience.tag("tag1"))
                .setNotification(Notification.android("推送videoAlter", "推送videoTile", null))
                .build();
    }

	/**
	 * 删除视频
	 */
	@PostMapping("deleteVideo")
	public String deleteVideo(@RequestParam(CATALOG)Integer catalogId,@RequestParam(VIDEO)Integer videoId){
		try{
			Catalog catalog = catalogDao.findOne(catalogId);
			Video findVideo = catalog.findVideo(videoId);
			String url = findVideo.getUrl();
			if(findVideo != null){
				//删除视频文件
				new Thread(){
					public void run() {
						String[] split = url.split(AppConstants.URL_VIDEO_PATH);
						String fileName = "";
						if(split != null && split.length > 0){
							fileName = split[split.length - 1];
						}
						File file = new File(AppConstants.TOMCAT_VIDEO_PATH,fileName);  
						if(file.exists()){
							file.delete();
						}
					};
				}.start();
				catalog.removeVideo(videoId);
				catalogDao.save(catalog);
				//删除相关评论
				List<Comment> commentList = findVideo.getComment();
				for (int j = 0; j < commentList.size(); j++) {
					commentDao.delete(commentList.get(j));
				}
				//删除相关赞
				List<Vote> voteList = findVideo.getVote();
				for (int j = 0; j < voteList.size(); j++) {
					voteDao.delete(voteList.get(j));
				}
				videoDao.delete(findVideo);
				return AppConstants.SUCCESS;
			}
		}catch(Exception e){
			System.out.println("异常-----"+e.getMessage().toString());
		}
		return AppConstants.FAIL;
	}

	
	/**
	 * 更新视频信息
	 *//*
	@PostMapping("updateVideo")
	public String updateVideo(@RequestParam(VIDEO)String videoJson){
		Video video = JSON.parseObject(videoJson, new TypeReference<Video>() {});
		try{
			if(videoDao.save(video) != null){
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
