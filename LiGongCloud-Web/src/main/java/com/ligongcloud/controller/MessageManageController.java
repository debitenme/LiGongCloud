package com.ligongcloud.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ligongcloud.bean.MessageBean;
import com.ligongcloud.bean.PageBean;
import com.ligongcloud.dao.MessageDao;
import com.ligongcloud.dao.UserDao;
import com.ligongcloud.util.ResponseUtil;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
@Controller
@RequestMapping("/messageManage")
public class MessageManageController {

	@Autowired
	private MessageDao messageDao;
	
	@Autowired
	private UserDao userDao;

	/**
	 * 加载所需的消息
	 */
	@RequestMapping(value = "/loadMessage")
	public String loadUserMessage(String s_title, String s_userTo,
			String s_toUser, String s_smessageDate, String s_emessageDate,
			String page, String rows, HttpServletResponse response,
			HttpSession httpSession) throws Exception {
		
		JSONObject result = new JSONObject();
		JsonConfig jsonConfig = new JsonConfig();

		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray jsonArray = JSONArray.fromObject( search(s_title, s_userTo, s_toUser, s_smessageDate,
						s_emessageDate, page, rows, httpSession), jsonConfig);
		
		Long total = messageDao.messageCount(s_title, s_userTo, s_toUser, s_smessageDate, s_emessageDate, httpSession);

		result.put("rows", jsonArray);
		result.put("total", total);
		ResponseUtil.write(response, result);
		return null;
	}

	/**
	 * 查询所需消息
	 */
	private ArrayList<MessageBean> search(String s_title, String s_userTo,
			String s_toUser, String s_smessageDate, String s_emessageDate,
			String page, String rows, HttpSession httpSession) throws Exception {
		
		if (page == null) {
			page = "1";
			rows = "10";
		}

		PageBean pageBean = new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
		
		ArrayList<MessageBean> messageBeans = messageDao.messageList(pageBean,
				s_title, s_userTo, s_toUser, s_smessageDate, s_emessageDate,
				httpSession);
		
		return messageBeans;
	}
	
	/**
	 * 发送消息
	 * @throws Exception 
	 */
	@RequestMapping(value = "/sendMessage")
	public String sendMessage(String sendToUser,String sendTitle,String sendContext, HttpSession httpSession,HttpServletResponse response) throws Exception{
		JSONObject result=new JSONObject();
		if(userDao.isNullByStuNo(sendToUser)){
			result.put("success", "false");	
			result.put("errorMeg", "该用户不存在");
			ResponseUtil.write(response, result);
			return null;
		}
		
		messageDao.sendMessage(sendToUser, sendTitle, sendContext, httpSession);
		result.put("success", "true");	
		result.put("errorMeg", "发送成功");
		ResponseUtil.write(response, result);
		return null;
	}
	
	/**
	 * 删除消息
	 */
	@RequestMapping(value = "/deleteMessage")
	public String deleteMessage( String delIds,HttpServletResponse response ) throws Exception{
		JSONObject result=new JSONObject();
		int delNums=messageDao.deleteMessage(delIds);
		if(delNums>0){
			result.put("success", "true");
			result.put("delNums", delNums);
		}else{
			result.put("errorMeg", "删除失败");
		}
		result.put("delNum", delNums);
		ResponseUtil.write(response, result);
		return null;
	}
	
	/**
	 * 标记消息为未处理状态(由消息发出者进行处理)
	 */
	@RequestMapping(value = "/dealNotMessage")
	public String dealNotMessage( String delIds,HttpServletResponse response, HttpSession httpSession ) throws Exception{
		JSONObject result=new JSONObject();
		
		messageDao.dealNotMessage(delIds);
		result.put("errorMeg", "标记成功");
		ResponseUtil.write(response, result);
		return null;
	}
	
	/**
	 * 标记消息为以处理状态(由消息接收者进行处理)
	 */
	@RequestMapping(value = "/dealOkMessage")
	public String dealOkMessage( String delIds,HttpServletResponse response, HttpSession httpSession) throws Exception{
		JSONObject result=new JSONObject();
		
		messageDao.dealOkMessage(delIds,httpSession);
		result.put("errorMeg", "标记成功");
		ResponseUtil.write(response, result);
		return null;
	}
}
