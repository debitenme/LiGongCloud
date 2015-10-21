package com.ligongcloud.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
@Entity
public class User {
	private int 		id;
	private String      stuNo;
	private String 		username;
	private String 		email;
	private String 		password;
	private String 		joindate;
	private int 		gender;	//性别 0--女   1--男
	private Disk        disk;
	private Set<Message> message;
	private Set<File> file;
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setJoindate(String joindate) {
		this.joindate = joindate;
	}
	public String getJoindate() {
		return joindate;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public int getGender() {
		return gender;
	}
	public String getStuNo() {
		return stuNo;
	}
	public void setStuNo(String stuNo) {
		this.stuNo = stuNo;
	}
	@OneToOne(mappedBy="user")
	public Disk getDisk() {
		return disk;
	}
	public void setDisk(Disk disk) {
		this.disk = disk;
	}
	@OneToMany(mappedBy="user")
	public Set<Message> getMessage() {
		return message;
	}
	public void setMessage(Set<Message> message) {
		this.message = message;
	}
	@OneToMany(mappedBy="user")
	public Set<File> getFile() {
		return file;
	}
	public void setFile(Set<File> file) {
		this.file = file;
	}
	
	
}
