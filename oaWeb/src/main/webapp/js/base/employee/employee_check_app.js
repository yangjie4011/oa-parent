$(function(){
	
	var authority = $("#authority").val();
	if(authority != 'hr'){
		$(".my-record .edit").hide();
	}
	
	//加载员工信息
	initEmployee();
	
	//异步加载教育经历
	setTimeout(initEmpSchool(),300);
	
	//异步加载培训经历
	setTimeout(initEmpTranning(),600);
	
	//异步加载工作经历
	setTimeout(initWork(),900);
	
	//异步加载紧急联系人
	setTimeout(initUrgentContact(),1200);
	
	//异步加载家庭成员（配偶+子女）信息【timeOut：不用等待上一步代码执行完后才执行下面的代码】
	setTimeout(initFamilyMember(),1500);
	
	//setTimeout(getMore(),1100);
});


//通行证帐号
function initUserName(){
	var staffStr = $("#empJobTable #staffStr");
	var staffStrForm = $("#baseJobForm #staffStr");
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'employeeId':$("#id").val()},
   		url : contextPath + "/user/getByCondition.htm",
   		success : function(data) {
   			if(data != null && data !=''){
   				staffStr.text(data.userName);
   				staffStrForm.val(data.userName);
   			}
   		}
	});
}

//更多选项
function getMore(){
	$(".get-more").hide();
	$("#moreDiv").show();
	
	//初始化业绩和奖惩
	initAchieverment();
	
	//初始化在司培训
	initCompanyTrain();
	
	//初始化合同
	initContract();
	
	//初始化考核记录
	initEmpAppraise();
	
	//初始化岗位记录
	initEmpPosition()
}

//初始化员工岗位记录
function initEmpPosition(){
	var empPositionTable = $("#empPositionTable");

	var form = $("#empPositionForm");
	empPositionTable.find("div[class='m']").remove();
	form.find("li").not(":first").remove();//移除多余元素
	
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'employeeId':$("#id").val()},
   		url : contextPath + "/empPostRecord/getListByCondition.htm",
   		success : function(data) {
   			if(data != null && data !=''){
   				$(data).each(function(index){
   					var rowDiv='<div class="m"><span class="r-input" style="display:none" id="id">'+data[index].id
						+'</span><span class="r-input" style="display:none" id="version">'+data[index].version
						+'</span><span class="r-input" style="display:none" id="prePositionId">'+data[index].prePositionId
						+'</span><span class="r-input" style="display:none" id="positionId">'+data[index].positionId
						+'</span><div class="moudle-ll"><div class="mk sd"><div class="l-title">调整日期</div><div class="r-input" id="adjustDate">'
   						+getValByUndefined(data[index].adjustDate)
   						+'</div></div><div class="mk sd"><div class="l-title">原岗位</div><div class="r-input" id="prePositionName">'
   						+getValByUndefined(data[index].prePosition.positionName)
   						+'</div></div><div class="mk sd"><div class="l-title">现岗位</div><div class="r-input" id="positionName">'
   						+(typeof data[index].position == 'undefined'?'':getValByUndefined(data[index].position.positionName))
   						+'</div></div></div></div>';
   					    
						empPositionTable.append(rowDiv);
						
						rowDiv = '<li><div class="input-list"><div class="edit-y"></div>'+rowDiv+'</div></li>';
						form.find("ul").append(rowDiv);
   				})
   			}else{
					var rowDiv='<div class="m"><input type="hidden" id="id" value="'
						+'"/><div class="moudle-ll"><div class="mk sd"><div class="l-title">调整日期</div><div class="r-input" id="adjustDate">'
						+'--</div></div><div class="mk sd"><div class="l-title">原岗位</div><div class="r-input" id="prePositionName">'
						+'--</div></div><div class="mk sd"><div class="l-title">现岗位</div><div class="r-input" id="positionName">'
						+'--</div></div></div></div>';
					    
					empPositionTable.append(rowDiv);
   	   		}
   		}
	});
}

//初始化考核记录
function initEmpAppraise(){
	var empAppraiseTable = $("#empAppraiseTable");

	var form = $("#empAppraiseForm");
	empAppraiseTable.find("div[class='m']").remove();
	form.find("li").not(":first").remove();//移除多余元素
	
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'employeeId':$("#id").val()},
   		url : contextPath + "/empAppraise/getListByCondition.htm",
   		success : function(data) {
   			if(data != null && data !=''){
   				$(data).each(function(index){
   					var rowDiv = '<div class="m"><span class="r-input" style="display:none" id="id">'+data[index].id
						+'</span><span class="r-input" style="display:none" id="version">'+data[index].version
						+'</span><div class="moudle-ll"><div class="mk sd"><div class="l-title">考核日期</div><div class="r-input" id="annualExaminationTime">'
   						+getValByUndefined(data[index].annualExaminationTime)
   						+'</div></div><div class="mk sd"><div class="l-title">考核成绩</div><div class="r-input" id="score">'
   						+getValByUndefined(data[index].score)
   						+'</div></div><div class="mk sd"><div class="l-title text">考核结论</div><div class="r-input" id="conclusion">'
   						+getValByUndefined(data[index].conclusion)
   						+'</div></div></div></div>';
   					
						empAppraiseTable.append(rowDiv);
						
						rowDiv = '<li><div class="input-list"><div class="edit-y"></div>'+rowDiv+'</div></li>';
						form.find("ul").append(rowDiv);
   				})
   			}else{
					var rowDiv = '<div class="m"><input type="hidden" id="id" value="'
					    +'"/><div class="moudle-ll"><div class="mk sd"><div class="l-title">考核日期</div><div class="r-input" id="annualExaminationTime">'
						+'--</div></div><div class="mk sd"><div class="l-title">考核成绩</div><div class="r-input" id="score">'
						+'--</div></div><div class="mk sd"><div class="l-title text">考核结论</div><div class="r-input" id="conclusion">'
						+'--</div></div></div></div>';
					
					empAppraiseTable.append(rowDiv);
   			}
   		}
	});
}

//初始化合同
function initContract(){
	var contractTable = $("#contractTable");

	var form = $("#contractForm");
	contractTable.find("div[class='m']").remove();
	form.find("li").not(":first").remove();//移除多余元素
	
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'employeeId':$("#id").val(),delFlag:0},
   		url : contextPath + "/empContract/getListByCondition.htm",
   		success : function(data) {
   			if(data != null && data !=''){
   				$(data).each(function(index){
   					var rowDiv = '<div class="m"><span class="r-input" style="display:none" id="id">'+data[index].id
						+'</span><span class="r-input" style="display:none" id="version">'+data[index].version
						+'</span><div class="moudle-ll"><div class="mk sd"><div class="l-title">签订日期</div><div class="r-input" id="contractSignedDate">'
   						+getValByUndefined(data[index].contractSignedDate)
   						+'</div></div><div class="mk sd"><div class="l-title">合同期限</div><div class="r-input"><span class="r-input" id="contractPeriod">'
   						+getValByUndefined(data[index].contractPeriod)
   						+'</span>年</div></div><div class="mk sd"><div class="l-title">试用到期日</div><div class="r-input"><span class="r-input" id="probationExpire">'
   						+getValByUndefined(data[index].probationExpire)
   						+'</span>个月</div></div><div class="mk sd"><div class="l-title">合同到期日</div><div class="r-input" id="contractEndTime">'
   						+getValByUndefined(data[index].contractEndTime)
   						+'</div></div></div></div>';
   								
						contractTable.append(rowDiv);
						
						rowDiv = '<li><div class="input-list"><div class="edit-y"></div>'+rowDiv+'</div></li>';
						form.find("ul").append(rowDiv);
   				})
   			}else{
					var rowDiv = '<div class="m"><input type="hidden" id="id" value="'
				    	+'"/><div class="moudle-ll"><div class="mk sd"><div class="l-title">签订日期</div><div class="r-input" id="contractSignedDate">'
						+'--</div></div><div class="mk sd"><div class="l-title">合同期限</div><div class="r-input" id="contractPeriod">'
						+'--</div></div><div class="mk sd"><div class="l-title">试用到期日</div><div class="r-input" id="probationExpire">'
						+'--</div></div><div class="mk sd"><div class="l-title">合同到期日</div><div class="r-input" id="contractEndTime">'
						+'--</div></div></div></div>';
								
					contractTable.append(rowDiv);
   			}
   		}
	});
}

//初始化在司培训
function initCompanyTrain(){
	var companyTrainTable = $("#companyTrainTable");

	var form = $("#companyTrainForm");
	companyTrainTable.find("div[class='m']").remove();
	form.find("li").not(":first").remove();//移除多余元素
	
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'employeeId':$("#id").val(),'isCompanyTraining':'1'},
   		url : contextPath + "/empTraining/getListByCondition.htm",
   		success : function(data) {
   			if(data != null && data !=''){
   				$(data).each(function(index){
   					var rowDiv =  '<div class="m"><span class="r-input" style="display:none" id="id">'+data[index].id
						+'</span><span class="r-input" style="display:none" id="version">'+data[index].version
						+'</span><div class="moudle-ll"><div class="mk sd"><div class="l-title">培训时间</div><div class="r-input" id="startTime">'
   						+getValByUndefined(data[index].startTime)
   						+'</div></div><div class="mk sd text"><div class="l-title">培训项目/内容</div></div><div class="sd text">'
   						+getValByUndefined(data[index].trainingProName)
   						+'</div></div></div>';
   					
						companyTrainTable.append(rowDiv);
						
						rowDiv = '<li><div class="input-list"><div class="edit-y"></div>'+rowDiv+'</div></li>';
						form.find("ul").append(rowDiv);
   				})
   			}else{
					var rowDiv =  '<div class="m"><span class="r-input" style="display:none" id="id">'
						+'</span><span class="r-input" style="display:none" id="version">'
						+'</span><div class="moudle-ll"><div class="mk sd"><div class="l-title">培训时间</div><div class="r-input" id="startTime">--'
						+'</div></div><div class="mk sd text"><div class="l-title">培训项目/内容</div><div class="r-input" id="trainingProName">--'
						+'</div></div></div></div>';
					
					companyTrainTable.append(rowDiv);	
   			}
   		}
	});
}

//初始化业绩和奖惩
function initAchieverment(){
	var achievementTable = $("#achievementTable");

	var form = $("#achievementForm");
	achievementTable.find("div[class='m']").remove();
	form.find("li").not(":first").remove();//移除多余元素
	
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'employeeId':$("#id").val()},
   		url : contextPath + "/empAchievement/getListByCondition.htm",
   		success : function(data) {
   			if(data != null && data !=''){
   				$(data).each(function(index){
   					var rowDiv = '<div class="m"><span class="r-input" style="display:none" id="id">'+data[index].id
						+'</span><span class="r-input" style="display:none" id="version">'+data[index].version
						+'</span><div class="moudle-ll"><div class="mk sd"><div class="l-title">奖惩时间</div><div class="r-input" id="processTime">'
   						+getValByUndefined(data[index].processTime)
   						+'</div></div><div class="mk sd"><div class="l-title">奖惩内容</div><div class="r-input" id="content">'
   						+getValByUndefined(data[index].content)
   						+'</div></div></div></div>';
   					
						achievementTable.append(rowDiv);
						
						rowDiv = '<li><div class="input-list"><div class="edit-y"></div>'+rowDiv+'</div></li>';
						form.find("ul").append(rowDiv);
						
   				})
   			}else{
					var rowDiv = '<div class="m"><span class="r-input" style="display:none" id="id">--'
					    +'</span><span class="r-input" style="display:none" id="version">--'
					    +'</span><div class="moudle-ll"><div class="mk sd"><div class="l-title">奖惩时间</div><div class="r-input" id="processTime">--'
						+'</div></div><div class="mk sd"><div class="l-title">奖惩内容</div><div class="r-input" id="content">--'
						+'</div></div></div></div>';
					
				    	achievementTable.append(rowDiv);
   			}
   		}
	});
}

//初始化工作经历
function initWork(){
	var workTable = $("#workTable");

	var form = $("#workForm");
	workTable.find("div[class='m']").remove();
	form.find("li").not(":first").remove();//移除多余元素
	
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'employeeId':$("#id").val()},
   		url : contextPath + "/empWorkRecord/getListByCondition.htm",
   		success : function(data) {
   			if(data != null && data !=''){
   				$(data).each(function(index){

   					var rowDiv='<div class="m"><span class="r-input" style="display:none" id="id">'+data[index].id
						+'</span><span class="r-input" style="display:none" id="version">'+data[index].version
						+'</span><div class="moudle-ll"><div class="timeDouble sd"><span class="r-input" id="startTime">'
   					    +getValByUndefined(data[index].startTime)+'</span>至<span class="r-input" id="endTime">'+getValByUndefined(data[index].endTime)
   					    +'</span></div><div class="mk sd"><div class="l-title">公司名称</div><div class="r-input" id="companyName">'
   					    +getValByUndefined(data[index].companyName)
   					    +'</div></div><div class="mk sd"><div class="l-title">职位</div><div class="r-input" id="positionName">'
   					    +getValByUndefined(data[index].positionName)
   					    +'</div></div><div class="mk sd"><div class="l-title">主办业务</div><div class="r-input" id="positionTask">'
   					    +getValByUndefined(data[index].positionTask)
   					    +'</div></div></div></div>';
   					
						workTable.append(rowDiv);
						
						rowDiv = '<li><div class="input-list"><div class="edit-y"></div>'+rowDiv+'</div></li>';
						form.find("ul").append(rowDiv);
   				})
   			}else{

					var rowDiv='<div class="m"><input type="hidden" id="id" value="'
					    +'"/><div class="moudle-ll"><div class="timeDouble sd"><span id="startTime">'
					    +'--</span>至<span id="endTime">'
					    +'--</span></div><div class="mk sd"><div class="l-title">公司名称</div><div class="r-input" id="companyName">'
					    +'--</div></div><div class="mk sd"><div class="l-title">职位</div><div class="r-input" id="positionName">'
					    +'--</div></div><div class="mk sd"><div class="l-title">主办业务</div><div class="r-input" id="positionTask">'
					    +'--</div></div></div></div>';
					
				    	workTable.append(rowDiv);	
   			}
   		}
	});
	return true;
}

//初始化培训经历
function initEmpTranning(){
	var tranningTable = $("#tranningTable");
	var form = $("#tranningForm");
	tranningTable.find("div[class='m']").remove();
	form.find("li").not(":first").remove();//移除多余元素
	
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'employeeId':$("#id").val(),'isCompanyTraining':'0'},
   		url : contextPath + "/empTraining/getListByCondition.htm",
   		success : function(data) {
   			if(data != null && data !=''){
   				$(data).each(function(index){

   					var rowDiv='<div class="m"><span class="r-input" style="display:none" id="id">'+data[index].id
						+'</span><span class="r-input" style="display:none" id="version">'+data[index].version
						+'</span><div class="moudle-ll"><div class="timeDouble sd"><span class="r-input" id="startTime">'
   					    +getValByUndefined(data[index].startTime)+'</span>至<span class="r-input" id="endTime">'+getValByUndefined(data[index].endTime)
   					    +'</span></div><div class="mk sd"><div class="l-title">培训机构</div><div class="r-input" id="trainingInstitutions">'
   					    +getValByUndefined(data[index].trainingInstitutions)
   					    +'</div></div><div class="mk sd"><div class="l-title">所获证书</div><div class="r-input" id="obtainCertificate">'
   					    +getValByUndefined(data[index].obtainCertificate)
   					    +'</div></div><div class="mk sd"><div class="l-title">培训内容</div><div class="r-input" id="content">'
   					    +getValByUndefined(data[index].content)
   					    +'</div></div></div></div>';
   					    
						tranningTable.append(rowDiv);
						
						rowDiv = '<li><div class="input-list"><div class="edit-y"></div>'+rowDiv+'</div></li>';
						form.find("ul").append(rowDiv);
   				})
   			}else{

					var rowDiv='<div class="m"><input type="hidden" id="id" value="'
				    	+'"/><div class="moudle-ll"><div class="timeDouble sd"><span id="startTime">'
					    +'--</span>至<span id="endTime">'
					    +'--</span></div><div class="mk sd"><div class="l-title">培训机构</div><div class="r-input" id="trainingInstitutions">'
					    +'--</div></div><div class="mk sd"><div class="l-title">所获证书</div><div class="r-input" id="obtainCertificate">'
					    +'--</div></div><div class="mk sd"><div class="l-title">培训内容</div><div class="r-input" id="content">'
					    +'--</div></div></div></div>';
					    
					    tranningTable.append(rowDiv);
   			}
   		}
	});
	return true;
}

//初始化教育经历
function initEmpSchool(){
	var schoolTable = $("#schoolTable");
	var form = $("#schoolForm");
	schoolTable.find("div[class='m']").remove();
	form.find("li").not(":first").remove();//移除多余元素
	
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'employeeId':$("#id").val()},
   		url : contextPath + "/empSchool/getListByCondition.htm",
   		success : function(data) {
   			if(data != null && data !=''){
   				$(data).each(function(index){
   					var rowDiv='<div class="m"><span class="r-input" style="display:none" id="id">'+data[index].id
						+'</span><span class="r-input" style="display:none" id="version">'+data[index].version
						+'</span><span class="r-input" style="display:none" id="degree">'+data[index].degree
						+'</span><div class="moudle-ll"><div class="timeDouble sd"><span id="startTime" class="r-input">'
   					    +getValByUndefined(data[index].startTime)+'</span>至<span id="endTime" class="r-input">'+getValByUndefined(data[index].endTime)
   					    +'</span></div><div class="mk sd"><div class="l-title">学校名称</div><div class="r-input" id="school">'
   					    +getValByUndefined(data[index].school)
   					    +'</div></div><div class="mk sd"><div class="l-title">学历</div><div class="r-input" id="education">'//
   					    +getValByUndefined(data[index].educationName)
   					    +'</div></div><div class="mk sd"><div class="l-title">学位</div><div class="r-input" id="degreeName">'//
   					    +getValByUndefined(data[index].degreeName)
   					    +'</div></div><div class="mk sd"><div class="l-title">专业</div><div class="r-input" id="major">'
   					    +getValByUndefined(data[index].major)
   					    +'</div></div></div></div>';
   					
						schoolTable.append(rowDiv);

						rowDiv = '<li><div class="input-list"><div class="edit-y"></div>'+rowDiv+'</div></li>';
						
					    form.find("ul").append(rowDiv);
   				})
   			}else{
					var rowDiv='<div class="m"><input type="hidden" id="id" value="'
					    +'"/><div class="moudle-ll"><div class="timeDouble sd"><span id="startTime">'
					    +'--</span>至<spanid="endTime">'
					    +'--</span></div><div class="mk sd"><div class="l-title">学校名称</div><div class="r-input" id="school">'
					    +'--</div></div><div class="mk sd"><div class="l-title">学历</div><div class="r-input" id="education">'
					    +'--</div></div><div class="mk sd"><div class="l-title">学位</div><div class="r-input" id="degree">'
					    +'--</div></div><div class="mk sd"><div class="l-title">专业</div><div class="r-input" id="major">'
					    +'--</div></div></div></div>';
					
					schoolTable.append(rowDiv);
   			}
   		}
	});
	return true;
}

//初始化紧急联系人
function initUrgentContact(){
	var urgentContactTable = $("#urgentContactTable");

	var form = $("#urgentContactForm");
	urgentContactTable.find("div[class='m']").remove();
	form.find("li").not(":first").remove();//移除多余元素
	
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'employeeId':$("#id").val()},
   		url : contextPath + "/empUrgentContact/getListByCondition.htm",
   		success : function(data) {
   			if(data != null && data !=''){
   				$(data).each(function(index){
   					var rowDiv = '<div class="m"><span class="r-input" style="display:none" id="id">'+data[index].id
						+'</span><span class="r-input" style="display:none" id="version">'+data[index].version
						+'</span><div class="moudle-ll"><div class="mk sd"><div class="l-title">关系</div><div class="r-input" id="shortName">'
   						+getValByUndefined(data[index].shortName)
   						+'</div></div><div class="mk sd"><div class="l-title">姓名</div><div class="r-input" id="name">'
   						+getValByUndefined(data[index].name)
   						+'</div></div><div class="mk sd"><div class="l-title">手机</div><div class="r-input" id="mobile">'
   						+getValByUndefined(data[index].mobile)
   						+'</div></div><div class="mk sd"><div class="l-title">座机</div><div class="r-input" id="telphone">'
   						+getValByUndefined(data[index].telphone)
   						+'</div></div></div></div>';
   					
						urgentContactTable.append(rowDiv);

						rowDiv = '<li><div class="input-list"><div class="edit-y"></div>'+rowDiv+'</div></li>';
					    form.find("ul").append(rowDiv);
   				})
   			}else{
					var rowDiv = '<div class="m"><input type="hidden" id="id" value="'
					    +'"/><div class="moudle-ll"><div class="block sd friend" id="shortName">'
						+'--</div><div class="block sd name" id="name">'
						+'--</div><div class="block sd phone" id="mobile">'
						+'--</div></div></div>';
					
					    urgentContactTable.append(rowDiv);
   			}
   		}
	});
	return true;
}

function getVal(mobile,tel){
	if("" == mobile || null == mobile){
		return tel;
	}
	
	return mobile;
}

//初始化家庭成员信息
function initFamilyMember(){
	var spouseTable = $("#spouseTable");
	var childTable = $("#childTable");
	
	var spouseForm = $("#spouseForm");
	var childForm = $("#childForm");
	
	spouseTable.find("div[class='m']").remove();
	spouseForm.find("li").not(":first").remove();//移除多余元素
	childTable.find("div[class='m']").remove();
	childForm.find("li").not(":first").remove();//移除多余元素
	
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'employeeId':$("#id").val()},
   		url : contextPath + "/empFamilyMember/getListByCondition.htm",
   		success : function(data) {
   			if(data != null && data !=''){

   				spouseForm.find(".add-type").show();
   				$(data).each(function(index){
   					if("0" == data[index].relation){//配偶
   						var rowDiv1  = '<div class="m"><span class="r-input" style="display:none" id="id">'+data[index].id
							+'</span><span class="r-input" style="display:none" id="version">'+data[index].version
							+'</span><span class="r-input" style="display:none" id="relation">'+data[index].relation
							+'</span><div class="moudle-ll"><div class="mk sd"><div class="l-title">配偶姓名</div><div class="r-input" id="memberName">'
   							+getValByUndefined(data[index].memberName)
   							+'</div></div><div class="mk sd"><div class="l-title">手机号码</div><div class="r-input" id="memberTelphone">'
   							+getValByUndefined(data[index].memberTelphone)
   							+'</div></div><div class="mk sd"><div class="l-title">工作单位</div><div class="r-input" id="memberCompanyName">'
   							+getValByUndefined(data[index].memberCompanyName)
   							+'</div></div></div></div>';
   						
   						    spouseTable.append(rowDiv1);

							rowDiv1 = '<li><div class="input-list"><div class="edit-y"></div>'+rowDiv1+'</div></li>';
							spouseForm.find(".add-type").hide();//配偶不可新增
							spouseForm.find("ul").append(rowDiv1);
   					}else if("1" == data[index].relation){//子女
   						var rowDiv2 = '<div class="m"><span class="r-input" style="display:none" id="id">'+data[index].id
							+'</span><span class="r-input" style="display:none" id="version">'+data[index].version
							+'</span><span class="r-input" style="display:none" id="memberSex">'+data[index].memberSex
							+'</span><span class="r-input" style="display:none" id="relation">'+data[index].relation
							+'</span><div class="moudle-ll"><div class="mk sd"><div class="l-title">子女姓名</div><div class="r-input" id="memberName">'
   							+getValByUndefined(data[index].memberName)
   							+'</div></div><div class="mk sd"><div class="l-title">出生日期</div><div class="r-input" id="birthday">'
   							+getValByUndefined(data[index].birthday)
   							+'</div></div><div class="mk sd"><div class="l-title">子女性别</div><div class="r-input" id="memberSexName">'
   							+("0" == getValByUndefined(data[index].memberSex) ? "男" : "女")
   							+'</div></div></div></div>';
	   					
   						    childTable.append(rowDiv2);

							rowDiv2 = '<li><div class="input-list"><div class="edit-y"></div>'+rowDiv2+'</div></li>';
							childForm.find("ul").append(rowDiv2);
   					}
   				})
   				
   			}else{
					var rowDiv1 = '<div class="m"><input type="hidden" id="id" value="'
					    +'"/><div class="moudle-ll"><div class="block sd name" id="memberName">'
						+'--</div><div class="block sd phone" id="memberTelphone">'
						+'--</div><div class="block sd address" id="memberCompanyName">'
						+'--</div></div></div>';
					
					spouseTable.append(rowDiv1);
					
					var rowDiv2 = '<div class="m"><input type="hidden" id="id" value="'
				    	+'"/><div class="moudle-ll"><div class="block sd sex" id="memberSex">'
						+'--</div><div class="block sd name" id="memberName">'
						+'--</div><div class="block sd birthday" id="birthday">'
						+'--</div></div></div>';
				
					childTable.append(rowDiv2);
   				
   			}
   		}
	});
	return true;
}

//初始化员工相关信息
function initEmployee(){
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'id':$("#id").val()},
   		url : contextPath + "/employeeApp/getEmpInfo.htm",
   		success : function(data) {
   			if(data != null && data !=''){
   				//照片
   				$("#empId").val(data.id);
   				if(typeof data.picture != 'undefined' && data.picture != ''){
   	   				$("#ePic").css("background-image","url(" + data.picture + ")");
   				}
   				
   				//基本信息
   				var span = $('#empBaseTable,#empJobTable').find("div .r-input");
   				var input = $("#baseInfoForm,#baseJobForm").find("input");
   				//console.info("version:"+data.version);
   				$("#baseInfoForm .version").val(data.version);
   				$("#remarkForm .empVersion").val(data.version);
   				
   				$(span).each(function(i,domEle){
   					var id = $(this).attr("id");
   					
   					$(this).empty();
   					$(this).text(data[id]);
   				});
   				
   				$(input).each(function(i,domEle){
   					var className = $(this).attr("class");
   					$(this).val(data[className]);
   				});
   				
   				$("div #empBaseTable #sex").empty();
   				if(data.sex == 0){
   					$("#spouseForm #spouseSex").val('1');//配偶性别必须与本人相反
   					$("div #empBaseTable #sex").append("男");
   					$("#baseInfoForm #sexName").val("男");
   				}else if(data.sex == 1){
   					$("#spouseForm #spouseSex").val('0');//配偶性别必须与本人相反
   					$("div #empBaseTable #sex").append("女");
   					$("#baseInfoForm #sexName").val("女");
   				}
   				//在职信息
   				
   				$("#empJobTable #jobStatus").empty();
   				if(data.jobStatus == 0){
   					$("#empJobTable #jobStatus").text("在职");
   					$("#baseJobForm #jobStatusName").val("在职");
   				}else if(data.jobStatus == 1){
   					$("#empJobTable #jobStatus").text("离职");
   					$("#baseJobForm #jobStatusName").val("离职");
   				}else if(data.jobStatus == 2){
   					$("#empJobTable #jobStatus").text("待离职");
   					$("#baseJobForm #jobStatusName").val("待离职");
   				}else{
   					$("#empJobTable #jobStatus").text("其它");
   					$("#baseJobForm #jobStatusName").val("其它");
   				}
   				$("#cnName").text(data.cnName);
//   				$("#code").append(data.code);
   				$("#empJobTable #companyName").text(data.company.name);
   				$("#empJobTable #departName").text(data.depart.name);
   				$("#empJobTable #employeeLeader").text(data.reportToLeaderName);
   				$("#empJobTable #departLeader").text(data.depart.leaderName);
   				$("#empJobTable #positionName").text(data.position.positionName);
   				$("#empJobTable #positionSeqName").text(data.positionSeq);
   				$("#empJobTable #positionLevelName").text(data.positionLevel);
   				$("#empJobTable #probationExpire").text(data.empContract.probationExpire);
   				$("#empJobTable #contractEndTime").text(data.empContract.contractEndTime);
   				$("#empJobTable #quitTime").text(data.quitTime);
   				//从业背景
   				$("#workingBackground").text(data.workingBackground);
   				$("#industryRelevanceName").text(data.industryRelevanceName);
   				//备注
   				$("#remark").text(data.remark);
   				$("#remarkForm #remark").val(data.remark);
   				
   				//编辑页面的值
   				//$("#baseInfoForm .birthday").val(data.birthday);
   				$("#baseJobForm .empVersion").val(data.version);//员工表版本号
   				$("#baseJobForm .positionVersion").val(data.position.version);//员工岗位表
   				$("#baseJobForm .departVersion").val(data.depart.version);//员工岗位表
   				
   				$("#baseJobForm .departId").val(data.depart.id);
   				$("#baseJobForm .companyName").val(data.company.name);
   				$("#baseJobForm .departName").val(data.depart.name);
   				$("#baseJobForm .reportToLeader").val(data.reportToLeader);//
   				$("#baseJobForm .reportToLeaderName").val(data.reportToLeaderName);
   				$("#baseJobForm .leaderName").val(data.depart.leaderName);
   				$("#baseJobForm .leader").val(data.depart.leader);
   				
   				$("#baseJobForm .positionId").val(data.position.id);
   				$("#baseJobForm .positionName").val(data.position.positionName);
   				$("#baseJobForm .positionSeqName").val(data.positionSeq);
   				$("#baseJobForm .positionLevelName").val(data.positionLevel);
   				$("#baseJobForm .probationExpire").val(data.empContract.probationExpire);
   				$("#baseJobForm .contractEndTime").val(data.empContract.contractEndTime);
   				$("#baseJobForm .quitTime").val(data.quitTime);
   				
   				var ourAge = data.ourAge;
   				var ourAgeShow = ourAgeShowFunction(ourAge)+"年";
   				var beforeWorkAge = data.beforeWorkAge;
   				var workAge = data.workAge;
   				var workAgeShow = ourAgeShowFunction(workAge)+"年";
   				var beforeWorkAgeShow = beforeWorkAge+"年";
   				$("#empJobTable #ourAge").text(ourAgeShow);
   				$("#baseJobForm #ourAge").val(ourAgeShow);
   				$("#empJobTable #workAge").text(workAgeShow);
   				$("#baseJobForm #workAge").val(workAgeShow);
   				$("#empJobTable #beforeWorkAge").text(beforeWorkAgeShow);
   				$("#baseJobForm #beforeWorkAge").val(data.beforeWorkAge);
   				$("#empJobTable #workAddress").text(data.workAddressProvince+" "+data.workAddressCity);

   				var birthday = data.birthday;
   				var today = new Date().format("yyyy-mm-dd"); 
   				var year = getAge(birthday);

   				$("#empBaseTable #age").text(year);
				$("#baseInfoForm #age").val(year);
				
				//控制 其它 显示
			    $(".countryOtherDiv").hide();
			    $(".industryRelevanceOtherDiv").hide();
			    $(".degreeOfEducationOtherDiv").hide();
			    $(".politicalStatusOtherDiv").hide();
   				
   				//隐藏域赋值
   				$("#companyId").val(data.companyId);
   				$("form #companyId").val(data.companyId);
   				
   				readyEdit(data.companyId);
   				editEvent();
   			}

   			//加载通行证帐号
   			initUserName();
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