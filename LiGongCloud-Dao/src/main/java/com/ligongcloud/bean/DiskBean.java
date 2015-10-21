package com.ligongcloud.bean;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
public class DiskBean {
	
	private int     id;
	private String  user;
	private int     totalSize;
	private int     usedSize;
	private String  diskLocation;
	private int	    fileNumber;
	private int     shareNumber;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public int getUsedSize() {
		return usedSize;
	}
	public void setUsedSize(int usedSize) {
		this.usedSize = usedSize;
	}
	public String getDiskLocation() {
		return diskLocation;
	}
	public void setDiskLocation(String diskLocation) {
		this.diskLocation = diskLocation;
	}
	public int getFileNumber() {
		return fileNumber;
	}
	public void setFileNumber(int fileNumber) {
		this.fileNumber = fileNumber;
	}
	public int getShareNumber() {
		return shareNumber;
	}
	public void setShareNumber(int shareNumber) {
		this.shareNumber = shareNumber;
	}
}
