package com.ligongcloud.util;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
public class KbToMUtil {

	public static int kbToM( int sizeKb){
		
		int sizeM = sizeKb/1024000;
		
		if( sizeM == 0 ){
			sizeM = 1;
		}
		
		return sizeM;
		
	}
}
