package com.baisha.javademo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baisha.javademo.bean.Information;
import com.baisha.javademo.bean.User;

public interface InformationDAO extends JpaRepository<Information, Integer> {

	List<Information> findByUser(User user);
	
}
