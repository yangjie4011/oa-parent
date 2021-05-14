$(function(){
	//默认查询所有
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});
	var currentCompanyId = $("#currentCompanyId").val();
	//初始化公司下拉
	//getCompany();
	//初始化部门
	getFirstDepart();
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		getSecondDepart(this.value);
	});
	
	$("#export").click(function() {
		var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
		window.location.href = basePath + "entry/exportReport.htm?"+data;
	});	
	//上周
	$("#lastWeekFirst").click(function(){
		setLastWeek("employmentDate","employmentDateEnd", 'yyyy-MM-dd')
	});
	$("#lastWeek").click(function(){
		setLastWeek("createTime","createTimeEnd", 'yyyy-MM-dd')
	});
	//本月
	$("#thisMonthFirst").click(function(){
		setCurrentMonthTime("employmentDate","employmentDateEnd", 'yyyy-MM-dd');
	});
	$("#thisMonth").click(function(){
		setCurrentMonthTime("createTime","createTimeEnd", 'yyyy-MM-dd');
	});
	//上月
	$("#lastMonthFirst").click(function(){
		setLastMonthTime("employmentDate","employmentDateEnd", 'yyyy-MM-dd');
	});
	$("#lastMonth").click(function(){
		setLastMonthTime("createTime","createTimeEnd", 'yyyy-MM-dd');
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
		url:  basePath + "entry/getReportPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].name)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].typeCName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].cnName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].employmentDate)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].dpname)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaderName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].deptLeaderName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].positionName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].email)+"</td>"	
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].createTime)+"</td>";				
				var entryStatus = "";
				if(response.rows[i].entryStatus==1 || response.rows[i].entryStatus==4){
					entryStatus = "未入职";
				}else if(response.rows[i].entryStatus==2){
					entryStatus = "待入职";
				}else if(response.rows[i].entryStatus==3){
					entryStatus = "已入职";
				}
				html += "<td style='text-align:center;'><a href='#'>"+getValByUndefined(entryStatus)+"</a></td>";	
				if(response.rows[i].delayEntryDate== undefined){
					html += "<td style='text-align:center;'> 否 </td>";
				}else{
					html += "<td style='text-align:center;'> 是 </td>";
				}
				if(response.rows[i].canCancel==0){
					html += "<td style='text-align:center;'><a id="+response.rows[i].id+" onclick='cancelEntry(this);' style='color:blue;'href='#'>取消入职</a></td>"
				}else{
					html += "<td style='text-align:center;'>&nbsp;</td>"
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

function cancelEntry(obj){
	var name = $(obj).parents("tr").find("td:eq(2)").text();
	var id = $(obj).attr("id");
	secondConfirmation({
		"msg":"确认将 "+name+" 取消入职？",
		sureFn:function(){
			pageLoading(true);//加载动画
			$.ajax({
		 		async : true,
		 		type : "post",
				dataType:"json",
				data : {entryApplyId:id},
		 		url : contextPath + "/entry/cancelEntry.htm",
		 		success : function(response) {
		 			if(response.sucess){
		 				gotoPage($("#pageNo").val());
		 			}else{
		 				JEND.page.showError(response.msg);
		 			}
				},
				complete:function(XMLHttpRequest,textStatus){  
					pageLoading(false);//关闭动画
		        }
		 	});
		}
	});
}