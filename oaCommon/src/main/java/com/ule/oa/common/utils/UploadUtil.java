package com.ule.oa.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


public class UploadUtil {
	
	public static String uploadToQiNiu(File file, String fullName, String url) throws ClientProtocolException, IOException{
		return null;
	}
	
	public static byte[] fileToByteArray(CommonsMultipartFile file) {
	    DataInputStream dataStream = null;
	    ByteArrayOutputStream outByteStream = null;
		try {
			dataStream = new DataInputStream(file.getInputStream());
			byte datas[] = new byte[1024];	        
			int num = -1;	        
			outByteStream = new ByteArrayOutputStream();
			while ((num=dataStream.read(datas, 0, datas.length))!=-1) {	        	
				outByteStream.write(datas, 0, num);
			}
		} catch (IOException e) {
		
		}finally{
			try {
				if(outByteStream!=null) {
					outByteStream.flush();
					outByteStream.close();
				}
			} catch (IOException e) {}
			try {
				if(dataStream!=null) {
					dataStream.close();
				}
			} catch (IOException e) {}
		}
		return outByteStream!=null?outByteStream.toByteArray():null;
	}
}
