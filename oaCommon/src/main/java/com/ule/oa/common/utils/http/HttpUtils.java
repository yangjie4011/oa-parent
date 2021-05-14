package com.ule.oa.common.utils.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ule.oa.common.exception.OaException;

public class HttpUtils {
	
	private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
	
	public static String sendByGet(String url, Map<String, String> paramMap,boolean isReRquest) throws OaException {
		return sendByGet(url, paramMap, "UTF-8",isReRquest);
	}

	public static String sendByGet(String url, Map<String, String> paramMap, String charset,boolean isReRquest) throws OaException {
		
		HttpClient httpClient = new HttpClient();
		
		GetMethod method = new GetMethod(url);
		
		method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + charset);
		
		if (null != paramMap && !paramMap.isEmpty()) {
			Iterator<String> iterator = paramMap.keySet().iterator();
			// 设置参数信息
			StringBuffer queryString = new StringBuffer();
			while (iterator.hasNext()) {
				String key = iterator.next();
				queryString.append(key).append("=");
				try {
					queryString.append(URLEncoder.encode(paramMap.get(key), charset));
				} catch (UnsupportedEncodingException e) {
				}
				if (iterator.hasNext()) {
					queryString.append("&");
				}
			}
			method.setQueryString(queryString.toString());
		}
		
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			if(isReRquest){//重试
				// 执行get请求
				int count = 0;//最多重试5次
				while(count < 5){
					count++;
					logger.info("第" + count + "次发起请求!!!");
					
					try{
						httpClient.executeMethod(method);
						count = 5;//结束调用
					}catch(Exception e){
						logger.info("第" + count + "次请求失败!!!,失败原因={}",e);
						Thread.sleep(500);//休眠0.5秒
						if(count >= 5){
							throw e;
						}
					}
				}
			}else{
				httpClient.executeMethod(method);
			}
			
			// 获取响应的信息
			inputStream = method.getResponseBodyAsStream();

			// 输出响应的信息
			StringBuffer result = new StringBuffer();
			String readLine = null;
			reader = new BufferedReader(new InputStreamReader(inputStream, charset));
			while ((readLine = reader.readLine()) != null) {
				result.append(readLine);
			}
			return result.toString();
		} catch (Exception e) {
			logger.info("请求出错的url={}",url);
			logger.error("发送GET请求出错", e);
			throw new OaException("发送GET请求出错", e);
		} finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != reader) {
					reader.close();
				}
			} catch (IOException e) {
			}
			method.releaseConnection();
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
		}
	}

	public static String sendByPost(String url, Map<String, String> paramMap,boolean isReRquest) throws OaException {
		return sendByPost(url, paramMap, "UTF-8",isReRquest);
	}
	
	public static String sendByPost(String url, Map<String, String> paramMap, String charset,boolean isReRquest) throws OaException {
		
		HttpClient httpClient = new HttpClient();
		
		PostMethod method = new PostMethod(url);
		
		method.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=" + charset);
		
		if (null != paramMap && !paramMap.isEmpty()) {
			Iterator<String> iterator = paramMap.keySet().iterator();
			// 设置参数信息
			while (iterator.hasNext()) {
				String key = iterator.next();
				method.addParameter(new NameValuePair(key, paramMap.get(key)));
			}
		}
		
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			if(isReRquest){//重试
				// 执行get请求
				int count = 0;//最多重试5次
				while(count < 5){
					count++;
					logger.info("第" + count + "次发起请求!!!");
					
					try{
						httpClient.executeMethod(method);
						count = 5;//结束调用
					}catch(Exception e){
						logger.info("第" + count + "次请求失败!!!,失败原因={}",e);
						Thread.sleep(500);//休眠0.5秒
						if(count >= 5){
							throw e;
						}
					}
				}
			}else{
				httpClient.executeMethod(method);
			}

			// 获取响应的信息
			inputStream = method.getResponseBodyAsStream();

			// 输出响应的信息
			StringBuffer result = new StringBuffer();
			String readLine = null;
			reader = new BufferedReader(new InputStreamReader(inputStream, charset));
			while ((readLine = reader.readLine()) != null) {
				result.append(readLine);
			}
			return result.toString();
		} catch (Exception e) {
			logger.info("请求出错的url={}",url);
			logger.error("发送POST请求出错", e);
			throw new OaException("发送POST请求出错", e);
		} finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != reader) {
					reader.close();
				}
			} catch (IOException e) {
			}
			method.releaseConnection();
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
		}
	}
	
	
	public static String sendMailByPost(String url, Map<String, Object> paramMap,boolean isReRquest) throws OaException {
		return sendMailByPost(url, paramMap, "UTF-8",isReRquest);
	}
	
	public static String sendMailByPost(String url, Map<String, Object> paramMap, String charset,boolean isReRquest) throws OaException {
		
		HttpClient httpClient = new HttpClient();
		
		PostMethod method = new PostMethod(url);
		
		method.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=" + charset);
		
		if (null != paramMap && !paramMap.isEmpty()) {
			Iterator<String> iterator = paramMap.keySet().iterator();
			// 设置参数信息
			while (iterator.hasNext()) {
				String key = iterator.next();
				method.addParameter(new NameValuePair(key, paramMap.get(key) == null ? "" : paramMap.get(key).toString()));
			}
		}
		
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			if(isReRquest){//重试
				// 执行get请求
				int count = 0;//最多重试5次
				while(count < 5){
					count++;
					logger.info("第" + count + "次发起请求!!!");
					
					try{
						httpClient.executeMethod(method);
						count = 5;//结束调用
					}catch(Exception e){
						logger.info("第" + count + "次请求失败!!!,失败原因={}",e);
						Thread.sleep(500);//休眠0.5秒
						if(count >= 5){
							throw e;
						}
					}
				}
			}else{
				httpClient.executeMethod(method);
			}
			
			// 获取响应的信息
			inputStream = method.getResponseBodyAsStream();

			// 输出响应的信息
			StringBuffer result = new StringBuffer();
			String readLine = null;
			reader = new BufferedReader(new InputStreamReader(inputStream, charset));
			while ((readLine = reader.readLine()) != null) {
				result.append(readLine);
			}
			return result.toString();
		} catch (Exception e) {
			logger.info("请求出错的url={}",url);
			logger.error("发送POST请求出错", e);
			throw new OaException("发送POST请求出错", e);
		} finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
				if (null != reader) {
					reader.close();
				}
			} catch (IOException e) {
			}
			method.releaseConnection();
			httpClient.getHttpConnectionManager().closeIdleConnections(0);
		}
	}

}
