package com.ule.oa.base.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.ule.oa.base.po.EmpApplicationRegister;
import com.ule.oa.base.po.User;
import com.ule.oa.base.vo.TaskVO;
import com.ule.oa.common.exception.OaException;


/**
 * @ClassName: EmployeeRegister
 * @Description: 员工入职登记表
 * @author yangjie
 * @date 2017年5月22日
*/
public interface EmpApplicationRegisterService {
	
	public Map<String,Object> save(EmpApplicationRegister employeeRegister,User user) throws Exception;
	
	public EmpApplicationRegister getById(Long id);
	
	public Map<String,Object> delay(EmpApplicationRegister employeeRegister) throws Exception;
	
	//根据流程实例Id查询入职对象
	public EmpApplicationRegister queryByProcessInstanceId(String processInstanceId);
	
	/**
	 * 业务类转化为显示
	 * @param taskVO
	 * @param processInstanceId
	 */
	public void setValueToVO(TaskVO taskVO,String processInstanceId,String taskId);
	
	
	/**
	 * 审批入职
	 * @param processId
	 * @param commentType
	 * @throws Exception
	 */
	public Map<String,Object> completeTask(String processId, String comment ,String commentType,EmpApplicationRegister param) throws Exception;
		

	/**
	 * 条件查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<EmpApplicationRegister> getByCondition(EmpApplicationRegister param) throws Exception;
	
	/**
	 * 保存流程实例id
	 * @param newRegister
	 */
	public void updateProcessInstanceId(EmpApplicationRegister newRegister);
	
	void sendMsg(Long employeeId, User user, String title, String text);

	public void backEntry(String processId, String taskId, String message)  throws OaException ;

	public String readEmailTemplet(String[] params, String templetPropertie) throws IOException;

}
