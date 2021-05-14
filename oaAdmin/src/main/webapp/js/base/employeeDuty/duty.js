var map = {};
$(function(){
	signExcel = new SignExcel();  
	signExcel.init();
	var currentCompanyId = $("#currentCompanyId").val();
	$("#dutyTab").find("li").each(function(i){
		var obj = $(this);
		$(obj).click(function(){
			if(i==0){
				//查询未提交的值班安排
				getUnCommitData(1);
			}else if(i==1){
				gotoPage($("#pageNo").val());
			}
		})
	});
	$("#downloadDutyTemplate").click(function(){
		var departId = $("#searchDepart").val();
		var year = $("#searchYear").val();
		var vacation = $("#searchVacation").val();
		if($("#searchDepart").val()==""){
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("请选择部门！");
			});
			return;
		}
		if($("#searchYear").val()==""){
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("请选择年份！");
			});
			return;
		}
		if($("#searchDepart").val()==""){
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("请选择节假日！");
			});
			return;
		}
		exportDutyTemplate(departId,year,vacation);
	});
});

//获取未提交的值班数据
function getUnCommitData(type){
	//清空map
	map = {};
	//type=0,编辑模式，type=1查看模式
	if($("#searchDepart").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择部门！");
		});
		return;
	}
	if($("#searchYear").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择年份！");
		});
		return;
	}
	if($("#searchVacation").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择节假日！");
		});
		return;
	}
	$("#depart").val($("#searchDepart").val());
	$("#year").val($("#searchYear").val());
	$("#vacation").val($("#searchVacation").val());
	pageLoading(true);//加载动画
	//查询排班明细生成表格
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{departId:$("#depart").val(),year:$("#year").val(),vacation:$("#vacation").val()},
		url:contextPath + "/duty/getUnCommitDuty.htm",
		success:function(data){
			if(data.success){
				$("#detailList").find("tbody").html("");
				//标题
				$("#detailTitle").empty();
				$("#detailTitle").html("<strong>"+data.title+"</strong>");
				//公司
				$("#companyName").val(data.company);
				//部门
				$("#departName").val(data.depart);
				//状态
				$("#approvalStatus").val(data.approvalStatus);
				if(data.dutyDetailList != null){
					for(i=0;i<data.dutyDetailList.length;i++){
						//编辑模式
						if(type==0){
							//将所有数据放入map中
							var json = {
									vacationDate: data.dutyDetailList[i].vacationDate, 
						        	employeeIds: data.dutyDetailList[i].employeeIds,
						        	startTime:data.dutyDetailList[i].startTime, 
						        	endTime:data.dutyDetailList[i].endTime, 
						        	workHours:data.dutyDetailList[i].workHours, 
						        	dutyItem:data.dutyDetailList[i].dutyItem, 
						        	remarks:data.dutyDetailList[i].remarks, 
						        }
							var key = data.dutyDetailList[i].employeeIds+"_"+data.dutyDetailList[i].vacationDate;
							map[key]=json
							$('#detailList tr').find('th:eq(0)').show();
							var dutyDetail = "<tr class='dutyDetailList' id="+key+">";
							dutyDetail +="<td style='text-align:center;width:50px;'><a class='icon-shanchu' href='#' onclick='deleteDutyDetail(this);'></a></td>";
							dutyDetail +="<td style='text-align:center;width:75px;'>"+data.dutyDetailList[i].vacationDate+"</td>";			//日期
							dutyDetail +="<td style='text-align:center;width:75px;'>星期"+data.dutyDetailList[i].weekStr+"</td>";				//星期
							dutyDetail +="<td style='text-align:center;width:100px;'>"+data.dutyDetailList[i].codes+"</td>";				//员工编号
							dutyDetail +="<td style='text-align:center;width:100px;'>"+data.dutyDetailList[i].names+"</td>";				//员工姓名
							dutyDetail +="<td style='text-align:center;width:100px;'>From: "+data.dutyDetailList[i].startHours+"</td>";		//开始时间
							dutyDetail +="<td style='text-align:center;width:100px;'>To: "+data.dutyDetailList[i].endHours+"</td>";			//结束时间
							dutyDetail +="<td style='text-align:center;width:80px;'>"+data.dutyDetailList[i].workHours+"</td>";				//工作小时数
							dutyDetail +="<td style='text-align:center;width:200px;'>"+data.dutyDetailList[i].dutyItem+"</td>";				//工作内容简述
							dutyDetail +="<td style='text-align:center;width:150px;'>"+data.dutyDetailList[i].remarks+"</td>";				//备注
							dutyDetail += "</tr>";                                
							$("#detailList").find("tbody").append(dutyDetail);
						}else{
							//查看模式
							//隐藏删除列
							$('#detailList tr').find('th:eq(0)').hide();
							var dutyDetail = "<tr class='dutyDetailList' >";
							dutyDetail +="<td style='text-align:center;width:75px;'>"+data.dutyDetailList[i].vacationDate+"</td>";			//日期
							dutyDetail +="<td style='text-align:center;width:75px;'>星期"+data.dutyDetailList[i].weekStr+"</td>";				//星期
							dutyDetail +="<td style='text-align:center;width:100px;'>"+data.dutyDetailList[i].codes+"</td>";				//员工编号
							dutyDetail +="<td style='text-align:center;width:100px;'>"+data.dutyDetailList[i].names+"</td>";				//员工姓名
							dutyDetail +="<td style='text-align:center;width:100px;'>From: "+data.dutyDetailList[i].startHours+"</td>";		//开始时间
							dutyDetail +="<td style='text-align:center;width:100px;'>To: "+data.dutyDetailList[i].endHours+"</td>";			//结束时间
							dutyDetail +="<td style='text-align:center;width:80px;'>"+data.dutyDetailList[i].workHours+"</td>";				//工作小时数
							dutyDetail +="<td style='text-align:center;width:200px;'>"+data.dutyDetailList[i].dutyItem+"</td>";				//工作内容简述
							dutyDetail +="<td style='text-align:center;width:150px;'>"+data.dutyDetailList[i].remarks+"</td>";				//备注
							dutyDetail += "</tr>";                                
							$("#detailList").find("tbody").append(dutyDetail);
						}
					}
				}
				var html = "";
				if(type==0){
					html += "<button onclick='saveDutyPlan();' class='btn-green btn-large'><span><i class='icon-baocun'></i>保存</span></button>";
					html += "<button onclick='addDuty();' class='btn-orange btn-large'><span><i class='icon-jia1'></i>添加值班</span></button>";
					html += "<button onclick='getUnCommitData(1);' class='btn-blue btn-large'><span><i class='icon-chexiao'></i>返回</span></button>"; 
				}else{
					html += "<button onclick='commitDuty();' class='btn-green btn-large'><span><i class='icon-shangchuan1'></i>提交审核</span></button>"; 
					html += "<button onclick='getUnCommitData(0);' class='btn-blue btn-large'><span><i class='icon-bianji1'></i>编辑</span></button>";
				}
				$(".buttonDiv").empty();
				$(".buttonDiv").append(html);
				$("#detailDiv").show();//显示详情表格
				$(".scheduleRecord").hide();
				$("#commonPage").hide();
			}else{
				JEND.load('util.dialog', function() {
					JEND.util.dialog.alert(data.message);
				});
			}
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

function addDuty(){
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{year:$("#year").val(),vacation:$("#vacation").val()},
		url:contextPath + "/duty/getDutyDateList.htm",
		success:function(data){
			if(data.success){
				
				$("#dutyDate").append("<option value=''>请选择值班日期</option>");
				for(var i=0;i<data.dateList.length;i++){
					if(data.dateList[i].type==3){
						$("#dutyDate").append("<option weekDay="+data.dateList[i].weekDay+" value="+data.dateList[i].day+">"+data.dateList[i].day+"("+data.dateList[i].subject+")</option>");
					}else{
						$("#dutyDate").append("<option weekDay="+data.dateList[i].weekDay+" value="+data.dateList[i].day+">"+data.dateList[i].day+"</option>");
					}
				}
				
			}else{
				JEND.load('util.dialog', function() {
					JEND.util.dialog.alert(data.message);
				});
			}
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
	$("#dutyPlanDiv").show();
}

function getDutyEmployee(obj){
	showGetDeptList($("#depart").val(),obj);
}

function checkStartTime(){
	$("#workHours").val("")
	if($("#from").val() != ""){
		var startTime = $("#from").val();
		var minute = startTime.split(":")[1];
		if(0<minute<30){
			$("#from").val(startTime.split(":")[0]+":00")
		}
		if(minute >= 30){
			$("#from").val(startTime.split(":")[0]+":30")
		}
		calculatedHours();
	}
}
function checkEndTime(){
	$("#workHours").val("");
	if($("#to").val() != ""){
		var endTime = $("#to").val();
		var minute = endTime.split(":")[1];
		if(0<minute<30){
			$("#to").val(endTime.split(":")[0]+":00")
		}
		if(minute >= 30){
			$("#to").val(endTime.split(":")[0]+":30")
		}
		calculatedHours();
	}
}
//计算小时数
function calculatedHours(){
	if(typeof($("#from").val())!="undefined" && $("#from").val()!="" && typeof($("#to").val())!="undefined" && $("#to").val()!=""){
		var startTime=$("#from").val().substring(0,2);
		var endTime=$("#to").val().substring(0,2);
		//如果结束时间是0点则结束时间设为24点
		if(endTime == 0){
			endTime = 24;
		}
		var startHours=($("#from").val().substring(3,5)==30)?0.5:0;
		var endHours=($("#to").val().substring(3,5)==30)?0.5:0;
		
		var hours;
		if(startTime<endTime){
			hours=(parseFloat(endTime)+parseFloat(endHours))-(parseFloat(startTime)+parseFloat(startHours));
			if(hours>=10){
				hours=hours-2;
			}else if(hours>=5){
				hours=hours-1;
			}
			$("#workHours").val(hours);
			if($("#workHours").val() == "NaN"){
				$("#workHours").val("")
			}
		}else{
			$("#workHours").val(0);
		}
	}
}
function addDutyPlan(){
	if($("#dutyDate").val()=="" ||$("#from").val()==""||$("#to").val()==""||$("#dutyItem").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请完善值班安排内容！");
		});
		return;
	}
	if($("#workHours").val() == 0){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("值班结束时间需大于开始时间！");
		});
		return;
	}
	//将保存参数放入数组中
	var startTime=$("#dutyDate").val()+" "+$("#from").val()+":00";
	var endTime=$("#dutyDate").val()+" "+$("#to").val()+":00";
	var json = {
			vacationDate: $("#dutyDate").val(), 
        	employeeIds: $("#dutyEmployee").attr("employId"),
        	startTime:startTime,
        	endTime:endTime,
        	workHours:$("#workHours").val(),
        	dutyItem:$("#dutyItem").val(),
        	remarks:$("#remarks").val()
        }
	var key = $("#dutyEmployee").attr("employId")+"_"+$("#dutyDate").val();
	//一个员工一天只有一条数据
	if(map.hasOwnProperty(key)){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("该员工当天已存在值班安排！");
		});
	}else{
		map[key]=json;
		var dutyDetail = "<tr class='dutyDetailList' id="+key+">";
		dutyDetail +="<td style='text-align:center;width:50px;'><a class='icon-shanchu' href='#' onclick='deleteDutyDetail(this);'></a></td>";
		dutyDetail +="<td style='text-align:center;width:75px;'>"+$("#dutyDate").val()+"</td>";
		dutyDetail +="<td style='text-align:center;width:75px;'>"+$("#dutyDate").find("option:selected").attr("weekDay")+"</td>";
		dutyDetail +="<td style='text-align:center;width:100px;'>"+$("#dutyEmployee").attr("employCode")+"</td>";
		dutyDetail +="<td style='text-align:center;width:100px;'>"+$("#dutyEmployee").val()+"</td>";
		dutyDetail +="<td style='text-align:center;width:100px;'>"+"From: "+$("#from").val()+"</td>";
		dutyDetail +="<td style='text-align:center;width:100px;'>"+"To: "+$("#to").val()+"</td>";
		dutyDetail +="<td style='text-align:center;width:80px;'>"+$("#workHours").val()+"</td>";
		dutyDetail +="<td style='text-align:center;width:200px;'>"+$("#dutyItem").val()+"</td>";
		dutyDetail +="<td style='text-align:center;width:150px;'>"+$("#remarks").val()+"</td>";
		dutyDetail += "</tr>";
		$("#detailList").find("tbody").append(dutyDetail);
	}
	
	
	closeDiv();
}
//删除整行
function deleteDutyDetail(obj){
	var date = $(obj).parents("tr").find("td").eq(1).text();
	var dutyEmp = $(obj).parents("tr").find("td").eq(4).text()
	secondConfirmation({
		msg:'确认删除'+dutyEmp+'在'+date+'值班安排吗？',
		sureFn:function(){
			var key = $(obj).parents("tr").attr("id");
			delete map[key];
			$(obj).parents("tr").remove();
		}
	})
}
function saveDutyPlan(){
	secondConfirmation({
		msg:'确认保存值班安排吗？',
		sureFn:function(){
			pageLoading(true);//加载动画
			var jsonArray = [];
			 $.each(map,function(key,values){
				 jsonArray.push(values);
			 });
			var data = JSON.stringify(jsonArray);
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				contentType:"application/json",
				data:data,
				url:contextPath + "/duty/saveApplicationDutyDetail.htm?departId="+$("#depart").val()+"&year="+$("#year").val()+"&vacation="+$("#vacation").val(),
				success:function(data){
					if(data.success){
						getUnCommitData(0);
						JEND.load('util.dialog', function() {
							JEND.util.dialog.alert(data.message);
						});
					}else{
						JEND.load('util.dialog', function() {
							JEND.util.dialog.alert(data.message);
						});
					}
					closeSecondConfirmationDiv();
					pageLoading(false);//关闭动画
				},
				complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					closeSecondConfirmationDiv()
					pageLoading(false);//关闭动画
		        }
			});
		}
		
	})
}
function commitDuty(){
	secondConfirmation({
		"msg":"是否确认提交？",
		sureFn:function(){
			pageLoading(true);//加载动画
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{departId:$("#depart").val(),year:$("#year").val(),vacation:$("#vacation").val()},
				url:contextPath + "/duty/commitDuty.htm",
				success:function(data){
					if(data.success){
						JEND.load('util.dialog', function() {
							JEND.util.dialog.alert(data.message);
							gotoPage();
						});
					}else{
						JEND.load('util.dialog', function() {
							JEND.util.dialog.alert(data.message);
						});
					}
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
	})
}
//关闭弹框
function closeDiv(){
	$("#dutyDate").empty();
	$("#dutyEmployee").val("");
	$("#from").val("");
	$("#to").val("");
	$("#workHours").val("");
	$("#dutyItem").val("");
	$("#remarks").val("");
	$("#dutyPlanDiv").hide();
}


//分页查询值班记录
function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=15";
	var url = "duty/getDutyRecord.htm";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + url,
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].year+"</td>";//年份
				html += "<td style='text-align:center;'>"+response.rows[i].vacationName+"</td>";//法定节假日
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";//部门
				html += "<td style='text-align:center;'>"+response.rows[i].classSettingPerson+"</td>";//值班排班人
				html += "<td style='text-align:center;'>"+response.rows[i].submitDate+"</td>";//提交日期
				html += "<td style='text-align:center;'>"+response.rows[i].hrAuditor+"</td>";//人事批核人
				var approvalStatus = "";
				if(response.rows[i].approvalStatus==100){
					approvalStatus = "待审批";
				}else if(response.rows[i].approvalStatus==200){
					approvalStatus = "已审批";
				}else if(response.rows[i].approvalStatus==300){
					approvalStatus = "已拒绝";
				}else if(response.rows[i].approvalStatus==400){
					approvalStatus = "已撤销";
				}else if(response.rows[i].approvalStatus==500){
					approvalStatus = "已失效";
				}
				html += "<td style='text-align:center;'>"+approvalStatus+"</td>";
				html += "<td style='text-align:center;'><a onclick='showDetail("+response.rows[i].id+");' style='color:blue;' href='#'>查看</a></td>";
				html += "</tr>";
			}
			$("#reportList").append(html);
			$("#detailDiv").hide();//显示详情表格
			$(".scheduleRecord").show();
			$("#commonPage").show();
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

//查看排班记录
function showDetail(id){
	//查询排班明细生成表格
	pageLoading(true);//关闭动画
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{id:id},
		url:contextPath + "/duty/getDutyDetailById.htm",
		success:function(data){
			if(data.success){
				//标题
				$("#detailTitle").empty();
				$("#detailList").find("tbody").html("");
				$("#detailTitle").html("<strong>"+data.title+"</strong>");
				//公司
				$("#companyName").val(data.companyName);
				//部门
				$("#departName").val(data.departName);
				//状态
				var approvalStatus = "";
				if(data.approvalStatus==100){
					approvalStatus = "待审批";
				}else if(data.approvalStatus==200){
					approvalStatus = "已审批";
				}else if(data.approvalStatus==300){
					approvalStatus = "已拒绝";
				}else if(data.approvalStatus==400){
					approvalStatus = "已撤销";
				}else if(data.approvalStatus==500){
					approvalStatus = "已失效";
				}
				$("#approvalStatus").val(approvalStatus);
				if(data.dutyDetailList != null){
					for(i=0;i<data.dutyDetailList.length;i++){
						$('#detailList tr').find('th:eq(0)').hide();
						var dutyDetail = "<tr class='dutyDetailList' >";
						dutyDetail +="<td style='text-align:center;width:75px;'>"+data.dutyDetailList[i].vacationDate+"</td>";			//日期
						dutyDetail +="<td style='text-align:center;width:75px;'>星期"+data.dutyDetailList[i].weekStr+"</td>";				//星期
						dutyDetail +="<td style='text-align:center;width:100px;'>"+data.dutyDetailList[i].codes+"</td>";				//员工编号
						dutyDetail +="<td style='text-align:center;width:100px;'>"+data.dutyDetailList[i].names+"</td>";				//员工姓名
						dutyDetail +="<td style='text-align:center;width:100px;'>From: "+data.dutyDetailList[i].startHours+"</td>";		//开始时间
						dutyDetail +="<td style='text-align:center;width:100px;'>To: "+data.dutyDetailList[i].endHours+"</td>";			//结束时间
						dutyDetail +="<td style='text-align:center;width:80px;'>"+data.dutyDetailList[i].workHours+"</td>";				//工作小时数
						dutyDetail +="<td style='text-align:center;width:200px;'>"+data.dutyDetailList[i].dutyItem+"</td>";				//工作内容简述
						dutyDetail +="<td style='text-align:center;width:150px;'>"+data.dutyDetailList[i].remarks+"</td>";				//备注
						dutyDetail += "</tr>";                                
						$("#detailList").find("tbody").append(dutyDetail);
					}
				}
			}else{
				JEND.load('util.dialog', function() {
					JEND.util.dialog.alert(data.message);
				});
			}
			pageLoading(false);//关闭动画
			$(".scheduleRecord").hide();
			$("#detailDiv").show();//显示详情表格
			$("#commonPage").hide();
			$(".buttonDiv").empty();
			var html = "";
			html += "<button onclick='backWaitList();' class='btn-blue btn-large'><span><i class='icon-chexiao'></i>返回</span></button>"; 
			$(".buttonDiv").append(html);
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
		}
	});
	
}

function backWaitList(){
	$("#detailDiv").hide();//显示详情表格
	$(".scheduleRecord").show();
	$("#commonPage").show();
}

function exportDutyTemplate(departId,year,vacation){
	var data="departId="+departId+"&year="+year+"&vacation="+vacation;
	window.location.href = basePath + "duty/exportDutyTemplate.htm?"+data;
};
var SignExcel = function(){  
    
    this.init = function(){
          
        //上传excel  
         $("#uploadEventBtn").unbind("click").bind("click",function(){  
             $("#uploadEventFile").click();  
         });  
         $("#uploadEventFile").bind("change",function(){  
        	 var filePath = $("#uploadEventFile").val();
        	 var fileName = filePath.split("\\")[2];
        	 $("#uploadEventPath").attr("value",fileName);
         });  
          
    }; 
  //点击上传按钮  
    this.uploadBtn = function(){  
        var uploadEventFile = $("#uploadEventFile").val();  
        if(uploadEventFile == ''){  
        	var msg = "请选择excel,再上传";
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert(msg);
			});
        }else if(uploadEventFile.lastIndexOf(".xls")<0){//可判断以.xls和.xlsx结尾的excel  
        	var msg = "只能上传Excel文件";
        	JEND.load('util.dialog', function() {
				JEND.util.dialog.alert(msg);
			});
        }else{  
            var url =  basePath + 'duty/importDutyTemplate.htm';
            var departId = $("#searchDepart").val();
            var year = $("#searchYear").val();
            var vacationName = $("#searchVacation").val();
            $("#uploadDepart").attr("value",departId);
            $("#uploadYear").attr("value",year);
            $("#uploadVacation").attr("value",vacationName);
            var formData = new FormData($('form')[1]);  
            signExcel.sendAjaxRequest(url,'POST',formData);  
        }  
    };
    this.sendAjaxRequest = function(url,type,data){
    	pageLoading(true);//加载动画
    	
        $.ajax({  
            url : url,  
            type : type,  
            data : data,  
    		dataType:'json',
            success : function(response) {
            	pageLoading(false);//关闭动画
            	var result = response.result;
            	var msg = response.resultMsg
            	JEND.load('util.dialog', function() {
    				JEND.util.dialog.alert(msg);
    			});
            	if(result == "success"){
            		getUnCommitData(1);
            	}
            },  
            error : function(response) { 
            	pageLoading(false);//关闭动画
            },  
    		complete:function(XMLHttpRequest,textStatus){  
    			if(XMLHttpRequest.status=="403"){
    				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
    			}
    			pageLoading(false);//关闭动画
            },
            cache : false,  
            contentType : false,  
            processData : false  
        });  
    };  
}
