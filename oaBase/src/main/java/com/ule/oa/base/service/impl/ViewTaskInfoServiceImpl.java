package com.ule.oa.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.ViewTaskInfoMapper;
import com.ule.oa.base.po.tbl.ViewTaskInfoTbl;
import com.ule.oa.base.service.ViewTaskInfoService;
import com.ule.oa.common.utils.ConfigConstants;

import tk.mybatis.mapper.entity.Example;

@Service
public class ViewTaskInfoServiceImpl implements ViewTaskInfoService{
	@Autowired
	private ViewTaskInfoMapper viewTaskInfoMapper;
	
	/**
	 * 根据实例id,显示任务节点
	 */
	public List<ViewTaskInfoTbl> queryTasksByProcessId(String processId){

		Example example = new Example(ViewTaskInfoTbl.class);
		example.setOrderByClause("finish_time desc,statu desc");
		example.createCriteria().andEqualTo("processId", processId);
		ViewTaskInfoTbl record = new ViewTaskInfoTbl();
		record.setProcessId(processId);
		return viewTaskInfoMapper.selectByExample(example);
	}
	
	/**
	 * 保存节点数据显示
	 */
	public int save(ViewTaskInfoTbl viewTaskInfoTbl) {
		return viewTaskInfoMapper.insert(viewTaskInfoTbl);
	}

	@Override
	public ViewTaskInfoTbl getFirstAuditUser(String processId, String processKey,boolean flag) {
		if(processId==null||"".equals(processId)){
			return null;
		}
		Example example = new Example(ViewTaskInfoTbl.class);
		example.setOrderByClause("id");
		example.createCriteria().andEqualTo("processId", processId);
		List<ViewTaskInfoTbl> list = viewTaskInfoMapper.selectByExample(example);
		ViewTaskInfoTbl tbl = null;
		if(list!=null&&list.size()>1){
			tbl = list.get(1);
		}
		if(processKey.equals(ConfigConstants.LEAVE_KEY)){
			if(flag){
				tbl = null;
				if(list!=null&&list.size()==3){
					tbl = list.get(1);
				}
				if(list!=null&&list.size()==4){
					tbl = list.get(2);
				}
				if(list!=null&&list.size()==2&&list.get(1).getStatu().intValue()==ConfigConstants.REFUSE_STATUS.intValue()){
					tbl = list.get(1);
				}
			}
		}
		return tbl;
	}
		
	
	
	@Override
	public ViewTaskInfoTbl getAttnAuditUser(String processId, String processKey,boolean flag,int attnType) {
		if(processId==null||"".equals(processId)){
			return null;
		}
		Example example = new Example(ViewTaskInfoTbl.class);
		example.setOrderByClause("id");
		example.createCriteria().andEqualTo("processId", processId);
		List<ViewTaskInfoTbl> list = viewTaskInfoMapper.selectByExample(example);
		ViewTaskInfoTbl tbl = null;
		if(list!=null&&list.size()>1){
			tbl = list.get(1);	//获取 经理审批人
		}
		if(processKey.equals(ConfigConstants.ABNORMALATTENDANCE_KEY)){
			if(flag){
				tbl = null;
				if(attnType==1){//下属异常考勤  人事审批人
					if(list!=null&&list.size()==2){
						tbl = list.get(1);
					}
				}else{			//异常考勤	   人事审批人
					if(list!=null&&list.size()>2){
						tbl = list.get(2);
					}
				}
			}
		}
		return tbl;
	}

	@Override
	public int updateStatusById(ViewTaskInfoTbl viewTaskInfoTbl) {
		return viewTaskInfoMapper.updateStatusById(viewTaskInfoTbl);
	}

	@Override
	public List<ViewTaskInfoTbl> queryTasksByProcessIdList(
			List<String> processIdList) {
		return viewTaskInfoMapper.queryTasksByProcessIdList(processIdList);
	}

}
