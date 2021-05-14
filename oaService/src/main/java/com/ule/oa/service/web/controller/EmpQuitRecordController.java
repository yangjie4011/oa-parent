package com.ule.oa.service.web.controller;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmpQuitRecordService;
import com.ule.oa.common.cache.distlock.DistLockUtil;
import com.ule.oa.common.utils.http.IPUtils;

/**
  * @ClassName: EmpQuitRecordController
  * @Description: 员工离职记录控制层
  * @author minsheng
  * @date 2017年7月27日 下午2:14:26
 */
@Controller
@RequestMapping("empQuitRecord")
public class EmpQuitRecordController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EmpQuitRecordService empQuitRecordService;
	
	/**
	  * empJobStatusDoc(根据离职时间更改员工离职状态)
	  * @Title: empJobStatusDoc
	  * @Description: 根据离职时间更改员工离职状态
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/empJobStatusDoc.htm")
	public Map<String, String> empJobStatusDoc(){
		new Thread(new Runnable() {
			public void run() {
  				String lockValue = DistLockUtil.lock("updateQuitAndSaveInfos",120L);		
				if(StringUtils.isBlank(lockValue)){
					logger.info("修改离职信息操作已经启动，一天内不能重复调用!!!");			
				}else{//锁定定时
					try {
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
						logger.info("修改离职信息开始操作，时间为"+df.format(System.currentTimeMillis()));
  						empQuitRecordService.empJobStatusDoc();
					} catch (Exception e) {
						logger.error("根据离职时间更改员工离职状态失败，失败原因="+e.getMessage());
					}
				}
			}
		}).start();
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("response", "empJobStatusDoc触发成功,请稍后查看数据！");
		return map;
	}
	
}