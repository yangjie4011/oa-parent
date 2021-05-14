package com.ule.oa.base.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.google.common.collect.Maps;
import com.ibm.icu.text.SimpleDateFormat;
import com.ule.arch.basehttpclient.BaseHttpClientFactory;
import com.ule.arch.basehttpclient.client.BaseHttpClient;
import com.ule.arch.basehttpclient.model.HttpRequest;
import com.ule.arch.basehttpclient.model.HttpResponse;
import com.ule.arch.basehttpclient.standard.util.ContentCoverter;
import com.ule.oa.base.cache.ConfigCacheManager;
import com.ule.oa.base.mapper.CompanyMapper;
import com.ule.oa.base.mapper.DepartMapper;
import com.ule.oa.base.mapper.EmpDepartMapper;
import com.ule.oa.base.mapper.EmpPositionMapper;
import com.ule.oa.base.mapper.EmpTypeMapper;
import com.ule.oa.base.mapper.EmployeeMapper;
import com.ule.oa.base.mapper.PositionMapper;
import com.ule.oa.base.mapper.RabcUserMapper;
import com.ule.oa.base.po.ApplicationEmployeeClassDetail;
import com.ule.oa.base.po.AttnUsers;
import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.CompanyConfig;
import com.ule.oa.base.po.CompanyPositionLevel;
import com.ule.oa.base.po.CompanyPositionSeq;
import com.ule.oa.base.po.Config;
import com.ule.oa.base.po.CoopCompany;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpContract;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.EmpType;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.EmployeeApp;
import com.ule.oa.base.po.EmployeeClass;
import com.ule.oa.base.po.ImportEmployeeExcel;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.RequestParamQueryEmpCondition;
import com.ule.oa.base.po.SendMail;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.ApplicationEmployeeClassDetailService;
import com.ule.oa.base.service.CompanyConfigService;
import com.ule.oa.base.service.CompanyPositionLevelService;
import com.ule.oa.base.service.CompanyPositionSeqService;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.ConfigService;
import com.ule.oa.base.service.CoopCompanyService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpApplicationRegisterService;
import com.ule.oa.base.service.EmpContractService;
import com.ule.oa.base.service.EmpDepartService;
import com.ule.oa.base.service.EmpLeaveService;
import com.ule.oa.base.service.EmpPositionService;
import com.ule.oa.base.service.EmpTypeService;
import com.ule.oa.base.service.EmployeeAppService;
import com.ule.oa.base.service.EmployeeClassService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.SendMailService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.exception.OaException;
import com.ule.oa.common.utils.ConfigConstants;
import com.ule.oa.common.utils.DateUtils;
import com.ule.oa.common.utils.ExcelUtil;
import com.ule.oa.common.utils.PageModel;
import com.ule.oa.common.utils.UploadUtil;
import com.ule.oa.common.utils.excel.ExcelImportUtil;
import com.ule.oa.common.utils.json.JSONUtils;

@Service
public class EmployeeServiceImpl implements EmployeeService {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EmployeeMapper employeeMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DepartService departService;
	@Autowired
	private PositionService positionService;
	@Autowired
	private EmpTypeService empTypeService;
	@Autowired
	private CompanyConfigService companyConfigService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private CompanyPositionSeqService companyPositionSeqService;
	@Autowired
	private CompanyPositionLevelService companyPositionLevelService;
	@Autowired
	private EmpContractService empContractService;
	@Autowired
	private CoopCompanyService coopCompanyService;
	@Autowired
	private EmpDepartService empDepartService;
	@Autowired
	private EmpPositionService empPositionService;
	@Resource
	private ConfigCacheManager configCacheManager;
	@Autowired
	private EmployeeAppService employeeAppService;
	@Autowired
	private EmpLeaveService empLeaveService;
	@Autowired
	private EmpPositionMapper empPositionMapper;
	@Autowired
	private EmpApplicationRegisterService empApplicationRegisterService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private ApplicationEmployeeClassDetailService applicationEmployeeClassDetailService;
	@Autowired
	private EmployeeClassService employeeClassService;
	@Autowired
	private PositionMapper positionMapper;
	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private EmpTypeMapper empTypeMapper;
	@Autowired
	private DepartMapper departMapper;
	@Autowired
	private EmpDepartMapper empDepartMapper;
	@Autowired
	private RabcUserMapper rabcUserMapper;

	private static BaseHttpClient client = BaseHttpClientFactory.getClient();
	
	/**
	 * save(保存个人信息) @Title: save @Description: 保存个人信息 @param employee 设定文件 void
	 * 返回类型 @throws
	 */
	public void save(Employee employee) {
		employeeMapper.save(employee);
	}

	/**
	 * updateById(更新个人信息) @Title: updateById @Description: 更新个人信息 @param
	 * employee @return 设定文件 int 返回类型 @throws
	 */
	public int updateById(Employee employee) {
		return employeeMapper.updateById(employee);
	}

	/**
	 * getListByCondition(根据查询条件查询所有的员工个人信息) @Title:
	 * getListByCondition @Description: 根据查询条件查询所有的员工个人信息 @param
	 * employee @return 设定文件 List<Employee> 返回类型 @throws
	 */
	public List<Employee> getListByCondition(Employee employee) {
		return employeeMapper.getListByCondition(employee);
	}

	/**
	 * getPageList(分页查询员工信息) @Title: getPageList @Description: 分页查询员工信息 @param
	 * employee @return 设定文件 PageModel<Employee> 返回类型 @throws
	 */
	public PageModel<Employee> getPageList(Employee employee) {
		
		long time1 = System.currentTimeMillis();
		
		List<Integer> departList = departService.getDepartList(employee.getFirstDepart(), employee.getSecondDepart());
		employee.setDepartList(departList);

		int page = employee.getPage() == null ? 0 : employee.getPage();
		int rows = employee.getRows() == null ? 0 : employee.getRows();

		PageModel<Employee> pm = new PageModel<Employee>();
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			pm.setPageNo(0);
			pm.setPageSize(0);
			pm.setTotal(0);
			employee.setOffset(pm.getOffset());
			employee.setLimit(pm.getLimit());
			pm.setRows(new ArrayList<Employee>());
			return pm;
		}else{
			pm.setPageNo(page);
			pm.setPageSize(rows);
			employee.setCurrentUserDepart(deptDataByUserList);//数据权限
			List<Long> subEmployeeIdList = getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				employee.setSubEmployeeIdList(subEmployeeIdList);
			}
			if(employee.getEmpTypeId()==null){
				employee.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
			}
			
			Integer total = employeeMapper.getCount(employee);
			pm.setTotal(total);
			employee.setOffset(pm.getOffset());
			employee.setLimit(pm.getLimit());
			List<Employee> employees = employeeMapper.getPageList(employee);
			// 获得部门名称（一级部门+"_"+当前部门）
			//getDepartName(employees);
	
			pm.setRows(employees);
			
			long time2 = System.currentTimeMillis();
			logger.info("employee/getPageList uses time is:"+(time2-time1));
			return pm;
		}
	}
	/**
	 * getPageList(分页查询员工信息)不带权限 @Title: getPageList @Description: 分页查询员工信息 @param
	 * employee @return 设定文件 PageModel<Employee> 返回类型 @throws
	 */
	public PageModel<Employee> getDeptList(Employee employee) {
		
		//获取指定员工类型数据
		employee.setEmpTypeIdList(configService.getNeedEmpTypeIdList());
		
		List<Integer> departList = departService.getDepartList(employee.getFirstDepart(), employee.getSecondDepart());
		employee.setDepartList(departList);

		int page = employee.getPage() == null ? 0 : employee.getPage();
		int rows = employee.getRows() == null ? 0 : employee.getRows();

		PageModel<Employee> pm = new PageModel<Employee>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = employeeMapper.getCount(employee);
		pm.setTotal(total);
		employee.setOffset(pm.getOffset());
		employee.setLimit(pm.getLimit());
		List<Employee> employees = employeeMapper.getPageList(employee);
		// 获得部门名称（一级部门+"_"+当前部门）
		getDepartName(employees);
		pm.setRows(employees);
		return pm;
	}

	/**
	 * getPageList(分页查离职通知员工信息) @Title: getPageList @Description:
	 * 分页查离职通知员工信息 @param employee @return 设定文件 PageModel<Employee> 返回类型 @throws
	 */
	public PageModel<Employee> getQuitPageList(Employee employee) {

		List<Integer> departList = departService.getDepartList(employee.getFirstDepart(), employee.getSecondDepart());
		employee.setDepartList(departList);

		int page = employee.getPage() == null ? 0 : employee.getPage();
		int rows = employee.getRows() == null ? 0 : employee.getRows();

		PageModel<Employee> pm = new PageModel<Employee>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = employeeMapper.getQuitCount(employee);
		pm.setTotal(total);
		employee.setOffset(pm.getOffset());
		employee.setLimit(pm.getLimit());
		List<Employee> employees = employeeMapper.getQuitPageList(employee);
		// 获得部门名称（一级部门+"_"+当前部门）
		getDepartName(employees);

		pm.setRows(employees);
		return pm;
	}

	public void getDepartName(List<Employee> employees) {
		for (Employee employee : employees) {
			if (null != employee.getDepart()) {
				Long departId = employee.getDepart().getId();

				Depart dp = departService.getById(departId);
				if (null != dp) {
					if (dp.getType() != 1 && null != dp.getParentId()) {
						dp.setName(setDepartName(dp, dp.getName()));
					}

					employee.setDepart(dp);
				}
			} else {
				employee.setDepart(new Depart());
			}
		}
	}

	public String setDepartName(Depart parentDepart, String childDepartName) {
		Depart model = new Depart();
		model.setId(parentDepart.getParentId());
		Depart dp = departService.getByCondition(model);

		if (null != dp) {
			if (dp.getType().intValue() == 1 || null == dp.getParentId()) {// 当前查找的节点是一级部门或者当前部门是挂在虚拟部门下面
				return dp.getName() + "_" + childDepartName;
			} else {
				return setDepartName(dp, childDepartName);
			}
		} else {
			return parentDepart.getName();
		}
	}

	/**
	 * getDepartName(根据部门id获取一级部门+当前部门) @Title: getDepartName @Description:
	 * 根据部门id获取一级部门+当前部门 @param departId @return 设定文件 String 返回类型 @throws
	 */
	public String getDepartName(Long departId) {
		Depart dp = departService.getById(departId);
		if (null != dp) {
			if (dp.getType() != 1 && null != dp.getParentId()) {
				return setDepartName(dp, dp.getName());
			} else {
				return dp.getName();
			}
		} else {
			return "";
		}
	}

	/**
	 * @throws OaException
	 *             getCurrentEmployeeId(获取当前登录员工id) @Title:
	 *             getCurrentEmployeeId @Description: 获取当前登录员工id @return 设定文件
	 *             Long 返回类型 @throws
	 */
	@Override
	public Long getCurrentEmployeeId() throws OaException {
		Employee emp = getCurrentEmployee();
		if (null == emp) {
			throw new OaException("获取当前登录员工信息失败，请重新登录");
		}

		return emp.getId();
	}

	/**
	 * getCurrentEmployee(获取当前登录员工信息) @Title: getCurrentEmployee @Description:
	 * 获取当前登录员工信息 @return 设定文件 Employee 返回类型 @throws
	 */
	@Override
	public Employee getCurrentEmployee() throws OaException {
		User user = userService.getCurrentUser();
		if (null == user) {
			throw new OaException("获取当前登录员工信息失败，请重新登录");
		}

		return user.getEmployee();
	}

	/**
	 * getByCondition(根据条件查询员工信息) @Title: getByCondition @Description:
	 * 根据条件查询员工信息 @return 设定文件 Long 返回类型 @throws
	 */
	@Override
	public Employee getByCondition(Employee employee) {
		List<Employee> EmployeeList = getListByCondition(employee);

		if (null != EmployeeList && EmployeeList.size() > 0) {
			return EmployeeList.get(EmployeeList.size() - 1);
		}

		return employee;
	}

	/**
	 * getEmpDetailByCondition(获取员工相关详细信息) @Title:
	 * getEmpDetailByCondition @Description: 获取员工相关详细信息 @param employee @return
	 * 设定文件 Employee 返回类型 @throws
	 */
	@Override
	public Employee getEmpDetailByCondition(Employee employee) {
		CompanyConfig companyConfig = null;
		Config config = null;
		Employee emp = getByCondition(employee);

		// 查找员工上级
		if (null != emp) {
			EmpDepart ed = new EmpDepart();
			ed.setEmployeeId(employee.getId());
			ed = empDepartService.getByCondition(ed);

			if (null != ed) {
				Depart d = departService.getById(ed.getDepartId());
				if (null != d) {
					Employee e = new Employee();
					e.setId(d.getLeader());
					e = getByCondition(e);
					if (null != e) {
						emp.setLeaderName(e.getCnName());
					}
				}
			}
		}

		// 政治面貌
		if (null != emp && null != emp.getPoliticalStatus() && StringUtils.isBlank(emp.getPoliticalStatusOther())) {
			config = new Config();
			config.setId(emp.getPoliticalStatus());

			Config c = configService.getByCondition(config);
			if (null != c) {
				emp.setPoliticalName(c.getDisplayName());
			}
		} else {
			emp.setPoliticalName(emp.getPoliticalStatusOther());
		}

		// 文化程度
		if (null != emp && null != emp.getDegreeOfEducation() && StringUtils.isBlank(emp.getDegreeOfEducationOther())) {
			config = new Config();
			config.setId(emp.getDegreeOfEducation());

			Config c = configService.getByCondition(config);
			if (null != c) {
				emp.setDegreeOfEducationName(c.getDisplayName());
			}
		} else {
			emp.setDegreeOfEducationName(emp.getDegreeOfEducationOther());
		}

		// 行业相关性
		if (null != emp && null != emp.getIndustryRelevance() && StringUtils.isBlank(emp.getIndustryRelevanceOther())) {
			companyConfig = new CompanyConfig();
			companyConfig.setId(emp.getIndustryRelevance());
			companyConfig.setCompanyId(emp.getCompanyId());
			;

			CompanyConfig cc = companyConfigService.getByCondition(companyConfig);
			if (null != cc) {
				emp.setIndustryRelevanceName(cc.getDisplayName());
			}
		} else {
			emp.setIndustryRelevanceName(emp.getIndustryRelevanceOther());
		}

		// 婚姻状况
		if (null != emp && null != emp.getMaritalStatus()) {
			config = new Config();
			config.setId(emp.getMaritalStatus());

			Config c = configService.getByCondition(config);
			if (null != c) {
				emp.setMaritalStatusName(c.getDisplayName());
			}
		}

		// 民族
		if (null != emp && null != emp.getNation()) {
			config = new Config();
			config.setId(emp.getNation());

			Config c = configService.getByCondition(config);
			if (null != c) {
				emp.setNationName(c.getDisplayName());
			}
		}

		// 工时类型
		if (null != emp && null != emp.getWorkType()) {
			companyConfig = new CompanyConfig();
			companyConfig.setId(emp.getWorkType());

			CompanyConfig cc = companyConfigService.getByCondition(companyConfig);
			if (null != cc) {
				emp.setWorkTypeName(cc.getDisplayName());
			}
		}

		// 是否排班
		if (null != emp && null != emp.getWhetherScheduling()) {
			config = new Config();
			config.setId(emp.getWhetherScheduling());

			Config c = configService.getByCondition(config);
			if (null != c) {
				emp.setWhetherSchedulingName(c.getDisplayName());
			}
		}

		// 封装合作公司
		if (null != emp && null != emp.getCoopCompanyId()) {
			CoopCompany c = new CoopCompany();
			c.setId(emp.getCoopCompanyId());

			CoopCompany cc = coopCompanyService.getByCondition(c);
			if (null != cc) {
				emp.setCoopCompany(cc);
			}
		}

		// 封装公司
		if (null != emp && null != emp.getCompanyId()) {
			Company c = new Company();
			c.setId(emp.getCompanyId());
			Company co = companyService.getByCondition(c);

			if (null != co) {
				emp.setCompany(co);
			}
		}

		// 查询部门信息
		EmpDepart empDepart = new EmpDepart();
		Depart depart = new Depart();
		if (null != emp && null != emp.getId()) {
			empDepart = new EmpDepart();
			empDepart.setEmployeeId(emp.getId());
			EmpDepart ed = empDepartService.getByCondition(empDepart);

			if (null != ed) {
				depart = departService.getById(ed.getDepartId());

				if (null != depart) {
					if (null != depart.getLeader()) {
						employee.setId(depart.getLeader());
						employee = this.getByCondition(employee);

						if (null != employee) {
							depart.setLeaderName(employee.getCnName());
						}

					}
				} else {
					depart = new Depart();
				}
			} else {
				depart = new Depart();
				empDepart = new EmpDepart();
			}
		}
		empDepart.setDepart(depart);
		emp.setEmpDepart(empDepart);

		// 查询职位信息
		EmpPosition ep = new EmpPosition();
		Position po = new Position();
		if (null != emp && null != emp.getId()) {
			EmpPosition empPosition = new EmpPosition();
			empPosition.setEmployeeId(emp.getId());

			ep = empPositionService.getByCondition(empPosition);
			if (null != ep && null != ep.getPositionId()) {
				po = positionService.getById(ep.getPositionId());

				if (null != po) {
					// 职级
					CompanyPositionLevel pl = new CompanyPositionLevel();
					pl.setId(po.getPositionLevelId());
					pl.setCompanyId(po.getCompanyId());

					CompanyPositionLevel cp = companyPositionLevelService.getByCondition(pl);
					if (null != cp) {
						po.setCompanyPositionLevel(cp);
					}

					// 职位序列
					CompanyPositionSeq ps = new CompanyPositionSeq();
					ps.setId(po.getPositionSeqId());
					ps.setCompanyId(po.getCompanyId());
					CompanyPositionSeq cps = companyPositionSeqService.getByCondition(ps);
					if (null != cps) {
						po.setCompanyPositionSeq(cps);
					}
				} else {
					po = new Position();
				}
			} else {
				po = new Position();
				ep = new EmpPosition();
			}
		}
		ep.setPosition(po);
		emp.setEmpPosition(ep);

		// 员工类型
		if (null != emp && null != emp.getEmpTypeId()) {
			EmpType empType = new EmpType();
			empType.setId(emp.getEmpTypeId());
			EmpType et = empTypeService.getByCondition(empType);

			if (null != et) {
				emp.setEmpType(et);
			}
		}

		// 员工汇报对象
		if (null != emp && null != emp.getEmpTypeId()) {
			Long reportId = emp.getReportToLeader();
			Employee reportToLeade = getById(reportId);
			if (null != reportToLeade) {
				emp.setReportToLeaderName(reportToLeade.getCnName());
			}
		}

		// 合同信息
		EmpContract empContract = new EmpContract();
		empContract.setCompanyId(emp.getCompanyId());
		empContract.setEmployeeId(emp.getId());
		empContract.setIsActive(EmpContract.IS_ACTIVE_NORMAL);
		EmpContract contract = empContractService.getByCondition(empContract);
		if (null != contract) {
			emp.setEmpContract(contract);
		}

		return emp;
	}

	/**
	 * 
	 * getDepartEmpsList(根据部门ID获取部门员工列表) @Title: getDepartEmpsList @param
	 * id @return 设定文件 List<Employee> 返回类型 @throws
	 */
	@Override
	public List<Employee> getDepartEmpsList(Long id) {
		return employeeMapper.getDepartEmpsList(id);
	}

	@Override
	public List<Employee> getEmpsByDepart(String departId) {
		return employeeMapper.getEmpsByDepart(departId);
	}

	@Override
	public Employee getLeaderById(Long id) {
		return employeeMapper.getLeaderById(id);
	}

	@Override
	public List<Employee> getReportPerson(Employee employee) {
		List<Employee> list = new ArrayList<Employee>();
		Depart depart = departService.getById(employee.getEmpDepart().getDepartId());
		// 部门负责人
		Employee dh = new Employee();
		dh = employeeMapper.getLeaderById(depart.getLeader());
		while (depart.getType().intValue() != Depart.TYPE_DEPART_ONE.intValue()) {
			depart = departService.getById(depart.getParentId());
			if (depart.getType().intValue() == Depart.TYPE_DEPART_ONE.intValue()) {
				break;
			}
		}
		// 所属一级部门部门负责人
		Employee pdh = new Employee();
		pdh = employeeMapper.getLeaderById(depart.getLeader());
		// M级员工
		list = employeeMapper.getMLevalEmpsByDepart(employee);
		if (dh != null) {
			list.add(dh);
		}
		if (pdh != null) {
			list.add(pdh);
		}
		// 去重
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size() - 1; i++) {
				for (int j = list.size() - 1; j > i; j--) {
					if (list.get(j).getId().equals(list.get(i).getId())) {
						list.remove(j);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 
	 * getListForBoardroomDue(会议室预定页面获取员工列表) @Title:
	 * getListForBoardroomDue @param employee @return 设定文件 @see
	 * com.ule.oa.base.service.EmployeeService#getListForBoardroomDue(com.ule.oa.base.po.Employee) @throws
	 */
	@Override
	public PageModel<Employee> getListForBoardroomDue(Employee employee) {
		int page = employee.getPage() == null ? 1 : employee.getPage();
		int rows = employee.getRows() == null ? 10 : employee.getRows();
		PageModel<Employee> pm = new PageModel<Employee>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = employeeMapper.getListCountForBoardroomDue(employee);
		List<Employee> emList = new ArrayList<Employee>();
		if (page == 1 && total < rows) {
			employee.setOffset(pm.getOffset());
			employee.setLimit(pm.getLimit());
			emList = employeeMapper.getListForBoardroomDue(employee);
		}
		pm.setTotal(total);
		pm.setRows(emList);
		return pm;
	}

	@Override
	public Employee getForEmpSelfAssessmentById(Long id) {
		return employeeMapper.getForEmpSelfAssessmentById(id);
	}

	@Override
	public int getEmpCountByDepartId(Long departId) {
		return employeeMapper.getEmpCountByDepartId(departId);
	}

	@Override
	public int getEmpTotalByDepartId(Long departId) {
		return employeeMapper.getEmpTotalByDepartId(departId);
	}

	@Override
	public List<Employee> getMLeaderByDepartId(Long departId) {
		return employeeMapper.getMLeaderByDepartId(departId);
	}

	@Override
	public Employee getById(Long id) {
		return employeeMapper.getById(id);
	}

	/**
	 * saveEmployeeInfo(保存员工信息与成长履历表) @Title: saveEmployeeInfo @Description:
	 * 保存员工信息与成长履历表 @param file @param employee @throws Exception 设定文件 @see
	 * com.ule.oa.base.service.EmployeeService#saveEmployeeInfo(org.springframework.web.multipart.commons.CommonsMultipartFile,
	 * com.ule.oa.base.po.Employee) @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveEmployeeInfo(CommonsMultipartFile file, Employee employee) throws Exception {
		String filename = file.getOriginalFilename();
		String fileFullName = "uleOA/pic/employee/"
				+ DateUtils.getNow().replace(" ", "").replace("-", "").replace(":", "") + "/" + filename;
		File temfile = null;
		try {
			temfile = new File(filename);
			FileUtils.writeByteArrayToFile(temfile, UploadUtil.fileToByteArray(file));
			// 上传图片，返回图片url
			String result = UploadUtil.uploadToQiNiu(temfile, fileFullName, ConfigConstants.QINIU_HOST);
			Map<String, String> resultMap = (Map<String, String>) JSONUtils.readAsMap(result);
			if (null != resultMap && "success".equals(resultMap.get("status"))
					&& StringUtils.isNotBlank(resultMap.get("url"))) {
				employee.setPicture(resultMap.get("url"));
				employee.setCreateTime(new Date());
				employee.setCreateUser(userService.getCurrentAccount());

				// 保存员工信息
				employeeMapper.save(employee);

				// 保存员工部门信息
				empDepartService.save(employee.getEmpDepart());

				// 保存员工职位信息
				empPositionService.save(employee.getEmpPosition());
			} else {
				throw new Exception("保存失败!");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (null != temfile) {
				if(!temfile.delete()) {
					
				}
			}
		}
	}

	/**
	 * saveEmployeeBase(保存员工基础信息) @Title: saveEmployeeBase @Description:
	 * 保存员工基础信息 @param file @param employee @throws Exception 设定文件 @see
	 * com.ule.oa.base.service.EmployeeService#saveEmployeeInfo(org.springframework.web.multipart.commons.CommonsMultipartFile,
	 * com.ule.oa.base.po.Employee) @throws
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveEmployeeBase(Employee employee) throws Exception {
		try {
			employee.setCreateTime(new Date());
			employee.setCreateUser(userService.getCurrentAccount());

			// 保存员工信息
			employeeMapper.save(employee);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Employee> getByEmpId(Long id) {
		return employeeMapper.getByEmpId(id);
	}

	@Override
	public PageModel<Employee> getEmpListByCondition(Employee employee) {
		int page = employee.getPage() == null ? 0 : employee.getPage();
		int rows = employee.getRows() == null ? 0 : employee.getRows();

		PageModel<Employee> pm = new PageModel<Employee>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = employeeMapper.getEmpListByConditionCount(employee);
		pm.setTotal(total);
		employee.setOffset(pm.getOffset());
		employee.setLimit(pm.getLimit());
		List<Employee> employees = employeeMapper.getEmpListByCondition(employee);
		pm.setRows(employees);
		return pm;
	}

	@Override
	public List<Employee> getAttnEmpListByCondition(Employee employee) {
		return employeeMapper.getAttnEmpListByCondition(employee);
	}

	@Override
	public int getListByConditionCount(Employee employee) {
		return employeeMapper.getListByConditionCount(employee);
	}

	@Override
	public Integer getEmpListByNameCount(String cnName) {
		return employeeMapper.getEmpListByNameCount(cnName);
	}

	@Override
	public Integer getEmpListLikeNameCount(String cnName) {
		return employeeMapper.getEmpListLikeNameCount(cnName);
	}

	@Override
	public List<Employee> getByEmpIdList(List<Long> list) {
		return employeeMapper.getByEmpIdList(list);
	}

	@Override
	public List<Employee> getByIds(List<Long> ids) {
		Employee employee = new Employee();
		employee.setIds(ids);
		List<Employee> list = employeeMapper.getListByCondition(employee);
		// 查询部门信息
		for (Employee emp : list) {
			if (null != emp && null != emp.getId()) {
				Depart depart = departService.getInfoByEmpId(emp.getId());
				emp.setDepartName(depart != null ? depart.getName() : null);
				Position position = positionService.getByEmpId(emp.getId());
				emp.setPositionName(position != null ? position.getPositionName() : null);
			}
		}
		return list;
	}

	@Override
	public List<Employee> getReportPersonList(Long id) {

		Employee condition = new Employee();
		condition.setId(id);
		List<Employee> list = employeeMapper.getAllMLevalEmps(condition);
		return list;
	}

	/**
	 * getEmpMobileByUserName(根据用户名获取员工电话) @Title:
	 * getEmpMobileByUserName @Description: 根据用户名获取员工电话 @param
	 * userName @return @throws OaException 设定文件 EmployeeApp 返回类型 @throws
	 */
	public Employee getEmpMobileByUserName(String userName) throws OaException {
		User oldU = new User();
		oldU.setUserName(userName);
		oldU = userService.getByCondition(oldU);

		Long empId = 0L;
		if (null != oldU && null != oldU.getId()) {
			empId = oldU.getEmployeeId();
		} else {
			throw new OaException("您输入的账户不对，请重新输入");
		}

		// 验证输入的手机号和入职登记的手机号是否一致
		Employee emp = getById(empId);
		if (null == emp || null == emp.getId()) {
			throw new OaException("您输入的账户没有入职信息");
		}
		String mobile = emp.getMobile();
		if (StringUtils.isBlank(mobile)) {
			throw new OaException("您入职登记的时候没有填写手机号,无法修改密码");
		}

		return emp;
	}

	@Override
	public HSSFWorkbook exportEmployeeList(Employee employee) {
		User currentUser = userService.getCurrentUser();
		List<Long> deptDataByUserList = rabcUserMapper.getDataPermissions(currentUser.getEmployeeId());
		if(deptDataByUserList==null || deptDataByUserList.size()==0){
			return null;
		}else{
			List<Integer> departList = departService.getDepartList(employee.getFirstDepart(), employee.getSecondDepart());
			employee.setDepartList(departList);
			employee.setCurrentUserDepart(deptDataByUserList);//数据权限
			List<Long> subEmployeeIdList = getSubEmployeeList(currentUser.getEmployeeId());//下属员工
			if(subEmployeeIdList!=null&&subEmployeeIdList.size()>0){
				employee.setSubEmployeeIdList(subEmployeeIdList);
			}
			List<Employee> employees = employeeMapper.getPageList(employee);
	
			for (int i = 0; i < employees.size(); i++) {
				switch (employees.get(i).getJobStatus()) {
				case 0:
					employees.get(i).setJobStatusName("在职");
					break;
				case 1:
					employees.get(i).setJobStatusName("离职");
					break;
				case 2:
					employees.get(i).setJobStatusName("待离职");
					break;
				case 3:
					employees.get(i).setJobStatusName("已提出离职");
					break;
				default:
					employees.get(i).setJobStatusName("未命名,请赋此员工 在职状态！");
					break;
				}
			}
			// 获得部门名称（一级部门+"_"+当前部门）
			getDepartName(employees);
	
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> datas = employees.stream().map(o -> {
				Map<String, Object> map = new HashMap<>();
				try {
					// BeanUtils.populate(o,map);
					map = BeanUtils.describe(o);
				} catch (Exception e) {
				}
				return map;
			}).collect(Collectors.toList());
	
			String[] titles = { "员工编号", "员工姓名", "员工类型", "部门", "职位名称", "工时制", "在职状态" };
			String[] keys = { "code", "cnName", "empTypeName", "departName", "positionName", "workTypeName",
					"jobStatusName" };
	
			return ExcelUtil.exportExcel(datas, keys, titles, "workbook.xls");
		}
	}

	@Override
	public HSSFWorkbook exportQuitEmployeeList(Employee employee) {
		List<Map<String, Object>> sMapList = new ArrayList<Map<String, Object>>();
		List<Integer> departList = departService.getDepartList(employee.getFirstDepart(), employee.getSecondDepart());
		employee.setDepartList(departList);
		List<Employee> employees = employeeMapper.getQuitPageList(employee);

		// 获得部门名称（一级部门+"_"+当前部门）
		getDepartName(employees);

		/*
		 * @SuppressWarnings("unchecked") List<Map<String,Object>> datas
		 * =employees.stream().map(o ->{ Map<String,Object> map = new
		 * HashMap<>(); try { //处理时间格式 DateConverter dateConverter = new
		 * DateConverter(); //设置日期格式 dateConverter.setPatterns(new
		 * String[]{"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss"}); //注册格式
		 * ConvertUtils.register(dateConverter, Date.class); //封装数据
		 * BeanUtils.populate(o,map); map = BeanUtils.describe(o); } catch
		 * (Exception e) { e.printStackTrace(); } return map;
		 * }).collect(Collectors.toList());
		 */

		for (Employee employeeList : employees) {
			// 封装数据
			Map<String, Object> sdoMap = new HashMap<String, Object>();
			sdoMap.put("code", employeeList.getCode());
			sdoMap.put("cnName", employeeList.getCnName());
			sdoMap.put("empTypeName", employeeList.getEmpTypeName());
			sdoMap.put("firstEntryTime", DateUtils.format(employeeList.getFirstEntryTime(), DateUtils.FORMAT_SHORT));
			sdoMap.put("quitTime", DateUtils.format(employeeList.getQuitTime(), DateUtils.FORMAT_SHORT));
			sdoMap.put("departName", employeeList.getDepartName());
			sdoMap.put("positionName", employeeList.getPositionName());
			sdoMap.put("leaderName", employeeList.getLeaderName());
			sdoMap.put("departLeaderName", employeeList.getDepartLeaderName());
			String jobStatusName = "";
			if (employeeList.getJobStatus().intValue() == 0) {
				jobStatusName = "在职";
			} else if (employeeList.getJobStatus().intValue() == 1) {
				jobStatusName = "离职";
			} else if (employeeList.getJobStatus().intValue() == 2) {
				jobStatusName = "待离职";
			} else if (employeeList.getJobStatus().intValue() == 3) {
				jobStatusName = "已提出离职";
			} else {
				jobStatusName = "未命名,请赋此员工 在职状态！";
			}
			sdoMap.put("jobStatusName", jobStatusName);
			sMapList.add(sdoMap);
		}
		String[] titles = { "员工编号", "员工姓名", "员工类型", "入职日期", "离职日期", "部门", "职位", "汇报对象", "部门负责人", "状态" };
		String[] keys = { "code", "cnName", "empTypeName", "firstEntryTime", "quitTime", "departName", "positionName",
				"leaderName", "departLeaderName", "jobStatusName" };

		return ExcelUtil.exportExcel(sMapList, keys, titles, "workbook.xls");
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updReporterBatch(Long reporterId, String employeeIds) throws OaException {

		Employee emp = getCurrentEmployee();
		String[] idArray = employeeIds.split(",");
		List<Long> ids = Arrays.asList(idArray).stream().map(s -> Long.parseLong(s.trim()))
				.collect(Collectors.toList());

		Employee e = new Employee();
		e.setUpdateUser(emp.getCnName());
		e.setReportToLeader(reporterId);
		e.setIds(ids);
		employeeMapper.updReporterBatch(e);
	}

	@Override
	public void deleteEmp(int id) {
		// employeeMapper.deleteEmp();
	}

	@Override
	public Map<String, Object> updateEmpFingerprintId(Employee employee) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (null != employee.getFingerprintId()) { /** 更改了指纹ID，可以优化判断是否有值变化 **/
				String OA_SERVICE_URL = configCacheManager.getConfigDisplayCode("MO_SERVICE_URL");
				// 校验指纹ID是否存在，存在并且oa_employee_id为空才能入职
				String userid = "";
				HashMap<String, String> paramMap3 = new HashMap<String, String>();
				paramMap3.put("fingerprintId", String.valueOf(employee.getFingerprintId()));
				// OA_SERVICE_URL = "http://localhost:8080/oaService"; //本地测试
				logger.info("入职根据指纹ID查询用户地址:" + OA_SERVICE_URL + "/attnUsers/selectByFingerprintId.htm");
				
				HttpRequest req = new HttpRequest.Builder().url(OA_SERVICE_URL + "/attnUsers/selectByFingerprintId.htm").post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
	    				ContentCoverter.formConvertAsString(paramMap3)).build();
	    		HttpResponse rep = client.sendRequest(req);
				String response3 = rep.fullBody();
				
				Map<?, ?> responseMap3 = JSONUtils.readAsMap(response3);
				String resultCode = (String) responseMap3.get("result");
				if ("success".equals(resultCode)) {
					userid = (String) responseMap3.get("userid");
				} else {
					throw new OaException((String) responseMap3.get("message"));
				}
				// 将OA员工ID关联到考勤机user表
				AttnUsers attnUsers = new AttnUsers();
				attnUsers.setUserid(Integer.valueOf(userid));
				attnUsers.setOaEmpId(employee.getId());
				HashMap<String, String> paramMap4 = new HashMap<String, String>();
				paramMap4.put("jsonData", JSONUtils.write(attnUsers));
				logger.info("入职关联employeeId地址:" + OA_SERVICE_URL + "/attnUsers/updateById.htm");
				
				HttpRequest req4 = new HttpRequest.Builder().url(OA_SERVICE_URL + "/attnUsers/updateById.htm").post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
	    				ContentCoverter.formConvertAsString(paramMap4)).build();
	    		HttpResponse rep4 = client.sendRequest(req4);
				String response4 = rep4.fullBody();
				
				logger.info("关联考勤机结果:" + response4);
				map.put("message", "修改成功");
				map.put("flag", true);
			}
			// 先更新接口数据 在更新表中的数据
			EmployeeApp employeeApp = new EmployeeApp();
			employeeApp.setFingerprintId(employee.getFingerprintId());
			employeeApp.setId(employee.getId());
			employeeApp.setVersion(employee.getVersion());
			employeeAppService.updateEmpBaseInfo(employeeApp);
		} catch (Exception e) {
			map.put("flag", false);
			map.put("message", "修改失败,msg=" + e.getMessage());
		}
		return map;
	}

	@Override
	public Employee queryEmpInfoById(Employee employee) {
		List<Employee> pageList = employeeMapper.getPageList(employee);
		return pageList.get(0);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> updateEmpQuitInfo(Employee employee) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		// 第一步 点击离职时 把jobstart状态 改 成状态 2 代表 代离职状态
		// jobstart 0.在职 1.离职 2.待离职 3.已提出离职 （前台web做出离职操作）
		// 第二步 每天24点 定时刷新 状态 3 时间限制两天
		// 例选择2018年6月19号离职，系统应在6月19号当日24点，即20号0点更新员工状态变为离职
		// 1.把状态改成离职 状态2
		// 2.刷新年假数据
		// 3.发邮件给通知相关人员
		// 4.在把员工信息数据放入 员工历史信息表中
		// 一进方法先把状态改为待离职，判断quitTimeUpdateTime是改期还是离职操作
		Employee employees = new Employee();
		employees.setId(employee.getId());
		employees.setJobStatus(2);
		employees.setVersion(employee.getVersion()); // version +1;
		if (employee.getQuitTime() != null) {
			employees.setQuitTime(employee.getQuitTime());
		} else if (employee.getQuitTimeUpdateTime() != null) {
			employees.setQuitTime(employee.getQuitTimeUpdateTime());
			employees.setQuitTimeUpdateTime(employee.getQuitTimeUpdateTime());
		}
		// 如果是改期 则直接改期（不需要定时）
		if (employee.getQuitTimeUpdateTime() != null) {
			List<Employee> quitPageList = employeeMapper.getQuitPageList(employee);
			if(quitPageList.size()==0){
				logger.info("离职员工 查询失败 ");
				throw new Exception("离职员工 查询失败");
			}
			Employee old = quitPageList.get(0);
			EmployeeApp employeeApp = new EmployeeApp();
			employeeApp.setCnName(old.getCnName());
			// 修改离职时间为当天的23:59:59
			employeeApp.setQuitTime(
					DateUtils.parse(DateUtils.format(employees.getQuitTime(), DateUtils.FORMAT_SHORT) + " 23:59:59",
							DateUtils.FORMAT_LONG));
			employeeApp.setFirstEntryTime(old.getFirstEntryTime());
			employeeApp.setId(old.getId());
			employeeApp.setVersion(old.getVersion());
			employeeApp.setBeforeWorkAge(old.getBeforeWorkAge());
			empLeaveService.updateQuitLeaveByEmpInfo(employeeApp);// 重算年假数据
			empLeaveService.updateSickLeaveByEmpInfo(employeeApp);// 重算病假数据
			// 3.0 发邮件通知相关人员
			User user = new User();
			user.setEmployeeId(old.getId());
			user.setCompanyId(old.getCompanyId());
			List<EmpPosition> positionName = sendMailPositionName();
			List<SendMail> sendMailList = new ArrayList<SendMail>();
			
			//发给默认通知的 pmo 等员工
			String pageStr = employee.getNoticeStr();
			String[] sendPmosEmail = pageStr.split(",");
			if(sendPmosEmail.length>0){
				for (int i = 0; i < sendPmosEmail.length; i++) {
					SendMail sendMail = sendUpdateEmpQuitTimeEmail(employee,sendPmosEmail[i]);
					sendMailList.add(sendMail);				
				}
			}
			logger.info("发送邮件给 默认发送人员  " + old.getCnName() + " 离职日期变更通知 记录数为={}", sendPmosEmail.length+1 + "");
			
			//发给 行政、人事
			for (EmpPosition emp : positionName) {
				// 判断是否有值 如果有 邮件操作则是 更改离职日期
				if (employee.getQuitTimeUpdateTime() != null) {
					empApplicationRegisterService.sendMsg(emp.getEmployeeId(), user,
							"离职日期变更通知——" + old.getDepartName() + " " + old.getCnName(),
							old.getDepartName() + " " + old.getCnName() + "员工已变更离职日期，原离职日期为"
									+ DateUtils.format(employee.getQuitUpdateBefore(), DateUtils.FORMAT_SHORT_CN)
									+ "变更后的离职日期为:"
									+ DateUtils.format(employee.getQuitTimeUpdateTime(), DateUtils.FORMAT_SHORT_CN)
									+ "，请相关部门提前做好离职准备安排。");
					Employee receiver = employeeMapper.getById(emp.getEmployeeId());
					if (receiver != null) {
						SendMail sendMail = sendUpdateEmpQuitTimeEmail(employee,receiver.getEmail());
						sendMailList.add(sendMail);
					}
				}
			}
			logger.info("发送邮件给 人事、行政全组 、IT全组" + old.getCnName() + " 离职日期变更通知 记录数为={}", positionName.size() + "");
			// 给领导、部门负责人发送邮件
			List<Employee> pageList = employeeMapper.getPageList(old);
			if(pageList.size()==0){
				logger.info("领导、部门负责人查询失败 ");
				throw new Exception("领导、部门负责人查询失败");
			}
			Employee empInfo = pageList.get(0);
			
			Employee departName = employeeMapper.getById(empInfo.getDepartLeaderId());
			empApplicationRegisterService.sendMsg(old.getReportToLeader(), user,
					"离职日期变更通知——" + old.getDepartName() + " " + old.getCnName(),
					old.getDepartName() + " " + old.getCnName() + "员工已变更离职日期，原离职日期为"
							+ DateUtils.format(employee.getQuitUpdateBefore(), DateUtils.FORMAT_SHORT_CN) + "变更后的离职日期为:"
							+ DateUtils.format(employee.getQuitTimeUpdateTime(), DateUtils.FORMAT_SHORT_CN)
							+ "，请相关部门提前做好离职准备安排。");
			
			//新员工逐级汇报对象到部门负责人为止
			Long reportToLeader = empInfo.getReportToLeader();//申请人汇报对象
			List<Long> reportToLeaderList = new ArrayList<Long>();
			int count = 0;
			while(true){
				count = count + 1;
				if(reportToLeader!=null){
					reportToLeaderList.add(reportToLeader);
					//判断是否是部门负责人
					List<Depart> departList = departMapper.getAllDepartByLeaderId(reportToLeader);
					if(departList!=null&&departList.size()>0){
						break;
					}else{
						Employee reportToLeaderObject = employeeMapper.getById(reportToLeader);
						if (reportToLeaderObject != null) {
							SendMail sendMail = sendUpdateEmpQuitTimeEmail(employee,reportToLeaderObject.getEmail());
							sendMailList.add(sendMail);
						}
						
						
						reportToLeader = reportToLeaderObject!=null?reportToLeaderObject.getReportToLeader():null;
					}
				}else{
					logger.info("请假申请:申请人汇报对象为空。");
				}
				//对象关系过多，自动跳出循环，防止死循环
				if(count>10){
					break;
				}
			}
			
			if (departName != null && departName.getId() != null) {
				empApplicationRegisterService.sendMsg(departName.getId(), user,
						"离职日期变更通知——" + old.getDepartName() + " " + old.getCnName(),
						old.getDepartName() + " " + old.getCnName() + "员工已变更离职日期，原离职日期为"
								+ DateUtils.format(employee.getQuitUpdateBefore(), DateUtils.FORMAT_SHORT_CN)
								+ "变更后的离职日期为:"
								+ DateUtils.format(employee.getQuitTimeUpdateTime(), DateUtils.FORMAT_SHORT_CN)
								+ "，请相关部门提前做好离职准备安排。");
				Employee receiverDh = employeeMapper.getById(departName.getId());
				if (receiverDh != null) {
					SendMail sendMail = sendUpdateEmpQuitTimeEmail(employee,receiverDh.getEmail());
					sendMailList.add(sendMail);
				}
			}
			
			logger.info("发送邮件给 直接主管，部门负责人 " + old.getDepartName() + old.getCnName() + "离职日期变更通知" + "，最后工作日为"
					+ DateUtils.format(employee.getQuitTime(), DateUtils.FORMAT_SHORT_CN) + "");
			// 循环发送邮件给要通知的员工
			for (int i = 0; i < employee.getIds().size(); i++) {
				empApplicationRegisterService.sendMsg(departName.getId(), user,
						"离职日期变更通知——" + old.getDepartName() + " " + old.getCnName(),
						old.getDepartName() + " " + old.getCnName() + "员工已变更离职日期，原离职日期为"
								+ DateUtils.format(employee.getQuitUpdateBefore(), DateUtils.FORMAT_SHORT_CN)
								+ "变更后的离职日期为:"
								+ DateUtils.format(employee.getQuitTimeUpdateTime(), DateUtils.FORMAT_SHORT_CN)
								+ "，请相关部门提前做好离职准备安排。");
				Employee notiyEmp = employeeMapper.getById(employee.getIds().get(i));
				if (notiyEmp != null) {
					SendMail sendMail = sendUpdateEmpQuitTimeEmail(employee,notiyEmp.getEmail());
					sendMailList.add(sendMail);
				}
			}
			logger.info("发送邮件给 选定相关通知人 " + old.getDepartName() + old.getCnName() + "离职日期变更通知" + "，最后工作日为"
					+ DateUtils.format(employee.getQuitTime(), DateUtils.FORMAT_SHORT_CN) + " 共计发送 指定通知员工 "
					+ employee.getIds().size() + " 位");

			// 循环发送指定发送的人邮件给要通知的员工
			if (StringUtils.isNotBlank(employee.getSendEmail())) {
				if (employee.getSendEmail().contains(",")) {
					String[] sendemial = employee.getSendEmail().split(",");
					for (int i = 0; i < sendemial.length; i++) {
						if (sendemial != null) {
							SendMail sendMail = sendUpdateEmpQuitTimeEmail(employee,sendemial[i]);
							sendMailList.add(sendMail);
						}
					}
				} else {
					SendMail sendMail = sendUpdateEmpQuitTimeEmail(employee,employee.getSendEmail());
					sendMailList.add(sendMail);
				}
				
				logger.info("发送邮件给 指定相关通知人邮箱: " + employee.getSendEmail()+ "离职日期变更通知" + "，最后工作日为"
						+ DateUtils.format(employee.getQuitTime(), DateUtils.FORMAT_SHORT_CN) + " 共计发送 指定通知员工 "
						+ employee.getIds().size() + " 位");
				
			}
			
			
			if (sendMailList != null && sendMailList.size() > 0) {
				//list根据邮箱名去重
				Set<String> set = new  HashSet<String>(); 
		         List<SendMail> newList = new  ArrayList<SendMail>(); 
		         for (SendMail cd:sendMailList) {
		            if(set.add(cd.getReceiver())){
		                newList.add(cd);
		            }
		        }
				sendMailService.batchSave(newList);
			}
			map.put("message", "修改成功");
			map.put("flag", true);
			employeeMapper.updateById(employees);
			return map;
		} else {// 离职操作 改状态为 待离职 2 发送邮件给相关人员
			Map<String, Object> updateQuitYearinfoSendMail = updateQuitYearinfoSendMail(employee);
			// 测试定时任务 保存历史数据 第二天更新状态
			// updateJobStatusAndSaveEmpHistory();
			employeeMapper.updateById(employees);
			return updateQuitYearinfoSendMail;
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> updateQuitYearinfoSendMail(Employee listEmp) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		// 2.0 离职计算 年假 假期 数据
		boolean flagSuccess = false;
		List<Employee> quitPageList = employeeMapper.getQuitPageList(listEmp);
		if(quitPageList.size()==0){
			logger.info("离职员工 查询失败 ");
			throw new Exception("离职员工 查询失败");
		}
		Employee old = quitPageList.get(0);
		EmployeeApp employeeApp = new EmployeeApp();
		employeeApp.setCnName(old.getCnName());
		// 修改离职时间为当天的23:59:59
		employeeApp.setQuitTime(DateUtils.parse(
				DateUtils.format(listEmp.getQuitTime(), DateUtils.FORMAT_SHORT) + " 23:59:59", DateUtils.FORMAT_LONG));
		employeeApp.setFirstEntryTime(old.getFirstEntryTime());
		employeeApp.setId(old.getId());
		employeeApp.setVersion(old.getVersion());
		employeeApp.setBeforeWorkAge(old.getBeforeWorkAge());
		empLeaveService.updateQuitLeaveByEmpInfo(employeeApp);// 重算年假数据
		empLeaveService.updateSickLeaveByEmpInfo(employeeApp);// 重算病假数据
		// 离职排班日期取消
		if (listEmp.getQuitTime() != null) {
			ApplicationEmployeeClassDetail classDetail = new ApplicationEmployeeClassDetail();
			classDetail.setEmployId(listEmp.getId());
			classDetail.setClassDate(listEmp.getQuitTime());
			applicationEmployeeClassDetailService.deleteByQuitTime(classDetail);
			EmployeeClass employeeClass = new EmployeeClass();
			employeeClass.setEmployId(listEmp.getId());
			employeeClass.setClassDate(listEmp.getQuitTime());
			employeeClassService.deleteByQuitTime(employeeClass);
		}

		// 3.0 发邮件通知相关人员
		User user = new User();
		user.setEmployeeId(old.getId());
		user.setCompanyId(old.getCompanyId());
		// 发邮件给相关人员
		if (old != null) {
			// 获取所有需要通知的员工id
			List<SendMail> sendMailList = new ArrayList<SendMail>();
			List<EmpPosition> empPositionList = sendMailPositionName();
			
			
			//发给默认通知的 pmo 等员工
			String pageStr = listEmp.getNoticeStr();
			String[] sendPmosEmail = pageStr.split(",");
			if(sendPmosEmail.length>0 && StringUtils.isNotBlank(sendPmosEmail[0])){
				for (int i = 0; i < sendPmosEmail.length; i++) {
					SendMail sendMail = sendEmpQuitEmail(listEmp,sendPmosEmail[i]);
					sendMailList.add(sendMail);
				}
			}
			logger.info("发送默认发送人员" + old.getCnName() + "员工离职通知 记录数为={}", sendPmosEmail.length+1 + "");
			
			for (EmpPosition emp : empPositionList) {
				// 判断是否有值 如果有 邮件操作则是 更改离职日期
				empApplicationRegisterService.sendMsg(emp.getEmployeeId(), user,
						"离职通知——" + old.getDepartName() + "" + old.getCnName(),
						old.getDepartName() + " " + old.getCnName() + "员工已确定离职，最后工作日为"
								+ DateUtils.format(listEmp.getQuitTime(), DateUtils.FORMAT_SHORT_CN)
								+ "，请相关部门提前做好离职准备安排。");
				Employee receiver = employeeMapper.getById(emp.getEmployeeId());
				if (receiver != null) {
					SendMail sendMail = sendEmpQuitEmail(listEmp,receiver.getEmail());
					sendMailList.add(sendMail);
				}
			}
			logger.info("发送邮件给 人事、行政全组 、IT全组" + old.getCnName() + "员工离职通知 记录数为={}", empPositionList.size() + "");

			// 离职操作时保存数据
			List<Employee> pageList = employeeMapper.getPageList(old);
			if(pageList.size()==0){
				logger.info("离职员工 查询失败 ");
				throw new Exception("离职员工 查询失败");
			}
			Employee empInfo = pageList.get(0);
			Employee departName = employeeMapper.getById(empInfo.getDepartLeaderId());
			
			//新员工逐级汇报对象到部门负责人为止
			Long reportToLeader = old.getReportToLeader();//申请人汇报对象
			List<Long> reportToLeaderList = new ArrayList<Long>();
			int count = 0;
			while(true){
				count = count + 1;
				if(reportToLeader!=null){
					reportToLeaderList.add(reportToLeader);
					//判断是否是部门负责人
					List<Depart> departList = departMapper.getAllDepartByLeaderId(reportToLeader);
					if(departList!=null&&departList.size()>0){
						break;
					}else{
						Employee reportToLeaderObject = employeeMapper.getById(reportToLeader);
						if (reportToLeaderObject != null) {
							SendMail sendMail = sendEmpQuitEmail(listEmp,reportToLeaderObject.getEmail());
							sendMailList.add(sendMail);
							// 发邮件给相关人员 部门负责人 直接负责人
							empApplicationRegisterService.sendMsg(reportToLeaderObject.getId(), user,
									"离职通知——" + old.getDepartName() + "" + old.getCnName(),
									old.getDepartName() + " " + old.getCnName() + "员工已确定离职，最后工作日为"
											+ DateUtils.format(listEmp.getQuitTime(), DateUtils.FORMAT_SHORT_CN) + "，请相关部门提前做好离职准备安排。");
						}
						reportToLeader = reportToLeaderObject!=null?reportToLeaderObject.getReportToLeader():null;
					}
				}else{
					logger.info("请假申请:申请人汇报对象为空。");
				}
				//对象关系过多，自动跳出循环，防止死循环
				if(count>10){
					break;
				}
			}

			if (departName != null && departName.getId() != null) {
				empApplicationRegisterService.sendMsg(departName.getId(), user,
						"离职通知——" + old.getDepartName() + "" + old.getCnName(),
						old.getDepartName() + " " + old.getCnName() + "员工已确定离职，最后工作日为"
								+ DateUtils.format(listEmp.getQuitTime(), DateUtils.FORMAT_SHORT_CN)
								+ "，请相关部门提前做好离职准备安排。");
				Employee receiverDh = employeeMapper.getById(departName.getId());
				if (receiverDh != null) {
					SendMail sendMail = sendEmpQuitEmail(listEmp,receiverDh.getEmail());
					sendMailList.add(sendMail);
				}
			}
			logger.info("发送邮件给 直接主管，部门负责人 " + old.getDepartName() + old.getCnName() + "员工离职通知 " + "，最后工作日为"
					+ DateUtils.format(listEmp.getQuitTime(), DateUtils.FORMAT_SHORT_CN) + "");
			// 循环发送邮件给要通知的员工
			for (int i = 0; i < listEmp.getIds().size(); i++) {
				Employee notiyEmp = employeeMapper.getById(listEmp.getIds().get(i));
				empApplicationRegisterService.sendMsg(listEmp.getIds().get(i), user,
						"离职通知——" + old.getDepartName() + "" + old.getCnName(),
						old.getDepartName() + " " + old.getCnName() + "员工已确定离职，最后工作日为"
								+ DateUtils.format(listEmp.getQuitTime(), DateUtils.FORMAT_SHORT_CN)
								+ "，请相关部门提前做好离职准备安排。");

				if (notiyEmp != null) {
					SendMail sendMail = sendEmpQuitEmail(listEmp,notiyEmp.getEmail());
					sendMailList.add(sendMail);
				}
			}
			logger.info("发送邮件给 选定相关通知人" + old.getDepartName() + old.getCnName() + "员工离职通知 " + "，最后工作日为"
					+ DateUtils.format(listEmp.getQuitTime(), DateUtils.FORMAT_SHORT_CN) + " 共计发送 指定通知员工 "
					+ listEmp.getIds().size() + " 位");
			// 循环发送指定发送的人邮件给要通知的员工
			if (StringUtils.isNotBlank(listEmp.getSendEmail())) {
				if (listEmp.getSendEmail().contains(",")) {
					String[] sendemial = listEmp.getSendEmail().split(",");
					for (int i = 0; i < sendemial.length; i++) {
						if (sendemial != null) {
							SendMail sendMail = sendEmpQuitEmail(listEmp,sendemial[i]);
							sendMailList.add(sendMail);
						}
					}
				} else {
					SendMail sendMail = sendEmpQuitEmail(listEmp,listEmp.getSendEmail());
					sendMailList.add(sendMail);
				}
				logger.info("发送邮件给 指定相关通知人邮箱：" + listEmp.getSendEmail() + "	离职人信息：" + old.getDepartName()
						+ old.getCnName() + "员工离职通知 " + "，最后工作日为"
						+ DateUtils.format(listEmp.getQuitTime(), DateUtils.FORMAT_SHORT_CN));
			}

			// 发邮箱接口
			if (sendMailList != null && sendMailList.size() > 0) {
				//list根据邮箱名去重
				Set<String> set = new  HashSet<String>(); 
		         List<SendMail> newList = new  ArrayList<SendMail>(); 
		         for (SendMail cd:sendMailList) {
		            if(set.add(cd.getReceiver())){
		                newList.add(cd);
		            }
		        }
				sendMailService.batchSave(newList);
			}
		}
		flagSuccess = true;
		if (flagSuccess) {
			map.put("message", "修改成功");
			map.put("flag", true);
		}
		return map;
	}
	
	
	public SendMail sendEmpQuitEmail(Employee employee,String email) throws IOException{
			Employee empTemplet=employeeMapper.getById(employee.getId());
			
			User user = userService.getCurrentUser();
			//发送邮件封装参数
			//日期转化
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			String nowDateStr = sdf.format(new Date());
			String quitTimeStr = sdf.format(employee.getQuitTime());
			//汇报对象
			Employee leader = employeeMapper.getById(empTemplet.getReportToLeader());
			//公司
			Company company = companyMapper.getById(empTemplet.getCompanyId());
			//员工类型
			EmpType empType = empTypeMapper.getById(empTemplet.getEmpTypeId());
			//职位
			List<Employee> byEmpId = employeeMapper.getByEmpId(empTemplet.getId());
			SendMail sendMail = new SendMail();
			String isMoSystem="是";
				sendMail.setReceiver(email);
				if("外包员工".equals(empType.getTypeCName()) || "实习生".equals(empType.getTypeCName())){
					isMoSystem="否";
					if("实习生".equals(empType.getTypeCName())){
						sendMail.setSubject("离职通知——"+employee.getCnName()+"("+empType.getTypeCName()+")");
					}else{
						sendMail.setSubject("离职通知——"+employee.getCnName()+"(外包)");
					}
				}else{
					isMoSystem="是";
					sendMail.setSubject("离职通知——"+employee.getCnName());
				}
			String params[]={
					StringUtils.isEmpty(company.getName()) ? "" : company.getName(),
					StringUtils.isEmpty(employee.getCnName()) ? "" : employee.getCnName(),
					StringUtils.isEmpty(empType.getTypeCName())? "" : empType.getTypeCName(),
					StringUtils.isEmpty(quitTimeStr) ? "" : quitTimeStr,
					StringUtils.isEmpty(byEmpId.get(0).getEmpDepart().getDepart().getName()) ? "" : byEmpId.get(0).getEmpDepart().getDepart().getName(),
					StringUtils.isEmpty(byEmpId.get(0).getEmpPosition().getPosition().getPositionName()) ? "" : byEmpId.get(0).getEmpPosition().getPosition().getPositionName(),
				    leader!=null ? leader.getCnName() : "",
					isMoSystem,
					StringUtils.isEmpty(employee.getRemark()) ? "" : employee.getRemark(),
					nowDateStr,
					user.getEmployee().getCnName(),
					user.getDepart().getName()
			};
			String templetPropertie = "empQuitTemplet";
			String msg = empApplicationRegisterService.readEmailTemplet(params, templetPropertie);
	      
			
			sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
			sendMail.setText(msg);
			sendMail.setOaMail(SendMail.OA_MAIL_P);
			
			return sendMail;		
	}

	public SendMail sendUpdateEmpQuitTimeEmail(Employee employee,String email) throws IOException{
		Employee empTemplet=employeeMapper.getById(employee.getId());
		User user = userService.getCurrentUser();
		//发送邮件封装参数
		//日期转化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String nowDateStr = sdf.format(new Date());
		
		
		String quitTimeStrBefor = sdf.format(employee.getQuitUpdateBefore());
		String quitTimeStrAfter = sdf.format(employee.getQuitTimeUpdateTime());
		//汇报对象
		Employee leader = employeeMapper.getById(empTemplet.getReportToLeader());
		//公司
		Company company = companyMapper.getById(empTemplet.getCompanyId());
		//员工类型
		EmpType empType = empTypeMapper.getById(empTemplet.getEmpTypeId());
		//职位
		List<Employee> byEmpId = employeeMapper.getByEmpId(empTemplet.getId());
		//部门
		Depart depart = departMapper.getById(empTemplet.getDepartId());
		
		SendMail sendMail = new SendMail();
		sendMail.setReceiver(email);
		String isMoSystem="是";
		if("外包员工".equals(empType.getTypeCName()) || "实习生".equals(empType.getTypeCName())){
			isMoSystem="否";
			if("实习生".equals(empType.getTypeCName())){
				sendMail.setSubject("离职通知更新——"+employee.getCnName()+"("+empType.getTypeCName()+")");
			}else{
				sendMail.setSubject("离职通知更新——"+employee.getCnName()+"(外包)");
			}
		}else{
			isMoSystem="是";
			sendMail.setSubject("离职通知更新——"+employee.getCnName());
		}
		String params[]={
				StringUtils.isEmpty(quitTimeStrBefor) ? "" : quitTimeStrBefor,
				StringUtils.isEmpty(company.getName()) ? "" : company.getName(),
				StringUtils.isEmpty(employee.getCnName()) ? "" : employee.getCnName(),
				StringUtils.isEmpty(empType.getTypeCName())? "" : empType.getTypeCName(),
				StringUtils.isEmpty(quitTimeStrAfter) ? "" : quitTimeStrAfter,
				StringUtils.isEmpty(byEmpId.get(0).getEmpDepart().getDepart().getName()) ? "" : byEmpId.get(0).getEmpDepart().getDepart().getName(),
				StringUtils.isEmpty(byEmpId.get(0).getEmpPosition().getPosition().getPositionName()) ? "" : byEmpId.get(0).getEmpPosition().getPosition().getPositionName(),
				StringUtils.isEmpty(leader.toString()) ? "" : leader.getCnName(),
				isMoSystem,
				StringUtils.isEmpty(employee.getRemark()) ? "" : employee.getRemark(),
				nowDateStr,
				user.getEmployee().getCnName(),
				user.getDepart().getName()
		};
		String templetPropertie = "empQuitUpdateTimeTemplet";
		String msg = empApplicationRegisterService.readEmailTemplet(params, templetPropertie);
      
		
		sendMail.setSendStatus(SendMail.SEND_STATUS_NO);
		sendMail.setText(msg);
		sendMail.setOaMail(SendMail.OA_MAIL_P);
		
		return sendMail;			
	}

	// 根据行政人员 和 人事角色 发送邮件
	@Override
	public List<EmpPosition> sendMailPositionName() {
		List<String> positionNames = new ArrayList<String>();
		positionNames.add("人力资源及行政总监");
		positionNames.add("人力资源高级经理");
		positionNames.add("人力资源经理");
		positionNames.add("人力资源助理");
		positionNames.add("高级HRBP");
		positionNames.add("HRBP");
		positionNames.add("HRBP专员");
		positionNames.add("薪资福利高级专员");
		positionNames.add("企业文化专员");
		positionNames.add("行政经理");
		positionNames.add("行政专员");
		positionNames.add("行政前台");
		positionNames.add("IT经理");
		positionNames.add("IT高级专员");
		EmpPosition empPosition = new EmpPosition();
		empPosition.setPositionNames(positionNames);
		return empPositionMapper.getListByPositionName(empPosition);
	}

	@Override
	public Map<String, Object> getEmpByInfo(Employee employee) {

		return employeeMapper.getEmpByPhone(employee);
	}

	@Override
	public Map<String, Object> queryDepartHeadIdByEmpId(Long empId) {
		return employeeMapper.queryDepartHeadIdByEmpId(empId);
	}

	@Override
	public boolean isLeaderByEmpId(Long empId) {
		boolean result = false;
		Map<String, Object> resule = employeeMapper.queryDepartHeadIdByEmpId(empId);
		if (StringUtils.equalsIgnoreCase(empId.toString(), resule.get("departLeader").toString())) {
			result = true;
		}
		return result;
	}

	@Override
	public Map<String, String> queryEmpBaseInfoById(Long empId) {
		return employeeMapper.queryEmpBaseInfoById(empId);
	}

	@Override
	public boolean isPMDepart(Long proposerId) {
		Map<String, String> result = employeeMapper.queryEmpBaseInfoById(proposerId);
		boolean flag = false;
		
		Config configCondition = new Config();
		configCondition.setCode("abnormal_attendance_departHeader_power");
		List<Config> list = configService.getListByCondition(configCondition);
        for(Config config:list){
			if(config.getDisplayCode().equals(result.get("departCode"))){
				flag = true;
				break;
			}
		}
        
		return flag;
	}

	@Override
	public List<Employee> queryByNameOrCode(String nameOrCode) {
		return employeeMapper.queryByNameOrCode(nameOrCode);
	}

	@Override
	public List<Employee> getDepartHeaderList() {
		return employeeMapper.getDepartHeaderList();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> importEmployee(MultipartFile file) throws OaException, Exception {
		logger.info("导入员工基本数据-----开始");
		try {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			// 获取当前登录人
			User currentUser = userService.getCurrentUser();
			// 判断文件是否存在
			if (null == file) {
				logger.error("文件不存在！");
				throw new OaException("文件不存在！");
			}
			// 获得文件名
			String fileName = file.getOriginalFilename();
			InputStream inputStream = file.getInputStream();
			// 判断文件是否是excel文件
			if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
				logger.error(fileName + "不是excel文件");
				throw new OaException(fileName + "不是excel文件");
			}
			// 读取文件转换为实体类
			List<Object> excelToBean = ExcelImportUtil.excelToBean(ImportEmployeeExcel.class, inputStream, 0, 0);
			List<ImportEmployeeExcel> employeeList = (List<ImportEmployeeExcel>) (List) excelToBean;
			int count = employeeList.size(); // 插入总条数
			int failCount = 0; // 失败条数
			StringBuffer msg = new StringBuffer();
			Map<Long,String> updateEmpMap = new HashMap<Long,String>();
			//用于记录导入的员工识别码
			Map<String,String> identificationMap = new HashMap<String,String>();
			importTemple:
			for (int i = 0; i < employeeList.size(); i++) {
				ImportEmployeeExcel employeeInfo = employeeList.get(i);
				// 校验必填字段
				if (StringUtils.isBlank(employeeInfo.getCompany())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：公司不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(employeeInfo.getEmpType())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：员工类型不能为空");
					continue importTemple;
				}
				if (employeeInfo.getFirstEntryTime() == null) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：员工入职时间不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(employeeInfo.getCode())
						|| StringUtils.isBlank(employeeInfo.getCnName())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：员工编号或员工中文名不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(employeeInfo.getDepartName())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：部门不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(employeeInfo.getReportToLeader())
						|| StringUtils.isBlank(employeeInfo.getReportToLeaderCode())
						) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：汇报对象或汇报对象编号不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(employeeInfo.getDepartLeader())
						|| StringUtils.isBlank(employeeInfo.getDepartLeaderCode())
					) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：部门负责人或部门负责人编号不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(employeeInfo.getPositionTitle())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：职位不能为空");
					continue importTemple;
				}
				if (employeeInfo.getBirthDay() == null) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：员工出生日期不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(employeeInfo.getSex())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：性别不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(employeeInfo.getWorkType())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：工时种类不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(employeeInfo.getWhetherScheduling())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：是否排班不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(employeeInfo.getEmail())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：工作邮箱不能为空");
					continue importTemple;
				}
				if (StringUtils.isBlank(employeeInfo.getPhoneNum())) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：手机号格式不正确");
					continue importTemple;
				}
				/*if (employeeInfo.getFingerPrint() == null || employeeInfo.getFingerPrint() == 0L) {
					failCount++;
					msg.append("\n第" + (i + 1) + "条数据：指纹编号不能为空");
					continue importTemple;
				}*/
				// 判断该员工是否存在于系统中
				Long empId = employeeMapper.getEmpIdByCode(employeeInfo.getCode());
				if (empId != null) {
					msg.append("\n第" + (i + 1) + "条数据：该员工已存在于系统中");
					failCount++;
					continue importTemple;
				}
				//判断邮箱是否唯一
				Integer emailCount = employeeMapper.getEmailCount(employeeInfo.getEmail());
				if(emailCount > 0){
					msg.append("\n第" + (i + 1) + "条数据：该员工邮箱重复请修改邮箱");
					failCount++;
					continue importTemple;
				}
				
				String empIdentification = "";
				String birthdayStr = DateUtils.format(employeeInfo.getBirthDay(), DateUtils.FORMAT_SIMPLE);
				//判断导入的数据中是否有相同出生日期的员工
				boolean containsKey = identificationMap.containsKey(birthdayStr);
				if(containsKey){
					//本次已导入的数据中有相同生日的员工,识别码自增一
					empIdentification = Long.toString(Long.parseLong(identificationMap.get(birthdayStr))+1);
					identificationMap.put(birthdayStr, empIdentification);
				}else{
					//判断数据库中是否有前缀为此出生日期的员工标识码的员工
					List<Employee> sameBirthdayEmpList = employeeMapper.getEmpByBirthday(birthdayStr);
					if(sameBirthdayEmpList != null && sameBirthdayEmpList.size() > 0){
						for (Employee employee : sameBirthdayEmpList) {
							//员工识别号前缀(生日)相同并且姓名相同则认为是同一人
							if(employeeInfo.getCnName().equals(employee.getCnName())){
								//两者都相同的员工则跳过最外层循环，不插入此条数据
								msg.append("\n第" + (i + 1) + "条数据：该员工已存在于系统中");
								failCount++;
								continue importTemple;
							}
						}
						empIdentification = Long.toString(Long.parseLong(sameBirthdayEmpList.get(0).getIdentificationNum())+1);
						identificationMap.put(birthdayStr, empIdentification);
					}else{
						//已导入的数据与数据库中都不存在此生日的识别码，生成第一条
						empIdentification = DateUtils.format(employeeInfo.getBirthDay(), DateUtils.FORMAT_SIMPLE) + "001";
						identificationMap.put(birthdayStr, empIdentification);
					}
				}
				// 查询根据部门名称查询部门id，查询部门是否存在
				Long departId = employeeMapper.getDepartId(employeeInfo.getDepartName());
				// 不存在则新建部门
				if (departId == null) {
					Depart depart = new Depart();
					depart.setCompanyId(1L); // 公司id暂定为1
					depart.setName(employeeInfo.getDepartName());
					depart.setCreateTime(new Date());
					depart.setCreateUser(currentUser.getEmployeeId() + "");
					depart.setType(1); // 一级部门
					depart.setRank(1); // 排序
					depart.setIsShowInMo(0);
					depart.setDelFlag(0);
					// 查询数据库中最后一条数据的code+1
					String codeStr = employeeMapper.getDepartMaxCode();
					String code = (Integer.parseInt(codeStr) + 1) + "";
					depart.setCode(code);
					employeeMapper.saveDepart(depart);
					departId = depart.getId();
				}
				Long positionSeqId = 0L; // 职位序列id
				// 查询职位序列
				if (employeeInfo.getPoistionSeq() != null && !employeeInfo.getPoistionSeq().equals("")) {
					positionSeqId = employeeMapper.getPositionSeqId(employeeInfo.getPoistionSeq());
					if (positionSeqId == null) {
						// 如果不存在则新建职位序列
						CompanyPositionSeq companyPositionSeq = new CompanyPositionSeq();
						companyPositionSeq.setCompanyId(1L);
						companyPositionSeq.setName(employeeInfo.getPoistionSeq());
						// 获取数据库中最大排序加一
						Integer maxRank = employeeMapper.getPositionSeqMaxRank();
						companyPositionSeq.setRank(maxRank + 1);
						companyPositionSeq.setCreateTime(new Date());
						companyPositionSeq.setCreateUser(currentUser.getEmployeeId() + "");
						companyPositionSeq.setUpdateTime(new Date());
						companyPositionSeq.setUpdateUser(currentUser.getEmployeeId() + "");
						companyPositionSeq.setDelFlag(0);
						employeeMapper.savePositionSeq(companyPositionSeq);
						positionSeqId = companyPositionSeq.getId();
					}
				}
				// 查询职位信息
				Long positionId = employeeMapper.getPositionIdByName(employeeInfo.getPositionTitle());
				// 如果不存在则新建职位
				if (positionId == null) {
					Position position = new Position();
					position.setCompanyId(1L);
					position.setPositionName(employeeInfo.getPositionTitle());
					// 查询职级id
					Long positionLevelId = employeeMapper.getPositionLevelId(employeeInfo.getPoistionLevel());
					position.setPositionLevelId(positionLevelId == null ? null : positionLevelId);
					position.setPositionSeqId(positionSeqId == 0L ? null : positionSeqId);
					// 获取数据库中最大排序加一
					Integer maxRank = employeeMapper.getPositionMaxRank();
					position.setRank(maxRank + 1);
					position.setCreateTime(new Date());
					position.setCreateUser(currentUser.getEmployeeId() + "");
					position.setUpdateTime(new Date());
					position.setUpdateUser(currentUser.getEmployeeId() + "");
					position.setDelFlag(0);
					positionMapper.save(position);
					positionId = position.getId();
				}
				// 暂时没有子部门
				// 2.封装员工数据初始化员工信息
				Employee employee = new Employee();
				Long empTypeId = employeeMapper.getEmpTypeId(employeeInfo.getEmpType());
				employee.setEmpTypeId(empTypeId);
				employee.setCompanyId(1L); // 公司id暂定为1
				employee.setCode(employeeInfo.getCode());
				employee.setCnName(employeeInfo.getCnName());
				employee.setEngName(employeeInfo.getEngName());
				employee.setFirstEntryTime(employeeInfo.getFirstEntryTime());
				employee.setBirthday(employeeInfo.getBirthDay());
				employee.setIdentificationNum(empIdentification);
				employee.setSex(employeeInfo.getSex().equals("男") ? 0 : 1);
				employee.setOurAge(employeeInfo.getOurAge());
				//查询汇报对象
				Long reportToLeaderId = employeeMapper.getEmpIdByCode(employeeInfo.getReportToLeaderCode());
				if (reportToLeaderId != null) {
					employee.setReportToLeader(reportToLeaderId);
				}
				employee.setPositionTitle(employeeInfo.getPositionTitle());
				employee.setFingerprintId(employeeInfo.getFingerPrint());
				employee.setEmail(employeeInfo.getEmail());
				employee.setDelFlag(0);
				employee.setCreateTime(new Date());
				employee.setCreateUser(currentUser.getEmployeeId() + "");
				employee.setJobStatus(0);
				employee.setAutoCalculateLeave(1); // 暂时都设置自动计算年假
				// 查询工时类型和是否排班对应id
				Long workTypeId = employeeMapper.getWorkTypeId(employeeInfo.getWorkType());
				employee.setWorkType(workTypeId);
				Long whetherScheduling = employeeMapper.getWhetherSchedulId(employeeInfo.getWhetherScheduling());
				employee.setWhetherScheduling(whetherScheduling);
				employee.setMobile(employeeInfo.getPhoneNum());
				employee.setWorkAddressProvince(employeeInfo.getWorkAddressProvince());
				employee.setWorkAddressCity(employeeInfo.getWorkAddressCity());
				employee.setWorkAddressType(1);
				if("上海".equals(employeeInfo.getWorkAddressProvince())||"上海市".equals(employeeInfo.getWorkAddressCity())){
					employee.setWorkAddressType(0);
				}
				employeeMapper.save(employee);
				//获取插入员工的数据id
				empId = employee.getId();
				/***因为导入的表中员工汇报人可能在员工之后，所以未查到汇报人的信息，需记录未导入汇报人的员工，重新修改汇报人***/
				if(employee.getReportToLeader() == null){
					updateEmpMap.put(empId, employeeInfo.getReportToLeaderCode());
				}
				// 3.修改部门负责人
				// 判断部门负责人是否存在
				Integer empListByCodeCount = employeeMapper.getEmpListByCodeCount(employeeInfo.getDepartLeaderCode());
				if (empListByCodeCount > 0) {
					employeeMapper.updateDepartLeader(employeeInfo.getDepartLeaderCode(), departId);
				}
				// 4.初始化通信证
				//获得邮箱前缀作为登录用户用户名
				String email = employeeInfo.getEmail();
				String[] userName = email.split("@");
				employeeMapper.initPass(userName[0], empId,
						currentUser.getEmployeeId() + "");
				// 5.初始化员工部门信息
				employeeMapper.initEmpAndDepart(empId, departId, currentUser.getEmployeeId() + "");
				// 6.初始化员工职位信息
				employeeMapper.initPosition(empId, positionId,
						currentUser.getEmployeeId() + "");
				// 7.绑定员工指纹id
				if(employeeInfo.getFingerPrint() != null){
					String OA_SERVICE_URL = configCacheManager.getConfigDisplayCode("MO_SERVICE_URL");
					logger.info("绑定指纹id:"+employeeInfo.getFingerPrint()+"，绑定员工id:"+empId);
					HashMap<String, String> paramMap = new HashMap<String,String>();
					paramMap.put("fingerPrint", employeeInfo.getFingerPrint().toString());
					paramMap.put("empId", empId.toString());
					
					HttpRequest req = new HttpRequest.Builder().url(OA_SERVICE_URL+"/attnUsers/bindFingerPrint.htm").post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
		    				ContentCoverter.formConvertAsString(paramMap)).build();
		    		HttpResponse rep = client.sendRequest(req);
					String response = rep.fullBody();
					
					if("fail".equals(response)){
						logger.error("员工="+employee.getId()+"与打卡机关系绑定失败。。。");
         			}
				}
				//8.创建sso账号
				HashMap<String, String> paramExistMap = new HashMap<String,String>();
     			paramExistMap.put("searchType", "1");
     			paramExistMap.put("searchValue", userName[0]);
				
				HttpRequest req2 = new HttpRequest.Builder().url(ConfigConstants.ULEACCOUNT_IS_EXIST).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
	    				ContentCoverter.formConvertAsString(paramExistMap)).build();
	    		HttpResponse rep2 = client.sendRequest(req2);
				String responseExist = rep2.fullBody();
				
				Map<?, ?> responseExistMap = JSONUtils.readAsMap(responseExist);
			    String resultCodeExist = (String)responseExistMap.get("errorCode");
			    //如果不存在则新建sso账号
	            if("SSO_USER_NOT_FOUND".equals(resultCodeExist)){
	            	HashMap<String, String> paramMap1 = new HashMap<String,String>();
					paramMap1.put("staffId", "-1");
					paramMap1.put("name", employeeInfo.getCnName());
					paramMap1.put("username", userName[0]);
					paramMap1.put("email", email);
					paramMap1.put("telephone", employeeInfo.getPhoneNum());
					paramMap1.put("status", "2");
					logger.info("新建通行证url="+ConfigConstants.ULEACCOUNT_ADD);
					
					HttpRequest req1 = new HttpRequest.Builder().url(ConfigConstants.ULEACCOUNT_ADD).post(HttpRequest.HttpMediaType.FORM_URLENCODE, 
		    				ContentCoverter.formConvertAsString(paramMap1)).build();
		    		HttpResponse rep1 = client.sendRequest(req1);
					String response1 = rep1.fullBody();
					
					logger.info("新建通行证返回="+response1);
	            }else{
	            	logger.info("员工通行证start:username="+userName[0]+"已存在");
	            }
			}
			msg.append("\n本次共导入" + count + "条数据，导入失败" + failCount + "条"); // 返回信息
			logger.info(msg.toString());
			logger.info("导入员工基本数据-----结束");
			resultMap.put("resultMsg", msg.toString());
			resultMap.put("updateEmpMap", updateEmpMap);
			return resultMap;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}

	}
	
	//重新绑定员工汇报对象
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateEmpReportLeader(Map<Long, String> updateEmpMap) {
		if(updateEmpMap != null && updateEmpMap.size() > 0){
			for (Entry<Long, String> map : updateEmpMap.entrySet()) {
				Long reportToLeaderId = employeeMapper.getEmpIdByCode(map.getValue());
				employeeMapper.updateReporterToLeader(map.getKey(),reportToLeaderId);
			}
		}
		
	}

	@Override
	public boolean isTDDepart(Long proposerId) {
		Map<String, String> result = employeeMapper.queryEmpBaseInfoById(proposerId);
		if ("117".equals(result.get("departCode"))||"138".equals(result.get("departCode"))||"136".equals(result.get("departCode"))) {
			return true;
		}
		return false;
	}

	@Override
	public boolean hasPowerSuperior(Long employeeId) {
		List<Depart> departList = departMapper.getAllDepartByLeaderId(employeeId);
		//判断是否是部门负责人
		if(departList!=null&&departList.size()>0){
			return true;
		}
		//判断职位序列：总监，副总监，VP，COO
		Employee seq = employeeMapper.getById(employeeId);
		if(seq!=null&&StringUtils.isNotBlank(seq.getPositionSeq())){
			if("总监".equals(seq.getPositionSeq())||"副总监".equals(seq.getPositionSeq())
					||"VP".equals(seq.getPositionSeq())||"COO".equals(seq.getPositionSeq())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取所有汇报对象
	 */
	@Override
	public List<Employee> getAllReportPerson() {
		//1.所有部门负责人
		//2.所有职位为M级的员工
		List<Employee> reporterList = employeeMapper.getAllReportPerson();
		return reporterList;
	}
	
	/**
	 * 保存汇报对象
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> saveReportPerson(String empIds, String employeeLeader) throws OaException {
		Map<String, Object> result = Maps.newHashMap();
		if(StringUtils.isBlank(empIds)||StringUtils.isBlank(employeeLeader)){
			throw new OaException("请选择员工与汇报对象");
		}
		String[] split = empIds.split(",");
		if(split == null){
			throw new OaException("请选择员工与汇报对象");
		}
		for (String empId : split) {
			Long emp = Long.parseLong(empId);
			Long leader = Long.parseLong(employeeLeader);
			employeeMapper.updateReporterToLeader(emp, leader);
		}
		result.put("success", true);
		result.put("message", "保存成功！");
		return result;
	}

	@Override
	public List<String> getAssigneeIdListByProcinstId(String procinstId) {
		return employeeMapper.getAssigneeIdListByProcinstId(procinstId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> updateEmpDepart(Employee employee) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> map =new HashMap<String, Object>();
		if (StringUtils.isBlank(employee.getNoticeStr())) {
			map.put("msg", "请选择员工");
			return map;
		}
		if (StringUtils.isBlank(employee.getFirstDepart().toString())) {
			map.put("msg", "请选择部门");
			return map;
		}
		 String updateEmpDepartIds = employee.getNoticeStr();
		 String[] updateEmpDepartIdsArray = updateEmpDepartIds.split(",");
		 for (int i = 0; i < updateEmpDepartIdsArray.length; i++) {
			 EmpDepart depart =new EmpDepart();
			 depart.setEmployeeId(Long.parseLong(updateEmpDepartIdsArray[i]));
			 depart.setDepartId(employee.getFirstDepart().longValue());
			 empDepartMapper.updateByEmployeeId(depart);
//			 //修改员工部门  去除之前部门的所有部门角色权限
//			 User userIds = userMapper.getByEmployeeId(Long.parseLong(updateEmpDepartIdsArray[i]));
//			 RabcUserRole userRole=new RabcUserRole();
//			 userRole.setUserId(userIds.getId());
//			 userRole.setDelFlag(1);
//			 userRole.setUpdateTime(new Date());
//			 userRole.setUpdateUser(userService.getCurrentUser().getUserName());
//			 rabcUserRoleMapper.updateUserRole(userRole);
		}
		 map.put("msg", "修改成功");
		return map;
	}
	
	//根据工时类型汇报对象部门查询所有员工
	@Override
	public List<Employee> getAllEmpByWorkTypeAndLeaderAndDepart(RequestParamQueryEmpCondition requestParamQueryEmpByCondition) {
		List<Employee> empList = employeeMapper.getAllEmpByWorkTypeAndLeaderAndDepart(requestParamQueryEmpByCondition);
		return empList;
	}

	@Override
	public Integer getStandardEmpCountByCount(RequestParamQueryEmpCondition requestParamQueryEmpByCondition) {
		Integer count = employeeMapper.getStandardEmpCountByCount(requestParamQueryEmpByCondition);
		return count;
	}

	@Override
	public Integer isQuitThisDay(Long empId, Date delayDate) {
		Integer count = employeeMapper.isQuitThisDay(empId,delayDate);
		return count;
	}

	@Override
	public List<Long> getSubEmployeeList(Long employeeId) {
		List<Long> subEmployeeIdList = new ArrayList<Long>();
		if(employeeId!=null){
			//如果员工是职位是人事，默认查询全公司员工
			Position position = positionService.getByEmpId(employeeId);
			if(position!=null&&("人力资源及行政总监".equals(position.getPositionName())||"企业文化专员".equals(position.getPositionName())
					||"人力资源高级经理".equals(position.getPositionName())||"HRBP专员".equals(position.getPositionName())
					||"高级HRBP".equals(position.getPositionName())||"HRBP".equals(position.getPositionName())
					||"HRBP助理".equals(position.getPositionName())||"人力资源助理".equals(position.getPositionName())
					||"薪资福利高级专员".equals(position.getPositionName())||"高级企业文化专员".equals(position.getPositionName()))){
				return subEmployeeIdList;
			}
			int count = 0;
			//根据汇报对象查询所有下属
			List<Long> reportToLeaderList = new ArrayList<Long>();
			reportToLeaderList.add(employeeId);
			while(true){
				count = count + 1;
				List<Employee> subList1 = employeeMapper.getByReportToLeaders(reportToLeaderList);
				reportToLeaderList.clear();
				if(subList1!=null&&subList1.size()>0){
					for(Employee data:subList1){
						subEmployeeIdList.add(data.getId());
						reportToLeaderList.add(data.getId());
					}
				}else{
					break;
				}
				if(count>10){
					break;
				}
			}
			//如果是部门负责人，查询出部门所有员工
			List<Depart> departList = departMapper.getAllDepartByLeaderId(employeeId);
			for(Depart depart:departList){
				List<Employee> subList2 = employeeMapper.getListByDepartId(depart.getId());
				for(Employee data:subList2){
					subEmployeeIdList.add(data.getId());
				}
			}
			
		}
		return subEmployeeIdList;
	}

	@Override
	public List<Employee> getListByIds(List<Long> ids) {
		Employee employee = new Employee();
		employee.setIds(ids);
		List<Employee> list = employeeMapper.getListByCondition(employee);
		return list;
	}

	@Override
	public PageModel<Employee> getSelectDivList(Employee employee) {
		List<Integer> departList = departService.getDepartList(employee.getFirstDepart(), employee.getSecondDepart());
		employee.setDepartList(departList);

		int page = employee.getPage() == null ? 0 : employee.getPage();
		int rows = employee.getRows() == null ? 0 : employee.getRows();

		PageModel<Employee> pm = new PageModel<Employee>();
		pm.setPageNo(page);
		pm.setPageSize(rows);
		Integer total = employeeMapper.getCount(employee);
		pm.setTotal(total);
		employee.setOffset(pm.getOffset());
		employee.setLimit(pm.getLimit());
		List<Employee> employees = employeeMapper.getPageList(employee);
		// 获得部门名称（一级部门+"_"+当前部门）
		getDepartName(employees);

		pm.setRows(employees);
		return pm;
	}
	
	@Override
	public Map<String, Object> getInfoByCode(String code) {
		Map<String, Object> result = new HashMap<String, Object>();
		Employee employee = employeeMapper.getInfoByCode(code);
		if(employee!=null&&employee.getQuitTime()==null){
			result.put("success", false);
			result.put("message", "员工尚未填写离职日期！");
			return result;
		}
		result.put("employee", employee);
		if(employee!=null&&employee.getCompanyId()!=null){
			Company companyP = new Company();
			companyP.setId(employee.getCompanyId());
			Company company = companyService.getByCondition(companyP);
			result.put("company", company);
		}
		if(employee!=null&&employee.getReportToLeader()!=null){
		    Employee reportToLeader = employeeMapper.getById(employee.getReportToLeader());
		    result.put("reportToLeader", reportToLeader!=null?reportToLeader.getCnName():"");
		}
		if(employee!=null&&employee.getDepartLeaderId()!=null){
			Employee departLeader = employeeMapper.getById(employee.getDepartLeaderId());
		    result.put("departLeader", departLeader!=null?departLeader.getCnName():"");
		}
		result.put("success", true);
		return result;
	}

	@Override
	public Employee getEmployeeByCode(String code) {
		return employeeMapper.getInfoByCode(code);
	}

	@Override
	public Map<String,Object> getIdentificationNum(String cnName, Date birthDate) {
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		if(birthDate==null){
			result.put("isRepeat", false);
			result.put("num", "");
			return result;
		}
		
		String identificationNum = DateUtils.format(birthDate, DateUtils.FORMAT_SIMPLE)+"001";//默认识别号
		
		//查看同一出生日期，同一中文名的员工，如果存在，已他的员工识别号为准
		List<Employee> list = employeeMapper.getIdentificationNum(cnName, birthDate);
		if(list!=null&&list.size()>0){
			identificationNum = list.get(0).getIdentificationNum();
			result.put("isRepeat", true);
			result.put("num", identificationNum);
			result.put("repeatList", list);
			return result;
		}
		
		//同一出生日期,存在，根据同一出身日期人数生成默认识别号
		List<Employee> sameBirthDayList = employeeMapper.getListByBirthDate(birthDate);
		int i = 1;
		for(Employee data:sameBirthDayList){
			if(StringUtils.isNotBlank(data.getIdentificationNum())){
				int num = Integer.valueOf(data.getIdentificationNum().substring(9,11));
				if(num>i){
					i = num;
				}
			}
		}
		
		if(sameBirthDayList!=null&&sameBirthDayList.size()>0){
			identificationNum = DateUtils.format(birthDate, DateUtils.FORMAT_SIMPLE)+genetateNum(3,i+1);
		}
		
		result.put("isRepeat", false);
		result.put("num", identificationNum);
		return result;
	}
	
	public String genetateNum(Integer length, Integer number) {
		String f = "%0" + length + "d";
		return String.format(f, number);
	}

	@Override
	public List<Long> getSubEmployeeList(Long employeeId, Long departId,boolean isGetAll) {
		List<Long> subEmployeeIdList = new ArrayList<Long>();
		if(employeeId!=null){
			
			Depart depart = departMapper.getByLeaderAndId(departId, employeeId);
			//特殊角色/部门负责人获取整个部门数据
			if(isGetAll||depart!=null){
				List<Employee> subList2 = employeeMapper.getListByDepartId(departId);
				for(Employee data:subList2){
					subEmployeeIdList.add(data.getId());
				}
				return subEmployeeIdList;
			}
			//获取下属数据
			List<Employee> subList1 = employeeMapper.getByReportToLeadersAndDepartId(employeeId,departId);
			if(subList1!=null&&subList1.size()>0){
				for(Employee data:subList1){
					subEmployeeIdList.add(data.getId());
				}
			}
			
		}
		return subEmployeeIdList;
	}

	@Override
	public List<Map<String, Object>> getEmployeeSeqList() {
		return employeeMapper.getEmployeeSeqList();
	}

	@Override
	public String getMaxCode(String prefix) {
	    Integer maxCode = employeeMapper.getMaxCodeByPrefix(prefix);
	    if(maxCode==null){
	        maxCode = 0;
	    }
	    String code = prefix+generateCode(4,maxCode+1);
		return code;
	}
	
	public static String generateCode(Integer length, Integer number) {
		String f = "%0" + length + "d";
		return String.format(f, number);
	}

	@Override
	public List<Employee> getListByCodes(List<String> codeList) {
		return employeeMapper.getListByCodes(codeList);
	}

	@Override
	public List<Employee> getByIdentificationNum(String identificationNum) {
		return employeeMapper.getByIdentificationNum(identificationNum);
	}
}
