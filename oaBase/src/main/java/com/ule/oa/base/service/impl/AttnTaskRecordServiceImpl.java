package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.AttnTaskRecordMapper;
import com.ule.oa.base.po.AttnTaskRecord;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.service.AttnTaskRecordService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.MsgUtils;

@Service
public class AttnTaskRecordServiceImpl implements AttnTaskRecordService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private AttnTaskRecordMapper attnTaskRecordMapper;
	
	@Resource
	private EmployeeService employeeService;
	
	private Date CURRENT_TIME = null;
	private final Integer COMPLETE_STATUS = 0;//已完成
	private final Integer NOT_COMPLETE_STATUS = 1;//未完成
	private final String CREATE_USER = "oaService";

	@Override
	public int insert(AttnTaskRecord record) {
		return attnTaskRecordMapper.insert(record);
	}

	@Override
	public List<AttnTaskRecord> selectByCondition(AttnTaskRecord attnTaskRecord) {
		return attnTaskRecordMapper.selectByCondition(attnTaskRecord);
	}

	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
	public int updateById(AttnTaskRecord record) {
		return attnTaskRecordMapper.updateById(record);
	}

	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
	public int saveBatch(List<AttnTaskRecord> list) {
		return attnTaskRecordMapper.saveBatch(list);
	}
	
	@Override
	public Date getMaxDateOfTask() {
		return attnTaskRecordMapper.getMaxDateOfTask();
	}
	

	private int deleteByDate(Date attnDate, List<Long> ids) {
		return attnTaskRecordMapper.deleteByDate(attnDate,ids);
	}

	@Override
	public void setTaskRecord(AttnTaskRecord record) {

		logger.info("进入[创建考勤任务记录].......");
		final Date startTime = record.getStartTime();
		final Date endTime = record.getEndTime();
		final String employeeIds = record.getEmployeeIds();
		Date attnDate = null;// 获取统计的日期
		List<Long> ids = null;//获取统计的员工列表

		if(null != employeeIds && !employeeIds.isEmpty()){
			String[] idArray = employeeIds.split(",");
			ids = Arrays.asList(idArray).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList()); 
			
		}

		try {
			if (null != startTime) {//有开始时间，要添加该日期的任务,否则只发送消息
				
				attnDate = startTime;
				deleteByDate(attnDate,ids);//防止之前任务创建部分出错，再次创建导致的数据重复
				List<AttnTaskRecord> list = null;

				// 1.查询出所有员工,条件是在职的或者昨天离职日期是昨天的
				// 2.支持根据ID重算
				Employee employeeCondition = new Employee();
			    //---------------------------------------
				//employeeCondition.setId(1716L);//需删除
				employeeCondition.setJobStatus(0);// 0：在职，1：离职
				employeeCondition.setQuitTime(attnDate);// 统计的日期离职的
				employeeCondition.setLimit(500);
				employeeCondition.setIds(ids);

				// 分页，没回查询500条
				List<Employee> empList = new ArrayList<Employee>();
				int i = 0;
				while (i == 0 || !empList.isEmpty()) {// list能查到数据，继续往后查,直到查不到
					employeeCondition.setOffset(i * 500);
					empList = employeeService.getAttnEmpListByCondition(employeeCondition);
					list = buildTaskList(empList, attnDate);
					if (!list.isEmpty()) {
						attnTaskRecordMapper.saveBatch(list);
					}
					i++;
				}

			}
			try {
				String beginDate = null,endDate = null;
				if(null != startTime){
					beginDate=DateUtils.format(startTime, DateUtils.FORMAT_SHORT);
				}
				if(null != endTime){
					endDate=DateUtils.format(endTime, DateUtils.FORMAT_SHORT);
				}
				HashMap<String, Object> map = new HashMap<String, Object>();
				// kafka消息头，必须有时间范围，否则后续会重复消费数据
				map.put("beginDate", beginDate);// 可为null
				map.put("endDate", endDate);
				map.put("ids", ids);
				MsgUtils.syncSendKafkaMsgByMap(map,"com.ule.oa.service.signRecordToAttnWorkProducer");
				logger.info("发送kafka成功  com.ule.oa.service.signRecordToAttnWorkProducer");
			} catch (Exception e) {
				logger.error("发送kafka失败  com.ule.oa.service.signRecordToAttnWorkProducer");
			}
			
			logger.info("结束[创建考勤任务记录].......");
		} catch (Exception e) {
			logger.error("批量插入考勤任务表过程中出错，时间为"+DateUtils.format(attnDate)+".",e);
		}
	}
	
	private List<AttnTaskRecord> buildTaskList(List<Employee> empList,Date attnDate){
		
		 CURRENT_TIME = DateUtils.parse(DateUtils.getNow(),DateUtils.FORMAT_LONG);
		 
		 List<AttnTaskRecord> list = new ArrayList<AttnTaskRecord>();
		 Long employeeId = null;
		 if(!empList.isEmpty()){
			 for(Employee employee:empList){
				 employeeId = employee.getId();
				 AttnTaskRecord attnTaskRecord = new AttnTaskRecord();
				 attnTaskRecord.setEmployId(employeeId);
				 attnTaskRecord.setAttnDate(attnDate);
				 attnTaskRecord.setSetSignRecordStatus(COMPLETE_STATUS);
				 attnTaskRecord.setSetSignRecordTime(CURRENT_TIME);
				 attnTaskRecord.setSetWorkHoursStatus(NOT_COMPLETE_STATUS);
				 attnTaskRecord.setSetWorkHoursTime(CURRENT_TIME);
				 attnTaskRecord.setSetAttnStatisticsStatus(NOT_COMPLETE_STATUS);
				 attnTaskRecord.setSetAttnStatisticsTime(CURRENT_TIME);
				 attnTaskRecord.setCreateTime(CURRENT_TIME);
				 attnTaskRecord.setCreateUser(CREATE_USER);
				 attnTaskRecord.setUpdateTime(CURRENT_TIME);
				 attnTaskRecord.setUpdateUser(CREATE_USER);
				 list.add(attnTaskRecord);
			 }
		 }
		 return list;
	}

	@Override
	public List<Employee> selectEmpByAttnTask(AttnTaskRecord attnTaskRecordCondition) {
		return attnTaskRecordMapper.selectEmpByAttnTask(attnTaskRecordCondition);
	}

}
