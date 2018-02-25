package com.baisha.javademo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baisha.javademo.bean.Commentinfo;
import com.baisha.javademo.bean.Information;
import com.baisha.javademo.bean.Voteinfo;
import com.baisha.javademo.dao.CommentSecondDAO;
import com.baisha.javademo.dao.CommentinfoDAO;
import com.baisha.javademo.dao.InformationDAO;
import com.baisha.javademo.dao.UserDAO;
import com.baisha.javademo.dao.VoteinfoDAO;
import com.baisha.javademo.util.AppConstants;
import com.baisha.javademo.util.StringUtil;
import com.baisha.javademo.util.UploadUtil;

@RestController
@RequestMapping("information")
public class InformationController {

	private static final String PAGE = "page";
	private static final String CREATE_TIME = "createTime";
	private static final String INFO = "info";
	private static final String TYPE = "type";
	private static final String USER = "user";
	private static final String FILES = "files";
	private static final String INFORMATION = "information";
	private static final String COMMENTINFO = "commentinfo";
	private static final String COMMENT_SECOND = "commentSecond";

	@Autowired
	InformationDAO informationDao;

	@Autowired
	UserDAO userDao;
	
	@Autowired
	CommentinfoDAO commentinfoDao;
	
	@Autowired
	CommentSecondDAO commentSecondDao;
	
	@Autowired
	VoteinfoDAO voteinfoDao;

	/**
	 * 查找说说
	 * 
	 * @return
	 */
	@RequestMapping("findInformation")
	public String findInformation(@RequestParam(PAGE) Integer page) {
		try {
			int size = 4;// 每次获取多少数据
			List<Information> findAll = informationDao.findAll(new Sort(Direction.DESC, CREATE_TIME));
			int count = findAll.size();
			int startPosition = page * size;
			int endPosition = (page + 1) * size;
			List<Information> list = new ArrayList<>();
			if (startPosition < count) {
				// 有数据
				if (endPosition > count) {
					// 只有一部分
					for (int i = startPosition; i < count; i++) {
						list.add(findAll.get(i));
					}
				} else {
					// 全部都有
					for (int i = startPosition; i < endPosition; i++) {
						list.add(findAll.get(i));
					}
				}
			}
//			return list;
			return JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
		} catch (Exception e) {
			e.printStackTrace();

		}
//		return null;
		return AppConstants.FAIL;
	}

	/**
	 * 按id查找
	 */
	@PostMapping("findInformationByComemntinfo")
	public String findInformationByComemntinfo(@RequestParam(COMMENTINFO)Integer commentinfoId){
		Information information = informationDao.findOne(commentinfoId);
		if(information != null){		
			return JSON.toJSONString(information, SerializerFeature.DisableCircularReferenceDetect);
		}
		return AppConstants.FAIL;
	}

	/**
	 * 按id查找
	 */
	@PostMapping("findInformationByComemntSecond")
	public String findInformationByComemntSecond(@RequestParam(COMMENT_SECOND)Integer commentinfoId){
		Information information = informationDao.findOne(commentinfoDao.getOne(commentinfoId).getInformationId());
		if(information != null){		
			return JSON.toJSONString(information, SerializerFeature.DisableCircularReferenceDetect);
		}
		return AppConstants.FAIL;
	}

	/**
	 * 发布说说
	 * 
	 * @return
	 */
	@PostMapping("saveInformation")
	public String saveInformation(HttpServletRequest request, @RequestParam(FILES) List<MultipartFile> files,
			@RequestParam(INFO) String info, @RequestParam(TYPE) String type, @RequestParam(USER) String userId) {
		try {
			System.out.println(files.size() + ";" + info + ";" + type + ";" + userId);
			int typeInt = Integer.parseInt(type);
			int userIdInt = Integer.parseInt(userId);
			String rootPath = "";
			String urlPath = "";
			if (typeInt == 0) {
				// 图片
				rootPath = AppConstants.TOMCAT_PUBLISH_PIC_PATH;
				urlPath = AppConstants.URL_PUBLISH_PIC_PATH;
			} else {
				// 视频
				rootPath = AppConstants.TOMCAT_PUBLISH_VIDEO_PATH;
				urlPath = AppConstants.URL_PUBLISH_VIDEO_PATH;
			}
			String url = UploadUtil.saveFiles(files, rootPath, urlPath);
			if (AppConstants.FAIL.equals(url)) {
				return AppConstants.FAIL;
			}
			Information information = new Information(info, url, typeInt, userDao.getOne(userIdInt));
			informationDao.save(information);
			return AppConstants.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("informationSava异常");
		}
		return AppConstants.FAIL;
	}

	@PostMapping("deleteInformation")
	public String deleteInformation(@RequestParam(INFORMATION) Integer informationId) {
		System.out.println("0");
		try{
			Information information = informationDao.findOne(informationId);
			if (information != null) {
				Integer type = information.getType();
				System.out.println("1");
				// 删除文件
				if (type != -1) {
					String url = information.getUrl();
					new Thread() {
						public void run() {
							if (type == 0) {
								// 图片
								StringUtil.deletePublishFile(url, AppConstants.TOMCAT_PUBLISH_PIC_PATH);
							} else if (type == 1) {
								// 视频
								StringUtil.deletePublishFile(url, AppConstants.TOMCAT_PUBLISH_VIDEO_PATH);
							}
						};
					}.start();
				}
				System.out.println("2");
				//删除相关评论
				List<Commentinfo> commentinfoList = information.getCommentinfo();
				System.out.println("2");
				for (int i = 0; i < commentinfoList.size(); i++) {
					System.out.println("3");
					commentinfoDao.delete(commentinfoList.get(i).getId());
				}
				//删除相关赞
				List<Voteinfo> voteinfoList = information.getVoteinfo();
				for (int i = 0; i < voteinfoList.size(); i++) {
					voteinfoDao.delete(voteinfoList.get(i).getId());
				}
				//删除说说
				informationDao.delete(information);
				return AppConstants.SUCCESS;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return AppConstants.FAIL;
	}
}
