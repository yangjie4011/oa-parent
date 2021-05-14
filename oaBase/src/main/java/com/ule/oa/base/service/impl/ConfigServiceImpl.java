package com.ule.oa.base.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.mapper.ConfigMapper;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.SmsService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.exception.SmsException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;

@Service
public class ConfigServiceImpl implements ConfigService{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private ConfigMapper configMapper;
	
	@Resource
	private SmsService smsService;
	
	@Resource
	private DataSource dataSource;
	
	@Resource
	private ConfigCacheManager configCacheManager;
	
	
	//---------员工导入----------------
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	@Autowired
	private DepartService departService;
	@Autowired
	private PositionService positionService;
	//--------------end-------------
	
	/**
	  * getListByCode(优先走缓存查询，查询不到则查DB)
	  * @Title: getListByCode
	  * @Description: 优先走缓存查询，查询不到则查DB
	  * @param code
	  * @return    设定文件
	  * @see com.ule.oa.base.service.ConfigService#getListByCode(java.lang.String)
	  * @throws
	 */
	public List<Config> getListByCode(String code){
		return configCacheManager.getListByCode(code);
	}
	
	/**
	  * updateById(这里用一句话描述这个方法的作用)
	  * @Title: updateById
	  * @Description: 根据主键修改配置信息
	  * @param config
	  * @return    设定文件
	  * int    返回类型
	  * @throws
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public int updateById(Config config) {
		return configMapper.updateById(config);
	}

	/**
	  * getListByCondition(根据条件获取所有配置信息)
	  * @Title: getListByCondition
	  * @Description: 根据条件获取所有配置信息
	  * @param config
	  * @return    设定文件
	  * List<Config>    返回类型
	  * @throws
	 */
	@Override
	public List<Config> getListByCondition(Config config) {
		List<Config> configList =  configMapper.getListByCondition(config);
		return configList;
	}
	
	/**
	  * getByCondition(根据查询条件查询配置信息)
	  * @Title: getByCondition
	  * @Description: 根据查询条件查询配置信息
	  * @param config
	  * @return    设定文件
	  * Config    返回类型
	  * @throws
	 */
	@Override
	public Config getByCondition(Config config){
		return getConfig(config);
	}
	
	public Config getConfig(Config config){
		List<Config> confList = getListByCondition(config);
		
		if(null != confList && confList.size() >0){
			if(StringUtils.isNotBlank(config.getDisplayCode())){
				for(Config conf : confList){
					if(StringUtils.isNotBlank(conf.getDisplayCode()) && conf.getDisplayCode().equals(config.getDisplayCode())){
						return conf;
					}
				}
			}
			
			return confList.get(0);
		}
		
		return null;
	}

	@Override
	public String getPhoneValidateCode(String phone,Integer second) throws OaException, SmsException {
		
		String template = "您的验证码为: {password}, 请于"+(second/60)+"分钟内及时使用【邮包裹后台】";
		String code = smsService.sendRandomCode(phone, template);
		
		return code;
	}

	//系统配置serviceimpl
	@Override
	public PageModel<Config> getByPagenation(Config Config) {
		int page = Config.getPage() == null ? 0 : Config.getPage();
		int rows = Config.getRows() == null ? 0 : Config.getRows();
		
		PageModel<Config> pm = new PageModel<Config>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = configMapper.getReportCount(Config);
		pm.setTotal(total);
		
		Config.setOffset(pm.getOffset());
		Config.setLimit(pm.getLimit());
		
		List<Config> roles = configMapper.getReportPageList(Config);
		pm.setRows(roles);
		return pm;
	}

	

	@Override
	public Config getConfigInfoById(Long id) {
		return configMapper.getConfigInfoById(id);
	}


	@Override
	public void saveConfig(Config config) {
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		config.setUpdateTime(updateTime);
		config.setCreateTime(updateTime);
		config.setDelFlag(0);
		config.setCreateUser("");
		config.setUpdateUser("");
		configMapper.saveConfig(config);
		
		//刷新缓存
		configCacheManager.reload();
	}

	@Override
	public List<Config> getConfiginfo() {
		return null;
	}

	@Override
	public void updateConfig(Config config) {
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		config.setUpdateTime(updateTime);
		configMapper.updateConfig(config);
		
		//刷新缓存
		configCacheManager.reload();
	}

	@Override
	public void deleteConfig(long id) {
		Config config = configMapper.getById(id);
		config.setDelFlag(1);
		Date updateTime = DateUtils.parse(DateUtils.getNow(DateUtils.FORMAT_LONG), DateUtils.FORMAT_LONG);
		config.setUpdateTime(updateTime);
		configMapper.deleteConfig(config);
		
		//刷新缓存
		configCacheManager.reload(); 
	}

	@Override
	@Transactional()
	public boolean inportEmpInfo(MultipartFile file) throws Exception{
		boolean flag = true;
		Map<String,Object> param = Maps.newHashMap();
		User currentUser = userService.getCurrentUser();
		String name = file.getOriginalFilename();  
	    //读取Excel数据到List中,员工模板已定义,则列数固定
	    int cloum = 18;//FIXME:列数不知道
	    List<List<Object>> list;
	    StringBuffer positionSql = new StringBuffer();
	    positionSql.append("insert into base_emp_position(employee_id,position_id,create_time,update_time,create_user,update_user,del_flag,version) values ");
	    StringBuffer departSql = new StringBuffer();
	    departSql.append("insert into base_emp_depart(employee_id,depart_id,create_time,update_time,create_user,update_user,del_flag,version) values ");
			list = ExcelUtil.readExcel(file, name, cloum);
			StringBuffer sb = new StringBuffer();
        //list中存的就是excel中的数据，可以根据excel中每一列的值转换成你所需要的值（从0开始），如：  
		    for(List<Object> unitLine: list){
		    	//首先通过员工编号进行查询,如果存在,则提示不做插入
		    	Employee emp = new Employee();
		    	User user = new User();
		    	emp.setCode((String)unitLine.get(2));
		    	Employee old = employeeService.getByCondition(emp);
		    	if(null != old) {
		    		sb.append("code为"+emp.getCode()+"已存在员工"+old.getCnName());
		    		continue;
		    	}
		    	String departName = (String)unitLine.get(8);
		    	Depart depart = (Depart)param.get(departName);
		    	if(depart==null) {
		    		depart = new Depart();
		    		depart.setName(departName);
		    		depart = departService.getByCondition(depart);
		    		if(depart == null) {
		    			sb.append("部门:"+departName+"不存在,请添加后再导入code为"+emp.getCode()+"员工信息");
		    			continue;
		    		}
		    		param.put(departName,depart);
		    	}
		    	String positionName = (String)unitLine.get(11);
		    	Position position = (Position)param.get(positionName);
		    	if(position==null) {
		    		position = new Position();
		    		position.setPositionName(positionName);
		    		position = positionService.getByCondition(position);
		    		if(position == null) {
		    			sb.append("职位:"+positionName+"不存在,请添加后再导入code:"+emp.getCode()+",name:"+unitLine.get(3)+"员工信息");
		    			continue;
		    		}
		    		param.put(departName,depart);
		    	}
		        //开始拼接员工信息
		    	//直接设置：员工编号,中文名,英文名,入职日期,工作邮箱
		    	emp.setCnName((String)unitLine.get(3));
		    	
		    	emp.setEngName((String)unitLine.get(4));
		    	emp.setFirstEntryTime((Date)unitLine.get(17));
		        //1.公司名称
		    	String companyName = (String)unitLine.get(0);
		    	Company paramCompany = (Company)param.get(companyName);
		    	if(paramCompany == null) {
		    		paramCompany = new Company();
		    		paramCompany.setName(companyName);
		    		paramCompany = companyService.getByCondition(paramCompany);
		    		param.put(companyName,paramCompany);
		    	}
		    	emp.setCompanyId(paramCompany.getId());
		    	user.setCompanyId(emp.getCompanyId());
		    	//2.员工类型
		    	String employeeType = (String)unitLine.get(1);
		    	CompanyConfig config =  (CompanyConfig)param.get(employeeType);
		    	if(config == null) {
		    		config = new CompanyConfig();
		    		config.setDisplayName(employeeType);
		    		config.setCompanyId(emp.getCompanyId());
		    		config = companyConfigService.getByCondition(config);
		    		param.put(employeeType,config);
		    	}
		    	emp.setEmpTypeId(config.getId());
		    	
		    	//3.汇报对象
		    	String leaderName=(String)unitLine.get(10);
		    	Employee leader = (Employee)param.get(leaderName);
		    	if(leader == null) {
		    		leader = new Employee();
		    		leader.setCnName(leaderName);
		    		leader = employeeService.getByCondition(leader);
		    		param.put(leaderName,leader);
		    	}
		    	emp.setReportToLeader(leader.getId());
		    	//4.性别
		    	emp.setSex(StringUtils.equalsIgnoreCase("男",(String)unitLine.get(12))?0:1);
		    	//5.工时种类
		    	String typeOfWork = (String)unitLine.get(15);
		    	CompanyConfig typeOfWorkConfig =  (CompanyConfig)param.get(typeOfWork);
		    	if(typeOfWorkConfig == null) {
		    		typeOfWorkConfig = new CompanyConfig();
		    		typeOfWorkConfig.setDisplayName(typeOfWork);
		    		typeOfWorkConfig.setCompanyId(emp.getCompanyId());
		    		typeOfWorkConfig = companyConfigService.getByCondition(config);
		    		param.put(typeOfWork,typeOfWorkConfig);
		    	}
		    	emp.setWorkType(typeOfWorkConfig.getId());
		    	//6.工时种类
		    	String whetherScheduling = (String)unitLine.get(16);
		    	Config whetherSchedulingConfig =  (Config)param.get(whetherScheduling);
		    	if(whetherSchedulingConfig == null) {
		    		whetherSchedulingConfig = new Config();
		    		whetherSchedulingConfig.setDisplayName(typeOfWork);
		    		whetherSchedulingConfig = getByCondition(whetherSchedulingConfig);
		    		param.put(whetherScheduling,whetherSchedulingConfig);
		    	}
		    	emp.setWhetherScheduling(whetherSchedulingConfig.getId());
		    	//设置默认字段:job_status
		    	emp.setJobStatus(0);//工作状态（0：在职，1：离职）
		    	emp.setCreateTime(new Date());
		    	emp.setCreateUser("system");
		    	emp.setUpdateTime(emp.getCreateTime());
		    	emp.setUpdateUser(emp.getCreateUser());
		    	emp.setDelFlag(0);
		    	emp.setVersion(0L);
		    	employeeService.save(emp);
		    	//存储通信证信息
		    	//通行证账号
		    	user.setUserName(StringUtils.substringBefore(emp.getEmail(), "@"));
		    	user.setEmployeeId(emp.getId());
		    	user.setIsLocked(0);
		    	user.setCreateTime(emp.getCreateTime());
		    	user.setCreateUser("system");
		    	user.setUpdateTime(emp.getCreateTime());
		    	user.setUpdateUser("system");
		    	user.setDelFlag(0);
		    	userService.save(user);
		    	//TODO:职位关系,部门关系
		    	//职位关系sql
		    	positionSql.append("("+emp.getId()+","+position.getId()+","+emp.getCreateTime()+","+emp.getCreateTime()+","+currentUser.getId()+","+currentUser.getId()+",0,0),");
		    	//部门关系sql
		    	departSql.append("("+emp.getId()+","+depart.getId()+","+emp.getCreateTime()+","+emp.getCreateTime()+","+currentUser.getId()+","+currentUser.getId()+",0,0),");
		    } 
		    //for循环结束剔除sql结尾的','
		    positionSql.deleteCharAt(positionSql.length()-1);
		    positionSql.append(";");
		    departSql.deleteCharAt(departSql.length()-1);
		    departSql.append(";");
		    //TODO
		  logger.info("导入员工基本数据-----结束");
		return flag;
	}

	@Override
	public List<Long> getNeedEmpTypeIdList() {
		Config config = new Config();
		config.setCode("need_show_emp_type_id");
		List<Config> confList = configMapper.getListByCondition(config);
		List<Long> empTypeIdList = new ArrayList<Long>();
    	for(Config data:confList){
    		empTypeIdList.add(Long.valueOf(data.getDisplayCode()));
    	}
		return empTypeIdList;
	}

	@Override
	public boolean checkYearLeaveCanOverDraw() {
		boolean isOverDraw = true;
		Config config = new Config();
		config.setCode(ConfigConstants.YEAR_LEAVE_OVERDRAW_FLAG);
		List<Config> configList =  configMapper.getListByCondition(config);
		if(configList!=null&&configList.size()>0) {
			String overDrawFlag = configList.get(0).getDisplayCode();
			if(overDrawFlag.equals(ConfigConstants.YEAR_LEAVE_OVERDRAW_NO)) {
				isOverDraw = false;
			}
		}
		return isOverDraw;
	}
}
