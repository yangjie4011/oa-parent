$(function(){
	gotoPage(1);
});

function query(){
	gotoPage(1);
}


function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = "page=" + $("#pageNo").val() + "&rows=10";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "vacation/list.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].annualDate+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].year+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].type+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].subject+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].content+"</td>";
				html += "<td style='text-align:left;'><a style='color:blue;' href='#' onclick='deleteVacation("+response.rows[i].id+");'>删除</a></td>";
				html += "</tr>";
			}
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			$("#token").val(XMLHttpRequest.getResponseHeader('token'));
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}

function deleteVacation(id){
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {id:id},
 		url : contextPath + "/vacation/delete.htm",
 		success : function(response) {
 			
		},  
        error : function(XMLHttpRequest,textStatus) { 
        	
        },
		complete:function(XMLHttpRequest,textStatus){  
			
        }
 	});
}









