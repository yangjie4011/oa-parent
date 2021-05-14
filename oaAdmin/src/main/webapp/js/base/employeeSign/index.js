$(function(){
	//查询按钮点击事件
	$("#query").click(function(){
		gotoPage(1);
	});
	getSysTemTime("HH:mm");
	setInterval(function() {   
		getSysTemTime("HH:mm");
	}, 1000); 
});

function getSysTemTime(type){
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: {type:type},
		url:  basePath + "/employeeSign/getSystemTime.htm",
		success:function(response){
			if(response.sucess){
				$("#currentTime").html(response.msg);  
			}
		}
	});
}

//分页查询
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
		url:  basePath + "/employeeSign/getList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].code)+"</td>";//员工编号
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].name)+"</td>";//姓名
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].departName)+"</td>";//部门
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].positionName)+"</td>";//职位
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].reportToLeader)+"</td>";//汇报对象
				html += "<td style='text-align:center;'><a style='color:blue;' href='#' onclick='getSignDiv("+JSON.stringify(response.rows[i])+");'>立即签到</a></td>";
				html += "</tr>";
			}
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}

function getSignDiv(obj){
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: {type:"yyyy年MM月dd日 HH:mm"},
		url:  basePath + "/employeeSign/getSystemTime.htm",
		success:function(response){
			if(response.sucess){
				$("#confirmTitle").html(obj.name+"，确认今天"+response.msg.substring(0,11)+"的签到时间是"+response.msg.substring(12,17)+"吗？");
			}
		}
	});
	$("#employeeId").val(obj.id);
	$("#signDiv").show();
}

function sign(){
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data :{employeeId:$("#employeeId").val()},
 		url : contextPath + "/employeeSign/sign.htm",
 		success : function(response) {
 			if(response.sucess){
 				$("#signDiv").hide();
 				$("#successDiv").show();
 			}else{
 				JEND.page.showError(response.msg);
 			}
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
			$("#singCode").val("");
			$("#singName").val("");
			$("#reportList").empty();
			$("#commonPage").empty();
        }
 	});
}

function closeDiv(id){
	$("#"+id).hide();
}

