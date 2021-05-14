package com.ule.oa.base.util.jdbc;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.ule.oa.common.persistence.SqlMapper;

/**
  * @ClassName: DataSourceMapperAspect
  * @Description: 切库策略
  * @author minsheng
  * @date 2017年6月7日 上午10:54:02
 */
@Component
@Aspect
public class DataSourceMapperAspect {
	@Before("execution(* com.ule.oa.base.mapper.*.*(..))")
	public void before(JoinPoint joinPoint) {
		
		if (joinPoint.getTarget() instanceof OaSqlMapper) {
			DataSourceKeyHolder.set("dataSourceOA");
		}
		else if (joinPoint.getTarget() instanceof AttenceSqlMapper) {
			DataSourceKeyHolder.set("dataSourceAttence");
		}
		
	}
	
}
