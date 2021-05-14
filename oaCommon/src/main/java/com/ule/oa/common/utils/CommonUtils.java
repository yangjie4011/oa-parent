package com.ule.oa.common.utils;

import com.ule.oa.common.cache.CacheHelper;
import com.ule.oa.common.cache.CacheKeyConstants;


/**
  * @ClassName: CommonUtils
  * @Description: 通用方法工具类
  * @author minsheng
  * @date 2017年9月18日 下午10:35:30
 */
public class CommonUtils {
	
	/**
	  * checkFileSuffix(check文件类型)
	  * @Title: checkFileSuffix
	  * @Description: TODO
	  * @param fileName	文件名称
	  * @param suffixList 文件上传类型,eg:jpg,png,bmp,jpeg
	  * @return    设定文件
	  * boolean    true:是需要上传的文件类型,不是需要上传的文件类型
	  * @throws
	 */
	public static boolean checkFileSuffix(String suffixList,String fileName){  
        boolean flag=false;  
        //获取文件后缀  
        String suffix=fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());  
          
        if(suffixList.contains(suffix.trim().toLowerCase())){  
            flag=true;  
        }
        
        return flag;  
    }
	
	public static String converToString(Object o){
		return null == o ? "" : o.toString();
	}
	
	public static Double converToDouble(Object o){
		if(null != o){
			return Double.parseDouble((String) o);
		}
		
		return null;
	}
	
	public static String getSysConfigKey(String code) {
		return CacheHelper.getKey(CacheKeyConstants.OA_SYS_CONFIG) + "_" + code;
	}
	
	public static String getSysCompanyConfigKey(String code) {
		return CacheHelper.getKey(CacheKeyConstants.OA_SYS_COMPANY_CONFIG) + "_" + code;
	}
}
