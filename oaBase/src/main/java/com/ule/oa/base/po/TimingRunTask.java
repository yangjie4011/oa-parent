package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.TimingRunTaskTbl;

/**
 * @ClassName: TimingRunTask
 * @Description: 定时启动流程表
 * @author mahaitao
 * @date 2017年6月29日 下午7:10:01
*/
@JsonInclude(Include.NON_NULL)
public class TimingRunTask extends TimingRunTaskTbl{

	private static final long serialVersionUID = -7655534386158598898L;
	//流程状态:0-未开始、1-已开始
	public static final Long PROCESS_STATUS0 = 0L;
	public static final Long PROCESS_STATUS1 = 1L;
}
