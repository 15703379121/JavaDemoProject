package com.baisha.javademo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baisha.javademo.bean.Practice;

public interface PracticeDAO extends JpaRepository<Practice, Integer>{

//	List<Practice> findByCatalog(Catalog catalog);

//	List<Practice> findByCid(Integer cid);
	
}
