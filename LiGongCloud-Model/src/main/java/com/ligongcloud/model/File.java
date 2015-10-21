package com.ligongcloud.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
@Entity
public class File {
	
	//path+name=location 是这样的关系
	
	private int 		id;	//ID
	private User        user;
//	private Set<User> Users = new HashSet<User>();	//文件所有人
//	private File parent;	//
//	private Set<File> myFiles = new HashSet<File>();	//
	private int		    size;	//文件大小
	private String  	path;	//路径(不包括文件名)	
	private String 		name;	//文件名
	private String 		type;	//类型
	private String 		password;	//密码
	private int 		isLock;			//是否加密文件    1-加密     0-未加密
	private int 		isShare;		//是否分享文件    1-加密     0-未加密
	private int 		shareDownload;  //分享下载次数
	private String 		description;	//描述
	private String 		location;	//地址(包括文件名)
	private String 		shareUrl;	//分享地址
	private String 		createDate;	//创建日期
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

//	@ManyToMany
//	@JoinTable(
//			name="f_u",
//			joinColumns={@JoinColumn(name="myfile_id")},
//			inverseJoinColumns={@JoinColumn(name="user_id")}
//	)
//	public Set<User> getUsers() {
//		return Users;
//	}
//	public void setUsers(Set<User> users) {
//		this.Users = users;
//	}
	@ManyToOne
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
//	@ManyToOne
//	public File getParent() {
//		return parent;
//	}
//	public void setParent(File parent) {
//		this.parent = parent;
//	}
//	@OneToMany(mappedBy="parent",cascade = CascadeType.ALL)
//	public Set<File> getMyFiles() {
//		return myFiles;
//	}
//	public void setMyFiles(Set<File> myFiles) {
//		this.myFiles = myFiles;
//	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getPath() {
		return path;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocation() {
		return location;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
	public void setIsLock(int isLock) {
		this.isLock = isLock;
	}
	public int getIsLock() {
		return isLock;
	}
	public void setIsShare(int isShare) {
		this.isShare = isShare;
	}
	public int getIsShare() {
		return isShare;
	}
	public void setShareDownload(int shareDownload) {
		this.shareDownload = shareDownload;
	}
	public int getShareDownload() {
		return shareDownload;
	}
	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}
	public String getShareUrl() {
		return shareUrl;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateDate() {
		return createDate;
	}
}
