package com.ligongcloud.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ligongcloud.bean.FileBean;
import com.ligongcloud.bean.ResourceBean;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
public class FileUtil {
	/**
	 * 删除指定文件夹下所有文件; 输入：文件夹完整绝对路径; 输出：是否删除文件夹下所有文件;
	 * 
	 * @param path
	 * @return
	 */
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				System.out.println("删除指定文件夹下所有文件");
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * 删除文件夹以及文件夹下所有文件; 输入：文件夹完整绝对路径; 输出：;
	 * 
	 * @param folderPath
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // 删除空文件夹
			System.out.println("删除文件夹以及文件夹下所有文件");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 删除指定单个文件
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
				System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}
	
	/**
	 * 创建文件夹，用来模拟网盘; 输入：文件夹完整绝对路径; 输出：是否创建文件夹;
	 * 
	 * @param folderPath
	 * @return boolean
	 */
	public static boolean createFolder(String folderPath) {
		File filePath = new File(folderPath);
		if (filePath.isDirectory()) {
			System.out.println("网盘（文件夹）已经存在");
			return false;
		} else {
			filePath.mkdir();
			System.out.println("网盘（文件夹）创建成功");
			return true;
		}
	}
	
	/**
	 * 下载解密后的文件
	 * @param file 要下载的文件
	 */
	public static boolean downloadFile(com.ligongcloud.model.File file,HttpServletResponse response, HttpServletRequest request) throws Exception{
		
		String location = file.getLocation();
		String locationa = file.getLocation()+"a";

		//解密文件
		EncryptGen.decryptFile(locationa, location);
		
		//设置响应头，控制浏览器下载该文件
		response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
		//读取要下载的文件，保存到文件输入流
		FileInputStream in = new FileInputStream(location);
		//创建输出流
		OutputStream out = response.getOutputStream();
		//创建缓冲区
		byte buffer[] = new byte[1024];
		int len = 0;
		//循环将输入流中的内容读取到缓冲区当中
		while((len=in.read(buffer))>0){
			//输出缓冲区的内容到浏览器，实现文件下载
			out.write(buffer, 0, len);
		}
		//关闭文件输入流
		in.close();
		//关闭输出流
		out.close();
		
		//删除解密并下载完成的文件
		FileUtil.deleteFile(location);
		
		return true;
	}
	
	/**
	 * 将file转换成FileBean以用于页面展示
	 * @param users
	 * @return
	 */
	public static ArrayList<FileBean> formatFile(ArrayList<com.ligongcloud.model.File> files){	//指明泛型时要注意。。。
		ArrayList<FileBean> fileBeans = new ArrayList<FileBean>();
		for( int i=0; i<files.size(); i++ ){
			FileBean fileBean = new FileBean();
			com.ligongcloud.model.File file = files.get(i);
			
			fileBean.setCreateDate(file.getCreateDate());
			fileBean.setDescription(file.getDescription());
			fileBean.setId(file.getId());
			fileBean.setLocation(file.getLocation());
			fileBean.setName(file.getName());
			fileBean.setPassword(file.getPassword());
			fileBean.setShareDownload(file.getShareDownload());
			fileBean.setShareUrl(file.getShareUrl());
			fileBean.setSize(file.getSize());
			fileBean.setType(file.getType());
			fileBean.setPath(file.getPath());
			String strShare;
			String strLock;
			if( file.getIsShare() == 1 ){
				strShare ="是";
			}else{
				strShare ="否";
			}
			
			if( file.getIsLock() == 1 ){
				strLock ="是";
			}else{
				strLock ="否";
			}
			fileBean.setIsLock(strLock);
			fileBean.setIsShare(strShare);
			fileBeans.add(fileBean);
		}
		return fileBeans;
	}

	public static ArrayList<ResourceBean> formatResource(
			ArrayList<com.ligongcloud.model.File> files) {

		ArrayList<ResourceBean> resourceBeans = new ArrayList<ResourceBean>();
		for( int i=0; i<files.size(); i++ ){
			ResourceBean resourceBean = new ResourceBean();
			com.ligongcloud.model.File file = files.get(i);
			
			resourceBean.setUserStuNo(file.getUser().getStuNo());
			resourceBean.setCreateDate(file.getCreateDate());
			resourceBean.setDescription(file.getDescription());
			resourceBean.setId(file.getId());
			resourceBean.setLocation(file.getLocation());
			resourceBean.setName(file.getName());
			resourceBean.setPassword(file.getPassword());
			resourceBean.setShareDownload(file.getShareDownload());
			resourceBean.setShareUrl(file.getShareUrl());
			resourceBean.setSize(file.getSize());
			resourceBean.setType(file.getType());
			resourceBean.setPath(file.getPath());
			String strShare;
			String strLock;
			if( file.getIsShare() == 1 ){
				strShare ="是";
			}else{
				strShare ="否";
			}
			
			if( file.getIsLock() == 1 ){
				strLock ="是";
			}else{
				strLock ="否";
			}
			resourceBean.setIsLock(strLock);
			resourceBean.setIsShare(strShare);
			resourceBeans.add(resourceBean);
		}
		return resourceBeans;
	}

}
