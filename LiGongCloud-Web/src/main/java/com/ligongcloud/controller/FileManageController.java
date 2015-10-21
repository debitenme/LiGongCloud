package com.ligongcloud.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ligongcloud.bean.FileBean;
import com.ligongcloud.bean.PageBean;
import com.ligongcloud.dao.DiskDao;
import com.ligongcloud.dao.FileDao;
import com.ligongcloud.dao.MessageDao;
import com.ligongcloud.dao.UserDao;
import com.ligongcloud.model.File;
import com.ligongcloud.model.User;
import com.ligongcloud.util.CopyFileUtil;
import com.ligongcloud.util.EncryptGen;
import com.ligongcloud.util.FileUtil;
import com.ligongcloud.util.KbToMUtil;
import com.ligongcloud.util.PasswordUtil;
import com.ligongcloud.util.ResponseUtil;
import com.ligongcloud.util.StringUtil;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
@Controller
@RequestMapping("/fileManage")
public class FileManageController {
	
	@Autowired
	private FileDao fileDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private MessageDao messageDao;
	
	@Autowired
	private DiskDao diskDao;
	
	/**
	 * 实现页面加载文件的功能，和搜索的功能，(同用户管理模块)
	 */
	@RequestMapping(value = "/loadFile")
	public String loadFile( String name,String s_screateDate,String s_ecreateDate,String page,String rows,HttpServletResponse response,HttpSession httpSession ) throws Exception {
		JSONObject result=new JSONObject();
		JsonConfig jsonConfig = new JsonConfig();

		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray jsonArray= JSONArray.fromObject(search(name, s_screateDate, s_ecreateDate, page, rows, httpSession),jsonConfig);
		Long total=fileDao.fileCount(name,s_screateDate,s_ecreateDate,httpSession);
		
		result.put("rows", jsonArray);
		result.put("total", total);
		ResponseUtil.write(response, result);
		return null;
	}
	/**
	 * 在加载文件时进行搜索
	 */
	private ArrayList<FileBean> search(String name,String s_screateDate,String s_ecreateDate,String page,String rows,HttpSession httpSession) throws Exception {
		if(page==null){
			page = "1";
			rows = "10";
		}

		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
		ArrayList<FileBean> fileBeans = fileDao.fileList(pageBean,name,s_screateDate,s_ecreateDate,httpSession);
		return fileBeans;
	}
	
	/**
	 * 上传文件
	 */
	@RequestMapping(value = "/uploadFile")
	public String uploadFile(String description, @RequestParam("attachs") MultipartFile[] attachs, HttpSession httpSession) throws Exception{
		User u = (User)httpSession.getAttribute("u");
		
		String path = DiskDao.BASELOCATION+u.getStuNo();
		
		for ( MultipartFile attach: attachs ) {
			
			if ( attach.isEmpty() ) {
				continue;
			}
			Long sizeL = attach.getSize();
			int size = KbToMUtil.kbToM( sizeL.intValue());
			
			//对该用户的网盘空间进行判断
			boolean b = diskDao.allowUpdate(u, size);
			if( !b ){
				//不能上传了
				String s = "您的网盘所余空间不足以存储这个文件，请向管理员申请网盘空间";
				httpSession.setAttribute("allowUpdate", s);
				return "webMessage";
			}
			
			String location = path+"/"+attach.getOriginalFilename();
			java.io.File f = new java.io.File(location );
			FileUtils.copyInputStreamToFile(attach.getInputStream(), f);
			
			String name = attach.getOriginalFilename();
			
			fileDao.uploadFile(path, name, size, description, httpSession);
			diskDao.updateFileNum(u);
			diskDao.updateDiskUsedSize(u);
			
			//至此，文件上传完毕，进行文件加密
			String newFile = location+"a";
			EncryptGen.encryptFile(location, newFile);
			FileUtil.deleteFile(location);
			
		}
		return "adminAndNormalFile";
	}
	
	/**
	 * 删除文件
	 */
	@RequestMapping(value = "/deleteFile")
	public String deleteFile(String delIds,HttpServletResponse response,HttpSession httpSession) throws Exception {
		User user = (User)httpSession.getAttribute("u");
		JSONObject result=new JSONObject();
		int delNums=fileDao.deleteFile(delIds,httpSession);
		diskDao.updateFileNum(user);
		diskDao.updateDiskUsedSize(user);
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
	 * 下载文件
	 */
	@RequestMapping(value = "/downloadFile")
	public String downloadFile(String delIds,Boolean overlay, HttpServletResponse response, HttpServletRequest request) throws Exception {
		
		fileDao.downloadFile(delIds,overlay, response, request);
		
		return null;   
	}
	
	/**
	 * 下载加密文件
	 */
	@RequestMapping(value = "/downloadLockFile")
	public String downloadLockFile(String lockFileId,String lockPassword, HttpServletResponse response, HttpServletRequest request) throws Exception {
		JSONObject result=new JSONObject();
		
		if( StringUtil.isEmpty(lockPassword)){
			result.put("success", "false");	
			result.put("errorMeg", "请输入密码");
			ResponseUtil.write(response, result);
			return null;
		}
		
		lockPassword = PasswordUtil.MD5Encode(lockPassword);
		
		File file = fileDao.loadFileById(lockFileId);
		String fileP = file.getPassword();
		if( !fileP.equals(lockPassword)){
			result.put("success", "false");
			result.put("errorMeg", "密码不正确");
			ResponseUtil.write(response, result);
			return null;
		}
		result.put("errorMeg", "密码正确！！");
		result.put("success", "true");
		ResponseUtil.write(response, result);
		return null;
	}
	
	/**
	 * 分享文件,这个方法中的事务处理比较多，要确保事务的原子性
	 * @throws Exception 
	 */
	@Transactional
	@RequestMapping(value = "/shareFile")
	public String shareFile(String delIds,String shareNo,Boolean overlay, HttpServletResponse response, HttpServletRequest request, HttpSession httpSession ) throws Exception{

		JSONObject result=new JSONObject();
		
		String oldLocation;
		String newLocation;
		String path;
		
		//得到接收分享文件的用户
		String shareNoStr[] = shareNo.split("\\,");
		shareNo = shareNoStr[shareNoStr.length-1];

		if( userDao.isNullByStuNo(shareNo)){
			result.put("success", "false");	
			result.put("errorMeg", "没有该用户");
			ResponseUtil.write(response, result);
			return null;
		}
		
		User user = userDao.loadUserByStuNo(shareNo);
		ArrayList<com.ligongcloud.model.File> files = fileDao.loadFileByIds(delIds);
		
		for( com.ligongcloud.model.File file : files ){
			
			boolean b = diskDao.allowUpdate(user, file.getSize());
			if( !b ){
				//String s = "您好友的网盘空间不足以存储这个文件，发送消息告诉他(她)吧!";
				//httpSession.setAttribute("allowUpdate", s);
				//return "webMessage";
				result.put("errorMeg", "您好友的网盘空间不足以存储这个文件，发送消息告诉他(她)吧!");
				result.put("success", "true");
				ResponseUtil.write(response, result);
				
				return null;
			}
			
			oldLocation = file.getLocation()+"a";
			path = DiskDao.BASELOCATION + user.getStuNo();
			newLocation = path + "\\" + file.getName()+"a";
			//把文件copy到被分享的用户那里
			CopyFileUtil.copyFile(oldLocation, newLocation, overlay);
			//将文件映射记录到被分享用户的数据库中
			fileDao.shareFile(path, file, user, httpSession);
			//在双方的消息数据库中写入分享的消息；
			messageDao.shareMessage(user, file, httpSession);
			//在接收文件的用户那里文件数量+1
			diskDao.updateFileNum(user);
			//更新接收文件用户的网盘的已使用大小。
			diskDao.updateDiskUsedSize(user);
		}

		result.put("errorMeg", "分享成功！！！！！");
		result.put("success", "true");
		ResponseUtil.write(response, result);
		
		return null;
	}
	
	/**
	 * 加密文件
	 */
	@RequestMapping(value = "/lockFile")
	public String lockFile(String fileId, String lick_password, String lick_password2, HttpSession httpSession){
		
		if( StringUtil.isEmpty(lick_password)||StringUtil.isEmpty(lick_password2) ){
			String s = "请返回输入密码";
			httpSession.setAttribute("allowUpdate", s);
			return "webMessage";
		}
		if( !lick_password.equals(lick_password2) ){
			String s = "请返回重新输入一致的密码";
			httpSession.setAttribute("allowUpdate", s);
			return "webMessage";
		}
		fileDao.lockFile(fileId,lick_password);
		return null;
	}
	
	/**
	 * 解密文件
	 */
	@RequestMapping(value = "/notlockFile")
	public String notlockFile(String notfileId, String notlick_password, HttpSession httpSession,HttpServletResponse response) throws Exception{
		JSONObject result=new JSONObject();
		
		if( StringUtil.isEmpty(notlick_password) ){
			result.put("success", "false");	
			result.put("errorMeg", "请输入密码");
			ResponseUtil.write(response, result);
			return null;
		}
		
		notlick_password = PasswordUtil.MD5Encode(notlick_password);
		
		File file = fileDao.loadFileById(notfileId);
		String fileP = file.getPassword();
		if( !fileP.equals(notlick_password)){
			result.put("success", "false");
			result.put("errorMeg", "密码不正确");
			ResponseUtil.write(response, result);
			return null;
		}
		result.put("errorMeg", "解密成功！！！！！");
		result.put("success", "true");
		ResponseUtil.write(response, result);
		fileDao.notlockFile(notfileId,notlick_password );
		return null;
	}
	
}
