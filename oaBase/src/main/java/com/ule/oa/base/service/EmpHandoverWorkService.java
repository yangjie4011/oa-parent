package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.EmpHandoverWork;

/**
 * @ClassName: 离职工作交接
 * @Description: 离职工作交接
 * @author yangjie
 * @date 2017年5月31日
 */
public interface EmpHandoverWorkService {
	
    public void save(EmpHandoverWork empHandoverWork);
	
	public int updateById(EmpHandoverWork empHandoverWork);
	
	public EmpHandoverWork getById(Long id);
	
	public void batchSave(List<EmpHandoverWork> list,Long ruProcdefId, Long entityId);
	
	public List<EmpHandoverWork> getListByCondition(EmpHandoverWork empHandoverWork);
	
	public void update(List<EmpHandoverWork> list,String updateUser);
	
	public void confirm(List<EmpHandoverWork> list, Long ruProcdefId, Long entityId);
	
	public void confirmHandover(List<EmpHandoverWork> list);
	
	public void approve(List<EmpHandoverWork> list,String empResignId);

}
