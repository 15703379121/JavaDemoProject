package com.baisha.javademo.bean;

import java.sql.Timestamp;
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
public class Information {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;//id
	
	private Integer type;//0图片，1视频
	
	private String info;//文字
	
	private String url;//视频或图片的网络地址

	private Integer commentinfoSize = 0;  // 评论量

	private Integer voteinfoSize = 0;  // 点赞量
	
	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user; //发表说说的用户
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<Commentinfo> commentinfo; //说说的评论
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Fetch(FetchMode.SUBSELECT)
	private List<Voteinfo> voteinfo;  //说说的点赞
	
	@org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
	private Timestamp createTime;  //创建时间

	public Information(){}
	
	public Information(String info, String url,Integer type, User user) {
		super();
		this.info = info;
		this.url = url;
		this.type = type;
		this.user = user;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getCommentinfoSize() {
		return commentinfoSize;
	}

	public void setCommentinfoSize(Integer commentinfoSize) {
		this.commentinfoSize = commentinfoSize;
	}

	public Integer getVoteinfoSize() {
		return voteinfoSize;
	}

	public void setVoteinfoSize(Integer voteinfoSize) {
		this.voteinfoSize = voteinfoSize;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Commentinfo> getCommentinfo() {
		return commentinfo;
	}

	public void setCommentinfo(List<Commentinfo> commentinfo) {
		this.commentinfo = commentinfo;
	}

	public List<Voteinfo> getVoteinfo() {
		return voteinfo;
	}

	public void setVoteinfo(List<Voteinfo> voteinfo) {
		this.voteinfo = voteinfo;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * 添加评论
	 * @param commentinfo
	 */
	public void addCommentinfo(Commentinfo commentinfo) {
		this.commentinfo.add(commentinfo);
		this.commentinfoSize = this.commentinfo.size();
	}
	/**
	 * 删除评论
	 * @param commentinfo
	 */
	public void removeCommentinfo(Integer commentinfoId) {
		for (int index=0; index < this.commentinfo.size(); index ++ ) {
			if (commentinfo.get(index).getId() == commentinfoId) {
				this.commentinfo.remove(index);
				break;
			}
		}
		
		this.commentinfoSize = this.commentinfo.size();
	}
	
	/**
	 * 查找评论
	 * @param commentinfoId
	 * @return 
	 */
	public Commentinfo findCommentinfo(Integer commentinfoId) {
		for (int index=0; index < this.commentinfo.size(); index ++ ) {
			if (commentinfo.get(index).getId() == commentinfoId) {
				return commentinfo.get(index);
			}
		}
		return null;		
	}
	
	/**
	 * 改变评论状态
	 * @param commentinfoId
	 * @param state
	 */
	public void updateCommentinfoState(Integer commentinfoId,Integer state) {
		for (int index=0; index < this.commentinfo.size(); index ++ ) {
			if (commentinfo.get(index).getId() == commentinfoId) {
				commentinfo.get(index).setState(state);
				break;
			}
		}	
	}
	
	/**
	 * 获取新评论个数
	 * @return
	 */
	public long getNewCommentinfoSize(){
		long count = 0;
		for (int index=0; index < this.commentinfo.size(); index ++ ) {
			if(commentinfo.get(index).getState() == 0){
				//新评论
				count++;
			}
		}	
		return count;
	}
	
	/**
	 * 获取新评论
	 * @return
	 */
	public List<Commentinfo> getNewCommentinfo(){
		List<Commentinfo> list = new ArrayList<>();
		for (int index=0; index < this.commentinfo.size(); index ++ ) {
			if(commentinfo.get(index).getState() == 0){
				//新评论
				list.add(commentinfo.get(index));
			}
		}	
		return list;
	}
	
	/**
	 * 改变评论状态
	 */
	public void updateCommentinfoState(){
		for (int index=0; index < this.commentinfo.size(); index ++ ) {
			if(commentinfo.get(index).getState() == 0){
				//新评论
				commentinfo.get(index).setState(1);
			}
		}	
	}
	
	/**
	 * 点赞
	 * @param voteinfo
	 * @return
	 */
	public boolean addVoteinfo(Voteinfo voteinfo) {
		boolean isExist = false;
		// 判断重复
		for (int index=0; index < this.voteinfo.size(); index ++ ) {
			if (this.voteinfo.get(index).getUser().getId() == voteinfo.getUser().getId()) {
				isExist = true;
				break;
			}
		}
		
		if (!isExist) {
			this.voteinfo.add(voteinfo);
			this.voteinfoSize = this.voteinfo.size();
		}

		return isExist;
	}
	/**
	 * 取消点赞
	 * @param voteinfoId
	 */
	public void removeVoteinfo(Integer voteinfoId) {
		for (int index=0; index < this.voteinfo.size(); index ++ ) {
			if (this.voteinfo.get(index).getId() == voteinfoId) {
				this.voteinfo.remove(index);
				break;
			}
		}
		
		this.voteinfoSize = this.voteinfo.size();
	}
	
	/**
	 * 寻找点赞
	 * @param voteinfoId
	 * @return
	 */
	public Voteinfo findVoteinfo(Integer voteinfoId) {
		for (int index=0; index < this.voteinfo.size(); index ++ ) {
			if (this.voteinfo.get(index).getId() == voteinfoId) {
				return voteinfo.get(index);
			}
		}
		return null;
	}
	
	/**
	 * 寻找点赞
	 * @param userId
	 * @return
	 */
	public Voteinfo findVoteinfoByUser(Integer userId) {
		for (int index=0; index < this.voteinfo.size(); index ++ ) {
			if (this.voteinfo.get(index).getUser().getId() == userId) {
				return voteinfo.get(index);
			}
		}
		return null;
	}
	
	/**
	 * 寻找点赞
	 * @param voteinfoId
	 * @return
	 */
	public void updateVoteinfoState(Integer voteinfoId,Integer state) {
		for (int index=0; index < this.voteinfo.size(); index ++ ) {
			if (this.voteinfo.get(index).getId() == voteinfoId) {
				voteinfo.get(index).setState(state);
				break;
			}
		}
	}
	
	/**
	 * 获取新评论个数
	 * @return
	 */
	public long getNewVoteinfoSize(){
		long count = 0;
		for (int index=0; index < this.voteinfo.size(); index ++ ) {
			if(voteinfo.get(index).getState() == 0){
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
		for (int index=0; index < this.voteinfo.size(); index ++ ) {
			if(voteinfo.get(index).getState() == 0){
				//新点赞
				voteinfo.get(index).setState(1);
			}
		}	
	}
	
	/**
	 * 获取新点赞
	 * @return
	 */
	public List<Voteinfo> getNewVoteinfo(){
		List<Voteinfo> list = new ArrayList<>();
		for (int index=0; index < this.voteinfo.size(); index ++ ) {
			if(voteinfo.get(index).getState() == 0){
				//新点赞
				list.add(voteinfo.get(index));
			}
		}	
		return list;
	}
	
	
}
