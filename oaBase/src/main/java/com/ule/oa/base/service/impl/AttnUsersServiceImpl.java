package com.ule.oa.base.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ule.oa.base.mapper.AttnUsersMapper;
import com.ule.oa.base.po.AttnUsers;
import com.ule.oa.base.service.AttnUsersService;

@Service
public class AttnUsersServiceImpl implements AttnUsersService{

	@Resource
	private AttnUsersMapper attnUsersMapper;
	
	@Override
	public Integer save(AttnUsers attnUsers) {
		return attnUsersMapper.save(attnUsers);
	}

	@Override
	public Integer saveBatch(List<AttnUsers> list) {
		return attnUsersMapper.saveBatch(list);
	}

	@Override
	public AttnUsers selectByFingerprintId(Integer fingerprintId) {
		return attnUsersMapper.selectByFingerprintId(fingerprintId);
	}

	@Override
	public void updateById(AttnUsers attnUsers) {
		attnUsersMapper.updateById(attnUsers);
	}


	@Override
	public void bindFingerPrint(String fingerPrint, String empId) {
		attnUsersMapper.bindFingerPrint(Long.parseLong(fingerPrint),Long.parseLong(empId));
	}

}
