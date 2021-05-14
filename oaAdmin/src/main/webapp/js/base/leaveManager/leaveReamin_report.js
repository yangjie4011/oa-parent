$(function(){
	//gotoPage(1);
	
	//查询按钮点击事件
	$("#query").click(function(){
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
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		getSecondDepart(this.value);
	});
});
//导出查询报表
function exportReport(){
	var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
	
	window.location.href = basePath + "/empLeave/exportReport.htm?" + data;
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
		url:  basePath + "/empLeave/getReportPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].employee.code)+"</td>";//员工编号
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].employee.cnName)+"</td>";//姓名
				if("" != getValByUndefined(response.rows[i].employee.depart)){
					html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].employee.depart.name)+"</td>";//部门
				}else{
					html += "<td style='text-align:center;'></td>";//部门
				}
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].employee.quitTime)+"</td>";//离职日期
				if("" != getValByUndefined(response.rows[i].employee.jobStatus) && response.rows[i].employee.jobStatus == "1"){
					html += "<td style='text-align:center;'>离职</td>";//在职状态
				}else{
					html += "<td style='text-align:center;'>在职</td>";//在职状态
				}
				html += "<td style='text-align:center;'>"+response.rows[i].leave1+"</td>";//法定年假总数-年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave2+"</td>";//法定年假总数-法定年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave3+"</td>";//法定年假总数-福利年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave4+"</td>";//法定年假总数-带薪病假
				html += "<td style='text-align:center;'>"+response.rows[i].leave5+"</td>";//剩余假期天数-去年法定年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave6+"</td>";//剩余假期天数-去年福利年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave7+"</td>";//剩余假期天数-当年法定年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave8+"</td>";//剩余假期天数-当年福利年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave9+"</td>";//剩余假期天数-年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave10+"</td>";//剩余假期天数-带薪病假
				html += "<td style='text-align:center;'>"+response.rows[i].leave11+"</td>";//剩余假期天数-调休小时数
				html += "<td style='text-align:center;'>"+response.rows[i].leave12+"</td>";//截止目前剩余假期天数-当年法定年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave13+"</td>";//截止目前剩余假期天数-当年福利年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave31+"</td>";//截止目前剩余假期天数-带薪病假
				html += "<td style='text-align:center;'>"+response.rows[i].leave14+"</td>";//当年已使用假期天数-法定年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave15+"</td>";//当年已使用假期天数-福利年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave16+"</td>";//当年已使用假期天数-年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave17+"</td>";//当年已使用假期天数-带薪病假
				html += "<td style='text-align:center;'>"+response.rows[i].leave18+"</td>";//当年已使用假期天数-非带薪病假
				html += "<td style='text-align:center;'>"+response.rows[i].leave19+"</td>";//当年已使用假期天数-事假
				html += "<td style='text-align:center;'>"+response.rows[i].leave20+"</td>";//当年已使用假期天数-调休小时数
				html += "<td style='text-align:center;'>"+response.rows[i].leave21+"</td>";//当年已使用假期天数-婚假
				html += "<td style='text-align:center;'>"+response.rows[i].leave22+"</td>";//当年已使用假期天数-丧假
				html += "<td style='text-align:center;'>"+response.rows[i].leave23+"</td>";//当年已使用假期天数-陪产假
				html += "<td style='text-align:center;'>"+response.rows[i].leave24+"</td>";//当年已使用假期天数-产前假
				html += "<td style='text-align:center;'>"+response.rows[i].leave25+"</td>";//当年已使用假期天数-产假
				html += "<td style='text-align:center;'>"+response.rows[i].leave26+"</td>";//当年已使用假期天数-哺乳假
				html += "<td style='text-align:center;'>"+response.rows[i].leave27+"</td>";//当年已使用假期天数-流产假
				html += "<td style='text-align:center;'>"+response.rows[i].leave28+"</td>";//当年已使用假期天数-其他
				html += "<td style='text-align:center;'>"+response.rows[i].leave29+"</td>";//透支假期天数-年假
				html += "<td style='text-align:center;'>"+response.rows[i].leave30+"</td>";//透支假期天数-带薪病假
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