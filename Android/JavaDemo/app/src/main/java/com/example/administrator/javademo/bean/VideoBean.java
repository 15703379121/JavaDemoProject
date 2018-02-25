package com.example.administrator.javademo.bean;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class VideoBean implements Serializable {

	private Integer id;
	
	private String title;
	
	private String info;
	
	private String url;

	private UserBean user;

	private Integer commentSize;  // 评论量

	private Integer voteSize;  // 点赞量

	private List<CommentBean> comment;

	private List<VoteBean> vote;

	private Integer catalogId;

	public VideoBean(){};

	public VideoBean(String title, String info, String url,UserBean user) {
		super();
		this.title = title;
		this.info = info;
		this.url = url;
		this.user = user;
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

	public List<CommentBean> getComment() {
		return comment;
	}

	public void setComment(List<CommentBean> comments) {
		this.comment = comments;
	}

	public List<VoteBean> getVote() {
		return vote;
	}

	public void setVote(List<VoteBean> votes) {
		this.vote = vote;
	}

	public Integer getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(Integer catalogId) {
		this.catalogId = catalogId;
	}

	@Override
	public String toString() {
		return "VideoBean{" +
				"id=" + id +
				", title='" + title + '\'' +
				", info='" + info + '\'' +
				", url='" + url + '\'' +
				", user=" + user +
				", commentSize=" + commentSize +
				", voteSize=" + voteSize +
				", comment=" + comment +
				", vote=" + vote +
				'}';
	}



	/**
	 * 寻找赞
	 * @param uid
	 * @return
	 */
	public VoteBean findVoteByUser(Integer uid) {
		for (int index=0; index < this.vote.size(); index ++ ) {
			if (this.vote.get(index).getUser().getId() == uid) {
				return vote.get(index);
			}
		}
		return null;
	}
	/**
	 * 添加评论
	 * @param comment
	 */
	public void addComment(CommentBean comment) {
		this.comment.add(comment);
		this.commentSize = this.comment.size();
	}
	/**
	 * 删除评论
	 * @param commentId
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
	 * @param uid
	 * @return
	 */
	public CommentBean findCommentByUser(Integer uid) {
		for (int index=0; index < this.comment.size(); index ++ ) {
			if (comment.get(index).getUser().getId() == uid) {
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
	 * 点赞
	 * @param vote
	 * @return
	 */
	public boolean addVote(VoteBean vote) {
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
}
