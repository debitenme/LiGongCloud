package com.ligongcloud.bean;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
public class PageBean {

	private int page;
	private int rows;
	@SuppressWarnings("unused")
	private int start;
	
	
	public PageBean() {
		super();
	}
	public PageBean(int page, int rows) {
		super();
		this.page = page;
		this.rows = rows;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getStart() {
		return (page-1)*rows;
	}
}
