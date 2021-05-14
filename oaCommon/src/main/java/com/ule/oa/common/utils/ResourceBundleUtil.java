package com.ule.oa.common.utils;

import java.util.ResourceBundle;

public class ResourceBundleUtil {
	
	public static String getValue(String propertiesName, String key) {
		ResourceBundle res = ResourceBundle.getBundle(propertiesName);
		String secretkey = res.getString(key); 
		return secretkey;
	}
	
}
