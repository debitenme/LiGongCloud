package com.ligongcloud.util;

import java.util.ArrayList;

import com.ligongcloud.bean.DiskBean;
import com.ligongcloud.model.Disk;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
public class DiskUtil {
	
	/**
	 * 将Disk转换为DiskBean，以用来在页面中展示
	 * @param disks 要进行被转化的Disk集合
	 */
	public static ArrayList<DiskBean> formatDisk( ArrayList<Disk> disks) {
		ArrayList<DiskBean> diskBeans = new ArrayList<DiskBean>();

		for (Disk disk : disks) {

			DiskBean d = new DiskBean();
			
			d.setDiskLocation(disk.getDiskLocation());
			d.setFileNumber(disk.getFileNumber());
			d.setId(disk.getId());
			d.setShareNumber(disk.getShareNumber());
			d.setTotalSize(disk.getTotalSize());
			d.setUsedSize(disk.getUsedSize());
			d.setUser(disk.getUser().getStuNo());

			diskBeans.add(d);
		}

		return diskBeans;
	}
}
