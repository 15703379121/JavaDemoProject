package com.baisha.javademo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baisha.javademo.bean.Comment;
import com.baisha.javademo.bean.User;

public interface CommentDAO extends JpaRepository<Comment, Integer> {

	List<Comment> findByUser(User user);

	List<Comment> findByUserAndState(User user, int i);

}
