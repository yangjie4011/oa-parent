package com.ule.oa.common.utils;

import java.util.regex.Pattern;

import com.ule.oa.common.spring.config.CustomPropertyPlaceholderConfigurer;

/**
  * @ClassName: ConfigConstants
  * @Description: 系统常量
  * @author minsheng
  * @date 2017年5月11日 下午1:40:08
 */
public class ConfigConstants {
	//文件上传服务器
	public final static String QINIU_HOST = (String)CustomPropertyPlaceholderConfigurer.getProperty("upload.qiniu.url");
	//修改员工信息 uleAccount.modifyInfo.url
	public final static String MODIFY_INFO_URL = (String)CustomPropertyPlaceholderConfigurer.getProperty("uleAccount.modifyInfo.url");
	//获取新的sso账号
	public final static String ULEACCOUNT_EXIST = (String)CustomPropertyPlaceholderConfigurer.getProperty("uleAccount.exist.url");
	//获取新的sso账号
	public final static String ULEACCOUNT_IS_EXIST = (String)CustomPropertyPlaceholderConfigurer.getProperty("uleAccount.isexist.url");
	//员工通行证新增
	public final static String ULEACCOUNT_ADD = (String)CustomPropertyPlaceholderConfigurer.getProperty("uleAccount.add.url");
	//员工通行证新增
	public final static String ULEACCOUNT_QUIT = (String)CustomPropertyPlaceholderConfigurer.getProperty("uleAccount.quit.url");
	//考勤机user新增----已废弃，oaService.saveBacth.url=http://oa-oaService.http.beta.uledns.com/oaService/attnUsers/saveBacth.htm
	//public final static String OASERVICE_USER_ADD = (String)CustomPropertyPlaceholderConfigurer.getProperty("oaService.saveBacth.url");
	//oa审批邮箱账号
	public final static String OA_SENDMAIL_USERNAME = (String)CustomPropertyPlaceholderConfigurer.getProperty("uleOa.sendMail.userName");
	//oa审批邮箱密码
	public final static String OA_SENDMAIL_PASSWORD = (String)CustomPropertyPlaceholderConfigurer.getProperty("uleOa.sendMail.password");
	//oa调用随身邮接口
	public final static String OA_SENDMAIL_URL = (String)CustomPropertyPlaceholderConfigurer.getProperty("uleOa.sendMail.url");
	
	//public final static String OA_URL = (String)CustomPropertyPlaceholderConfigurer.getProperty("OA_URL");
	
	public final static String OA_RSAKEY = (String)CustomPropertyPlaceholderConfigurer.getProperty("uleoa.rsakey");
	
	public final static String WEBMAIL_TOKEN = (String)CustomPropertyPlaceholderConfigurer.getProperty("WEBMAIL_TOKEN");
	
	public final static String VERIFY_OA_TOKEN = (String)CustomPropertyPlaceholderConfigurer.getProperty("verify_oa_token");
	
	//用户默认头像地址
	public final static String HEAD_PICTURE_URL = (String)CustomPropertyPlaceholderConfigurer.getProperty("head.picture.url");
	//10:入职登记申请、20:离职申请、30:延时工作申请、40:异常考勤、41:(产品管理部)异常考勤、50:排班、60-请假申请、70-外出申请、80-出差申请（普通员工）、
		//81-出差申请（部门负责人）、90-总结报告（普通员工）、91-总结报告（部门负责人）、100-延迟工作晚到,110-值班,120-下属考勤消异常,140-消下属缺勤
	public static final String ENTRY_CODE = "10";
//	public static final String LEFE_CODE = "20";
	public static final String OVERTIME_CODE = "30";
	public static final String ABNORMAL_CODE = "40";
//	public static final String RUN_CODE_41 = "41";
	public static final String LEAVE_CODE = "60";
	public static final String OUTGOING_CODE = "70";
	public static final String BUSINESS_CODE = "80";
	public static final String BUSINESSREPORT_CODE = "90";
	public static final String OVERTIME_LETE_CODE = "100";
	public static final String SE_CODE = "50";
	public static final String RUN_CODE_110 = "110";
	public static final String REMOVESUBABSENCE_CODE = "140";
	public static final String WORK_LOG_CODE = "150";//员工日志
//	public static final String ATTN_CODE = "120";
	//上传文件的服务器路径
	public final static String UPLOAD_FILE_URL = (String)CustomPropertyPlaceholderConfigurer.getProperty("file_url");
	
	//获取环境
	public final static String ENV = (String)CustomPropertyPlaceholderConfigurer.getProperty("cache.env");
	
	//否(INTEGER类型)
	public final static Integer IS_NO_INTEGER = 0;
	
	//是(integer类型)
	public final static Integer IS_YES_INTEGER = 1;
	
	//有效
	public final static Integer IS_ACTIVE_INTEGER = 0;
	
	//无效
	public final static Integer IS_NOT_ACTIVE_INTEGER = 1;
	
	//默认版本号
	public final static Long DEFAULT_VERSION = 0L;
	//时间格式
	public final static String DATEFORMAT="yyyy-MM-dd HH:mm";
	
	//假期类型
	public final static Integer LEAVE_TYPE_1 = 1;//年假
	public final static Integer LEAVE_TYPE_2 = 2;//病假
	public final static Integer LEAVE_TYPE_3 = 3;//婚假
	public final static Integer LEAVE_TYPE_4 = 4;//哺乳假
	public final static Integer LEAVE_TYPE_5 = 5;//调休
	public final static Integer LEAVE_TYPE_6 = 6;//产前假
	public final static Integer LEAVE_TYPE_7 = 7;//产假
	public final static Integer LEAVE_TYPE_8 = 8;//流产假
	public final static Integer LEAVE_TYPE_9 = 9;//陪产假
	public final static Integer LEAVE_TYPE_10 = 10;//丧假
	public final static Integer LEAVE_TYPE_11 = 11;//事假
	public final static Integer LEAVE_TYPE_12 = 12;//其它
	public final static Integer LEAVE_TYPE_13 = 13;//调休小时数管理
	
	protected final static Integer[] OTHERLEAVE_TYPE = {3,4,6,7,8,9,10,11,12};
	
	
	public static Integer[] getOtherleaveType() {
		return OTHERLEAVE_TYPE;
	}

	//二级分类
	public final static Integer CATEGORY_0 = 0;//总的年假
	public final static Integer CATEGORY_1 = 1;//法定年假
	public final static Integer CATEGORY_2 = 2;//福利年假
	//申请单类型
	public final static String BILL_TYPE_LEAVE = "请假申请单";
	public final static String BILL_TYPE_OVERTIME = "加班申请单";
	//时间单位类型
	public final static String TIME_UNIT_DAY = "天";
	public final static String TIME_UNIT_HOUR = "小时";
	//年假类型
	public final static String ANNUAL_LEAVE_LEGAL = "法定";
	public final static String ANNUAL_LEAVE_BENEFIT = "福利";
	//病假类型
	public final static String PAID_SICK_LEAVE = "带薪";
	public final static String UNPAID_SICK_LEAVE = "非带薪";
	//在职
	public final static Integer JOB_STATUS_0 = 0;
	//离职
	public final static Integer JOB_STATUS_1 = 1;
	//待离职
	public final static Integer JOB_STATUS_2 = 2;
	
	public final static String LEAVE_RADUIX_WELFARE = "leaveRaduixWelfare";
	//福利假期基数配置
	
	public final static String API = "API";
	
	//每天自动计算年假基数
	public final static int AUTO_CAL_YEAR_RADUIX = 1;
	//当年不自动计算年假基数
	public final static int NOT_CAL_YEAR_RADUIX_CURRENT_YEAR = 0;
	//永久计算年假基数
	public final static int NOT_CAL_YEAR_RADUIX_FOREVER = 2;
	
	
	//年假未计算状态
	public final static Integer CAL_STATUS_0 = 0;
	
	//年假计算中状态
	public final static Integer CAL_STATUS_1 = 1;
	
	//年假已计算状态
	public final static Integer CAL_STATUS_2 = 2;
	
	//年假计算失败状态
	public final static Integer YEAR_CAL_STATUS_3 = 3;
	
	//2017年默认上半年带薪病假为10天
	public final static Double DEFAULT_SICK_2017_PRE = 10.0;
	
	//默认带薪病假为5天
	public final static Double DEFAULT_SICK = 5.0;
	
	//2017年默认带薪病假为基数为7.5
	public final static Double DEFAULT_SICK_RADUIX_2017 = 7.5;
	
	//法定年假和福利年假默认基数都是5
	public final static Double DEFAULE_YEAR = 5.0;
	
	//假期计算失败后，发送预警短信
	public final static String LEAVE_CAL_FAIL_WARN_PHONE = "leaveCalFailWarnPhone";
	
	//假期明细报表计算失败后，发送预警短信
	public final static String LEAVE_APPLY_WARN_PHONE = "leaveApplyWarnPhone";
	
	//年假透支限制
	public final static String YEAR_LEAVE_OVERDRAW_FLAG = "year_leave_overdraw_flag";
	public final static String YEAR_LEAVE_OVERDRAW_NO = "0";
	public final static String YEAR_LEAVE_OVERDRAW_YES = "1";
	
	//任务类型
	public final static Integer TASK_TYPE_0 = 0;//计算司龄
	public final static Integer TASK_TYPE_1 = 1;//计算年假
	public final static Integer TASK_TYPE_2 = 2;//计算病假
	
	//文件上传类型
	public final static String PIC_UPLOAD_TYPE = "jpg,png,bmp,jpeg";
	
	public static final String ADMIN_REDIS_PRE = "ADMIN";//后台缓存前缀
	
	public final static Integer TOKEN_CHECK_CODE = 9999;
	
	public final static Integer ASK_STATUS = 0;//申请
	public final static Integer DOING_STATUS = 100;//审批中
	//审批结果
	public final static String PASS = "pass";//通过
	public final static Integer PASS_STATUS = 200;//通过
	
	public final static String REFUSE = "refuse";//拒绝
	public final static Integer REFUSE_STATUS = 300;//拒绝
	
	public final static String BACK = "back";//撤回
	public final static Integer BACK_STATUS = 400;//撤回
	
	public final static String OVERDUE = "overdue";//失效
	public final static Integer OVERDUE_STATUS = 500;//失效状态
	
	public final static String OVERDUEPASS = "overduePass";//失效同意
	public final static Integer OVERDUEPASS_STATUS = 600;//失效同意
	
	public final static String OVERDUEREFUSE = "overdueRefuse";//失效拒绝
	public final static Integer OVERDUEREFUSE_STATUS = 700;//失效拒绝
	
	public final static String PROCESSING = "processing";//处理中
	public final static String FINISH = "finish";//已完成
	
	public final static String AUTOCOMMIT_KEY = "proposer";
	//休假登记
	public final static String REGISTER_LEAVE_KEY = "registerLeave";
	//假期processKey
	public final static String LEAVE_KEY = "leave";
	//销假processKey
	public final static String CANCELLEAVE_KEY = "cancelLeave";
	//排班processKey
	public final static String SCHEDULING_KEY = "scheduling";
	//加班processKey
	public final static String OVERTIME_KEY = "overtime";
	//值班processKey
	public final static String DUTY_KEY = "duty";
	//异常考勤processKey
	public final static String ABNORMALATTENDANCE_KEY = "abnormalAttendance";
	//外出processKey
	public final static String OUTGOING_KEY = "outgoing";
	//出差processKey
	public final static String BUSINESS_KEY = "business";
	//出差报告processKey
	public final static String BUSINESSREPORT_KEY = "businessReport";
	//入职processKey
	public final static String ENTRY_KEY = "entry";
	//消下属缺勤processKey
	public final static String REMOVESUBABSENCE_KEY = "removeSubAbsence";
	//工作日志processKey
	public final static String WORK_LOG_KEY = "workLog";
	//流水类型(系统，HR修改)
	public final static String LEAVE_RECORD_SOURCE_SYSTEM = "系统";//失效
	public final static String LEAVE_RECORD_SOURCE_HR_U = "HR修改";
	public final static String LEAVE_RECORD_SOURCE_HR_R = "HR登记";
	//群组邮箱
	public static final String GROUP_EMAIL_PMO = "PMOEmail";
	public static final String GROUP_EMAIL_IT = "ITEmail";
	public static final String GROUP_EMAIL_ADMIN = "AdminEmail";
	public static final String GROUP_EMAIL_HR = "HREmail";
	
	public static enum STATUS {
    	WAIT_(100,"处理中"),PASS_(200,"已完成"),REFUCE_(300,"已拒绝"),BACK_(400,"已撤回"),ABATE_(500,"已失效"),ABATE_PASS(600,"失效同意"),ABATE_REFUCE(700,"失效拒绝");
    	private int statu;
    	private String operation;
    	STATUS(int statu,String operation){
    		this.statu=statu;
    		this.operation=operation;
    		
    	}
    	public static String getOperationByStatu(int statu) {
    		for (STATUS s : STATUS.values()) {
				if(s.statu==statu) {
					return s.operation;
				}
			}
    		return "异常数据";
    	}
	}
	
	//替换空格
	public static final Pattern REPLACE_BLACK_PATTERN = Pattern.compile("\\s*|\t|\r|\n");
	
	//替换中文
	public static final Pattern REPLACE_CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");
}
