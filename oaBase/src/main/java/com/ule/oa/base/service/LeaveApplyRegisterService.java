package com.ule.oa.base.service;

import org.springframework.validation.BindingResult;
import com.ule.oa.base.common.ResponseDTO;
import com.ule.oa.base.po.LeaveApplyRegister;
import com.ule.oa.common.utils.PageModel;

public interface LeaveApplyRegisterService {
	
	/**
	 * 休假登记
	 * @param data
	 * @param result
	 * @return
	 */
	public ResponseDTO registerLeave(LeaveApplyRegister data,BindingResult result) throws Exception;
	
	/**
	 * 分页查询休假登记
	 * @param data
	 * @return
	 */
	public PageModel<LeaveApplyRegister> getRegisterLeaveListByPage(LeaveApplyRegister data);

}
