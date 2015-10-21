package com.ligongcloud.util;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
public class StringUtil {

	public static boolean isEmpty(String str){
		if("".equals(str)||str==null){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isNotEmpty(String str){
		if(!"".equals(str)&&str!=null){
			return true;
		}else{
			return false;
		}
	}
}
