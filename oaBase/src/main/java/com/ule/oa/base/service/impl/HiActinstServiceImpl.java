package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.HiActinstMapper;
import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.service.HiActinstService;

/**
  * @ClassName: HiActinstServiceImpl
  * @Description: 流程信息业务层
  * @author wufei
  * @date 2017年6月6日 上午9:56:25
 */
@Service
public class HiActinstServiceImpl implements HiActinstService{
	
	@Autowired
	private HiActinstMapper hiActinstMapper;

	@Override
	public List<HiActinst> getList(HiActinst hiActinst) {
		return hiActinstMapper.getList(hiActinst);
	}

	@Override
	public List<HiActinst> getListByRunId(Long id) {
		return hiActinstMapper.getListByRunId(id);
	}

	@Override
	public List<HiActinst> getWaitListByRunId(Long id) {
		return hiActinstMapper.getWaitListByRunId(id);
	}
	
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
	public HiActinst getFirstAuditUserByBillTypeAndBillId(String billType,Long billId){
		HiActinst hiActinst = new HiActinst();
		hiActinst.setBillType(billType);
		hiActinst.setBillId(billId);
		
		return hiActinstMapper.getFirstAuditUserByBillTypeAndBillId(hiActinst);
	}
	
	/**
	  * getListByBill(根据单据类型和单据id获得流程信息)
	  * @Title: getListByBill
	  * @Description: 根据单据类型和单据id获得流程信息
	  * @param hiActinst
	  * @return    设定文件
	  * List<HiActinst>    返回类型
	  * @throws
	 */
	public List<HiActinst> getListByBill(HiActinst hiActinst){
		return hiActinstMapper.getListByBill(hiActinst);
	}
}
