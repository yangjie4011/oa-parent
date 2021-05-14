package com.ule.oa.base.service;

import java.util.Map;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import com.ule.oa.base.po.Employee;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.PageModel;

public interface EmployeeRecordService {
	
	/**
	  * getListByPage(分页查询员工信息)
	  * @Title: getListByPage
	  * @Description: 分页查询员工信息
	  * @param employee
	  * @return    设定文件
	  * PageModel<Employee>    返回类型
	  * @throws
	 */
	public PageModel<Employee> getListByPage(Employee employee);
	
	/**
	  * getUpdateInfo(获取履历编辑信息)
	  * @Title: getUpdateInfo
	  * @Description: 获取履历编辑信息
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> getUpdateInfo(Long employeeId);
	
	/**
	  * getScanInfo(获取履历查看信息)
	  * @Title: getScanInfo
	  * @Description: 获取履历查看信息
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> getScanInfo(Long employeeId);
	
	/**
	  * saveBaseInfo(保存员工基本信息)
	  * @Title: saveBaseInfo
	  * @Description: 保存员工基本信息
	  * @param employee
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> saveBaseInfo(Employee employee) throws OaException;
	
	/**
	  * saveBaseInfo(保存员工在职信息)
	  * @Title: savePayrollInfo
	  * @Description: 保存员工在职信息
	  * @param employee
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> savePayrollInfo(Employee employee) throws Exception;
	
	/**
	  * saveEducationExperience(保存员工教育经历)
	  * @Title: saveEducationExperience
	  * @Description: 保存员工教育经历
	  * @param empSchoolListStr
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> saveEducationExperience(String empSchoolListStr,Long employeeId) throws OaException;
	
	/**
	  * saveTrainingCertificate(保存员工培训证书)
	  * @Title: saveTrainingCertificate
	  * @Description: 保存员工培训证书
	  * @param empTrainingListStr
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> saveTrainingCertificate(String empTrainingListStr,Long employeeId) throws OaException;
	
	/**
	  * saveEducationExperience(保存员工工作经历)
	  * @Title: saveEducationExperience
	  * @Description: 保存员工工作经历
	  * @param empWorkRecordListStr
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> saveWorkExperience(String empWorkRecordListStr,Long employeeId) throws OaException;
	
	/**
	  * saveEmergencyContact(保存员工紧急联系人)
	  * @Title: saveEmergencyContact
	  * @Description: 保存员工紧急联系人
	  * @param empUrgentContactListStr
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> saveEmergencyContact(String empUrgentContactListStr,Long employeeId) throws OaException;
	
	/**
	  * saveSpouseInfo(保存员工配偶信息)
	  * @Title: saveSpouseInfo
	  * @Description: 保存员工配偶信息
	  * @param spouseInfoListStr
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> saveSpouseInfo(String spouseInfoListStr,Long employeeId) throws OaException;
	
	/**
	  * saveChildrenInfo(保存员工子女信息)
	  * @Title: saveChildrenInfo
	  * @Description: 保存员工子女信息
	  * @param childrenInfoListStr
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> saveChildrenInfo(String childrenInfoListStr,Long employeeId) throws OaException;
	
	/**
	  * saveAchievementAndRewardMerit(保存员工业绩与奖惩)
	  * @Title: saveAchievementAndRewardMerit
	  * @Description: 保存员工业绩与奖惩
	  * @param empAchievementListStr
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> saveAchievementAndRewardMerit(String empAchievementListStr,Long employeeId) throws OaException;
	
	/**
	  * saveTrainRecord(保存员工培训记录)
	  * @Title: saveTrainRecord
	  * @Description: 保存员工业绩与奖惩
	  * @param trainRecordListStr
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> saveTrainRecord(String trainRecordListStr,Long employeeId) throws OaException;
	
	/**
	  * saveContractSignRecord(保存员工合同签订记录)
	  * @Title: saveContractSignRecord
	  * @Description: 保存员工合同签订记录
	  * @param empContractListStr
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> saveContractSignRecord(String empContractListStr,Long employeeId) throws OaException;
	
	/**
	  * saveAssessRecord(保存员工考核记录)
	  * @Title: saveAssessRecord
	  * @Description: 保存员工考核记录
	  * @param empAppraiseListStr
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> saveAssessRecord(String empAppraiseListStr,Long employeeId) throws OaException;
	
	/**
	  * savePostRecord(保存员工岗位记录)
	  * @Title: savePostRecord
	  * @Description: 保存员工岗位记录
	  * @param postRecordListStr
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> savePostRecord(String postRecordListStr,Long employeeId) throws OaException;
	
	/**
	  * getPositionLevelAndSeqList(获取职位等级和职位序列列表)
	  * @Title: getPositionLevelAndSeqList
	  * @Description: 获取职位等级和职位序列列表
	  * @param positionId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> getPositionLevelAndSeqList(Long positionId);
	
	/**
	  * uploadEmployeePhoto(上传员工照片)
	  * @Title: uploadEmployeePhoto
	  * @Description: 上传员工照片
	  * @param file
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> uploadEmployeePhoto(CommonsMultipartFile file) throws Exception;
	
	/**
	  * getDownLoadInfo(获取下载信息)
	  * @Title: getDownLoadInfo
	  * @Description: 获取下载信息
	  * @param employeeId
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	public Map<String,Object> getDownLoadInfo(Long employeeId);
	

}
