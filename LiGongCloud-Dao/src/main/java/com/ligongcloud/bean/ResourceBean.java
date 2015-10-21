package com.ligongcloud.bean;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
public class ResourceBean {
	private int 		id;	//ID
	private String      userStuNo;
	private int		    size;	//文件大小
	private String  	path;
	private String 		name;	//文件名
	private String 		type;	//类型
	private String 		password;	//密码
	private String 		isLock;			//是否加密文件    1-加密     0-未加密
	private String 		isShare;		//是否分享文件    1-加密     0-未加密
	private int 		shareDownload;  //分享下载次数
	private String 		description;	//描述
	private String 		location;	//地址
	private String 		shareUrl;	//分享地址
	private String 		createDate;	//创建日期
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIsLock() {
		return isLock;
	}
	public void setIsLock(String isLock) {
		this.isLock = isLock;
	}
	public String getIsShare() {
		return isShare;
	}
	public void setIsShare(String isShare) {
		this.isShare = isShare;
	}
	public int getShareDownload() {
		return shareDownload;
	}
	public void setShareDownload(int shareDownload) {
		this.shareDownload = shareDownload;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getUserStuNo() {
		return userStuNo;
	}
	public void setUserStuNo(String userStuNo) {
		this.userStuNo = userStuNo;
	}
}
