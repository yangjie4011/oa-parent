package com.ule.oa.common.listener;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

/**
  * @ClassName: EmployeeLogEvent
  * @Description: 员工日志事件
  * @author minsheng
  * @date 2018年1月8日 上午11:13:43
 */
public class LogEvent extends ApplicationEvent{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Object oldBean;
	private Object newBean;
	
	/**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	private static final long serialVersionUID = -758292456393463347L;
	
	public LogEvent(Object oldSource,Object newSource) {
		super(oldSource);
		this.oldBean = oldSource;
		this.newBean = newSource;
	}
	
	public void writeLog() throws Exception{
		compareObj(oldBean, newBean);
		
		if(null == newBean){
			logger.info("执行保存方法");
		}else{
			logger.info("执行更新方法");
		}
	}
	
	/**
	  * compareObj(比较两个对象（type-1:新增，2：修改，3：删除）)
	  * @Title: compareObj
	  * @Description: 比较两个对象（type-1:新增，2：修改，3：删除）
	  * @param oldBean
	  * @param newBean
	  * @return    设定文件
	  * List<Map>    返回类型
	  * @throws
	 */
	public List<Map<String,Object>> compareObj(Object oldBean, Object newBean) {
		List<Map<String,Object>> list= new ArrayList<Map<String,Object>>();
        try {
            Class<? extends Object> clazz = oldBean.getClass();
            Field[] fields = oldBean.getClass().getDeclaredFields();
            Integer type = 2;//修改
            Map<String,Object> map = null;
            
            for (Field field : fields) {
            	if("serialVersionUID".equals(field.getName())){
                    continue;
                }
            	
            	map = new HashMap<String, Object>();
            	PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = pd.getReadMethod();
                Object o1 = getMethod.invoke(oldBean);
                o1 = (null == o1 || "".equals(o1.toString())) ? "" : o1;
                
	            if(null == newBean){//新增
	            	if(StringUtils.isBlank(o1.toString())){
		            	type = 1;
	                	map.put("field", field.getName());
	                	map.put("oldValue", o1);
	                	map.put("newValue", "");
	                	map.put("type", type);
	                	list.add(map);
	            	}
	            }else{//修改
	                Object o2 = getMethod.invoke(newBean);
	                o2 = (null == o2 || "".equals(o2.toString())) ? "" : o2;
	                
	                if (!o1.toString().equals(o2.toString())) {
	                	map.put("field", field.getName());
	                	map.put("oldValue", o1);
	                	map.put("newValue", o2);
	                	map.put("type", type);
	                	list.add(map);
	                }
	            }
            }
        } catch (Exception e) {
        }
        
        System.out.println(list);
        return list;
    }
}
