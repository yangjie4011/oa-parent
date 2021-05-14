package com.ule.oa.web.util;

public class ReturnUrlUtil {
	public static String getReturnUrl(String urlType) {
		String returnUrl = "/login/index.htm";//返回首页
		if(urlType == null || urlType.equals("")) {
			returnUrl = "/login/index.htm";
		}  else if(urlType.equals("1")) {
			returnUrl = "/login/index.htm";
		} else if(urlType.equals("2")) {
			returnUrl = "/login/indexAdd.htm";//返回更多页面
		} else if(urlType.equals("3")) {
			returnUrl = "/empAttn/index.htm";//我的考勤页面
		} else if(urlType.equals("4")) {
			returnUrl = "/empLeave/myLeaveView.htm";//我的假期页面
		} else if(urlType.equals("5")) {
			returnUrl = "/ruProcdef/my_examine.htm";//协助审批
		} else if(urlType.equals("6")) {
			returnUrl = "/runTask/index.htm";//我的申请
		} else if(urlType.equals("7")) {
			returnUrl = "/login/allRun.htm";//发起申请
		}else if(urlType.equals("8")) {
			returnUrl = "/employee/indexPerson.htm";//返回个人中心
		}else if(urlType.equals("9")) {
			returnUrl = "/employeeApp/index.htm?urlType=2";//返回员工查询页面
		}else if(urlType.equals("10")){
			returnUrl = "/runTask/allExamine.htm";//返回全部审批页面
		}else if(urlType.equals("11")){
			returnUrl = "/employeeClass/myClassView.htm";//返回我的日历
		}
		
		return returnUrl;
	}
}
