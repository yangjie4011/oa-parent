package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;

public interface ViewTaskInfoService {
	
	List<ViewTaskInfoTbl> queryTasksByProcessId(String processId);

	int save(ViewTaskInfoTbl viewTaskInfoTbl);
	
	/**
	  * getFirstAuditUser(根据单据类型和单据主键获取第一级审批人)
	  * @Title: getFirstAuditUser
	  * @Description: 根据单据类型和单据主键获取第一级审批人
	  * @return    设定文件
	  * HiActinst    返回类型
	  * @throws
	 */
	ViewTaskInfoTbl getFirstAuditUser(String processId,String processKey,boolean flag);

	ViewTaskInfoTbl getAttnAuditUser(String processId, String processKey,
			boolean flag,int attnType);
	
	int updateStatusById(ViewTaskInfoTbl viewTaskInfoTbl);
	
	List<ViewTaskInfoTbl> queryTasksByProcessIdList(List<String> processIdList);

}
