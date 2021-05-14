$(function(){
	$("#yearAndMonth").val(getLastMonthStartDate("yyyy-MM"));
	//默认查询所有
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});
	var currentCompanyId = $("#currentCompanyId").val();
	//初始化工时类型
	initWorkType(currentCompanyId);
	//初始化员工类型
	initEmployeeType(currentCompanyId);
	//初始化是否排班
	initWhetherScheduling();
	//初始化单据状态
	initApprovalStatus(currentCompanyId);
	//初始化部门
	getFirstDepart();
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		getSecondDepart(this.value);
	});
	
	$("#export").click(function() {
		var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
		window.location.href = basePath + "empApplicationOvertime/exportSumReport.htm?"+data;
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
		url:  basePath + "empApplicationOvertime/getApplyOverTimeSumReportByPage.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cn_name+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].depart_name+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].position_name+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].month+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].time1+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].time2+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].time3+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].actual_duration+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].remainRest+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].meals+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].trafficMeals1+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].trafficMeals2+"</td>";
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