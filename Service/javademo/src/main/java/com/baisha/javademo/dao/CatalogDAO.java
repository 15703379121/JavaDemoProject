package com.baisha.javademo.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.baisha.javademo.bean.Catalog;

public interface CatalogDAO extends JpaRepository<Catalog, Integer> {

	List<Catalog> findByProject(Integer project,Sort sort);

	List<Catalog> findByProjectAndTagAndTitle(Integer project, String tag, String title);
	
	
}
