$(function(){
	gotoPage();
	//查询按钮点击事件
	$("#query").click(function(){
		gotoPage(1);
	});
});

function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=10";
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "/empLeave/getLeaveRadixList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].code)+"</td>";//员工编号
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].name)+"</td>";//姓名
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].entryTime)+"</td>";//入职日期
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].departName)+"</td>";//部门
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].workTypeName)+"</td>";//工时制
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].legalCount)+"</td>";//法定年假基数
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].welfareCount)+"</td>";//福利年假基数
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveCount)+"</td>";//年假总数
				html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getUpdateDiv("+JSON.stringify(response.rows[i])+");'>修改</a></td>";
				html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getRecordDiv("+JSON.stringify(response.rows[i])+");'>详情</a></td>";
				html += "</tr>";
			}
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}

function getUpdateRecord(){
	pageLoading(true);//加载动画
	
	var data = "employeeId="+ $("#employeeId").val();
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "/updateLog/queryRaduixLog.htm",
		success:function(response){
			$("#recordList").empty();
			var html = "";
			for(var i=0;i<response.data.length;i++){
				var jsonInfo = JSON.parse(response.data[i].jsonInfo);
				html += "<tr>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.data[i].createTime)+"</td>";//修改日期
				html += "<td style='text-align:center;'>"+jsonInfo.beforeLegalRaduix+"</td>";//修改前法定基数
				html += "<td style='text-align:center;'>"+jsonInfo.afterLegalRaduix+"</td>";//修改后法定基数
				html += "<td style='text-align:center;'>"+jsonInfo.beforeWelfareRaduix+"</td>";//修改前福利基数
				html += "<td style='text-align:center;'>"+jsonInfo.afterWelfareRaduix+"</td>";//修改后福利基数				
				if(jsonInfo.updateType==0){
					html += "<td style='text-align:center;'>仅当年修改</td>";//修改类型
				}else{
					html += "<td style='text-align:center;'>永久修改</td>";//修改类型
				}
				html += "<td style='text-align:center;'>"+getValByUndefined(jsonInfo.remarkRecord)+"</td>";//修改记录备注
				html += "<td style='text-align:center;'>"+getValByUndefined(response.data[i].updateEmployeeName)+"</td>";//操作人
				html += "</tr>";
			}
			$("#recordList").append(html);
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}

function getUpdateDiv(obj){
	$("#employeeId").val(obj.id);
	$("#legalCount").val(obj.legalCount);
	$("#welfareCount").val(obj.welfareCount);
	$("#leaveCount").val(obj.leaveCount);
	$("#codeUpdate").val(obj.code);
	$("#nameUpdate").val(obj.name);
	
	$("#updateDiv").show();
}

function getRecordDiv(obj){
	$("#remarkRecord").val("");
	$("#employeeId").val(obj.id);
	$("#nameQuery").html(obj.name);
	$("#codeQuery").html(obj.code);
	getUpdateRecord();
	$("#recordDiv").show();
}

function closeDiv(){
	$("#updateDiv").hide();
	$("#recordDiv").hide();
}

function getLeaveCount(){
	var legalCount = $("#legalCount").val();
	var welfareCount = $("#welfareCount").val();
	$("#leaveCount").val(parseInt(legalCount)+parseInt(welfareCount));
}

//修改年休假基数
function updateLeaveRadix(){
	if((parseInt($("#legalCount").val())+parseInt($("#welfareCount").val()))>15){
		JEND.page.showError('超出年假上限无法提交！');
		return;
	}
	pageLoading(true);//加载动画
	$.ajax({
 		async : false,
 		type : "post",
		dataType:"json",
		data : {employeeId:$("#employeeId").val(),legalRaduix:$("#legalCount").val(),
			welfareRaduix:$("#welfareCount").val(),updateType:$("input[name='updateType']:checked").val(),remarkRecord:$("#remarkRecord").val()},
 		url : contextPath + "/empLeave/updateLeaveRadix.htm",
 		success : function(response) {
 			if(response.sucess){
 				gotoPage($("#pageNo").val());
 			}else{
 				JEND.page.showError(response.msg);
 			}
		},
		complete:function(XMLHttpRequest,textStatus){  
			closeDiv();
			pageLoading(false);//关闭动画
        }
 	});
}