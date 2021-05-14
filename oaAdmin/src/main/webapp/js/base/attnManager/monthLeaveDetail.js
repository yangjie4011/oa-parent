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
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		getSecondDepart(this.value);
	});
	
	
});

//导出查询报表
function exportReport(){
	var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
	
	window.location.href = basePath + "/monthLeaveDetail/exportReport.htm?" + data;
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
		url:  basePath + "/monthLeaveDetail/getReportPageList.htm",
		success:function(result){
			$("#reportList").empty();	
				var html = "";					//第一次循环设置默认值
				var rows = result.mapList;				
						for(i in rows){
							var o = rows[i];
							for(j in o){
								var oo = o[j];
								if(typeof(oo.code)=="undefined"|| oo.code.toString()==""){									
								}else{
								html += "<tr>";
								html += "<td>"+oo.code+"</td>";
								html += "<td>"+oo.name+"</td>";		
								var arr = new Array("1","11","2","5","7","9","10","3","6","8");//假期类型
								var type = arr[0];
								var typeIndex = 0;
								for(var m=1;m<=10;m++){
									type = arr[typeIndex];
									typeIndex++;
									for(n=1;n<=31;n++){									
										var aa = type+"_"+n;
										aa = aa.toString();										
										if(oo[aa]==""){
											html += "<td>0</td>";
										}else{
											if(typeof(oo[aa])=="undefined"){
												html += "<td>0</td>";
											}else{
												html += "<td>"+oo[aa]+"</td>";
											}										
										}
									}
								}
								html += "</tr>";
							  }
							}
						  }				
			$("#reportList").append(html);
			
			if(result != null && result.pageNo != null) {
				page = result.pageNo;
			}
			
			initPage("commonPage",result,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
	
	
}