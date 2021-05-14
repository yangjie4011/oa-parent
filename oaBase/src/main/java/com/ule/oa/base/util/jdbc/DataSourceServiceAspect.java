package com.ule.oa.base.util.jdbc;


import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
  * @ClassName: DataSourceServiceAspect
  * @Description: 根据Mapper接口的父接口设置数据源,父类如果是继承OaSqlMapper,切换到oa系统数据源，继承attence切换到考勤系统数据源
  * @author minsheng
  * @date 2017年6月7日 上午10:54:45
 */
@Component
@Aspect
@Order(1)
public class DataSourceServiceAspect {
	
	private Logger logger = LoggerFactory.getLogger(DataSourceServiceAspect.class);

	@Before("execution(* com.ule.oa.base.service.*.*(..))")
	public void before(JoinPoint joinPoint) {
		
		Class<?> clazz = joinPoint.getTarget().getClass();
		String method = joinPoint.getSignature().getName();
		Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
		try {
			Method m = clazz.getMethod(method, parameterTypes);
			if (null != m.getAnnotation(Transactional.class) && null != m.getAnnotation(DataSource.class)) {
				String dataSourceKey = m.getAnnotation(DataSource.class).value();
				if (null != dataSourceKey) {
					DataSourceKeyHolder.set(dataSourceKey);
				}
			}
		} catch (SecurityException e) {
			logger.error("error on set current dataSource", e);
		} catch (NoSuchMethodException e) {
			logger.error("error on set current dataSource", e);
		}
		
	}
	
}
