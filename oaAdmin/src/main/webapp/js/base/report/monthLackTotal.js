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
	//初始化部门
	getFirstDepart();
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		getSecondDepart(this.value);
	});
	
	$("#export").click(function() {
		var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
		window.location.href = basePath + "empAttn/exportMonthLackTotal.htm?"+data;
	});
});

function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	
	var time = $("#inputStartTime").val();
	time = time + "-01";//是为了方便后台能接受到yyyy-MM-dd格式
	$("#startTime").val(time);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=10";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empAttn/getMonthLackTotalPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			
			var rows = response.rows;
			$.each(rows,function(i,item){
				html += "<tr><td>"+formatStr(item.code)+"</td>"
				     +  "<td>"+formatStr(item.cnName)+"</td>"
				     +  "<td>"+formatStr(item.departName)+"</td>"
				     +  "<td>"+formatStr(item.positionName)+"</td>"
				     +  "<td>"+formatStr(item.workDate)+"</td>"
				     +  "<td>"+formatStr(item.total)+"</td></tr>";
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

var formatStr = function(tmp){
	if (typeof(tmp) == "undefined") 
	{ 
	//alert("null or undefined or NaN"); 
		tmp = "";
	}else{
		
	}
	return tmp;
}