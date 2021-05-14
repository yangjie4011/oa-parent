package com.ule.oa.base.service;

import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.ule.oa.base.po.CompanyFloor;

/**
 * 
  * @ClassName: CompanyFloorService
  * @Description: 公司楼层业务层
  * @author jiwenhang
  * @date 2017年5月23日 下午4:07:56
 */
public interface CompanyFloorService {

	List<CompanyFloor> getFloors(CompanyFloor companyFloor);

	void uploadAndUpdate(CommonsMultipartFile file, Long id);

	String getPicUrlById(Long id);
	
	CompanyFloor getById(Long id);

}
