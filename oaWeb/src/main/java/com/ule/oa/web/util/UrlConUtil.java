package com.ule.oa.web.util;


import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.io.OutputStream;

import java.io.UnsupportedEncodingException;

import java.lang.reflect.Field;

import java.net.HttpURLConnection;

import java.net.MalformedURLException;

import java.net.URL;

import java.util.Enumeration;

 

import javax.net.ssl.HttpsURLConnection;

import javax.net.ssl.SSLContext;

import javax.net.ssl.SSLSocketFactory;

import javax.net.ssl.TrustManager;

import javax.servlet.http.HttpServletRequest;

 

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

 

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;




 

public class UrlConUtil {

	

	private static Logger logger = LoggerFactory.getLogger(UrlConUtil.class);

 

	/**

	 * 发送HTTPS请求

	 * @return JSONObject

	 */

	public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) throws Exception {

		JSONObject json = null;

		// 创建SSLContext对象，并使用我们指定的信任管理器初始化

		//TrustManager[] tm = { new MyX509TrustManager() };

		//SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");

		//sslContext.init(null, tm, new java.security.SecureRandom());

		// 从上述SSLContext对象中得到SSLSocketFactory对象

		//SSLSocketFactory ssf = sslContext.getSocketFactory();

 

		URL url = new URL(requestUrl);

		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

		//conn.setSSLSocketFactory(ssf);

 

		conn.setDoOutput(true);

		conn.setDoInput(true);

		conn.setUseCaches(false);

		// 设置请求方式（GET/POST）

		conn.setRequestMethod(requestMethod);

 

		// 当outputStr不为null时向输出流写数据

		if (null != outputStr) {

			OutputStream outputStream = conn.getOutputStream();

			// 注意编码格式

			outputStream.write(outputStr.getBytes("UTF-8"));

			outputStream.close();

		}

 

		// 从输入流读取返回内容

		InputStream inputStream = conn.getInputStream();

		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String str = null;

		StringBuffer buffer = new StringBuffer();

		while ((str = bufferedReader.readLine()) != null) {

			buffer.append(str);

		}

 

		// 释放资源

		bufferedReader.close();

		inputStreamReader.close();

		inputStream.close();

		inputStream = null;

		conn.disconnect();

		return JSON.parseObject(buffer.toString());

	}

 

	/**

	 * 发送HTTP请求

	 * @return JSONObject

	 */

	public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) throws Exception {

		JSONObject json = null;

 

		URL url = new URL(requestUrl);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setDoOutput(true);

		conn.setDoInput(true);

		conn.setUseCaches(false);

		// 设置请求方式（GET/POST）

		conn.setRequestMethod(requestMethod);

 

		// 当outputStr不为null时向输出流写数据

		if (null != outputStr) {

			OutputStream outputStream = conn.getOutputStream();

			// 注意编码格式

			outputStream.write(outputStr.getBytes("UTF-8"));

			outputStream.close();

		}

 

		// 从输入流读取返回内容

		InputStream inputStream = conn.getInputStream();

		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String str = null;

		StringBuffer buffer = new StringBuffer();

		while ((str = bufferedReader.readLine()) != null) {

			buffer.append(str);

		}

 

		// 释放资源

		bufferedReader.close();

		inputStreamReader.close();

		inputStream.close();

		inputStream = null;

		conn.disconnect();

		return JSON.parseObject(buffer.toString());

	}

	

	/**

	 * 将输入字符串进行u8编码

	 * @return String

	 */

	public static String urlEncodeUTF8(String source) {

		String result = source;

		try {

			result = java.net.URLEncoder.encode(source, "utf-8");

		} catch (UnsupportedEncodingException e) {

			logger.info("url编码异常");

		}

		return result;

	}

 

	/**

	 * 获取当前请求中所有参数，以?开头，后面以key=value形式，使用&符号拼接,如?param=1&param2=aaa

	 * @return String

	 */

	public static String getReqParams(HttpServletRequest req) {

		StringBuffer params = new StringBuffer();

		Enumeration enu = req.getParameterNames();

		if (enu.hasMoreElements()) {

			params.append("?");

		}

		String andUrlChar = "&";

		while (enu.hasMoreElements()) {

			String paraName = (String) enu.nextElement();

			params.append(paraName).append("=").append(req.getParameter(paraName)).append(andUrlChar);

		}

		if (params.toString().endsWith(andUrlChar)) {

			params.deleteCharAt(params.lastIndexOf(andUrlChar));

		}

 

		return params.toString().replaceAll(andUrlChar, "%26");

	}

 

	/**

	 * 将对象拼接成：A=4444&B=5C=1

	 * @return String

	 */

	public static String toUrlParams(Object o) throws IllegalArgumentException, IllegalAccessException {

		StringBuffer sb = new StringBuffer();

		Class clazz = o.getClass();

		for (Field f : clazz.getDeclaredFields()) {

			// 这里设置访问权限为true

			// field.setAccessible(true)的设置是很关键的，它把权限设置为true，从而让对象能够操作那个类的私有字段

			f.setAccessible(true);

			// 判断String字段是否为空，不为空进行解码

			if(f.get(o) == null){

				sb.append(f.getName()).append("=").append("").append("&");

			}else{

				sb.append(f.getName()).append("=").append(f.get(o)).append("&");

			}

		}

		sb.deleteCharAt(sb.lastIndexOf("&"));

		return sb.toString();

	}

}

