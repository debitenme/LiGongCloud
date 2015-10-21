package com.ligongcloud.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ligongcloud.bean.FileBean;
import com.ligongcloud.bean.PageBean;
import com.ligongcloud.model.File;
import com.ligongcloud.model.User;
import com.ligongcloud.util.FileUtil;
import com.ligongcloud.util.PasswordUtil;
import com.ligongcloud.util.StringUtil;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
@Component("fileDao")
public class FileDao {
	
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 返回满足条件的文件数量(方法思路同UserDao中的userCount)
	 * (注意：File数据库中的name字段有".",不要忘了查询时的单引号)
	 */
	public Long fileCount(String name,String s_screateDate,String s_ecreateDate,HttpSession httpSession) throws Exception {
		
		User u = (User)httpSession.getAttribute("u");
		
		Long total;
		if( StringUtil.isNotEmpty(name)||StringUtil.isNotEmpty(s_screateDate)||StringUtil.isNotEmpty(s_ecreateDate) ){
			//需要查询的个数
			StringBuffer sqlBuffer = new StringBuffer( "select count(*) from File file where file.user.id="+u.getId() + " ");
			
			if(StringUtil.isNotEmpty(name)){
				sqlBuffer.append("and file.name = '"+name +"' "  );
			}
			
			if(StringUtil.isEmpty(s_screateDate)){
				s_screateDate = "2010-01-01";
			}	
			
			if(StringUtil.isEmpty(s_ecreateDate)){
				//如果s_ejoindate为空，设s_ejoindate为当前系统时间
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				s_ecreateDate = df.format(new Date()) ;
			}
			
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
			Date begin = sdf.parse(s_screateDate);
			Date end = sdf.parse(s_ecreateDate);
			
			sqlBuffer.append("and file.createDate > :beginTime and file.createDate <= :endTime");
			Query q = sessionFactory.getCurrentSession().createQuery( sqlBuffer.toString()).setTimestamp("beginTime",begin).setTimestamp("endTime",end);
			total = (Long)q.uniqueResult();
		}else{
			//总个数
			Query q = sessionFactory.getCurrentSession().createQuery("select count(*) from File file where file.user.id="+u.getId());
			total = (Long)q.uniqueResult();
		}
		System.out.println(total);
		return total;
	}

	/**
	 * 加载所有文件或查询文件
	 */
	public ArrayList<FileBean> fileList(PageBean pageBean, String name,String s_screateDate,String s_ecreateDate,HttpSession httpSession) throws Exception {
		
		if( StringUtil.isNotEmpty(name)||StringUtil.isNotEmpty(s_ecreateDate)||StringUtil.isNotEmpty(s_screateDate) ){
			return this.searchFile(name, s_screateDate, s_ecreateDate, pageBean,httpSession);
		}else{
			return this.loadPageFile(pageBean,httpSession);
		}
	}
	
	/**
	 * 查询文件;  
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<FileBean> searchFile(String name,String s_screateDate,String s_ecreateDate,PageBean pageBean,HttpSession httpSession) throws Exception{
		User u = (User)httpSession.getAttribute("u");
		
		StringBuffer sqlBuffer = new StringBuffer( "from File file where file.user.id="+u.getId()+" ");
		
		if(StringUtil.isNotEmpty(name)){
			sqlBuffer.append("and file.name = '"+name +"' " );
		}
		
		if(StringUtil.isEmpty(s_screateDate)){
			s_screateDate = "2010-01-01";
		}	
		
		if(StringUtil.isEmpty(s_ecreateDate)){
			//如果s_ejoindate为空，设s_ejoindate为当前系统时间
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			s_ecreateDate = df.format(new Date()) ;
		}
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
		Date begin = sdf.parse(s_screateDate);
		Date end = sdf.parse(s_ecreateDate);
		
		sqlBuffer.append("and file.createDate > :beginTime and file.createDate <= :endTime");
		Query q = sessionFactory.getCurrentSession().createQuery(sqlBuffer.toString()).setTimestamp("beginTime",begin).setTimestamp("endTime",end);
		q.setFirstResult(pageBean.getStart());   
		q.setMaxResults(pageBean.getRows());
		
		ArrayList<File> files = (ArrayList<File>) q.list();
		ArrayList<FileBean> fileBeans = FileUtil.formatFile(files);
		return fileBeans;
	}

	/**
	 * 分页加载所有文件; 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<FileBean> loadPageFile( PageBean pageBean,HttpSession httpSession ) {
		User u = (User)httpSession.getAttribute("u");
		Query q = sessionFactory.getCurrentSession().createQuery("from File file where file.user.id="+u.getId());
		q.setFirstResult(pageBean.getStart());   
		q.setMaxResults(pageBean.getRows());  
		ArrayList<com.ligongcloud.model.File> files = (ArrayList<com.ligongcloud.model.File>) q.list();
		ArrayList<FileBean> fileBeans = FileUtil.formatFile(files);
		return fileBeans;
	}
	
	/**
	 * 上传文件
	 * @return
	 */
	public Boolean uploadFile(String path, String name, int size, String description,HttpSession httpSession ){
		
		//上传时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String createDate = df.format(new Date()) ;
		//上传者
		User user = (User)httpSession.getAttribute("u");
		//文件的location
		String location = path + "\\" + name; 
		//文件的类型type
		String typeStr[] = name.split("\\.");
		String type = typeStr[typeStr.length-1];
		
		File file = new File();
		file.setUser(user);
		file.setSize(size);
		file.setPath(path);
		file.setName(name);
		file.setType(type);
		file.setPassword("");
		file.setIsLock(0);
		file.setIsShare(0);
		file.setShareDownload(0);
		file.setDescription(description);
		file.setLocation(location);
		file.setShareUrl("");
		file.setCreateDate(createDate);
		
		sessionFactory.getCurrentSession().save(file);
		
		return true;
	}
	/**
	 * 分享文件
	 */
	public void shareFile(String path, File file, User user, HttpSession httpSession) {
		//分享时间
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String createDate = df.format(new Date()) ;
		//文件的location
		String location = path + "\\" + file.getName(); 
		//文件的类型type
		String type = file.getType();
		//文件的描述
		User u = (User)httpSession.getAttribute("u");
		String description = u.getStuNo()+"分享给我的文件";
		
		File fileShare = new File();
		fileShare.setUser(user);
		fileShare.setSize(file.getSize());
		fileShare.setPath(path);
		fileShare.setName(file.getName());
		fileShare.setType(type);
		fileShare.setPassword("");
		fileShare.setIsLock(0);
		fileShare.setIsShare(0);
		fileShare.setShareDownload(0);
		fileShare.setDescription(description);
		fileShare.setLocation(location);
		fileShare.setShareUrl("");
		fileShare.setCreateDate(createDate);
		
		sessionFactory.getCurrentSession().save(fileShare);
		
		//更改被分享的文件属性，是否被分享
		String sql = "update File f set f.isShare=1 where f.id="+file.getId();
		Query q = sessionFactory.getCurrentSession().createQuery(sql);
		q.executeUpdate();
		
		//更改被分享的文件属性，被分享次数
		int shareNun = file.getShareDownload()+1;
		String sqlS = "update File f set f.shareDownload="+shareNun+"  where f.id="+file.getId();
		Query qS = sessionFactory.getCurrentSession().createQuery(sqlS);
		qS.executeUpdate();
		
		//分享文件后，网盘中的分享文件数量要更改
		//得到网盘中的分享文件数量
		String sqlN = "select count(*) from File f where f.user.id="+u.getId()+" and f.isShare=1";
		Query qN = sessionFactory.getCurrentSession().createQuery(sqlN);
		long shareNumL = (Long) qN.uniqueResult();
		int shareNum = (int)shareNumL;
		
		//更新网盘分享文件的数量
		String sqlSD = "update Disk d set d.shareNumber=" +shareNum+" where d.user.id="+u.getId();
		Query qSD = sessionFactory.getCurrentSession().createQuery(sqlSD);
		qSD.executeUpdate();
	}
	
	/**
	 * 删除文件(一定要注意删除本地本件和删除数据库中数据的先后顺序)
	 */
	@SuppressWarnings("unchecked")
	public int deleteFile(String delIds, HttpSession httpSession){
		//注意：要先找出这些文件，根据数据库中的数据进行本地文件的删除，若先删除数据库中的数据，就没有映射进行本地文件的删除了
		//删除本地文件（即删除网盘中的文件）
		Query q = sessionFactory.getCurrentSession().createQuery("from File file where file.id in (" + delIds + ")");
		ArrayList<File> files = (ArrayList<File>) q.list();
		String fileName;
		for( File file : files ){
			fileName = file.getLocation()+"a";
			FileUtil.deleteFile(fileName);
		}
		//删除数据库中的映射
		Query queryu = sessionFactory.getCurrentSession().createQuery(
				"delete File file where file.id in (" + delIds + ")");
		queryu.executeUpdate();
		
		return 1;
	}
	
	/**
	 * 下载文件
	 */
	public void downloadFile(String delIds,Boolean overlay,HttpServletResponse response, HttpServletRequest request) throws Exception{
		ArrayList<File> files = this.loadFileByIds(delIds);
		
		for( File file : files ){
			FileUtil.downloadFile(file, response, request);
		}
	}
	
	/**
	 * 加载指定ID集合文件; 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<File> loadFileByIds(String ids) {
		Query q = sessionFactory.getCurrentSession().createQuery(
				"from File f where f.id in (" + ids + ")");
		ArrayList<File> files = (ArrayList<File>) q.list();
		return files;
	}
	
	/**
	 * 加载指定ID文件; 
	 */
	@SuppressWarnings("unchecked")
	public File loadFileById(String id) {
		int idF = Integer.parseInt(id);
		Query q = sessionFactory.getCurrentSession().createQuery(
				"from File f where f.id =" +idF);
		ArrayList<File> files = (ArrayList<File>) q.list();
		return files.get(0);
	}
	
	/**
	 * 加载指定userde的文件; 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<File> loadFileByUser(User user) {
		String sqlFile = "from File f where f.user.id = "+ user.getId();
		Query qFile = sessionFactory.getCurrentSession().createQuery(sqlFile);
		ArrayList<com.ligongcloud.model.File> files = (ArrayList<com.ligongcloud.model.File>) qFile.list();
		return files;
	}

	/**
	 * 加密文件
	 */
	public void lockFile(String fileId, String lick_password) {
		
		int id = Integer.parseInt(fileId);
		
		String sqlL = "update File f set f.isLock=1 where f.id="+id;
		Query qL = sessionFactory.getCurrentSession().createQuery(sqlL);
		qL.executeUpdate();
		
		String sqlp = "update File f set f.password = '"+PasswordUtil.MD5Encode(lick_password)+"' where f.id="+id;
		Query qp = sessionFactory.getCurrentSession().createQuery(sqlp);
		qp.executeUpdate();
	}

	/**
	 * 解密文件
	 */
	public void notlockFile(String notfileId, String notlick_password) {
		
		int id = Integer.parseInt(notfileId);
		
		String sqlL = "update File f set f.isLock=0 where f.id="+id;
		Query qL = sessionFactory.getCurrentSession().createQuery(sqlL);
		qL.executeUpdate();
		
		String sqlp = "update File f set f.password ='' where f.id="+id;
		Query qp = sessionFactory.getCurrentSession().createQuery(sqlp);
		qp.executeUpdate();
		
	}
	
	/**
	 * 删除指定用户Id集合的文件的数据库文件
	 */
	public void deleteFileByUserIds(String delIds) {
			
		Query queryd = sessionFactory.getCurrentSession().createQuery("delete File f where f.user.id in (" + delIds +")");
		queryd.executeUpdate();

	}

}
