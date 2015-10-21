package com.ligongcloud.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ligongcloud.bean.PageBean;
import com.ligongcloud.bean.UserBean;
import com.ligongcloud.model.User;
import com.ligongcloud.util.StringUtil;
import com.ligongcloud.util.UserUtil;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
@Component("userDao")
public class UserDao {

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 加载所有用户; 输入：null; 输出： 将数据库中所有的用户保存在一个list中
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<User> loadAllUser() {
		Query q = sessionFactory.getCurrentSession().createQuery("from User");
		ArrayList<User> users = (ArrayList<User>) q.list();
		return users;
	}

	/**
	 * 加载指定ID用户; 输入：用户ID; 输出：一名用户
	 */
	@SuppressWarnings("unchecked")
	public User loadUserById(int id) {
		Query q = sessionFactory.getCurrentSession().createQuery(
				"from User u where u.id=" + id);
		ArrayList<User> users = (ArrayList<User>) q.list();
		return users.get(0);
	}
	
	/**
	 * 加载指学号用户
	 */
	@SuppressWarnings("unchecked")
	public User loadUserByStuNo( String stuNo ){
		Query q = sessionFactory.getCurrentSession().createQuery(
				"from User u where u.stuNo='" + stuNo+"'");
		ArrayList<User> users = (ArrayList<User>) q.list();
		return users.get(0);
	}
	
	/**
	 * 加载指定ID集合用户; 输入：用户ID集合; 输出：多名用户
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<User> loadUserByIds(String ids) {
		Query q = sessionFactory.getCurrentSession().createQuery(
				"from User u where u.id in (" + ids + ")");
		ArrayList<User> users = (ArrayList<User>) q.list();
		return users;
	}
	
	/**
	 * 根据学号，判断有没有这个学生 
	 * @return 
	 * 			没有这个学生返回——true;有这个学生返回——false
	 */
	public boolean isNullByStuNo(String stuNo){
		String sql = "select count(*) From User u where u.stuNo='"+ stuNo+"'";
		Query q = sessionFactory.getCurrentSession().createQuery(sql);
		long total = (Long)q.uniqueResult();
		int i = (int)total;
		if(0==i){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 分页加载所有用户; 输入：页码对象; 输出：指定页码中的用户
	 * @param pageBean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<UserBean> loadPageUser( PageBean pageBean ) {
		Query q = sessionFactory.getCurrentSession().createQuery("from User");
		q.setFirstResult(pageBean.getStart());   
		q.setMaxResults(pageBean.getRows());  
		ArrayList<User> users = (ArrayList<User>) q.list();
		ArrayList<UserBean> userBeans = UserUtil.formatUser(users);
		return userBeans;
	}

	/**
	 * 存储用户; 输入：一名用户; 输出：将输入的用户存至数据库;
	 */
	public void saveUser(User user) {
		System.out.println("save start!!");
		sessionFactory.getCurrentSession().save(user);
		System.out.println("save end!!");
	}

	/**
	 * 删除用户; 输入：学号; 输出：将此学号的用户删除，并且删除该用户的网盘
	 */
	public int deleteUser(String delIds) {

		Query queryu = sessionFactory.getCurrentSession().createQuery(
				"delete User u where u.id in (" + delIds + ")");
		queryu.executeUpdate();
		return 1;
	}

	/**
	 * 更新用户； 输入：param； 输出：更新用户信息（没有网盘信息）；
	 * @param id
	 * @param stuNo
	 * @param password
	 * @param username
	 * @param gender
	 * @param joindate
	 * @param email
	 */
	public User updateUser(int id, String stuNo, String password, String username, int gender, String joindate, String email) {
		
		String sql = "update User u set u.stuNo=?, u.password=?, u.username=?,u.gender=?,u.joindate=?,u.email=? where u.id=?";
		Query queryu = sessionFactory.getCurrentSession().createQuery(sql)
				.setParameter(0, stuNo).setParameter(1, password)
				.setParameter(2, username).setParameter(3, gender)
				.setParameter(4, joindate).setParameter(5, email)
				.setParameter(6, id);
		queryu.executeUpdate();

		User user = this.loadUserById(id);
		return user;
		
	}
	
	/**
	 * 查询用户;  输入：学号，时间段;  输出：查询到的时间; 
	 * @param s_stuNo
	 * @param s_sjoindate
	 * @param s_ejoindate
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<UserBean> searchUser(String s_stuNo,String s_sjoindate,String s_ejoindate,PageBean pageBean) throws Exception{
		StringBuffer sqlBuffer = new StringBuffer( "from User user where 1=1 ");
		
		if(StringUtil.isNotEmpty(s_stuNo)){
			sqlBuffer.append("and user.stuNo = '"+s_stuNo +"' " );
		}
		
		if(StringUtil.isEmpty(s_sjoindate)){
			s_sjoindate = "2010-01-01";
		}	
		
		if(StringUtil.isEmpty(s_ejoindate)){
			//如果s_ejoindate为空，设s_ejoindate为当前系统时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			s_ejoindate = df.format(new Date()) ;
		}
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
		Date begin = sdf.parse(s_sjoindate);
		Date end = sdf.parse(s_ejoindate);
		
		sqlBuffer.append("and user.joindate > :beginTime and user.joindate <= :endTime");
		Query q = sessionFactory.getCurrentSession().createQuery(sqlBuffer.toString()).setTimestamp("beginTime",begin).setTimestamp("endTime",end);
		q.setFirstResult(pageBean.getStart());   
		q.setMaxResults(pageBean.getRows());
		
		ArrayList<User> users = (ArrayList<User>) q.list();
		ArrayList<UserBean> userBeans = UserUtil.formatUser(users);
		return userBeans;
	}
	
	/**
	 * 返回满足条件的用户数量
	 * @param s_stuNo
	 * @param s_sjoindate
	 * @param s_ejoindate
	 * @return
	 * @throws Exception
	 */
	public Long userCount(String s_stuNo, String s_sjoindate, String s_ejoindate) throws Exception {
		Long total;
		if( StringUtil.isNotEmpty(s_stuNo)||StringUtil.isNotEmpty(s_ejoindate)||StringUtil.isNotEmpty(s_sjoindate) ){
			//需要查询的个数
			StringBuffer sqlBuffer = new StringBuffer( "select count(*) from User user where 1=1 ");
			
			if(StringUtil.isNotEmpty(s_stuNo)){
				sqlBuffer.append("and user.stuNo = '"+s_stuNo + "' " );
			}
			
			if(StringUtil.isEmpty(s_sjoindate)){
				s_sjoindate = "2010-01-01";
			}	
			
			if(StringUtil.isEmpty(s_ejoindate)){
				//如果s_ejoindate为空，设s_ejoindate为当前系统时间
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				s_ejoindate = df.format(new Date()) ;
			}
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
			Date begin = sdf.parse(s_sjoindate);
			Date end = sdf.parse(s_ejoindate);
			
			sqlBuffer.append("and user.joindate > :beginTime and user.joindate <= :endTime");
			Query q = sessionFactory.getCurrentSession().createQuery( sqlBuffer.toString()).setTimestamp("beginTime",begin).setTimestamp("endTime",end);
			total = (Long)q.uniqueResult();
		}else{
			//总个数
			Query q = sessionFactory.getCurrentSession().createQuery("select count(*) from User");
			total = (Long)q.uniqueResult();
		}
		System.out.println(total);
		return total;
	}

	/**
	 * 加载所有用户或查询用户
	 * @param pageBean
	 * @param s_stuNo
	 * @param s_sjoindate
	 * @param s_ejoindate
	 * @return
	 * @throws Exception
	 */
	public ArrayList<UserBean> userList(PageBean pageBean, String s_stuNo, String s_sjoindate, String s_ejoindate) throws Exception {
		
		if( StringUtil.isNotEmpty(s_stuNo)||StringUtil.isNotEmpty(s_ejoindate)||StringUtil.isNotEmpty(s_sjoindate) ){
			return this.searchUser(s_stuNo, s_sjoindate, s_ejoindate,pageBean);
		}else{
			return this.loadPageUser(pageBean);
		}
	}
	
}
