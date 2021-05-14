var startFlag=false;
$(function(){
	$("#updateDiv").hide();
	gotoPage(1);
	//页面大tab布局
	$("#query").click(function(){
		gotoPage(1);
	});
	
	$("#leaderTab").click(function(){
		$("#updateDiv").hide();
		$("#selectedBut").show();
		$("#commonPage").show();
		gotoPage(1);
	});
	
	$("#hrTab").click(function(){
		$("#updateDiv").show();
		$("#selectedBut").hide();
		
		$("#commonPage").hide();
		$("#reportList").find("thead").html("");
		$("#reportList").find("tbody").html("");
		$("#reportListTitle").find("thead").html("");
		$("#reportListTitle").find("tbody").html("");
	});
	
	getFirstDepart();
	
	$('#clickMe').click(function(){
		$("input[name='pageNameStr']").val();
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
	            $("input[name='pageNameStr']").val(nameVal+'等,共计'+ids.length+'个员工');
	            $("#empDepartIds").val(ids);
	        }
	    })
	})	
	
	if($("#isScheduler").val()=="false"){
		//初始化部门
		getScheduleDepartList();
	}
	//根据部门获取组别
	$("#firstDepart").change(function(){
		getGroupListByDepartId(this.value);
	});
	$("#scheduleTab").find("li").each(function(i){
		var obj = $(this);
		$(obj).click(function(){
			if(i==0){
				$("#groupIdStr").val($("#groupId").val());
				getUnCommitData(0);
			}else if(i==1){
				gotoPage($("#pageNo").val());
			}
		})
	});
	
	
	
});

//获得排班组别
function getGroupListByDepartId(id){
	var groupId = $("#groupId");
	groupId.empty();
	var options = "<option value=''>请选择</option>";
	groupId.append(options);
	
	if(null != id){
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data:{departId:id},
			url:contextPath + "/schedule/getGroupListByDepartId.htm",
			success:function(data){
				$.each(data, function(index) {
					groupId.append("<option value= " + data[index].id + ">" + data[index].name + "</option>");
				});
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
		url:  basePath + "schedule/getEmpClassListByCondition.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].code)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].cnName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].departName)+"</td>";
				html += "<td style='text-align:center;'>"+(response.rows[i].whetherScheduling=="487"?"是":"否")+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].scheduleGroupName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].scheduleCnName)+"</td>";
				html += "</tr>";
			}
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			/*if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}*/
			pageLoading(false);//关闭动画
        }
	});
}

//弹出更新职位界面
function update(){
	
	if($("#clickMe").val()==null || $("#clickMe").val()==''){
		JEND.page.alert({type:"error", message:"请选择员工"});
		return;
	}
	pageLoading(true);//加载动画
	var data = {"isWhetherScheduling":$("#whetherScheduling").val(),"empClassIds":$("#empDepartIds").val()}
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:basePath + "schedule/updateEmpClassInfo.htm",
		success:function(data){
          if(data.code=="0000"){
          	 closeDiv("updateDiv");
          	closeDiv("updateZcQrDiv");
          	 $("#messageBox #messageContent").text(data.message);  
	      	 $("#messageBox").show();
	      	 $("#firstDepart").val("");
	      	 $("#clickMe").val("");
	      	 $("#empDepartIds").val("");
	      	 
	      	$("#updateDiv").show();
	    	$("#selectedBut").hide();
	      	 
          }else{
        	  closeDiv('updateDiv');
        	  closeDiv("updateZcQrDiv");
        	  if (data.code == '0002'){
        		  $("#messageBox #messageContent").text("操作失败,请与开发人员确认问题！");  
        		  $("#messageBox").show();        		  
        	  }
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
function reconfirm(){
	if($("#clickMe").val()==null || $("#clickMe").val()==''){
		JEND.page.alert({type:"error", message:"请选择员工"});
		return;
	}
	$("#updateZcQrDiv").show();
	
	
}

var closeDiv = function(divName){
	$("#"+divName).hide();
}