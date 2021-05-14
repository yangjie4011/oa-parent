package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpPostRecordTbl;

/**
  * @ClassName: EmpPostRecord
  * @Description: 员工岗位（职位）记录表
  * @author minsheng
  * @date 2017年5月8日 下午1:45:57
 */
@JsonInclude(Include.NON_NULL)
public class EmpPostRecord extends EmpPostRecordTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -2518494278273917372L;
	
	//自定义对象
	private Depart depart;
	private Position position;
	private Depart preDepart;
	private Position prePosition;
	
	public Depart getPreDepart() {
		return preDepart;
	}
	public void setPreDepart(Depart preDepart) {
		this.preDepart = preDepart;
	}
	public Position getPrePosition() {
		return prePosition;
	}
	public void setPrePosition(Position prePosition) {
		this.prePosition = prePosition;
	}
	public Depart getDepart() {
		return depart;
	}
	public void setDepart(Depart depart) {
		this.depart = depart;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}

}
