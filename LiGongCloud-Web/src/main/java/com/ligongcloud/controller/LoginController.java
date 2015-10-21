package com.ligongcloud.controller;


import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ligongcloud.bean.UserBean;
import com.ligongcloud.dao.DiskDao;
import com.ligongcloud.dao.UserDao;
import com.ligongcloud.model.Disk;
import com.ligongcloud.model.User;
import com.ligongcloud.util.PasswordUtil;
import com.ligongcloud.util.StringUtil;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
@Controller
@RequestMapping("/login")
public class LoginController{
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private DiskDao diskDao;
	
	/**
	 * 用户登录
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/login")
	public String login(String loginType,String username,String password, HttpSession httpSession){

		if( StringUtil.isEmpty(username)||StringUtil.isEmpty(password) ){
			return "login";
		}
		
		//管理员登录
		if( loginType.equals("admin") ){
			if(username.equals("admin")&&password.equals("admin")){
				User u = userDao.loadUserById(1);
				//2015.03.27——为了加载文件时，判断文件路径（加载这个人的文件）
				httpSession.setAttribute("u", u);
				return "adminMain";
			}else{
				System.out.println("admin fail");
				return "login";
			}
		}
		
		//普通用户登录
		if( loginType.equals("normal") ) {
			//禁止管理员以用户的身份登录
			if( username.equals("admin") ){
				return "login";
			}
			
			ArrayList<User> users = userDao.loadAllUser();
			for( User user : users ){
				
				String stuNoStr = user.getStuNo();
				String passwordStr = user.getPassword();
				if( stuNoStr.equals(username) && passwordStr.equals(PasswordUtil.MD5Encode(password)) ){
					UserBean u = new UserBean();
					
					u.setId(user.getId());
					u.setStuNo(stuNoStr);
					u.setPassword(passwordStr);
					u.setEmail(user.getEmail());
					String genderStr ;
					if(1 == user.getGender()){
						genderStr = "男";
					}else{
						genderStr = "女";
					}
					u.setGender(genderStr);
					u.setJoindate(user.getJoindate());
					u.setUsername(user.getUsername());
					
					//2015.03.26——在页面中显示这个登陆用户的信息
					//2015.03.27——为了加载文件时，判断文件路径（加载这个人的文件）
					httpSession.setAttribute("uBean", u);
					httpSession.setAttribute("u", user);
					
					Disk dBean = diskDao.loadDiskByUser(user.getId());
					httpSession.setAttribute("dBean", dBean);
					
					return "normalMain";
				}
			}
			
		}else{
			return "login";
		}
		
		return "login";
	}
	
}