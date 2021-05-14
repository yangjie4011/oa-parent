package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.EmpApplicationMapper;
import com.ule.oa.base.po.EmpApplication;
import com.ule.oa.base.po.EmpFamilyMember;
import com.ule.oa.base.po.EmpSchool;
import com.ule.oa.base.po.EmpTraining;
import com.ule.oa.base.po.EmpUrgentContact;
import com.ule.oa.base.po.EmpWorkRecord;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.User;
import com.ule.oa.base.po.tbl.EmpApplicationTbl;
import com.ule.oa.base.service.EmpApplicationService;
import com.ule.oa.base.service.EmpFamilyMemberService;
import com.ule.oa.base.service.EmpSchoolService;
import com.ule.oa.base.service.EmpTrainingService;
import com.ule.oa.base.service.EmpUrgentContactService;
import com.ule.oa.base.service.EmpWorkRecordService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.utils.DateUtils;

@Service
public class EmpApplicationServiceImpl implements EmpApplicationService{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private UserService userService;
	
	@Resource
	private EmpApplicationMapper empApplicationMapper;
	
	@Resource
	private EmployeeService employeeService;
	
	@Resource
	private EmpFamilyMemberService empFamilyMemberService;
	
	@Resource
	private EmpUrgentContactService empUrgentContactService;
	
	@Resource
	private EmpSchoolService empSchoolService;
	
	@Resource
	private EmpTrainingService empTrainingService;
	
	@Resource
	private EmpWorkRecordService empWorkRecordService;
	
	private static String CURRENT_USER_NAME = null;//当前登陆用户姓名
	
	private static Date CURRENT_TIME = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);//当前时间
	
	private static Long CURRENT_EMPLOYEE_ID = null;//当前员工ID
	
	private static String CURRENT_VERSION = null;//当前员工ID

	@Override
	public Integer checkExistApply(Employee employee) {
		CURRENT_EMPLOYEE_ID =  employee.getId();
		List<EmpApplication> list = getEmpApplicationList(CURRENT_EMPLOYEE_ID,null);
		if(null != list && !list.isEmpty()){
			return list.size();
		}
		return 0;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void apply(Employee employee) {
		
		CURRENT_EMPLOYEE_ID =  employee.getId();//资料主键
		
		User user = userService.getCurrentUser();
		CURRENT_USER_NAME = user.getUserName();
		
		String versionDataBase =  empApplicationMapper.getMaxVersionByEmpId(CURRENT_EMPLOYEE_ID);
		Long versionDataBaseLong = StringUtils.isNotBlank(versionDataBase)?Long.parseLong(versionDataBase):0L;
		versionDataBaseLong++;
		CURRENT_VERSION = versionDataBaseLong.toString();
		
		Employee employeeCondition = new Employee();
		employeeCondition.setId(CURRENT_EMPLOYEE_ID);
		Employee odlEmployee = employeeService.getByCondition(employeeCondition);//得到原始数据
		
		//新老数据作比较
		//主数据,参数解释 ：依次为旧对象，新对象，记录模块，操作类型(1-新增、2-修改，3-删除)
		List<EmpApplicationTbl> list = contrastMainEmpInfo(odlEmployee,employee,"baseInfo",2L);
	
		//明细数据对比开始
		//1.配偶信息
		//2.子女信息
		//配偶，子女都属于家庭成员信息
		EmpFamilyMember familyCondition = new EmpFamilyMember();//familyCondition.getRelation(),0:配偶，1：子女
		familyCondition.setEmployeeId(CURRENT_EMPLOYEE_ID);
		List<EmpFamilyMember> oldListFamilyMember = empFamilyMemberService.getListByCondition(familyCondition);
		List<EmpFamilyMember> newListFamilyMember = employee.getEmpFamilyMembers();
		List<EmpApplicationTbl> familyList = contrastFamilyList(oldListFamilyMember,newListFamilyMember,"family");
		list.addAll(familyList);
		
		
		//3.紧急联系人
		EmpUrgentContact urgentContactCondition = new EmpUrgentContact();
		urgentContactCondition.setEmployeeId(CURRENT_EMPLOYEE_ID);
		List<EmpUrgentContact> oldListUrgent = empUrgentContactService.getListByCondition(urgentContactCondition);
		List<EmpUrgentContact> newListUrgent = employee.getEmpUrgentContacts();
		List<EmpApplicationTbl> urgentList = contrastUrgentList(oldListUrgent,newListUrgent,"urgentContact");
		list.addAll(urgentList);
		
		//4.教育经历
		EmpSchool empSchoolCondition = new EmpSchool();
		empSchoolCondition.setEmployeeId(CURRENT_EMPLOYEE_ID);
		List<EmpSchool> oldListSchool = empSchoolService.getListByCondition(empSchoolCondition);
		List<EmpSchool> newListSchool = employee.getEmpSchools();
		List<EmpApplicationTbl> schoolList = contrastSchoolList(oldListSchool,newListSchool,"school");
		list.addAll(schoolList);
		
		//5.培训和证书
		EmpTraining empTrainingCondition = new EmpTraining();
		empTrainingCondition.setEmployeeId(CURRENT_EMPLOYEE_ID);
		List<EmpTraining> oldListTraining = empTrainingService.getListByCondition(empTrainingCondition);
		List<EmpTraining> newListTraining = employee.getEmpTrainings();
		List<EmpApplicationTbl> trainingList = contrastTrainingList(oldListTraining,newListTraining,"training");
		list.addAll(trainingList);
		
		//6.工作经历
		EmpWorkRecord empWorkRecordCondition = new EmpWorkRecord();
		empWorkRecordCondition.setEmployeeId(CURRENT_EMPLOYEE_ID);
		List<EmpWorkRecord> oldListEmpWorkRecord = empWorkRecordService.getListByCondition(empWorkRecordCondition);
		List<EmpWorkRecord> newListEmpWorkRecord = employee.getEmpWorkRecords();
		List<EmpApplicationTbl> empWorkRecordList = contrastWorkRecordList(oldListEmpWorkRecord,newListEmpWorkRecord,"workRecord");
		list.addAll(empWorkRecordList);
		
		//批量保存数据
		if(null != list && !list.isEmpty()) {
			empApplicationMapper.insertBatch(list);
		}
		
	}
	
	private List<EmpApplicationTbl> contrastMainEmpInfo(Employee oldBean, Employee newBean,String module,Long type) {
        List<EmpApplicationTbl> list= new ArrayList<EmpApplicationTbl>();
        try {
            
            String oldPoliticalStatus = oldBean.getPoliticalStatus().toString(),
            	   newPoliticalStatus = newBean.getPoliticalStatus().toString();
            
            String oldUleAccount = oldBean.getUleAccount(),
                   newUleAccount = newBean.getUleAccount();
            String oldMobile = oldBean.getMobile(),
                   newMobile = newBean.getMobile();
            
            String oldHouseholdRegister = oldBean.getHouseholdRegister().toString(),
                   newHouseholdRegister = newBean.getHouseholdRegister().toString();
            
            String oldMaritalStatus = oldBean.getMaritalStatus().toString(),
                   newMaritalStatus = newBean.getMaritalStatus().toString();
            
            String oldAddress = oldBean.getAddress(),
                   newAddress = newBean.getAddress();
            String showValue;
            
        	if(!oldPoliticalStatus.equals(newPoliticalStatus)){//政治面貌
        		showValue = "政治面貌 由 "+oldPoliticalStatus+" 修改为 "+newPoliticalStatus;
        		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"politicalStatus",oldPoliticalStatus,newPoliticalStatus,showValue); 
        		list.add(empApplicationTbl1);
        	}
        	if(!oldUleAccount.equals(newUleAccount)){//邮乐账号
        		showValue = "邮乐账号 由 "+oldUleAccount+" 修改为 "+newUleAccount;
        		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"uleAccount",oldUleAccount,newUleAccount,showValue); 
        		list.add(empApplicationTbl2);
        	}
        	if(!oldMobile.equals(newMobile)){//手机
        		showValue = "手机号码 由 "+oldMobile+" 修改为 "+newMobile;
        		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"mobile",oldMobile,newMobile,showValue); 
        		list.add(empApplicationTbl3);
        	}
        	if(!oldHouseholdRegister.equals(newHouseholdRegister)){//户籍
        		showValue = "户籍面貌 由 "+oldHouseholdRegister+" 修改为 "+newHouseholdRegister;
        		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"householdRegiste",oldHouseholdRegister,newHouseholdRegister,showValue); 
        		list.add(empApplicationTbl4);
        	}
        	if(!oldMaritalStatus.equals(newMaritalStatus)){//婚姻状况
        		showValue = "婚姻状况 由 "+oldMaritalStatus+" 修改为 "+newMaritalStatus;
        		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"maritalStatus",oldMaritalStatus,newMaritalStatus,showValue); 
        		list.add(empApplicationTbl5);
        	}
        	if(!oldAddress.equals(newAddress)){//地址
        		showValue = "地址由 "+oldAddress+" 修改为 "+newAddress;
        		EmpApplicationTbl empApplicationTbl6 = setEmployeeLog(type,module,"address",oldAddress,newAddress,showValue); 
        		list.add(empApplicationTbl6);
        	}
        	
        } catch (Exception e) {
            logger.error("数据对比出错{}",e);
        }
        logger.info("lst:{}",list);
        return list;
    }
	
	private EmpApplicationTbl setEmployeeLog(Long type,String module,String detailModule,String oldValue,String newValue,String showValue){
    	
    	EmpApplicationTbl empApplicationTbl = new EmpApplicationTbl();
    	
    	empApplicationTbl.setEmployeeId(CURRENT_EMPLOYEE_ID);
    	empApplicationTbl.setType(type);
    	empApplicationTbl.setModule(module);//信息的模块
    	empApplicationTbl.setModuleDetail(detailModule);
    	empApplicationTbl.setOldValue(oldValue);
    	empApplicationTbl.setNewValue(newValue);
    	if(null == showValue){
	    	if(type.equals(1L)){
	    		showValue = "新增"+module+"信息的"+detailModule+":"+newValue;
	        }else if(type.equals(2L)){
	        	showValue = "修改"+module+"信息的"+detailModule+"由"+(StringUtils.isBlank(oldValue)?"空":oldValue)+"修改为"+(StringUtils.isBlank(newValue)?"空":newValue);
	    	}else if(type.equals(3L)){
	    		showValue = "删除"+module+"信息的"+detailModule+":"+oldValue;
	    	}
    	}
	    empApplicationTbl.setShowValue(showValue);
    	
    	empApplicationTbl.setApprovalStatus(100L);//'审批状态:100-求审批、 200-已审批、300-驳回',
    	//empApplicationTbl.setArrayValue(1L);
    	//ArrayValue要改为，修改：记录修改的明细表数据主键，新增，修改由-1递减。便于查询时分组
    	empApplicationTbl.setVersion(CURRENT_VERSION);
    	empApplicationTbl.setDelFlag(0L);
    	empApplicationTbl.setCreateUser(CURRENT_USER_NAME);
    	empApplicationTbl.setCreateTime(CURRENT_TIME);
    	empApplicationTbl.setUpdateUser(CURRENT_USER_NAME);
    	empApplicationTbl.setUpdateTime(CURRENT_TIME);
		return empApplicationTbl;
	}
	
	
	
	//明细紧急联系人信息List对比
	private List<EmpApplicationTbl> contrastFamilyList(List<EmpFamilyMember> oldList, List<EmpFamilyMember> newList,String module) {
		
        List<EmpApplicationTbl> list= new ArrayList<EmpApplicationTbl>();
        
        List<EmpFamilyMember> deleteIdList = new ArrayList<EmpFamilyMember>();//存放删除的数据集
        if(null != oldList){deleteIdList.addAll(oldList);}
        
        List<EmpFamilyMember> addIdList = new ArrayList<EmpFamilyMember>();//存放新增的数据集
        if(null != newList){addIdList.addAll(newList);}

        if(null != oldList && null != newList) {
        	for(EmpFamilyMember newFamilyMember:newList){
            	Long newId = newFamilyMember.getId();
                for(EmpFamilyMember oldFamilyMember:oldList){

                	Long oldId = oldFamilyMember.getId();
                	if(oldId.equals(newId)){//有相等的id，说明是修改,从删除列表和新增列表移除该项记录
                		
                		addIdList.remove(newFamilyMember);
                		deleteIdList.remove(oldFamilyMember);
                		
                		//参数解释 ：依次为旧对象，新对象，记录模块，操作类型(1-新增、2-修改，3-删除)
                		if(oldFamilyMember.getRelation().intValue() == 0){
                			module = "spouse";
                		}else if(oldFamilyMember.getRelation().intValue() == 1){
                			module = "child";
                		}
                		
                		List<EmpApplicationTbl> updateList = contrastFamilyInfo(oldFamilyMember,newFamilyMember,module,2L,oldId);
                		list.addAll(updateList);//把修改的数据封装进去
                	}
                }
            	
            }
        }
        
    	//这里单独处理新增和删除的数据
        if(!deleteIdList.isEmpty()){//处理删除的数据，这里可以考虑下，怎么记录整条删除的日志
        	 for(EmpFamilyMember familyMember:deleteIdList){
         		if(familyMember.getRelation().intValue() == 0){
        			module = "spouse";
        		}else if(familyMember.getRelation().intValue() == 1){
        			module = "child";
        		}
        		List<EmpApplicationTbl> deleteList = contrastFamilyInfo(familyMember,null,module,3L,familyMember.getId());
         		list.addAll(deleteList);//把修改的数据封装进去
        	 }
        }
        
        if(!addIdList.isEmpty()){//处理新增的数据，这里可以考虑下，怎么记录整条新增除的日志
        	Long arrayVlue = -1L;
       	 for(EmpFamilyMember familyMember:addIdList){
       		    familyMember.setEmployeeId(CURRENT_EMPLOYEE_ID);
         		if(familyMember.getRelation().intValue() == 0){
        			module = "spouse";
        		}else if(familyMember.getRelation().intValue() == 1){
        			module = "child";
        		}
       	    	List<EmpApplicationTbl> deleteList = contrastFamilyInfo(null,familyMember,module,1L,arrayVlue);
        		list.addAll(deleteList);//把修改的数据封装进去
        		arrayVlue--;
       	 }	
        }
		
        return list;
	}
	
	private List<EmpApplicationTbl> contrastFamilyInfo(EmpFamilyMember oldBean, EmpFamilyMember newBean,
			String module, Long type,Long arrayValue) {
        List<EmpApplicationTbl> list= new ArrayList<EmpApplicationTbl>();
        
        String oldMemberName,newMemberName;
        String oldMemberCompanyName,newMemberCompanyName;
        String oldMemberMobile,newMemberMobile;
        String oldBirthday,newBirthday;
        String oldMemberSex,newMemberSex;
        String showValue;
        
        if(type.equals(1L)) {
        	//新增
        	if(newBean.getRelation().intValue() == 0){//0:配偶
	    	    	newMemberName = newBean.getMemberName();
	        		newMemberCompanyName = newBean.getMemberCompanyName();
	        		newMemberMobile = newBean.getMemberMobile();
	        		showValue = "配偶信息 "+newMemberName+" "+newMemberCompanyName + " "+newMemberMobile;
	        	//姓名
	        		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"memberName","",newMemberName,showValue); 
	        		empApplicationTbl1.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl1);
	        	//工作单位
	        		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"memberCompanyName","",newMemberCompanyName,showValue); 
	        		empApplicationTbl2.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl2);
	        	
	        	//电话
	        		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"memberMobile","",newMemberMobile,showValue); 
	        		empApplicationTbl3.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl3);
	        	
	        }else{//子女
	        	
	        	    newMemberName = newBean.getMemberName();
	        	    newBirthday = DateUtils.format(newBean.getBirthday(),DateUtils.FORMAT_SHORT);
	        	    newMemberSex = getChGender(newBean.getMemberSex());
	        		showValue = "子女信息 "+newMemberName+" "+newBirthday + " "+newMemberSex;
	        	//姓名
	        		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"memberName","",newMemberName,showValue); 
	        		empApplicationTbl4.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl4);
	        	
	        	//生日
	        		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"birthday","",newBirthday,showValue); 
	        		empApplicationTbl5.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl5);
	        	
	        	//电话
	        		EmpApplicationTbl empApplicationTbl6 = setEmployeeLog(type,module,"memberSex","",newMemberSex,showValue); 
	        		empApplicationTbl6.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl6);
	        }
        }
	       
        
        if(type.equals(2L)) {
        	//更新
        	if(oldBean.getRelation().intValue() == 0){//0:配偶
	        	
    	    	newMemberName = newBean.getMemberName();
        		newMemberCompanyName = newBean.getMemberCompanyName();
        		newMemberMobile = newBean.getMemberMobile();
        		
    	    	oldMemberName = oldBean.getMemberName();
    	    	oldMemberCompanyName = oldBean.getMemberCompanyName();
    	    	oldMemberMobile = oldBean.getMemberMobile();
        		showValue = "配偶信息 由 "+ oldMemberName+" "+oldMemberCompanyName + " "+oldMemberMobile+ " 修改为 "+
        				     newMemberName+" "+newMemberCompanyName + " "+newMemberMobile ;
	    	    	
	        	if(!newMemberName.equals(oldMemberName)){//姓名
	        		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"memberName",oldMemberName,newMemberName,showValue); 
	        		empApplicationTbl1.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl1);
	        	}
	        	if(!newMemberCompanyName.equals(oldMemberCompanyName)){//工作单位
	        		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"memberCompanyName",oldMemberCompanyName,newMemberCompanyName,showValue);  
	        		empApplicationTbl2.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl2);
	        	}
	        	if(!newMemberMobile.equals(oldMemberMobile)){//电话
	        		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"memberMobile",oldMemberMobile,newMemberMobile,showValue);  
	        		empApplicationTbl3.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl3);
	        	}
	        	
	        }else{//子女
	        	
        	    newMemberName = newBean.getMemberName();
        	    newBirthday = DateUtils.format(newBean.getBirthday(),DateUtils.FORMAT_SHORT);
        	    newMemberSex = getChGender(newBean.getMemberSex());
        	    
        	    oldMemberName = oldBean.getMemberName();
        	    oldBirthday = DateUtils.format(oldBean.getBirthday(),DateUtils.FORMAT_SHORT);
        	    oldMemberSex = getChGender(oldBean.getMemberSex());
        		showValue = "子女信息 由 "+ oldMemberName+" "+oldBirthday + " "+oldMemberSex + " 修改为 "+
    				        newMemberName+" "+newBirthday + " "+newMemberSex ;
        	    
	        	if(!oldMemberName.equals(newMemberName)){//姓名
	        		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"memberName",oldMemberName,newMemberName,showValue); 
	        		empApplicationTbl4.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl4);
	        	}
	        	if(!oldBirthday.equals(newBirthday)){//生日
	        		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"birthday",oldBirthday,newBirthday,showValue); 
	        		empApplicationTbl5.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl5);
	        	}
	        	if(!oldMemberSex.equals(newMemberSex)){//
	        		EmpApplicationTbl empApplicationTbl6 = setEmployeeLog(type,module,"memberSex",oldMemberSex,newMemberSex,showValue); 
	        		empApplicationTbl6.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl6);
	        	}
	        }
        }
	        
        
        if(type.equals(3L)) {
        	//删除
        	if(oldBean.getRelation().intValue() == 0){//0:配偶
        		
    	    	oldMemberName = oldBean.getMemberName();
    	    	oldMemberCompanyName = oldBean.getMemberCompanyName();
    	    	oldMemberMobile = oldBean.getMemberMobile();
        		showValue = "配偶信息 "+oldMemberName+" "+oldMemberCompanyName + " "+oldMemberMobile;
        	//姓名
        		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"memberName",oldMemberName,"",showValue); 
        		empApplicationTbl1.setArrayValue(arrayValue);
        		list.add(empApplicationTbl1);
        		
        	//工作单位
        		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"memberCompanyName",oldMemberCompanyName,"",showValue); 
        		empApplicationTbl2.setArrayValue(arrayValue);
        		list.add(empApplicationTbl2);
        	
        	//电话
        		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"memberMobile",oldMemberMobile,"",showValue); 
        		empApplicationTbl3.setArrayValue(arrayValue);
        		list.add(empApplicationTbl3);
        	
	        }else{//子女
	    	    
	        	    oldMemberName = oldBean.getMemberName();
	        	    oldBirthday = DateUtils.format(oldBean.getBirthday(),DateUtils.FORMAT_SHORT);
	        	    oldMemberSex = getChGender(oldBean.getMemberSex());
	        		showValue = "子女信息 "+oldMemberName+" "+oldBirthday + " "+oldMemberSex;
	        	//姓名
	        		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"memberName",oldMemberName,"",showValue); 
	        		empApplicationTbl4.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl4);
	        	
	        	//生日
	        		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"birthday",oldBirthday,"",showValue); 
	        		empApplicationTbl5.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl5);
	        	
	        	//电话
	        		EmpApplicationTbl empApplicationTbl6 = setEmployeeLog(type,module,"memberSex",oldMemberSex,"",showValue); 
	        		empApplicationTbl6.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl6);
	        }
        	
        }
		return list;
	}

	//明细紧急联系人信息List对比
	private List<EmpApplicationTbl> contrastUrgentList(List<EmpUrgentContact> oldList, List<EmpUrgentContact> newList,String module) {
		
        List<EmpApplicationTbl> list= new ArrayList<EmpApplicationTbl>();
        
        List<EmpUrgentContact> deleteIdList = new ArrayList<EmpUrgentContact>();//存放删除的数据集
        if(null != oldList){deleteIdList.addAll(oldList);}
        
        List<EmpUrgentContact> addIdList = new ArrayList<EmpUrgentContact>();//存放新增的数据集
        if(null != newList){addIdList.addAll(newList);}
        
        if(null != oldList && null != newList) {
        	for(EmpUrgentContact newEmpUrgentContact:newList){
            	Long newId = newEmpUrgentContact.getId();
                for(EmpUrgentContact oldEmpUrgentContact:oldList){

                	Long oldId = oldEmpUrgentContact.getId();
                	if(oldId.equals(newId)){//有相等的id，说明是修改,从删除列表和新增列表移除该项记录
                		
                		addIdList.remove(newEmpUrgentContact);
                		deleteIdList.remove(oldEmpUrgentContact);
                		
                		//参数解释 ：依次为旧对象，新对象，记录模块，操作类型(1-新增、2-修改，3-删除)
                		List<EmpApplicationTbl> updateList = contrastUrgentInfo(oldEmpUrgentContact,newEmpUrgentContact,module,2L,oldId);
                		list.addAll(updateList);//把修改的数据封装进去
                	}
                }
            	
            }
        }
    	//这里单独处理新增和删除的数据
        if(!deleteIdList.isEmpty()){//处理删除的数据，这里可以考虑下，怎么记录整条删除的日志
        	 for(EmpUrgentContact empUrgentContact:deleteIdList){
        		List<EmpApplicationTbl> deleteList = contrastUrgentInfo(empUrgentContact,null,module,3L,empUrgentContact.getId());
         		list.addAll(deleteList);//把修改的数据封装进去
        	 }
        }
        
        if(!addIdList.isEmpty()){//处理新增的数据，这里可以考虑下，怎么记录整条新增除的日志
        	Long arrayValue = -1L;
       	 for(EmpUrgentContact empUrgentContact:addIdList){
       		    empUrgentContact.setEmployeeId(CURRENT_EMPLOYEE_ID);
       	    	List<EmpApplicationTbl> deleteList = contrastUrgentInfo(null,empUrgentContact,module,1L,arrayValue);
        		list.addAll(deleteList);//把修改的数据封装进去
        		arrayValue--;
       	 }	
        }
		
        return list;
	}
	

	

	
	private List<EmpApplicationTbl> contrastUrgentInfo(EmpUrgentContact oldBean,
			EmpUrgentContact newBean, String module, Long type,Long arrayValue) {
        List<EmpApplicationTbl> list= new ArrayList<EmpApplicationTbl>();
        
        String oldPriority,newPriority;
        String oldShortName,newShortName;
        String oldName,newName;
        String oldMobile,newMobile;
        String oldTelphone,newTelphone;
        String showValue;
        
        if(type.equals(1L))//新增
	        {
        	   newPriority = newBean.getPriority().toString();
        	   newShortName = newBean.getShortName();
        	   newName = newBean.getShortName();
        	   newMobile = newBean.getMobile();
        	   newTelphone = newBean.getTelphone();
        	   showValue = "紧急联系人信息 "+newPriority+" "+newShortName+" "+newName+" "+newMobile+" "+newTelphone;
        	   
	        	//联系人级别
	    		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"priority","",newPriority,showValue); 
        		empApplicationTbl1.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl1);
	    		
	    		//称谓
	    		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"shortName","",newShortName,showValue); 
        		empApplicationTbl2.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl2);
	    		
	        	//联系人名称
	    		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"name","",newName,showValue); 
        		empApplicationTbl3.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl3);
	    		
	    		//手机
	    		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"mobile","",newMobile,showValue); 
        		empApplicationTbl4.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl4);
	    		
	    		//座机
	    		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"telphone","",newTelphone,showValue); 
        		empApplicationTbl5.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl5);
	        	
	        }	
        
        if(type.equals(2L))//修改
	        {
		     	   newPriority = newBean.getPriority().toString();
		     	   newShortName = newBean.getShortName();
		     	   newName = newBean.getShortName();
		     	   newMobile = newBean.getMobile();
		     	   newTelphone = newBean.getTelphone();
		     	   
		     	   oldPriority = oldBean.getPriority().toString();
		     	   oldShortName = oldBean.getShortName();
		     	   oldName = oldBean.getShortName();
		           oldMobile = oldBean.getMobile();
		     	   oldTelphone = oldBean.getTelphone();
		     	   
		     	   
		     	   showValue = "紧急联系人信息  由"+newPriority+" "+newShortName+" "+newName+" "+newMobile+" "+newTelphone+" 修改为 "+
		     			        oldPriority+" "+oldShortName+" "+oldName+" "+oldMobile+" "+oldTelphone ;
     	   
	        	if(!oldPriority.equals(newPriority)){//级别
	        		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"priority",oldPriority,newPriority,showValue); 
	        		empApplicationTbl1.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl1);
	        	}
		    	if(!oldShortName.equals(newShortName)){//称谓
		    		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"shortName",oldShortName,newShortName,showValue); 
	        		empApplicationTbl2.setArrayValue(arrayValue);
		    		list.add(empApplicationTbl2);
		    	}
		    	if(!oldName.equals(newName)){//姓名
		    		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"name",oldName,newName,showValue); 
	        		empApplicationTbl3.setArrayValue(arrayValue);
		    		list.add(empApplicationTbl3);
		    	}
		    	if(!oldMobile.equals(newMobile)){//手机
		    		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"mobile",oldMobile,newMobile,showValue); 
	        		empApplicationTbl4.setArrayValue(arrayValue);
		    		list.add(empApplicationTbl4);
		    	}
		    	if(!oldTelphone.equals(newTelphone)){//电话
		    		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"telphone",oldTelphone,newTelphone,showValue); 
	        		empApplicationTbl5.setArrayValue(arrayValue);
		    		list.add(empApplicationTbl5);
		    	}
	        }
        
        if(type.equals(3L))//删除
        {
	     	   
	     	   oldPriority = oldBean.getPriority().toString();
	     	   oldShortName = oldBean.getShortName();
	     	   oldName = oldBean.getShortName();
	           oldMobile = oldBean.getMobile();
	     	   oldTelphone = oldBean.getTelphone();
        	   showValue = "紧急联系人信息 "+oldPriority+" "+oldShortName+" "+oldName+" "+oldMobile+" "+oldTelphone;
    		
        	//联系人级别
    		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"priority",oldPriority,"",showValue); 
    		empApplicationTbl1.setArrayValue(arrayValue);
    		list.add(empApplicationTbl1);
    		
    		//称谓
    		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"shortName",oldShortName,"",showValue); 
    		empApplicationTbl2.setArrayValue(arrayValue);
    		list.add(empApplicationTbl2);
    		
        	//联系人名称
    		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"name",oldName,"",showValue); 
    		empApplicationTbl3.setArrayValue(arrayValue);
    		list.add(empApplicationTbl3);
    		
    		//手机
    		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"mobile",oldMobile,"",showValue); 
    		empApplicationTbl4.setArrayValue(arrayValue);
    		list.add(empApplicationTbl4);
    		
    		//座机
    		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"telphone",oldTelphone,"",showValue); 
    		empApplicationTbl5.setArrayValue(arrayValue);
    		list.add(empApplicationTbl5);
        	
        }
		return list;
	}

	//明细教育经历信息List对比
	private List<EmpApplicationTbl> contrastSchoolList(List<EmpSchool> oldList, List<EmpSchool> newList,String module) {
		
        List<EmpApplicationTbl> list= new ArrayList<EmpApplicationTbl>();
        
        List<EmpSchool> deleteIdList = new ArrayList<EmpSchool>();//存放删除的数据集
        if(null != oldList){deleteIdList.addAll(oldList);}
        
        List<EmpSchool> addIdList = new ArrayList<EmpSchool>();//存放新增的数据集
        if(null != newList){addIdList.addAll(newList);}
        
        if(null != newList && null != oldList) {
        	for(EmpSchool newEmpSchool:newList){
            	Long newId = newEmpSchool.getId();
                for(EmpSchool oldEmpSchool:oldList){

                	Long oldId = oldEmpSchool.getId();
                	if(oldId.equals(newId)){//有相等的id，说明是修改,从删除列表和新增列表移除该项记录
                		
                		addIdList.remove(newEmpSchool);
                		deleteIdList.remove(oldEmpSchool);
                		
                		//参数解释 ：依次为旧对象，新对象，记录模块，操作类型(1-新增、2-修改，3-删除)
                		List<EmpApplicationTbl> updateList = contrastSchoolInfo(oldEmpSchool,newEmpSchool,module,2L,oldId);
                		list.addAll(updateList);//把修改的数据封装进去
                	}
                }
            	
            }
        }
        
    	//这里单独处理新增和删除的数据
        if(!deleteIdList.isEmpty()){//处理删除的数据，这里可以考虑下，怎么记录整条删除的日志
        	 for(EmpSchool empSchool:deleteIdList){
        		List<EmpApplicationTbl> deleteList = contrastSchoolInfo(empSchool,null,module,3L,empSchool.getId());
         		list.addAll(deleteList);//把修改的数据封装进去
        	 }
        }
        
        if(!addIdList.isEmpty()){//处理新增的数据，这里可以考虑下，怎么记录整条新增除的日志
        	Long arrayValue = -1L;
       	 for(EmpSchool empSchool:addIdList){
       		    empSchool.setEmployeeId(CURRENT_EMPLOYEE_ID);
       	    	List<EmpApplicationTbl> deleteList = contrastSchoolInfo(null,empSchool,module,1L,arrayValue);
        		list.addAll(deleteList);//把修改的数据封装进去
        		arrayValue--;
       	 }	
        }
		
        
        return list;
	}
	
	private List<EmpApplicationTbl> contrastSchoolInfo(EmpSchool oldBean,
			EmpSchool newBean, String module, Long type,Long arrayValue) {
        List<EmpApplicationTbl> list= new ArrayList<EmpApplicationTbl>();
        
        String oldStartTime,newStartTime;
        String oldEndTime,newEndTime;
        String oldSchool,newSchool;
        String oldMajor,newMajor;
        String oldEducation,newEducation;//int型，需要转换
        String oldDegree,newDegree;
        String showValue;
        
        if(type.equals(1L))//新增
	        {
    	        newStartTime = DateUtils.format(newBean.getStartTime(),DateUtils.FORMAT_SHORT);
    	        newEndTime = DateUtils.format(newBean.getEndTime(),DateUtils.FORMAT_SHORT);
    	        newSchool = newBean.getSchool();
    	        newMajor = newBean.getMajor();
    	        newEducation = newBean.getEducation().toString();
    	        newDegree = String.valueOf(newBean.getDegree());
	        	showValue = "教育经历 "+newStartTime+" "+newEndTime+" "+newSchool+" "+newMajor+" "+newEducation+
	        			     " "+newDegree;
	    		
	          	//开始时间
	    		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"startTime","",newStartTime,showValue); 
	    		empApplicationTbl1.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl1);
	    		
	        	//结束时间
	    		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"endTime","",newEndTime,showValue); 
	    		empApplicationTbl2.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl2);
	    		
	        	//学校名
	    		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"school","",newSchool,showValue); 
	    		empApplicationTbl3.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl3);
	    		
	        	//专业
	    		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"major","",newMajor,showValue); 
	    		empApplicationTbl4.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl4);
	    		
	        	//学历
	    		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"education","",newEducation,showValue); 
	    		empApplicationTbl5.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl5);
	    		
	        	//学wei
	    		EmpApplicationTbl empApplicationTbl6 = setEmployeeLog(type,module,"degree","",newDegree,showValue); 
	    		empApplicationTbl6.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl6);
        	
	        }
        
        if(type.equals(2L))//新增
	        {
		        newStartTime = DateUtils.format(newBean.getStartTime(),DateUtils.FORMAT_SHORT);
		        newEndTime = DateUtils.format(newBean.getEndTime(),DateUtils.FORMAT_SHORT);
		        newSchool = newBean.getSchool();
		        newMajor = newBean.getMajor();
		        newEducation = newBean.getEducation().toString();
		        newDegree = newBean.getDegree().toString();
		        
		        oldStartTime = DateUtils.format(oldBean.getStartTime(),DateUtils.FORMAT_SHORT);
		        oldEndTime = DateUtils.format(oldBean.getEndTime(),DateUtils.FORMAT_SHORT);
		        oldSchool = oldBean.getSchool();
		        oldMajor = oldBean.getMajor();
		        oldEducation = oldBean.getEducation().toString();
		        oldDegree = oldBean.getDegree().toString();
		        
	        	showValue = "教育经历 由  "+oldStartTime+" "+oldEndTime+" "+oldSchool+" "+oldMajor+" "+oldEducation+
	       			        " "+oldDegree
		        	        +" 修改为 "+
	       			        newStartTime+" "+newEndTime+" "+newSchool+" "+newMajor+" "+newEducation+
	        			     " "+newDegree;
        	    //开始时间
	        	if(!oldStartTime.equals(newStartTime)){
	        		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"startTime",oldStartTime,newStartTime,showValue);
	        		empApplicationTbl1.setArrayValue(arrayValue); 
	        		list.add(empApplicationTbl1);
	        	}
        	    //结束时间
	        	if(!oldEndTime.equals(newEndTime)){
	        		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"endTime",oldEndTime,newEndTime,showValue); 
	        		empApplicationTbl2.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl2);
	        	}
        	    //学校
	        	if(!oldSchool.equals(newSchool)){
	        		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"school",oldSchool,newSchool,showValue); 
	        		empApplicationTbl3.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl3);
	        	}
        	    //专业
	        	if(!oldMajor.equals(newMajor)){
	        		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"major",oldMajor,newMajor,showValue); 
	        		empApplicationTbl4.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl4);
	        	}
        	    //学历
	        	if(!oldEducation.equals(newEducation)){
	        		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"education",oldEducation,newEducation,showValue);
	        		empApplicationTbl5.setArrayValue(arrayValue); 
	        		list.add(empApplicationTbl5);
	        	}
        	    //学位
	        	if(!oldDegree.equals(newDegree)){
	        		EmpApplicationTbl empApplicationTbl6 = setEmployeeLog(type,module,"degree",oldDegree,newDegree,showValue); 
	        		empApplicationTbl6.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl6);
	        	}
        	
	        }
        
        if(type.equals(3L))//删除
	        {
	        
		        oldStartTime = DateUtils.format(oldBean.getStartTime(),DateUtils.FORMAT_SHORT);
		        oldEndTime = DateUtils.format(oldBean.getEndTime(),DateUtils.FORMAT_SHORT);
		        oldSchool = oldBean.getSchool();
		        oldMajor = oldBean.getMajor();
		        oldEducation = oldBean.getEducation().toString();
		        oldDegree = oldBean.getDegree().toString();
	        	showValue = "教育经历 "+oldStartTime+" "+oldEndTime+" "+oldSchool+" "+oldMajor+" "+oldEducation+
       			     " "+oldDegree;
	        	
	        	//开始时间
	    		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"startTime",oldStartTime,"",showValue); 
	    		empApplicationTbl1.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl1);
	    		
	        	//结束时间
	    		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"endTime",oldEndTime,"",showValue); 
	    		empApplicationTbl2.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl2);
	    		
	        	//学校名
	    		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"school",oldSchool,"",showValue); 
	    		empApplicationTbl3.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl3);
	    		
	        	//专业
	    		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"major",oldMajor,"",showValue); 
	    		empApplicationTbl4.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl4);
	    		
	        	//学历
	    		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"education",oldEducation,"",showValue); 
	    		empApplicationTbl5.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl5);
	    		
	        	//学wei
	    		EmpApplicationTbl empApplicationTbl6 = setEmployeeLog(type,module,"degree",oldDegree,"",showValue); 
	    		empApplicationTbl6.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl6);
	        	
	        }
		return list;
	}

	//明细教育经历信息List对比
	private List<EmpApplicationTbl> contrastTrainingList(List<EmpTraining> oldList, List<EmpTraining> newList,String module) {
		
        List<EmpApplicationTbl> list= new ArrayList<EmpApplicationTbl>();
        
        List<EmpTraining> deleteIdList = new ArrayList<EmpTraining>();//存放删除的数据集
        if(null != oldList){deleteIdList.addAll(oldList);}
        
        List<EmpTraining> addIdList = new ArrayList<EmpTraining>();//存放新增的数据集
        if(null != newList){addIdList.addAll(newList);}
        
        if(null != newList && null != oldList) {
        	 for(EmpTraining newEmpTrainning:newList){
             	Long newId = newEmpTrainning.getId();
                 for(EmpTraining oldEmpTrainning:oldList){

                 	Long oldId = oldEmpTrainning.getId();
                 	if(oldId.equals(newId)){//有相等的id，说明是修改,从删除列表和新增列表移除该项记录
                 		
                 		addIdList.remove(newEmpTrainning);
                 		deleteIdList.remove(oldEmpTrainning);
                 		
                 		//参数解释 ：依次为旧对象，新对象，记录模块，操作类型(1-新增、2-修改，3-删除)
                 		List<EmpApplicationTbl> updateList = contrastTrainingInfo(oldEmpTrainning,newEmpTrainning,module,2L,oldId);
                 		list.addAll(updateList);//把修改的数据封装进去
                 	}
                 }
             	
             }
        }
       
    	//这里单独处理新增和删除的数据
        if(!deleteIdList.isEmpty()){//处理删除的数据，这里可以考虑下，怎么记录整条删除的日志
        	 for(EmpTraining empTrainning:deleteIdList){
         		
        		List<EmpApplicationTbl> deleteList = contrastTrainingInfo(empTrainning,null,module,3L,empTrainning.getId());
         		list.addAll(deleteList);//把修改的数据封装进去
        	 }
        }
        
        if(!addIdList.isEmpty()){//处理新增的数据，这里可以考虑下，怎么记录整条新增除的日志
        	Long arrayValue = -1L;
       	 for(EmpTraining empTrainning:addIdList){
       		    empTrainning.setEmployeeId(CURRENT_EMPLOYEE_ID);
       	    	List<EmpApplicationTbl> deleteList = contrastTrainingInfo(null,empTrainning,module,1L,arrayValue);
        		list.addAll(deleteList);//把修改的数据封装进去
        		arrayValue--;
       	 }	
        }
		
        
        return list;
		
	}
	
	private List<EmpApplicationTbl> contrastTrainingInfo(EmpTraining oldBean, EmpTraining newBean,
			String module, Long type,Long arrayValue) {
        List<EmpApplicationTbl> list= new ArrayList<EmpApplicationTbl>();
        
        String oldStartTime,newStartTime;
        String oldEndTime,newEndTime;
        String oldTrainingInstitutions,newTrainingInstitutions;
        String oldContent,newContent;
        String oldObtainCertificate,newObtainCertificate;
        String showValue;
        
        if(type.equals(1L))//新增
	        {
		        newStartTime = DateUtils.format(newBean.getStartTime(),DateUtils.FORMAT_SHORT);
		        newEndTime = DateUtils.format(newBean.getEndTime(),DateUtils.FORMAT_SHORT);
		        newTrainingInstitutions = newBean.getTrainingInstitutions();
		        newContent = newBean.getContent();
		        newObtainCertificate = newBean.getObtainCertificate();
	        	showValue = "培训经历 "+newStartTime+" "+newEndTime+" "+newTrainingInstitutions+" "+newContent+" "+newObtainCertificate;
	        	//开始时间
	    		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"startTime","",newStartTime,showValue); 
	    		empApplicationTbl1.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl1);
	    		
	        	//结束时间
	    		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"endTime","",newEndTime,showValue); 
	    		empApplicationTbl2.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl2);
	    		
	    		//培训结构
	    		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"trainingInstitutions","",newTrainingInstitutions,showValue); 
	    		empApplicationTbl3.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl3);
	    		
	    		//培训内容
	    		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"content","",newContent,showValue); 
	    		empApplicationTbl4.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl4);
	    		
	    		//获得证书
	    		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"obtainCertificate","",newObtainCertificate,showValue); 
	    		empApplicationTbl5.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl5);
        	
	        }
        
        if(type.equals(2L))//修改
	        {
		        newStartTime = DateUtils.format(newBean.getStartTime(),DateUtils.FORMAT_SHORT);
		        newEndTime = DateUtils.format(newBean.getEndTime(),DateUtils.FORMAT_SHORT);
		        newTrainingInstitutions = newBean.getTrainingInstitutions();
		        newContent = newBean.getContent();
		        newObtainCertificate = newBean.getObtainCertificate();
		        
		        oldStartTime = DateUtils.format(oldBean.getStartTime(),DateUtils.FORMAT_SHORT);
		        oldEndTime = DateUtils.format(oldBean.getEndTime(),DateUtils.FORMAT_SHORT);
		        oldTrainingInstitutions = oldBean.getTrainingInstitutions();
		        oldContent = oldBean.getContent();
		        oldObtainCertificate = oldBean.getObtainCertificate();
	        	showValue = "培训经历 由 "+oldStartTime+" "+oldEndTime+" "+oldTrainingInstitutions+" "+oldContent+" "+oldObtainCertificate
	        			+" 修改为 "
		                +newStartTime+" "+newEndTime+" "+newTrainingInstitutions+" "+newContent+" "+newObtainCertificate;
	        	
	    	    //开始时间
	        	if(!oldStartTime.equals(newStartTime)){
	        		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"startTime",oldStartTime,newStartTime,showValue); 
		    		empApplicationTbl1.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl1);
	        	}
	    	    //结束时间
	        	if(!oldEndTime.equals(newEndTime)){
	        		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"endTime",oldEndTime,newEndTime,showValue); 
		    		empApplicationTbl2.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl2);
	        	}
	    	    //培训结构
	        	if(!oldTrainingInstitutions.equals(newTrainingInstitutions)){
	        		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"trainingInstitutions",oldTrainingInstitutions,newTrainingInstitutions,showValue);
		    		empApplicationTbl3.setArrayValue(arrayValue); 
	        		list.add(empApplicationTbl3);
	        	}
	    	    //培训内容
	        	if(!oldContent.equals(newContent)){
	        		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"content",oldContent,newContent,showValue); 
		    		empApplicationTbl4.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl4);
	        	}
	    	    //获得证书
	        	if(!oldObtainCertificate.equals(newObtainCertificate)){
	        		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"obtainCertificate",oldObtainCertificate,newObtainCertificate,showValue); 
		    		empApplicationTbl5.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl5);
	        	}
        	
	        }
        
        if(type.equals(3L))//删除
	        {
	        
		        oldStartTime = DateUtils.format(oldBean.getStartTime(),DateUtils.FORMAT_SHORT);
		        oldEndTime = DateUtils.format(oldBean.getEndTime(),DateUtils.FORMAT_SHORT);
		        oldTrainingInstitutions = oldBean.getTrainingInstitutions();
		        oldContent = oldBean.getContent();
		        oldObtainCertificate = oldBean.getObtainCertificate();
	        	showValue = "培训经历 "+oldStartTime+" "+oldEndTime+" "+oldTrainingInstitutions+" "+oldContent+" "+oldObtainCertificate;
	        	//开始时间
	    		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"startTime",oldStartTime,"",showValue); 
	    		empApplicationTbl1.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl1);
	    		
	        	//结束时间
	    		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"endTime",oldEndTime,"",showValue); 
	    		empApplicationTbl2.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl2);
	    		
	    		//培训结构
	    		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"trainingInstitutions",oldTrainingInstitutions,"",showValue); 
	    		empApplicationTbl3.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl3);
	    		
	    		//培训内容
	    		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"content",oldContent,"",showValue); 
	    		empApplicationTbl4.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl4);
	    		
	    		//获得证书
	    		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"obtainCertificate",oldObtainCertificate,"",showValue); 
	    		empApplicationTbl5.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl5);
        	
	        }
		return list;
	}

	//明细工作经历信息List对比
	private List<EmpApplicationTbl> contrastWorkRecordList(List<EmpWorkRecord> oldList, List<EmpWorkRecord> newList,String module) {
		
        List<EmpApplicationTbl> list= new ArrayList<EmpApplicationTbl>();
        
        List<EmpWorkRecord> deleteIdList = new ArrayList<EmpWorkRecord>();//存放删除的数据集
        if(null != oldList){deleteIdList.addAll(oldList);}
        
        List<EmpWorkRecord> addIdList = new ArrayList<EmpWorkRecord>();//存放新增的数据集
        if(null != newList){addIdList.addAll(newList);}

        if(null != newList && null != oldList) {
        	for(EmpWorkRecord newEmpRecord:newList){
    	    	Long newId = newEmpRecord.getId();
    	        for(EmpWorkRecord oldEmpRecord:oldList){
    	
    	        	Long oldId = oldEmpRecord.getId();
    	        	if(oldId.equals(newId)){//有相等的id，说明是修改,从删除列表和新增列表移除该项记录
    	        		
    	        		addIdList.remove(newEmpRecord);
    	        		deleteIdList.remove(oldEmpRecord);

    	        		//参数解释 ：依次为旧对象，新对象，记录模块，操作类型(1-新增、2-修改，3-删除)
    	        		List<EmpApplicationTbl> updateList = contrastWorkRecordInfo(oldEmpRecord,newEmpRecord,module,2L,oldId);
    	        		list.addAll(updateList);//把修改的数据封装进去
    	        	}
    	        }
    	    	
    	    }
        }
	    
    	//这里单独处理新增和删除的数据
        if(!deleteIdList.isEmpty()){//处理删除的数据，这里可以考虑下，怎么记录整条删除的日志
        	 for(EmpWorkRecord empRecord:deleteIdList){

        		List<EmpApplicationTbl> deleteList = contrastWorkRecordInfo(empRecord,null,module,3L,empRecord.getId());
         		list.addAll(deleteList);//把修改的数据封装进去
        	 }
        }
        
        if(!addIdList.isEmpty()){//处理新增的数据，这里可以考虑下，怎么记录整条新增除的日志
        	Long arrayValue = -1L;
       	 for(EmpWorkRecord empRecord:addIdList){
       		    empRecord.setEmployeeId(CURRENT_EMPLOYEE_ID);

       	    	List<EmpApplicationTbl> deleteList = contrastWorkRecordInfo(null,empRecord,module,1L,arrayValue);
        		list.addAll(deleteList);//把修改的数据封装进去
        		arrayValue--;
       	 }	
        }
        
        return list;	
	}

	private List<EmpApplicationTbl> contrastWorkRecordInfo(EmpWorkRecord oldBean, EmpWorkRecord newBean,
			String module,Long type,Long arrayValue) {
        List<EmpApplicationTbl> list= new ArrayList<EmpApplicationTbl>();
        
        String oldStartTime,newStartTime;
        String oldEndTime,newEndTime;
        String oldCompanyName,newCompanyName;
        String oldPositionName,newPositionName;
        String oldPositiontitle,newPositiontitle;
        String oldPositionTask,newPositionTask;
        String showValue;
        
        if(type.equals(1L))//新增
	        {
		        newStartTime = DateUtils.format(newBean.getStartTime(),DateUtils.FORMAT_SHORT);
		        newEndTime = DateUtils.format(newBean.getEndTime(),DateUtils.FORMAT_SHORT);
		        newCompanyName = newBean.getCompanyName();
		        newPositionName = newBean.getPositionName();
		        newPositiontitle = newBean.getPositionTitle();
		        newPositionTask = newBean.getPositionTask();
	        	showValue = "工作经历 "+newStartTime+" "+newEndTime+" "+newCompanyName+" "+ 
		                    newPositionName+" "+newPositiontitle +" "+newPositionTask;
        	
	        	//开始时间
	    		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"startTime","",newStartTime,showValue); 
	    		empApplicationTbl1.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl1);
	    		
	        	//结束时间
	    		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"endTime","",newEndTime,showValue);
	    		empApplicationTbl2.setArrayValue(arrayValue); 
	    		list.add(empApplicationTbl2);
	    		
	    		//单位名称
	    		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"companyName","",newCompanyName,showValue); 
	    		empApplicationTbl3.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl3);
	    		
	    		//职位名称
	    		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"positionName","",newPositionName,showValue); 
	    		empApplicationTbl4.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl4);
	    		
	    		//职称
	    		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"positionTitle","",newPositiontitle,showValue); 
	    		empApplicationTbl5.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl5);
	    		
	    		//主办业务
	    		EmpApplicationTbl empApplicationTbl6 = setEmployeeLog(type,module,"positionTask","",newPositionTask,showValue); 
	    		empApplicationTbl6.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl6);
	        }
        
        if(type.equals(2L))//修改
	        {
		        newStartTime = DateUtils.format(newBean.getStartTime(),DateUtils.FORMAT_SHORT);
		        newEndTime = DateUtils.format(newBean.getEndTime(),DateUtils.FORMAT_SHORT);
		        newCompanyName = newBean.getCompanyName();
		        newPositionName = newBean.getPositionName();
		        newPositiontitle = newBean.getPositionTitle();
		        newPositionTask = newBean.getPositionTask();
		        
		        oldStartTime = DateUtils.format(oldBean.getStartTime(),DateUtils.FORMAT_SHORT);
		        oldEndTime = DateUtils.format(oldBean.getEndTime(),DateUtils.FORMAT_SHORT);
		        oldCompanyName = oldBean.getCompanyName();
		        oldPositionName = oldBean.getPositionName();
		        oldPositiontitle = oldBean.getPositionTitle();
		        oldPositionTask = oldBean.getPositionTask();
	        	showValue = "工作经历 由 "+oldStartTime+" "+oldEndTime+" "+oldCompanyName+" "+ 
		        			oldPositionName+" "+oldPositiontitle +" "+oldPositionTask+
			                " 修改为 "+
		        			newStartTime+" "+newEndTime+" "+newCompanyName+" "+ 
			                newPositionName+" "+newPositiontitle +" "+newPositionTask;
	    	    //开始时间
	        	if(!oldStartTime.equals(newStartTime)){
	        		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"startTime",oldStartTime,newStartTime,showValue);
		    		empApplicationTbl1.setArrayValue(arrayValue); 
	        		list.add(empApplicationTbl1);
	        	}
	    	    //结束时间
	        	if(!oldEndTime.equals(newEndTime)){
	        		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"endTime",oldEndTime,newEndTime,showValue); 
		    		empApplicationTbl2.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl2);
	        	}
	    	    //单位名称
	        	if(!oldCompanyName.equals(newCompanyName)){
	        		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"companyName",oldCompanyName,newCompanyName,showValue); 
		    		empApplicationTbl3.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl3);
	        	}
	    	    //职位名称
	        	if(!oldPositionName.equals(newPositionName)){
	        		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"positionName",oldPositionName,newPositionName,showValue); 
		    		empApplicationTbl4.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl4);
	        	}
	    	    //职称
	        	if(!oldPositiontitle.equals(newPositiontitle)){
	        		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"positionTitle",oldPositiontitle,newPositiontitle,showValue); 
		    		empApplicationTbl5.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl5);
	        	}
	    	    //主办业务
	        	if(!oldPositionTask.equals(newPositionTask)){
	        		EmpApplicationTbl empApplicationTbl6 = setEmployeeLog(type,module,"positionTask",oldPositionTask,newPositionTask,showValue); 
		    		empApplicationTbl6.setArrayValue(arrayValue);
	        		list.add(empApplicationTbl6);
	        	}
        	
	        }
        
        if(type.equals(3L))//删除
	        {
	        
		        oldStartTime = DateUtils.format(oldBean.getStartTime(),DateUtils.FORMAT_SHORT);
		        oldEndTime = DateUtils.format(oldBean.getEndTime(),DateUtils.FORMAT_SHORT);
		        oldCompanyName = oldBean.getCompanyName();
		        oldPositionName = oldBean.getPositionName();
		        oldPositiontitle = oldBean.getPositionTitle();
		        oldPositionTask = oldBean.getPositionTask();
	        	showValue = "工作经历  "+oldStartTime+" "+oldEndTime+" "+oldCompanyName+" "+ 
		        			oldPositionName+" "+oldPositiontitle +" "+oldPositionTask;
	        	//开始时间
	    		EmpApplicationTbl empApplicationTbl1 = setEmployeeLog(type,module,"startTime",oldStartTime,"",showValue); 
	    		empApplicationTbl1.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl1);
	    		
	        	//结束时间
	    		EmpApplicationTbl empApplicationTbl2 = setEmployeeLog(type,module,"endTime",oldEndTime,"",showValue); 
	    		empApplicationTbl2.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl2);
	    		
	    		//单位名称
	    		EmpApplicationTbl empApplicationTbl3 = setEmployeeLog(type,module,"companyName",oldCompanyName,"",showValue); 
	    		empApplicationTbl3.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl3);
	    		
	    		//职位名称
	    		EmpApplicationTbl empApplicationTbl4 = setEmployeeLog(type,module,"positionName",oldPositionName,"",showValue); 
	    		empApplicationTbl4.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl4);
	    		
	    		//职称
	    		EmpApplicationTbl empApplicationTbl5 = setEmployeeLog(type,module,"positionTitle",oldPositiontitle,"",showValue); 
	    		empApplicationTbl5.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl5);
	    		
	    		//主办业务
	    		EmpApplicationTbl empApplicationTbl6 = setEmployeeLog(type,module,"positionTask",oldPositionTask,"",showValue); 
	    		empApplicationTbl6.setArrayValue(arrayValue);
	    		list.add(empApplicationTbl6);
	        }
		return list;
	}

	private String getChGender(Integer memberSex){
		return memberSex==0?"男":"女";
	}
	
	private Integer getMemberSex(String chGender){
		return chGender.equals("男")?0:1;
	}

	@Override
	public Employee checkEmployeeMain(Employee employee) {
		
		CURRENT_EMPLOYEE_ID =  employee.getId();//资料主键
		
		Employee oldEmployee = employeeService.getEmpDetailByCondition(employee);//得到原始数据

		List<EmpApplication> list = getEmpApplicationList(CURRENT_EMPLOYEE_ID,"baseInfo");//得到修改过的数据
		if(null == list || list.isEmpty()){
			logger.info("当前用户{}没有提交申请修改基础信息",CURRENT_EMPLOYEE_ID);
			return oldEmployee;
		}
		
		String module;
		String moduleDetail;
		String newValue;
		for(EmpApplication empApplication:list){
			empApplication.getType();
			module = empApplication.getModule();
			moduleDetail = empApplication.getModuleDetail();
			empApplication.getOldValue();
			newValue = empApplication.getNewValue();
			
			if(module.equals("baseInfo")) {
				//主信息
				if(moduleDetail.equals("politicalStatus")){
					oldEmployee.setPoliticalStatus(Long.parseLong(newValue));
				}else if(moduleDetail.equals("uleAccount")){
					oldEmployee.setUleAccount(newValue);
				}else if(moduleDetail.equals("mobile")){
					oldEmployee.setMobile(newValue);
				}else if(moduleDetail.equals("householdRegiste")){
					oldEmployee.setHouseholdRegister(newValue);
				}else if(moduleDetail.equals("maritalStatus")){
					oldEmployee.setMaritalStatus(Long.parseLong(newValue));
				}else if(moduleDetail.equals("address")){
					oldEmployee.setAddress(newValue);
				}
			}
		}
		
		
		return oldEmployee;
	}

	@Override
	public List<EmpFamilyMember> checkEmployeeFamily(Employee employee) {
		
		CURRENT_EMPLOYEE_ID =  employee.getId();//资料主键
		
		EmpFamilyMember familyCondition = new EmpFamilyMember();//familyCondition.getRelation(),0:配偶，1：子女
		familyCondition.setEmployeeId(CURRENT_EMPLOYEE_ID);
		List<EmpFamilyMember> oldListFamilyMember = empFamilyMemberService.getListByCondition(familyCondition);
		
		List<EmpApplication> list = getEmpApplicationList(CURRENT_EMPLOYEE_ID,"spouse");//得到修改过的数据

		List<EmpApplication> listChild = getEmpApplicationList(CURRENT_EMPLOYEE_ID,"child");//得到修改过的数据
		list.addAll(listChild);
		
		Long type;
		String moduleDetail;
		String newValue;
		Long arrayValue = null;

		List<EmpApplication> addEmpFamilyMembers = new ArrayList<EmpApplication>();//把新增的数据存起来作特殊处理
		Integer addRows = 0;//记录新增了几条数据
		
		for(EmpApplication empApplication:list){
			type = empApplication.getType();
			moduleDetail = empApplication.getModuleDetail();
			newValue = empApplication.getNewValue();

			if(type.equals(1L)){
				if(null == arrayValue){
					addRows++;
				}else if(!arrayValue.equals(empApplication.getArrayValue())){
					addRows++;//新增记录条数+1
				}
				addEmpFamilyMembers.add(empApplication);
			}else if(type.equals(2L)){
				for(EmpFamilyMember changedFamilyMember:oldListFamilyMember){
					if(changedFamilyMember.getId().equals(empApplication.getArrayValue())){
						if(moduleDetail.equals("memberName")){
							changedFamilyMember.setMemberName(newValue);
						}else if(moduleDetail.equals("birthday")){
							changedFamilyMember.setBirthday(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("memberSex")){
							changedFamilyMember.setMemberSex(getMemberSex(newValue));
						}else if(moduleDetail.equals("memberCompanyName")){
							changedFamilyMember.setMemberCompanyName(newValue);
						}else if(moduleDetail.equals("memberMobile")){
							changedFamilyMember.setMemberMobile(newValue);
						}
					}
					
				}
			}else if(type.equals(3L)){
				Iterator<EmpFamilyMember> it = oldListFamilyMember.iterator();
				while(it.hasNext()){
				    EmpFamilyMember delEmpFamilyMember = it.next();
				    if(delEmpFamilyMember.getId().equals(empApplication.getArrayValue())){
				        it.remove();
				    }
				}
			}
			arrayValue = empApplication.getArrayValue();
		}
		
		if(!addEmpFamilyMembers.isEmpty()){
			for(Integer i=0;i<addRows;i++){
				EmpFamilyMember addEmpFamilyMember  = new EmpFamilyMember();
				Long addArrayValue = null;
				for(EmpApplication empApplication:addEmpFamilyMembers){
					if(null==addArrayValue || addArrayValue.equals(empApplication.getArrayValue())){
						type = empApplication.getType();
						moduleDetail = empApplication.getModuleDetail();
						newValue = empApplication.getNewValue();
						
						if(moduleDetail.equals("memberName")){
							addEmpFamilyMember.setMemberName(newValue);
						}else if(moduleDetail.equals("birthday")){
							addEmpFamilyMember.setBirthday(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("memberSex")){
							addEmpFamilyMember.setMemberSex(getMemberSex(newValue));
						}else if(moduleDetail.equals("memberCompanyName")){
							addEmpFamilyMember.setMemberCompanyName(newValue);
						}else if(moduleDetail.equals("memberMobile")){
							addEmpFamilyMember.setMemberMobile(newValue);
						}
						
						if(null == addEmpFamilyMember.getRelation()) {
							if(empApplication.getModule().equals("child")){
								addEmpFamilyMember.setRelation(1);
							}else{
								addEmpFamilyMember.setRelation(0);
							}
						}
						
					}else{
						continue;
					}
					
					addArrayValue = empApplication.getArrayValue();
				}
				oldListFamilyMember.add(addEmpFamilyMember);
			}
			
		}
		
		return oldListFamilyMember;
	}

	@Override
	public List<EmpUrgentContact> checkEmployeeUrgent(Employee employee) {
		
		CURRENT_EMPLOYEE_ID =  employee.getId();//资料主键
		EmpUrgentContact urgentCondition = new EmpUrgentContact();
		urgentCondition.setEmployeeId(CURRENT_EMPLOYEE_ID);
		List<EmpUrgentContact> oldEmpUrgentContacts = empUrgentContactService.getListByCondition(urgentCondition);
		
		List<EmpApplication> list = getEmpApplicationList(CURRENT_EMPLOYEE_ID,"urgentContact");
		
		Long type;
		String moduleDetail;
		String newValue;
		Long arrayValue = null;

		List<EmpApplication> addUrgentContacts = new ArrayList<EmpApplication>();//把新增的数据存起来作特殊处理
		Integer addRows = 0;//记录新增了几条数据
		
		for(EmpApplication empApplication:list){
			type = empApplication.getType();
			moduleDetail = empApplication.getModuleDetail();
			newValue = empApplication.getNewValue();

			if(type.equals(1L)){
				if(null == arrayValue){
					addRows++;
				}else if(!arrayValue.equals(empApplication.getArrayValue())){
					addRows++;//新增记录条数+1
				}
				addUrgentContacts.add(empApplication);
			}else if(type.equals(2L)){
				for(EmpUrgentContact changedUrgent:oldEmpUrgentContacts){
					if(changedUrgent.getId().equals(empApplication.getArrayValue())){
						if(moduleDetail.equals("priority")){
							changedUrgent.setPriority(Integer.parseInt(newValue));
						}else if(moduleDetail.equals("shortName")){
							changedUrgent.setShortName(newValue);
						}else if(moduleDetail.equals("name")){
							changedUrgent.setName(newValue);
						}else if(moduleDetail.equals("mobile")){
							changedUrgent.setMobile(newValue);
						}else if(moduleDetail.equals("telphone")){
							changedUrgent.setTelphone(newValue);
						}
					}
					
				}
			}else if(type.equals(3L)){
				Iterator<EmpUrgentContact> it = oldEmpUrgentContacts.iterator();
				while(it.hasNext()){
					EmpUrgentContact delEmpUrgentContact = it.next();
				    if(delEmpUrgentContact.getId().equals(empApplication.getArrayValue())){
				        it.remove();
				    }
				}
			}
			arrayValue = empApplication.getArrayValue();
		}
		
		if(!addUrgentContacts.isEmpty()){
			for(Integer i=0;i<addRows;i++){
				EmpUrgentContact addEmpUrgentContact  = new EmpUrgentContact();
				Long addArrayValue = null;
				for(EmpApplication empApplication:addUrgentContacts){
					if(null==addArrayValue || addArrayValue.equals(empApplication.getArrayValue())){
						type = empApplication.getType();
						moduleDetail = empApplication.getModuleDetail();
						newValue = empApplication.getNewValue();
						
						if(moduleDetail.equals("priority")){
							addEmpUrgentContact.setPriority(Integer.parseInt(newValue));
						}else if(moduleDetail.equals("shortName")){
							addEmpUrgentContact.setShortName(newValue);
						}else if(moduleDetail.equals("name")){
							addEmpUrgentContact.setName(newValue);
						}else if(moduleDetail.equals("mobile")){
							addEmpUrgentContact.setMobile(newValue);
						}else if(moduleDetail.equals("telphone")){
							addEmpUrgentContact.setTelphone(newValue);
						}
					}else{
						continue;
					}
					
					addArrayValue = empApplication.getArrayValue();
				}
				oldEmpUrgentContacts.add(addEmpUrgentContact);
			}
			
		}
		
		return oldEmpUrgentContacts;
	}

	@Override
	public List<EmpSchool> checkEmployeeSchool(Employee employee) {
		
		CURRENT_EMPLOYEE_ID =  employee.getId();//资料主键
		EmpSchool schoolCondition = new EmpSchool();
		schoolCondition.setEmployeeId(CURRENT_EMPLOYEE_ID);
		List<EmpSchool> oldEmpSchools = empSchoolService.getListByCondition(schoolCondition);
		
		List<EmpApplication> list = getEmpApplicationList(CURRENT_EMPLOYEE_ID,"school");
		
		Long type;
		String moduleDetail;
		String newValue;
		Long arrayValue = null;

		List<EmpApplication> addSchools = new ArrayList<EmpApplication>();//把新增的数据存起来作特殊处理
		Integer addRows = 0;//记录新增了几条数据
		
		for(EmpApplication empApplication:list){
			type = empApplication.getType();
			moduleDetail = empApplication.getModuleDetail();
			newValue = empApplication.getNewValue();

			if(type.equals(1L)){
				if(null == arrayValue){
					addRows++;
				}else if(!arrayValue.equals(empApplication.getArrayValue())){
					addRows++;//新增记录条数+1
				}
				addSchools.add(empApplication);
			}else if(type.equals(2L)){
				for(EmpSchool changedEmpSchool:oldEmpSchools){
					if(changedEmpSchool.getId().equals(empApplication.getArrayValue())){
						if(moduleDetail.equals("startTime")){
							changedEmpSchool.setStartTime(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("endTime")){
							changedEmpSchool.setEndTime(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("school")){
							changedEmpSchool.setSchool(newValue);
						}else if(moduleDetail.equals("major")){
							changedEmpSchool.setMajor(newValue);
						}else if(moduleDetail.equals("education")){
							changedEmpSchool.setEducation(Integer.parseInt(newValue));
						}else if(moduleDetail.equals("degree")){
							changedEmpSchool.setDegree(Integer.parseInt(newValue));
						}
					}
					
				}
			}else if(type.equals(3L)){
				Iterator<EmpSchool> it = oldEmpSchools.iterator();
				while(it.hasNext()){
					EmpSchool delEmpSchool = it.next();
				    if(delEmpSchool.getId().equals(empApplication.getArrayValue())){
				        it.remove();
				    }
				}
			}
			arrayValue = empApplication.getArrayValue();
		}
		
		if(!addSchools.isEmpty()){
			for(Integer i=0;i<addRows;i++){
				EmpSchool addEmpSchool  = new EmpSchool();
				Long addArrayValue = null;
				for(EmpApplication empApplication:addSchools){
					if(null==addArrayValue || addArrayValue.equals(empApplication.getArrayValue())){
						type = empApplication.getType();
						moduleDetail = empApplication.getModuleDetail();
						newValue = empApplication.getNewValue();
						
						if(moduleDetail.equals("startTime")){
							addEmpSchool.setStartTime(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("endTime")){
							addEmpSchool.setEndTime(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("school")){
							addEmpSchool.setSchool(newValue);
						}else if(moduleDetail.equals("major")){
							addEmpSchool.setMajor(newValue);
						}else if(moduleDetail.equals("education")){
							addEmpSchool.setEducation(Integer.parseInt(newValue));
						}else if(moduleDetail.equals("degree")){
							addEmpSchool.setDegree(Integer.parseInt(newValue));
						}
					}else{
						continue;
					}
					
					addArrayValue = empApplication.getArrayValue();
				}
				oldEmpSchools.add(addEmpSchool);
			}
			
		}
		
		return oldEmpSchools;
	}

	@Override
	public List<EmpTraining> checkEmployeeTraining(Employee employee) {
		
		CURRENT_EMPLOYEE_ID =  employee.getId();//资料主键
		EmpTraining empTrainingCondition = new EmpTraining();
		empTrainingCondition.setEmployeeId(CURRENT_EMPLOYEE_ID);
		List<EmpTraining> oldEmpTrainings = empTrainingService.getListByCondition(empTrainingCondition);
		
		List<EmpApplication> list = getEmpApplicationList(CURRENT_EMPLOYEE_ID,"training");
		
		Long type;
		String moduleDetail;
		String newValue;
		Long arrayValue = null;

		List<EmpApplication> addSchools = new ArrayList<EmpApplication>();//把新增的数据存起来作特殊处理
		Integer addRows = 0;//记录新增了几条数据
		
		for(EmpApplication empApplication:list){
			type = empApplication.getType();
			moduleDetail = empApplication.getModuleDetail();
			newValue = empApplication.getNewValue();

			if(type.equals(1L)){
				if(null == arrayValue){
					addRows++;
				}else if(!arrayValue.equals(empApplication.getArrayValue())){
					addRows++;//新增记录条数+1
				}
				addSchools.add(empApplication);
			}else if(type.equals(2L)){
				for(EmpTraining changedtraining:oldEmpTrainings){
					if(changedtraining.getId().equals(empApplication.getArrayValue())){
						if(moduleDetail.equals("startTime")){
							changedtraining.setStartTime(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("endTime")){
							changedtraining.setEndTime(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("trainingInstitutions")){
							changedtraining.setTrainingInstitutions(newValue);
						}else if(moduleDetail.equals("content")){
							changedtraining.setContent(newValue);
						}else if(moduleDetail.equals("obtainCertificate")){
							changedtraining.setObtainCertificate(newValue);
						}
					}
					
				}
			}else if(type.equals(3L)){
				Iterator<EmpTraining> it = oldEmpTrainings.iterator();
				while(it.hasNext()){
					EmpTraining delEmpTraining = it.next();
				    if(delEmpTraining.getId().equals(empApplication.getArrayValue())){
				        it.remove();
				    }
				}
			}
			arrayValue = empApplication.getArrayValue();
		}
		
		if(!addSchools.isEmpty()){
			for(Integer i=0;i<addRows;i++){
				EmpTraining addEmpTraining  = new EmpTraining();
				Long addArrayValue = null;
				for(EmpApplication empApplication:addSchools){
					if(null==addArrayValue || addArrayValue.equals(empApplication.getArrayValue())){
						type = empApplication.getType();
						moduleDetail = empApplication.getModuleDetail();
						newValue = empApplication.getNewValue();
						
						if(moduleDetail.equals("startTime")){
							addEmpTraining.setStartTime(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("endTime")){
							addEmpTraining.setEndTime(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("trainingInstitutions")){
							addEmpTraining.setTrainingInstitutions(newValue);
						}else if(moduleDetail.equals("content")){
							addEmpTraining.setContent(newValue);
						}else if(moduleDetail.equals("obtainCertificate")){
							addEmpTraining.setObtainCertificate(newValue);
						}
					}else{
						continue;
					}
					
					addArrayValue = empApplication.getArrayValue();
				}
				oldEmpTrainings.add(addEmpTraining);
			}
			
		}
		
		return oldEmpTrainings;
	}

	@Override
	public List<EmpWorkRecord> checkEmployeeWork(Employee employee) {
		
		CURRENT_EMPLOYEE_ID =  employee.getId();//资料主键
		EmpWorkRecord empWorkRecordCondition = new EmpWorkRecord();
		empWorkRecordCondition.setEmployeeId(CURRENT_EMPLOYEE_ID);
		List<EmpWorkRecord> oldEmpWorkRecords = empWorkRecordService.getListByCondition(empWorkRecordCondition);
		
		List<EmpApplication> list = getEmpApplicationList(CURRENT_EMPLOYEE_ID,"workRecord");
		
		Long type;
		String moduleDetail;
		String newValue;
		Long arrayValue = null;

		List<EmpApplication> addWorkRecords = new ArrayList<EmpApplication>();//把新增的数据存起来作特殊处理
		Integer addRows = 0;//记录新增了几条数据
		
		for(EmpApplication empApplication:list){
			type = empApplication.getType();
			moduleDetail = empApplication.getModuleDetail();
			newValue = empApplication.getNewValue();

			if(type.equals(1L)){
				if(null == arrayValue){
					addRows++;
				}else if(!arrayValue.equals(empApplication.getArrayValue())){
					addRows++;//新增记录条数+1
				}
				
				addWorkRecords.add(empApplication);
			}else if(type.equals(2L)){
				for(EmpWorkRecord changedWorkRecord:oldEmpWorkRecords){
					if(changedWorkRecord.getId().equals(empApplication.getArrayValue())){
						if(moduleDetail.equals("startTime")){
							changedWorkRecord.setStartTime(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("endTime")){
							changedWorkRecord.setEndTime(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("companyName")){
							changedWorkRecord.setCompanyName(newValue);
						}else if(moduleDetail.equals("positionName")){
							changedWorkRecord.setPositionName(newValue);
						}else if(moduleDetail.equals("positionTitle")){
							changedWorkRecord.setPositionTitle(newValue);
						}else if(moduleDetail.equals("positionTask")){
							changedWorkRecord.setPositionTask(newValue);
						}
					}
					
				}
			}else if(type.equals(3L)){
				Iterator<EmpWorkRecord> it = oldEmpWorkRecords.iterator();
				while(it.hasNext()){
					EmpWorkRecord delEmpWorkRecord = it.next();
				    if(delEmpWorkRecord.getId().equals(empApplication.getArrayValue())){
				        it.remove();
				    }
				}
			}
			arrayValue = empApplication.getArrayValue();
		}
		
		if(!addWorkRecords.isEmpty()){
			for(Integer i=0;i<addRows;i++){
				EmpWorkRecord addEmpWorkRecord  = new EmpWorkRecord();
				Long addArrayValue = null;
				for(EmpApplication empApplication:addWorkRecords){
					if(null==addArrayValue || addArrayValue.equals(empApplication.getArrayValue())){
						type = empApplication.getType();
						moduleDetail = empApplication.getModuleDetail();
						newValue = empApplication.getNewValue();
						
						if(moduleDetail.equals("startTime")){
							addEmpWorkRecord.setStartTime(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("endTime")){
							addEmpWorkRecord.setEndTime(DateUtils.parse(newValue,DateUtils.FORMAT_SHORT));
						}else if(moduleDetail.equals("companyName")){
							addEmpWorkRecord.setCompanyName(newValue);
						}else if(moduleDetail.equals("positionName")){
							addEmpWorkRecord.setPositionName(newValue);
						}else if(moduleDetail.equals("positionTitle")){
							addEmpWorkRecord.setPositionTitle(newValue);
						}else if(moduleDetail.equals("positionTask")){
							addEmpWorkRecord.setPositionTask(newValue);
						}
					}else{
						continue;
					}
					
					addArrayValue = empApplication.getArrayValue();
				}
				oldEmpWorkRecords.add(addEmpWorkRecord);
			}
			
		}
		
		return oldEmpWorkRecords;
	}
	
	private List<EmpApplication> getEmpApplicationList(Long id,String module){
		
		EmpApplication condition = new EmpApplication();
		condition.setEmployeeId(id);
		condition.setModule(module);
		condition.setApprovalStatus(100L);
		List<EmpApplication> list = empApplicationMapper.selectByCondition(condition);//得到修改过的数据
		return list;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void pass(Employee employee) throws Exception {
		
		CURRENT_EMPLOYEE_ID =  employee.getId();//资料主键
		
		User user = userService.getCurrentUser();
		CURRENT_USER_NAME = user.getUserName();
		
		//1.保存主信息，更新
		employeeService.updateById(employee);
		
		//2.保存家庭成员信息,删除，更新，添加
		List<EmpFamilyMember> empFamilyMembers = employee.getEmpFamilyMembers();
		if(null == empFamilyMembers) {
			empFamilyMembers = new ArrayList<EmpFamilyMember>();
		}
		
		empFamilyMemberService.deleteBatchNotApply(empFamilyMembers,CURRENT_EMPLOYEE_ID,CURRENT_USER_NAME,CURRENT_TIME);
		Iterator<EmpFamilyMember> itFamily = empFamilyMembers.iterator();
		while(itFamily.hasNext()){
		    EmpFamilyMember empFamilyMember = itFamily.next();
			if(null == empFamilyMember.getId()){
				empFamilyMember.setEmployeeId(CURRENT_EMPLOYEE_ID);
				empFamilyMember.setCreateUser(CURRENT_USER_NAME);
				empFamilyMember.setCreateTime(CURRENT_TIME);
				empFamilyMember.setUpdateUser(CURRENT_USER_NAME);
				empFamilyMember.setUpdateTime(CURRENT_TIME);
				empFamilyMember.setDelFlag(0);
				
			}else{
				empFamilyMember.setUpdateUser(CURRENT_USER_NAME);
				empFamilyMember.setUpdateTime(CURRENT_TIME);
				empFamilyMemberService.updateById(empFamilyMember);
				itFamily.remove();
			}
		}
		
		empFamilyMemberService.saveBatch(empFamilyMembers);
		
		//3.保存联系人信息,删除，更新，添加
		List<EmpUrgentContact> empUrgentContacts = employee.getEmpUrgentContacts();
		if(null == empUrgentContacts) {
			empUrgentContacts = new ArrayList<EmpUrgentContact>();
		}
		
		empUrgentContactService.deleteBatchNotApply(empUrgentContacts,CURRENT_EMPLOYEE_ID,CURRENT_USER_NAME,CURRENT_TIME);
		Iterator<EmpUrgentContact> itUrgentContact = empUrgentContacts.iterator();
		while(itUrgentContact.hasNext()){
			EmpUrgentContact empUrgentContact = itUrgentContact.next();
			if(null == empUrgentContact.getId()){
				empUrgentContact.setEmployeeId(CURRENT_EMPLOYEE_ID);
				empUrgentContact.setCreateUser(CURRENT_USER_NAME);
				empUrgentContact.setCreateTime(CURRENT_TIME);
				empUrgentContact.setUpdateUser(CURRENT_USER_NAME);
				empUrgentContact.setUpdateTime(CURRENT_TIME);
				empUrgentContact.setDelFlag(0);
			}else{
				empUrgentContact.setUpdateUser(CURRENT_USER_NAME);
				empUrgentContact.setUpdateTime(CURRENT_TIME);
				empUrgentContactService.updateById(empUrgentContact);
		        itUrgentContact.remove();
			}
		}
		
		empUrgentContactService.saveBatch(empUrgentContacts);
		
		//4.保存教育信息,删除，更新，添加
		List<EmpSchool> empSchools = employee.getEmpSchools();
		if(null == empSchools) {
			empSchools = new ArrayList<EmpSchool>();
		}
		
		empSchoolService.deleteBatchNotApply(empSchools,CURRENT_EMPLOYEE_ID,CURRENT_USER_NAME,CURRENT_TIME);
		Iterator<EmpSchool> itEmpSchool = empSchools.iterator();
		while(itEmpSchool.hasNext()){
			EmpSchool empSchool = itEmpSchool.next();
			if(null == empSchool.getId()){
				empSchool.setEmployeeId(CURRENT_EMPLOYEE_ID);
				empSchool.setCreateUser(CURRENT_USER_NAME);
				empSchool.setCreateTime(CURRENT_TIME);
				empSchool.setUpdateUser(CURRENT_USER_NAME);
				empSchool.setUpdateTime(CURRENT_TIME);
				empSchool.setDelFlag(0);
			}else{
				empSchool.setUpdateUser(CURRENT_USER_NAME);
				empSchool.setUpdateTime(CURRENT_TIME);
				empSchoolService.updateById(empSchool);
				itEmpSchool.remove();
			}
		}
		
		empSchoolService.saveBatch(empSchools);
		
		//5.保存培训信息,删除，更新，添加
		List<EmpTraining> empTrainings = employee.getEmpTrainings();
		if(null == empTrainings) {
			empTrainings = new ArrayList<EmpTraining>();
		}
		
		empTrainingService.deleteBatchNotApply(empTrainings,CURRENT_EMPLOYEE_ID,CURRENT_USER_NAME,CURRENT_TIME);
		Iterator<EmpTraining> itEmpTraining = empTrainings.iterator();
		while(itEmpTraining.hasNext()){
			EmpTraining empTraining = itEmpTraining.next();
			if(null == empTraining.getId()){
				empTraining.setEmployeeId(CURRENT_EMPLOYEE_ID);
				empTraining.setCreateUser(CURRENT_USER_NAME);
				empTraining.setCreateTime(CURRENT_TIME);
				empTraining.setUpdateUser(CURRENT_USER_NAME);
				empTraining.setUpdateTime(CURRENT_TIME);
				empTraining.setDelFlag(0);
			}else{
				empTraining.setUpdateUser(CURRENT_USER_NAME);
				empTraining.setUpdateTime(CURRENT_TIME);
				empTrainingService.updateById(empTraining);
				itEmpTraining.remove();
			}
		}
		
		empTrainingService.saveBatch(empTrainings);
		
		//6.保存工作经历信息,删除，更新，添加
		List<EmpWorkRecord> empWorkRecords = employee.getEmpWorkRecords();
		if(null == empWorkRecords) {
			empWorkRecords = new ArrayList<EmpWorkRecord>();
		}
		
		empWorkRecordService.deleteBatchNotApply(empWorkRecords,CURRENT_EMPLOYEE_ID,CURRENT_USER_NAME,CURRENT_TIME);
		Iterator<EmpWorkRecord> itEmpWorkRecord = empWorkRecords.iterator();
		while(itEmpWorkRecord.hasNext()){
			EmpWorkRecord empWorkRecord = itEmpWorkRecord.next();
			if(null == empWorkRecord.getId()){
				empWorkRecord.setEmployeeId(CURRENT_EMPLOYEE_ID);
				empWorkRecord.setCreateUser(CURRENT_USER_NAME);
				empWorkRecord.setCreateTime(CURRENT_TIME);
				empWorkRecord.setUpdateUser(CURRENT_USER_NAME);
				empWorkRecord.setUpdateTime(CURRENT_TIME);
				empWorkRecord.setDelFlag(0);
			}else{
				empWorkRecord.setUpdateUser(CURRENT_USER_NAME);
				empWorkRecord.setUpdateTime(CURRENT_TIME);
				empWorkRecordService.updateById(empWorkRecord);
				itEmpWorkRecord.remove();
			}
		}
		
		empWorkRecordService.saveBatch(empWorkRecords);
		
		//更新记录的日志状态
		Long approvalId = user.getId();//审核人id,获取当前用户id
		String approvalName = user.getUserName();
		EmpApplication empApplication = new EmpApplication();
		empApplication.setApprovalId(approvalId);
		empApplication.setApprovalName(approvalName);
		empApplication.setEmployeeId(CURRENT_EMPLOYEE_ID);
		empApplication.setApprovalStatus(200L);
		empApplication.setUpdateTime(CURRENT_TIME);
		empApplication.setUpdateUser(CURRENT_USER_NAME);
		empApplicationMapper.updateAppyLogStatus(empApplication);//状态200：审核通过
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void reject(Employee employee) {
		
		CURRENT_EMPLOYEE_ID =  employee.getId();//资料主键
		
		User user = userService.getCurrentUser();
		CURRENT_USER_NAME = user.getUserName();
		CURRENT_EMPLOYEE_ID =  employee.getId();
		//List<EmpApplication> list = getEmpApplicationList(CURRENT_EMPLOYEE_ID,null);
		
		//更新记录的日志状态
		Long approvalId = user.getId();//审核人id,获取当前用户id
		String approvalName = user.getUserName();
		EmpApplication empApplication = new EmpApplication();
		empApplication.setApprovalId(approvalId);
		empApplication.setApprovalName(approvalName);
		empApplication.setEmployeeId(CURRENT_EMPLOYEE_ID);
		empApplication.setApprovalStatus(300L);
		empApplication.setUpdateTime(CURRENT_TIME);
		empApplication.setUpdateUser(CURRENT_USER_NAME);
		empApplicationMapper.updateAppyLogStatus(empApplication);//状态300：审核驳回
	}
}
