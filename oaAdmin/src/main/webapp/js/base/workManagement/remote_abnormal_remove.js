$(function(){

	//默认查询所有
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});
	var currentCompanyId = $("#currentCompanyId").val();
	//初始化工时类型
	initWorkType(currentCompanyId);

	//初始化部门
	getFirstDepart();
	getFirstDepart("firstDepartOfUpd");
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		getSecondDepart(this.value);
		initEmployeeLeader(this.value);
	});
	$("#checkAll").click(function(){
		if (this.checked) {
			$(".tdCheck").attr("checked",true);
		}else{
			$(".tdCheck").attr("checked",false);
		}
	});
});

function getPassDiv(obj,type){
	$("#employeeIds").val("");
	$("#registerDates").val("");
	$("textarea[name='approvalReason']").val("");
	var employeeIds = "";
	var registerDates = "";
	if(type=="1"){
		$('.tdCheck').each(function(index) {
			if ($(this).attr('checked') == 'checked') {
				employeeIds = employeeIds + $(this).attr("employId") + ",";
				registerDates = registerDates + $(this).attr("date") + ",";
			}
		});
		
		if(employeeIds != ""){
			employeeIds = employeeIds.substring(0, employeeIds.length-1);
		}
		if(registerDates != ""){
			registerDates = registerDates.substring(0, registerDates.length-1);
		}
		
	}else{
		employeeIds =  $(obj).attr("employId");
		registerDates = $(obj).attr("date");
		$("input[name='pssStatus'][value="+$(obj).attr("approvalStatus")+"]").attr("checked",true); 
		var reason = $(obj).attr("reason");
		if(reason==null||reason=="null"){
			reason="";
		}
		$("textarea[name='approvalReason']").val(reason);
	}
	$("#employeeIds").val(employeeIds);
	$("#registerDates").val(registerDates);
	$("#updateReporterDiv").show();
}

function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=20";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "remoteAbnormalRemove/getPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				//console.log("数据："+JSON.stringify(response.rows[i]));
				html += "<tr>";
				html += "<td style='text-align:cneter;'><input class='tdCheck' type ='checkbox' employId='"+response.rows[i].employeeId+"' date = '"+response.rows[i].registerDate+"'/></td>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(formatStr(response.rows[i].departName))+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].reportToLeaderName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].workTypeName)+"</td>";
				var className = getValByUndefined(response.rows[i].className);
				if(className!=""){
					var classStartTime = getValByUndefined(response.rows[i].classStartTime);
					var classEndTime = getValByUndefined(response.rows[i].classEndTime);
					var classStr = className+"："+classStartTime+"-"+classEndTime;
					html += "<td style='text-align:center;'>"+classStr+"</td>";
				}else{
					html += "<td style='text-align:center;'>&nbsp;</td>";
				}
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].registerDate)+"</td>";
                var approvalStatus = response.rows[i].approvalStatus;
                var approvalStatusName = "";
                if(approvalStatus==0){
                	approvalStatusName="通过";
                }else if(approvalStatus==1){
                	approvalStatusName="不通过";
                }else{
                	approvalStatusName="未提交";
                }
                html += "<td style='text-align:center;'>"+approvalStatusName+"</td>";
                if(approvalStatus==0||approvalStatus==1){
                	html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].updateUser)+"</td>";
                }else{
                	html += "<td style='text-align:center;'>&nbsp;</td>";
                }
				html += "<td style='text-align:left;'><a style='color:blue;' employId='"+response.rows[i].employeeId+"' date = '"+response.rows[i].registerDate+"' reason = '"+response.rows[i].reason+"' approvalStatus = '"+response.rows[i].approvalStatus+"' onclick='getPassDiv(this,2)' >审阅</a></td>";
				
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

var formatStr = function(o){
	
	var result = "";
	if(typeof(o) == "undefined"){
		
	}else{
		result = o;
	}
	return result;
}

var closeDiv = function(divName){
	$("#"+divName).hide();
	$("#employeeIds").val("");
	$("#registerDates").val("");
}

var updateBatch = function(){	
	pageLoading(true);//开启动画
	var employeeIds = $("#employeeIds").val();
	var registerDates = $("#registerDates").val();
	if('' == employeeIds){
		pageLoading(false);//关闭动画
		JEND.page.alert({type:"error", message:"请至少选择一个需要审阅的单据！"});
		return;
	}
	//审阅状态
	var approvalStatus = $("input[name='pssStatus']:checked").val();
	var approvalReason = $("textarea[name='approvalReason']").val();
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: {employeeIds:employeeIds,registerDates:registerDates,approvalStatus:approvalStatus,approvalReason:approvalReason},
		url:  basePath + "remoteAbnormalRemove/review.htm",
		success:function(response){
			JEND.page.alert({type:"success", message:response.msg});		
			closeDiv('updateReporterDiv');
			gotoPage($("#pageNo").val());
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	})
} 