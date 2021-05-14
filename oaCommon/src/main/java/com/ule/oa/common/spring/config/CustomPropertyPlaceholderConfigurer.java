package com.ule.oa.common.spring.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * 解析properties
 * 
 * @author zhangwei@tomstaff.com
 */
public class CustomPropertyPlaceholderConfigurer extends
		PropertyPlaceholderConfigurer {
	
	private static Map<String, Object> propertiesMap = new HashMap<String, Object>();

	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			Properties props) throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		
		for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            propertiesMap.put(keyStr, value);
        }
		
	}
	
	public static Object getProperty(String name) {
		return propertiesMap.get(name);
	}

}
