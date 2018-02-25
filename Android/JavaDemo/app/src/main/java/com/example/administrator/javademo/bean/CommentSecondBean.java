package com.example.administrator.javademo.bean;

public class CommentSecondBean {

	private Integer id; // 用户的唯一标识

	private String content;
	
	private Integer state;

	private UserBean uSend;

	private UserBean uReceive;

//	private CommentinfoBean commentinfo;

	private Integer commentinfoId;

	private long createTime;

	public CommentSecondBean(){}

	public CommentSecondBean(String content, Integer state, UserBean uSend, UserBean uReceive,Integer commentinfoId) {
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

	public UserBean getuSend() {
		return uSend;
	}

	public void setuSend(UserBean uSend) {
		this.uSend = uSend;
	}

	public UserBean getuReceive() {
		return uReceive;
	}

	public void setuReceive(UserBean uReceive) {
		this.uReceive = uReceive;
	}

	public Integer getCommentinfoId() {
		return commentinfoId;
	}

	public void setCommentinfoId(Integer commentinfoId) {
		this.commentinfoId = commentinfoId;
	}

	public long getCreateTime() {
		return createTime;
	}
	
	
}
