$(function(){
	$('#clickMe,#cancelQuit').click(function(){
		$("input[name='pageStr']").val();
		$("#cancelQuitIds").val();
        $("#quitIds").val();
	    PersonSel_luffy.init({
	        conditions:['quitTimeBegin','quitTimeEnd'],
	        cb:function(result){
	            var nameVal = '';
	            var ids = [];
	            for(var item in result){
	            	if(result[item].children.length){
	            		var children = result[item].children;
	            		children.forEach(function(worker){
	            			if(!nameVal)nameVal+=children[0].name;
	            			ids.push(worker.id);
	            		})
	            	}
	            }
	            $("input[name='pageStr']").val(nameVal+'等,共计'+ids.length+'个员工');
	            $("#cancelQuitIds").val(ids);
	            $("#quitIds").val(ids);
	        }
	    })
	})
	
	
	$("#yearAndMonth").val(getLastMonthStartDate("yyyy-MM"));
	//默认查询所有
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});
	var currentCompanyId = $("#currentCompanyId").val();
	//初始化工时类型
	initWorkType(currentCompanyId);
	//初始化部门
	getFirstDepart();
	getFirstDepart("firstDepartOfUpd");
	//初始化员工类型
	initEmployeeType(currentCompanyId);
	$("#export").click(function() {
		var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
		window.location.href = basePath + "quit/exportQuitEmployeeList.htm?"+data;
	});
});

/**
 * 组件变量
 * 1，自定义更多可控的查询条件
 * 2，点击确定毁掉函数
 */
function delThisUser(id){
	$(".updateUserSpan").find("#"+id+"").remove();
	pageLoading(true);//加载动画
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: {id:id},
		url:  basePath + "companyConfig/deleteCompanyConfig.htm",
		success:function(response){
			$("#messageBox #messageContent").text(response.result.message);
 	      	$("#messageBox").show();
			pageLoading(false);
		}
		
	});
	
}

function insertUserShow(){
	$("#updateUesrDiv").show();
}

function insertUser(){
	var email=$("#displayCode").val();
	//正则 表达式    
	var pattern = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/;

	JEND.load('util.dialog', function() {
		if($("#displayName").val()=="" || $("#displayName").val()==null){
			JEND.util.dialog.alert("请填写员工姓名！");
			return null;
		}
		if($("#displayCode").val()=="" || $("#displayCode").val()==null){
			JEND.util.dialog.alert("请填写邮箱！");
			return null;
		}
		if(!pattern.test(email)){
			JEND.util.dialog.alert("邮箱格式不正确！");
			return;
		}
	});
	if($("#displayName").val()=="" || $("#displayName").val()==null
		||$("#displayCode").val()=="" || $("#displayCode").val()==null
		||!pattern.test(email)
	){
		return null;
	}else{
		var data=$("#insertUserForm").serialize();
		pageLoading(true);//加载动画
		$.ajax({
			async:true,
			type:'post',
			dataType:'json',
			data: data,
			url:basePath + "companyConfig/addCompanyForm.htm",
			success:function(data){
	          if(data.result.flag){
	        	 var html="";
        		 html+="<span id="+data.result.companyConfig.id+" style='margin-left:25px;'>"+data.result.companyConfig.displayName+"<input type='hidden' name='quitNotiesCode' value='"+data.result.companyConfig.displayCode+"'><i class='del_i' onclick='delThisUser("+data.result.companyConfig.id+")'></i></span>";
        	     $(".updateUserSpan").append(html);
        	     $("#displayName").val("");
        	     $("#companyId").val("");
        	     $("#displayCode").val("");
	        	 $("#updateUesrDiv").hide();   
	        	 $("#messageBox #messageContent").text(data.result.message);
	 	      	 $("#messageBox").show();
	          }else{
	        	  if(data.code == '0001'){
	        		  $("#messageBox #messageContent").text("操作失败：该员工有假期未审批！");  
	        		  $("#messageBox").show();
	        	  }else if (data.code == '0002'){
	        		  $("#messageBox #messageContent").text("操作失败,请与开发人员确认问题！");  
	        		  $("#messageBox").show();        		  
	        	  }
	          }
	          pageLoading(false);//关闭动画
			},
			complete:function(XMLHttpRequest,textStatus){  
				if(XMLHttpRequest.status=="403"){
					JEND.page.alert({type:"error", message:"您没有该操作权限！"});
				}
	        }
		});
	}	
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
		url:  basePath + "quit/getQuitNotifyPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				//console.log("数据："+JSON.stringify(response.rows[i]));
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(formatStr(response.rows[i].empTypeName))+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].firstEntryTime)+"</td>";
				html += "<td style='text-align:center;color:red;'>"+getValByUndefined(response.rows[i].quitTime)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].departName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].positionName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaderName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].departLeaderName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(getJobStatusName(response.rows[i].jobStatus))+"</td>";
				if(getValByUndefined(getJobStatusName(response.rows[i].jobStatus))=="在职"){
					html += "<td style='text-align:left;'><a style='color:blue;' id="+response.rows[i].id+" onclick='update("+response.rows[i].id+",1)' >离职</a></td>";
				}else{
					html += "<td style='text-align:left;'><a style='color:blue;' id="+response.rows[i].id+" onclick='update("+response.rows[i].id+",3)' >离职日期修改</a> <a style='color:blue;' id="+response.rows[i].id+" onclick='update("+response.rows[i].id+",2)' >取消离职</a>";
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

var formatStr = function(o){
	
	var result = "";
	if(typeof(o) == "undefined"){
		
	}else{
		result = o;
	}
	return result;
}

var getJobStatusName = function(o){
	//0.在职 1.离职   2.待离职  3.已提出离职 （前台web做出离职操作）
	var str="";
	switch (o) {
		case 0:
			str="在职"
			break;
		case 1:
			str="离职"
			break;
		case 2:
			str="待离职"
			break;
		case 3:
			str="已提出离职"
			break;	
		default:
			str="职位未定义"
			break;
		}
	return str;
}

//提示框关闭
var closeDiv = function(divName){
	if(divName=="deleteDiv2" ||divName=="deleteDiv1"){		
		$("#zcqr").attr("class","red-but");//设置Id为two的class属性。
		$('#zcqr').attr("disabled",false)//将input元素设置为disabled
		$('#zcqx').attr("disabled",false)//将input元素设置为disabled		
	}	
	$("#"+divName).hide();
}

//弹出更新职位界面
function update(id,num){
	$("#cancelQuit").val("");
	$("#clickMe").val("");
	$("#salaryBalanceDate").val("");
	$("#quitTime").val("");
	//创建新的更新窗口	
	$("#addOrEidt").val('2');//修改标识
	$("#id").val(id);
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'id':id},
		url: basePath + "employee/getEmpInfo.htm",
		success:function(data){
			if(data != null && data.id != null){						
				//选中职级和职位序列下拉
				$(".updateForm :text[name='cnName']").val(data.cnName);
				$(".updateForm :text[name='departName']").val(data.departName);
				$(".updateForm :text[name='positionName']").val(data.positionName);				
				if(num==2){
					$("#cancelQuitTime").val("");
					//取消离职
					$(".updateForm :text[name='quitTime']").val(data.quitTime);
					$("#id2").val(data.id);
					$("#version2").val(data.version);
					$("#firstEntryTimeEnd").val(data.firstEntryTime);
					$("#quitTimeEnd").val(data.quitTime);
					$("#beforeWorkAge2").val(data.beforeWorkAge);
					$("#cancelQuitTime").val(data.quitTime);
					$("#quitTimeUpdateDate").show();
					$("#quitTime").hide();
					$("#updateDiv2").show();	
				}else if(num==1 || num==3){ //num==1 离职 || num==3 修改离职时间
					//离职信息修改
					$("#id").val(data.id);
					$("#version").val(data.version);
					$("#firstEntryTime2").val(data.firstEntryTime);
					$("#quitTime").val(data.quitTime);
					$("#quitTimeUpdateDate").val(data.quitTime);
					$("#quitTimeqr").val(data.quitTime);
					$("#beforeWorkAge").val(data.beforeWorkAge);					
					$(".updateForm :text[name='code']").val(data.code);
					$(".updateForm :text[name='empTypeName']").val(data.empTypeName);
					if(num ==1) {
						$("#sendEmail").val("");
						$("#quitUpdateBefore").val(null);
						$("#quitTimeUpdateDate").val(null);
						$("#quitTimeUpdateDate").hide(); 
						$("#quitTime").show();
						
						$("#zcqr").attr("onclick","reconfirmQuit(1);");
					}else if (num==3){
						$("#quitUpdateBefore").val(data.quitTime);
						$("#quitTime").val(null);
						$("#quitTime").hide();
						$("#quitTimeUpdateDate").show();
						$("#zcqr").attr("onclick","reconfirmQuit(3);");
					}
					$("#updateDiv").show();
					/*$("#updateQuitBox").find("li").attr("class","active");
					$("#updateQuitBox").find("li input").attr("checked",true);*/
					
				}
			}
		}
	});
}




//修改离职信息
function updatePostion(){
	var strQuitNoties="";
	var selectGroupIds = $("#updateForm .updateUserSpan input[name='quitNotiesCode']");
	if(selectGroupIds.length>0){
		for(var i=0;i<selectGroupIds.length;i++){
			strQuitNoties+=selectGroupIds[i].value+","
			if(selectGroupIds.length-1==i){
				strQuitNoties=strQuitNoties.substring(0, strQuitNoties.length-1);
			}
		}
	}
	
	pageLoading(true);//加载动画
	var data=$("#updateForm").serialize()+"&noticeStr="+strQuitNoties;
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:basePath + "employee/updateEmpQuit.htm",
		success:function(data){
          if(data.flag==true){
          	closeDiv("updateDiv");
			closeDiv('deleteDiv2');
			$("#salaryBalanceDate").val("");
			$("#quitTime").val("");
          	$("#messageBox #messageContent").text(data.message);
	      	 $("#messageBox").show();
	         $('#reportList').children('tr').remove();//删除 dom操作节点
	         gotoPage(1);//跳转到第几页
          }else{
        	  closeDiv('deleteDiv2');
        	  if(data.code == '0001'){
        		  $("#messageBox #messageContent").text("操作失败：该员工有假期未审批！");  
        		  $("#messageBox").show();
        	  }else if (data.code == '0002'){
        		  $("#messageBox #messageContent").text("操作失败,请与开发人员确认问题！");  
        		  $("#messageBox").show();        		  
        	  }
        	  closeDiv("updateDiv");
          }
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}


//修改离职日期
function updateEmpQuitTime(){
	var strQuitNoties="";
	var selectGroupIds = $("#updateForm .updateUserSpan input[name='quitNotiesCode']");
	if(selectGroupIds.length>0){
		for(var i=0;i<selectGroupIds.length;i++){
			strQuitNoties+=selectGroupIds[i].value+","
			if(selectGroupIds.length-1==i){
				strQuitNoties=strQuitNoties.substring(0, strQuitNoties.length-1);
			}
		}
	}
	var data=$("#updateForm").serialize()+"&noticeStr="+strQuitNoties;
	pageLoading(true);//加载动画
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:data,
		url:basePath + "employee/updateEmpQuitTime.htm",
		success:function(data){
          if(data.flag==true){
          	closeDiv("updateDiv");
			closeDiv('deleteDiv1');
			$("#salaryBalanceDate").val("");
			$("#quitTime").val("");
          	$("#messageBox #messageContent").text(data.message);
	      	 $("#messageBox").show();
	         $('#reportList').children('tr').remove();//删除 dom操作节点
	         gotoPage(1);//跳转到第几页
          }else{
        	  closeDiv('deleteDiv1');
        	  if(data.code == '0001'){
        		  $("#messageBox #messageContent").text("操作失败：该员工有假期未审批！");  
        		  $("#messageBox").show();
        	  }else if (data.code == '0002'){
        		  $("#messageBox #messageContent").text("操作失败,请与开发人员确认问题！");  
        		  $("#messageBox").show();        		  
        	  }
        	  closeDiv("updateDiv");
          }
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}


//shanchu
function deletePostion() {
	 
	var id = $("#deleteId").val();
	var version = $("#version").val();
	$.post(basePath + "employee/deleteEmp.htm", {
		id : id, 
		version : version
	}, function(data) {
		//1.关闭提示框
  	closeDiv("deleteDiv");
  	$("#messageBox #messageContent").text("删除成功！");
  	$("#messageBox").show();
  	$('#reportList').children('tr').remove();//删除 dom操作节点
  	gotoPage(1);//跳转到第几页
	}, 'json');
}

//删除职位
function del(id,version){
	$("#deleteId").val(id);
	$("#version").val(version);
	$("#deleteDiv").show();
}
//再次确认离职
function reconfirmQuit(num){	
	//指定发送人邮箱 判断
	var sendEmail=$("#sendEmail").val();
	//正则 表达式    
	var pattern = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,20}$/;
	
	if(sendEmail!=""){
		if(sendEmail.indexOf(",")!=-1){// !=-1含有 ==-1不含有
			var result=sendEmail.split(",");
			for(var i=0;i<result.length;i++){
				if(!pattern.test(result[i])){
					JEND.page.alert({type:"error", message:"邮箱格式不正确，请以@ule.com结尾"});
					return;
				}
			}
		}else {
			if(!pattern.test(sendEmail)){
				JEND.page.alert({type:"error", message:"邮箱格式不正确，请以@ule.com结尾"});
				return;
			}
		}
	}
	var quitTime=$("#quitTime").val();
	var quitTimeUpdateDate=$("#quitTimeUpdateDate").val();
	if(quitTime=="" && quitTimeUpdateDate==""){
		JEND.page.alert({type:"error", message:"离职日期不能为空"});
		return;
	}
	$("#zcqr").attr("class","grey-but");
	$('#zcqr').attr("disabled",true);
	$('#zcqx').attr("disabled",true);
	if(num==1){//离职
		$("#deleteDiv2").show();
	}else if(num==3){//更改离职时间
		$("#deleteDiv1").show();
	}
	
}


//取消离职 离职时间清空 工作状态改为 0
function cancleQuit(){	
	//指定发送人邮箱 判断
	var sendEmail=$("#cancleSendEmail").val();
	//正则 表达式    
	var pattern = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,20}$/;
	
	if(sendEmail!=""){
		if(sendEmail.indexOf(",")!=-1){// !=-1含有 ==-1不含有
			var result=sendEmail.split(",");
			for(var i=0;i<result.length;i++){
				if(!pattern.test(result[i])){
					JEND.page.alert({type:"error", message:"邮箱格式不正确，请以@ule.com结尾"});
					return;
				}
			}
		}else {
			if(!pattern.test(sendEmail)){
				JEND.page.alert({type:"error", message:"邮箱格式不正确，请以@ule.com结尾"});
				return;
			}
		}
	}
	var strQuitNoties="";
	var selectGroupIds = $("#updateForm2 .updateUserSpan input[name='quitNotiesCode']");
	if(selectGroupIds.length>0){
		for(var i=0;i<selectGroupIds.length;i++){
			strQuitNoties+=selectGroupIds[i].value+","
			if(selectGroupIds.length-1==i){
				strQuitNoties=strQuitNoties.substring(0, strQuitNoties.length-1);
			}
		}
	}
	var data=$("#updateForm2").serialize()+"&noticeStr="+strQuitNoties;
	
	
	pageLoading(true);//关闭动画
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:basePath + "quit/updateEmpQuitInfo.htm",
		success:function(data){
			pageLoading(false);//关闭动画
			closeDiv("updateDiv2");
          if(data.flag==true){
          	closeDiv("updateDiv");
          	$("#messageBox #messageContent").text(data.message);
          }else{
        	  if(data.code == '0001'){
        		  $("#messageBox #messageContent").text("操作失败：该员工有假期未审批！");  
        		  $("#messageBox").show();
        	  }else if (data.code == '0002'){
        		  $("#messageBox #messageContent").text("操作失败,请与开发人员确认问题！");  
        		  $("#messageBox").show();		  
        	  }
        	  closeDiv("updateDiv");
        	  
          }
          $("#messageBox").show();
          $('#reportList').children('tr').remove();//删除 dom操作节点        	
          gotoPage(1);//跳转到第几页
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}
