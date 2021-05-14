package com.ule.oa.admin.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//判断是否是操作（标记需要操作权限的接口）
@Target(ElementType.METHOD)  
@Retention(RetentionPolicy.RUNTIME) 
public @interface IsOperation {
	
	boolean returnType() default false;//默认false，返回403 code,true的时候返回403无操作权限页面

}
