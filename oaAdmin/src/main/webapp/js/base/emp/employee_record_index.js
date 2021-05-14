$(function(){
	
	//默认查询所有
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});
	
	var currentCompanyId = $("#currentCompanyId").val();

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
		url:  basePath + "employeeRecord/getListByPage.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>"
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(formatStr(response.rows[i].empTypeName))+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].departName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].positionName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].workTypeName)+"</td>";
				html += "<td style='text-align:left;'><a style='color:blue;' id="+response.rows[i].id+" onclick='toScanHtm(this)'>查看</a>&nbsp;&nbsp;<a style='color:blue;' id="+response.rows[i].id+" onclick='toEditHtm(this)'>编辑</a></td>";
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

function toScanHtm(obj){
	window.location.href = basePath+"employeeRecord/scan.htm?employeeId="+$(obj).attr("id");
}

function toEditHtm(obj){
	window.location.href = basePath+"employeeRecord/update.htm?type=1&employeeId="+$(obj).attr("id");
}

var formatStr = function(o){
	
	var result = "";
	if(typeof(o) == "undefined"){
		
	}else{
		result = o;
	}
	return result;
}
