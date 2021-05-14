package com.ule.oa.admin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.base.po.EmpType;
import com.ule.oa.base.service.EmpTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * @ClassName: EmpTypeController
  * @Description: 员工类型控制层
  * @author minsheng
  * @date 2017年5月22日 下午8:47:59
 */
@Controller
@RequestMapping("empType")
public class EmpTypeController {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmpTypeService empTypeService;
	
	/**
	  * getListByCondition(获得员工类型下拉)
	  * @Title: getListByCondition
	  * @Description: 获得员工类型下拉
	  * @param empType
	  * @return    设定文件
	  * List<EmpType>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getListByCondition.htm")
	public List<EmpType> getListByCondition(EmpType empType){
		
		return empTypeService.getListByCondition(empType);
	}
}
