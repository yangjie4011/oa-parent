//全局变量 map
var map = {};
$(function(){
	$("#month").val(getMonthStartDate('yyyy-MM'));
	var currentCompanyId = $("#currentCompanyId").val();
	//初始化员工类型
	initWorkType(currentCompanyId);
	//初始化部门
	getFirstDepart();
	
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		initEmployeeLeader(this.value);
	});
	gotoPage1();
	$("#query").click(function(){
		/*if($("#isMenu").val()=="true"){
			gotoPage();
		}else{*/
			gotoPage1();
		//}
	});
	//本月
	$("#thisMonth").click(function(){
		$("#month").val(getMonthStartDate('yyyy-MM'));
	});
	//上月
	$("#lastMonth").click(function(){
		$("#month").val(getLastMonthStartDate('yyyy-MM'));
	});
});


//处理map集合 新增集合 键=员工_时间
function setMap(param){
    var key=param.empId+"_"+param.date;
    map[key]=param.classId+"_"+param.isRemote+"_"+param.id;
}
//删除map里面的元素
function deleteMap(id){
    delete map[id];  
}

//判断map里面是否存在该元素
function isHaving(id){
   for(var i in map){
       if(id==i){
          return true;
        }
    }
   return false;
}

//选择班次
function selectClassSet(obj,type,employId){
	var error=false;
	$.getJSON(contextPath +'/workManagement/isWorkDate.htm?date='+$(obj).attr("date")+"&employeeId="+employId,function(data){
		
		if(data.success){
		}else{
			
			JEND.load('util.dialog', function() {
				
				JEND.util.dialog.alert(data.msg);
				
			});
			$("#updateDiv").hide();
			error=true;
			return;
		}
	});
	if(error){
		return;
	}
	
	
	
	var rowsIndex=parseFloat($(obj).closest("td").index())+parseFloat(1);
	var workDay=$("#reportList").find("tr:eq(0)").find("th:eq("+$(obj).index()+")").text();

	var strWeek ="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+$(obj).attr("date").substring(5,10)+"&nbsp;&nbsp;星期"+workDay;
	$("#weekStr").html(strWeek);
	if(type!=2){ //非审核通过
		$("#classSet").empty();
		$.ajaxSettings.async = false;
		var employeeId = $(obj).parent().attr("employId");
		var classDate = $(obj).attr("date");
		$.getJSON(contextPath +'/employeeClass/getDayShiftClass.htm?employeeId='+employeeId+"&classDate="+classDate,function(data){
			$.each(data, function(index) {
				var option = "";
					option +="<option mustAttnTime="+data[index].mustAttnTime+" value="+data[index].id+">"+data[index].name+"&nbsp;"+data[index].startTime+"-"+data[index].endTime+"&nbsp;"+data[index].mustAttnTime+"小时</option>";
				
				$("#classSet").append(option);
			});
		});
		$("#employName").val("");
		$("#employeeId").val("");
		$("#registerDate").val("");
		$("#employName").text($(obj).parent().attr("employName"));
		$("#employeeId").val(employeeId);
		$("#registerDate").val(classDate);
		
		$("#updateDiv").show();

		$("#setClassSet").show();
		$("#setClassSet").unbind();
		var tdObj = obj;
		$("#setClassSet").click(function(){
			
			var param = {
				"empId":$(obj).parent().attr("employId"),
				"date":$(obj).attr("date"),
				"isRemote":$("#isRemote").val(),
				"classId":$("#classSet").val()
			};
			
			if(type==0){
				 param = {
					"empId":$(obj).parent().attr("employId"),
					"date":$(obj).attr("date"),
					"isRemote":$("#isRemote").val(),
					"classId":$("#classSet").val(),
					"id":$(obj).attr("workId")
				};
			}  
				var key=$("#employId").val()+"_"+$("#classDate").val();
				if(isHaving(key)){
					deleteMap(key);
				}
				setMap(param);
				var isRemoteStr ="";
				if($("#isRemote").val()!='' && $("#isRemote").val()==0){
					isRemoteStr="<br/> 远程";
				}
				$(tdObj).html($("#classSet").find("option:selected").text().substring(0,1)+isRemoteStr);
		
				
				$("#updateDiv").hide();
				rowsIndex=0;
		})
	}else{
		$("#classSet").empty();
		$("#updateDiv").show();
		$("#setClassSet").unbind();
		$("#setClassSet").hide();
		var option = "";
			option +="<option >"+$(obj).attr("classsSetName")+" 已审核日期 不能修改</option>";	
		$("#classSet").append(option);
	}	
}



function gotoPage1(page){
	map = {};
	$("#showTitle").hide();
	$("#btnHidden").hide();
	if($("#month").val()==""){     
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择月份");
		});
		return;
	}
	
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
		url:  basePath + "workManagement/queryList.htm",
		success:function(response){
			$("#reportList1").empty();
			var html = "";
			var monthStr=$("#month").val();
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].leaderName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].workTypeName)+"</td>";
				
				html += "<td style='text-align:center;'>"+monthStr+"</td>";
				html += "<td><a style='color:blue;'  onclick='showDetail("+response.rows[i].id+",\""+$("#month").val()+"\")'>查看</a></td>";
				html += "</tr>";
			}
			$("#reportList1").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPageByName("commonPage1",response,page,"gotoPage1");
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
	$(".second").show();
}



//获取排班明细表格
function showDetail(id,month){
	$("#commonPage1").hide();
	$("#showTitle").show();
	$(".second").hide();
	$("#reportList").show();
	$(".tablebox").empty();
	$(".tablebox").show();
	
	var groupName=$("#groupId").find("option:selected").text();
	if(groupName==""){
		$("#groupName").text("未选择");
	}else{
		$("#groupName").text(groupName);
	}
	$("#titleClass").text("工作日历"+$("#month").val().substring(0,4)+"年"+$("#month").val().substring(5,7)+"月");

	$("#classStatus").html("&nbsp;&nbsp;&nbsp;&nbsp;"+"状态:"+status);
	//查询排班明细生成表格
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{"empId":id,"month":month},
		url:contextPath + "/workManagement/queryWorkRegistInfo.htm",
		success:function(data){
			$("#reportListTitle").find("thead").html("");
			$("#reportListTitle").find("tbody").html("");
			$("#reportList").find("thead").html("");
			$("#reportList").find("tbody").html("");
			$(".content").find(".download").remove();
			//申请日期
			$("#applyDate").val(data.applyDate);
			//状态
			$("#approvalStatus").val(data.approvalStatus);
			//加载标题
			var headTitle = "<tr>";
			var head = "<tr>";
			for(var i=0;i<data.weekDays.length;i++){
				if(i<=3){
					headTitle +="<th style='overflow-x:auto;width:100px;text-align:center;  height: 19px;'>"+data.weekDays[i]+"</th>";
				}else{
					head +="<th style='overflow-x:auto;width:10px;text-align:center;  height: 19px;'>"+data.weekDays[i]+"</th>";
				}
			}
			head += "</tr>";
			headTitle += "</tr>";
			$("#reportListTitle").find("thead").append(headTitle);
			$("#reportList").find("thead").append(head);
			//天
			var daysTitle = "<tr>";
			var days = "<tr>";
			for(var i=0;i<data.days.length;i++){
				if(i<=3){
					daysTitle +="<td style='text-align:center;  height: 19px;'>"+data.days[i]+"</td>";
				}else{
					days +="<td style='text-align:center;  height: 19px;'>"+data.days[i]+"</td>";
				}
			}
			daysTitle += "</tr>";
			days += "</tr>";
			$("#reportListTitle").find("tbody").append(daysTitle);
			$("#reportList").find("tbody").append(days);
			
			if(data.employee!=null){
				
				var classDetailTitle = "<tr employName="+data.employee.cnName+" employId="+data.employee.id+">";
				classDetailTitle +="<td style='text-align:center;height: 40px;'>"+data.employee.code+"</td>";
				classDetailTitle +="<td style='text-align:center;height: 40px;'>"+data.employee.empPosition.position.positionName+"</td>";
				classDetailTitle +="<td style='text-align:center;height: 40px;'>"+data.employee.cnName+"</td>";
				classDetailTitle +="<td style='text-align:center;height: 40px;'>"+data.shouldTime+"</td>";
				classDetailTitle +="</tr>";
				var classDetail= "<tr employName="+data.employee.cnName+" employId="+data.employee.id+">";
	
				var m = data.days.length;
				
				var x=0;
				for(var j=1;j<=m-4;j++){
					if(j<10){
						x="0"+j;
					}else{
						x=j
					}
					if(typeof(data.workDetail[x])!="undefined"){
						if(data.workDetail[x].approveStatus==0){//已通过
							classDetail +="<td onclick='selectClassSet(this,2,"+data.employee.id+");' classsSetName="+data.workDetail[x].classsSetName+" workId='"+data.workDetail[x].id+"' date='"+data.dates[j+3]+"' classId='"+data.workDetail[x].classsSettingId+"'>"+(getValByUndefined(data.workDetail[x].classsSetName)!=""?data.workDetail[x].classsSetName:"休")+"<br/>远程</td>";					
						}else{
							classDetail +="<td onclick='selectClassSet(this,0,"+data.employee.id+");' workId='"+data.workDetail[x].id+"' date='"+data.dates[j+3]+"' classId='"+data.workDetail[x].classsSettingId+"'>"+(getValByUndefined(data.workDetail[x].classsSetName)!=""?data.workDetail[x].classsSetName:"休")+"<br/>远程</td>";					
						}
					}else{
						classDetail +="<td  onclick='selectClassSet(this,1,"+data.employee.id+");' date='"+data.dates[j+3]+"' style='text-align:center;height: 40px;'>&nbsp;</td>";
					}
					
				}
				classDetail += "</tr>";
				$("#reportListTitle").find("tbody").append(classDetailTitle);
				$("#reportList").find("tbody").append(classDetail);
			}
			
			$("#btnHidden").show();
		}
	});
}


//保存远程登记数据
function saveWorkMap(){
	if(JSON.stringify(map)=="{}"){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("没有任何变更，无需保存！");
		});
		return;
	}
	pageLoading(true);//加载动画
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{info:JSON.stringify(map)},
		url:contextPath + "/workManagement/saveMapWorkRegister.htm",
		success:function(data){
			if(data.success){
				map = {};
				JEND.load('util.dialog', function() {
					JEND.util.dialog.alert(data.message);
				});
			}else{
				JEND.load('util.dialog', function() {
					JEND.util.dialog.alert(data.message);
				});
			}
			closeCounterDiv();
			pageLoading(false);//关闭动画
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}

function  returnMap(){
	gotoPage1();
}



function saveDiv(){
	$("#reconfirmDiv").show();
}


//关闭窗口操作
function closeDiv(){
	$("#fastApproveDiv").hide();
	$("#passDiv").hide();
	$("#attnRefuseDiv").hide();
	$("#refuseDiv").hide();
	$("#detailDiv").hide();
}



function closeCounterDiv(){
	$("#updateDiv").hide();
	$("#settingInfo").hide();
	$("#reconfirmDiv").hide();
}

