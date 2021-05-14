//工时类型
var workTypeSelect = function (){
	var companyId = $("#companyId").val();
	var workType = {id:'workTypeSelect',values:[],keys:[]};
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'companyId':companyId,'code':'typeOfWork'},
		url:contextPath + "/companyConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				workType.values.push(data[index].displayName);
				workType.keys.push(data[index].id);
			});
		}
	});
	
	return workType;
}

/**
员工信息编辑页面公用的下拉JS，为了配合前端js下拉特殊数据封装.
by zhangjintao
**/

var countrySelect = function(){
	var country = {id:'countrySelect',values:[],keys:[]};
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'code':'country'},
		url:contextPath + "/companyConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				country.values.push(data[index].displayName);
				country.keys.push(data[index].id);
			});
		}
	});
	return country;
}

//民族
var nationSelect = function(){
	var nation = {id:'nationSelect',values:[],keys:[]};
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'code':'nation'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				nation.values.push(data[index].displayName);
				nation.keys.push(data[index].id);
			});
		}
	});
	return nation;
}

//婚姻状况
var maritalStatusSelect = function(){
	var maritalStatus = {id:'maritalStatusSelect',values:[],keys:[]};
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'code':'maritalStatus'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				maritalStatus.values.push(data[index].displayName);
				maritalStatus.keys.push(data[index].id);
			});
		}
	});
	return maritalStatus;
}

//从业相关性
var industryRelevanceSelect = function(){
	var companyId = $("#companyId").val();
	var industryRelevance = {id:'industryRelevanceSelect',values:[],keys:[]};
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'companyId':companyId,'code':'industryCorrelation'},
		url:contextPath + "/companyConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				industryRelevance.values.push(data[index].displayName);
				industryRelevance.keys.push(data[index].id);
			});
		}
	});
	return industryRelevance;
}

//学历
var educationSelect = function(){
	var education = {id:'educationSelect',values:[],keys:[]};
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'code':'educationLevel'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index,item) {
				education.values.push(item.displayName);
				education.keys.push(item.id);
			});
		}
	});
	return education;
	
}

//学位
var degreeSelect = function(){
	var degree = {id:'degreeSelect',values:[],keys:[]};
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'code':'degree'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index,item) {
				degree.values.push(item.displayName);
				degree.keys.push(item.id);
			});
		}
	});
	return degree;
	
}

var positionSelect = function (departId){ //职位
	
	var position = {id:'positionSelect',values:[],keys:[],levels:[]};
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'departId':departId},
		url:contextPath + "/position/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				position.values.push(data[index].positionName);
				position.keys.push(data[index].id);
				position.levels.push(data[index].companyPositionLevel.code);
			});
		}
	});
	return position;
}


var positionLevelSelect = function (positionType){ //职位级别
	
	var position = {id:'positionLevelSelect',values:[]};
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'positionType':positionType},
		url:contextPath + "/position/getLevelList.htm",
		success:function(data){
			$.each(data, function(index) {
				position.values.push(data[index]);
			});
		}
	});
	return position;
}

var positionSeqSelect = function (positionType){ //职位序列
	
	var position = {id:'positionSeqSelect',values:[]};
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'positionType':positionType},
		url:contextPath + "/position/getSeqList.htm",
		success:function(data){
			$.each(data, function(index) {
				position.values.push(data[index]);
			});
		}
	});
	return position;
}

var groupSelect = function (departId){ //排班组别
	
	var group = {id:'groupSelect',values:[],keys:[]};
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'departId':departId},
		url:contextPath + "/depart/getGroupListByDepartId.htm",
		success:function(data){
			$.each(data, function(index) {
				group.values.push(data[index].name);
				group.keys.push(data[index].id);
			});
		}
	});
	return group;
}

var politicalStatusSelect = function (){ //政治面貌
	
	var politicalStatus = {id:'politicalStatusSelect',values:[],keys:[]};
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'code':'politicalStatus'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				politicalStatus.values.push(data[index].displayName);
				politicalStatus.keys.push(data[index].id);
			});
		}
	});
	return politicalStatus;
}

var reporterSelect = function (id){ //汇报对象下拉
	
	var reporter = {id:'reporterSelect',values:[],keys:[]};
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'id':id},
		url:contextPath + "/employee/getReportPersonList.htm",
		success:function(data){
			$.each(data, function(index) {
				reporter.values.push(data[index].cnName);
				reporter.keys.push(data[index].id);
			});
		}
	});
	return reporter;
}

var companySelect = function(){
	
	var company = {id:'companySelect',values:[],keys:[]};
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'sId':Math.random()},
		url:contextPath + "/company/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index,item) {
				company.values.push(item.name);
				company.keys.push(item.id);
			});
		}
	});
	return company;
}

var empTypeSelect = function(companyId){
	
	var empType = {id:'empTypeSelect',values:[],keys:[]};
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'companyId':companyId},
		url:contextPath + "/empType/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index,item) {
				empType.values.push(item.typeCName);
				empType.keys.push(item.id);
			});
		}
	});
	return empType;
}

var whetherSchedulSelect = function(){
	
	var whetherSchedul = {id:'whetherSchedulSelect',values:[],keys:[]};
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'code':'whetherScheduling'},
		url:contextPath + "/sysConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index,item) {
				whetherSchedul.values.push(item.displayName);
				whetherSchedul.keys.push(item.id);
			});
		}
	});
	return whetherSchedul;
}