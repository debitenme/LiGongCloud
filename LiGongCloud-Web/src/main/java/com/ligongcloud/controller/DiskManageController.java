package com.ligongcloud.controller;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.fop.svg.PDFTranscoder;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ligongcloud.bean.DiskBean;
import com.ligongcloud.bean.PageBean;
import com.ligongcloud.dao.DiskDao;
import com.ligongcloud.dao.UserDao;
import com.ligongcloud.model.User;
import com.ligongcloud.util.DateUtil;
import com.ligongcloud.util.ResponseUtil;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
@Controller
@RequestMapping("/diskManage")
public class DiskManageController {
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private DiskDao diskDao;
	
	@Autowired
	private UserDao userDao;
	
	@RequestMapping(value = "/loadDisk")
	public String loadDisk(String s_user, String s_susedSize,
			String s_eusedSize, String page, String rows,
			HttpServletResponse response, HttpSession httpSession) throws Exception {
		
		JSONObject result = new JSONObject();
		JsonConfig jsonConfig = new JsonConfig();

		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		JSONArray jsonArray = JSONArray.fromObject( search(s_user, s_susedSize, s_eusedSize, page, rows, httpSession), jsonConfig);
		
		Long total = diskDao.diskCount(s_user, s_susedSize, s_eusedSize, httpSession);

		result.put("rows", jsonArray);
		result.put("total", total);
		ResponseUtil.write(response, result);
		return null;
	}

	private ArrayList<DiskBean> search(String s_user, String s_susedSize,
			String s_eusedSize, String page, String rows,
			HttpSession httpSession) {
		
		if (page == null) {
			page = "1";
			rows = "10";
		} 
		
		PageBean pageBean = new PageBean(Integer.parseInt(page), Integer.parseInt(rows));
		
		ArrayList<DiskBean> diskBeans = diskDao.diskList(pageBean,
				s_user, s_susedSize, s_eusedSize,
				httpSession);
		
		return diskBeans;
	}
	
	/**
	 * 修改网盘容量
	 * @param u_user 对该用户的网盘容量进行修改
	 * @param u_totalSize 修改后的网盘容量
	 * @return
	 */
	@RequestMapping(value = "/updateDiskSize")
	public void updateDiskSize(String u_user, int u_totalSize){
		User u = userDao.loadUserByStuNo(u_user);
		diskDao.updateDiskSize(u.getId(), u_totalSize);
	}
	
	/**
	 * 查看分析图之后的返回按钮处理
	 * @return
	 */
	@RequestMapping(value="/roleBack")
	public String roleBack(){
		return "adminDiskManage";
	}
	
	/**
	 * 用户网盘容量使用情况柱状图分析
	 * @return
	 */
	@RequestMapping(value = "/analyseDiskSize")
	public String analyseDiskSize(HttpSession httpSession){
		List<Integer> nums = diskDao.analyseDiskSizeNum();
		httpSession.setAttribute("diskNum", nums);
		return "analysis/analyseDiskSize";
	}
	
	/**
	 * 用户网盘文件存储情况柱状图分析
	 * @return
	 */
	@RequestMapping(value = "/analyseFileSave")
	public String analyseFileSave(HttpSession httpSession){
		List<Integer> nums = diskDao.analyseFileSave();
		httpSession.setAttribute("fileNum", nums);
		return "analysis/analyseFileNum";
	}
	
	/**
	 * 用户网盘文件分享情况柱状图分析
	 * @return
	 */
	@RequestMapping(value = "/analyseFileShare")
	public String analyseFileShare(HttpSession httpSession){
		List<Integer> nums = diskDao.analyseFileShare();
		httpSession.setAttribute("shareNum", nums);
		return "analysis/analyseShareNum";
	}
	
	/**
	 * highchars导出
	 */
	@RequestMapping(value = "/exportHighCharts")
	public void exportHighCharts(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {

		String time = "_" + DateUtil.formatDate(new Date(), "yyyy-MM-dd");//yyyymmddhhmmss2Format(new Date());
		request.setCharacterEncoding("UTF-8");
		
		String type = request.getParameter("type");
		String svg = request.getParameter("svg");
		String title = request.getParameter("titlename");
		
		ServletOutputStream out = response.getOutputStream();
		if (null != type && null != svg) {
			svg = svg.replaceAll(":rect", "rect");
			String ext = "";
			Transcoder t = null;
			if ("image/png".equals(type)) {
				ext = "png";
				t = new PNGTranscoder();
			} else if ("image/jpeg".equals(type)) {
				ext = "jpg";
				t = new JPEGTranscoder();
			} else if ("application/pdf".equals(type)) {
				ext = "pdf";
				t = new PDFTranscoder();
			} else if ("image/svg+xml".equals(type)) {
				ext = "svg";
			}
			String filename = new String(title.getBytes("GBK"), "iso-8859-1");
			response.addHeader("Content-Disposition", "attachment; filename=" + filename + time + "." + ext);
			response.addHeader("Content-Type", type);
			if (null != t) {
				TranscoderInput input = new TranscoderInput(new StringReader(svg));
				TranscoderOutput output = new TranscoderOutput(out);
				t.transcode(input, output);

			} else if ("svg".equals(ext)) {
				svg = svg.replace("http://www.w3.org/2000/svg", "http://www.w3.org/TR/SVG11/");
				out.print(svg);
			} else {
				out.print("Invalid type: " + type);
			}
		} else {
			response.addHeader("Content-Type", "text/html");
		}
		out.flush();
		out.close();
	}

}
