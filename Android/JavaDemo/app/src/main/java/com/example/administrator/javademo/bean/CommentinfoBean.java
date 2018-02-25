package com.example.administrator.javademo.bean;

import java.sql.Timestamp;
import java.util.List;

public class CommentinfoBean{

	private Integer id; // 用户的唯一标识

	private String content;
	
	private Integer state;

	private UserBean user;

//	private InformationBean information;

	private Integer informationId;

	private Integer commentSecondSize = 0; // 评论量

	private long createTime;

	private List<CommentSecondBean> commentSecond; // 评论的评论

	protected CommentinfoBean() {
		// TODO Auto-generated constructor stub
	}
	
	public CommentinfoBean(UserBean user, Integer informationId, String content,Integer state) {
		this.content = content;
		this.informationId = informationId;
		this.user = user;
		this.state = state;
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

	public Integer getCommentSecondSize() {
		return commentSecondSize;
	}

	public void setCommentSecondSize(Integer commentSecondSize) {
		this.commentSecondSize = commentSecondSize;
	}

	public List<CommentSecondBean> getCommentSecond() {
		return commentSecond;
	}

	public void setCommentSecond(List<CommentSecondBean> commentSecond) {
		this.commentSecond = commentSecond;
	}

	/**
	 * 添加评论
	 *
	 */
	public void addCommentSecond(CommentSecondBean commentSecond) {
		this.commentSecond.add(commentSecond);
		this.commentSecondSize = this.commentSecond.size();
	}

	/**
	 * 删除评论
	 *
	 */
	public void removecommentSecond(Integer commentSecondId) {
		for (int index = 0; index < this.commentSecond.size(); index++) {
			if (commentSecond.get(index).getId() == commentSecondId) {
				this.commentSecond.remove(index);
				break;
			}
		}

		this.commentSecondSize = this.commentSecond.size();
	}

	/**
	 * 查找评论
	 *
	 * @return
	 */
	public CommentSecondBean findCommentSecond(Integer commentSecondId) {
		for (int index = 0; index < this.commentSecond.size(); index++) {
			if (commentSecond.get(index).getId() == commentSecondId) {
				return commentSecond.get(index);
			}
		}
		return null;
	}

	/**
	 * 改变评论状态
	 *
	 * @param state
	 */
	public void updateCommentSecondState(Integer commentSecondId, Integer state) {
		for (int index = 0; index < this.commentSecond.size(); index++) {
			if (commentSecond.get(index).getId() == commentSecondId) {
				commentSecond.get(index).setState(state);
				break;
			}
		}
	}

}
