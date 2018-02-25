package com.baisha.javademo.bean;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity // 实体
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class CommentSecond {
	
	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private Integer id; // 用户的唯一标识

	private String content;
	
	private Integer state;
 
	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="uSend_id")
	private User uSend;
	 
	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="uReceive_id")
	private User uReceive;
	 
//	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
//	@JoinColumn(name="commentinfo_id")
//	private Commentinfo commentinfo;
	
	private Integer commentinfoId;
	
	@org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
	private Timestamp createTime;

	public CommentSecond(){}
	
	public CommentSecond(String content, Integer state, User uSend, User uReceive,Integer commentinfoId) {
		this.content = content;
		this.state = state;
		this.uSend = uSend;
		this.uReceive = uReceive;
		this.commentinfoId = commentinfoId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public User getuSend() {
		return uSend;
	}

	public void setuSend(User uSend) {
		this.uSend = uSend;
	}

	public User getuReceive() {
		return uReceive;
	}

	public void setuReceive(User uReceive) {
		this.uReceive = uReceive;
	}

	public Integer getCommentinfoId() {
		return commentinfoId;
	}

	public void setCommentinfoId(Integer commentinfoId) {
		this.commentinfoId = commentinfoId;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}
	
	
}
