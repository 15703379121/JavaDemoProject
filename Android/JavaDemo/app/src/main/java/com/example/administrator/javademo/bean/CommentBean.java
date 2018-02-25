package com.example.administrator.javademo.bean;

import java.sql.Timestamp;

public class CommentBean {

	private Integer id; // 用户的唯一标识

	private String content;
	
	private Integer state;

	private UserBean user;

	private Long createTime;

	private Integer videoId;
 
	public CommentBean() {
		// TODO Auto-generated constructor stub
	}
	public CommentBean(UserBean user,Integer videoId, String content,Integer state) {
		this.content = content;
		this.user = user;
		this.state = state;
		this.videoId = videoId;
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

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}
 
	public Long getCreateTime() {
		return createTime;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getVideoId() {
		return videoId;
	}

	public void setVideoId(Integer videoId) {
		this.videoId = videoId;
	}

	@Override
	public String toString() {
		return "CommentBean{" +
				"id=" + id +
				", content='" + content + '\'' +
				", state=" + state +
				", user=" + user +
				", createTime=" + createTime +
				'}';
	}
}
