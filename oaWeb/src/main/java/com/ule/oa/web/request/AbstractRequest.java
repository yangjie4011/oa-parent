package com.ule.oa.web.request;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import com.ule.oa.web.response.AbstractResponse;

public abstract class AbstractRequest<T extends AbstractResponse> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4914880913456221671L;
	
	/**
	 * 获取API方法名
	 * @author zhangwei@tomstaff.com
	 * @return
	 */
	public abstract String getApiMethod();
	
	/**
	 * 获取HTTP方法名
	 * @author zhangwei@tomstaff.com
	 * @return
	 */
	public abstract String getHttpMethod();
	
	/**
	 * 获取应用级参数
	 * @author zhangwei@tomstaff.com
	 * @return
	 */
	public abstract Map<String, String> getParams();
	
	/**
	 * response类
	 * @author zhangwei@tomstaff.com
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Class<T> getResponseClass() {
		Type genType = getClass().getGenericSuperclass();  
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();  
		return (Class<T>) params[0];
	}


	public boolean isLogEnabled() {
		return true;
	}
	
}
