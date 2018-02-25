package com.example.administrator.javademo.bean;

import java.sql.Timestamp;


public class VoteinfoBean {

	private Integer id; // 用户的唯一标识

	private UserBean user;

	private long createTime;
 
	private Integer state;

//	private InformationBean information;

	private Integer informationId;

	protected VoteinfoBean() {
	}

	public VoteinfoBean(UserBean user,Integer informationId,Integer state) {
		this.user = user;
		this.informationId = informationId;
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

	public Integer getInformationId() {
		return informationId;
	}

	public void setInformationId(Integer informationId) {
		this.informationId = informationId;
	}
}
