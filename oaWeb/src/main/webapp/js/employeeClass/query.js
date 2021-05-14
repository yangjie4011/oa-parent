$(function(){
	$("#year").click(function(){
		var t = $(this);
		var yearList = new Array();
		var now = new Date();
		yearList.push(now.getFullYear()-3);
		yearList.push(now.getFullYear()-2);
		yearList.push(now.getFullYear()-1);
		yearList.push(now.getFullYear());
		yearList.push(now.getFullYear()+1);
		OA.bPup(yearList,function(d){
			$(t).val(d);
		});
	})
})
//获取部门人员排班信息
function getEmployeeClassByDepartAndMonth(){
	$.getJSON(contextPath +'/employeeClass/getEmployeeClassByDepartAndMonth.htm?departId='+$("#depart").attr("partid")+"&month="+$("#selDate").val(),function(data){
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
            if(data[index].approvalStatus==100){
    			//待审批
    			$("#employeeClassList").append("<div class='moudle'><topq>"+departName+"</topq><i class='icon-p3'></i><ul><li><left>年月</left><right>"+data[index].classMonth+"</right></li><li><left>成员</left><right>"+data[index].employeeNum+"</right></li><li><left>已排班</left><right>"+data[index].classEmployeeNum+"</right></li><li><left>排班人</left><right>"+data[index].classSettingPerson+"</right></li></ul><footq><ul><li></li><li><div id="+data[index].id+" onclick='toApprove(this)' class='btn'>审核</div></li></ul></footq></div>");
    		}else if(data[index].approvalStatus==200){
    			//已审批
    			$("#employeeClassList").append("<div class='moudle'><topq>"+departName+"</topq><i></i><ul><li><left>年月</left><right>"+data[index].classMonth+"</right></li><li><left>成员</left><right>"+data[index].employeeNum+"</right></li><li><left>已排班</left><right>"+data[index].classEmployeeNum+"</right></li><li><left>排班人</left><right>"+data[index].classSettingPerson+"</right></li></ul><footq><ul><li><div id="+data[index].id+" onclick='detailNormal(this)' class='btn'>查看</div></li><li><div id="+data[index].id+" onclick='exportByHR(this)' class='btn'>导出</div></li><li><div id="+data[index].id+" classEmployeeNum="+data[index].classEmployeeNum+" departId="+data[index].departId+" onclick='toHrMoveEmployeeClass(this)' class='btn'>调班</div></li></ul></footq></div>");
    		}else if(data[index].approvalStatus==300){
    			//已拒绝
    			$("#employeeClassList").append("<div class='moudle'><topq>"+departName+"</topq><i class='icon-p1'></i><ul><li><left>年月</left><right>"+data[index].classMonth+"</right></li><li><left>成员</left><right>"+data[index].employeeNum+"</right></li><li><left>已排班</left><right>"+data[index].classEmployeeNum+"</right></li><li><left>排班人</left><right>"+data[index].classSettingPerson+"</right></li></ul><footq><ul><li></li></ul></footq></div>");
    		}
		});
  });
}
//跳转到人事审核页面
function toApprove(obj){
	window.location.href=contextPath + "/employeeClass/toHrApprove.htm?employeeClassId="+$(obj).attr("id");
}
//查看日常
function detailNormal(obj){
	window.location.href=contextPath + "/employeeClass/detailNormal.htm?type=1&id="+$(obj).attr("id");
}
//导出
function exportByHR(obj){
	OA.twoSurePop({
		tips:'确认导出吗？',
		sureFn:function(){
		    	OA.pageLoading(true);
				$.ajax({
					async:true,
					type:'post',
					dataType:'json',
					data:{
						token:$("#token").val(),
						id:$(obj).attr("id")
					},
					url:contextPath + "/employeeClass/exportEmpClassReprotById.htm",
					success:function(data){
						if(data.success){
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
//跳转到人事调班页面
function toHrMoveEmployeeClass(obj){
	window.location.href=contextPath + "/employeeClass/toHrMoveEmployeeClass.htm?id="+$(obj).attr("id")+"&departId="+$(obj).attr("departId");
}

//获取部门人员值班信息
function getEmployeeDutyByDepartAndVacation(){
	$.getJSON(contextPath +'/employeeClass/getEmployeeDutyByDepartAndVacation.htm?departId='+$("#depart1").attr("partid")+'&vacationName='+$("#selDate2").val()+'&year='+$("#year").val(),function(data){
    	$("#employeeDutyList").empty();
    	if(data.length==0){
    		$("#employeeDutyList").append("<p>暂无排班数据～</p>");
    		return;
    	}
    	$.each(data, function(index) {
			//已审批
			$("#employeeDutyList").append("<div class='moudle'><topq>"+data[index].departName+"</topq><i></i><ul><li><left>假期</left><right>"+data[index].year+"/"+data[index].vacationName+"</right></li><li><left>成员</left><right>"+data[index].employDutyCount+"</right></li><li><left>排班人</left><right>"+data[index].classSettingPerson+"</right></li></ul><footq><ul><li></li><li><div year="+data[index].year+" vacationName="+data[index].vacationName+" departId="+data[index].departId+" onclick='detailVacation(this)' class='btn'>查看</div></li><li><div year="+data[index].year+" vacationName="+data[index].vacationName+" departId="+data[index].departId+" onclick='exportByHR1(this)' class='btn'>导出</div></li></ul></footq></div>");
		});
  });
}
//查看
function detailVacation(obj){
	window.location.href=contextPath + "/employeeClass/toHrMoveEmployeeDuty.htm?year="+$(obj).attr("year")+"&vacationName="+$(obj).attr("vacationName")+"&departId="+$(obj).attr("departId");
}
//导出值班
function exportByHR1(obj){
	OA.twoSurePop({
		tips:'确认导出吗？',
		sureFn:function(){
		    	OA.pageLoading(true);
				$.ajax({
					async:true,
					type:'post',
					dataType:'json',
					data:{
						token:$("#token").val(),
						year:$(obj).attr("year"),
						vacationName:$(obj).attr("vacationName"),
						departId:$(obj).attr("departId")
					},
					url:contextPath + "/employeeClass/exportEmpDutyReprotById.htm",
					success:function(data){
						if(data.success){
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