package com.baisha.javademo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baisha.javademo.bean.User;
import com.baisha.javademo.bean.Vote;

public interface VoteDAO extends JpaRepository<Vote, Integer> {

	List<Vote> findByUser(User user);

	List<Vote> findByUserAndState(User user, int i);

}
