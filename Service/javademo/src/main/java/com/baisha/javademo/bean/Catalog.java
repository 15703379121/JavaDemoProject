package com.baisha.javademo.bean;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })//懒加载问题
public class Catalog{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer project;
	
	private Integer tag;
	
	private String title;
	

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
//	@JoinTable(name = "catalog_video", joinColumns = @JoinColumn(name = "catalog_id", referencedColumnName = "id"), 
//		inverseJoinColumns = @JoinColumn(name = "video_id", referencedColumnName = "id"))
	private List<Video> video;
	

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
//	@JoinTable(name = "catalog_practice", joinColumns = @JoinColumn(name = "catalog_id", referencedColumnName = "id"), 
//		inverseJoinColumns = @JoinColumn(name = "practice_id", referencedColumnName = "id"))
	private List<Practice> practice;
	
	public Catalog(){};

	public Catalog(Integer project, Integer tag, String title) {
		super();
		this.project = project;
		this.tag = tag;
		this.title = title;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProject() {
		return project;
	}

	public void setProject(Integer project) {
		this.project = project;
	}

	public Integer getTag() {
		return tag;
	}

	public void setTag(Integer tag) {
		this.tag = tag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Video> getVideo() {
		return video;
	}

	public void setVideo(List<Video> video) {
		this.video = video;
	}

	public List<Practice> getPractice() {
		return practice;
	}

	public void setPractices(List<Practice> practice) {
		this.practice = practice;
	}

	/**
	 * 添加视频
	 * @param video
	 */
	public void addVideo(Video video) {
		this.video.add(video);
	}
	/**
	 * 删除视频
	 * @param video
	 */
	public void removeVideo(Integer videoId) {
		for (int index=0; index < this.video.size(); index ++ ) {
			if (video.get(index).getId() == videoId) {
				this.video.remove(index);
				break;
			}
		}
	}
	
	/**
	 * 寻找视频
	 * @param videoId
	 * @return
	 */
	public Video findVideo(Integer videoId){
		for (int index=0; index < this.video.size(); index ++ ) {
			if (video.get(index).getId() == videoId) {
				return video.get(index);
			}
		}
		return null;
	}
	
	/**
	 * 添加习题
	 * @param practice
	 */
	public void addPractice(Practice practice) {
		this.practice.add(practice);
	}
	/**
	 * 删除习题
	 * @param practice
	 */
	public void removePractice(Integer practiceId) {
		for (int index=0; index < this.practice.size(); index ++ ) {
			if (practice.get(index).getId() == practiceId) {
				this.practice.remove(index);
				break;
			}
		}
	}
	
	/**
	 * 寻找习题
	 * @param videoId
	 * @return
	 */
	public Practice findPractice(Integer practiceId){
		for (int index=0; index < this.practice.size(); index ++ ) {
			if (practice.get(index).getId() == practiceId) {
				return practice.get(index);
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Catalog [id=" + id + ", project=" + project + ", tag=" + tag + ", title=" + title + "]";
	}
	
}
