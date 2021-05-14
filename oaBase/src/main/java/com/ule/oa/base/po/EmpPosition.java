package com.ule.oa.base.po;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpPositionTbl;

/**
  * @ClassName: EmpPostion
  * @Description: 员工岗位表
  * @author minsheng
  * @date 2017年5月8日 下午1:45:43
 */
@JsonInclude(Include.NON_NULL)
public class EmpPosition extends EmpPositionTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 3899439546641764536L;
	
	private Position position;
	
	private List<String> positionNames;

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public List<String> getPositionNames() {
		return positionNames;
	}

	public void setPositionNames(List<String> positionNames) {
		this.positionNames = positionNames;
	}
}
