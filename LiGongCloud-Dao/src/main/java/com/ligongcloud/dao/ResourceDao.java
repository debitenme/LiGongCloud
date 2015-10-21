package com.ligongcloud.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ligongcloud.bean.PageBean;
import com.ligongcloud.bean.ResourceBean;
import com.ligongcloud.model.File;
import com.ligongcloud.util.FileUtil;
import com.ligongcloud.util.StringUtil;

@Component("resourceDao")
public class ResourceDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	public Long resourceCount(String name, String userStuNo,
			String s_screateDate, String s_ecreateDate, HttpSession httpSession) throws Exception {

		Long total;
		if( StringUtil.isNotEmpty(name)||StringUtil.isNotEmpty(userStuNo)||StringUtil.isNotEmpty(s_screateDate)||StringUtil.isNotEmpty(s_ecreateDate) ){
			//需要查询的个数
			StringBuffer sqlBuffer = new StringBuffer( "select count(*) from File file where 1=1 ");
			
			if(StringUtil.isNotEmpty(name)){
				sqlBuffer.append("and file.name like '%"+name +"%' "  );
			}
			
			if(StringUtil.isNotEmpty(userStuNo)){
				sqlBuffer.append("and file.user.stuNo = '"+userStuNo +"' "  );
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
			Query q = sessionFactory.getCurrentSession().createQuery("select count(*) from File file ");
			total = (Long)q.uniqueResult();
		}
		System.out.println(total);
		return total;
	
	}

	public ArrayList<ResourceBean> resourceList(PageBean pageBean, String name,
			String userStuNo, String s_screateDate, String s_ecreateDate,
			HttpSession httpSession) throws Exception {
		
		if( StringUtil.isNotEmpty(name)||StringUtil.isNotEmpty(userStuNo)||StringUtil.isNotEmpty(s_ecreateDate)||StringUtil.isNotEmpty(s_screateDate) ){
			return this.searchFile(name, userStuNo, s_screateDate, s_ecreateDate, pageBean,httpSession);
		}else{
			return this.loadPageFile(pageBean,httpSession);
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<ResourceBean> searchFile(String name, String userStuNo,
			String s_screateDate, String s_ecreateDate, PageBean pageBean,
			HttpSession httpSession) throws Exception {
		
		StringBuffer sqlBuffer = new StringBuffer( "from File file where 1=1 ");
		
		if(StringUtil.isNotEmpty(name)){
			sqlBuffer.append("and file.name like '%"+name +"%' " );
		}
		
		if(StringUtil.isNotEmpty(userStuNo)){
			sqlBuffer.append("and file.user.stuNo = '"+userStuNo +"' " );
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
		ArrayList<ResourceBean> resourceBeans = FileUtil.formatResource(files);
		return resourceBeans;
		
	}

	@SuppressWarnings("unchecked")
	private ArrayList<ResourceBean> loadPageFile(PageBean pageBean,
			HttpSession httpSession) {
		
		Query q = sessionFactory.getCurrentSession().createQuery("from File file");
		q.setFirstResult(pageBean.getStart());   
		q.setMaxResults(pageBean.getRows());  
		ArrayList<com.ligongcloud.model.File> files = (ArrayList<com.ligongcloud.model.File>) q.list();
		ArrayList<ResourceBean> resourceBeans = FileUtil.formatResource(files);
		return resourceBeans;
	}

}
