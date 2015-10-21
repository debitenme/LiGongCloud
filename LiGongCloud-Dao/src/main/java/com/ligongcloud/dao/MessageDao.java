package com.ligongcloud.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ligongcloud.bean.MessageBean;
import com.ligongcloud.bean.PageBean;
import com.ligongcloud.model.File;
import com.ligongcloud.model.Message;
import com.ligongcloud.model.User;
import com.ligongcloud.util.MessageUtil;
import com.ligongcloud.util.StringUtil;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
@Component("messageDao")
public class MessageDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private UserDao userDao;
	/**
	 * 加载消息条数
	 */
	public Long messageCount(String s_title, String s_userTo, String s_toUser,
			String s_smessageDate, String s_emessageDate,HttpSession httpSession) throws Exception {

		User u = (User)httpSession.getAttribute("u");
		
		Long total;
		if( StringUtil.isNotEmpty(s_title)||StringUtil.isNotEmpty(s_userTo)||StringUtil.isNotEmpty(s_toUser)||StringUtil.isNotEmpty(s_smessageDate)||StringUtil.isNotEmpty(s_emessageDate) ){
			//需要查询的个数
			StringBuffer sqlBuffer = new StringBuffer( "select count(*) from Message message where message.user.id="+u.getId() + " ");
			
			if(StringUtil.isNotEmpty(s_title)){
				sqlBuffer.append("and message.title = '"+s_title +"' "  );
			}
			
			if(StringUtil.isNotEmpty(s_userTo)){
				sqlBuffer.append("and message.userTo = '"+s_userTo +"' "  );
			}
			
			if(StringUtil.isNotEmpty(s_toUser)){
				sqlBuffer.append("and message.toUser = '"+s_toUser +"' "  );
			}
			
			if(StringUtil.isEmpty(s_smessageDate)){
				s_smessageDate = "2010-01-01";
			}	
			
			if(StringUtil.isEmpty(s_emessageDate)){
				//如果s_ejoindate为空，设s_ejoindate为当前系统时间
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				s_emessageDate = df.format(new Date()) ;
			}
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
			Date begin = sdf.parse(s_smessageDate);
			Date end = sdf.parse(s_emessageDate);
			
			sqlBuffer.append("and message.messageDate > :beginTime and message.messageDate <= :endTime");
			Query q = sessionFactory.getCurrentSession().createQuery( sqlBuffer.toString()).setTimestamp("beginTime",begin).setTimestamp("endTime",end);
			total = (Long)q.uniqueResult();
		}else{
			//总个数
			Query q = sessionFactory.getCurrentSession().createQuery("select count(*) from Message message where message.user.id="+u.getId());
			total = (Long)q.uniqueResult();
		}
		System.out.println(total);
		return total;
	}

	/**
	 * 加载所需消息
	 */
	public ArrayList<MessageBean> messageList(PageBean pageBean,
			String s_title, String s_userTo, String s_toUser,
			String s_smessageDate, String s_emessageDate,HttpSession httpSession) throws Exception {

		if (StringUtil.isNotEmpty(s_title) || StringUtil.isNotEmpty(s_userTo)
				|| StringUtil.isNotEmpty(s_toUser)
				|| StringUtil.isNotEmpty(s_smessageDate)
				|| StringUtil.isNotEmpty(s_emessageDate)) {
			return this.searchMessage(s_title, s_userTo, s_toUser,s_smessageDate,s_emessageDate, pageBean,httpSession);
		}else{
			return this.loadPageMessage(pageBean,httpSession);
		}

	}

	/**
	 * 查询所有消息
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<MessageBean> loadPageMessage(PageBean pageBean,
			HttpSession httpSession) {
		
		
		User u = (User)httpSession.getAttribute("u");
		Query q = sessionFactory.getCurrentSession().createQuery("from Message message where message.user.id="+u.getId());
		q.setFirstResult(pageBean.getStart());   
		q.setMaxResults(pageBean.getRows());  
		ArrayList<Message> messages = (ArrayList<Message>) q.list();
		ArrayList<MessageBean> messageBeans = MessageUtil.formatMessage(messages);
		return messageBeans;
	}
	
	/**
	 * 查询所需消息
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<MessageBean> searchMessage(String s_title, String s_userTo,
			String s_toUser, String s_smessageDate, String s_emessageDate,
			PageBean pageBean, HttpSession httpSession) throws Exception {
		
		User u = (User)httpSession.getAttribute("u");
		
		StringBuffer sqlBuffer = new StringBuffer( "from Message message where message.user.id="+u.getId()+" ");
		
		if(StringUtil.isNotEmpty(s_title)){
			sqlBuffer.append("and message.title = '"+s_title +"' "  );
		}
		
		if(StringUtil.isNotEmpty(s_userTo)){
			sqlBuffer.append("and message.userTo = '"+s_userTo +"' "  );
		}
		
		if(StringUtil.isNotEmpty(s_toUser)){
			sqlBuffer.append("and message.toUser = '"+s_toUser +"' "  );
		}
		
		
		if(StringUtil.isEmpty(s_smessageDate)){
			s_smessageDate = "2010-01-01";
		}	
		
		if(StringUtil.isEmpty(s_emessageDate)){
			//如果s_ejoindate为空，设s_ejoindate为当前系统时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			s_emessageDate = df.format(new Date()) ;
		}
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的m m表示的是分钟  
		Date begin = sdf.parse(s_smessageDate);
		Date end = sdf.parse(s_emessageDate);
		
		sqlBuffer.append("and message.messageDate > :beginTime and message.messageDate <= :endTime");
		Query q = sessionFactory.getCurrentSession().createQuery(sqlBuffer.toString()).setTimestamp("beginTime",begin).setTimestamp("endTime",end);
		q.setFirstResult(pageBean.getStart());   
		q.setMaxResults(pageBean.getRows());
		
		ArrayList<Message> messages = (ArrayList<Message>) q.list();
		ArrayList<MessageBean> messageBeans = MessageUtil.formatMessage(messages);
		return messageBeans;
	}
	
	/**
	 * 发送消息
	 */
	public void sendMessage(String sendToUser,String sendTitle,String sendContext, HttpSession httpSession){
		//为发送者和接收者分别添加消息
		User toUser = userDao.loadUserByStuNo(sendToUser);
		User u = (User)httpSession.getAttribute("u");
		String userTo = u.getStuNo();
		
		Message messageUserTo = MessageUtil.createMessage(sendToUser, sendTitle, sendContext, userTo, u);
		Message messageToUser = MessageUtil.createMessage(sendToUser, sendTitle, sendContext, userTo, toUser);
		
		sessionFactory.getCurrentSession().save(messageToUser);
		sessionFactory.getCurrentSession().save(messageUserTo);
	}
	
	/**
	 * 删除消息
	 */
	public int deleteMessage(String delIds){
		Query queryu = sessionFactory.getCurrentSession().createQuery(
				"delete Message m where m.id in (" + delIds + ")");
		queryu.executeUpdate();
		return 1;
	}
	
	/**
	 * 标记消息为未处理状态
	 */
	public void dealNotMessage(String delIds) {
		String sql = "update Message message set message.state=0 where message.id in ("+ delIds + ")";
		Query q = sessionFactory.getCurrentSession().createQuery(sql);
		q.executeUpdate();
	}

	/**
	 * 标记消息为以处理状态
	 */
	public void dealOkMessage(String delIds, HttpSession httpSession) {
		
		String sql = "update Message message set message.state=1 where message.id in ("+ delIds + ")";
		Query q = sessionFactory.getCurrentSession().createQuery(sql);
		q.executeUpdate();
	}

	/**
	 * 加载指定ID集合的消息; 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Message> loadMessageByIds(String ids) {
		Query q = sessionFactory.getCurrentSession().createQuery(
				"from Message m where m.id in (" + ids + ")");
		ArrayList<Message> messages = (ArrayList<Message>) q.list();
		return messages;
	}
	
	/**
	 * 加载指定ID的消息; 
	 */
	@SuppressWarnings("unchecked")
	public Message loadMessageById(String id) {
		Query q = sessionFactory.getCurrentSession().createQuery(
				"from Message m where m.id = " + id );
		ArrayList<Message> messages = (ArrayList<Message>) q.list();
		return messages.get(0);
	}

	/**
	 * 分享文件后再双方消息数据库中建立分享消息
	 * @param user接收分享文件的人
	 * @param file被分享的文件
	 * @param httpSession用来得到登陆者（文件分享者）（文件所有人）
	 */
	public void shareMessage(User user, File file, HttpSession httpSession) {
		User u = (User)httpSession.getAttribute("u");
		String sendTitle = "分享文件";
		String sendContext = u.getStuNo()+"将文件分享给"+user.getStuNo();
		this.sendMessage(user.getStuNo(), sendTitle, sendContext, httpSession);
	}
	
	/**
	 * 删除指定用户Id集合的消息
	 */
	public void deleteMessageByUserIds(String delIds) {
		Query queryu = sessionFactory.getCurrentSession().createQuery(
				"delete Message m where m.user.id in (" + delIds + ")");
		queryu.executeUpdate();		
	}
	
}
