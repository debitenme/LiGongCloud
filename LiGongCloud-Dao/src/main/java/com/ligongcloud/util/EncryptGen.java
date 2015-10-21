package com.ligongcloud.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 
 * @author HeJiaWang
 * @version 1.0
 */
public class EncryptGen {

	// static boolean debug =false ;

	// 加密KEY不能随便改动
	static final byte[] KEYVALUE = "6^)(9-p35@%3#4S!4S0)$Y%%^&5(j.&^&o(*0)$Y%!#O@*GpG@=+@j.&6^)(0-=+"
			.getBytes();

	static final int BUFFERLEN = 512;

	public EncryptGen() {
	}

	/**
	 * 对文件进行加密
	 *
	 * @param String
	 *            oldFile 原始要加密的文件
	 * @param String
	 *            newFile 加密后的文件
	 * @return
	 */
	public static void encryptFile(String oldFile, String newFile)
			throws Exception {
		FileInputStream in = new FileInputStream(oldFile);
		File file = new File(newFile);
		if (!file.exists())
			file.createNewFile();
		FileOutputStream out = new FileOutputStream(file);
		int c, pos, keylen;
		pos = 0;
		keylen = KEYVALUE.length;
		byte buffer[] = new byte[BUFFERLEN];
		while ((c = in.read(buffer)) != -1) {
			for (int i = 0; i < c; i++) {
				buffer[i] ^= KEYVALUE[pos];
				out.write(buffer[i]);
				pos++;
				if (pos == keylen)
					pos = 0;
			}
		}
		in.close();
		out.close();
	}

	/**
	 * 对文件进行解密
	 *
	 * @param String
	 *            oldFile 原始要解密的文件
	 * @param String
	 *            newFile 解密后的文件
	 * @return
	 */
	public static void decryptFile(String oldFile, String newFile)
			throws Exception {
		FileInputStream in = new FileInputStream(oldFile);
		File file = new File(newFile);
		if (!file.exists())
			file.createNewFile();
		FileOutputStream out = new FileOutputStream(file);
		int c, pos, keylen;
		pos = 0;
		keylen = KEYVALUE.length;
		byte buffer[] = new byte[BUFFERLEN];
		while ((c = in.read(buffer)) != -1) {
			for (int i = 0; i < c; i++) {
				buffer[i] ^= KEYVALUE[pos];
				out.write(buffer[i]);
				pos++;
				if (pos == keylen)
					pos = 0;
			}
		}
		in.close();
		out.close();
	}
}