package com.ule.oa.common.utils.http;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class HttpWriter {
	public static void writerJson(HttpServletResponse response,String str){
		writerText(response,"UTF-8","application/json; charset=utf-8",str);
	}
	public static void writerJson(HttpServletResponse response,String characterEncoding,String str){
		writerText(response,characterEncoding,"application/json",str);
	}
	public static void writerXml(HttpServletResponse response,String str){
		writerText(response,"UTF-8","text/xml",str);
	}
	public static void writerXml(HttpServletResponse response,String characterEncoding,String str){
		writerText(response,characterEncoding,"text/xml",str);
	}
	public static void writerText(HttpServletResponse response,String str){
		writerText(response,"UTF-8","text/plain",str);
	}
	public static void writerText(HttpServletResponse response,String characterEncoding,String str){
		writerText(response,characterEncoding,"text/plain",str);
	}
	public static void writerText(HttpServletResponse response,String characterEncoding,String contentType,String str){
		PrintWriter writer=null;
		try{
			response.setCharacterEncoding(characterEncoding);
			response.setContentType(contentType);
			writer=response.getWriter();
			writer.print(str);
			writer.flush();
		}catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
		}finally{
			if(writer != null){
				writer.close();
			}
		}
	}
}
