package com.baisha.javademo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baisha.javademo.bean.CommentSecond;
import com.baisha.javademo.bean.User;

public interface CommentSecondDAO extends JpaRepository<CommentSecond, Integer> {

//	List<CommentSecond> findByUser(User user);

	List<CommentSecond> findByUReceive(User user);

	List<CommentSecond> findByUReceiveAndState(User user, int i);

}
