package com.baisha.javademo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baisha.javademo.bean.User;
import com.baisha.javademo.bean.Voteinfo;

public interface VoteinfoDAO extends JpaRepository<Voteinfo, Integer> {

	List<Voteinfo> findByUser(User user);

	List<Voteinfo> findByUserAndState(User user, int i);

}
