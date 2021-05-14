$(function(){
//	gotoPage();
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
		url:  basePath + "empLeave/getLeaveRecordList.htm",
		success:function(response){
			$("#reportList").empty();
			$("#employeeList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveRecordId)+"</td>";//流水编号
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].employeeCode)+"</td>";//员工编号
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].employeeName)+"</td>";//员工姓名
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveTypeText)+"</td>";//假期类型
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].childLeaveType)+"</td>";//子类型
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaveYear)+"</td>";//年份
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].updateType)+"</td>";//修改类型
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].days)+"</td>";//数量
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].unitText)+"</td>";//单位
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].sourceText)+"</td>";//流水类型
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].createUser)+"</td>";//操作人
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].remark)+"</td>";//备注
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].createTime)+"</td>";//生产时间
				/*html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getRecordDiv("+JSON.stringify(response.rows[i])+");'>详情</a></td>";*/
				html += "</tr>";
			}
			$("#reportList").append(html);
			if(response.rows.length > 0){
				var html2 = "";
				html2 += "<tr>";
				html2 += "<td style='text-align:center;'>"+getValByUndefined(response.rows[0].empCode)+"</td>";//员工编号
				html2 += "<td style='text-align:center;'>"+getValByUndefined(response.rows[0].empName)+"</td>";//员工姓名
				html2 += "<td style='text-align:center;'>"+getValByUndefined(response.rows[0].departName)+"</td>";//部门名称
				html2 += "<td style='text-align:center;'>"+getValByUndefined(response.rows[0].firstEntryTime)+"</td>";//入职日期
				html2 += "<td style='text-align:center;'>"+getValByUndefined(response.rows[0].quitTime)+"</td>";//离职日期
				html2 += "<td style='text-align:center;'>"+getValByUndefined(response.rows[0].jobStatus)+"</td>";//在职状态
				html2 += "</tr>";
				$("#employeeList").append(html2);
			}
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



function getRecordDiv(obj){
	$("#leaveRecordId").val(obj.leaveRecordId);
	$("#nameQuery").html(obj.employeeName);
	$("#codeQuery").html(obj.employeeCode);
	getUpdateRecord();
	$("#recordDiv").show();
}

function closeDiv(){
	$("#recordDiv").hide();
}

