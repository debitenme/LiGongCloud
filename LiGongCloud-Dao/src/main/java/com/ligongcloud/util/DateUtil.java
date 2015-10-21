package com.ligongcloud.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
public class DateUtil {

	/**
	 * 将时间类型转换成字符串类型
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDate(Date date,String format){
		String result="";
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		if(date!=null){
			result=sdf.format(date);
		}
		return result;
	}
	
	/**
	 * 将字符串类型转换成时间类型
	 * @param str
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public static Date formatString(String str,String format) throws Exception{
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.parse(str);
	}
}
