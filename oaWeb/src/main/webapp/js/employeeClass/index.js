$(function(){
	//获取班次
	if($("#isSetPerson").val()){
		getClassSetByPage();
		getEmployeeClassByDepart();
		//跳转班次新增
		$("#addClassSet").click(function(){
			window.location.href=contextPath + "/employeeClass/classSet.htm";
		})
	}
	if($("#isDh").val()){
		getEmployeeDutyByDepart();
	}
})

//删除班次
function delClassSet(obj){
	OA.twoSurePop({
		tips:'确认删除吗？',
		sureFn:function(){
		    	OA.pageLoading(true);
				$.ajax({
					async:true,
					type:'post',
					dataType:'json',
					data:{
						id:$(obj).parent().attr("id"),
						version:$(obj).parent().attr("version"),
						token:$("#token").val()
					},
					url:contextPath + "/classSetting/delete.htm",
					success:function(data){
						if(data.success){
							OA.pageLoading(false);
							OA.titlePup(data.message,'win');
							if($("#isShowAll").val()==0){
								getClassSetByPage();
							}else{
								$(obj).parent().remove();
							}
						}else{
							OA.pageLoading(false);
							OA.titlePup(data.message,'lose');
						}
					}
				});
		},
		cancelFn:function(){
			
		}
	})
}
//获取班次
function getClassSetByPage(){
	 $.getJSON(contextPath +'/classSetting/getListByCondition.htm?isShowAll=0',function(data){
	    	$("#classSetList").empty();
	    	$.each(data, function(index) {
	    		if(data[index].id != 0&&index>=1&&index<=3){
	    			$("#classSetList").append("<li version="+data[index].version+" id="+data[index].id+">"+data[index].name+"："+data[index].startTime+"-"+data[index].endTime+"<span>应出勤工时： "+data[index].mustAttnTime+"</span></li>");
	    		}
			});
	    	if(data.length==1){
	    		$("#classSetList").append("<p>暂无班次数据～</p");
	    	}
	    	if(data.length>4){
	    		$("#classSetList").append("<li onclick='showAll(this)' class='open'>展开所有<i class='bot'></i></li>");
	    	}
	  });
}
//展示所有
function showAll(obj){
    $.getJSON(contextPath +'/classSetting/getListByCondition.htm',function(data){
    	$("#isShowAll").val("1");
    	$("#classSetList").empty();
    	$.each(data, function(index) {
    		if(data[index].id != 0){
    		  $("#classSetList").append("<li version="+data[index].version+" id="+data[index].id+">"+data[index].name+"："+data[index].startTime+"-"+data[index].endTime+"<span>应出勤工时： "+data[index].mustAttnTime+"</span></li>");
    		}
		});
    });
}

//新增下月排班申请数据
function addLastMonth(){
	var lastMonth = parseInt($("#lastMonth").val());
	OA.twoSurePop({
		tips:'请开始您'+lastMonth+'月的排班',
		sureFn:function(){
		    	OA.pageLoading(true);
				$.ajax({
					async:true,
					type:'post',
					dataType:'json',
					data:{
						token:$("#token").val()
					},
					url:contextPath + "/employeeClass/addLastMonth.htm",
					success:function(data){
						if(data.success){
							getEmployeeClassByDepart();
							OA.pageLoading(false);
							OA.titlePup(data.message,'win');
							
						}else{
							OA.pageLoading(false);
							OA.titlePup(data.message,'lose');
						}
					}
				});
		},
		cancelFn:function(){
			
		}
	})
}
//获取部门人员排班信息
function getEmployeeClassByDepart(){
	$.getJSON(contextPath +'/employeeClass/getEmployeeClassByDepart.htm',function(data){
    	$("#employeeClassList").empty();
    	if(data.length==0){
    		$("#employeeClassList").append("<p>暂无排班数据～</p>");
    		return;
    	}
    	$.each(data, function(index) {
    		var departName="";
            if(typeof(data[index].parentDepartName)=="undefined"||data[index].parentDepartName==""){
            	departName = data[index].departName;
            }else{
            	departName = data[index].parentDepartName+"-"+ data[index].departName;
            }
    		if(typeof(data[index].approvalStatus)=="undefined"){
    			$("#employeeClassList").append("<div class='moudle'><topq>"+departName+"<span id="+data[index].id+" onclick='delEmployeeClass(this)' class='del'></span></topq><ul><li><left>年月</left><right>"+data[index].classMonth+"</right></li><li><left>成员</left><right>"+data[index].employeeNum+"</right></li><li><left>已排班</left><right>"+data[index].classEmployeeNum+"</right></li><li><left>排班人</left><right>"+data[index].classSettingPerson+"</right></li></ul><footq><ul><li></li><li><div id="+data[index].id+" onclick='updateEmployeeClass(this)' class='btn'>编辑排班</div></li><li><div id="+data[index].id+" classEmployeeNum="+data[index].classEmployeeNum+" onclick='submitEmployeeClass(this)' class='btn'>提交审核</div></li></ul></footq></div>");
    		}else if(data[index].approvalStatus==100){
    			//待审批
    			$("#employeeClassList").append("<div class='moudle'><topq>"+departName+"</topq><i class='icon-p3'></i><ul><li><left>年月</left><right>"+data[index].classMonth+"</right></li><li><left>成员</left><right>"+data[index].employeeNum+"</right></li><li><left>已排班</left><right>"+data[index].classEmployeeNum+"</right></li><li><left>排班人</left><right>"+data[index].classSettingPerson+"</right></li></ul><footq><ul><li></li><li><div id="+data[index].id+" onclick='detailNormal(this)' class='btn'>查看</div></li></ul></footq></div>");
    		}else if(data[index].approvalStatus==200){
    			//已审批
    			$("#employeeClassList").append("<div class='moudle'><topq>"+departName+"</topq><i></i><ul><li><left>年月</left><right>"+data[index].classMonth+"</right></li><li><left>成员</left><right>"+data[index].employeeNum+"</right></li><li><left>已排班</left><right>"+data[index].classEmployeeNum+"</right></li><li><left>排班人</left><right>"+data[index].classSettingPerson+"</right></li></ul><footq><ul><li></li><li><div id="+data[index].id+" onclick='detailNormal(this)' class='btn'>查看</div></li><li><div id="+data[index].id+" classEmployeeNum="+data[index].classEmployeeNum+" onclick='moveEmployeeClass(this)' class='btn'>调班</div></li></ul></footq></div>");
    		}else if(data[index].approvalStatus==300||data[index].approvalStatus==400){
    			//已拒绝
    			$("#employeeClassList").append("<div class='moudle'><topq>"+departName+"</topq><i class='icon-p1'></i><ul><li><left>年月</left><right>"+data[index].classMonth+"</right></li><li><left>成员</left><right>"+data[index].employeeNum+"</right></li><li><left>已排班</left><right>"+data[index].classEmployeeNum+"</right></li><li><left>排班人</left><right>"+data[index].classSettingPerson+"</right></li></ul><footq><ul><li></li><li><div id="+data[index].id+" onclick='detailNormal(this)' class='btn'>查看</div></li></ul></footq></div>");
    		}else if(data[index].approvalStatus==600){
    			//调班后
    			$("#employeeClassList").append("<div class='moudle'><topq>"+departName+"</topq><ul><li><left>年月</left><right>"+data[index].classMonth+"</right></li><li><left>成员</left><right>"+data[index].employeeNum+"</right></li><li><left>已排班</left><right>"+data[index].classEmployeeNum+"</right></li><li><left>排班人</left><right>"+data[index].classSettingPerson+"</right></li></ul><footq><ul><li></li><li><div id="+data[index].id+" onclick='moveEmployeeClass(this)' class='btn'>调班</div></li><li><div id="+data[index].id+" classEmployeeNum="+data[index].classEmployeeNum+" onclick='submitEmployeeClass(this)' class='btn'>提交审核</div></li></ul></footq></div>");
    		}
		});
  });
}
//删除排班申请数据
function delEmployeeClass(obj){
	OA.twoSurePop({
		tips:'确认删除吗？',
		sureFn:function(){
		    	OA.pageLoading(true);
				$.ajax({
					async:true,
					type:'post',
					dataType:'json',
					data:{
						id:$(obj).attr("id"),
						token:$("#token").val()
					},
					url:contextPath + "/employeeClass/delEmployeeClass.htm",
					success:function(data){
						if(data.success){
							OA.pageLoading(false);
							getEmployeeClassByDepart();
							OA.titlePup(data.message,'win');
						}else{
							OA.pageLoading(false);
							OA.titlePup(data.message,'lose');
						}
					}
				});
		},
		cancelFn:function(){
			
		}
	})
}
//提交排班审核数据
function submitEmployeeClass(obj){
	if($(obj).attr("classEmployeeNum")==0){
		OA.titlePup('已排班人数为0，不能提交！','lose');
		return;
	}
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
		    	OA.pageLoading(true);
				$.ajax({
					async:true,
					type:'post',
					dataType:'json',
					data:{
						classdetailId:$(obj).attr("id"),
						token:$("#token").val()
					},
					url:contextPath + "/employeeClass/submitEmployeeClass.htm",
					success:function(data){
						if(data.success){
							getEmployeeClassByDepart();
							OA.pageLoading(false);
							OA.titlePup(data.message,'win');
						}else{
							OA.pageLoading(false);
							if(data.fullFlag){
								OA.twoSurePop({
									tips:data.message,
									sureFn:function(){
										window.location.href=contextPath + "/employeeClass/updateNormal.htm?id="+$(obj).attr("id");
									},
									cancelFn:function(){
										
									}
								})
							}else{
								OA.titlePup(data.message,'lose');
							}
						}
					}
				});
		},
		cancelFn:function(){
			
		}
	})
}
//编辑排班审核数据
function updateEmployeeClass(obj){
	window.location.href=contextPath + "/employeeClass/updateNormal.htm?id="+$(obj).attr("id");
}
//查看日常
function detailNormal(obj){
	window.location.href=contextPath + "/employeeClass/detailNormal.htm?type=2&id="+$(obj).attr("id");
}
//调班日常页面
function moveEmployeeClass(obj){
	window.location.href=contextPath + "/employeeClass/toMoveEmployeeClass.htm?id="+$(obj).attr("id");
}



//获取部门人员值班信息
function getEmployeeDutyByDepart(){
	$.getJSON(contextPath +'/employeeClass/getEmployeeDutyByDepart.htm',function(data){
    	$("#employeeDutyList").empty();
    	if(data.length==0){
    		$("#employeeDutyList").append("<p>暂无排班数据～</p>");
    		return;
    	}
    	$.each(data, function(index) {
    		var departName="";
            if(typeof(data[index].parentDepartName)=="undefined"||data[index].parentDepartName==""){
            	departName = data[index].departName;
            }else{
            	departName = data[index].parentDepartName+"-"+ data[index].departName;
            }
            var dutyNum = 0;
            if(typeof(data[index].dutyNum)=="undefined"){
            	dutyNum = 0;
            }else{
            	dutyNum = data[index].dutyNum;
            }
    		if(typeof(data[index].approvalStatus)=="undefined"||data[index].approvalStatus==300){
    			$("#employeeDutyList").append("<div class='moudle'><topq>"+departName+"<span id="+data[index].id+" onclick='delEmployeeDuty(this)' class='del'></span></topq><ul><li><left>假期</left><right>"+data[index].year+"/"+data[index].vacationName+"</right></li><li><left>成员</left><right>"+dutyNum+"</right></li><li><left>排班人</left><right>"+data[index].classSettingPerson+"</right></li></ul><footq><ul><li><div id="+data[index].id+" onclick='detailDuty(this)' class='btn'>查看</div></li><li><div id="+data[index].id+" onclick='updateEmployeeDuty(this)' class='btn'>编辑值班</div></li><li><div id="+data[index].id+" dutyNum="+dutyNum+" onclick='submitEmployeeDuty(this)' class='btn'>提交审核</div></li></ul></footq></div>");
    		}else if(data[index].approvalStatus==100){
    			//待审批
    			$("#employeeDutyList").append("<div class='moudle'><topq>"+departName+"</topq><i class='icon-p3'></i><ul><li><left>假期</left><right>"+data[index].year+"/"+data[index].vacationName+"</right></li><li><left>成员</left><right>"+dutyNum+"</right></li><li><left>排班人</left><right>"+data[index].classSettingPerson+"</right></li></ul><footq><ul><li></li><li><div id="+data[index].id+" onclick='detailDuty(this)' class='btn'>查看</div></li></ul></footq></div>");
    		}else if(data[index].approvalStatus==200){
    			//已审批
    			$("#employeeDutyList").append("<div class='moudle'><topq>"+departName+"</topq><i></i><ul><li><left>假期</left><right>"+data[index].year+"/"+data[index].vacationName+"</right></li><li><left>成员</left><right>"+dutyNum+"</right></li><li><left>排班人</left><right>"+data[index].classSettingPerson+"</right></li></ul><footq><ul><li></li><li><div id="+data[index].id+" onclick='detailDuty(this)' class='btn'>查看</div></li></ul></footq></div>");
    			
    		//<li><div id="+data[index].id+" classEmployeeNum="+data[index].classEmployeeNum+" onclick='moveEmployeeDuty(this)' class='btn'>调班</div></li> 值班 调班代码先注释掉	
    		}
		});
  });
}
//新增值班页面
function toAddVacation(){
	window.location.href=contextPath + "/employeeClass/toAddVacation.htm";
}
//编辑值班审核数据
function updateEmployeeDuty(obj){
	window.location.href=contextPath + "/employeeClass/updateVacation.htm?dutyId="+$(obj).attr("id");
}
//查看值班页面
function detailDuty(obj){
	window.location.href=contextPath + "/employeeClass/detailVacation.htm?dutyId="+$(obj).attr("id");
}
//调班值班页面
function moveEmployeeDuty(obj){
	window.location.href=contextPath + "/employeeClass/toMoveEmployeeDuty.htm?dutyId="+$(obj).attr("id");
}
//删除值班申请数据
function delEmployeeDuty(obj){
	OA.twoSurePop({
		tips:'确认删除吗？',
		sureFn:function(){
		    	OA.pageLoading(true);
				$.ajax({
					async:true,
					type:'post',
					dataType:'json',
					data:{
						id:$(obj).attr("id"),
						token:$("#token").val()
					},
					url:contextPath + "/employeeClass/delEmployeeDuty.htm",
					success:function(data){
						if(data.success){
							getEmployeeDutyByDepart();
							OA.pageLoading(false);
							OA.titlePup(data.message,'win');
						}else{
							OA.pageLoading(false);
							OA.titlePup(data.message,'lose');
						}
					}
				});
		},
		cancelFn:function(){
			
		}
	})
}
//提交值班审核数据
function submitEmployeeDuty(obj){
	if($(obj).attr("dutyNum")==0){
		OA.titlePup('已排班人数为0，不能提交！','lose');
		return;
	}
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
		    	OA.pageLoading(true);
				$.ajax({
					async:true,
					type:'post',
					dataType:'json',
					data:{
						id:$(obj).attr("id"),
						token:$("#token").val()
					},
					url:contextPath + "/employeeClass/submitEmployeeDuty.htm",
					success:function(data){
						if(data.success){
							getEmployeeDutyByDepart();
							OA.pageLoading(false);
							OA.titlePup(data.message,'win');
						}else{
							OA.pageLoading(false);
							OA.titlePup(data.message,'lose');
						}
					}
				});
		},
		cancelFn:function(){
			
		}
	})
}
