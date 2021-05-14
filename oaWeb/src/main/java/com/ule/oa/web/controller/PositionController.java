package com.ule.oa.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.JsonWriter;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.json.JSONUtils;

/**
  * @ClassName: PositionController
  * @Description: 职位管理
  * @author minsheng
  * @date 2017年5月12日 上午10:54:07
 */
@Controller
@RequestMapping("position")
public class PositionController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PositionService positionService; 
	@Autowired
	private EmployeeService employeeService;
	
	@RequestMapping("/index.htm")
	public String index(HttpServletRequest request) throws OaException{
		request.setAttribute("companyId", employeeService.getCurrentEmployee().getCompanyId());
		
		return "base/position/position_index";
	}
	
	@ResponseBody
	@RequestMapping("/getPageList.htm")
	public PageModel<Position> getPageList(Position position){
		PageModel<Position> pm=new PageModel<Position>();
		pm.setRows(new java.util.ArrayList<Position>());
		pm.setTotal(0);
		pm.setPageNo(1);
		
		try {
			pm = positionService.getPageList(position);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return pm;
	}
	
	/**
	  * getByCondition(根据查询条件看职位信息)
	  * @Title: getByCondition
	  * @Description: 根据查询条件看职位信息
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getByCondition.htm")
	public Position getPositionByCondition(Position position){
		return positionService.getByCondition(position);
	}
	
	@ResponseBody
	@RequestMapping("/save.htm")
	public JSON save(Position position){
		Map<String,Object> map = new HashMap<String,Object>();
		
		try{
			positionService.savePosition(position);
			
			map.put("message", "保存成功");
			map.put("flag", true);
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "保存失败,"+e.getMessage());
		}
		
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/update.htm")
	public JSON update(Position position){
		Map<String,Object> map = new HashMap<String,Object>();
		
		try{
			positionService.update(position);
			
			map.put("message", "修改成功");
			map.put("flag", true);
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "修改失败,msg="+e.getMessage());
		}
		
		return JsonWriter.successfulResult(map);
	}
	
	@ResponseBody
	@RequestMapping("/delete.htm")
	public JSON delete(Position position){
		Map<String,Object> map = new HashMap<String,Object>();
		
		try{
			positionService.delete(position);
			
			map.put("message", "修改成功");
			map.put("flag", true);
		}catch(Exception e){
			map.put("flag", false);
			map.put("message", "修改失败,msg="+e.getMessage());
		}
		
		return JsonWriter.successfulResult(map);
	}
	
	/**
	  * getListByCondition(根据查询条件看职位信息)
	  * @Title: getListByCondition
	  * @Description: 根据查询条件看职位信息
	  * List<Position>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getListByCondition.htm")
	public List<Position> getListByCondition(Position position){
		return positionService.getListByCondition(position);
	}
	
	/**
	  * getListByCondition(根据查询条件看职位信息)
	  * @Title: getListByCondition
	  * @Description: 根据查询条件看职位信息
	  * List<Position>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getPosSeqAndLv.htm")
	public Map<String,List<String>> getPosSeqAndLv(Position position){
		return positionService.getPosSeqAndLv(position);
	}
	
	@RequestMapping("/getPositionTree.htm")
	public String getPositionTree(){
		return "base/position/position_tree";
	}
	
	/**
	  * getTreeList(根据条件查询部门结果集)
	  * @Title: getTreeList
	  * @Description: 根据条件查询部门结果集
	  * @param depart    设定文件
	  * void    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getTreeList.htm")
	public JSON getTreeList(Position position){
		return JsonWriter.successfulResult(JSONUtils.write(positionService.getTreeList(position)));
	}
	
	/**
	  * getSeqList(根据职位类别获取职位序列)
	  * @Title: getListByCondition
	  * @Description: 根据查询条件看职位信息
	  * List<Position>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getSeqList.htm")
	public List<String> getSeqList(String positionType){
		return positionService.getSeqList(positionType);
	}
	
	/**
	  * getLevelList(根据职位类别获取职位级别)
	  * @Title: getListByCondition
	  * @Description: 根据查询条件看职位信息
	  * List<Position>    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/getLevelList.htm")
	public List<String> getLevelList(String positionType){
		return positionService.getLevelList(positionType);
	}
	
}
