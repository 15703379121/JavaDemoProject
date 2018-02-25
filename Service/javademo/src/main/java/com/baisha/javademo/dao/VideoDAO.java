package com.baisha.javademo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baisha.javademo.bean.User;
import com.baisha.javademo.bean.Video;

public interface VideoDAO extends JpaRepository<Video, Integer>{

	List<Video> findByUser(User user);

	Video findByUrl(String url);

//	List<Video> findByCatalog(Catalog catalog);

//	List<Video> findByCid(Integer cid, PageRequest pageRequest);

//	List<Video> findByCid(Integer cid);
	
}
