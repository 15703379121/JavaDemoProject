package com.baisha.javademo.bean;

import java.util.ArrayList;
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

@Entity
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class Video {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String title;
	
	private String info;
	
	private String url;
	
	private Integer catalogId;

	 
//	@Column(name="commentSize")
	private Integer commentSize = 0;  // 评论量

//	@Column(name="voteSize")
	private Integer voteSize = 0;  // 点赞量
	
	
	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
//	@JoinTable(name = "video_comment", joinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id"), 
//		inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id"))
	private List<Comment> comment;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
//	@JoinTable(name = "video_vote", joinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id"), 
//		inverseJoinColumns = @JoinColumn(name = "vote_id", referencedColumnName = "id"))
	private List<Vote> vote;

	public Video(){};
	
	public Video(String title, String info, String url,User user,Integer catalogId) {
		super();
		this.title = title;
		this.info = info;
		this.url = url;
		this.user = user;
		this.catalogId = catalogId;
	}

	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getInfo() {
		return info;
	}


	public void setInfo(String info) {
		this.info = info;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getCommentSize() {
		return commentSize;
	}
	public void setCommentSize(Integer commentSize) {
		this.commentSize = commentSize;
	}
	public Integer getVoteSize() {
		return voteSize;
	}
	public void setVoteSize(Integer voteSize) {
		this.voteSize = voteSize;
	}
	public List<Comment> getComment() {
		return comment;
	}
	
	public void setComment(List<Comment> comment) {
		this.comment = comment;
		this.commentSize = this.comment.size();
	}
	
	public List<Vote> getVote() {
		return vote;
	}
	public void setVote(List<Vote> vote) {
		this.vote = vote;
		this.voteSize = this.vote.size();
	}	
		
	public Integer getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(Integer catalogId) {
		this.catalogId = catalogId;
	}

	/**
	 * 添加评论
	 * @param comment
	 */
	public void addComment(Comment comment) {
		this.comment.add(comment);
		this.commentSize = this.comment.size();
	}
	/**
	 * 删除评论
	 * @param comment
	 */
	public void removeComment(Integer commentId) {
		for (int index=0; index < this.comment.size(); index ++ ) {
			if (comment.get(index).getId() == commentId) {
				this.comment.remove(index);
				break;
			}
		}
		
		this.commentSize = this.comment.size();
	}
	
	/**
	 * 查找评论
	 * @param commentId
	 * @return 
	 */
	public Comment findComment(Integer commentId) {
		for (int index=0; index < this.comment.size(); index ++ ) {
			if (comment.get(index).getId() == commentId) {
				return comment.get(index);
			}
		}
		return null;		
	}
	
	/**
	 * 改变评论状态
	 * @param commentId
	 * @param state
	 */
	public void updateCommentState(Integer commentId,Integer state) {
		for (int index=0; index < this.comment.size(); index ++ ) {
			if (comment.get(index).getId() == commentId) {
				comment.get(index).setState(state);
				break;
			}
		}	
	}
	
	/**
	 * 获取新评论个数
	 * @return
	 */
	public long getNewCommentSize(){
		long count = 0;
		for (int index=0; index < this.comment.size(); index ++ ) {
			if(comment.get(index).getState() == 0){
				//新评论
				count++;
			}
		}	
		return count;
	}
	
	/**
	 * 改变评论状态
	 */
	public void updateCommentState(){
		for (int index=0; index < this.comment.size(); index ++ ) {
			if(comment.get(index).getState() == 0){
				//新评论
				comment.get(index).setState(1);
			}
		}	
	}
	
	/**
	 * 获取新评论
	 * @return
	 */
	public List<Comment> getNewComment(){
		List<Comment> list = new ArrayList<>();
		for (int index=0; index < this.comment.size(); index ++ ) {
			if(comment.get(index).getState() == 0){
				//新评论
				list.add(comment.get(index));
			}
		}	
		return list;
	}
	
	/**
	 * 点赞
	 * @param vote
	 * @return
	 */
	public boolean addVote(Vote vote) {
		boolean isExist = false;
		// 判断重复
		for (int index=0; index < this.vote.size(); index ++ ) {
			if (this.vote.get(index).getUser().getId() == vote.getUser().getId()) {
				isExist = true;
				break;
			}
		}
		
		if (!isExist) {
			this.vote.add(vote);
			this.voteSize = this.vote.size();
		}

		return isExist;
	}
	/**
	 * 取消点赞
	 * @param voteId
	 */
	public void removeVote(Integer voteId) {
		for (int index=0; index < this.vote.size(); index ++ ) {
			if (this.vote.get(index).getId() == voteId) {
				this.vote.remove(index);
				break;
			}
		}
		
		this.voteSize = this.vote.size();
	}
	
	/**
	 * 寻找点赞
	 * @param voteId
	 * @return
	 */
	public Vote findVote(Integer voteId) {
		for (int index=0; index < this.vote.size(); index ++ ) {
			if (this.vote.get(index).getId() == voteId) {
				return vote.get(index);
			}
		}
		return null;
	}
	
	/**
	 * 寻找点赞
	 * @param userId
	 * @return
	 */
	public Vote findVoteByUser(Integer userId) {
		for (int index=0; index < this.vote.size(); index ++ ) {
			if (this.vote.get(index).getUser().getId() == userId) {
				return vote.get(index);
			}
		}
		return null;
	}
	
	/**
	 * 寻找点赞
	 * @param voteId
	 * @return
	 */
	public void updateVoteState(Integer voteId,Integer state) {
		for (int index=0; index < this.vote.size(); index ++ ) {
			if (this.vote.get(index).getId() == voteId) {
				vote.get(index).setState(state);
				break;
			}
		}
	}
	
	/**
	 * 获取新评论个数
	 * @return
	 */
	public long getNewVoteSize(){
		long count = 0;
		for (int index=0; index < this.vote.size(); index ++ ) {
			if(vote.get(index).getState() == 0){
				//新评论
				count++;
			}
		}	
		return count;
	}
	
	/**
	 * 改变点赞状态
	 */
	public void updatevoteinfoState(){
		for (int index=0; index < this.vote.size(); index ++ ) {
			if(vote.get(index).getState() == 0){
				//新点赞
				vote.get(index).setState(1);
			}
		}	
	}
	
	/**
	 * 获取新点赞
	 * @return
	 */
	public List<Vote> getNewVote(){
		List<Vote> list = new ArrayList<>();
		for (int index=0; index < this.vote.size(); index ++ ) {
			if(vote.get(index).getState() == 0){
				//新点赞
				list.add(vote.get(index));
			}
		}	
		return list;
	}
	
	
}
