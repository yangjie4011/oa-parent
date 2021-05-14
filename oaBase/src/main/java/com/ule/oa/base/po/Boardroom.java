package com.ule.oa.base.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.BoardroomTbl;

@JsonInclude(Include.NON_NULL)
public class Boardroom extends BoardroomTbl {

	private static final long serialVersionUID = -5072378552515238787L;

	private String floorName;//楼层

	public String getFloorName() {
		return floorName;
	}

	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}
	
}
