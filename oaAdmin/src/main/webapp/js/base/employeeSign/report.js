$(function(){
	$("#lastWeek").click(function(){
		setLastWeek("startTime","endTime", 'yyyy-MM-dd')
	});
	//本月
	$("#thisMonth").click(function(){
		setCurrentMonthTime("startTime","endTime", 'yyyy-MM-dd');
	});
	//上月
	$("#lastMonth").click(function(){
		setLastMonthTime("startTime","endTime", 'yyyy-MM-dd');
	});
	
	gotoPage(1);
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
});

//分页查询外出报表
function gotoPage(page){
	pageLoading(true);//加载动画
	
	$("#reportList").empty();
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize()+ "&rows=10";;
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "/employeeSign/getReportPageList.htm",
		success:function(response){
			var html = "";
			
			var rows = response.rows;
			$.each(rows,function(i,item){
				html += "<tr><td>"+formatStr(item.code)+"</td>"
				     +  "<td>"+formatStr(item.cnName)+"</td>"
				     +  "<td>"+formatStr(item.departName)+"</td>"
				     +  "<td>"+formatStr(item.workTypeName)+"</td>"
				     +  "<td>"+formatStr(item.attnDate)+"</td>"
				     +  "<td>"+formatStr(item.dayofweek)+"</td>"
				     +  "<td>"+formatStr(item.className)+"</td>"
				     +  "<td>"+formatStr(item.classPriod)+"</td>"
				     +  "<td>"+formatStr(item.signTime)+"</td></tr>";
			});
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

var exportReport = function(){
	$("#queryform").attr("action",basePath + "/employeeSign/exportEmployeeSignReport.htm");
	$("#queryform").submit();
}

var formatStr = function(tmp){
	if (typeof(tmp) == "undefined") 
	{ 
	//alert("null or undefined or NaN"); 
		tmp = "";
	}else{
		
	}
	return tmp;
}