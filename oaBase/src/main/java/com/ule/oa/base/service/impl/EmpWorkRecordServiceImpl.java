package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpWorkRecordMapper;
import com.ule.oa.base.po.EmpWorkRecord;
import com.ule.oa.base.service.EmpWorkRecordService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;

/**
  * @ClassName: EmpWorkRecordServiceImpl
  * @Description: 员工工作经历业务层
  * @author minsheng
  * @date 2017年5月9日 上午9:10:06
 */
@Service
public class EmpWorkRecordServiceImpl implements EmpWorkRecordService {
	@Autowired
	private EmpWorkRecordMapper empWorkRecordMapper;
	@Autowired
	private UserService userService;

	/**
	  * save(保存员工工作经历)
	  * @Title: save
	  * @Description: 保存员工工作经历
	  * @param record    设定文件
	  * void    返回类型
	  * @throws
	 */
	public void save(EmpWorkRecord record){
		record.setCreateTime(new Date());
		record.setCreateUser(userService.getCurrentAccount());
		record.setDelFlag(ConfigConstants.IS_NO_INTEGER);
		
		empWorkRecordMapper.save(record);
	}

	/**
	  * getListByCondition(获取所有员工的工作经历)
	  * @Title: getListByCondition
	  * @Description: 获取所有员工的工作经历
	  * @param id
	  * @return    设定文件
	  * List<EmpWorkRecord>    返回类型
	  * @throws
	 */
	public List<EmpWorkRecord> getListByCondition(EmpWorkRecord empWorkRecord){
		return empWorkRecordMapper.getListByCondition(empWorkRecord);
	}

	/**
	 * @throws OaException 
	  * updateById(根据主键更新员工工作经历)
	  * @Title: updateById
	  * @Description: 根据主键更新员工工作经历
	  * @param record
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	public int updateById(EmpWorkRecord record) throws OaException{
		int updateCount = empWorkRecordMapper.updateById(record);
		
		if(updateCount == 0){
			throw new OaException("您当前编辑的员工工作经历已经被其它人修改过，请重新编辑");
		}
		
		return updateCount;
	}

	@Override
	public int saveBatch(List<EmpWorkRecord> empWorkRecords) {
		if(null != empWorkRecords && empWorkRecords.isEmpty()){
			return empWorkRecordMapper.saveBatch(empWorkRecords);
		}
		return 0;
	}

	@Override
	public int deleteBatchNotApply(List<EmpWorkRecord> empWorkRecords,
			Long employeeId, String updateUser, Date updateTime) {
		return empWorkRecordMapper.deleteBatchNotApply(empWorkRecords,employeeId,updateUser,updateTime);
	}

}
