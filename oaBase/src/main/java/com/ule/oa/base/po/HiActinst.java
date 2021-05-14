package com.ule.oa.base.po;

import java.util.List;

import com.ule.oa.base.po.tbl.HiActinstTbl;

/**
 * @ClassName: HiActinst
 * @Description: 流程运行进程表
 * @author mahaitao
 * @date 2017年5月27日 12:58
*/
public class HiActinst extends HiActinstTbl{

	private static final long serialVersionUID = 6235452865723812541L;

	/** 是否处理:0-是、1否 */ 
	public static final Long IS_START_Y = 0L;
	public static final Long IS_START_N = 1L;
	/** 100-未完成、200-审批通过、300-审批拒绝 、400-撤消、500-提交**/
	public static final Long  STATUS_100 = 100L;
	public static final Long  STATUS_200 = 200L;
	public static final Long  STATUS_300 = 300L;
	public static final Long  STATUS_400 = 400L;
	public static final Long  STATUS_500 = 500L;
	
	//职位名称
	private String positionName;
	private String billType;
	private Long billId;
	private List<String> billTypeList;//单据类型

	public List<String> getBillTypeList() {
		return billTypeList;
	}

	public void setBillTypeList(List<String> billTypeList) {
		this.billTypeList = billTypeList;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
}
