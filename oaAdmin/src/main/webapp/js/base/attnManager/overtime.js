$(function(){
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
		window.location.href = basePath + "empApplicationOvertime/exportReport.htm?"+data;
	});
	//上周
	$("#lastWeek").click(function(){
		setLastWeek("applyStartDate","applyEndDate", 'yyyy-MM-dd')
	});
	//本月
	$("#thisMonth").click(function(){
		setCurrentMonthTime("applyStartDate","applyEndDate", 'yyyy-MM-dd');
	});
	//上月
	$("#lastMonth").click(function(){
		setLastMonthTime("applyStartDate","applyEndDate", 'yyyy-MM-dd');
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
		url:  basePath + "empApplicationOvertime/getReportPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].positionName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].applyDate+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].dayofweek+"</td>";
				var applyType = "";
				if(response.rows[i].applyType==100){
					applyType = "项目";
				}else if(response.rows[i].applyType==200){
					applyType = "会议";
				}else if(response.rows[i].applyType==300){
					applyType = "日常工作";
				}else if(response.rows[i].applyType==400){
					applyType = "其他";
				}
				html += "<td style='text-align:center;'>"+applyType+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].expectStartTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].expectEndTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].expectDuration+"</td>";
				if(typeof(response.rows[i].actualStartTime)=="undefined"){
					html += "<td style='text-align:center;'>&nbsp;</td>";
				}else{
					html += "<td style='text-align:center;'>"+response.rows[i].actualStartTime+"</td>";
				}
				if(typeof(response.rows[i].actualEndTime)=="undefined"){
					html += "<td style='text-align:center;'>&nbsp;</td>";
				}else{
					html += "<td style='text-align:center;'>"+response.rows[i].actualEndTime+"</td>";
				}
				if(typeof(response.rows[i].actualDuration)=="undefined"){
					html += "<td style='text-align:center;'>&nbsp;</td>";
				}else{
					html += "<td style='text-align:center;'>"+response.rows[i].actualDuration+"</td>";
				}				
				if(typeof(response.rows[i].auditUser)=="undefined"){
					html += "<td style='text-align:center;'>&nbsp;</td>";
				}else{
					html += "<td style='text-align:center;'>"+response.rows[i].auditUser+"</td>";
				}
				html += "<td style='text-align:center;'>"+response.rows[i].submitDate+"</td>";
				var approvalStatus = "";
				if(response.rows[i].approvalStatus==100){
					approvalStatus = "待审批";
				}else if(response.rows[i].approvalStatus==200){
					approvalStatus = "已审批";
				}else if(response.rows[i].approvalStatus==300){
					approvalStatus = "已拒绝";
				}else if(response.rows[i].approvalStatus==400){
					approvalStatus = "已撤销";
				}else if(response.rows[i].approvalStatus==500){
					approvalStatus = "已失效";
				}
				html += "<td style='text-align:center;'>"+approvalStatus+"</td>";
				if(typeof(response.rows[i].reason)=="undefined"){
					html += "<td style='text-align:center;'>&nbsp;</td>";
				}else{
					html += "<td style='text-align:center;'>"+response.rows[i].reason+"</td>";
				}
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