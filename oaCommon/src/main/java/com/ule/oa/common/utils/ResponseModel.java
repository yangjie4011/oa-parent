package com.ule.oa.common.utils;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 文件导入之后 输出导入结果
 * @author zengli
 * @date 2015年11月12日 下午2:20:06
 */
public class ResponseModel<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 失败结果集 */
	private List<T> failResult;
	/** 成功结果集 */
	private List<T> successResult;
	/** 失败总条数 */
	private int failTotal;
	/** 成功总条数 */
	private int successTotal;
	/** 导入总条数 */
	private int total;

	/**验证结果*/
	private boolean success;	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	
	public List<T> getFailResult() {
		return failResult;
	}

	public void setFailResult(List<T> failResult) {
		this.failResult = failResult;
	}

	public List<T> getSuccessResult() {
		return successResult;
	}

	public void setSuccessResult(List<T> successResult) {
		this.successResult = successResult;
	}

	public int getFailTotal() {
		return failTotal;
	}

	public void setFailTotal(int failTotal) {
		this.failTotal = failTotal;
	}

	public int getSuccessTotal() {
		return successTotal;
	}

	public void setSuccessTotal(int successTotal) {
		this.successTotal = successTotal;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}