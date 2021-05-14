package com.ule.oa.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ChangeProFile {

	public static void main(String[] args) {
		
		final String env = "prd";//local,beta,prd
		final String app = "uleOa";//uleOa,tomOa
		readyFilePath(env,app);
	}
	
	private static void readyFilePath(String env,String app){
		
		System.out.println(System.getProperty("user.dir"));
		final String basePath = System.getProperty("user.dir");
		String sourcePath = "\\src\\main\\resources\\proFile\\";
		final String resoucesPath = basePath+"\\src\\main\\resources\\";//放spring-dataSource目录，不可改变
		String targetPath = "\\src\\main\\resources\\";
		
		if(env.equals("local")){
			sourcePath = sourcePath + "local\\";
		}else if(env.equals("beta")){
			sourcePath = sourcePath + "beta\\";
			targetPath = "\\src\\main\\conf\\beta\\";
		}else if(env.equals("prd")){
			sourcePath = sourcePath + "prd\\";
			targetPath = "\\src\\main\\conf\\prd\\";
		}
		
		sourcePath = basePath + sourcePath;
		targetPath = basePath + targetPath;
		final String sourceFilePath = sourcePath;
		final String targetFilePath = targetPath;
        Map<String,String> fileNameMap = new HashMap<>();//定义要复制的文件列表
		if(app.equals("uleOa")){
			if(env.equals("local")){
				fileNameMap = new HashMap<String, String>(){
					private static final long serialVersionUID = 1L;

					{
						put(sourceFilePath+"jdbc2-ule.properties",targetFilePath+"jdbc2.properties");
				        put(sourceFilePath+"jedisclient-ule.properties",targetFilePath+"jedisclient.properties");
				        put(sourceFilePath+"kafka-clients-cfg-ule.xml",targetFilePath+"kafka-clients-cfg.xml");
				        put(sourceFilePath+"spring-dataSource-ule.xml",resoucesPath+"spring-dataSource.xml");
					}
				};
			}else{
				fileNameMap = new HashMap<String, String>(){
					private static final long serialVersionUID = 1L;

					{
				        put(sourceFilePath+"jedisclient-ule.properties",targetFilePath+"jedisclient.properties");
				        put(sourceFilePath+"kafka-clients-cfg-ule.xml",targetFilePath+"kafka-clients-cfg.xml");
				        put(sourceFilePath+"spring-dataSource-ule.xml",resoucesPath+"spring-dataSource.xml");
					}
				};
			}
		}else if(app.equals("tomOa")){
			if(env.equals("local")){
				fileNameMap = new HashMap<String, String>(){
					private static final long serialVersionUID = 1L;

					{
						put(sourceFilePath+"jdbc2-tom.properties",targetFilePath+"jdbc2.properties");
				        put(sourceFilePath+"jedisclient-tom.properties",targetFilePath+"jedisclient.properties");
				        put(sourceFilePath+"kafka-clients-cfg-tom.xml",targetFilePath+"kafka-clients-cfg.xml");
				        put(sourceFilePath+"spring-dataSource-tom.xml",resoucesPath+"spring-dataSource.xml");
					}
				};
			}else{
				fileNameMap = new HashMap<String, String>(){
					private static final long serialVersionUID = 1L;

					{
				        put(sourceFilePath+"jedisclient-tom.properties",targetFilePath+"jedisclient.properties");
				        put(sourceFilePath+"kafka-clients-cfg-tom.xml",targetFilePath+"kafka-clients-cfg.xml");
				        put(sourceFilePath+"spring-dataSource-tom.xml",resoucesPath+"spring-dataSource.xml");
					}
				};
			}
		}
		fileNameMap.forEach((k, v) -> copyFile(k,v));
		
	}
	
	

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		
		InputStream inStream = null;
		FileOutputStream fs = null;
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				inStream = new FileInputStream(oldPath); // 读入原文件
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
		}finally {
			if(inStream!=null) {
				try {
					inStream.close();
				} catch (IOException e) {
				}
			}
			if(fs!=null) {
				try {
					fs.close();
				} catch (IOException e) {
				}
			}
			
		}

	}
}
