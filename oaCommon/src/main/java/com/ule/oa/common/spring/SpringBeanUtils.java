package com.ule.oa.common.spring;

import java.util.Map;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringBeanUtils {
	
	/**
	 * @Description: 注册bean
	 * @author zhangwei@tomstaff.com
	 * @date 2015年6月12日上午11:10:22
	 * @version 2.1.0
	 */
	public static Object registerBean(String className, String beanName,Map<String, Object> properties) {
		
		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) SpringContextUtils.getContext();
		
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(className);
		beanDefinitionBuilder.setLazyInit(false);
		MutablePropertyValues propertyValues = new MutablePropertyValues();
		if (properties != null) {
			propertyValues.addPropertyValues(properties);
		}
		beanDefinitionBuilder.getBeanDefinition().setPropertyValues(propertyValues);
		defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
		return defaultListableBeanFactory.getBean(beanName);
	}
}
