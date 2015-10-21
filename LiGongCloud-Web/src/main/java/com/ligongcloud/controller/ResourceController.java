package com.ligongcloud.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ligongcloud.bean.PageBean;
import com.ligongcloud.bean.ResourceBean;
import com.ligongcloud.dao.ResourceDao;
import com.ligongcloud.util.ResponseUtil;

/**
 * 
 * @author HeJiaWang
 * @version 3.0
 */
@Controller
@RequestMapping("/resource")
public class ResourceController {
	
	@Autowired
	private ResourceDao resourceDao;
	
	@RequestMapping(value = "/loadResource")
	public String loadAllFile(String name, String userStuNo,
			String s_screateDate, String s_ecreateDate, String page,
			String rows, HttpServletResponse response, HttpSession httpSession)
			throws Exception {
		
		JSONObject result = new JSONObject();
		JsonConfig jsonConfig = new JsonConfig();

		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray jsonArray = JSONArray.fromObject(
				search(name, userStuNo, s_screateDate, s_ecreateDate, page,
						rows, httpSession), jsonConfig);
		
		Long total = resourceDao.resourceCount(name, userStuNo, s_screateDate,
				s_ecreateDate, httpSession);

		result.put("rows", jsonArray);
		result.put("total", total);
		ResponseUtil.write(response, result);
		return null;
	}

	private ArrayList<ResourceBean> search(String name, String userStuNo,
			String s_screateDate, String s_ecreateDate, String page,
			String rows, HttpSession httpSession) throws Exception {
		
		if (page == null) {
			page = "1";
			rows = "10";
		}

		PageBean pageBean = new PageBean(Integer.parseInt(page),
				Integer.parseInt(rows));
		ArrayList<ResourceBean> resourceBeans = resourceDao.resourceList(pageBean, name,
				userStuNo, s_screateDate, s_ecreateDate, httpSession);
		return resourceBeans;
	}
}
