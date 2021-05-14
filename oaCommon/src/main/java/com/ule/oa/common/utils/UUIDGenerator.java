package com.ule.oa.common.utils;

public class UUIDGenerator {
	
	public static String generate() {
		return java.util.UUID.randomUUID().toString().replace("-", "");
	}

}
