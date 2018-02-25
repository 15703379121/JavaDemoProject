package com.baisha.javademo.dao;

import java.util.Iterator;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baisha.javademo.bean.User;

public interface UserDAO extends JpaRepository<User, Integer>{
	
	//登录---按username/password/type查寻
	public List<User> findByIdentifierAndPasswordAndType(String identifier,String password,String type);

	//批量添加用户
	public boolean save(Iterator<User> iterator);
	
	//按编号查寻
	public List<User> findByIdentifier(String identifier);
	
	//通过字段%like%查询
	public List<User> findByIdentifierStartingWith(String identifier);
		
}
