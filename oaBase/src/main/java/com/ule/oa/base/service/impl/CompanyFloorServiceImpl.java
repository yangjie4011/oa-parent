package com.ule.oa.base.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.ule.oa.base.mapper.CompanyFloorMapper;
import com.ule.oa.base.po.CompanyFloor;
import com.ule.oa.base.service.CompanyFloorService;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.UploadUtil;
import com.ule.oa.common.utils.json.JSONUtils;

/**
  * @ClassName: CompanyServiceImpl
  * @Description: 公司业务层
  * @author minsheng
  * @date 2017年5月8日 下午4:37:23
 */
@Service
public class CompanyFloorServiceImpl implements CompanyFloorService {
	@Autowired
	private CompanyFloorMapper companyFloorMapper;
	
	@Override
	public List<CompanyFloor> getFloors(CompanyFloor companyFloor) {
		return companyFloorMapper.getListByCondition(companyFloor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void uploadAndUpdate(CommonsMultipartFile file, Long id) {
 		String filename = file.getOriginalFilename();
		String fileFullName = "uleOA/pic/companySeat/" + DateUtils.getNow().replace(" ", "").replace("-", "").replace(":", "") + "/" + filename;
		File temfile = null;
		try {
			temfile = new File(filename);
			FileUtils.writeByteArrayToFile(temfile, UploadUtil.fileToByteArray(file));
			String result = UploadUtil.uploadToQiNiu(temfile, fileFullName,	ConfigConstants.QINIU_HOST);
			Map<String, String> resultMap = (Map<String, String>) JSONUtils.readAsMap(result);
			if(null != resultMap && "success".equals(resultMap.get("status")) && StringUtils.isNotBlank(resultMap.get("url"))){
				CompanyFloor model = new CompanyFloor();
				model.setId(id);
				model.setFloorSeatPicUrl(resultMap.get("url"));
				model.setUpdateUser("system");
				model.setUpdateTime(new Date());
				companyFloorMapper.update(model);
			}else{
				throw new Exception("保存失败!");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (null != temfile) {
				if(!temfile.delete()) {
					
				}
			}
		}
	}

	@Override
	public String getPicUrlById(Long id) {
		CompanyFloor companyFloor = companyFloorMapper.selectByPrimaryKey(id);
		if(null == companyFloor){
			return "";
		}
		return companyFloor.getFloorSeatPicUrl();
	}

	@Override
	public CompanyFloor getById(Long id) {
		return companyFloorMapper.selectByPrimaryKey(id);
	}
}
