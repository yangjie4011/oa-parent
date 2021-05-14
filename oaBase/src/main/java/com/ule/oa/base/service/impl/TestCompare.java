package com.ule.oa.base.service.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.tbl.UserTbl;

public class TestCompare<T> {
	


    public static  List<Map> contrastObj(Object oldBean, Object newBean) {
        List<Map> list= new ArrayList<Map>();
        //if (oldBean instanceof SysConfServer && newBean instanceof SysConfServer) {
            /*T pojo1 = (T) oldBean;
            T pojo2 = (T) newBean;*/
        try {
            Class clazz = oldBean.getClass();
            Field[] fields = oldBean.getClass().getDeclaredFields();
            int i=1;
            for (Field field : fields) {
            	Integer type = 2;
            	
                if("serialVersionUID".equals(field.getName())){
                    continue;
                }
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = pd.getReadMethod();
                Object o1 = getMethod.invoke(oldBean);
                Object o2 = getMethod.invoke(newBean);
                
                if(o1==null){
                    o1 = "";//老数据没有，新增，操作类型新增
                    type = 1;
                }
                if(o2 == null){
                    o2 = "";//新数据没有，新增，操作类型删除
                    type = 3;
                }
                
                if (!o1.toString().equals(o2.toString())) {
                    Map map = new HashMap<String, String>();
                	map.put("field", field.getName());
                	map.put("oldValue", o1);
                	map.put("newValue", o2);
                	map.put("type", type);
                    list.add(map);
                } 
            }
            System.out.println(i);
        } catch (Exception e) {
        }
     // }
        System.out.println(list);
        return list;
    }
	  
	public static void main(String[] args) throws Exception {  
	    UserTbl user1 = new UserTbl();  
	    user1.setUserName("zhangsan");
	    user1.setCompanyId(121L);
	    user1.setId(0L);
	    
	    UserTbl user2 = new UserTbl();  
	    user2.setUserName("lisi");
	    user2.setCompanyId(11L);
	    user2.setRemark("添加的");
	    
	    contrastObj(user1,user2);
	}  

}
