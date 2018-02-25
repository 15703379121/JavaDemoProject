package com.baisha.javademo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baisha.javademo.bean.Catalog;
import com.baisha.javademo.bean.Comment;
import com.baisha.javademo.bean.Practice;
import com.baisha.javademo.bean.Video;
import com.baisha.javademo.bean.Vote;
import com.baisha.javademo.dao.CatalogDAO;
import com.baisha.javademo.dao.CommentDAO;
import com.baisha.javademo.dao.PracticeDAO;
import com.baisha.javademo.dao.VideoDAO;
import com.baisha.javademo.dao.VoteDAO;
import com.baisha.javademo.util.AppConstants;

@RestController
@RequestMapping("catalog")
public class CatalogController {
	
	@Autowired
	private CatalogDAO catalogDao;
	
	@Autowired
	private VideoDAO videoDao;
	
	@Autowired
	private PracticeDAO practiceDao;
	
	@Autowired
	private CommentDAO commentDao;
	
	@Autowired
	private VoteDAO voteDao;
	
	private static final String CATALOG = "catalog";
	private static final String PROJECT = "project";
	private static final String TAG = "tag";
	private static final String TITLE = "title";
	
	/**
	 * 按project查找目录
	 */
	@PostMapping("findCatalog")
	public List<Catalog> findCatalog(@RequestParam(PROJECT)Integer project){
		List<Catalog> list = catalogDao.findByProject(project,new Sort(Direction.ASC,TAG));
		return list;
	}
	
	/**
	 * 增加标题
	 */
	@PostMapping("saveCatalog")
	public String saveCatalog(@RequestParam(PROJECT)Integer project,
			@RequestParam(TAG)Integer tag,@RequestParam(TITLE)String title){
		if(catalogDao.save(new Catalog(project,tag,title)) != null){
			return AppConstants.SUCCESS;
		}
		return AppConstants.FAIL;
	}
	
	/**
	 * 删除标题
	 */
	@PostMapping("deleteCatalog")
	public String deleteCatalog(@RequestParam(CATALOG)String catalogJson){
		Catalog catalog = JSON.parseObject(catalogJson, new TypeReference<Catalog>() {});
		try{
			//删除相关习题
			List<Practice> practiceList = catalog.getPractice();
			for (int i = 0; i < practiceList.size(); i++) {
				practiceDao.delete(practiceList.get(i));
			}
			//删除相关视频
			List<Video> videoList = catalog.getVideo();
			for (int i = 0; i < videoList.size(); i++) {
				Video video = videoList.get(i);
				//删除相关评论
				List<Comment> commentList = video.getComment();
				for (int j = 0; j < commentList.size(); j++) {
					commentDao.delete(commentList.get(j));
				}
				//删除相关赞
				List<Vote> voteList = video.getVote();
				for (int j = 0; j < voteList.size(); j++) {
					voteDao.delete(voteList.get(j));
				}
				videoDao.delete(video);
			}
			catalogDao.delete(catalog);
		}catch(Exception e){
			System.out.println("异常-----"+e.getMessage().toString());
			return AppConstants.FAIL;
		}
		return AppConstants.SUCCESS;
	}

	
	/**
	 * 更新标题
	 */
	@PostMapping("updateCatalog")
	public String updateCatalog(@RequestParam(CATALOG)String catalogJson){
		Catalog catalog = JSON.parseObject(catalogJson, new TypeReference<Catalog>() {});
		try{
			if(catalogDao.save(catalog) != null){
				return AppConstants.SUCCESS;
			}
		}catch(Exception e){
			System.out.println("异常-----"+e.getMessage().toString());
			return AppConstants.FAIL;
		}
		return AppConstants.FAIL;
	}
	
}
