package com.ule.oa.base.mapper;

import java.util.List;
import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.util.jdbc.OaSqlMapper;

public interface SendMailMapper extends OaSqlMapper{
	
	public SendMail getById(Long id); 
	
	public int updateById(SendMail model);

	public int save(SendMail model);

	public List<SendMail> getListByCondition(SendMail model);
	
	void batchSave(List<SendMail> model);

}