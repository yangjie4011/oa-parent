$(function () {
	$("#firstDepart").change(function() { 
		var departId=$("#firstDepart").val();
		var roleIdList = document.getElementsByName("roleIdList");
			var data='/userRole/getRoleInfosbyDepartId.htm?departId='+$("#firstDepart").val()+"&userId="+$("#updateId").val();
			$.ajaxSettings.async = false;
			$.getJSON(contextPath +data,function(data){
				$("#rbacRoleList").empty();
				for(var i=0;i<data.departRoleList.length;i++){	
					var uid=data.departRoleList[i].id;
					var html="";
					html+="<li style='width:100%;float:left;' id='roleList"+data.departRoleList[i].id+"}'><label>";
					html+="<input id='unChecked"+data.departRoleList[i].departId+"_"+data.departRoleList[i].id+"' style='width:16px;margin-left:5px;' type='checkbox' onClick='checkBoxSelected(this)' rid="+data.departRoleList[i].id+" value="+data.departRoleList[i].name+">"+data.departRoleList[i].name+"";
					html+="</label></li>";
					$("#rbacRoleList").append(html);
				}
				
				for(var j=0;j<roleIdList.length;j++){
					for(var i=0;i<data.departRoleList.length;i++){	
						var uid=data.departRoleList[i].id;
						if(roleIdList[j].value==uid){
							$("#unChecked"+data.departRoleList[i].departId+"_"+data.departRoleList[i].id+"").attr("checked",true)//选中
						}
					}
				}
				
			});
	}); 	
	
	var currentCompanyId = $("#currentCompanyId").val();
	gotoPage(1);
	getFirstDepart();
	//查询事件
	$("#query").click(function(){
		gotoPage(1);
	});
})   

function initDepartRoleList(){ 
	var data='/userRole/getDepartRoleListByUserId.htm?userId='+$("#updateId").val();
	$.ajaxSettings.async = false;
	$.getJSON(contextPath +data,function(data){
		for(var i=0;i<data.length;i++){
			var html="";
	        html+="<li id="+data[i].departId+"_"+data[i].id+" style='margin-left: 8px;'><a onclick='delThisUser(\""+data[i].departId+"_"+data[i].id+"\")'>×</a>"+data[i].departName+"_"+data[i].name+"<input type='hidden' name='roleIdList' value="+data[i].id+"></li>";
	        $(".updateUserSpan").append(html);
		}
	});
}


function delThisUser(id){
	$(".updateUserSpan").find("#"+id+"").remove();
	$("#unChecked"+id).attr("checked",false)//未选中
}	

function checkBoxSelected(obj){
	var departNameSelectedStr = $("#firstDepart").find("option:selected").text();
	var departNameSelectedId = $("#firstDepart").val();  
	var rid=$(obj).attr('rid');
    if(obj.checked){
         var html="";
         html+="<li id="+$("#firstDepart").val()+"_"+rid+" style='margin-left: 8px;'><a onclick='delThisUser(\""+$("#firstDepart").val()+"_"+rid+"\")'>×</a>"+departNameSelectedStr+"_"+obj.value+"<input type='hidden' name='roleIdList' value="+rid+"></li>";
     	 $(".updateUserSpan").append(html);
    }else{
    	delThisUser($("#firstDepart").val()+"_"+rid);
    }
}



function gotoPage(page){
	if(!page){
		page = 1;
	}
	pageLoading(true);//加载动画
	$("#pageNo").val(page);
	var data =$("#queryFrom").serialize()+"&page="+$("#pageNo").val()+"&rows=10"+"&companyId="+$("#currentCompanyId").val();
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:data,
		url:  basePath+"/userRole/getPageList.htm",
		success:function(response){
			$("#roleList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){			
				html += "<tr>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].code)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].cnName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].remark)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].updateUser)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].updateTime)+"</td>";
				var isLockedStr="";
				var isStatus="";
				if(response.rows[i].isLocked==0){
					isLockedStr="可用";
					isStatus="停用";
				}else if(response.rows[i].isLocked==1){
					isLockedStr="停用";
					isStatus="启用"
				}
				html += "<td style='text-align:center;'>"+getValByUndefined(isLockedStr)+"</td>";
				html +="<td style='text-align:center;'><a style='color:blue;' id="+response.rows[i].id+" onclick='updateDiv(this)' >修改</a>&nbsp;<a style='color:blue;' id="+response.rows[i].id+" onclick='forzenDiv(this,\""+isStatus+"\",\""+response.rows[i].cnName+"\")' >"+isStatus+"</a>&nbsp</td>";
				html +="</tr>";
			}
			$("#roleList").append(html);
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

function updateSaveDiv(){
	$("#updateShowDiv").show();
}
//修改角色Div
function updateDiv(obj){
	$.post(basePath+"/userRole/queryUserById.htm",{id:$(obj).attr("id")},function(data){
		if(data.result.flag){
			$(".updateForm :text[name='cnName']").val("");
            $("#updateEmpId").val("");			
			$("#updateDiv").show();			
			$("#updateId").val(data.result.usre.id);
			$("#cnName").val(data.result.usre.cnName);
			$("#updateRemark").val(data.result.usre.remark);
			$("#userName").val(data.result.usre.userName);
			$("#updateCode").val(data.result.usre.code);						
			$("#firstDepart option[value='']").attr("selected","selected");			
			$("#rbacRoleList").empty();
			$(".updateUserSpan").empty();	
			initDepartRoleList();
		}
	},'json');
}

//修改角色
function update(obj){
	
	pageLoading(true);//加载动画
	var data =$("#updateForm").serialize();
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath+"/userRole/updateById.htm",
		success:function(response){
			var type;
			if(response.result.flag){
				type="success";
			}else{
				type="error";
			}
			JEND.page.alert({type:type, message:response.result.message});
			gotoPage(1);
			$("#updateShowDiv").hide();
			closeDiv('updateDiv');
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}


//停用角色
function forzenDiv(obj,str,activeName){
	var isEnable=0;
	if(str=="启用"){
		isEnable=0;
	}else{
		isEnable=1;
	}
	
	$("#activeDiv").show();
	$("#activeId").val(isEnable);
	$("#activeUid").val($(obj).attr("id"));
	$("#active").text(str);
	$("#activeName").text(activeName);
}

function forzen(){
	pageLoading(true);//加载动画
	var data ={id:$("#activeUid").val(),isLocked:$("#activeId").val()};
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath+"/userRole/forzenUserById.htm",
		success:function(data){
			var type;
			if(data.result.flag){
				type="success";
			}else{
				type="error";
			}			
			JEND.page.alert({type:type, message:data.result.message});
			if(data.result.flag){
				$("#activeDiv").hide();
				gotoPage(1);
			}
		},
		complete:function(XMLHttpRequest,textStatus){  
			pageLoading(false);//关闭动画
        }
	});
}

function closeDiv(obj){
	$("#"+obj+"").hide();
}

