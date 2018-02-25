package com.baisha.javademo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baisha.javademo.bean.Information;
import com.baisha.javademo.bean.Voteinfo;
import com.baisha.javademo.dao.InformationDAO;
import com.baisha.javademo.dao.UserDAO;
import com.baisha.javademo.dao.VoteinfoDAO;
import com.baisha.javademo.util.AppConstants;

@RestController
@RequestMapping("voteinfo")
public class VoteinfoController {
	
	@Autowired
	InformationDAO informationDao;
	
	@Autowired
	VoteinfoDAO voteinfoDao;
	
	@Autowired
	UserDAO userDao;
	
	private static final String INFORMATION = "information";

	private static final String USER = "user";
	
	/**
	 * 寻找赞
	 * @param informationId
	 * @return
	 */
	@PostMapping("findVoteinfo")
	public String findVoteinfo(@RequestParam(INFORMATION)Integer informationId){
		try{
			Information information = informationDao.getOne(informationId);
			List<Voteinfo> list = information.getVoteinfo();
			return JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
		}catch(Exception e){
			e.printStackTrace();
		}
		return AppConstants.FAIL;
	}
	
	@PostMapping("updateVoteinfo")
	public String updateVoteinfo(@RequestParam(INFORMATION)Integer informationId
			,@RequestParam(USER)Integer userId){
		try{
			Information information = informationDao.getOne(informationId);
			Voteinfo voteinfo = information.findVoteinfoByUser(userId);
			int state = -1;
			if(voteinfo == null){
				//无赞变有赞 -- 添加赞
				state = 1;
				information.addVoteinfo(new Voteinfo(userDao.getOne(userId), informationId, 0));
			}else{
				//有赞变无赞 -- 删除赞
				state = 0;
				information.removeVoteinfo(voteinfo.getId());
				voteinfoDao.delete(voteinfo);
			}
			informationDao.save(information);
			return AppConstants.SUCCESS+":"+state+":"+information.getVoteinfoSize();
		}catch(Exception e){
			e.printStackTrace();
		}
		return AppConstants.FAIL;
	}
	
}
