$(function(){
	gotoPage();

	$("#insert").click(function(){
		insertClassSetting();
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
		url:  basePath + "employeeClass/queryClassSetting.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				var flagName;
				if(response.rows[i].isEnable==1){
					flagName="启用";
				}else{
					flagName="禁用";
				}
				
				html += "<tr>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].fullName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].name)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].startTime+"~"+response.rows[i].endTime)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].mustAttnTime)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].groupNames)+"</td>";
				html += "<td><a style='color:blue;' id="+response.rows[i].id+" onclick='setting(this.id,"+response.rows[i].isEnable+",\""+response.rows[i].name+"\")'>"+flagName+"</a><a style='color:blue;' id="+response.rows[i].id+" onclick='delById("+response.rows[i].id+",\""+response.rows[i].name+"\")' >  删除</a><a style='color:blue;' onclick='updateById("+response.rows[i].id+")' >  修改</a></td>";
				html += "</tr>";
			}
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



function setting(id,flagId,className){
		$("#enableClassName").text(className);
		if(flagId==1){
			flagId=0;
			$("#enableStatus").text("启用");
		}else{
			flagId=1;
			$("#enableStatus").text("禁用");
		}
		$("#reconfirmDiv").show();
		
		$("#enableFlagId").val(flagId);
		$("#classId").val(id);
}

function updateSetting(){
	pageLoading(true);//加载动画
	var isEnable=$("#enableFlagId").val();
	if(isEnable==0){
		$.ajax({
			async:true,
			type:'post',
			dataType:'json',
			data: {id:$("#classId").val(),isEnable:$("#enableFlagId").val()},
			url:  basePath + "employeeClass/updateClassSettingEnableById.htm",
			success:function(response){
				gotoPage();
				if(response.success){
					JEND.page.alert({type:"success", message:"修改状态成功！"});
				}else{
					JEND.page.alert({type:"error", message:"修改状态失败！"});
				}
				$("#reconfirmDiv").hide();
			},
			complete:function(XMLHttpRequest,textStatus){  
				if(XMLHttpRequest.status=="403"){
					JEND.page.alert({type:"error", message:"您没有该操作权限！"});
				}
				pageLoading(false);//关闭动画
	        }
		});
	}else if(isEnable==1){
		$.ajax({
			async:true,
			type:'post',
			dataType:'json',
			data: {id:$("#classId").val(),isEnable:$("#enableFlagId").val()},
			url:  basePath + "employeeClass/updateClassSettingNoEnableById.htm",
			success:function(response){
				gotoPage();
				if(response.success){
					JEND.page.alert({type:"success", message:"修改状态成功！"});
				}else{
					JEND.page.alert({type:"error", message:"修改状态失败！"});
				}
				$("#reconfirmDiv").hide();
			},
			complete:function(XMLHttpRequest,textStatus){  
				if(XMLHttpRequest.status=="403"){
					JEND.page.alert({type:"error", message:"您没有该操作权限！"});
				}
				pageLoading(false);//关闭动画
	        }
		});
	}
}

function closeCounterDiv(){
	$("#reconfirmDiv").hide();
	$("#delectDiv").hide();
}


function closeDiv(){
	$("#insertDiv").hide();
	$("#updateDiv").hide();
}

function calculatedHours(){
	
	if(typeof($("#startTime").val())!="undefined" && $("#startTime").val()!="" && typeof($("#endTime").val())!="undefined" && $("#endTime").val()!=""){
		var startTime=$("#startTime").val().substring(0,2);
		var endTime=$("#endTime").val().substring(0,2);
		var startHours=($("#startTime").val().substring(3,5)==30)?0.5:0;
		var endHours=($("#endTime").val().substring(3,5)==30)?0.5:0;
		var hours;
		if(startTime>endTime){
			hours=24-startTime-startHours;
			hours=parseFloat(hours)+parseFloat(endTime)+parseFloat(endHours);
			$("#isInterDay").val(1);
		}else{
			hours=(parseFloat(endTime)+parseFloat(endHours))-(parseFloat(startTime)+parseFloat(startHours));
			$("#isInterDay").val(0);
		}
		if(hours>=10){
			hours=hours-2;
		}else if(hours>=5){
			hours=hours-1;
		}
		$("#shouldTime").val(hours);
	}else{
		return;
	}
}

function save(){
	
	var strGroupIds="";
	var selectGroupIds=$("#selectGroupIds").find("li.active input");
	if(selectGroupIds.length>0){
		for(var i=0;i<selectGroupIds.length;i++){
			strGroupIds+=selectGroupIds[i].value+","
			if(selectGroupIds.length-1==i){
				strGroupIds=strGroupIds.substring(0, strGroupIds.length-1);
			}
		}
	}
	
	
	
	var name= $(".insretForm :text[name='name']").val();
	JEND.load('util.dialog', function() {
	if($(".insretForm :text[name='mustAttnTime']").val()<6){
		JEND.util.dialog.alert("请最少安排6小时工作！");
		return;
	}
	if($(".insretForm :text[name='fullName']").val()==null||""==$(".insretForm :text[name='fullName']").val()){
		JEND.util.dialog.alert("班次名称不能为空！");
		return;
	}
	if(name.length>1){
		JEND.util.dialog.alert("班次简称只能是一个字！");
		return;
	}
	if($(".insretForm :text[name='startTime']").val()==null){
		JEND.util.dialog.alert("上班时间不能为空！");
		return;
	}
	if($(".insretForm :text[name='endTime']").val()==null){
		JEND.util.dialog.alert("下班时间不能为空！");
		return;
	}
	});
	if(name.length>1||$(".insretForm :text[name='startTime']").val()==null||$(".insretForm :text[name='endTime']").val()==null || $(".insretForm :text[name='mustAttnTime']").val()<6){
		return;
	}
	

	
	
	var data = $("#insretForm").serialize()+"&groupIds="+strGroupIds;
	pageLoading(true);//加载动画
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "employeeClass/insertClassSetting.htm",
		success:function(response){
			JEND.load('util.dialog', function() {
				if(response.success){
					JEND.page.alert({type:"success", message:"添加成功！"});
					$(".insretForm :text[name='mustAttnTime']").val("");
					$(".insretForm :text[name='name']").val("")
					$(".insretForm :text[name='fullName']").val("")
					$(".insretForm :text[name='startTime']").val("");
					$(".insretForm :text[name='endTime']").val("")
					$("#insertDiv").hide();
					gotoPage();
				}else{
					JEND.page.showError(response.msg);
				}
			});
			
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}

function updateClassName(){
	
	var strGroupIds="";
	var selectGroupIds=$("#updateGroupIds").find("li.active input");
	if(selectGroupIds.length>0){
		for(var i=0;i<selectGroupIds.length;i++){
			strGroupIds+=selectGroupIds[i].value+","
			if(selectGroupIds.length-1==i){
				strGroupIds=strGroupIds.substring(0, strGroupIds.length-1);
			}
		}
	}
	
	var name= $(".updateForm :text[name='name']").val();
	JEND.load('util.dialog', function() {
		if($(".updateForm :text[name='name']").val()==null||""==$(".updateForm :text[name='name']").val()){
			JEND.util.dialog.alert("班次简称不能为空！");
			return;
		}
		if($(".updateForm :text[name='fullName']").val()==null||""==$(".updateForm :text[name='fullName']").val()){
			JEND.util.dialog.alert("班次名称不能为空！");
			return;
		}
		if(name.length>1){
			JEND.util.dialog.alert("班次简称只能是一个字！");
			return;
		}
	});
	
	
	
	
	pageLoading(true);//加载动画
	var data = $("#updateForm").serialize()+"&id="+$("#updateClassId").val()+"&groupIds="+strGroupIds;
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "employeeClass/updateClassName.htm",
		success:function(response){
			JEND.load('util.dialog', function() {
				if(response.success){
					JEND.page.alert({type:"success", message:"修改成功！"});
					$("#updateClassId").val("");
					$(".updateForm :text[name='mustAttnTime']").val("");
					$(".updateForm :text[name='name']").val("")
					$(".updateForm :text[name='fullName']").val("")
					$(".updateForm :text[name='startTime']").val("");
					$(".updateForm :text[name='endTime']").val("")
					$("#updateDiv").hide();
					gotoPage();
				}else{
					JEND.page.showError(response.msg);
				}
			});
			
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }	
	});
}


function insertClassSetting(){
	$("#insertDiv").show();
	$(".val-selected").text("请选择……");
	$("#selectGroupIds").find("li input").attr("checked",false);
	$("#selectGroupIds").find("li").removeClass("active");
}

function delById(id,delClassName){
	$("#delectDiv").show();
	$("#delFlagId").val(id);
	$("#delClassName").text(delClassName);
}

function deleteSave(){
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: {id:$("#delFlagId").val(),delFlag:1},
		url:  basePath + "employeeClass/deleteClassSettingById.htm",
		success:function(response){
			JEND.load('util.dialog', function() {
				if(response.success){
					JEND.page.alert({type:"success", message:"删除成功！"});
					$("#delFlagId").val("");
					$("#delectDiv").hide();
					gotoPage();
				}
			});
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});		
}


function updateById(id){
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: {id:id},
		url:  basePath + "employeeClass/queryClassById.htm",
		success:function(response){
			JEND.load('util.dialog', function() {
				if(response.success){
					$("#updateDiv").show();
					$("#updateClassId").val(id);
					$(".updateForm :text[name='mustAttnTime']").val(response.classSet.mustAttnTime);
					$(".updateForm :text[name='name']").val(response.classSet.name);
					$(".updateForm :text[name='fullName']").val(response.classSet.fullName);
					$(".updateForm :text[name='startTime']").val(response.classSet.startTime);
					$(".updateForm :text[name='endTime']").val(response.classSet.endTime);
					
					$("#updateGroupIds").find("li input").attr("checked",false);
					$("#updateGroupIds").find("li").removeClass("active");
					var updateGroupIds=$("#updateGroupIds").find("li");
					var strGroupNames="";
					var selectGroupIds=response.classSet.groupIds;
					if(selectGroupIds!="undefined" && typeof(selectGroupIds)!="undefined"){
						var splitArray=selectGroupIds.split(",");
						if(selectGroupIds.length>0){
							for(var j=0;j<splitArray.length;j++){	
								console.log(splitArray[j]);	
								$("#groupIds"+splitArray[j]).addClass('active');
								$("#checkboxs"+splitArray[j]).attr("checked",true);
								strGroupNames+=$("#groupIds"+splitArray[j]).text()+",";
								if(selectGroupIds.length-1==j){
									strGroupNames=strGroupNames.substring(0, strGroupNames.length-1);
								}
							}	
						}
					}
					$(".val-selected").text(strGroupNames);
					
					
				}
			});
		}	
	});
}
