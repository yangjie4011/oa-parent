package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.EmpHandoverWorkMapper;
import com.ule.oa.base.mapper.HrEmpCheckDetailMapper;
import com.ule.oa.base.mapper.HrEmpCheckListMapper;
import com.ule.oa.base.po.EmpHandoverWork;
import com.ule.oa.base.po.HrEmpCheckDetail;
import com.ule.oa.base.po.HrEmpCheckList;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.EmpHandoverWorkService;
import com.ule.oa.base.service.RunTaskService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.po.CommonPo;

/**
 * @ClassName: 离职工作交接
 * @Description: 离职工作交接
 * @author yangjie
 * @date 2017年5月31日
 */
@Service
public class EmpHandoverWorkServiceImpl implements EmpHandoverWorkService {
	
	@Autowired
	private EmpHandoverWorkMapper empHandoverWorkMapper;
	@Autowired
	private HrEmpCheckListMapper hrEmpCheckListMapper;
	@Autowired
	private HrEmpCheckDetailMapper hrEmpCheckDetailMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private RunTaskService runTaskService;

	@Override
	public void save(EmpHandoverWork empHandoverWork) {
		empHandoverWorkMapper.save(empHandoverWork);
	}

	@Override
	public int updateById(EmpHandoverWork empHandoverWork) {
		return empHandoverWorkMapper.updateById(empHandoverWork);
	}

	@Override
	public EmpHandoverWork getById(Long id) {
		return empHandoverWorkMapper.getById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchSave(List<EmpHandoverWork> list,Long ruProcdefId, Long entityId) {
		//runTaskService.nextFlow(ruProcdefId, userService.getCurrentUser(), "", entityId.toString());
		empHandoverWorkMapper.batchSave(list);
	}

	@Override
	public List<EmpHandoverWork> getListByCondition(
			EmpHandoverWork empHandoverWork) {
		return empHandoverWorkMapper.getListByCondition(empHandoverWork);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void confirm(List<EmpHandoverWork> list, Long ruProcdefId, Long entityId) {
		String empIds = "";
		for (EmpHandoverWork empHandoverWork : list) {
			if(empIds.length() > 0) {
				empIds = ",";
			}
			empIds = empIds + empHandoverWork.getReceiverId();
		}
		//runTaskService.nextFlow(ruProcdefId, userService.getCurrentUser(), "", entityId.toString());
		update(list, "");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(List<EmpHandoverWork> list,String updateUser) {
		for(EmpHandoverWork handover:list){
			//删除
			if(handover.getDelFlag().equals(CommonPo.STATUS_DELETE)){
				handover.setUpdateTime(new Date());
				handover.setUpdateUser(updateUser);
				empHandoverWorkMapper.delete(handover);
			}
			//修改
			if(handover.getId()!=null){
				handover.setUpdateTime(new Date());
				handover.setUpdateUser(updateUser);
				empHandoverWorkMapper.updateById(handover);
			}
			//新增
			if(handover.getId()==null){
				handover.setCreateTime(new Date());
				handover.setCreateUser(updateUser);
				empHandoverWorkMapper.save(handover);
			}
		}
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void confirmHandover(List<EmpHandoverWork> list) {
		for(EmpHandoverWork handover:list){
			empHandoverWorkMapper.updateById(handover);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void approve(List<EmpHandoverWork> list, String empResignId) {
		User user = userService.getCurrentUser();
		for(EmpHandoverWork handover:list){
			handover.setUpdateUser(user.getEmployee().getCnName());
			empHandoverWorkMapper.updateById(handover);
		}
		//生成离职手续办理清单
		List<HrEmpCheckDetail> checkDetailList = hrEmpCheckDetailMapper.getListByCondition(null);
		for(HrEmpCheckDetail detail:checkDetailList){
			HrEmpCheckList checkList = new HrEmpCheckList();
			checkList.setEmpResignId(Long.valueOf(empResignId));
			checkList.setDepartName(detail.getDepartName());
			checkList.setCheckItem(detail.getCheckItem());
			checkList.setDelFlag(CommonPo.STATUS_NORMAL);
			checkList.setCreateTime(new Date());
			checkList.setCreateUser(user.getEmployee().getCnName());
			hrEmpCheckListMapper.save(checkList);
		}
	}

}
