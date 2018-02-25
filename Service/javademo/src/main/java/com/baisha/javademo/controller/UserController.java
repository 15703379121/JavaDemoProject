package com.baisha.javademo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baisha.javademo.bean.User;
import com.baisha.javademo.dao.UserDAO;
import com.baisha.javademo.util.AppConstants;

@RestController
@RequestMapping("user")
class UserController {
	@RequestMapping()
	public String api(){
		return "皮皮";
	}
	
	
	@Autowired
	private UserDAO userDao;

	private final static String IDENTIFIER = "identifier";
	private final static String USERNAME = "username";
	private final static String PASSWORD = "password";
	private final static String TYPE = "type";
	private final static String USERLIST = "userList";
	
	/**
	 * 查找所有用户
	 */
	@RequestMapping("findAll")
	public List<User> findAll(){
		return userDao.findAll();
	}
	/**
	 * 登录
	 */
	@PostMapping("checkLogin")
	public User checkLogin(@RequestParam(IDENTIFIER)String identifier,
			@RequestParam(PASSWORD)String password,
			@RequestParam(TYPE)String type){
		User user = null;
		List<User> list = userDao.findByIdentifierAndPasswordAndType(identifier, password, type);
		if (!list.isEmpty()) {
			user = list.get(0);
		}
		return user;
	}

	
	//增
	@PostMapping(value = "userSave")
	public User save(@RequestParam(IDENTIFIER)String identifier,
			@RequestParam(USERNAME) String username,
			@RequestParam(PASSWORD) String password,
			@RequestParam(TYPE) String type){
		return userDao.save(new User(identifier,username, password, type));
	}  
	/**
	 * 批量添加用户
	 */
	@PostMapping("saveAll")
	public String saveAll(@RequestParam(USERLIST)String userListJson){  
		List<User> userList = JSON.parseArray(userListJson, User.class);
		int count = 0;
		if(!userList.isEmpty()){
			for (int position = 0; position < userList.size(); position++) {
				try{
					userDao.save(userList.get(position));
				}catch(Exception e){
					count++;
				}
			}
			return AppConstants.SUCCESS+"-"+count;
		}else{
			return AppConstants.FAIL;
		}
	}
	
	/**
	 * 批量删除
	 */
	@PostMapping("deleteAll")
	public String deleteAll(@RequestParam(IDENTIFIER)String identifier){
		try{
			System.out.println("identifier----"+identifier);
			List<User> list = userDao.findByIdentifierStartingWith(identifier);
			System.out.println("list---"+list);
			System.out.println("list.size()----"+list.size());
			for (User user : list) {
				userDao.delete(user);
			}
		}catch(Exception e){
			return AppConstants.FAIL;
		}
		return AppConstants.SUCCESS;
	}
	
	/**
	 * 修改密码
	 */
	@PostMapping("updatePassword")
	public String updatePassword(@RequestParam(IDENTIFIER)String identifier,
			@RequestParam(PASSWORD) String password){
		try{
			//按identifier查找
			List<User> list = userDao.findByIdentifier(identifier);
			if(!list.isEmpty()){
				//修改密码
				User user = list.get(0);
				user.setPassword(password);
				User update = userDao.save(user);
				if(update != null){
					//修改成功
					return AppConstants.SUCCESS;
				}
			}
		}catch(Exception e){
			return AppConstants.FAIL;
		}
		return AppConstants.FAIL;
	}
}
