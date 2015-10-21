package com.ligongcloud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 没有实际意义，当highchart图出来后，图形的右上角有选项回到此controller。
 * @author HeJiaWang
 * @version 2.0
 */
@Controller
public class Export {
	
	@RequestMapping(value = "/export")
	public String export(){
		//return "redirect: diskManage/exportHighCharts";
		return "adminDiskManage";
	}
}
