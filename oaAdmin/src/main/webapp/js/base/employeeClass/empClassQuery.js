
$(function(){
	$("#query").click(function(){
		gotoPage();
		$("#departStr").val($("#firstDepart").find("option:selected").text());
		$("#monthStr").val($("#month").val());
	});
	var currentCompanyId = $("#currentCompanyId").val();
	//初始化部门
	getScheduleDepartList();
	//根据部门获取组别
	$("#firstDepart").change(function(){
		getGroupListByDepartId(this.value);
	});
	//本月
	$("#thisMonth").click(function(){
		$("#month").val(getMonthStartDate('yyyy-MM'));
	});
	//上月
	$("#lastMonth").click(function(){
		$("#month").val(getLastMonthStartDate('yyyy-MM'));
	});
	
	$("#exports").click(function() {
		if($("#firstDepart").val()==""){     
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("部门不能为空！");
			});
			return; 
		}
		if($("#firstDepart").val()==""){
			return;
		}
		var data="month="+$("#month").val()+"&departId="+$("#firstDepart").val()+"&groupId="+$("#groupId").val();
		window.location.href = basePath + "employeeClass/exportClass.htm?"+data;
	});
});
     
function exportClass(obj){
	if($("#firstDepart").val()==""){     
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("部门不能为空！");
		});
		return;
	}
	if($("#firstDepart").val()==""){
		return;
	}
	var data="month="+$("#month").val()+"&departId="+$("#firstDepart").val()+"&groupId="+$("#groupId").val();

	window.location.href = basePath + "employeeClass/exportClass.htm?"+data;
			//"month="+$(obj).attr("month")+"&departId="+$(obj).attr("departId")+"&groupId="+$(obj).attr("groupId");
}

function gotoPage(page){
	$("#reportTitle").hide();
	$("#reportContent").hide();
	$("#showTitle").hide();
	$(".tablebox").hide();
	if($("#firstDepart").val()==""){     
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("部门不能为空！");
		});
		return;
	}
	$(".showTable").show();
	
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	
	if($("#groupId").val()!=null && $("#groupId").val()!=""){
		data="month="+$("#month").val()+"&departId="+$("#firstDepart").val()+"&groupId="+$("#groupId").val() + "&page=" + $("#pageNo").val() + "&rows=10";
	}else{
		data="month="+$("#month").val()+"&departId="+$("#firstDepart").val()+"&page=" + $("#pageNo").val() + "&rows=10";
	}
		
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:data,
		url:contextPath + "/employeeClass/queryClass.htm",
		success:function(response){
			$("#reportList1").empty();
			var html = "";
			for(var i=0;i<response.empClass.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.empClass[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.empClass[i].groupName)+"</td>";
				html += "<td style='text-align:center;'>"+response.empClass[i].classMonth+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.empClass[i].classSettingPerson)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.empClass[i].createTime)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.empClass[i].classEmployeeNum)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.shouldTime)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.empClass[i].updateUser)+"</td>";		
				var createTime=((typeof(response.empClass[i].createTime)=='undefined'||response.empClass[i].createTime==undefined)?'':response.empClass[i].createTime.substring(0,11))

				html += "<td><a style='color:blue;'  onclick='query("+response.empClass[i].id+","+true+","+response.empClass[i].groupId+","+response.empClass[i].departId+",\""+createTime+"\")'>查看</a></td>";
				html += "</tr>";
			}	
			
			$("#reportList2").empty();
			var html1 = "";			
			for(var i=0;i<response.pm.rows.length;i++){
				html1 += "<tr>";
				html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].id)+"</td>";
				html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].departName)+"</td>";
				html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].groupName)+"</td>";
				html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].classEmployeeNum)+"</td>";
				html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].createTime)+"</td>";
				var moveTypeStr="";
				if(Number(response.pm.rows[i].moveType)==0){
					moveTypeStr="调班申请";
				}else if(Number(response.pm.rows[i].moveType)==1){
					moveTypeStr="人事调班";
				}else if(Number(response.pm.rows[i].moveType)==2){
					moveTypeStr="入职申请";
				}
				html1 += "<td style='text-align:center;'>"+moveTypeStr+"</td>";
				html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].classSettingPerson)+"</td>";
				html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].createTime)+"</td>";
				html1 += "<td style='text-align:center;'>"+getValByUndefined(response.pm.rows[i].remark)+"</td>";
				html1 += "<td><a style='color:blue;'  onclick='showDetail("+response.pm.rows[i].id+")'>查看</a></td>";
				html1 += "</tr>";
			}	
			
			
			$("#reportList1").append(html);
			$("#reportList2").append(html1);
			if(response.pm != null && response.pm.pageNo != null) {
				page = response.pm.pageNo;
			}
			initPage("commonPage",response.pm,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
	
}


//用来查看详情
function query(id,booleanflag,groupId,departId,appDate){
	$("#groupIdStr").val(groupId);
	var booleanflag=booleanflag;
	$("#showTitle").show();

	if(booleanflag){
		$("#titleClass").text($("#departStr").val()+"日常工作排班表—"+$("#monthStr").val().substring(0,4)+"年"+$("#monthStr").val().substring(5,7)+"月");
	}else{
		$("#titleClass").text($("#departStr").val()+"日常工作调班表—"+$("#monthStr").val().substring(0,4)+"年"+$("#monthStr").val().substring(5,7)+"月");
	}
	
	
	$(".showTable").hide();
	$("#reportTitle").show();
	$("#reportContent").show();
	$(".tablebox").empty();
	$(".tablebox").show();
	var strId="";
	if(id!=undefined){
		strId="&id="+id;
	}
	
	var data="month="+$("#month").val()+"&departId="+$("#firstDepart").val()+strId+"&isQueryflag="+booleanflag;
	
	
	if(booleanflag==true &&groupId!="" && groupId!=null && groupId!=undefined){
		data="month="+$("#month").val()+"&departId="+departId+"&isQueryflag="+booleanflag+"&groupId="+groupId;
	}
	
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		url:contextPath + "/employeeClass/queryClassInfo.htm?"+data,
		success:function(data){
			$("#reportTitle").find("thead").html("");
			$("#reportTitle").find("tbody").html("");
			$("#reportContent").find("thead").html("");
			$("#reportContent").find("tbody").html("");
			$(".content").find(".download").remove();
			//申请日期
			$("#applyDate").val(appDate);
			//加载标题
			var headTitle = "<tr>";
			var headContent = "<tr>";
			for(var i=0;i<data.weekDays.length;i++){
				if(i<=4){
					headTitle +="<th style='overflow-x:auto;width:100px;text-align:center;'>"+data.weekDays[i]+"</th>";
				}else{
					headContent +="<th style='overflow-x:auto;width:10px;text-align:center;'>"+data.weekDays[i]+"</th>";
				}
			}
			headTitle += "</tr>";
			headContent += "</tr>";
			$("#reportTitle").find("thead").append(headTitle);
			$("#reportContent").find("thead").append(headContent);
			//天
			var daysTitle = "<tr>";
			var daysContent = "<tr>";
			for(var i=0;i<data.days.length;i++){
				if(i<=4){
					daysTitle +="<td style='text-align:center;'>&nbsp;</td>";
				}else{
					daysContent +="<td style='text-align:center;'>"+data.days[i]+"</td>";
				}
			}
			daysTitle += "</tr>";
			daysContent += "</tr>";
			$("#reportTitle").find("tbody").append(daysTitle);
			$("#reportContent").find("tbody").append(daysContent);
			//排班详情
			var m = data.days.length;
			var should_time=0,dayCount=0,must_attn_time=0;
			for(i=0;i<data.classDetail.length;i++){
				parseFloat(should_time+=parseFloat(data.classDetail[i].should_time));
				parseFloat(dayCount+=parseFloat(data.classDetail[i].dayCount));
				parseFloat(must_attn_time+=parseFloat(data.classDetail[i].must_attn_time));
				var classDetail = "<tr class='parentTr' employName="+data.classDetail[i].employ_name+" employId="+data.classDetail[i].employ_id+">";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].positionName+"</td>";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].employ_name+"</td>";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].should_time+"</td>";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].dayCount+"</td>";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].must_attn_time+"</td>";
				var classDetail1 = "<tr>";
				for(var j=1;j<=m-5;j++){
					var compareEntryFlag=CompareTime(data.classDetail[i].firstEntryTime,data.dates[j+4]);
					var compareQuitFlag=CompareTime(data.classDetail[i].quitTime,data.dates[j+4]);
						
					if(typeof(data.classDetail[i][j])=="undefined"){
						if(compareEntryFlag==1){//入职时间大于 当前排班时间
							classDetail1 +="<td></td>";								
						}else{
							if(data.classDetail[i].quitTime!=null && compareQuitFlag==2 && data.classDetail[i].jobStatus==1){//离职时间 小于  当前排班时间
								classDetail1 +="<td></td>";	
							}else{
								classDetail1 +="<td style='text-align:center;cursor:pointer;' date='"+data.dates[j+4]+"' isChangeClass="+booleanflag+" employName="+data.classDetail[i].employ_name+" isMove='notMove'  onclick='showSettingInfo(this)'></td>";
							}
						}
					}else{
						
						if(data.classDetail[i][j].split(",")[3]=="notMove"){
							//白,2019-03-25,1,notMove-->原班次信息，日期，原班次id，没有调班
							//if(data.classDetail[i].firstEntryTime)
							if(compareEntryFlag==1){//入职时间大于 当前排班时间
								classDetail1 +="<td style='text-align:center;cursor:pointer;' isChangeClass="+booleanflag+" employName="+data.classDetail[i].employ_name+" isMove='notMove' date='"+data.dates[j+4]+"' classEmpId="+data.classDetail[i][j].split(",")[4]+" setId="+data.classDetail[i][j].split(",")[2]+" style='text-align:center;'>"+((data.classDetail[i][j].split(",")[0]=='null'||data.classDetail[i][j].split(",")[0]=='')?'':data.classDetail[i][j].split(",")[0])+"</td>";							
							}else{
								if(data.classDetail[i].quitTime!=null && compareQuitFlag==2 && data.classDetail[i].jobStatus==1){//离职时间 小于  当前排班时间
									classDetail1 +="<td style='text-align:center;cursor:pointer;'  isChangeClass="+booleanflag+" employName="+data.classDetail[i].employ_name+" isMove='notMove' date='"+data.dates[j+4]+"' classEmpId="+data.classDetail[i][j].split(",")[4]+" setId="+data.classDetail[i][j].split(",")[2]+" style='text-align:center;'>"+((data.classDetail[i][j].split(",")[0]=='null'||data.classDetail[i][j].split(",")[0]=='')?'':data.classDetail[i][j].split(",")[0])+"</td>";
								}else{
									classDetail1 +="<td style='text-align:center;cursor:pointer;' onclick='showSettingInfo(this);' isChangeClass="+booleanflag+" employName="+data.classDetail[i].employ_name+" isMove='notMove' date='"+data.dates[j+4]+"' classEmpId="+data.classDetail[i][j].split(",")[4]+" setId="+data.classDetail[i][j].split(",")[2]+" style='text-align:center;'>"+((data.classDetail[i][j].split(",")[0]=='null'||data.classDetail[i][j].split(",")[0]=='')?'':data.classDetail[i][j].split(",")[0])+"</td>";
								}
							}
						}else if(data.classDetail[i][j].split(",")[3]=="move"){
							//休,2019-03-27,0,move,早,41-->原班次信息，日期，原班次id，调班,调整后班次信息,调整后班次id
							//休息的班次不存在，默认id=0，特殊处理
							var classId=null;
							if(booleanflag==false){
								classId=data.classDetail[i][j].split(",")[5];
							}
							classDetail1 +="<td style='text-align:center;cursor:pointer;' onclick='showSettingInfo(this);' isChangeClass="+booleanflag+" empId="+data.classDetail[i][j].split(",")[4]+" classId="+classId+" employName="+data.classDetail[i].employ_name+" isMove='move' date='"+data.dates[j+4]+"' setId="+data.classDetail[i][j].split(",")[2]+" newSetId="+data.classDetail[i][j].split(",")[5]+" showName="+data.classDetail[i][j].split(",")[0]+" style='text-align:center;color:blue;'>"+((data.classDetail[i][j].split(",")[0]=='null'||data.classDetail[i][j].split(",")[0]=='')?'':data.classDetail[i][j].split(",")[0])+"</td>";
						}
					}
				}
				classDetail += "</tr>";
				classDetail1 += "</tr>";
				$("#reportTitle").find("tbody").append(classDetail);
				$("#reportContent").find("tbody").append(classDetail1);
			}
			var countsTitle = "<tr>";
			var countsContent = "<tr>";
			countsTitle +="<td colspan='1'>排班统计</td>";
			countsTitle +="<td colspan='1'>"+data.count+"</td>";
			countsTitle +="<td colspan='1'>"+should_time+"</td>";
			countsTitle +="<td colspan='1'>"+dayCount+"</td>";
			countsTitle +="<td colspan='1'>"+must_attn_time+"</td>";
			
			for(var j=1;j<=m-5;j++){
				countsContent +="<td style='text-align:center;'>&nbsp;</td>";
			}
			countsTitle += "</tr>";
			countsContent += "</tr>";
			$("#reportTitle").find("tbody").append(countsTitle);
			$("#reportContent").find("tbody").append(countsContent);
			var content ="";
			if(booleanflag){
				 content = "<button id='export' groupId="+$("#groupId").val()+" month="+$("#month").val()+" departId="+$("#firstDepart").val()+" onclick='exportClass(this);' class='blue-but'><span><i class='icon add'></i>导出</span></button>";
			}
			var returnbox = "<div class='button-wrap ml-4'>" +
					"<button  onclick='returnbox();' class='blue-but'><span><i class='icon'></i>返回</span></button>" +
					content+
			"</div>";
			$(".tablebox").append(returnbox);
		}
	});
}

function returnbox(){
	
	$("#showTitle").hide();
	$(".showTable").show();
	$("#reportTitle").hide();
	$("#reportContent").hide();
	$(".tablebox").hide();
	
}



function moveSingle(name,date){
	$("#updateDiv").show();
	$("#settingInfo").hide();
	$("#classSet").empty();
	$("#move_name").val();
	$("#move_date").val();
	
	$.ajaxSettings.async = false;
	$.getJSON(contextPath +'/employeeClass/getClassSet.htm?groupId='+$("#groupIdStr").val()+"",function(data){
		$.each(data, function(index) {
			var option = "";
			if(index==0){
				option +="<option value="+data[index].id+">"+data[index].name+"</option>";
			}else{
				option +="<option value="+data[index].id+">"+data[index].name+"&nbsp;"+data[index].startTime+"-"+data[index].endTime+"&nbsp;"+data[index].mustAttnTime+"小时</option>";
			}
			$("#classSet").append(option);
		});
	});
	$("#pb_name").html(name);
	if(date!=null){
		$("#pb_date").html(date);
	}
}



function closeCounterDiv(){
	$("#updateDiv").hide();
	$("#reconfirmDiv").hide();
}
function closeInfoDiv(){
	$("#settingInfo").hide();
}

//显示某员工某天班次信息
function showSettingInfo(obj){
	var index = $(obj).closest("tr").index();
	var employId=$("#reportTitle").find("tbody").find("tr:eq("+index+")").attr("employId");
	var text=$(obj).text();
	var isQuery= $(obj).attr("isChangeClass");
	var strPb="排班日期:";
	if(isQuery=='false'){
		strPb="原排班班次";
	}
	if($(obj).attr("setId")!="null"){
		
		var isMove = $(obj).attr("isMove");
		if(isMove=="notMove"){
			var param = {
				date:$(obj).attr("date"),
				setId:$(obj).attr("setId")
			};
		}else{
			if($(obj).attr("classId")==null || $(obj).attr("classId")=='null' ){
				if(employId!=null){
					var param = {
						date:$(obj).attr("date"),
						empId:employId
					};
				}else{
					var param = {
						date:$(obj).attr("date"),
						empId:$(obj).attr("empId")
					};
				}
			}else{
				var param = {
					date:$(obj).attr("date"),
					classId:$(obj).attr("classId"),
					empId:$(obj).attr("empId")
				};
			}
		}
	}else{
		if($(obj).attr("classId")!=null || $(obj).attr("classId")!='null' ){
			var param = {
					date:$(obj).attr("date"),
					empId:$(obj).attr("empId")
				};
		}else{
			var param = {
					date:$(obj).attr("date")
				};
		}
	}
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:param,
		url:contextPath + "/employeeClass/getSettingInfos.htm",
		success:function(data){
			$(".settingInfo").empty();
			$("#changeClassSpan").empty();
			var html="<div class='title'><strong><i class='icon'></i>"+$(obj).attr("employName")+"</strong>";
			html +="<i onclick='closeInfoDiv();' class='mo-houtai-box-close' style='width: 25px;height: 40px;'></i></div>";
			html +="<div class='form-main'><div class='col'><div class='form'><label class='w'  style='width:130px;'>"+strPb+"</label>";
			html +="<span>"+data.date+"</span></div></div><div class='col'><div class='form'>";		
			html +="<label class='w' style='width:130px;'></label><span>"+data.weekDay+"</span></div></div><div class='col'><div class='form'>";

			if(data.setName!='null' && data.setName!=undefined){
				html +="<label class='w' style='width:130px;'>排班班次:</label><span>"+data.setName+"&nbsp;"+data.startTime+"-"+data.endTime+"</span></div></div>";				
			}else{
				html +="<label class='w' style='width:130px;'>排班班次:</label><span>休</span></div></div>";
			}
			isMove = $(obj).attr("isMove");
			if(isMove=="move"){
				html +="<div class='col'><div class='form'><label class='w' style='width:130px;'>调整后班次</label>";
				if(data.newSetName=="null"){
					html +="<span>休</span></div></div>";
				}else{
					html +="<span>"+data.newSetName+"&nbsp;"+data.newStartTime+"-"+data.newEndTime+"</span></div></div>";
				}
				
			}
			
			var employName=$(obj).attr("employName");
			var dateCurrent = $(obj).attr("date");
			var classbox="";
				var date ="";
				if(isMove=="move"){
					date=data.newSetName+"&nbsp;"+data.newStartTime+"-"+data.newEndTime;
				}else if($(obj).attr("setId")=="null" || $(obj).attr("setId")==undefined){
					date='休';
				}else{
					date=data.setName+"&nbsp;"+data.startTime+"-"+data.endTime;
				}
				var dateFomart=data.date+"&nbsp;"+data.weekDay;
				if($(obj).attr("isChangeClass")==true || $(obj).attr("isChangeClass")=='true'){
					classbox="<div class='col'><button id='zcqr' class='red-but' onclick='moveSingle(\""+employName+"\",\""+dateFomart+"\");'  style='width:120px;'>"
					+"<span>调班</span></button></div>";
				}
			
			html +="<div class='col'><div class='form'><div class='button-wrap ml-4 tiaob'>"+classbox+"</div></div></div>";
		   
			$(".settingInfo").append(html);
			$("#settingInfo").show();
			//调班赋值 empid 和 时间
			$("#classDate").val($(obj).attr("date"));
			
			$("#classEmpId").val($(obj).attr("classEmpId"));
			if(employId!=null){
				$("#classEmpId").val(employId);
			}
		}
	});
	
}
function reconfirm(){
	
	$("#updateDiv").hide();
	$("#reconfirmDiv").show();
	
	
	$("#pb_dateAfter").text($("#pb_date").text());
	
	$("#pb_dateBefore").text($("#classSet").find("option:selected").text());
}


function save(){
	var pb_dateAfter=$("#pb_dateAfter").text().substring(0,1);
	var pb_dateBefore=$("#pb_dateBefore").text().substring(0,1);
	if(pb_dateAfter==pb_dateBefore){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("调班信息与原班次一样，请修改后再调班");
		});
		return; 
	}
	
	var setId=getValByUndefined($("#classSet").val());
	var data="date="+$("#classDate").val()+"&employId="+$("#classEmpId").val()+"&setId="+setId+"&remark="+$("#remark").val();
	if(setId==""){
		data="date="+$("#classDate").val()+"&employId="+$("#classEmpId").val()+"&remark="+$("#remark").val();
	}
		
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		url:contextPath + "/employeeClass/hrMoveAtAdmin.htm?"+data,
		success:function(data){
			JEND.load('util.dialog', function() {
				if(data.success){
					JEND.util.dialog.alert(data.message);
					gotoPage();
				}else{
					JEND.page.showError(data.message);
				}
			});
			$("#remark").val("");
			$("#reconfirmDiv").hide();
			return;
		}
	});
}


function CompareTime(date1,date2){
    var oDate1 = new Date(date1);
    var oDate2 = new Date(date2);
    if(oDate1.getTime() > oDate2.getTime()){
    	return 1;
    } else {
    	return 2;
    }
}


//获取排班明细表格
function showDetail(id){
	
	$("#showTitle").show();
	$(".showTable").hide();
	$("#reportTitle").show();
	$("#reportContent").show();
	$(".tablebox").empty();
	$(".tablebox").show();
	
	//查询排班明细生成表格
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{id:id},
		url:contextPath + "/employeeClass/getDetailById.htm",
		success:function(data){
			$("#reportTitle").find("thead").html("");
			$("#reportTitle").find("tbody").html("");
			$("#reportContent").find("thead").html("");
			$("#reportContent").find("tbody").html("");
			$(".content").find(".download").remove();
			//申请日期
			$("#applyDate").val(data.applyDate);
			//状态
			$("#approvalStatus").val(data.approvalStatus);
			//加载标题
			var headTitle = "<tr>"
			var headContent = "<tr>"
			for(var i=0;i<data.weekDays.length;i++){
				if(i<=4){
					headTitle +="<th style='overflow-x:auto;width:100px;text-align:center;'>"+data.weekDays[i]+"</th>";
				}else{
					headContent +="<th style='overflow-x:auto;width:10px;text-align:center;'>"+data.weekDays[i]+"</th>";
				}
			}
			headTitle += "</tr>";
			headContent += "</tr>";
			$("#reportTitle").find("thead").append(headTitle);
			$("#reportContent").find("thead").append(headContent);
			//天
			var daysTitle = "<tr>";
			var daysContent = "<tr>";
			for(var i=0;i<data.days.length;i++){
				if(i<=4){
					daysTitle +="<td style='text-align:center;'>&nbsp;</td>";
				}else{
					daysContent +="<td style='text-align:center;'>"+data.days[i]+"</td>";
				}
			}
			daysTitle += "</tr>";
			daysContent += "</tr>";
			$("#reportTitle").find("tbody").append(daysTitle);
			$("#reportContent").find("tbody").append(daysContent);
			//排班详情
			var m = data.days.length;
			var shuldTimeCount = 0;//应出勤统计
			var employeeCount = 0;//人数统计
			var daysCount = 0;//排班天数统计
			var mustAttnTimeCount  = 0;//排班工时统计
			for(i=0;i<data.classDetail.length;i++){
				
				
				var classDetail = "<tr employName="+data.classDetail[i].employ_name+" employId="+data.classDetail[i].employ_id+">";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].positionName+"</td>";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].employ_name+"</td>";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].should_time+"</td>";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].dayCount+"</td>";
				classDetail +="<td style='text-align:center;'>"+data.classDetail[i].must_attn_time+"</td>";
				var classDetail1 ="<tr>";
				shuldTimeCount = shuldTimeCount + data.classDetail[i].should_time;
				employeeCount = employeeCount +1;
				daysCount = daysCount + data.classDetail[i].dayCount;
				mustAttnTimeCount = mustAttnTimeCount + data.classDetail[i].must_attn_time;
				for(var j=1;j<=m-5;j++){
					if(typeof(data.classDetail[i][j])=="undefined"){
						classDetail1 +="<td style='text-align:center;'></td>";
					}else{
						if(data.classDetail[i][j].split(",")[3]=="notMove"){
							//白,2019-03-25,1,notMove-->原班次信息，日期，原班次id，没有调班
							classDetail1 +="<td onclick='showClassSettingInfo(this);' employName="+data.classDetail[i].employ_name+" isMove='notMove' date="+data.classDetail[i][j].split(",")[1]+" setId="+data.classDetail[i][j].split(",")[2]+" style='text-align:center;cursor:pointer;'>"+data.classDetail[i][j].split(",")[0]+"</td>";
						}else if(data.classDetail[i][j].split(",")[3]=="move"){
							//休,2019-03-27,0,move,早,41-->原班次信息，日期，原班次id，调班,调整后班次信息,调整后班次id
							//休息的班次不存在，默认id=0，特殊处理
							classDetail1 +="<td onclick='showClassSettingInfo(this);' employName="+data.classDetail[i].employ_name+" isMove='move' date="+data.classDetail[i][j].split(",")[1]+" setId="+data.classDetail[i][j].split(",")[2]+" newSetId="+data.classDetail[i][j].split(",")[5]+" style='text-align:center;color:blue;cursor:pointer;'>"+data.classDetail[i][j].split(",")[4]+"</td>";
						}
					}
				}
				classDetail += "</tr>";
				classDetail1 += "</tr>";
				$("#reportTitle").find("tbody").append(classDetail);
				$("#reportContent").find("tbody").append(classDetail1);
			}
			var countsTitle = "<tr>";
			var countsContent = "<tr>";
			countsTitle +="<td>排班统计</td>";
			countsTitle +="<td>"+employeeCount+"</td>";
			countsTitle +="<td>"+shuldTimeCount+"</td>";
			countsTitle +="<td>"+daysCount+"</td>";
			countsTitle +="<td>"+mustAttnTimeCount+"</td>";
			for(var j=1;j<=m-5;j++){
				countsContent +="<td style='text-align:center;'>&nbsp;</td>";
			}
			countsTitle += "</tr>";
			countsContent += "</tr>";
			$("#reportTitle").find("tbody").append(countsTitle);
			$("#reportContent").find("tbody").append(countsContent);
			$(".buttonDiv").empty();
			
			
			var returnbox = "<div class='button-wrap ml-4'>" +
			"<button  onclick='returnbox();' class='blue-but'><span><i class='icon'></i>返回</span></button></div>";
			$(".tablebox").append(returnbox);
		}
	});
}
//显示某员工某天班次信息
function showClassSettingInfo(obj){
	
	var isMove = $(obj).attr("isMove");
	if(isMove=="notMove"){
		var param = {
			date:$(obj).attr("date"),
			setId:$(obj).attr("setId")
		};
	}else{
		var param = {
			date:$(obj).attr("date"),
			setId:$(obj).attr("setId"),
			newSetId:$(obj).attr("newSetId")
		};
	}
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:param,
		url:contextPath + "/employeeClass/getSettingInfo.htm",
		success:function(data){
			$(".settingInfo").empty();
			$("#changeClassSpan").empty();
			var html="<div class='title'><strong><i class='icon'></i>"+$(obj).attr("employName")+"</strong>";
			html +="<i onclick='closeInfoDiv();' class='mo-houtai-box-close' style='width: 25px;height: 30px;'></i></div>";
			html +="<div class='form-main'><div class='col'><div class='form'><label class='w'  style='width:130px;'>排班日期:</label>";
			if(isMove=="move"){
				html +="<span>"+data.date+"</span></div></div><div class='col'><div class='form'>";		
				html +="<label class='w' style='width:130px;'></label><span>"+data.weekDay+"</span></div></div><div class='col'><div class='form'><label class='w' style='width:130px;'>原排班班次:</label>";

			}else{
				html +="<span>"+data.date+"</span></div></div><div class='col'><div class='form'>";		
				html +="<label class='w' style='width:130px;'></label><span>"+data.weekDay+"</span></div></div><div class='col'><div class='form'><label class='w' style='width:130px;'>班次信息:</label>";
			}
			if(data.setName=="休息"){
				html +="<span>"+data.setName+"</span></div></div>";
			}else{
				html +="<span>"+data.setName+"&nbsp;"+data.startTime+"-"+data.endTime+"</span></div></div>";
			}
			if(isMove=="move"){
				html +="<div class='col'><div class='form'><label class='w' style='width:130px;'>调整后班次:</label>";
				if(data.newSetName=="休息"){
					html +="<span>"+data.newSetName+"</span></div></div>";
				}else{
					html +="<span>"+data.newSetName+"&nbsp;"+data.newStartTime+"-"+data.newEndTime+"</span></div></div>";
				}
			}
			html +="<div class='col'><div class='button-wrap ml-4 tiaob'></div></div></div>";
		    $(".settingInfo").append(html);
			$("#settingInfo").show();
		}
	});
	
}

