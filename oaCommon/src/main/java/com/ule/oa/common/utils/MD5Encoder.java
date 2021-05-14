package com.ule.oa.common.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Encoder {
	 public static String md5Hex(String data) {
		 return DigestUtils.md5Hex(data);
	 }
	
	
	public static void main(String[] args) {
		System.out.println(MD5Encoder.md5Hex("ule456123"+ "{" + "ule.oa" + "}"));
	}
	
}
