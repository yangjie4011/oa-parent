package com.ule.oa.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.ule.oa.base.po.CompanyFloor;
import com.ule.oa.base.po.CompanySeat;
import com.ule.oa.base.service.CompanyFloorService;
import com.ule.oa.base.service.CompanySeatService;
import com.ule.oa.common.utils.JSON;
import com.ule.oa.common.utils.json.JSONUtils;

/**
 * 
  * @ClassName: CompanySeatController
  * @Description: 公司座位信息
  * @author jiwenhang
  * @date 2017年5月23日 下午3:16:06
 */
@Controller
@RequestMapping("companySeat")
public class CompanySeatController {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CompanySeatService companySeatService;
	@Autowired
	private CompanyFloorService companyFloorService;
	/**
	 * 
	  * index(座位图首页)
	  * @Title: index
	  * @Description: TODO
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	@RequestMapping("/index.htm")
	public String index(){
		return "base/companySeat/companySeat_index";
	}
	
	/**
	 * 
	  * manage(座位图管理页)
	  * @Title: manage
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	@RequestMapping("/manage.htm")
	public String manage(){
		return "base/companySeat/companySeat_manage";
	}

	/**
	 * 
	  * floors(获取所有楼层)
	  * @Title: floors
	  * @Description: TODO
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/floors.htm")
	public String floors(CompanyFloor companyFloor){
		return JSONUtils.write(companyFloorService.getFloors(companyFloor));
	}
	
	/**
	 * 
	  * floors(获取楼层所有可用座位)
	  * @Title: floors
	  * @Description: TODO
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/seats.htm")
	public String seats(CompanySeat companySeat){
		return JSONUtils.write(companySeatService.getListByCondition(companySeat));
	}
	
	/**
	 * 
	  * uploadAndSave(上传图片,更新url)
	  * @Title: uploadAndSave
	  * @Description: TODO
	  * @param request
	  * @param file
	  * @return    设定文件
	  * String    返回类型
	  * @throws
	 */
	@ResponseBody
	@RequestMapping("/uploadAndUpdate.htm")
	public JSON uploadAndUpdate(HttpServletRequest request, @RequestParam("file") CommonsMultipartFile file, Long id){
		JSON json = new JSON(false, null, null);
		try {
			companyFloorService.uploadAndUpdate(file, id);
			json.setSuccess(true);
			json.setMessage("保存成功!");
		} catch (Exception e) {
			json.setMessage("保存失败!");
		}
		return json;
	}
	
	@ResponseBody
	@RequestMapping("/getPic.htm")
	public String getPic(HttpServletRequest request, Long id){
		return JSONUtils.write(companyFloorService.getPicUrlById(id));
	}
	
}
