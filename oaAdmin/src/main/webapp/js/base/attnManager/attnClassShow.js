$(function() {
	$(".jui-tabswitch").find("li").each(function(i){
		var obj = $(this);
		$(obj).click(function(){
			if(i==0){
				$(".content").show();
			}else{
				$(".content").hide();
			}
		})
	})
	getClassList(1);
});

var getClassList = function (page){
	pageLoading(true);//加载动画
	
	$("#reportList").empty();
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var currentCompanyId = $("#currentCompanyId").val();
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {companyId:currentCompanyId,rows:10,page:page},
 		url : contextPath + "/empAttn/getAllClassShowList.htm",
 		success : function(response) {
			var html = "";
			var rows = response.rows;
			$.each(rows,function(i,item){
				html += '<tr><td style="display:none;">'+formatStr(item.id)+'</td>'
			         +  '<td>'+formatStr(item.name)+'</td>'
				     +  '<td>'+formatStr(item.startTime)+"-"+formatStr(item.endTime)+'</td>'
				     +  '<td>'+formatStr(item.mustAttnTime)+'</td>'
				     +  '<td>'+formatStr(item.allowLateTime)+'</td>'
				     +  '<td>'+formatStr(item.departName)+'</td>';
				     if(i==0){
				    	 html += '<td><a allowLateTime='+item.allowLateTime+' startTime='+item.startTime+' endTime='+item.endTime+' id='+item.id+' class="blue-f-d fb" href="#" onclick="getSetForm(this);">设置<a></td></tr>';
				     }else{
				    	 html += '<td>'+""+'</td></tr>';
				     }
			});
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
 	});
}

function getSetForm(obj){
	$("#startTime").val($(obj).attr("startTime"));
	$("#endTime").val($(obj).attr("endTime"));
	$("#allowLateTime").val($(obj).attr("allowLateTime"));
	$("#settingId").val($(obj).attr("id"));
	$("#updateDiv").show();
}

function closeDiv(){
	$("#updateDiv").hide();
}

function updateSetting(){
    pageLoading(true);//加载动画
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : {id:$("#settingId").val(),startTime:$("#startTime").val(),endTime:$("#endTime").val(),allowLateTime:$("#allowLateTime").val()},
 		url : contextPath + "/empAttn/updateSetting.htm",
 		success : function(response) {
 			getClassList($("#pageNo").val());
		},
		complete:function(XMLHttpRequest,textStatus){  
			closeDiv();
			pageLoading(false);//关闭动画
        }
 	});
}

var formatStr = function(tmp){
	if (typeof(tmp) == "undefined") 
	{ 
		tmp = "";
	}else{
		
	}
	return tmp;
}