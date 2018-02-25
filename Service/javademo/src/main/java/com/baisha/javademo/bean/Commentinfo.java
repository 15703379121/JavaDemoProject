package com.baisha.javademo.bean;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity // 实体
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Commentinfo {

	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private Integer id; // 用户的唯一标识

	private String content;

	private Integer state;

	private Integer commentSecondSize = 0; // 评论量

	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

//	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
//	@JoinColumn(name = "information_id")
//	private Information information;

	private Integer informationId;
	
	@org.hibernate.annotations.CreationTimestamp // 由数据库自动创建时间
	private Timestamp createTime;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<CommentSecond> commentSecond; // 评论的评论

	protected Commentinfo() {
		// TODO Auto-generated constructor stub
	}

	public Commentinfo(User user, Integer informationId, String content, Integer state) {
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Timestamp getCreateTime() {
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

	public List<CommentSecond> getCommentSecond() {
		return commentSecond;
	}

	public void setCommentSecond(List<CommentSecond> commentSecond) {
		this.commentSecond = commentSecond;
	}

	/**
	 * 添加评论
	 * 
	 * @param commentinfo
	 */
	public void addCommentSecond(CommentSecond commentSecond) {
		this.commentSecond.add(commentSecond);
		this.commentSecondSize = this.commentSecond.size();
	}

	/**
	 * 删除评论
	 * 
	 * @param commentinfo
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
	 * @param commentinfoId
	 * @return
	 */
	public CommentSecond findCommentSecond(Integer commentSecondId) {
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
	 * @param commentinfoId
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
