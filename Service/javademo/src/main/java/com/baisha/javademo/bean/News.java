package com.baisha.javademo.bean;

public class News implements Comparable<News> {

	private Integer type;//类型 0,comment; 1,vote; 2,commentinfo; 3voteinfo; 4,commentSecond
	
	private String content;
	
	private Integer state;

	private User user;

	private long createTime;

	private Integer parentId;//父id
	
	private String picUrl;
	
	public News(Integer type, String content, Integer state, User user, long createTime, Integer parentId,String picUrl) {
		super();
		this.type = type;
		this.content = content;
		this.state = state;
		this.user = user;
		this.createTime = createTime;
		this.parentId = parentId;
		this.picUrl = picUrl;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public int compareTo(News o) {
		return (int) (this.getCreateTime() - o.getCreateTime());
	}
	
}
