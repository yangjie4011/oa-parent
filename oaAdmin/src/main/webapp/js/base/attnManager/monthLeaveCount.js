$(function(){	
	$("#yearAndMonth").val(getMonthStartDate("yyyy-MM"));	
	//默认查询所有
	gotoPage(1);
	//查询按钮点击事件
	$("#query").click(function(){
		var month=$("#yearAndMonth").val();
		if(month==null ||month==""){
			JEND.page.showError("请选择查询的月份");
			return;
		}
		gotoPage(1);
	});
	//导出点击事件
	$("#export").click(function(){
		exportReport();
	});
	var currentCompanyId = $("#currentCompanyId").val();
	//初始化工时类型
	initWorkType(currentCompanyId);
	//初始化单据状态
	initApprovalStatus(currentCompanyId);
	//初始化部门
	getFirstDepart();
});

//导出查询报表
function exportReport(){
	var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
	
	window.location.href = basePath + "/monthLeaveCount/exportReport.htm?" + data;
}

//分页查询外出报表
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
		url:  basePath + "/monthLeaveCount/getReportPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			
			var rows = response.mapList;
			if(rows!=null){
				$.each(rows,function(i,item){
					$.each(item,function(key,value){
						html += "<tr>"		
						 +  "<td>"+ value.code+"</td>"
					     +  "<td>"+ value.cnName+"</td>"
					     +  "<td>"+ value.departName +"</td>"					     
					     +  "<td>"+ value.count_1 +"</td>"
					     +  "<td>"+ value.days_1 +"</td>"
					     +  "<td>"+ value.count_2 +"</td>"
					     +  "<td>"+ value.days_2 +"</td>"
					     +  "<td>"+ value.count_3+"</td>"
					     +  "<td>"+ value.days_3+"</td>"
					     +  "<td>"+ value.count_5+"</td>"
					     +  "<td>"+ value.days_5+"</td>"
					     +  "<td>"+ value.count_6+"</td>"					     
					     +  "<td>"+ value.days_6+"</td>"
					     +  "<td>"+ value.count_7+"</td>"
					     +  "<td>"+ value.days_7+"</td>"
					     +  "<td>"+ value.count_8+"</td>"
					     +  "<td>"+ value.days_8+"</td>"
					     +  "<td>"+ value.count_9+"</td>"
					     +  "<td>"+ value.days_9+"</td>"    
					     +  "<td>"+ value.count_10+"</td>"
					     +  "<td>"+ value.days_10+"</td>"
					     +  "<td>"+ value.count_11+"</td>"
					     +  "<td>"+ value.days_11+"</td>"
					     +"</tr>";
					});
				});
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