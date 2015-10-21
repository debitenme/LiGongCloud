package com.ligongcloud.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.ligongcloud.bean.MessageBean;
import com.ligongcloud.model.Message;
import com.ligongcloud.model.User;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
public class MessageUtil {

	public static ArrayList<MessageBean> formatMessage( ArrayList<Message> messages ){
		ArrayList<MessageBean> messageBeans = new ArrayList<MessageBean>();
		
		for( Message message : messages ){
			
			MessageBean m = new MessageBean();
			
			m.setContent(message.getContent());
			m.setId(message.getId());
			m.setMessageDate(message.getMessageDate());
			String stageM;
			if( message.getState() == 1 ){
				stageM = "已处理";
			}else{
				stageM = "未处理";
			}
			m.setState(stageM);
			m.setTitle(message.getTitle());
			m.setToUser(message.getToUser());
			m.setUserTo(message.getUserTo());
			m.setUser(message.getUser().getStuNo());
			
			messageBeans.add(m);
		}
		
		return messageBeans;
	}
	
	public static Message createMessage(String sendToUser,String sendTitle,String sendContext,String userTo,User u){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String messageDate = df.format(new Date());
		int state = 0;
		Message message = new Message();
		message.setContent(sendContext);
		message.setMessageDate(messageDate);
		message.setState(state);
		message.setTitle(sendTitle);
		message.setToUser(sendToUser);
		message.setUser(u);
		message.setUserTo(userTo);
		return message;
	}
	
}
