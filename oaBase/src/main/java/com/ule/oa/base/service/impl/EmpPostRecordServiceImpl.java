package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpPostRecordMapper;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpPostRecord;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpPostRecordService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;

/**
  * @ClassName: EmpPostRecordServiceImpl
  * @Description: 员工岗位记录业务层
  * @author minsheng
  * @date 2017年6月22日 下午7:53:10
 */
@Service
public class EmpPostRecordServiceImpl implements EmpPostRecordService {
	@Autowired
	private EmpPostRecordMapper empPostRecordMapper;
	@Autowired
	private DepartService departService;
	@Autowired
	private PositionService positionService;
	
	@Resource
	private UserService userService;
	
	private Date CURRENT_TIME = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);//当前时间
	
	public List<EmpPostRecord> getListByCondition(EmpPostRecord empPostRecord){
		List<EmpPostRecord> list = empPostRecordMapper.getListByCondition(empPostRecord);
		
		if(null != list && list.size()>0){
			for(EmpPostRecord ep : list){
				//封装原部门
				Depart depart = departService.getById(ep.getPreDepartId());
				if(null != depart){
					depart.setName(depart.getName());
				}else{
					depart = new Depart();
				}
				ep.setPreDepart(depart);
				
				//封装原岗位
				Position po = positionService.getById(ep.getPrePositionId());
				if(null != po){
					po.setPositionName(po.getPositionName());
				}else{
					po = new Position();
				}
				ep.setPrePosition(po);
			}
		}else{
			list = new ArrayList<EmpPostRecord>();
		}
		return list;
	}

	@Override
	public Integer save(EmpPostRecord empPostRecord) {
		
		empPostRecord.setCreateUser(userService.getCurrentAccount());
		empPostRecord.setCreateTime(CURRENT_TIME);
		empPostRecord.setUpdateUser(userService.getCurrentAccount());
		empPostRecord.setUpdateTime(CURRENT_TIME);
		empPostRecord.setDelFlag(ConfigConstants.IS_NO_INTEGER);
		return empPostRecordMapper.save(empPostRecord);
	}

	@Override
	public Integer updateById(EmpPostRecord empPostRecord) throws Exception{
		
		empPostRecord.setUpdateUser(userService.getCurrentAccount());
		empPostRecord.setUpdateTime(CURRENT_TIME);
		
		Integer count = empPostRecordMapper.updateById(empPostRecord);
		
		if(0 == count){
			throw new OaException("本条数据已被其他人更新，请确认！");
		}
		return count;
	}
}
