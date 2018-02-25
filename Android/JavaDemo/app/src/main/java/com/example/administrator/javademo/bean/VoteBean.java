package com.example.administrator.javademo.bean;

import java.sql.Timestamp;

public class VoteBean {
	private Integer id; // 用户的唯一标识

	private UserBean user;

	private long createTime;

	private Integer state;

//	private VideoBean video;

	private Integer videoId;

	protected VoteBean() {
	}

	public VoteBean(UserBean user,Integer videoId,Integer state) {
		this.user = user;
		this.videoId = videoId;
		this.state = state;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public long getCreateTime() {
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
		return "VoteBean{" +
				"id=" + id +
				", state=" + state +
				", user=" + user +
				", createTime=" + createTime +
				'}';
	}
}
