package com.ligongcloud.dao;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ligongcloud.bean.DiskBean;
import com.ligongcloud.bean.PageBean;
import com.ligongcloud.model.Disk;
import com.ligongcloud.model.User;
import com.ligongcloud.util.DiskUtil;
import com.ligongcloud.util.FileUtil;
import com.ligongcloud.util.StringUtil;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
@Component("diskDao")
public class DiskDao {

	public static final String BASELOCATION = "H:\\disk\\";

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private FileDao fileDao;
	
	@Autowired
	private UserDao userDao;

	/**
	 * 开通网盘; 输入：用户、网盘大小 输出：在（网盘）中为用户新建一个以学号命名的文件夹
	 */
	public void openDisk(User user, int size) {
		System.out.println("open disk start!!");

		String diskLocation = BASELOCATION + user.getStuNo();

		Disk disk = new Disk();
		disk.setUser(user);
		disk.setTotalSize(size);
		disk.setUsedSize(0);
		disk.setDiskLocation(diskLocation);
		disk.setFileNumber(0);
		disk.setShareNumber(0);

		sessionFactory.getCurrentSession().save(disk);
		// 创建文件夹，以模拟网盘空间
		FileUtil.createFolder(diskLocation);
		System.out.println("open disk end!!");
	}

	/**
	 * 关闭网盘; 输入：网盘所有者的学号; 输出：是否删除网盘，删除为true，未删除为false
	 */
	@SuppressWarnings("unchecked")
	public int closeDisk(String delIds) {

		Query q = sessionFactory.getCurrentSession().createQuery(
				"from User u where u.id in (" + delIds + ")");
		ArrayList<User> users = (ArrayList<User>) q.list();

		for (User user : users) {
			Disk disk = user.getDisk();
			Query queryd = sessionFactory.getCurrentSession().createQuery("delete Disk d where d.id=" + disk.getId());
			queryd.executeUpdate();

			// 删除文件夹以及文件
			String diskLocation = BASELOCATION + user.getStuNo();
			FileUtil.delFolder(diskLocation);
		}

		System.out.println("关闭网盘");
		return 1;

	}

	/**
	 * 更新网盘大小 输入：用户id，网盘大小； 输出：更新网盘信息（随着用户信息的改变，用户的网盘信息页要变化）；
	 * 
	 * @param id
	 * @param diskSize
	 */
	public void updateDiskSize(int id, int diskSize) {
		String sql = "update Disk disk set disk.totalSize=? where disk.user.id=?";
		Query queryd = sessionFactory.getCurrentSession().createQuery(sql)
				.setParameter(0, diskSize)
				.setParameter(1, id);
		queryd.executeUpdate();
		System.out.println("update disk size!!");
	}
	
	/**
	 * 更新网盘路径 输入：用户id，用户学号； 输出：更新网盘信息（随着用户学号的改变，用户的网盘路径也要变化，网盘路径名与学号一致）；
	 */
	public void updateDiskLocation(int id, String stuNo) {
		//更新数据库
		String sql = "update Disk disk set disk.diskLocation=? where disk.user.id=?";
		Query queryd = sessionFactory.getCurrentSession().createQuery(sql)
				.setParameter(0, stuNo)
				.setParameter(1, id);
		queryd.executeUpdate();
		System.out.println("update disk location!!");
		
		//更新本地文件夹
		User user = userDao.loadUserById(id);
		File fileOld = new File(BASELOCATION+user.getStuNo());
		File fileNew = new File(BASELOCATION+stuNo);
		fileOld.renameTo(fileNew);
	}
	
	/**
	 * 更新user网盘中的文件数量
	 */
	public void updateFileNum( User user ){
		
		String sqlN = "select count(*) from File f where f.user.id = "+ user.getId();
		Query qN = sessionFactory.getCurrentSession().createQuery(sqlN);
		long lN = (Long)qN.uniqueResult();
		int fileNumber = (int)lN;
		
		String sql = "update Disk d set d.fileNumber=" +fileNumber+" where d.user.id="+user.getId();
		Query q = sessionFactory.getCurrentSession().createQuery(sql);
		q.executeUpdate();
	}
	
	/**
	 * 更新user网盘中的已使用大小
	 */
	public void updateDiskUsedSize( User user ){
		
		int fileTotalSize = 0;
		
		ArrayList<com.ligongcloud.model.File> files = fileDao.loadFileByUser(user);
		for( com.ligongcloud.model.File f : files ){
			fileTotalSize = fileTotalSize + f.getSize();
		}
		
		String sqlUpdateDiskUsedSize = "update Disk d set d.usedSize = "+ fileTotalSize +" where d.user.id = "+user.getId();
		Query q = sessionFactory.getCurrentSession().createQuery(sqlUpdateDiskUsedSize);
		q.executeUpdate();
		
	}

	/**
	 * 网盘数量
	 * @return
	 */
	public Long diskCount(String s_user, String s_susedSize,
			String s_eusedSize, HttpSession httpSession) {
		
		//User u = (User)httpSession.getAttribute("u");
		
		Long total;
		if( StringUtil.isNotEmpty(s_user)||StringUtil.isNotEmpty(s_susedSize)||StringUtil.isNotEmpty(s_eusedSize) ){
			//需要查询的个数
			StringBuffer sqlBuffer = new StringBuffer( "select count(*) from Disk d where 1=1 ");
			
			if(StringUtil.isNotEmpty(s_user)){
				sqlBuffer.append("and d.user.stuNo = '"+s_user +"' "  );
			}
			
			if(StringUtil.isNotEmpty(s_susedSize)){
				sqlBuffer.append("and d.usedSize >= "+s_susedSize +" "  );
			}
			
			if(StringUtil.isNotEmpty(s_eusedSize)){
				sqlBuffer.append("and d.usedSize <= "+s_eusedSize +" "  );
			}
			
			Query q = sessionFactory.getCurrentSession().createQuery( sqlBuffer.toString());
			total = (Long)q.uniqueResult();
		}else{
			//总个数
			Query q = sessionFactory.getCurrentSession().createQuery("select count(*) from Disk d ");
			total = (Long)q.uniqueResult();
		}
		System.out.println(total);
		return total;
	}

	/**
	 * 分条件加载网盘
	 */
	public ArrayList<DiskBean> diskList(PageBean pageBean, String s_user,
			String s_susedSize, String s_eusedSize, HttpSession httpSession) {

		if (StringUtil.isNotEmpty(s_user) || StringUtil.isNotEmpty(s_susedSize)
				|| StringUtil.isNotEmpty(s_eusedSize) ) {
			return this.searchDisk(s_user, s_susedSize, s_eusedSize, pageBean, httpSession);
		}else{
			return this.loadPageDisk(pageBean,httpSession);
		}
	}

	/**
	 * 加载所有网盘，即，没有查询的情况下
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<DiskBean> loadPageDisk(PageBean pageBean,
			HttpSession httpSession) {

		//User u = (User)httpSession.getAttribute("u");
		Query q = sessionFactory.getCurrentSession().createQuery("from Disk d ");
		q.setFirstResult(pageBean.getStart());   
		q.setMaxResults(pageBean.getRows());  
		ArrayList<Disk> disks = (ArrayList<Disk>) q.list();
		ArrayList<DiskBean> diskBeans = DiskUtil.formatDisk(disks);
		return diskBeans;
	}

	/**
	 * 查询网盘，
	 */
	@SuppressWarnings("unchecked")
	private ArrayList<DiskBean> searchDisk(String s_user,
			String s_susedSize, String s_eusedSize, PageBean pageBean,
			HttpSession httpSession) {
		
		//User u = (User)httpSession.getAttribute("u");
		
		StringBuffer sqlBuffer = new StringBuffer( "from Disk d where 1=1 ");
		
		if(StringUtil.isNotEmpty(s_user)){
			sqlBuffer.append("and d.user.stuNo = '"+s_user +"' "  );
		}
		
		if(StringUtil.isNotEmpty(s_susedSize)){
			sqlBuffer.append("and d.usedSize >= "+s_susedSize +" "  );
		}
		
		if(StringUtil.isNotEmpty(s_eusedSize)){
			sqlBuffer.append("and d.usedSize <= "+s_eusedSize +" "  );
		}
		
		Query q = sessionFactory.getCurrentSession().createQuery(sqlBuffer.toString());
		q.setFirstResult(pageBean.getStart());   
		q.setMaxResults(pageBean.getRows());
		
		ArrayList<Disk> disks = (ArrayList<Disk>) q.list();
		ArrayList<DiskBean> diskBeans = DiskUtil.formatDisk(disks);
		return diskBeans;
	}

	/**
	 * 根据用户得到该用户的网盘
	 * @param u
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Disk loadDiskByUser(int userId){
		String sql = "From Disk d where d.user.id = " + userId;
		Query q = sessionFactory.getCurrentSession().createQuery(sql);
		ArrayList<Disk> disks = (ArrayList<Disk>) q.list();
		return disks.get(0);
	}
	
	/**
	 * user的网盘空间能否放进size大小的文件.?????????????????????????????????????????????？？？？？？？？？？？？？？？？？？？？？？？？
	 * @param u 网盘所有者
	 * @param size 上传或分享文件的大小
	 */
	public boolean allowUpdate(User u, int size) {
		/*		????????????????????????????u.getDisk().getUsedSize();值咋不变,为什么
		int diskUsedSize = u.getDisk().getUsedSize();*/
		
		Disk d= this.loadDiskByUser(u.getId());
		int diskSize = d.getTotalSize();
		int diskUsedSize = d.getUsedSize(); 
		
		if( diskSize >= diskUsedSize+size ){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 返回用户网盘已使用容量在"~100","101-200","201-300","301-400","401-500","500~"这些阶段的数量
	 */
	public ArrayList<Integer> analyseDiskSizeNum() {
		String sql1 = "select count(*) from Disk d where d.usedSize<=100";
		String sql2 = "select count(*) from Disk d where d.usedSize>=101 and d.usedSize<=200";
		String sql3 = "select count(*) from Disk d where d.usedSize>=201 and d.usedSize<=300";
		String sql4 = "select count(*) from Disk d where d.usedSize>=301 and d.usedSize<=400";
		String sql5 = "select count(*) from Disk d where d.usedSize>=401 and d.usedSize<=500";
		String sql6 = "select count(*) from Disk d where d.usedSize>=501";
		return this.analyse(sql1, sql2, sql3, sql4, sql5, sql6);
	}
	
	/**
	 * 返回用户网盘存储文件数量在"~20","21-40","41-60","61-80","81-100","101~"这些阶段的数量
	 */
	public ArrayList<Integer> analyseFileSave() {
		String sql1 = "select count(*) from Disk d where d.fileNumber<=20";
		String sql2 = "select count(*) from Disk d where d.fileNumber>=21 and d.fileNumber<=40";
		String sql3 = "select count(*) from Disk d where d.fileNumber>=41 and d.fileNumber<=60";
		String sql4 = "select count(*) from Disk d where d.fileNumber>=61 and d.fileNumber<=80";
		String sql5 = "select count(*) from Disk d where d.fileNumber>=81 and d.fileNumber<=100";
		String sql6 = "select count(*) from Disk d where d.fileNumber>=101";
		return this.analyse(sql1, sql2, sql3, sql4, sql5, sql6);
	}
	
	/**
	 * 返回用户网盘存储文件数量在"~20","21-40","41-60","61-80","81-100","101~"这些阶段的数量
	 */
	public ArrayList<Integer> analyseFileShare() {
		String sql1 = "select count(*) from Disk d where d.shareNumber<=20";
		String sql2 = "select count(*) from Disk d where d.shareNumber>=21 and d.shareNumber<=40";
		String sql3 = "select count(*) from Disk d where d.shareNumber>=41 and d.shareNumber<=60";
		String sql4 = "select count(*) from Disk d where d.shareNumber>=61 and d.shareNumber<=80";
		String sql5 = "select count(*) from Disk d where d.shareNumber>=81 and d.shareNumber<=100";
		String sql6 = "select count(*) from Disk d where d.shareNumber>=101";
		return this.analyse(sql1, sql2, sql3, sql4, sql5, sql6);
	}
	
	/**
	 * 供数据柱状图分析创建的私有方法.参数为sql语句.
	 */
	private ArrayList<Integer> analyse(String sql1,String sql2,String sql3,String sql4,String sql5,String sql6){
		Query q1 = sessionFactory.getCurrentSession().createQuery(sql1);
		long numL1 = (Long)q1.uniqueResult();
		int num1 = (int)numL1;
		
		Query q2 = sessionFactory.getCurrentSession().createQuery(sql2);
		long numL2 = (Long)q2.uniqueResult();
		int num2 = (int)numL2;
		
		Query q3 = sessionFactory.getCurrentSession().createQuery(sql3);
		long numL3 = (Long)q3.uniqueResult();
		int num3 = (int)numL3;
		
		Query q4 = sessionFactory.getCurrentSession().createQuery(sql4);
		long numL4 = (Long)q4.uniqueResult();
		int num4 = (int)numL4;
		
		Query q5 = sessionFactory.getCurrentSession().createQuery(sql5);
		long numL5 = (Long)q5.uniqueResult();
		int num5 = (int)numL5;
		
		Query q6 = sessionFactory.getCurrentSession().createQuery(sql6);
		long numL6 = (Long)q6.uniqueResult();
		int num6 = (int)numL6;
		
		ArrayList<Integer> nums = new ArrayList<Integer>();
		nums.add(num1);
		nums.add(num2);
		nums.add(num3);
		nums.add(num4);
		nums.add(num5);
		nums.add(num6);
		
		return nums;
	}

}
