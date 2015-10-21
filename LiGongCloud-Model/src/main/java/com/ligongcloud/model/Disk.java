package com.ligongcloud.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
@Entity
public class Disk {
	private int     id;
	private User    user;
	private int     totalSize;
	private int     usedSize;
	private String  diskLocation;
	private int	    fileNumber;
	private int     shareNumber;
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@OneToOne					
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
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
	public void setFileNumber(int fileNumber) {
		this.fileNumber = fileNumber;
	}
	public int getFileNumber() {
		return fileNumber;
	}
	public void setShareNumber(int shareNumber) {
		this.shareNumber = shareNumber;
	}
	public int getShareNumber() {
		return shareNumber;
	}
	public String getDiskLocation() {
		return diskLocation;
	}
	public void setDiskLocation(String diskLocation) {
		this.diskLocation = diskLocation;
	}
}
