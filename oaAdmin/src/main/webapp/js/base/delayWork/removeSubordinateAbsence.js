$(function(){
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});
	divShow();
});

function gotoPage(page){
	if(!page){
		page = 1;
	}
	pageLoading(true);//加载动画
	$("#pageNo").val(page);
	var data = $("#queryform").serialize()+ "&page=" + $("#pageNo").val() + "&rows=10";
	$.ajax({
		async:true,
		type:'post',
		data:data,
		dataType:'json',
		url:contextPath + "/removeSubordinateAbsence/getRemoveSubordinateAbsencePage.htm",
		success:function(data){		
			$("#h3InfoShow").show();
			$("#reportList").find("thead").html("");
			$("#reportList").find("tbody").html("");
			$("#reportListTitle").find("thead").html("");
			$("#reportListTitle").find("tbody").html("");
			//加载标题
			if(data.success){
				var headText = "<tr>";
				var head = "<tr>";
				for(var i=0;i<data.weekDays.length;i++){
					if(i<=4){
						headText +="<th style='overflow-x:auto;text-align:center;'>&nbsp;</th>";
					}else{
						head +="<th style='overflow-x:auto;text-align:center;'>"+data.weekDays[i]+"</th>";
					}
				}					
				//天
				var daysTitle = "<tr>";
				var days = "<tr>";
				for(var i=0;i<data.days.length;i++){
					if(i<=4){
						daysTitle +="<td style='text-align:center;'>"+data.days[i]+"</td>";
					}else{
						days +="<td style='text-align:center;'>"+data.days[i]+"</td>";
					}
				}
				daysTitle += "</tr>";
				days += "</tr>";
				
				headText += "</tr>";
				head += "</tr>";
				$("#reportListTitle").find("thead").append(headText);
				$("#reportList").find("thead").append(head);
				
				$("#reportListTitle").find("tbody").append(daysTitle);
				$("#reportList").find("tbody").append(days);
				var m = data.days.length;
				var should_time=0,dayCount=0,must_attn_time=0;
				if(data.page.rows!=null){
					for(i=0;i<data.page.rows.length;i++){
						var classDetailTitle = "<tr class='parentTr' empCnName="+data.page.rows[i].cnName+" empId="+data.page.rows[i].employeeId+">";
						classDetailTitle +="<td style='text-align:center;'>"+data.page.rows[i].empCode+"</td>";
						classDetailTitle +="<td style='text-align:center;'>"+data.page.rows[i].empCnName+"</td>";
						classDetailTitle +="<td style='text-align:center;'>"+data.page.rows[i].empAttnCount+"</td>";
						classDetailTitle +="<td style='text-align:center;'>"+data.page.rows[i].overEmpAttnHours+"</td>";
						classDetailTitle +="<td style='text-align:center;'>"+data.page.rows[i].attnStatusCount+"</td>";
						classDetailTitle+="</tr>"
						var count=0;
						var classDetail="<tr class='parentTr'>";
						for(var j=1;j<=m-5;j++){
							if(typeof(data.page.rows[i][j])=="undefined"){								
								classDetail +="<td>&nbsp;</td>";
							}else{
								if(data.page.rows[i][j].fontColor=="blue"){
									classDetail +="<td style='color:blue;' onClick='chlckInfos("+data.page.rows[i][j].employId+",\""+data.page.rows[i][j].attnDate+"\",this)'>异常</td>";		
								}else if(data.page.rows[i][j].fontColor=="gray"){
									classDetail +="<td  style='color:gray;' onClick='chlckInfos("+data.page.rows[i][j].employId+",\""+data.page.rows[i][j].attnDate+"\",this)'>异常</td>";	
								}else{
									classDetail +="<td style='color:black;' onClick='chlckInfos("+data.page.rows[i][j].employId+",\""+data.page.rows[i][j].attnDate+"\",this)'>异常</td>";	
								}
							}	
						}
						classDetail += "</tr>";
						$("#reportListTitle").find("tbody").append(classDetailTitle);
						$("#reportList").find("tbody").append(classDetail);
					}
				}	
				if(data != null && data.page.pageNo != null) {
					page = data.page.pageNo;
				}
				initPage("commonPage",data.page,page);
			}else{
				JEND.load('util.dialog', function() {
					if(data.message==null){
						JEND.util.dialog.alert("查询失败");
					}else{
						JEND.util.dialog.alert(data.message);
					}					
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
	
}
function cancelAttnDetail(){
	secondConfirmation({
	   "msg":"确认要撤销？",
		sureFn:function(){
			cancelAttn();
		}
	});
}

function cancelAttn(){	
	var orderNosList = new Array();
	orderNosList.push($("#processinstanceId").val());
	pageLoading(true);//加载动画
	var data ="processIds="+orderNosList+"&comment="+$("#removeAbsenceReason").val()+"&commentType=back";
	$.ajax({
		async:true,
		type:'post',
		data:data, 
		dataType:'json',
		url:contextPath + "/removeSubordinateAbsence/completeTask.htm",
		success:function(data){
			if(data.sucess){
				JEND.page.alert({type:"success", message:data.msg});
				var overAttnHours=$("#reportListTitle").find("tr:eq("+$("#hiddenTrIndex").val()+")").find("td:eq(3)").text();
				$("#reportListTitle").find("tr:eq("+$("#hiddenTrIndex").val()+")").find("td:eq(3)").text(parseFloat(overAttnHours)+parseFloat($("#workHours").val()));				
				$("#reportList").find("tr:eq("+$("#hiddenTrIndex").val()+")").find("td:eq("+$("#hiddenTdIndex").val()+")").css("color","black");
				closeDiv();
			}else{
				JEND.page.alert({type:"error", message:data.msg});
			}
			
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading();//加载动画
        }
	});
}


function commitAttnDetail(overEmpAttnHours){
	if(overEmpAttnHours<0.5){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("该员工出勤多余小时数不足，无法申请");
		});
	}else if($("#removeAbsenceReason").val()=="" || $("#removeAbsenceReason").val()==null){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请填写缺勤理由后提交");
		});
	}else{
		pageLoading(true);//加载动画
		var data =$("#updateForm").serialize();
		$.ajax({
			async:true,
			type:'post',
			data:data,
			dataType:'json',
			url:contextPath + "/removeSubordinateAbsence/commitApplicationForm.htm",
			success:function(data){
				if(data.success){
					JEND.page.alert({type:"success", message:data.message});
					var overAttnHours=$("#reportListTitle").find("tr:eq("+$("#hiddenTrIndex").val()+")").find("td:eq(3)").text();
					$("#reportListTitle").find("tr:eq("+$("#hiddenTrIndex").val()+")").find("td:eq(3)").text(parseFloat(overAttnHours)-parseFloat($("#workHours").val()));				
					$("#reportList").find("tr:eq("+$("#hiddenTrIndex").val()+")").find("td:eq("+$("#hiddenTdIndex").val()+")").css("color","blue");				
					closeDiv();
				}else{
					JEND.page.alert({type:"error", message:data.message});
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
}


function chlckInfos(empId,attnDate,obj){
	$("#hiddenTrIndex").val(($(obj).closest("tr").index()+1));
	$("#hiddenTdIndex").val($(obj).index());
	$("#removeAbsenceReason").val("");
	$("#workHours").val("");
	$("#removeAbsenceHours").val("");
	$("#startDate").val("");
	$("#endDate").val("");
	$("#processinstanceId").val("");
	
	
	$("#attnDate").val(attnDate);
	
		var data ="empId="+empId+"&attnDate="+attnDate;
		$.ajax({
			async:false,
			type:'post',
			data:data,
			dataType:'json',
			url:contextPath + "/removeSubordinateAbsence/getAttnDetail.htm",
			success:function(data){
				$("#reconfirmOrSubmit").show();
				$("#startDate").prop("disabled", true);
				$("#endDate").prop("disabled", true);
				$("#removeAbsenceReason").prop("disabled", false);
				$("#pb_overAttnTime").text();
				//动态赋值按钮
				$("#reconfirmOrSubmit").attr('onclick','').unbind('click');
				$("#employeeId").val(empId);
				var attnStr="";
				var attnStrData="";
				if(data.success){
					//基本属性赋值
					
					$("#pb_name").text(data.emp.cnName);
					$("#pb_leaderName").text(data.emp.leaderName);
					//考勤时间多段赋值
					if(data.removeSubordinateAbsence!=null){
						$("#workHours").val(data.removeSubordinateAbsence.removeAbsenceHours);
						$("#removeAbsenceHours").val(data.removeSubordinateAbsence.removeAbsenceHours);
						if(data.removeSubordinateAbsence!=null){
							$("#removeAbsenceReason").val((data.removeSubordinateAbsence.removeAbsenceReason==null)?"":data.removeSubordinateAbsence.removeAbsenceReason);	
						}
						$("#pb_overAttnTime").text(((typeof(data.removeSubordinateAbsence.overHoursOfAttendance)=="undefined" || data.removeSubordinateAbsence.overHoursOfAttendance==null)?0:data.removeSubordinateAbsence.overHoursOfAttendance)+"小时");
					}else{
						$("#workHours").val(data.removeAbsenceHours);
						$("#removeAbsenceHours").val(data.removeAbsenceHours);
						$("#pb_overAttnTime").text(((typeof(data.overHoursOfAttendance)=="undefined" || data.overHoursOfAttendance==null)?0:data.overHoursOfAttendance)+"小时");
					}
					if(data.attnDetailList!=null && data.attnDetailList.length>0){
						for(i=0;i<data.attnDetailList.length;i++){
							attnStr += ((data.attnDetailList[i].startTime==null && typeof(data.attnDetailList[i].startTime)=="undefined")?"空卡":data.attnDetailList[i].startTime.substring(11,16)) +"-"+((data.attnDetailList[i].endTime==null && typeof(data.attnDetailList[i].endTime)=="undefined")?"空卡":data.attnDetailList[i].endTime.substring(11,16))+"&nbsp;/&nbsp;";
							attnStrData += ((data.attnDetailList[i].startTime==null && typeof(data.attnDetailList[i].startTime)=="undefined")?"空卡":data.attnDetailList[i].startTime.substring(11,16))+"-"+((data.attnDetailList[i].endTime==null && typeof(data.attnDetailList[i].endTime)=="undefined")?"空卡":data.attnDetailList[i].endTime.substring(11,16))+"，";
						}
					}	
					
					if(attnStr!=""){
						attnStr=attnStr.substr(0,attnStr.length-7);
					}
					$("#attnWorkHoursList").html(attnStr);
					if(attnStrData!=""){
						attnStrData=attnStrData.substr(0,attnStrData.length-1);
					}
					//撤回 赋值流程id
					if(data.removeSubordinateAbsence){
						$("#processinstanceId").val(data.removeSubordinateAbsence.processinstanceId);
					}
				   	$("#pb_yesterDayEndTime").text(data.yesterdayOffTimeStr);
				    
					$("#yesterdayOffTime").val((data.yesterdayOffTime==null)?null:data.yesterdayOffTime);
				    $("#attendanceDate").val((attnDate==null)?"":attnDate);
				    $("#attendanceHour").val(attnStrData);
				    $("#submitterId").val(data.emp.reportToLeader);
				    
				    $("#pb_date").text(data.attnDate);
					switch(data.buttonType){
					     case 1:
					 		$("#reconfirmOrSubmitStr").text("提交");
					 		$("#retrunBut").text("取消"); 
					    	$("#delayItem").prop("disabled", false);
					        $("#reconfirmOrSubmit").bind("click",function(){
					    		secondConfirmation({
						    	   "msg":"是否要提交？",
						    		sureFn:function(){
						    			commitAttnDetail(data.overHoursOfAttendance);
						    		}
						    	});
					        });
					        $("#retrunBut").show();
					        $("#cancelBut").hide();
					        break;
					     case 2:
					 		$("#reconfirmOrSubmitStr").text("撤销"); 
					    	$("#cancelBut").show();
					    	$("#reconfirmOrSubmit").hide();
					    	$("#retrunBut").hide();
					    	break;
					     case 3:
					    	$("#retrunBut").text("关闭"); 
					    	$("#removeAbsenceReason").prop("disabled", true);
					    	$("#retrunBut").show();
					    	$("#reconfirmOrSubmit").hide();
					    	$("#cancelBut").hide();
					    	break;
					} 
					$("#updateDiv").show();
					var leftW = 100*(1-$("#updateDivCen").width()/$("body").width())/2;
					$("#updateDivCen").css("left",leftW+"%");  
				}else{
					JEND.load('util.dialog', function() {
						JEND.util.dialog.alert(data.message);
					});
				}
			}
		});
}
function closeDiv(){
	$("#updateDiv").hide();
}
function divShow(){
var height= $(window).height()>=488?488:$(window).height()-10;
var html="";
html+="<div class='popbox-bg'></div>";    
html+="<div class='popbox-center' id='updateDivCen' style='top: 0%; left: 30%'>";   
html+="<div class='popbox-main' style=''>"; 
html+="<div class='cun-pop-content' style='height:"+height+"px;overflow-y: auto;'>";  
html+="<div class='form-wrap'>"; 
html+="<div class='title'>"; 
html+="<strong id='titleStr'><i class='icon add-user'></i>消下属缺勤</strong>"; 
html+="<i onclick='closeDiv();' class='mo-houtai-box-close' style='width: 25px;height: 30px;'></i>"; 							
html+="</div>"; 
	html+="<div class=''>"; 
		html+="<input type='hidden' id='processinstanceId' name='processinstanceId' />"; 
		html+="<form id='updateForm' class='updateForm'>"; 
		html+="<input type='hidden' id='employeeId' name='employeeId' value=''/>"; 
		html+="<input type='hidden' id='id' name='id' value=''/>"; 
		html+="<input type='hidden' id='hiddenTrIndex' />"; 
		html+="<input type='hidden' id='hiddenTdIndex' />"; 
		html+="<input type='hidden' id='submitterId' name='submitterId' /><!-- 主管id -->"; 
		html+="<input type='hidden' id='yesterdayOffTime' name='yesterdayOffTime' /><!--昨天下班时间-->"; 
		html+="<input type='hidden' id='attendanceDate' name='attendanceDate' /><!--//考勤时间 -->"; 
		html+="<input type='hidden' id='attendanceHour' name='attendanceHour' /><!--//考勤时间段  -->"; 	
		html+="<input type='hidden' id='removeAbsenceHours' name='removeAbsenceHours' /><!--//考勤时间段  -->"; 	
		html+="<div class='col'>"; 
		html+="<div class='form'>"; 
		html+="<label class='w'  style='width:130px;'>消缺勤员工姓名：</label>"; 
		html+="<span id='pb_name'></span>"; 
		html+="</div>"; 
			html+="</div>"; 
				html+="<div class='col'>"; 
				html+="<div class='form'>"; 
				html+="<label class='w'  style='width:130px;'>申诉主管：</label>"; 
		html+="<span id='pb_leaderName'></span>"; 
		html+="</div>"; 
			html+="</div>"; 
				html+="<div class='col'>"; 
				html+="<div class='form'>"; 
				html+="<label class='w'  style='width:130px;'>出勤多余小时数：</label>"; 
		html+="<span id='pb_overAttnTime'></span>"; 
		html+="</div>"; 
			html+="</div>"; 	
				html+="<div class='col'>"; 
				html+="<div class='form'>"; 
				html+="<label class='w' style='width:130px;'>前一天下班时间：</label>"; 
		html+="<span id='pb_yesterDayEndTime'></span>"; 
		html+="</div>"; 
			html+="</div>"; 	
				html+="<div class='col'>"; 
				html+="<div class='form'>"; 
				html+="<label class='w'  style='width:130px;'>考勤日期：</label>"; 
		html+="<span id='pb_date'></span>"; 
		html+="</div>"; 
			html+="</div>"; 	
				html+="<div class='col'>"; 
				html+="<div class='form'>"; 
				html+="<label class='w' style='width:130px;'>考勤时间：</label>"; 
				html+="<span id='attnWorkHoursList'></span>";
		html+="</div>";    	
			html+="</div>"; 
			html+="<div class='col'>"; 
			html+="<div class='form'>"; 
				html+="<label class='w'  style='width:130px;'>消缺勤小时数：</label>"; 
		
		html+="<input type='text' id='workHours' onchange='setRemoveAbsenceHours(this);'>"; 		
		html+="<input type='hidden' id='actualHoursFlag'>"; 
		html+="</div>"; 
			html+="</div>"; 
				html+="<div class='col' style='margin-bottom: 16px;'>"; 
		html+="<div class='form'>"; 
		html+="<label class='w'  style='width:130px;'>缺勤理由：</label>"; 
		html+="<textarea placeholder='限50个字以内' name='removeAbsenceReason' style='width:250px;  height: 80px;' id='removeAbsenceReason'  onkeyup='if(this.value.length>100) this.value=this.value.substr(0,50)' maxlength='50'></textarea>"; 										
		html+="</div>"; 
			html+="</div>"; 
				html+="</form>"; 
					html+="<div style='text-align:center;margin-bottom:3px;'>"; 
					html+="<button class='blue-but btn-middle' id='reconfirmOrSubmit' style='float:none;' ><span id='reconfirmOrSubmitStr'><i class='icon'></i>提交</span></button>";  
					html+="<button class='red-but btn-middle' id='retrunBut' style='float:none;' onclick='closeDiv()'><span><i class='icon'></i>取消</span></button>"; 
					html+="<button class='red-but btn-middle' onclick='cancelAttnDetail()' style='float:none;' id='cancelBut'><span><i class='icon' ></i>撤销</span></button>"; 
					html+="</div>"; 
						html+="</div>"; 
							html+="</div>"; 
								html+="</div>"; 
									html+="</div>"; 
										html+="</div>"; 
	$("#updateDiv").append(html);									
}

function setRemoveAbsenceHours(obj){
	$("#removeAbsenceHours").val($(obj).val());
}

