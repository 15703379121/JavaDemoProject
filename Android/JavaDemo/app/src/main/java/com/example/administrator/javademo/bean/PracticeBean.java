package com.example.administrator.javademo.bean;

import java.io.Serializable;

public class PracticeBean implements Serializable{

	private Integer id;
	
	private String title;
	
	private String info;
	
	private String url;

	private UserBean user;

	private CatalogBean catalog;

	public PracticeBean(){};

	public PracticeBean(String title, String info, String url,UserBean user,CatalogBean catalog) {
		super();
		this.title = title;
		this.info = info;
		this.url = url;
		this.user = user;
		this.catalog = catalog;
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

	public CatalogBean getCatalog() {
		return catalog;
	}

	public void setCatalog(CatalogBean catalog) {
		this.catalog = catalog;
	}


}
