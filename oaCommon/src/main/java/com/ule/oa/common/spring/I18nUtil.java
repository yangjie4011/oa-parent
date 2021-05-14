package com.ule.oa.common.spring;

import java.util.Locale;

public class I18nUtil {

	public static String getMessage(String code, Object[] args, Locale locale) {
		return SpringContextUtils.getContext().getMessage(code, args, locale);
	}
	
	public static String getMessage(String code, Object[] args) {
		return getMessage(code, args, Locale.SIMPLIFIED_CHINESE);
	}
	
	public static String getMessage(String code) {
		return getMessage(code, null);
	}
	
}
