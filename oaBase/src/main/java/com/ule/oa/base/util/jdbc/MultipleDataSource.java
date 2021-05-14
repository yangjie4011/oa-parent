package com.ule.oa.base.util.jdbc;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 获取当前线程的数据源
 *
 */
public class MultipleDataSource extends AbstractRoutingDataSource {

	/**
	 * 此方法在开启事务时被调用
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceKeyHolder.get();
	}

}
