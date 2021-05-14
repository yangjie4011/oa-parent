package com.ule.oa.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ule.oa.base.po.DepartPosition;
import com.ule.oa.base.service.DepartPositionService;

/**
  * @ClassName: DepartPositionController
  * @Description: 部门职位信息控制层
  * @author minsheng
  * @date 2017年5月12日 下午5:03:46
 */
@Controller
@RequestMapping("departPosition")
public class DepartPositionController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DepartPositionService departPositionService;

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
	public List<DepartPosition> getListByCondition(DepartPosition departPosition){
		return departPositionService.getListByCondition(departPosition);
	}
}
