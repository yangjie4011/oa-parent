package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ule.oa.base.mapper.PositionMapper;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.DepartPosition;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.CompanyPositionLevelService;
import com.ule.oa.base.service.CompanyPositionSeqService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartPositionService;
import com.ule.oa.base.service.EmpPositionService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.po.CommonPo;
import com.ule.oa.common.utils.PageModel;

/**
  * @ClassName: PositionServiceImpl
  * @Description: 职位管理
  * @author minsheng
  * @date 2017年5月12日 上午10:57:43
 */
@Service
public class PositionServiceImpl implements PositionService {
	@Autowired
	private PositionMapper positionMapper;
	@Autowired
	private DepartPositionService departPositionService;
	@Autowired
	private UserService userService;
	@Autowired
	private CompanyPositionLevelService companyPositionLevelService;
	@Autowired
	private CompanyPositionSeqService companyPositionSeqService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmpPositionService empPositionService;
	@Autowired
	private ConfigService configService;
	
	public Position getById(Long id){
		return positionMapper.getById(id);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void savePosition(Position position) throws OaException{
		//校验职位唯一性
		Long isExist = checkExistsPositionName(position.getCompanyId(),position.getPositionName());
		if(isExist!=null){
			//职位已经存在，校验该职位是否在这个部门下面已经存在
			DepartPosition param = new DepartPosition();
			param.setDepartId(position.getDepartId());
			param.setPositionId(isExist);
			param.setCompanyId(position.getCompanyId());
			List<DepartPosition> departPositionList = departPositionService.getListByCondition(param);
			if(departPositionList!=null&&departPositionList.size()>0){
				throw new OaException("职位"+position.getPositionName()+"已经存在");
			}else{
				//保存部门和职位关系
				DepartPosition departPosition = new DepartPosition();
				departPosition.setCompanyId(position.getCompanyId());
				departPosition.setDepartId(position.getDepartId());
				departPosition.setPositionId(isExist);
				departPosition.setDelFlag(CommonPo.STATUS_NORMAL);
				departPosition.setCreateTime(new Date());
				departPosition.setCreateUser(userService.getCurrentAccount());
				departPositionService.save(departPosition);
			}
		}else{
			//保存职位基础信息
			position.setCreateTime(new Date());
			position.setCreateUser(userService.getCurrentAccount());
			position.setDelFlag(CommonPo.STATUS_NORMAL);
			save(position);
			
			//保存部门和职位关系
			DepartPosition departPosition = new DepartPosition();
			departPosition.setCompanyId(position.getCompanyId());
			departPosition.setDepartId(position.getDepartId());
			departPosition.setPositionId(position.getId());
			departPosition.setDelFlag(CommonPo.STATUS_NORMAL);
			departPosition.setCreateTime(new Date());
			departPosition.setCreateUser(userService.getCurrentAccount());
			departPositionService.save(departPosition);
		}
	}
	
	/**
	  * checkExistsPositionName(根据职位名称校验职位的唯一性)
	  * @Title: checkExistsPositionName
	  * @Description: 根据职位名称校验职位的唯一性
	  * @param name
	  * @return    设定文件
	  * boolean    true:存在，false：不存在
	  * @throws
	 */
	public Long checkExistsPositionName(Long companyId,String name){
		Position position = new Position();
		position.setCompanyId(companyId);
		position.setPositionName(name.trim());
		
		Position result = getByCondition(position);
		if(null != result){
			return result.getId();
		}
		
		return null;
	}
	
	/**
	 * @throws OaException 
	  * save(保存职位信息)
	  * @Title: save
	  * @Description: 保存职位信息
	  * @param record
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	@Override
	public int save(Position position) throws OaException{
		
		Position old = new Position();
		old.setCompanyId(position.getCompanyId());
		old.setPositionName(position.getPositionName());
		
		old = getByCondition(old); 
		if(null != old && old.getId() != null){
			throw new OaException("职位名称不能重复");
		}
		
		return positionMapper.save(position);
	}

	/**
	  * getListByCondition(根据条件查询职位信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件查询职位信息
	  * @param id
	  * @return    设定文件
	  * List<Position>    返回类型
	  * @throws
	 */
	@Override
	public List<Position> getListByCondition(Position position){
		return positionMapper.getListByCondition(position);
	}
	
	/**
	  * getByCondition(根据条件查询职位信息)
	  * @Title: getByCondition
	  * @Description: 根据条件查询职位信息
	  * @param id
	  * @return    设定文件
	  * List<Position>    返回类型
	  * @throws
	 */
	@Override
	public Position getByCondition(Position position){
		return positionMapper.getByCondition(position);
	}

	/**
	 * @throws Exception 
	  * updateById(根据主键更新职位信息)
	  * @Title: updateById
	  * @Description: 根据主键更新职位信息
	  * @param record
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	@Override
	public int updateById(Position position) throws Exception{
		int updateCount = positionMapper.updateById(position);
		
		if(updateCount == 0){
			throw new OaException("您当前修改的员工信息已经被其它人修改过，请重新编辑");
		}
		
		return updateCount;
	}
	
	/**
	  * getPageList(分页查询职位信息)
	  * @Title: getPageList
	  * @Description: 分页查询职位信息
	  * @param position
	  * @return    设定文件
	  * PageModel<Position>    返回类型
	  * @throws
	 */
	@Override
	public PageModel<Position> getPageList(Position position){
		int page = position.getPage() == null ? 0 : position.getPage();
		int rows = position.getRows() == null ? 0 : position.getRows();
		
		PageModel<Position> pm = new PageModel<Position>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = positionMapper.getCount(position);
		pm.setTotal(total);
		
		DepartPosition departPosition = new DepartPosition();
		departPosition.setCompanyId(position.getCompanyId());
		departPosition.setDepartId(position.getDepartId());
		departPosition.setOffset(pm.getOffset());
		departPosition.setLimit(pm.getLimit());
		
		List<Position> positions=positionMapper.getPageList(departPosition);
		pm.setRows(positions);
		return pm;
	}
	
	/**
	  * saveOrUpdate(保存或更新职位信息)
	  * @Title: saveOrUpdate
	  * @Description: 保存或更新职位信息
	  * @param position
	  * @return    设定文件
	  * Map<String,Object>    返回类型
	  * @throws
	 */
	@Override
	public int update(Position position) throws Exception{
		String newPostionName = position.getPositionName();
		Long newCompanyId = position.getCompanyId();
		Position oldPosition = getById(position.getId());
		
		//如果修改的职位名称和老的职位名称不一致的时候需要校验职位名称的唯一性
		if(null == oldPosition){
			throw new OaException("您当前修改的职位信息已经不存在!!!");
		}else if(!newPostionName.equals(oldPosition.getPositionName())){
			//校验职位唯一性
			Long positionId = checkExistsPositionName(newCompanyId,newPostionName);
			if(positionId!=null){
				throw new OaException("职位"+position.getPositionName()+"已经存在");
			}
		}
		
		oldPosition.setPositionName(position.getPositionName());
		oldPosition.setParentId(position.getParentId());
		oldPosition.setRemark(position.getRemark());
		oldPosition.setUpdateTime(new Date());
		oldPosition.setUpdateUser(userService.getCurrentAccount());
		oldPosition.setPositionLevelId(position.getPositionLevelId());
		oldPosition.setPositionSeqId(position.getPositionSeqId());
		
		return updateById(oldPosition);
		
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void delete(Position position) throws Exception{
		Position oldPosition = getById(position.getId());
		
		if(null != oldPosition){
			//1.判断职位下面是否存在人
			EmpPosition ep = new EmpPosition();
			ep.setPositionId(position.getId());
			List<EmpPosition> epList = empPositionService.getListByCondition(ep);
			
			//职位下面没有人
			if(null == epList || epList.size() == 0){
				oldPosition.setDelFlag(CommonPo.STATUS_DELETE);
				oldPosition.setUpdateTime(new Date());
				oldPosition.setUpdateUser(userService.getCurrentAccount());
				
				//2.删除部门和职位关系
				departPositionService.delByPositionId(oldPosition.getId());
				
				//3.删除职位
				updateById(oldPosition);
				
				return;
			}
			
			throw new OaException("职位下面还存在员工不能删除!!!");
		}
		
		throw new OaException("您当前修改的职位信息已经不存在!!!");
	}
	
	
	/**
	  * getTreeList(获得部门树)
	  * @Title: getTreeList
	  * @Description: 获得部门树
	  * @param position
	  * @return    设定文件
	  * List<Map<String, String>>    返回类型
	  * @throws
	 */
	public List<Map<String, String>> getTreeList(Position position){
		List<Position> listByCondition = positionMapper.getTreeList(position);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		for (Position pos : listByCondition) {
			map = new HashMap<String, String>();
			map.put("id", pos.getId() + "");
			map.put("pId", pos.getParentId() == null ? "0" : pos.getParentId() + "");
			map.put("companyId", pos.getCompanyId() == null ? "" : pos.getCompanyId() + "");
			map.put("name", pos.getPositionName());
			map.put("rank", pos.getRank() + "");
			list.add(map);
		}
		return list;
	}

	/**
	  * getCurrentPosition(获取当前登录用户对应的职位)
	  * @Title: getCurrentPosition
	  * @Description: 获取当前登录用户对应的职位
	  * @return
	  * @throws OaException    设定文件
	  * @see com.ule.oa.base.service.PositionService#getCurrentPosition()
	  * @throws
	 */
	public Position getCurrentPosition() throws OaException{
		User user = userService.getCurrentUser();
		
		if(null == user){
			throw new OaException("获取当前登录用户对应的职位信息为空，请重新登录");
		}
		
		return user.getPosition();
	}

	@Override
	public Position getByEmpId(Long employeeId) {
		return positionMapper.getByEmpId(employeeId);
	}

	@Override
	public List<String> getSeqList(String positionType) {
		List<Config> configList = configService.getListByCode("position_seq_"+positionType);
		List<String> seqList = new ArrayList<String>();
		for(Config data:configList){
			for(String seq:data.getDisplayCode().split(",")){
				seqList.add(seq);
			}
		}
		
		return seqList;
	}

	@Override
	public List<String> getLevelList(String positionType) {
		List<Config> configList = configService.getListByCode("position_level_"+positionType);
		List<String> levelList = new ArrayList<String>();
		for(Config data:configList){
			for(String seq:data.getDisplayCode().split(",")){
				levelList.add(seq);
			}
		}
		
		return levelList;
	}

	@Override
	public Map<String, List<String>> getPosSeqAndLv(Position position) {
		Map<String,List<String>> result = new HashMap<String,List<String>>();
		Position data = positionMapper.getByCondition(position);
		
		List<Config> configList = configService.getListByCode("position_seq_"+data.getCompanyPositionLevel().getCode());
		List<String> seqList = new ArrayList<String>();
		for(Config con:configList){
			for(String seq:con.getDisplayCode().split(",")){
				seqList.add(seq);
			}
		}
		
		List<Config> configList1 = configService.getListByCode("position_level_"+data.getCompanyPositionLevel().getCode());
		List<String> levelList = new ArrayList<String>();
		for(Config con:configList1){
			for(String seq:con.getDisplayCode().split(",")){
				levelList.add(seq);
			}
		}
		
		result.put("seqList", seqList);
		result.put("levelList", levelList);
		return result;
	}
	
}
