package com.ule.oa.base.mapper;

import java.util.List;

import com.ule.oa.base.po.HiActinst;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

/**
 * @ClassName: HiActinstMapper
 * @Description: 流程运行进程表
 * @author mahaitao
 * @date 2017年5月27日 13:08
*/
public interface HiActinstMapper extends OaSqlMapper{

	/**
	 * 根据ID获取
	 * @param id
	 * @return
	 */
	HiActinst getById(Long id);
	
	/**
	 * 获取列表
	 * @param HiActinst
	 * @return
	 */
	List<HiActinst> getPageList(HiActinst hiActinst);
	
	/**
	 * 获取列表
	 * @param HiActinst
	 * @return
	 */
	List<HiActinst> getList(HiActinst hiActinst);
	
	/**
	 * 获取总数
	 * @param HiActinst
	 * @return
	 */
	Integer getCount(HiActinst hiActinst);
	
	/**
	 * 保存
	 * @param HiActinst
	 * @return
	 */
	Long save(HiActinst hiActinst);
	
	/**
	 * 批量保存
	 * @param HiActinst
	 */
	void batchSave(List<HiActinst> hiActinst);
	
	/**
	 * 更新
	 * @param id
	 * @return
	 */
	Integer updateById(HiActinst hiActinst);
	
	/**
	 * 删除求执行流程进程
	 * @param hiActinst
	 * @return
	 */
	Integer refuseByRunId(HiActinst hiActinst);
	
	List<HiActinst> getListByRunId(Long id);
	
	//全部为完成任务列表
	List<HiActinst> getWaitListByRunId(Long id);
	
	/**
	  * getFirstAuditUserByBillTypeAndBillId(根据单据类型和单据主键获取第一级审批人)
	  * @Title: getFirstAuditUserByBillTypeAndBillId
	  * @Description: 根据单据类型和单据主键获取第一级审批人
	  * @param hiActinst 单据类型和单据主键
	  * @return    设定文件
	  * HiActinst    返回类型
	  * @throws
	 */
	HiActinst getFirstAuditUserByBillTypeAndBillId(HiActinst hiActinst);
	
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
