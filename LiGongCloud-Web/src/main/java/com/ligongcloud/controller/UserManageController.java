package com.ligongcloud.controller;

import java.io.OutputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ligongcloud.bean.PageBean;
import com.ligongcloud.bean.UserBean;
import com.ligongcloud.dao.DiskDao;
import com.ligongcloud.dao.FileDao;
import com.ligongcloud.dao.MessageDao;
import com.ligongcloud.dao.UserDao;
import com.ligongcloud.model.Disk;
import com.ligongcloud.model.User;
import com.ligongcloud.util.PasswordUtil;
import com.ligongcloud.util.ResponseUtil;
import com.ligongcloud.util.StringUtil;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
@Controller
@RequestMapping("/userManage")
public class UserManageController {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private DiskDao diskDao;
	
	@Autowired
	private FileDao fileDao;
	
	@Autowired
	private MessageDao messageDao;
	
	/**
	 * 搜索的实现方式！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
	 * 实现页面加载用户的功能，和搜索的功能，
	 * 搜索的实现方式就是判断搜索框内是否有参数(2015.03.21)
	 */
	@RequestMapping(value = "/loadUser")
	public String loadUser(String s_stuNo,String s_sjoindate,String s_ejoindate,String page,String rows,HttpServletResponse response) throws Exception {
		JSONObject result=new JSONObject();
		JsonConfig jsonConfig = new JsonConfig();

		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray jsonArray= JSONArray.fromObject(search(s_stuNo, s_sjoindate, s_ejoindate, page, rows),jsonConfig);
		Long total=userDao.userCount(s_stuNo,s_sjoindate,s_ejoindate);
		
		result.put("rows", jsonArray);
		result.put("total", total);
		ResponseUtil.write(response, result);
		return null;
	}
	/**
	 * 在加载用户时进行搜索
	 */
	private ArrayList<UserBean> search(String s_stuNo,String s_sjoindate,String s_ejoindate,String page,String rows) throws Exception {
		if(page==null){
			page = "1";
			rows = "10";
		}

		PageBean pageBean=new PageBean(Integer.parseInt(page),Integer.parseInt(rows));
		ArrayList<UserBean> userBeans = userDao.userList(pageBean,s_stuNo,s_sjoindate,s_ejoindate);
		return userBeans;
	}

	@RequestMapping(value = "/deleteUser")
	public String deleteUser(String delIds,HttpServletResponse response) throws Exception {
		//先关闭网盘，在删除用户，有外键
		JSONObject result=new JSONObject();
		//删除该用户的disk数据库映射以及网盘中的文件
		int delNumss = diskDao.closeDisk(delIds);
		//删除该用户的文件数据库映射
		fileDao.deleteFileByUserIds(delIds );
		//删除该用户的消息数据库映射
		messageDao.deleteMessageByUserIds(delIds);
		//删除该用户的数据库映射
		int delNums=userDao.deleteUser(delIds);
		if(delNums>0 && delNumss>0 ){
			result.put("success", "true");
			result.put("delNums", delNums);
		}else{
			result.put("errorMeg", "删除失败");
		}
		result.put("delNum", delNums);
		ResponseUtil.write(response, result);

		return null;

	}
	
	@RequestMapping(value = "/addUser")
	public String addUser(String stuNo,String password,String username,int gender,String joindate,String email,int diskSize,HttpServletResponse response) throws Exception{
		
		JSONObject result=new JSONObject();
		//如果该学号存在
		if( !userDao.isNullByStuNo(stuNo) ){
			result.put("success", "false");	
			result.put("errorMeg", "该用户已存在");
			ResponseUtil.write(response, result);
			return null;
		}
		
		//当前台性别为“未选择”时的后台处理
		if( gender == 10 ){
			gender = 1;
		}
		
		User user = new User();
		user.setStuNo(stuNo);
		user.setPassword(PasswordUtil.MD5Encode(password));
		user.setUsername(username);
		user.setGender(gender);
		user.setJoindate(joindate);
		user.setEmail(email);
		
		userDao.saveUser(user);
		diskDao.openDisk(user, diskSize);
		
		result.put("errorMeg", "注册成功！！！！！");
		result.put("success", "true");
		ResponseUtil.write(response, result);
		
		return null;
	}
	
	@RequestMapping(value = "/updateUser")
	public String updateUser(int uid,String ustuNo,String upassword,String uusername,int ugender,String ujoindate,String uemail,int udiskSize,HttpServletResponse response) throws Exception{
		
		JSONObject result=new JSONObject();
		//如果修改了学号、并且修改后的学号存在
		if(!userDao.loadUserById(uid).getStuNo().equals(ustuNo)&&!userDao.isNullByStuNo(ustuNo) ){
			result.put("success", "false");	
			result.put("errorMeg", "该用户已存在");
			ResponseUtil.write(response, result);
			return null;
		}
		
		//当前台性别为“未选择”时的后台处理
		if( ugender == 10 ){
			ugender = 1;
		}
		
		//先updateDisk，后updateUser
		diskDao.updateDiskSize(uid,udiskSize);
		diskDao.updateDiskLocation(uid, ustuNo);
		userDao.updateUser(uid, ustuNo, PasswordUtil.MD5Encode(upassword), uusername, ugender, ujoindate, uemail);
		
		result.put("errorMeg", "修改成功！！！！！");
		result.put("success", "true");
		ResponseUtil.write(response, result);
		
		return null;
	}

	@RequestMapping(value = "/exportExcel")
	public void exportExcel(String s_stuNo,String s_sjoindate,String s_ejoindate,String page,String rows,HttpServletResponse response) throws Exception{	
		//ArrayList<User> users = userDao.loadAllUser(); //userDao.getData(search());
		ArrayList<UserBean> userBeans =this.search(s_stuNo, s_sjoindate, s_ejoindate, page, rows); //UserUtil.formatUser(users);
		HSSFWorkbook workbook = new HSSFWorkbook();	
		try {
			HSSFSheet sheet = workbook.createSheet("user");	
			HSSFRow row = sheet.createRow(0);
			String[] cellTitle = {"ID", "学号", "姓名", "邮箱","参加日期", "性别", "网盘大小","已使用网盘容量","文件总数量","文件分享数量"};	
			for (int i = 0; i < cellTitle.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellValue(cellTitle[i]);
			}
			
			for(int rowIndex=0; rowIndex<userBeans.size(); rowIndex++){
				row = sheet.createRow(rowIndex+1);	
				HSSFCell cell = row.createCell(rowIndex);
				cell.setCellValue(cellTitle[rowIndex]);
				UserBean ub = userBeans.get(rowIndex);
				Disk d = diskDao.loadDiskByUser(ub.getId());
				for(int cellnum = 0; cellnum < 10; cellnum++){
					cell = row.createCell(cellnum);
					switch(cellnum){
					case 0:
						cell.setCellValue(ub.getId());
						break;
					case 1:
						cell.setCellValue(ub.getStuNo());
						break;
					case 2:
						cell.setCellValue(ub.getUsername());
						break;
					case 3:
						cell.setCellValue(ub.getEmail());
						break;
					case 4:
						cell.setCellValue(ub.getJoindate());
						break;
					case 5:
						cell.setCellValue(ub.getGender());
						break;
					case 6:
						cell.setCellValue(ub.getDiskSize());
						break;
					case 7:
						cell.setCellValue(d.getUsedSize());
						break;
					case 8:
						cell.setCellValue(d.getFileNumber());
						break;
					case 9:
						cell.setCellValue(d.getShareNumber());
						break;
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}	
		String exportFileName = "网盘用户情况列表.xls";
		
		response.setHeader("Content-Disposition", "attachment;filename=" + new String((exportFileName).getBytes(), "ISO8859-1"));//设定输出文件头
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 定义输出类型
		
		OutputStream out = response.getOutputStream();
		workbook.write(out);
		out.flush();
		out.close();
	}
	
	
	@RequestMapping(value = "/reviseUser")
	public String reviseUser(int id,String username,String password,String password2,String email,int gender,HttpSession httpSession ){
		
		if( !password.equals(password2) ){
			System.out.println("密码不一致");
			return "normalMain";
		}
		if( StringUtil.isEmpty(password)||StringUtil.isEmpty(password2) ){
			System.out.println("密码不能为空");
			return "normalMain";
		}
		
		UserBean u = (UserBean)httpSession.getAttribute("uBean");
		User user = userDao.updateUser(id, u.getStuNo(), PasswordUtil.MD5Encode(password), username, gender, u.getJoindate(), email); 

		u.setDiskSize(user.getDisk().getTotalSize());
		u.setEmail(user.getEmail());
		String genderStr;
		if( user.getGender() == 1 ){
			genderStr = "男";
		}else{
			genderStr = "女";
		}
		u.setGender(genderStr);
		u.setId(id);
		u.setJoindate(user.getJoindate());
		u.setPassword(PasswordUtil.MD5Encode(password));
		u.setStuNo(user.getStuNo());
		u.setUsername(username);
		
		httpSession.setAttribute("uBean", u);
		
		return "normalMain";
	}
	
}
