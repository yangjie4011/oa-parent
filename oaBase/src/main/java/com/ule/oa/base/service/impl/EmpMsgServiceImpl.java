package com.ule.oa.base.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.EmpMsgMapper;
import com.ule.oa.base.po.EmpMsg;
import com.ule.oa.base.service.EmpMsgService;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.PageModel;

/**
 * @ClassName: 我的消息
 * @Description: 我的消息
 * @author yangjie
 * @date 2017年6月8日
 */
@Service
public class EmpMsgServiceImpl implements EmpMsgService {
	
	@Autowired
	private EmpMsgMapper empMsgMapper;

	@Override
	public int save(EmpMsg userMsg) {
		return empMsgMapper.save(userMsg);
	}

	@Override
	public int updateById(EmpMsg userMsg) {
		return empMsgMapper.updateById(userMsg);
	}

	@Override
	public List<EmpMsg> getListByCondition(EmpMsg userMsg) {
		return empMsgMapper.getListByCondition(userMsg);
	}

	@Override
	public PageModel<EmpMsg> getPageList(EmpMsg userMsg) {
		int page = userMsg.getPage() == null ? 0 : userMsg.getPage();
		int rows = userMsg.getRows() == null ? 0 : userMsg.getRows();
		
		PageModel<EmpMsg> pm = new PageModel<EmpMsg>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = empMsgMapper.getCount(userMsg);
		pm.setTotal(total);
		if(userMsg.getOffset() == null) {
			userMsg.setOffset(pm.getOffset());
		}
		if(userMsg.getLimit() == null) {
			userMsg.setLimit(pm.getOffset());
		}
		List<EmpMsg> userMsgs=empMsgMapper.getPageList(userMsg);
		
		pm.setRows(userMsgs);
		return pm;
	}

	@Override
	public PageModel<EmpMsg> getList(EmpMsg userMsg) {
		PageModel<EmpMsg> pm = new PageModel<EmpMsg>();
		List<EmpMsg> userMsgs=empMsgMapper.getPageList(userMsg);
		String createTime = "";
		//当前日期
		String currentDate = DateUtils.getNow(DateUtils.FORMAT_SHORT);
		//当前年份
		String currentYear = DateUtils.getYear(new Date());
		//昨天日期
		String yesterday = DateUtils.format(DateUtils.addDay(new Date(), -1), DateUtils.FORMAT_SHORT);
		if(userMsgs != null && userMsgs.size() > 0) {
			for (EmpMsg empMsg : userMsgs) {
				if(empMsg.getCreateTime() != null) {
					//判断是否是今年
					if(currentYear.equals(DateUtils.getYear(empMsg.getCreateTime()))) {
						//判断是否是今天,今天显示发送消息的具体时间H:M
						if(currentDate.equals(DateUtils.format(empMsg.getCreateTime(), DateUtils.FORMAT_SHORT))) {
							createTime = DateUtils.format(empMsg.getCreateTime(), "HH:mm");
						} else if(yesterday.equals(DateUtils.format(empMsg.getCreateTime(), DateUtils.FORMAT_SHORT))) { //判断是否是昨天,显示 昨天 H:M
							createTime = "昨天" + DateUtils.format(empMsg.getCreateTime(), "HH:mm");
						} else { //超过一天的，显示消息发送的日期，月-日
							createTime = DateUtils.format(empMsg.getCreateTime(), "M") + "月" + DateUtils.format(empMsg.getCreateTime(), "d") + "日";
						}
					} else {
						//超过当年的，显示年-月-日
						createTime = DateUtils.format(empMsg.getCreateTime(), DateUtils.FORMAT_SHORT);
					}
				} 
				empMsg.setCrTime(createTime);
			}
		}
		pm.setRows(userMsgs);
		return pm;
	}

	@Override
	public Integer getCount(EmpMsg userMsg) {
		return empMsgMapper.getCount(userMsg);
	}

}
