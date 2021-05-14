//关闭新增职位页面
function closeWindown(){
	JEND.page.dialog.close();
}


/*******************************************************系统配置表 start***********************************************/
//国籍(国家)
function iniCountry(){
	var country = $("#country");
	country.empty();
	var options = "<option value=''>请选择</option>";
	country.append(options);
	
	$.ajax({
		async:false,
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
		async:false,
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
		async:false,
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
		async:false,
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
		async:false,
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
		async:false,
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
//从业相关性
function initIndustryRelevance(companyId){
	var industryRelevance = $("#industryRelevance");
	industryRelevance.empty();
	var options = "<option value=''>请选择</option>";
	industryRelevance.append(options);
	
	$.ajax({
		async:false,
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
		async:false,
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
/*************************************************************公司配置表 end*****************************************************************/





/*******************************************************初始化部门树 start*****************************************************/
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
/*******************************************************树 end*****************************************************/


/******************************************************初始化职位树 start**************************************************/
//职位
function initPositionTree(departId){
	$.ajax({
		async:false,
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
/******************************************************初始化职位树 end**************************************************/

//加载职位序列下拉
function initpositionSeq(companyId){
	var sel = $("#positionSeqId");
	sel.empty();
	var options = "<option value=''>请选择</option>";
	sel.append(options);
	
	$.ajax({
		async:false,
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
		async:false,
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
		async:false,
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

//获取公司列表 web 
function getCompany(){
	var companyId = $("#companyId");
	companyId.empty();
	var options = "<option value=''>请选择</option>";
	companyId.append(options);
	
	$.ajax({
		async:false,
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


//如果节点值为空，转json默认会去掉这个节点
function getValByUndefined(nodeVal){
	if("undefined" == nodeVal || "undefined" == typeof(nodeVal)){
		return "";
	}
	
	return nodeVal;
}


//员工类型
function initEmployeeType(companyId){
	var empTypeId = $("#empTypeId");
	empTypeId.empty();
	var options = "<option value=''>请选择</option>";
	empTypeId.append(options);
	
	$.ajax({
		async:false,
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

//汇报对象
function initEmployeeLeader(departId){
	var employeeLeader = $("#employeeLeader");
	employeeLeader.empty();
	var options = "<option value=''>请选择</option>";
	employeeLeader.append(options);
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'departId':departId},
		url:contextPath + "/employee/getMLeaderByDepartId.htm",
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
		async:false,
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
		async:false,
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
		async:false,
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
		//alert("请输入手机号");
		return false;
	}
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'phone':$("#phone").val()},
		url:contextPath + "/sms/sendRandomCode.htm",
		success:function(data){
			if(data.success){
				OA.titlePup('发送验证码成功！','win');
			}else{
				OA.titlePup(data.message,'lose');
			}
		}
	});
}

function clickSxyIcon(url){
	var dataSourceUrl = getCookieValue("dataSourceUrl");
	if(dataSourceUrl != null && dataSourceUrl == 1) {//通过随心邮收件箱登录到MO系统的
		window.location.href = "https://wxmail.tom.com/index.php?s=/addon/Tom/Tomt/index/token/gh_89640de82fb6.html";
	}else{
		window.location.href = url;
	}
}

//获取cookie
function getCookieValue(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) != -1) return c.substring(name.length, c.length);
    }
    return "";
}

//初始化员工头像
function iniEmpPic(){
	var empPic = $("#empPic");
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:'',
		url:contextPath + "/user/getEmpPic.htm",
		success:function(response){
			empPic.css({
	    		"background": "#fff url("+response.message+") no-repeat center",
	    		"background-size":"contain",
	    		"-webkit-background-size":"contain"
	    	});
		},
		error:function(response){
			empPic.css({
	    		"background": "#fff url('') no-repeat center",
	    		"background-size":"contain",
	    		"-webkit-background-size":"contain"
	    	});
		}
	});
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

function addDate(date,days){ 
    var d = new Date(Date.parse(date.replace(/-/g,  "/"))); 
    d.setDate(d.getDate()+parseInt(days)); 
    return d.format("yyyy/MM/dd").replaceAll("/","-"); 
} 