package com.example.administrator.javademo.bean;

import android.app.Activity;
import android.widget.ImageView;

import java.sql.Timestamp;
import java.util.List;

public class InformationBean {

	private Integer id;//id
	
	private Integer type;//0图片，1视频
	
	private String info;//文字
	
	private String url;//视频或图片的网络地址

	private Integer commentinfoSize = 0;  // 评论量

	private Integer voteinfoSize = 0;  // 点赞量

	private UserBean user; //发表说说的用户

	private List<CommentinfoBean> commentinfo; //说说的评论

	private List<VoteinfoBean> voteinfo;  //说说的点赞

	private long createTime;  //创建时间

	public InformationBean(){}
	
	public InformationBean(String info, String url,Integer type, UserBean user) {
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

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public List<CommentinfoBean> getCommentinfo() {
		return commentinfo;
	}

	public void setCommentinfo(List<CommentinfoBean> commentinfo) {
		this.commentinfo = commentinfo;
	}

	public List<VoteinfoBean> getVoteinfo() {
		return voteinfo;
	}

	public void setVoteinfo(List<VoteinfoBean> voteinfo) {
		this.voteinfo = voteinfo;
	}

	public long getCreateTime() {
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
	public void addCommentinfo(CommentinfoBean commentinfo) {
		this.commentinfo.add(commentinfo);
		this.commentinfoSize = this.commentinfo.size();
	}
	/**
	 * 删除评论
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
	public CommentinfoBean findCommentinfo(Integer commentinfoId) {
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
	 * 点赞
	 * @param voteinfo
	 * @return
	 */
	public boolean addVoteinfo(VoteinfoBean voteinfo) {
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
	public VoteinfoBean findVoteinfo(Integer voteinfoId) {
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
	public VoteinfoBean findVoteinfoByUser(Integer userId) {
		for (int index=0; index < this.voteinfo.size(); index ++ ) {
			if (this.voteinfo.get(index).getUser().getId() == userId) {
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
	public void voteinfoByUserShow(final Activity activity,final Integer userId, final ImageView iv) {
		new Thread(){
			@Override
			public void run() {
				boolean flag = false;
				for (int index=0; index < voteinfo.size(); index ++ ) {
					if (voteinfo.get(index).getUser().getId() == userId) {
						flag = true;
						break;
					}
				}
				final boolean finalFlag = flag;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						iv.setSelected(finalFlag);
					}
				});
			}
		}.start();

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
	
	
	
}
