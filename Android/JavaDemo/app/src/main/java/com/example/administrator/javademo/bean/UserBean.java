package com.example.administrator.javademo.bean;

/**
 * Created by Administrator on 2018/1/30 0030.
 */

public class UserBean {
    private Integer id;

    private String  identifier;

    private String username;

    private String password;

    private String type;

    public UserBean(){}

    public UserBean(String identifier, String username, String password, String type) {
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

    @Override
    public String toString() {
        return "UserBean{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
