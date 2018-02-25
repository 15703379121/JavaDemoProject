package com.baisha.javademo.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class User {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true,insertable = true,nullable = false)
	private String  identifier;
	
	private String username;

    private String password;

    private String type;
    
    public User(){}

	public User(String identifier,String username, String password, String type) {
		super();
		this.identifier = identifier;
		this.username = username;
		this.password = password;
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public User(Integer id, String identifier, String username, String password, String type) {
		super();
		this.id = id;
		this.identifier = identifier;
		this.username = username;
		this.password = password;
		this.type = type;
	}
	
}
