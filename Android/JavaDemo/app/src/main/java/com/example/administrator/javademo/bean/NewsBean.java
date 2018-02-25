package com.example.administrator.javademo.bean;

public class NewsBean implements Comparable<NewsBean> {

	private Integer type;//类型 0,comment; 1,vote; 2,commentinfo; 3,voteinfo; 4,commentSecond
	
	private String content;
	
	private Integer state;

	private UserBean user;

	private long createTime;

	private Integer parentId;//父id

	private String picUrl;
	
	public NewsBean(Integer type, String content, Integer state, UserBean user, long createTime, Integer parentId,String picUrl) {
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

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
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
	
	public int compareTo(NewsBean o) {
		return (int) (this.getCreateTime() - o.getCreateTime());
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	@Override
	public String toString() {
		return "NewsBean{" +
				"type=" + type +
				", content='" + content + '\'' +
				", state=" + state +
				", user=" + user +
				", createTime=" + createTime +
				", parentId=" + parentId +
				", picUrl='" + picUrl + '\'' +
				'}';
	}
}
