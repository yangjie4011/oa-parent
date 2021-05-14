//关闭新增职位页面
function closeWindown(id){
	$("#"+id).window('close');
}


/*******************************************************系统配置表 start***********************************************/
//国籍(国家)
function iniCountry(){
	var country = $("#country");
	country.empty();
	var options = "<option value=''>请选择</option>";
	country.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'code':'country'},
		url:contextPath + "/companyConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				country.append("<option value= " + data[index].id + ">" + data[index].displayName + "</option>");
			});
			country.show();
		}
	});
}

//婚姻状况
function initMaritalStatus(){
	var maritalStatus = $("#maritalStatus");
	maritalStatus.empty();
	var options = "<option value=''>请选择</option>";
	maritalStatus.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'code':'maritalStatus'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				maritalStatus.append("<option value= " + data[index].id + ">" + data[index].displayName + "</option>");
			});
			maritalStatus.show();
		}
	});
}

//民族
function initNation(){
	var nation = $("#nation");
	nation.empty();
	var options = "<option value=''>请选择</option>";
	nation.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'code':'nation'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				nation.append("<option value= " + data[index].id + ">" + data[index].displayName + "</option>");
			});
			nation.show();
		}
	});
}

//政治面貌
function initPolitical(){
	var politicalStatus = $("#politicalStatus");
	politicalStatus.empty();
	var options = "<option value=''>请选择</option>";
	politicalStatus.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'code':'politicalStatus'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				politicalStatus.append("<option value= " + data[index].id + ">" + data[index].displayName + "</option>");
			});
			politicalStatus.show();
		}
	});
}

//文化程度
function initDegreeOfEducation(){
	var degreeOfEducation = $("#degreeOfEducation");
	degreeOfEducation.empty();
	var options = "<option value=''>请选择</option>";
	degreeOfEducation.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'code':'educationLevel'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				degreeOfEducation.append("<option value= " + data[index].id + ">" + data[index].displayName + "</option>");
			});
			degreeOfEducation.show();
		}
	});
}

//是否排班
function initWhetherScheduling(){
	var whetherScheduling = $("#whetherScheduling");
	whetherScheduling.empty();
	var options = "<option value=''>请选择</option>";
	whetherScheduling.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'code':'whetherScheduling'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				whetherScheduling.append("<option value= " + data[index].id + ">" + data[index].displayName + "</option>");
			});
			whetherScheduling.show();
		}
	});
}

/*******************************************************系统配置表 end***********************************************/


/*************************************************************公司配置表 start*****************************************************************/
//单据状态
function initApprovalStatus(companyId){
	var approvalStatus = $("#approvalStatus");
	approvalStatus.empty();
	var options = "<option value=''>请选择</option>";
	approvalStatus.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'companyId':companyId,'code':'approvalStatus'},
		url:contextPath + "/companyConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				approvalStatus.append("<option value= " + data[index].displayCode + ">" + data[index].displayName + "</option>");
			});
			approvalStatus.show();
		}
	});
}

//单据状态
function initApprovalStatusbyClass(companyId){
	var approvalStatus = $(".approvalStatus");
	approvalStatus.empty();
	var options = "<option value=''>请选择</option>";
	approvalStatus.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'companyId':companyId,'code':'approvalStatus'},
		url:contextPath + "/companyConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				approvalStatus.append("<option value= " + data[index].displayCode + ">" + data[index].displayName + "</option>");
			});
			approvalStatus.show();
		}
	});
}

//从业相关性
function initIndustryRelevance(companyId){
	var industryRelevance = $("#industryRelevance");
	industryRelevance.empty();
	var options = "<option value=''>请选择</option>";
	industryRelevance.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'companyId':companyId,'code':'industryCorrelation'},
		url:contextPath + "/companyConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				industryRelevance.append("<option value= " + data[index].id + ">" + data[index].displayName + "</option>");
			});
			industryRelevance.show();
		}
	});
}

//工时类型
function initWorkType(companyId){
	var workType = $("#workType");
	workType.empty();
	var options = "<option value=''>请选择</option>";
	workType.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'companyId':companyId,'code':'typeOfWork'},
		url:contextPath + "/companyConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				workType.append("<option value= " + data[index].id + ">" + data[index].displayName + "</option>");
			});
			workType.show();
		}
	});
}

//页面上多个工时类型 byClass 后缀 根据页面定义的class
function initWorkTypebyClass(companyId){
	var workType = $(".workType");
	workType.empty();
	var options = "<option value=''>请选择</option>";
	workType.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'companyId':companyId,'code':'typeOfWork'},
		url:contextPath + "/companyConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				workType.append("<option value= " + data[index].id + ">" + data[index].displayName + "</option>");
			});
			workType.show();
		}
	});
}

//初始化公司类型
function initDepartType(nodeId,companyId){
	var departType = $("#" + nodeId);
	departType.empty();
	var options = "<option value=''>请选择</option>";
	departType.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'companyId':companyId,'code':'departType'},
		url:contextPath + "/companyConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				departType.append("<option value= " + data[index].displayCode + ">" + data[index].displayName + "</option>");
			});
			departType.show();
		}
	});
}
/*************************************************************公司配置表 end*****************************************************************/





/*******************************************************树 start*****************************************************/
//获得第一级部门
function getFirstDepart(selectId){
	/**扩展，可以根据传过来的dom id，赋值**/
	if(typeof(selectId) == "undefined" || selectId == ""){
		selectId = "firstDepart";
	}
	var firstDepart = $("#"+selectId);
	firstDepart.empty();
	var options = "<option value=''>请选择</option>";
	firstDepart.append(options);
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		url:contextPath + "/depart/getFirstDepart.htm",
		success:function(data){
			$.each(data, function(index) {
				firstDepart.append("<option value= " + data[index].id + ">" + data[index].name + "</option>");
			});
		}
	});
}

//获得第二级部门
function getSecondDepart(id){
	var secondDepart = $("#secondDepart");
	secondDepart.empty();
	var options = "<option value=''>请选择</option>";
	secondDepart.append(options);
	
	if(null != id){
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data:{id:id},
			url:contextPath + "/depart/getByParentId.htm",
			success:function(data){
				$.each(data, function(index) {
					secondDepart.append("<option value= " + data[index].id + ">" + data[index].name + "</option>");
				});
				
				if(data.length > 0){
					$("#secondDepartDiv").css("display","block");
				}else{
					$("#secondDepartDiv").css("display","none");
				}
			}
		});
	}
}

//获得第一级部门 一个页面包含多个部门 根据页面上定义的class
function getFirstDepartByClass(selectId){
	/**扩展，可以根据传过来的dom id，赋值**/
	if(typeof(selectId) == "undefined" || selectId == ""){
		selectId = "firstDepart";
	}
	var firstDepart = $("."+selectId);
	firstDepart.empty();
	var options = "<option value=''>请选择</option>";
	firstDepart.append(options);
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		url:contextPath + "/depart/getFirstDepart.htm",
		success:function(data){
			$.each(data, function(index) {
				firstDepart.append("<option value= " + data[index].id + ">" + data[index].name + "</option>");
			});
		}
	});
}

//初始化排班组别树
function initScheduleGroupTree(currentCompanyId){
	$.ajax({
   		async : false,
   		enable:true,
   		type : "post",
 		dataType:"json",
 		data : {'companyId':currentCompanyId},
   		url : contextPath + "/schedule/getGroupTreeList.htm",
   		success : function(data) {
   			var ztreeObj = $.fn.zTree.init($("#scheduleGroupTree"), groupSetting, JSON.parse(data.result));
   			ztreeObj.expandAll(true);
   		}
   	});
}
//查询排班部门列表
function getScheduleDepartList(selectId){
	/**扩展，可以根据传过来的dom id，赋值**/
	if(typeof(selectId) == "undefined" || selectId == ""){
		selectId = "firstDepart";
	}
	var firstDepart = $("#"+selectId);
	firstDepart.empty();
	var options = "<option value=''>请选择</option>";
	firstDepart.append(options);
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		url:contextPath + "/schedule/getScheduleDepartList.htm",
		success:function(data){
			$.each(data, function(index) {
				firstDepart.append("<option value= " + data[index].id + ">" + data[index].name + "</option>");
			});
		}
	});
}
//初始化部门树（根据公司id获得部门树）
function initDepartTree(currentCompanyId){
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'companyId':currentCompanyId},
   		url : contextPath + "/depart/getTreeList.htm",
   		success : function(data) {
   			var ztreeObj = $.fn.zTree.init($("#departTree"), departSetting, JSON.parse(data.result));
   			ztreeObj.expandAll(true);
   		}
   	});
}

//初始化部门树（根据公司id获得部门树）
function initResourceTree(currentCompanyId,roleId){
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'companyId':currentCompanyId,roleId:roleId},
   		url : contextPath + "/resource/getTreeList.htm",
   		success : function(data) {
   			var ztreeObj = $.fn.zTree.init($("#resourceTree"), resourceSetting, JSON.parse(data.result));
   			ztreeObj.expandAll(true);
   		}
   	});
}
/*******************************************************树 end*****************************************************/


/******************************************************初始化职位树 start**************************************************/
//职位
function initPositionTree(departId){
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'departId':departId},
		url:contextPath + "/position/getTreeList.htm",
		success:function(data){
			var ztreeObj = $.fn.zTree.init($("#positionTree"), positionSetting, JSON.parse(data.result));
   			ztreeObj.expandAll(true);
		}
	});
}

//职位
function initPositionList(departId){
	
	var position = $("#position");
	position.empty();
	var options = "<option value=''>请选择</option>";
	position.append(options);
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'departId':departId},
		url:contextPath + "/position/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index,item) {
				position.append("<option value= " + item.id + ">" + item.positionName + "</option>");
			});
		}
	});
}
/******************************************************初始化职位树 end**************************************************/

//加载职位序列下拉
function initpositionSeq(companyId){
	var sel = $("#positionSeqId");
	sel.empty();
	var options = "<option value=''>请选择</option>";
	sel.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		url:contextPath + "/companyPositionSeq/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				sel.append("<option value= " + data[index].id + ">" + data[index].name + "</option>");
			});
		}
	});
}

//加载职位等级下拉
function initPositionLevel(companyId){
	var sel = $("#positionLevelId");
	sel.empty();
	var options = "<option value=''>请选择</option>";
	sel.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		url:contextPath + "/companyPositionLevel/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				sel.append("<option value= " + data[index].id + ">" + data[index].name + "</option>");
			});
		}
	});
}


//根据员工类型加载合作公司名称
function getCoopCompanyByEmpType(empTypeId){
	var coopCompany = $("#coopCompanyId");
	coopCompany.empty();
	var options = "<option value=''>请选择</option>";
	coopCompany.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'empTypeId':empTypeId},
		url:contextPath + "/coopCompany/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				coopCompany.append("<option value= " + data[index].id + ">" + data[index].coopCompanyName + "</option>");
			});
			coopCompany.show();
		}
	});
}

//获取公司列表
function getCompany(){
	var companyId = $("#companyId");
	companyId.empty();
	var options = "<option value=''>请选择</option>";
	companyId.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		url:contextPath + "/company/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				companyId.append("<option value= " + data[index].id + ">" + data[index].name + "</option>");
			});
			companyId.show();
		}
	});
}

//获取公司列表 admin后台
function getAdminCompany(){
	//获取name为 companyId 的下拉框 chushih	
	var companyName = $("select[name='companyId']");
	companyName.empty();
	var options = "<option value=''>请选择</option>";
	companyName.append(options);	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',	
		url:contextPath + "/companyConfig/getCompanyinfo.htm",
		success:function(data){
			$.each(data, function(index) {
				companyName.append("<option value= " + data[index].companyId + ">" + data[index].name + "</option>");
			});
			companyName.show();
		}
	});
}


//如果节点值为空，转json默认会去掉这个节点
function getValByUndefined(nodeVal){
	if("undefined" == nodeVal || "undefined" == typeof(nodeVal)||nodeVal==null){
		return "";
	}
	
	return nodeVal;
}

//如果节点值为空，则返回false 
function isVlues(nodeVal){
	if("undefined" == nodeVal || "undefined" == typeof(nodeVal)||nodeVal==null){
		return false;
	}
	
	return true;
}


//员工类型
function initEmployeeType(companyId){
	var empTypeId = $("#empTypeId");
	empTypeId.empty();
	var options = "<option value=''>请选择</option>";
	empTypeId.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'companyId':companyId},
		url:contextPath + "/empType/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				empTypeId.append("<option value= " + data[index].id + ">" + data[index].typeCName + "</option>");
			});
			empTypeId.show();
		}
	});
}


//以什么字符串开头
String.prototype.startWith=function(str){     
  var reg=new RegExp("^"+str);     
  return reg.test(this);        
}  

//已什么字符串结尾
String.prototype.endWith=function(str){     
  var reg=new RegExp(str+"$");     
  return reg.test(this);        
}

//汇报对象,参数1：部门ID，参数2：页面select的id
function initEmployeeLeader(departId,selectId){
	/**扩展，可以根据传过来的dom id，赋值**/
	if(typeof(selectId) == "undefined" || selectId == ""){
		selectId = "employeeLeader";
	}
	var employeeLeader = $("#"+selectId);
	console.info("selectId:"+selectId);
	employeeLeader.empty();
	var options = "<option value=''>请选择</option>";
	employeeLeader.append(options);
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'departId':departId},
		url:contextPath + "/employee/getReportPerson.htm",
		success:function(data){
			$.each(data, function(index) {
				employeeLeader.append("<option value= " + data[index].id + ">" + data[index].cnName + "</option>");
			});
			employeeLeader.show();
		}
	});
}

//流程类型
function initProcessType(){
	var processType = $("#processType");
	processType.empty();
	var options = "<option value=''>请选择</option>";
	processType.append(options);
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'code':'processType'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				processType.append("<option value= " + data[index].displayCode + ">" + data[index].displayName + "</option>");
			});
			processType.show();
		}
	});
}

//根据流程类型获取流程名称
function getNameByProcessType(processType){
	var processName = $("#processName");
	processName.empty();
	var options = "<option value=''>请选择</option>";
	processName.append(options);
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'processType':processType},
		url:contextPath + "/reProcdef/getNameByProcessType.htm",
		success:function(data){
			$.each(data, function(index) {
				processName.append("<option value= " + data[index].processName + ">" + data[index].processName + "</option>");
			});
			processName.show();
		}
	});
}

//授权状态
function initAuthStatus(){
	var authStatus = $("#authStatus");
	authStatus.empty();
	var options = "<option value=''>请选择</option>";
	authStatus.append(options);
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'code':'authStatus'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				authStatus.append("<option value= " + data[index].displayCode + ">" + data[index].displayName + "</option>");
			});
			authStatus.show();
		}
	});
}

//发送验证码
function sendRadmonCode(){
	var phone = $("#phone").val();
	if(phone == ""){
		JEND.page.showError("请输入手机号");
		return false;
	}
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'phone':$("#phone").val()},
		url:contextPath + "/sms/sendRandomCode.htm",
		success:function(data){
			if(data.success){
				JEND.page.showSuccess("验证码发送成功，请稍后查看手机");
			}else{
				JEND.page.showError(data.message);
			}
		}
	});
}

function clearById(id){
	$(id).empty();
}

/**
 * check时间控件
 */
function checkTime(startTime,endTime){
	if(startTime !='' && endTime != '' && startTime > endTime ){
		JEND.page.alert("开始时间必须小于或等于结束时间");
		
		return false;
	}else if(startTime !='' && endTime != '' && endTime < startTime){
		JEND.page.alert("结束时间必须大于或等于开始时间");
		
		return false;
	}
	
	return true;
}

var now = new Date(); //当前日期 

//获取当前月 
function getCurrMonth(){
	return now.getMonth(); 
}

//获取上月日期
function getLastMonth(){
	var lastMonthDate = new Date(); //上月日期
	lastMonthDate.setDate(1);
	lastMonthDate.setMonth(lastMonthDate.getMonth()-1);
	
	return lastMonthDate.getMonth();
}

//获取去年
function getLastYear(){
	var lastMonthDate = getLastMonth();
	
	return lastMonthDate.getYear();
}

//获取当前年
function getCurrYear(){
	var nowYear = now.getYear(); //当前年 
	nowYear += (nowYear < 2000) ? 1900 : 0;
	
	return nowYear;
}

//获得某月的天数 
function getMonthDays(myMonth){ 
	var monthStartDate = new Date(getCurrYear(), myMonth, 1); 
	var monthEndDate = new Date(getCurrYear(), myMonth + 1, 1); 
	var days = (monthEndDate - monthStartDate)/(1000 * 60 * 60 * 24); 
	return days; 
} 

//获得本月的开始日期 
function getMonthStartDate(style){
	var monthStartDate = new Date(getCurrYear(), getCurrMonth(), 1); 
	return monthStartDate.format(style); 
}

//获得本月的结束日期 
function getMonthEndDate(style){ 
	var monthEndDate = new Date(getCurrYear(), getCurrMonth(), getMonthDays(getCurrMonth())); 
	return monthEndDate.format(style); 
}

//获得上月开始时间
function getLastMonthStartDate(style){
	var nowYear = getCurrYear();
	if(getCurrMonth() == 0){//如果当前月是1月份，那么年份要自动减1
		nowYear = nowYear - 1;
	}
	var lastMonthStartDate = new Date(nowYear, getLastMonth(), 1);
	return lastMonthStartDate.format(style); 
}

//获得上月结束时间
function getLastMonthEndDate(style){
	var nowYear = getCurrYear();
	if(getCurrMonth() == 0){//如果当前月是1月份，那么年份要自动减1
		nowYear = nowYear - 1;
	}
	var lastMonthEndDate = new Date(nowYear, getLastMonth(), getMonthDays(getLastMonth()));
	return lastMonthEndDate.format(style); 
}

//得到今天距离本周一的天数
var dayMSec = 24 * 3600 * 1000;
function getDayBetweenMonday(){    
    //得到今天的星期数(0-6),星期日为0    
    var weekday = now.getDay();    
    //周日    
    if(weekday == 0){    
        return 6;    
    }else{    
        return weekday - 1;
    }    
}

//获取上周时间
function setLastWeek(startTimeId,endTimeId,style){    
    //得到距离本周一的天数    
    var weekdayBetween = getDayBetweenMonday();    
        
    //得到本周星期一的毫秒值    
    var nowMondayMSec = now.getTime() - weekdayBetween * dayMSec;    
    //得到上周一的毫秒值    
    var lastMondayMSec = nowMondayMSec - 7 * dayMSec;    
    //得到上周日的毫秒值    
    var lastSundayMSec = nowMondayMSec - 1 * dayMSec;    
        
    var lastMonday = new Date(lastMondayMSec);    
        
    var lastSunday = new Date(lastSundayMSec);    
        
    $("#"+startTimeId).val(lastMonday.format(style));
	$("#"+endTimeId).val(lastSunday.format(style));
}    

//设置上月时间
function setLastMonthTime(startTimeId,endTimeId,style){
	$("#"+startTimeId).val(getLastMonthStartDate(style));
	$("#"+endTimeId).val(getLastMonthEndDate(style));
}

//设置本月时间
function setCurrentMonthTime(startTimeId,endTimeId,style){
	$("#"+startTimeId).val(getMonthStartDate(style));
	$("#"+endTimeId).val(getMonthEndDate(style));
}

//获取上周时间
function setLastWeekByClass(startTimeId,endTimeId,style){    
    //得到距离本周一的天数    
    var weekdayBetween = getDayBetweenMonday();    
        
    //得到本周星期一的毫秒值    
    var nowMondayMSec = now.getTime() - weekdayBetween * dayMSec;    
    //得到上周一的毫秒值    
    var lastMondayMSec = nowMondayMSec - 7 * dayMSec;    
    //得到上周日的毫秒值    
    var lastSundayMSec = nowMondayMSec - 1 * dayMSec;    
        
    var lastMonday = new Date(lastMondayMSec);    
        
    var lastSunday = new Date(lastSundayMSec);    
        
    $("."+startTimeId).val(lastMonday.format(style));
	$("."+endTimeId).val(lastSunday.format(style));
}    

//设置上月时间
function setLastMonthTimeByClass(startTimeId,endTimeId,style){
	$("."+startTimeId).val(getLastMonthStartDate(style));
	$("."+endTimeId).val(getLastMonthEndDate(style));
}

//设置本月时间
function setCurrentMonthTimeByClass(startTimeId,endTimeId,style){
	$("."+startTimeId).val(getMonthStartDate(style));
	$("."+endTimeId).val(getMonthEndDate(style));
}


//点击隐藏 显示 查询模块 
function getButton(obj){
	let buttonElement =  $(obj).parents(".form-wrap").find(".button-wrap").toggle();
}

Date.prototype.format = function(fmt) { 
    var o = { 
       "M+" : this.getMonth()+1,                 //月份 
       "d+" : this.getDate(),                    //日 
       "h+" : this.getHours(),                   //小时 
       "m+" : this.getMinutes(),                 //分 
       "s+" : this.getSeconds(),                 //秒 
       "q+" : Math.floor((this.getMonth()+3)/3), //季度 
       "S"  : this.getMilliseconds()             //毫秒 
   }; 
   if(/(y+)/.test(fmt)) {
           fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
   }
    for(var k in o) {
       if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
   return fmt; 
}

//获得排班组别
function getGroupListByDepartId(id){
	var groupId = $("#groupId");
	groupId.empty();
	var options = "<option value=''>请选择</option>";
	groupId.append(options);
	
	if(null != id){
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data:{departId:id},
			url:contextPath + "/schedule/getGroupListByDepartId.htm",
			success:function(data){
				$.each(data, function(index) {
					groupId.append("<option value= " + data[index].id + ">" + data[index].name + "</option>");
				});
			}
		});
	}
}
function secondConfirmation(data){
	//新建div元素节点
	var div = "<div class='commonBox popbox' id='secondConfirmationDiv' style='display:block'><div class='popbox-bg'></div><div class='popbox-center' style='top: 10%; left: 30%'><div class='popbox-main'><div class='cun-pop-content'><div class='form-wrap'><div class='title'><strong>再次确认</strong><i onclick='closeSecondConfirmationDiv();' class='mo-houtai-box-close' style='width: 25px;height: 30px;'></i></div><div class='form-main'><div class='col'><div class='form'><label class='w'  style='width:250px;'>"+data.msg+"</label></div></div><div class='col'><div class='button-wrap ml-4'><button class='red-but sure' style='width:120px;'><span>确认</span></button><button id='zcqx' class='small grey-but'onclick='closeSecondConfirmationDiv();' style='width:120px;'><span>取消</span></button></div></div></div></div></div></div></div></div>"
	//在body的现有子节点的最后添加div
	$("body").append(div);
    $('.sure').click(function () {
         if (data.sureFn) data.sureFn();
         $("#secondConfirmationDiv").remove();
    })
   
}
function closeSecondConfirmationDiv(){
	$("#secondConfirmationDiv").remove();
}
var gotoPageDeptId=null;  //全局变量  占时用于全局 因获取不了值 下次有时间更改
function showGetDeptList(deptId,obj){
	var showGetDeptList = $("#showGetDeptList");
	var pageHtml="";
	if(deptId!=null){
		gotoPageDeptId=deptId;
	}
	pageHtml+="<div class='commonBox popbox' id='getDeptList' style='display:none'>";
	pageHtml+="<input type='hidden' id='pageNoDept' value=''/>";
	pageHtml+="<div class='popbox-bg'></div>";
	pageHtml+="<div class='popbox-center' style=' top:10%; left:15%; position: absolute;width: 800px;height: 600px;'>";
	pageHtml+="<div class='popbox-main' style=''>";
	pageHtml+="<div class='cun-pop-content'>";
	pageHtml+="<div class='form-wrap'>";
	pageHtml+="<div class='title'>";
	pageHtml+="<strong><i class='icon'></i>选择人员</strong>";
	pageHtml+="</div>";
		pageHtml+="<br/>";
			pageHtml+="<div>";
				pageHtml+="<input type='text' class='form-text'  name='cnName' value='' id='queryDeptByNameAndCode' style='display: block;float: left; width: 388px;height: 34px;line-height: 34px;border: 1px solid #d9d9d9;text-indent: 10px; margin-left: 8px;' placeholder='请输入员工编号或员工姓名' /> ";
				pageHtml+="<button  class='red-but' onclick='gotoPageDept(1,"+deptId+");' style='width:120px; margin-left: 8px;'>";
				pageHtml+="<span>查询</span>";
					pageHtml+="</button>";
						pageHtml+="</div>";
							pageHtml+="<div class='form-main' style='margin-top: 40px;'>";
							pageHtml+="<table>";
								pageHtml+="<thead>";
									pageHtml+="<tr>";
										pageHtml+="<th style='overflow-x:auto;width:30px;text-align:center;'></th>";
										pageHtml+="<th style='overflow-x:auto;width:100px;text-align:center;'>员工编号</th>";
										pageHtml+="<th style='overflow-x:auto;width:100px;text-align:center;'>员工姓名</th>";
										pageHtml+="</tr>";
											pageHtml+="</thead>";
												pageHtml+="<tbody id='reportDeptList'>";
												pageHtml+="</tbody>";
													pageHtml+="</table>";
														pageHtml+="</div>";
						
															pageHtml+="</div>";
					pageHtml+="</div>";
						pageHtml+="</div>";
							pageHtml+="<div class='col' style='bottom:60px;  position: relative; margin-left: -480px;'>";
							pageHtml+="<div class='button-wrap ml-4'>";
							pageHtml+="<button  class='red-but' id='successSaveEmp' style='width:120px;'>";
							pageHtml+="<span>确定</span>";
								pageHtml+="</button>";
									pageHtml+="<button  class='small grey-but'";
									pageHtml+="onclick='closegetDeptList()' style='width:120px;'>";
									pageHtml+="<span>取消</span>";
										pageHtml+="</button>";
											pageHtml+="</div>";
												pageHtml+="</div>";
													pageHtml+="<div class='paging' id='getDeptListPage' style='bottom:110px;  position: relative;' ></div> ";	
													pageHtml+="</div>";
														pageHtml+="</div>";
	
	showGetDeptList.append(pageHtml);	
	$("#successSaveEmp").unbind();
	$("#successSaveEmp").bind("click",function(){
		successSaveEmp($(obj));
	});
	$("#getDeptList").show();
}
function closegetDeptList(){
	$("#showGetDeptList").empty();
}
function gotoPageDept(page,deptId){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	if(!deptId){
		deptId =gotoPageDeptId;
	}
	$("#pageNoDept").val(page);
	var data ="nameCodeStr="+$("#queryDeptByNameAndCode").val()+ "&page=" + $("#pageNoDept").val() + "&rows=10";
	if(deptId!=null && deptId!="" ){
		var data ="nameCodeStr="+$("#queryDeptByNameAndCode").val()+"&departId=" +deptId+"&page=" + $("#pageNoDept").val() + "&rows=10";
	}
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  contextPath + "/employee/getDeptList.htm",
		success:function(response){
			$("#reportDeptList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:cneter;'><input code="+response.rows[i].code+" id="+response.rows[i].id+" value="+response.rows[i].cnName+" class='tdCheck' type='radio' name='selectNameAndCode'></td>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html+="</tr>";
			}
			$("#reportDeptList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPageByName("getDeptListPage",response,page,"gotoPageDept");
		},
	complete:function(XMLHttpRequest,textStatus){  
		pageLoading(false);//关闭动画
    }
});
}

function successSaveEmp(obj){
	var map ={};
	var selectRadio=$("input[name='selectNameAndCode']:checked");
	$(obj).val(selectRadio.attr("value"));
	$(obj).attr("employId",selectRadio.attr("id"));
	$(obj).attr("employCode",selectRadio.attr("code"));
	closegetDeptList();
} 

function calculatedHours(startTime,endTime){
	var hours = 0;
	var startHour = startTime.substring(0,2);
	var endHour = endTime.substring(0,2);
	//如果结束时间是0点则结束时间设为24点
	if(endHour == 0){
		endHour = 24;
	}
	var startMinute=(startTime.substring(3,5)==30)?0.5:0;
	var endMinute=(endTime.substring(3,5)==30)?0.5:0;
	if(startHour<endHour){
		hours=(parseFloat(endHour)+parseFloat(endMinute))-(parseFloat(startHour)+parseFloat(startMinute));
		if(hours>=10){
			hours=hours-2;
		}else if(hours>=5){
			hours=hours-1;
		}
	}
	return hours;
}

//校验只能输入0.5的倍数
function checkDouble(obj){
	obj.value = obj.value.replace(/[^\d.]/g,"");  //清除“数字”和“.”以外的字符  
    obj.value = obj.value.replace(/\.{2,}/g,"."); //只保留第一个. 清除多余的  
    obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$","."); 
    obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d).*$/,'$1$2.5');//只能输入1个小数 ，且已0.5为单位
    if(obj.value.indexOf(".")< 0 && obj.value !=""){//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额 
        obj.value= parseFloat(obj.value); 
    } 
}
//文本框只能输入0.5的倍数
function checkData(value)  
{     
    var str = value.replace(/[^\d\.]/g, '');  
    var pointIndex = str.search(/\./);  
      
    if(-1 !== pointIndex)  
    {  
        str = str.substr(0, pointIndex + 1).replace(/\./, '.5') ;  
    }  
    return str;  
}  
