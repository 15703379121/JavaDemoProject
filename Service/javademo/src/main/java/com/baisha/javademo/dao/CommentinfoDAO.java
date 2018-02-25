package com.baisha.javademo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baisha.javademo.bean.Commentinfo;
import com.baisha.javademo.bean.User;

public interface CommentinfoDAO extends JpaRepository<Commentinfo, Integer> {

	List<Commentinfo> findByUser(User user);

	List<Commentinfo> findByUserAndState(User user, int i);

}
