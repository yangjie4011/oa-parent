package com.ule.oa.web.vo;

/**
  * @ClassName: ResultErrorVo
  * @Description: 全局异常处理器返回结果集
  * @author minsheng
  * @date 2017年10月12日 上午8:58:54
 */
public class ResultErrorVo {
	private String msg;//返回的错误信息
	private Integer code;//返回的错误状态码
	private String remark;//返回的详细信息
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
