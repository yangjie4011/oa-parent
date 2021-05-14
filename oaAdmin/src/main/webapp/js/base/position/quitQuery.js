$(function(){
	gotoPage();
	$("#query").click(function(){
		gotoPage(1);
	});
	//初始化部门
	getFirstDepart();
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		getSecondDepart(this.value);
	});

});

//获得第一级部门
function getAllDepart(){
	$("#firstDepart").empty();
	var options = "<option value=''>请选择</option>";
	$("#firstDepart").append(options);
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		url:contextPath + "/depart/getAllDepart.htm",
		success:function(data){
			$.each(data, function(index) {
				$("#firstDepart").append("<option value= " + data[index].name + ">" + data[index].name + "</option>");
			});
		}
	});
}



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
		url:  basePath + "quit/getReportPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].code)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].employeeName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].employeeType)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].entryTime)+"</td>";
				html += "<td style='text-align:center;color:red;'>"+getValByUndefined(response.rows[i].quitTime)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].departName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].positionName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].reportLeader)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].departHeader)+"</td></tr>";
			}
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPage("commonPage",response,page,"gotoPage");
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});	
}
