package com.ligongcloud.util;

import java.util.ArrayList;

import com.ligongcloud.bean.UserBean;
import com.ligongcloud.model.User;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
public class UserUtil {
	
	/**
	 * 将User转换成UserBean以用于页面展示
	 * @param users
	 * @return
	 */
	public static ArrayList<UserBean> formatUser(ArrayList<User> users){
		ArrayList<UserBean> userBeans = new ArrayList<UserBean>();
		for( int i=0; i<users.size(); i++ ){
			UserBean userBean = new UserBean();
			userBean.setDiskSize(users.get(i).getDisk().getTotalSize());
			userBean.setEmail(users.get(i).getEmail());
			userBean.setId(users.get(i).getId());
			userBean.setJoindate(users.get(i).getJoindate());
			userBean.setPassword(users.get(i).getPassword());
			userBean.setStuNo(users.get(i).getStuNo());
			userBean.setUsername(users.get(i).getUsername());
			int genderInt = users.get(i).getGender();
			if(1 == genderInt){
				userBean.setGender("男");
			}else{
				userBean.setGender("女");
			}
			userBeans.add(userBean);
		}
		return userBeans;
	}
}
