/*package com.ule.oa.web.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.web.subject.WebSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ule.oa.base.po.Company;
import com.ule.oa.base.po.Depart;
import com.ule.oa.base.po.EmpDepart;
import com.ule.oa.base.po.EmpPosition;
import com.ule.oa.base.po.Employee;
import com.ule.oa.base.po.Position;
import com.ule.oa.base.po.RuProcdef;
import com.ule.oa.base.po.User;
import com.ule.oa.base.service.CompanyService;
import com.ule.oa.base.service.DepartService;
import com.ule.oa.base.service.EmpDepartService;
import com.ule.oa.base.service.EmpPositionService;
import com.ule.oa.base.service.EmployeeService;
import com.ule.oa.base.service.PositionService;
import com.ule.oa.base.service.RuProcdefService;
import com.ule.oa.base.service.UserService;
import com.ule.oa.common.spring.SpringContextUtils;
import com.ule.oa.common.utils.SessionUtils;
import com.ule.oa.common.utils.http.IPUtils;
import com.ule.oa.web.session.HttpSessionCacheManager;
import com.ule.oa.web.session.HttpSessionCacheWrapper;
import com.ule.oa.web.session.SessionContext;

*//**
 * oa发送邮件访问回调url过滤器
 * 
 * @author wufei
 *//*
public class OaSendMailFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String sessionId;

	private String cookieDomain;

	private String cookiePath;

	private String ignores;
	*//**
	 * session缓存管理对象
	 *//*
	private HttpSessionCacheManager sessionCacheManager;

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		String str = request.getServletPath();
		if ("/runTask/handle.htm".equals(str)) {
			String callbackMaile = request.getParameter("callbackMail"); // 随身邮接口返回的邮箱数据
			String ruProcdefId = request.getParameter("ruProcdefId");
			RuProcdefService ruProcdefService = SpringContextUtils.getContext()
					.getBean(RuProcdefService.class);
			RuProcdef ruProcdef = ruProcdefService.getById(Long
					.valueOf(ruProcdefId));
			EmployeeService employeeService = SpringContextUtils.getContext()
					.getBean(EmployeeService.class);
			Employee emp = employeeService.getById(ruProcdef.getAssigneeId());
			if (emp != null && emp.getEmail() != null
					&& emp.getEmail().equals(callbackMaile)) {
//				doGetAuthenticationInfo(emp);
				filterChain.doFilter(request, response);
			} else {
				// 如果验证失败,则跳转404
				RequestDispatcher requestDispatcher = request
						.getRequestDispatcher("/error/404.jsp");
				requestDispatcher.forward(request, response);
			}
			
			 * String encodeEmail = request.getParameter("value");
			 * //解密前获取的email数据 String decodeEmail = UleRsa.decode(encodeEmail);
			 * //解密后获取的email数据 String callbackMaile =
			 * request.getParameter("callbackMail"); //随身邮接口返回的邮箱数据
			 * //如果给的邮箱和返回的邮箱一致,则自动登录跳转审批页面 if(decodeEmail != null &&
			 * decodeEmail.equals(callbackMaile)) {
			 * filterChain.doFilter(request, response); } else { //如果验证失败,则跳转404
			 * RequestDispatcher requestDispatcher =
			 * request.getRequestDispatcher("/error/404.jsp");
			 * requestDispatcher.forward(request,response); }
			 
			// filterChain.doFilter(request, response);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	protected void doGetAuthenticationInfo(Employee emp) {
		// 当前用户不存在
		User user = new User();
		user.setEmployeeId(emp.getId());
		UserService userService = SpringContextUtils.getContext().getBean(
				UserService.class);
		user = userService.getByCondition(user);
		// 当前用户不存在
		if (null == user) {
			logger.error("用户{}信息不存在", emp.getCnName());
			throw new UnknownAccountException();
		}
		// 取出公司编码
		Company company = new Company();
		company.setId(user.getCompanyId());
		CompanyService companyService = SpringContextUtils.getContext()
				.getBean(CompanyService.class);
		company = companyService.getByCondition(company);
		if (null == company) {// 用户不存在
			logger.error("用户{}公司信息不存在", emp.getCnName());
			throw new UnknownAccountException();
		}

		if (null != emp) {
			// 封装员工信息
			user.setEmployee(emp);

			// 封装部门信息
			EmpDepart ed = new EmpDepart();
			ed.setEmployeeId(emp.getId());
			EmpDepartService empDepartService = SpringContextUtils.getContext()
					.getBean(EmpDepartService.class);
			EmpDepart empDepart = empDepartService.getByCondition(ed);
			if (null != empDepart && null != empDepart.getDepartId()) {
				DepartService departService = SpringContextUtils.getContext()
						.getBean(DepartService.class);
				Depart depart = departService.getById(empDepart.getDepartId());
				if (null != depart) {
					user.setDepart(depart);
				}
			}

			// 封装职位信息
			EmpPosition ep = new EmpPosition();
			ep.setEmployeeId(emp.getId());
			EmpPositionService empPositionService = SpringContextUtils
					.getContext().getBean(EmpPositionService.class);
			EmpPosition empPosition = empPositionService.getByCondition(ep);
			if (null != empPosition && null != empPosition.getPositionId()) {
				PositionService positionService = SpringContextUtils
						.getContext().getBean(PositionService.class);
				Position position = positionService.getById(empPosition
						.getPositionId());
				if (null != position) {
					user.setPosition(position);
				}
			}
		}
		// 用户存在
		user.setCompany(company);

		// 记住密码
		ServletRequest req = ((WebSubject) SecurityUtils.getSubject())
				.getServletRequest();
		HttpServletRequest request = (HttpServletRequest) req;
		ServletResponse res = ((WebSubject) SecurityUtils.getSubject())
				.getServletResponse();
		HttpServletResponse response = (HttpServletResponse) res;
		// 记住用户名、密码功能(注意：cookie存放密码会存在安全隐患)
		String remFlag = request.getParameter("remFlag");
		String loginInfo = user.getUserName() + "!!||" + user.getPassword()
				+ "!!||" + remFlag;
		if ("1".equals(remFlag)) { // "1"表示用户勾选记住密码
			Cookie userCookie = new Cookie("loginInfo", loginInfo);

			userCookie.setMaxAge(30 * 24 * 60 * 60); // 存活期为一个月 30*24*60*60
			userCookie.setPath("/");
			response.addCookie(userCookie);
		} else {// 移除缓存
			Cookie cookie = new Cookie("loginInfo", null);
			cookie.setMaxAge(0);
			cookie.setPath("/");
			response.addCookie(cookie);
		}
		logger.info("用户{}登录成功，登录ip={}", emp.getCnName(),
				IPUtils.getIpAddress(request));

		JdbcRealm jdbcRealm = SpringContextUtils.getContext().getBean(
				JdbcRealm.class);
		// 清除之前的权限
		jdbcRealm.clearCachedAuthorizationInfo();
		jdbcRealm.clearCachedAuthenticationInfo();

		// sessionId值
		String sid = SessionUtils.getSessionId(request);

		// 生成sessionId值
		if (sid == null) {
			sid = generateSid();
			addCookie(response, sid);
		}

		// 获取缓存session
		HttpSessionCacheWrapper session = sessionCacheManager.get(sid);

		// 设置到线程中
		SessionContext.setContext(new SessionContext(sid, session));
		// 更新到缓存
		sessionCacheManager.put(sid, SessionContext.getContext().getSession());

	}

	private void addCookie(HttpServletResponse response, String sid) {
		Cookie cookie = new Cookie(sessionId, sid);
		cookie.setMaxAge(-1);
		if (cookieDomain != null && cookieDomain.length() > 0) {
			cookie.setDomain(cookieDomain);
		}
		cookie.setPath(cookiePath);
		response.addCookie(cookie);
	}

	private String generateSid() {
		return java.util.UUID.randomUUID().toString().replace("-", "");
	}

	private boolean isIgnore(HttpServletRequest request) {
		return request.getRequestURI().startsWith(
				request.getContextPath() + ignores);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		ignores = filterConfig.getInitParameter("ignores");
		sessionId = filterConfig.getInitParameter("sessionId");
		cookieDomain = filterConfig.getInitParameter("cookieDomain");
		cookiePath = filterConfig.getInitParameter("cookiePath");
		if (null == cookiePath || cookiePath.length() == 0) {
			cookiePath = filterConfig.getServletContext().getContextPath();
		}

		sessionCacheManager = SpringContextUtils.getContext().getBean(
				HttpSessionCacheManager.class);
	}

	@Override
	public void destroy() {
		sessionCacheManager = null;
	}
}
*/