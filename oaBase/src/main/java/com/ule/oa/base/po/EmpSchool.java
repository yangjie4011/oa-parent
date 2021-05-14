package com.ule.oa.base.po;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ule.oa.base.po.tbl.EmpSchoolTbl;

/**
  * @ClassName: EmpSchool
  * @Description: 员工毕业院校表
  * @author minsheng
  * @date 2017年5月8日 下午1:46:11
 */
@JsonInclude(Include.NON_NULL)
public class EmpSchool extends EmpSchoolTbl {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = 7025713239506471816L;
	
	//自定义属性名
	private String degreeName;//学位中文名.
	private String educationName;//学位中文名.
	public String getDegreeName() {
		return degreeName;
	}
	public void setDegreeName(String degreeName) {
		this.degreeName = degreeName;
	}
	public String getEducationName() {
		return educationName;
	}
	public void setEducationName(String educationName) {
		this.educationName = educationName;
	}
	

}
