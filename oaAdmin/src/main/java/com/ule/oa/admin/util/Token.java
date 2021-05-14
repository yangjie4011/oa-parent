package com.ule.oa.admin.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
  * @ClassName: Token
  * @Description: 生成token或者移除token
  * @author minsheng
  * @date 2018年1月5日 上午11:26:24
 */
@Target(ElementType.METHOD)  
@Retention(RetentionPolicy.RUNTIME) 
public @interface Token {
	/**
	  * generate(方法前加上generate=true，表示需要生成token)
	  * @Title: generate
	  * @Description: 方法前加上generate=true，表示需要生成token
	  * @return    设定文件
	  * boolean    返回类型
	  * @throws
	 */
	boolean generate() default false;
	
	/**
	  * remove(方法前加上remove=true，标识需要移除token)
	  * @Title: remove
	  * @Description: 方法前加上remove=true，标识需要移除token
	  * @return    设定文件
	  * boolean    返回类型
	  * @throws
	 */
    boolean remove() default false;  
}
