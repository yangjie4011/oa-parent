package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.DepartPosition;


/**
  * @ClassName: DepartPositionService
  * @Description: 部门职位表
  * @author minsheng
  * @date 2017年5月12日 下午4:27:54
 */
public interface DepartPositionService {
	public List<DepartPosition> getListByCondition(DepartPosition departPosition);
	public int save(DepartPosition departPosition);
	public int delByPositionId(Long positionId);
}
