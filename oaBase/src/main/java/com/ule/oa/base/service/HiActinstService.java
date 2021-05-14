package com.ule.oa.base.service;

import java.util.List;

import com.ule.oa.base.po.HiActinst;

/**
  * @ClassName: HiActinstService
  * @Description: 流程信息业务层接口
  * @author wufei
  * @date 2017年6月6日 上午9:56:25
 */
public interface HiActinstService {
	
	/**
	 * 获取列表
	 * @param HiActinst
	 * @return
	 */
	List<HiActinst> getList(HiActinst hiActinst);
	//全部任务列表
	List<HiActinst> getListByRunId(Long id);
	//全部为完成任务列表
	List<HiActinst> getWaitListByRunId(Long id);
	
	/**
	  * getFirstAuditUserByBillTypeAndBillId(根据单据类型和单据主键获取第一级审批人)
	  * @Title: getFirstAuditUserByBillTypeAndBillId
	  * @Description: 根据单据类型和单据主键获取第一级审批人
	  * @param billType	单据类型:10:入职登记申请、20:离职申请、30:延时工作申请、40:异常考勤、50:排班、60-请假申请、70-外出申请、80-出差申请（普通员工）
	  * @param billId	单据主键
	  * @return    设定文件
	  * HiActinst    返回类型
	  * @throws
	 */
	HiActinst getFirstAuditUserByBillTypeAndBillId(String billType,Long billId);
	
	/**
	  * getListByBill(根据单据类型和单据id获得流程信息)
	  * @Title: getListByBill
	  * @Description: 根据单据类型和单据id获得流程信息
	  * @param hiActinst
	  * @return    设定文件
	  * List<HiActinst>    返回类型
	  * @throws
	 */
	List<HiActinst> getListByBill(HiActinst hiActinst);
}
