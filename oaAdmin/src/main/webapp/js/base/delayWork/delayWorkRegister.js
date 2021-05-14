var startFlag=false;
$(function(){
	
	//页面大tab布局
	$("#query").click(function(){
		gotoPage(1);
	});
	
	$(".jui-tabswitch").find("li").each(function(i){
		var obj = $(this);
		if($(obj).attr('id')=="leaderTab"){
			startFlag=false;
			$("#messagePrompt").show();
			gotoPage(1);
		}
		if($(obj).attr('id')=="hrTab"){
			startFlag=true;
		}else{
			startFlag=false;
		}
		$("#queryInfo").show();
	
		
		$("#export").click(function(){
			window.location.href = basePath + "delayWork/exportDelayWorkPage.htm?leaderName="+$("#leaderName").val()+"&month="+$("#month").val()+"&empCode="+$("#code").val()+"&empCnName="+$("#cnName").val()+"&departId="+$("#departId").val();
		})
	});
	
	if($(".jui-tabswitch").find("li").length==2){
		$(".jui-tabswitch").find("li").each(function(i){
			var obj = $(this);
			$(obj).click(function(){
				if(i==0){
					if($(obj).attr('id')=="hrTab"){
						startFlag=true;
					}else{
						startFlag=false;
					}
					$("#messagePrompt").show();
					gotoPage();
				}else if(i==1){
					if($(obj).attr('id')=="hrTab"){
						startFlag=true;
					}else{
						startFlag=false;
					}
					$("#messagePrompt").hide();
					$("#commonPage").empty();
					$("#reportList").find("thead").html("");
					$("#reportList").find("tbody").html("");
					$("#reportListTitle").find("thead").html("");
					$("#reportListTitle").find("tbody").html("");
				}
			})
		});
		startFlag=false;
	}
	
	
	
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
		url:contextPath + "/delayWork/getDelayWorkPage.htm",
		success:function(data){
			
			$("#reportList").find("thead").html("");
			$("#reportList").find("tbody").html("");
			$("#reportListTitle").find("thead").html("");
			$("#reportListTitle").find("tbody").html("");
			//加载标题
			if(data.success){
				var headText = "<tr>";
				var head = "<tr>";
				for(var i=0;i<data.weekDays.length;i++){
					if(i<=1){
						headText +="<th style='overflow-x:auto;text-align:center;'>"+data.weekDays[i]+"</th>";
					}else{
						head +="<th style='overflow-x:auto;text-align:center;'>"+data.weekDays[i]+"</th>";
					}
				}
				head +="<th style='overflow-x:auto;text-align:center;'></th>";
				head +="<th style='overflow-x:auto;text-align:center;'></th>";
				
				//天
				var daysTitle = "<tr>";
				var days = "<tr>";
				for(var i=0;i<data.days.length;i++){
					if(i<=1){
						daysTitle +="<td style='text-align:center;'>"+data.days[i]+"</td>";
					}else{
						if(i==data.days.length-1){
							
						}else{
							days +="<td style='text-align:center;'>"+data.days[i]+"</td>";
						}
					}
					if(i==data.days.length-1 && data.days[i]=="状态" && startFlag==true){
						head +="<th style='overflow-x:auto;text-align:center;'></th>";
						days +="<td style='overflow-x:auto;text-align:center;'>状态</th>";
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
						classDetailTitle+="</tr>"
						var count=0;
						var classDetail="<tr class='parentTr'>";
						for(var j=1;j<=m-2;j++){
							if(typeof(data.page.rows[i][j])=="undefined"){
								if(startFlag){
									if(j==m-2){
										if(data.page.rows[i].status == 1){
											classDetail +="<td style='text-align:center;' ><select style='height:20px;' disabled='disabled' ><option>已复核</option></select></td>";
										}else{
											classDetail +="<td style='text-align:center;' ><select style='height:20px;' onchange='toReview("+data.page.rows[i].empId+",this)'><option></option> <option>已复核</option></select></td>";	
										}
									}else if(j==m-3){
										classDetail +="<td style='text-align:center;'>"+data.page.rows[i].overHoursOfAttendance+"</td>";
									}else if(j==m-4){
										classDetail +="<td style='text-align:center;'>"+data.page.rows[i].count+"</td>";
									}else{
										classDetail +="<td onClick='chlckInfos(\""+data.page.rows[i].empCnName+"\",\""+data.page.rows[i].empCode+"\","+data.page.rows[i].empId+",\""+data.dates[j-1]+"\",2,this)'></td>";
									}
								}else{
									if(j==m-2){
										
									}else
									if(j==m-3){
										classDetail +="<td style='text-align:center;'>"+data.page.rows[i].overHoursOfAttendance+"</td>";
									}else if(j==m-4){
										classDetail +="<td style='text-align:center;'>"+data.page.rows[i].count+"</td>";
									}else{
										classDetail +="<td onClick='chlckInfos(\""+data.page.rows[i].empCnName+"\",\""+data.page.rows[i].empCode+"\","+data.page.rows[i].empId+",\""+data.dates[j-1]+"\",2,this)'></td>";
									}
								}
							}else{
								if(data.page.rows[i][j].fontColor=="blue"){
									classDetail +="<td style='color:blue;' onClick='chlckInfos(\""+data.page.rows[i].empCnName+"\",\""+data.page.rows[i].empCode+"\","+data.page.rows[i][j].employeeId+",\""+data.page.rows[i][j].delayDate+"\","+data.page.rows[i][j].isMatched+",this)'>"+(data.page.rows[i][j].actualDelayHour!=null?data.page.rows[i][j].actualDelayHour:data.page.rows[i][j].expectDelayHour)+"</td>";	
								}else{
									classDetail +="<td onClick='chlckInfos(\""+data.page.rows[i].empCnName+"\",\""+data.page.rows[i].empCode+"\","+data.page.rows[i][j].employeeId+",\""+data.page.rows[i][j].delayDate+"\","+data.page.rows[i][j].isMatched+",this)'>"+(data.page.rows[i][j].actualDelayHour!=null?data.page.rows[i][j].actualDelayHour:data.page.rows[i][j].expectDelayHour)+"</td>";	
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

function toReview(empId,obj){
	var month = $("#month").val()
	var data = "empId=" + empId + "&month=" +month;
	$.ajax({
		async:false,
		type:'post',
		data:data,
		dataType:'json',
		url:contextPath + "/delayWork/toReview.htm",
		success:function(data){
			if(data.success){
				JEND.page.alert({type:"success", message:data.message});
				obj.disabled="disabled";
			}else{
				$(obj).val('');
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
function deleteDelayWork(){
	secondConfirmation({
	   "msg":"确认要删除？",
		sureFn:function(){
			deleteDelayWorkDetail();
		}
	});
}

function confirmDelayWorkDetail(obj,type,hoursText){
	if(typeof($("#startDate").val())=="undefined" && $("#startDate").val()=="" && typeof($("#endDate").val())=="undefined" && $("#endDate").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请登记延时工作时间后提交");
		});
	}else{
		pageLoading(true);//开启动画
		if(type==0){
			changeHours();
		}else{
			//manualchangeHours();
		}
		var hours=$("#workHours").val();
		var startTime=$("#startTime").val()+" "+$("#startDate").val()+":00";
		var endTime=$("#endTime").val()+" "+$("#endDate").val()+":00";
		
		if(type==0){//传预计工时到后台
			var data =$("#updateForm").serialize()+"&startTime="+startTime+"&endTime="+endTime+"&expectDelayHour="+$("#workHours").val();
		}else{ //传 实际工时到后台
			var data =$("#updateForm").serialize()+"&startTime="+startTime+"&endTime="+endTime+"&actualDelayHour="+$("#workHours").val();
		}
		$.ajax({
			async:true,
			type:'post',
			data:data,
			dataType:'json',
			url:contextPath + "/delayWork/confirmDelayWorkDetail.htm",
			success:function(data){
				if(data.success){
					JEND.page.alert({type:"success", message:data.message});
					if(type==1){
						$("#reportList").find("tr:eq("+($(obj).closest("tr").index()+1)+")").find("td:eq("+$(obj).index()+")").css("color","#666");
					}
					$("#reportList").find("tr:eq("+($(obj).closest("tr").index()+1)+")").find("td:eq("+$(obj).index()+")").text(hours);
					
					var tdCount=$("#reportList").find("tr:eq(1) td").size(); 
					if(startFlag){//人事登记
						var hoursNum=$("#reportList").find("tr:eq("+($(obj).closest("tr").index()+1)+")").find("td:eq("+(tdCount-2)+")").text();
						var totalDay=$("#reportList").find("tr:eq("+($(obj).closest("tr").index()+1)+")").find("td:eq("+(tdCount-3)+")").text();
						$("#reportList").find("tr:eq("+($(obj).closest("tr").index()+1)+")").find("td:eq("+(tdCount-2)+")").text((parseFloat(hoursNum)-parseFloat(hoursText)+(parseFloat(hours))));//出勤多余小时数
						$("#reportList").find("tr:eq("+($(obj).closest("tr").index()+1)+")").find("td:eq("+(tdCount-3)+")").text((parseFloat(hoursNum)-parseFloat(hoursText)+(parseFloat(hours))));//合计
					}else{//主管登记
						var hoursNum=$("#reportList").find("tr:eq("+($(obj).closest("tr").index()+1)+")").find("td:eq("+(tdCount-1)+")").text();
						var totalDay=$("#reportList").find("tr:eq("+($(obj).closest("tr").index()+1)+")").find("td:eq("+(tdCount-2)+")").text();
						$("#reportList").find("tr:eq("+($(obj).closest("tr").index()+1)+")").find("td:eq("+(tdCount-1)+")").text((parseFloat(hoursNum)-parseFloat(hoursText)+(parseFloat(hours))));//出勤多余小时数
						$("#reportList").find("tr:eq("+($(obj).closest("tr").index()+1)+")").find("td:eq("+(tdCount-2)+")").text((parseFloat(hoursNum)-parseFloat(hoursText)+(parseFloat(hours))));//合计
					}
					closeDiv();
					gotoPage($("#pageNo").val());
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
function commitDelayWorkDetail(obj,type){
	if(typeof($("#startDate").val())=="undefined" && $("#startDate").val()=="" && typeof($("#endDate").val())=="undefined" && $("#endDate").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请登记延时工作时间后提交");
		});
	}else{
		pageLoading(true);//开启动画
		if(type==0){
			changeHours();
		}else{
			//manualchangeHours();
		}
		var hours=$("#workHours").val();
		var startTime=$("#startTime").val()+" "+$("#startDate").val()+":00";
		var endTime=$("#endTime").val()+" "+$("#endDate").val()+":00";
		if(type==0){//传预计工时到后台
			var data =$("#updateForm").serialize()+"&startTime="+startTime+"&endTime="+endTime+"&expectDelayHour="+$("#workHours").val();
		}else{ //传 实际工时到后台
			var data =$("#updateForm").serialize()+"&startTime="+startTime+"&endTime="+endTime+"&actualDelayHour="+$("#workHours").val();
		}
		$.ajax({
			async:true,
			type:'post',
			data:data,
			dataType:'json',
			url:contextPath + "/delayWork/commitDelayWorkDetail.htm",
			success:function(data){
				if(data.success){
					JEND.page.alert({type:"success", message:"提交成功！"});
					$("#reportList").find("tr:eq("+($(obj).closest("tr").index()+1)+")").find("td:eq("+$(obj).index()+")").text(hours);
					closeDiv();
					gotoPage($("#pageNo").val());
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
function deleteDelayWorkDetail(obj){
	var hiddenTrIndex = $("#hiddenTrIndex").val();
	var hiddenTdIndex = $("#hiddenTdIndex").val();
	pageLoading(true);//开启动画
	var data =$("#updateForm").serialize();
	
	$.ajax({
		async:true,
		type:'post',
		data:data,
		dataType:'json',
		url:contextPath + "/delayWork/deleteDelayWorkDetail.htm",
		success:function(data){
			if(data.success){
				JEND.page.alert({type:"success", message:data.message});
				$("#reportList").find("tr:eq("+hiddenTrIndex+")").find("td:eq("+hiddenTdIndex+")").text("");
				var updateType=$("#updateType").val();
				if(updateType==3 || updateType==4){//确认时间 删除 更改页面合计、多余小时数
					var tdCount=$("#reportList").find("tr:eq(1) td").size(); 
					var hours=$("#workHours").val();
					if(startFlag){//人事登记
						var hoursNum=$("#reportList").find("tr:eq("+(parseFloat(hiddenTrIndex))+")").find("td:eq("+(tdCount-2)+")").text();
						var totalDay=$("#reportList").find("tr:eq("+(parseFloat(hiddenTrIndex))+")").find("td:eq("+(tdCount-3)+")").text();
						$("#reportList").find("tr:eq("+(parseFloat(hiddenTrIndex))+")").find("td:eq("+(tdCount-2)+")").text((parseFloat(hoursNum)-(parseFloat(hours))));//出勤多余小时数
						$("#reportList").find("tr:eq("+(parseFloat(hiddenTrIndex))+")").find("td:eq("+(tdCount-3)+")").text((parseFloat(totalDay)-(parseFloat(hours))));//合计
					}else{//主管登记
						var hoursNum=$("#reportList").find("tr:eq("+(parseFloat(hiddenTrIndex))+")").find("td:eq("+(tdCount-1)+")").text();
						var totalDay=$("#reportList").find("tr:eq("+(parseFloat(hiddenTrIndex))+")").find("td:eq("+(tdCount-2)+")").text();
						$("#reportList").find("tr:eq("+(parseFloat(hiddenTrIndex))+")").find("td:eq("+(tdCount-1)+")").text((parseFloat(hoursNum)-(parseFloat(hours))));//出勤多余小时数
						$("#reportList").find("tr:eq("+(parseFloat(hiddenTrIndex))+")").find("td:eq("+(tdCount-2)+")").text((parseFloat(totalDay)-(parseFloat(hours))));//合计
					}
				}
				closeDiv();
				gotoPage($("#pageNo").val());
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


function chlckInfos(cnName,cnCode,empId,delayDate,num,obj){
	$("#updateType").val("");
	$("#hiddenTrIndex").val(($(obj).closest("tr").index()+1));
	$("#hiddenTdIndex").val($(obj).index());
	
	var Weekindex=$("#reportList").find("th:eq("+$(obj).index()+")").text();
	$("#unWorkDay").val(0);//标记是否为工作日 0是工作日 1是非工作日

	$("#delayItem").val("");
	$("#workHours").val("");
	$("#startDate").val("");
	$("#endDate").val("");
	
	$("#showTitleDiv").hide();
	
	$("#weekStr").text(Weekindex);
	$("#pb_code").text(cnCode);
	$("#pb_name").text(cnName);
	$("#delayDate").val(delayDate);
	var delayDateStr=delayDate.substring(0,4)+"年"+delayDate.substring(5,7)+"月"+delayDate.substring(8,10)+"日";	
	$("#pb_date").text(delayDateStr);
	
		var data ="empId="+empId+"&delayDate="+delayDate;
		$.ajax({
			async:false,
			type:'post',
			data:data,
			dataType:'json',
			url:contextPath + "/delayWork/getDelayWorkDetail.htm",
			success:function(data){
				$("#reconfirmOrSubmit").show();
				$("#startDate").prop("disabled", false);
				$("#endDate").prop("disabled", false);
				$('#workHours').attr("readonly","readonly")//去除input元素的readonly属性
				//动态赋值按钮
				$("#reconfirmOrSubmit").attr('onclick','').unbind('click');
				$("#employeeId").val(empId);
				if(data.success){
					$("#id").val(data.delayWorkRegister.id);
					$("#isConfirm").val(data.delayWorkRegister.isConfirm);
					$("#updateType").val(data.delayWorkRegister.updateType);
					switch(data.delayWorkRegister.updateType){
					     case 1:
					    	$("#titleStr").text("延时工作小时数登记"); 
					 		$("#reconfirmOrSubmitStr").text("提交");
					        if(data.delayWorkRegister.isWorkDay==0){
					        	$("#startDate").prop("disabled", true);
					        	$("#startDate").val((data.delayWorkRegister.expectStartTime==null)?"":data.delayWorkRegister.expectStartTime.substring(11,16));
					        }else{
					        	$("#unWorkDay").val(1);
					        }
					        $("#startDate").val((data.delayWorkRegister.expectStartTime==null)?"":data.delayWorkRegister.expectStartTime.substring(11,16));
					    	$("#endDate").val((data.delayWorkRegister.expectEndTime==null)?"":data.delayWorkRegister.expectEndTime.substring(11,16));
					    	$("#workHours").val(data.delayWorkRegister.expectDelayHour);
					    	$("#delayItem").prop("disabled", false);
					        $("#reconfirmOrSubmit").bind("click",function(){
					        	if($("#workHours").val()<1){
					    			JEND.load('util.dialog', function() {
										JEND.util.dialog.alert("延时工作时长不能小于1");
									});
					    		}else{
						    		secondConfirmation({
							    	   "msg":"是否要提交？",
							    		sureFn:function(){
							    			commitDelayWorkDetail(obj,0);
							    		}
							    	});
								}
							});
					        $("#delBut").hide();
					        break;
					     case 2:
					    	$("#titleStr").text("延时工作小时数登记"); 
					 		$("#reconfirmOrSubmitStr").text("提交"); 
					    	if(data.delayWorkRegister.isWorkDay==0){
						        $("#startDate").prop("disabled", true);
						        $("#startDate").val((data.delayWorkRegister.expectStartTime==null)?"":data.delayWorkRegister.expectStartTime.substring(11,16));
						    }else{
					        	$("#unWorkDay").val(1);
					        }
					    	$("#startDate").val((data.delayWorkRegister.expectStartTime==null)?"":data.delayWorkRegister.expectStartTime.substring(11,16));
						    $("#endDate").val((data.delayWorkRegister.expectEndTime==null)?"":data.delayWorkRegister.expectEndTime.substring(11,16));
						    $("#workHours").val(data.delayWorkRegister.expectDelayHour);
						    $("#delayItem").prop("disabled", false);
					    	$("#reconfirmOrSubmit").bind("click",function(){
					    		if($("#workHours").val()<1){
					    			JEND.load('util.dialog', function() {
										JEND.util.dialog.alert("延时工作时长不能小于1");
									});
					    		}else{
						    		secondConfirmation({
							    	   "msg":"是否要提交？",
							    		sureFn:function(){
							    			commitDelayWorkDetail(obj,0);
							    		}
							    	});
								}
							});
					    	$("#delBut").show();
					    	break;
					     case 3:
					    	$("#titleStr").text("延时工作小时数确认");  
					    	$("#reconfirmOrSubmitStr").text("确认时间"); 
					    	$("#startDate").val((data.delayWorkRegister.actualStartTime==null)?"":data.delayWorkRegister.actualStartTime.substring(11,16));
					    	$("#endDate").val((data.delayWorkRegister.actualEndTime==null)?"":data.delayWorkRegister.actualEndTime.substring(11,16));
					    	$("#startDate").prop("disabled", true);
					    	$("#endDate").prop("disabled", true);
					    	$("#delayItem").prop("disabled", true);
					    	$("#workHours").val(data.delayWorkRegister.actualDelayHour);
					    	$("#actualHoursFlag").val(data.delayWorkRegister.actualDelayHour);//新增一个变量 用于比较实际小时数
					    	$("#workHours").removeAttr("readonly");//去除input元素的readonly属性				    	
					    	$("#workHours").val(data.delayWorkRegister.actualDelayHour);
					    	var hoursText=data.delayWorkRegister.actualDelayHour;
					    	$("#reconfirmOrSubmit").bind("click",function(){
					    		secondConfirmation({
						    	   "msg":"是否要修改小时数？",
						    		sureFn:function(){
						    			confirmDelayWorkDetail(obj,1,hoursText);
						    		}
						    	});
							});
					    	$("#delBut").show();
					    	break;
					     case 4:
					    	$("#titleStr").text("延时工作小时数确认");   
					    	$("#reconfirmOrSubmitStr").text("确认时间");  
					    	$("#startDate").val((data.delayWorkRegister.actualStartTime==null)?"":data.delayWorkRegister.actualStartTime.substring(11,16));
					    	$("#endDate").val((data.delayWorkRegister.actualEndTime==null)?"":data.delayWorkRegister.actualEndTime.substring(11,16));
					    	$("#workHours").val(data.delayWorkRegister.actualDelayHour);
					    	var hoursText=data.delayWorkRegister.actualDelayHour;
					    	$("#actualHoursFlag").val(data.delayWorkRegister.actualDelayHour);//新增一个变量 用于比较实际小时数
					    	$("#startDate").prop("disabled", true);
					    	$("#endDate").prop("disabled", true);	
					    	$("#delayItem").prop("disabled", true);
					    	$("#workHours").removeAttr("readonly");//去除input元素的readonly属性	
					    	$("#reconfirmOrSubmit").bind("click",function(){
					    		secondConfirmation({
						    	   "msg":"是否要修改小时数？",
						    		sureFn:function(){
						    			confirmDelayWorkDetail(obj,1,hoursText);
						    		}
						    	});
							});
					    	$("#delBut").show();
					    	break;
					     case 5:
					    	$("#titleStr").text("延时工作小时数确认");   
					    	$("#startDate").val((data.delayWorkRegister.actualStartTime==null)?"":data.delayWorkRegister.actualStartTime.substring(11,16));
					    	$("#endDate").val((data.delayWorkRegister.actualEndTime==null)?"":data.delayWorkRegister.actualEndTime.substring(11,16));
					    	$("#startDate").prop("disabled", true);
					    	$("#endDate").prop("disabled", true);
					    	$("#delayItem").prop("disabled", true);
					    	$("#workHours").val(data.delayWorkRegister.actualDelayHour);
					        $("#reconfirmOrSubmit").hide();
					        $("#delBut").hide(); 
					        break;
					} 
			    	
					$("#delayItem").val((data.delayWorkRegister.delayItem==null)?"":data.delayWorkRegister.delayItem);
					$("#updateDiv").show();
					var leftW = 100*(1-$("#updateDivCen").width()/$("body").width())/2;
					$("#updateDivCen").css("left",leftW+"%");  
					var height= $(window).height()>=430?430:$(window).height()-10;
					$("#cun-pop-content").css("height",height+"px");  
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
function closeShowTitleDiv(){
	$("#showTitleDiv").hide();
}
function changeHours(){
	var hours=0;
	if(typeof($("#startDate").val())!="undefined" && $("#startDate").val()!="" && typeof($("#endDate").val())!="undefined" && $("#endDate").val()!=""){
		var startTime=$("#startDate").val().substring(0,2);
		var endTime=$("#endDate").val().substring(0,2);
		var startHours=0;
		if(parseInt($("#startDate").val().substring(3,5)) > 30) {
			startTime= parseInt($("#startDate").val().substring(0,2)) + 1; //时间小时数加一天
			startHours=0;
		} else if(parseInt($("#startDate").val().substring(3,5)) > 0) {
			startHours=0.5;
		}
		var endHours=($("#endDate").val().substring(3,5)>=30)?0.5:0;
		
		$("#startTime").val($("#delayDate").val());
		if($("#startDate").val()>$("#endDate").val()){
			hours=24-startTime-startHours;
			hours=parseFloat(hours)+parseFloat(endTime)+parseFloat(endHours);
			var endTime=$("#delayDate").val();
			dateTime=new Date(endTime);
			dateTime=dateTime.setDate(dateTime.getDate()+1);
			dateTime=new Date(dateTime).format("yyyy-MM-dd"); 
			$("#endTime").val(dateTime);
		}else{
			hours=(parseFloat(endTime)+parseFloat(endHours))-(parseFloat(startTime)+parseFloat(startHours));			
			$("#endTime").val($("#delayDate").val());
		}
		//星期六天满5-1 满10-2
		var unWorkDay=$("#unWorkDay").val();
		if(unWorkDay==1){ //如果为非工作日
			if(hours>=10){
				hours=hours-2;
			}else if(hours>=5){
				hours=hours-1;
			}
		}
		
		$("#workHours").val(hours);
		return hours;
	}else{
		return hours;
	}
}