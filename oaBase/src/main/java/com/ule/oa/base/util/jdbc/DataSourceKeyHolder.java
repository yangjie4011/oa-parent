package com.ule.oa.base.util.jdbc;


public class DataSourceKeyHolder {

	private static ThreadLocal<String> holder = new ThreadLocal<String>();
	
	public static void set(String dataSource) {
		holder.set(dataSource);
	}
	
	public static String get() {
		return holder.get();
	} 
    
    public static void remove(){  
	  holder.remove();  
    }  

	
}
