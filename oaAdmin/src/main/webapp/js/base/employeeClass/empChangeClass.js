//全局变量 map
var map = {};
$(function(){
	signExcel = new SignExcel();  
	signExcel.init();
	$("#query").click(function(){
		if($("#isMenu").val()=="true"){
			gotoPage();
		}else{
			gotoPage1();
		}
	});
	//本月
	$("#thisMonth").click(function(){
		$("#month").val(getMonthStartDate('yyyy-MM'));
	});
	//上月
	$("#lastMonth").click(function(){
		$("#month").val(getLastMonthStartDate('yyyy-MM'));
	});
	
	if($("#isScheduler").val()=="false"){
		//初始化部门
		getScheduleDepartList();
	}
	//根据部门获取组别
	$("#firstDepart").change(function(){
		getGroupListByDepartId(this.value);
	});

	//页面大tab布局
	$(".jui-tabswitch").find("li").each(function(i){
		var obj = $(this);
		$(obj).click(function(){
			$(".workType").val("");
			$(".applyStartDate").val("");
			$(".applyEndDate").val("");
			$(".firstDepart").val("");
			$(".approvalStatus").val("");
			
			$("input[name='code']").val("");
			$("input[name='cnName']").val("");
			$("input[name='applyName']").val("");
			$("input[name='agentName']").val("");
			if(i==0){
				$(".first").show();
				$("#commonPage").show();
				$(".second").hide();
				$("#commonPage1").hide();
			}else if(i==1){
				$(".first").show();
				$("#commonPage").show();
				$(".second").hide();
				$("#commonPage1").hide();
				gotoPage();
			}else if(i==2){
				$(".second").show();
				$("#reportList").find("thead").html("");
				$("#reportList").find("tbody").html("");
				$("#reportListTitle").find("thead").html("");
				$("#reportListTitle").find("tbody").html("");
				$(".tablebox").empty();
				
				$("#commonPage").hide();
				
				$("#commonPage1").show();				
				$("#flagTab").val(1);
				gotoPage1(1);
			}
		})
	});
	//下载模板
	$("#downloadScheduleTemplate").click(function(){
		var departId = $("#firstDepart").val();
		var groupId = $("#groupId").val();
		var month = $("#month").val();
		if($("#month").val()==""){
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("请选择调班月份！");
			});
			return;
		}
		if($("#firstDepart").val()==""){
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("请选择部门！");
			});
			return;
		}
		if($("#groupId").val()==""){
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("请选择调班组别！");
			});
			return;
		}
		exportScheduleTemplate(month,departId,groupId);
			
		
	});
});


//处理map集合 新增集合 键=员工_时间
function setMap(param){
    var key=param.empId+"_"+param.date;
    map[key]=param.classId;
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

function reconfirm(tdId,beforeClass,beforeClassId,mustAttnTime,employeId){
	var classSetName=$("#classSet").find("option:selected").text();
	var pb_dateAfter=beforeClass.substring(0,1);
	var pb_dateBefore=classSetName.substring(0,1);
	if(pb_dateAfter==pb_dateBefore){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("调班班次与原班次一致，请重新调班");
			return; 
		});
	}else{
		$("#"+tdId).text(pb_dateBefore);
		$("#"+tdId).css("color","blue");
	}
	var classSetId=$("#classSet").val();
	//调完几次班 跟原班次相同时 去除颜色 map去除键
	if(""+beforeClassId+""==classSetId){
		$("#"+tdId).text(pb_dateBefore);
		$("#"+tdId).css("color","");
		var key=$("#classEmpId").val()+"_"+$("#emp_date").val();
		if(isHaving(key)){
			deleteMap(key);
		}
	}else{
		var param = {
			"empId":$("#classEmpId").val(),
			"date":$("#emp_date").val(),
			"classId":$("#classSet").val()
		};
		setMap(param);
	}
	//计算总天数
	var countDayBefor;
	var countDayAfter;
	//计算排班工时
	var classNum=classSetName.substring((classSetName.length-4),(classSetName.length-2));
	
	beforeClassSetId=$("#"+tdId).attr("beforeClassId");
	
	if(beforeClassSetId==undefined || typeof(beforeClassSetId) == "undefined" ||beforeClassSetId==0){
		countDayBefor=0;
	}else{
		countDayBefor=1;
	}
	if(isNaN(classNum)||classNum==""){//判断 上面如果截取为空则是休息 计算天数为0 工时为0
		classNum=0;
		countDayAfter=0;
	}else{
		countDayAfter=1;
	}
	var flagnum=parseFloat(classNum)-parseFloat(mustAttnTime);
	var flagDay=parseFloat(countDayAfter)-parseFloat(countDayBefor);
	var tdHours=$("#"+employeId+"mustAttnTime").text();
	$("#"+employeId+"mustAttnTime").text(parseFloat(tdHours)+parseFloat(flagnum));
	$("#"+tdId).attr("mustAttnTime",classNum);
	var tdDays=$("#"+employeId+"dayCount").text();
	$("#"+employeId+"dayCount").text(parseFloat(tdDays)+parseFloat(flagDay));
	$("#"+tdId).attr("beforeClassId",countDayAfter);
	$("#dayCountReckon").text(parseFloat($("#dayCountReckon").text())+parseFloat(flagDay));
	$("#mustAttnTimeReckon").text(parseFloat($("#mustAttnTimeReckon").text())+parseFloat(flagnum));
	
	$("#updateDiv").hide();
}

function saveDiv(){
	$("#reconfirmDiv").show();
}
function save(){
	
	var shouldAttnTime = [];// 应出勤工时总和
	var mustAttnTime = [];// 调班工时总和
	
    $('.shouldAttnTime').each(function () {
    	shouldAttnTime.push($(this).html());
    });
    $('.mustAttnTime').each(function () {
    	mustAttnTime.push($(this).html());
    });
    //判断数组中 调班工时是否小于 应出勤工时
    for(var i=0;i<shouldAttnTime.length;i++){
        if(parseFloat(shouldAttnTime[i]) > parseFloat(mustAttnTime[i]) ){
        	JEND.load('util.dialog', function() {
    			JEND.util.dialog.alert("排班表中有员工应出勤工时未排满，请确认排满后提交！");
    			$("#reconfirmDiv").hide();
    			 return ;
    		});
            return ;
        }
    }
	
	if(Object.getOwnPropertyNames(map).length<1){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("未有调班，请调班后在提交!");
			$("#reconfirmDiv").hide();
		});
	}else{
		pageLoading(true);//加载动画
		var data="month="+$("#monthSave").val()+"&departId="+$("#firstDepartSave").val()+"&groupId="+$("#groupIdSave").val()+ "&map=" +JSON.stringify(map);	
		$.ajax({
			async:true,
			type:'post',
			dataType:'json',
			data:data,
			url:contextPath + "/employeeClass/changeClassSet.htm",
			success:function(data){  
				JEND.load('util.dialog', function() {
					if(data.success){
						gotoPage();
						JEND.util.dialog.alert(data.message);
					}else{
						JEND.page.showError(data.message);
					}
				});
				$("#remark").val("");
			},
			complete:function(XMLHttpRequest,textStatus){  
				if(XMLHttpRequest.status=="403"){
					JEND.page.alert({type:"error", message:"您没有该操作权限！"});
				}
				pageLoading(false);//关闭动画
	        }
		});
		$("#reconfirmDiv").hide();
	}
}


function gotoPage(){
	$("#isMenu").val("true");
	$("#classStatus").text("");
	if($("#firstDepart").val()==""){     
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("部门不能为空！");
		});
		return;
	}
	if($("#month").val()==""){     
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择月份！");
		});
		return;
	}
	if($("#groupId").val()==""){     
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择组别！");
		});
		return;
	}
	if($("#groupId").val()=="" || $("#month").val()==""||$("#firstDepart").val()==""){   
		return;
	}
	pageLoading(true);//加载动画
	$(".tablebox").empty();
	var data="month="+$("#month").val()+"&departId="+$("#firstDepart").val();
	
	var groupId=$("#groupId").val();
	if(groupId!="" && groupId!=null && groupId!=undefined){
		data="month="+$("#month").val()+"&departId="+$("#firstDepart").val()+"&groupId="+groupId;
	}
	
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		url:contextPath + "/employeeClass/queryChangeClassInfo.htm?"+data,
		success:function(data){
			$("#reportListTitle").find("thead").html("");
			$("#reportListTitle").find("tbody").html("");
			$("#reportList").find("thead").html("");
			$("#reportList").find("tbody").html("");
			$(".content").find(".download").remove();
			//申请日期
			$("#applyDate").val("");
			//加载标题
			var headTitle = "<tr>";
			var head = "<tr>";
			for(var i=0;i<data.weekDays.length;i++){
				if(i<=4){
					headTitle +="<th style='overflow-x:auto;width:100px;text-align:center; height: 19px;'>"+data.weekDays[i]+"</th>";
				}else{
					head +="<th style='overflow-x:auto;width:10px;text-align:center;'>"+data.weekDays[i]+"</th>";
				}
			}
			headTitle += "</tr>";
			head += "</tr>";
			$("#reportListTitle").find("thead").append(headTitle);
			$("#reportList").find("thead").append(head);
			//天
			var daysTitle = "<tr>";
			var days = "<tr>";
			for(var i=0;i<data.days.length;i++){
				if(i<=4){
					daysTitle +="<td style='text-align:center; height: 19px;'>"+data.days[i]+"</td>";
				}else{
					days +="<td style='text-align:center;'>"+data.days[i]+"</td>";
				}
			}
			daysTitle += "</tr>";
			days += "</tr>";
			$("#reportListTitle").find("tbody").append(daysTitle);
			$("#reportList").find("tbody").append(days);
			//排班详情
			var m = data.days.length;
			var should_time=0,dayCount=0,must_attn_time=0;
			for(i=0;i<data.classDetail.length;i++){
				parseFloat(should_time+=parseFloat(data.classDetail[i].should_time));
				parseFloat(dayCount+=parseFloat(data.classDetail[i].dayCount));
				parseFloat(must_attn_time+=parseFloat(data.classDetail[i].must_attn_time));
				var classDetailTitle = "<tr class='parentTr' employName="+data.classDetail[i].employ_name+" employId="+data.classDetail[i].employ_id+">";
				classDetailTitle +="<td style='text-align:center;'>"+data.classDetail[i].positionName+"</td>";
				classDetailTitle +="<td style='text-align:center;'>"+data.classDetail[i].employ_name+"</td>";
				classDetailTitle +="<td style='text-align:center;' id='"+data.classDetail[i].employ_id+"shouldAttnTime' class='shouldAttnTime' >"+data.classDetail[i].should_time+"</td>";
				classDetailTitle +="<td style='text-align:center;' id='"+data.classDetail[i].employ_id+"dayCount' class='dayCount' >"+data.classDetail[i].dayCount+"</td>";
				classDetailTitle +="<td style='text-align:center;' id='"+data.classDetail[i].employ_id+"mustAttnTime' class='mustAttnTime' >"+data.classDetail[i].must_attn_time+"</td>";
				classDetailTitle +="</tr>";
				var classDetail = "<tr class='parentTr' employName="+data.classDetail[i].employ_name+" employId="+data.classDetail[i].employ_id+">";
				for(var j=1;j<=m-5;j++){
					
					var nowDateFlag=CompareTime(data.nowDate,data.dates[j+4]);
					
					var compareEntryFlag=CompareTime(data.classDetail[i].firstEntryTime,data.dates[j+4]);
					var compareQuitFlag=CompareTime(data.classDetail[i].quitTime,data.dates[j+4]);
					if(typeof(data.classDetail[i][j])=="undefined"){
						
						if(nowDateFlag==1){
							classDetail +="<td id="+i+j+" onclick='showErrorInfo(1)'></td>";	
						}else if(compareEntryFlag==1){//入职时间大于 当前排班时间
							classDetail +="<td id="+i+j+" onclick='showErrorInfo(2)'></td>";								
						}else{
							if(data.classDetail[i].quitTime!=null && compareQuitFlag==2 &&data.classDetail[i].jobStatus==1){//离职时间 小于  当前排班时间
								classDetail +="<td id="+i+j+" onclick='showErrorInfo(3)'></td>";	
							}else if(data.classDetail[i].quitTime!=null && compareQuitFlag==2 &&data.classDetail[i].jobStatus==2){//离职时间 小于  当前排班时间
								classDetail +="<td id="+i+j+" onclick='showErrorInfo(4)'></td>";	
							}else{
								classDetail +="<td style='text-align:center;cursor:pointer;' onclick='showSettingInfo(this)' id="+i+j+" date='"+data.dates[j+4]+"'  employName="+data.classDetail[i].employ_name+" isMove='notMove' classEmpId="+data.classDetail[i].employ_id+" ></td>";
							}
						}	
					}else{
						//白,2019-03-25,1,notMove-->原班次信息，日期，原班次id，没有调班
						//if(data.classDetail[i].firstEntryTime)
						if(nowDateFlag==1){
							classDetail +="<td  onclick='showErrorInfo(1)' style='text-align:center;cursor:pointer;' id="+i+j+"  mustAttnTime="+data.classDetail[i][j].split(",")[5]+"  employName="+data.classDetail[i].employ_name+" isMove='notMove' date='"+data.dates[j+4]+"' classEmpId="+data.classDetail[i][j].split(",")[4]+" setId="+data.classDetail[i][j].split(",")[2]+" beforeClassId="+data.classDetail[i][j].split(",")[2]+" style='text-align:center;'>"+((data.classDetail[i][j].split(",")[0]=='null'||data.classDetail[i][j].split(",")[0]=='')?'':data.classDetail[i][j].split(",")[0])+"</td>";			
						}else if(compareEntryFlag==1){//入职时间大于 当前排班时间
							classDetail +="<td  onclick='showErrorInfo(2)' style='text-align:center;cursor:pointer;' id="+i+j+"  mustAttnTime="+data.classDetail[i][j].split(",")[5]+"  employName="+data.classDetail[i].employ_name+" isMove='notMove' date='"+data.dates[j+4]+"' classEmpId="+data.classDetail[i][j].split(",")[4]+" setId="+data.classDetail[i][j].split(",")[2]+" beforeClassId="+data.classDetail[i][j].split(",")[2]+" style='text-align:center;'>"+((data.classDetail[i][j].split(",")[0]=='null'||data.classDetail[i][j].split(",")[0]=='')?'':data.classDetail[i][j].split(",")[0])+"</td>";								
						}else{
							if(data.classDetail[i].quitTime!=null && compareQuitFlag==2 &&data.classDetail[i].jobStatus==1){//离职时间 小于  当前排班时间
								classDetail +="<td onclick='showErrorInfo(3)'  style='text-align:center;cursor:pointer;' id="+i+j+"  mustAttnTime="+data.classDetail[i][j].split(",")[5]+"  employName="+data.classDetail[i].employ_name+" isMove='notMove' date='"+data.dates[j+4]+"' classEmpId="+data.classDetail[i][j].split(",")[4]+" setId="+data.classDetail[i][j].split(",")[2]+" beforeClassId="+data.classDetail[i][j].split(",")[2]+" style='text-align:center;'>"+((data.classDetail[i][j].split(",")[0]=='null'||data.classDetail[i][j].split(",")[0]=='')?'':data.classDetail[i][j].split(",")[0])+"</td>";			
							}else if(data.classDetail[i].quitTime!=null && compareQuitFlag==2 &&data.classDetail[i].jobStatus==2){//离职时间 小于  当前排班时间
								classDetail +="<td id="+i+j+" onclick='showErrorInfo(4)'></td>";	
							}else{
								classDetail +="<td onclick='showSettingInfo(this)' style='text-align:center;cursor:pointer;' id="+i+j+"  mustAttnTime="+data.classDetail[i][j].split(",")[5]+"  employName="+data.classDetail[i].employ_name+" isMove='notMove' date='"+data.dates[j+4]+"' classEmpId="+data.classDetail[i][j].split(",")[4]+" setId="+data.classDetail[i][j].split(",")[2]+" beforeClassId="+data.classDetail[i][j].split(",")[2]+" style='text-align:center;'>"+((data.classDetail[i][j].split(",")[0]=='null'||data.classDetail[i][j].split(",")[0]=='')?'':data.classDetail[i][j].split(",")[0])+"</td>";							
							}
						}	
					}
				}
				classDetail += "</tr>";
				$("#reportListTitle").find("tbody").append(classDetailTitle);
				$("#reportList").find("tbody").append(classDetail);
			}
			var counts = "<tr>";
			counts +="<td colspan='1'>排班统计</td>";
			counts +="<td colspan='1'>"+data.count+"</td>";
			counts +="<td colspan='1'>"+should_time+"</td>";
			counts +="<td colspan='1' id='dayCountReckon'>"+dayCount+"</td>";
			counts +="<td colspan='1' id='mustAttnTimeReckon'>"+must_attn_time+"</td>";
			counts +="</tr>";
			var countsDetaile="<tr>";
			for(var j=1;j<=m-5;j++){
				countsDetaile +="<td style='text-align:center; height: 19px;'></td>";
			}
			countsDetaile += "</tr>";
			$("#reportListTitle").find("tbody").append(counts);
			$("#reportList").find("tbody").append(countsDetaile);
			var returnbox = "<div class='button-wrap ml-4'>" +
					"<button id='changeClassBut' onclick='changeClassSetting()' class='blue-but'><span><i class='icon'></i>调班</span></button></div>";
			$(".tablebox").append(returnbox);
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
	$("#reportList").show();
	$(".tablebox").show();
	$(".second").hide();
	$("td").css("color","");
	map = {};//赋值为空
	$("#showTitle").show();
	$("#titleClass").text(($("#firstDepart").find("option:selected").text()+"日常工作调班表—"+$("#month").val().substring(0,4)+"年"+$("#month").val().substring(5,7)+"月"));
	//每次点击查询 添加三个值
	$("#monthSave").val($("#month").val());
	$("#groupIdSave").val($("#groupId").val());
	$("#firstDepartSave").val($("#firstDepart").val());
	
	
	var groupName=$("#groupId").find("option:selected").text();
	if(groupName==""){
		$("#groupName").text("未选择");
	}else{
		$("#groupName").text(groupName);
	}
	
}

function showErrorInfo(num){
	if($(".tablebox").find("button").length>1){
		JEND.load('util.dialog', function() {
			if(num==1){
				JEND.util.dialog.alert("不能调今日前班次！");
			}else if(num==2){
				JEND.util.dialog.alert("入职前排班不能调！");
			}else if(num==3){
				JEND.util.dialog.alert("离职后排班不能调！");
			}else if(num==4){
				JEND.util.dialog.alert("待离职后排班不能调！");
			}
		});
	}	
}

function changeClassSetting(){
	
	$(".tablebox").empty();
	
	var returnbox = "<div class='button-wrap ml-4'>" +
	"<button onclick='saveDiv()' class='red-but'><span><i class='icon'></i>提交</span></button>"+
	"<button onclick='returnQuery()' class='blue-but'><span><i class='icon'></i>返回</span></button></div>";
	$(".tablebox").append(returnbox);
}

function returnQuery(){
	$(".tablebox").empty();
	$("td").unbind("click");
	var returnbox = "<div class='button-wrap ml-4'>" +
	"<button id='changeClassBut' onclick='changeClassSetting()' class='blue-but'><span><i class='icon'></i>调班</span></button></div>";
	$(".tablebox").append(returnbox);
	gotoPage();
}


function gotoPage1(page){
	$("#isMenu").val("false");
	
	if($("#firstDepart").val()==""){     
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("部门不能为空！");
		});
		return;
	}
	if($("#month").val()==""){     
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择月份！");
		});
		return;
	}
	if($("#groupId").val()==""){     
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择组别！");
		});
		return;
	}
	if($("#groupId").val()=="" || $("#month").val()==""||$("#firstDepart").val()==""){   
		return;
	}
	
	pageLoading(true);//加载动画
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	
	var data="month="+$("#month").val()+"&departId="+$("#firstDepart").val()+ "&page=" + $("#pageNo").val() + "&rows=10";
	var groupId=$("#groupId").val();
	if(groupId!="" && groupId!=null && groupId!=undefined){
		data="month="+$("#month").val()+"&departId="+$("#firstDepart").val()+"&groupId="+groupId+ "&page=" + $("#pageNo").val() + "&rows=10";
	}
	

	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "employeeClass/queryChangeClass.htm",
		success:function(response){
			$("#reportList1").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].id+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].groupName)+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].classEmployeeNum+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].createTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].classSettingPerson+"</td>";	
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
				}else if(response.rows[i].approvalStatus==600){
					approvalStatus = "失效同意";
				}else if(response.rows[i].approvalStatus==700){
					approvalStatus = "失效拒绝";
				}
				html += "<td style='text-align:center;'>"+approvalStatus+"</td>";
				html += "<td><a style='color:blue;'  onclick='showDetail("+response.rows[i].id+",\""+approvalStatus+"\")'>查看</a></td>";
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
	$("#showTitle").hide();
	
	$(".second").show();
	
}



//获取排班明细表格
function showDetail(id,status){
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
	$("#titleClass").text(($("#firstDepart").find("option:selected").text()+"日常工作调班表—"+$("#month").val().substring(0,4)+"年"+$("#month").val().substring(5,7)+"月"));

	$("#classStatus").html("&nbsp;&nbsp;&nbsp;&nbsp;"+"状态:"+status);
	//查询排班明细生成表格
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{id:id},
		url:contextPath + "/employeeClass/getDetailById.htm",
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
				if(i<=4){
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
				if(i<=4){
					daysTitle +="<td style='text-align:center;  height: 19px;'>"+data.days[i]+"</td>";
				}else{
					days +="<td style='text-align:center;  height: 19px;'>"+data.days[i]+"</td>";
				}
			}
			daysTitle += "</tr>";
			days += "</tr>";
			$("#reportListTitle").find("tbody").append(daysTitle);
			$("#reportList").find("tbody").append(days);
			//排班详情
			var m = data.days.length;
			var shuldTimeCount = 0;//应出勤统计
			var employeeCount = 0;//人数统计
			var daysCount = 0;//排班天数统计
			var mustAttnTimeCount  = 0;//排班工时统计
			for(i=0;i<data.classDetail.length;i++){
				
				
				var classDetailTitle = "<tr employName="+data.classDetail[i].employ_name+" employId="+data.classDetail[i].employ_id+">";
				classDetailTitle +="<td style='text-align:center;'>"+data.classDetail[i].positionName+"</td>";
				classDetailTitle +="<td style='text-align:center;'>"+data.classDetail[i].employ_name+"</td>";
				classDetailTitle +="<td style='text-align:center;'>"+data.classDetail[i].should_time+"</td>";
				classDetailTitle +="<td style='text-align:center;'>"+data.classDetail[i].dayCount+"</td>";
				classDetailTitle +="<td style='text-align:center;'>"+data.classDetail[i].must_attn_time+"</td>";
				classDetailTitle +="</tr>";
				var classDetail= "<tr employName="+data.classDetail[i].employ_name+" employId="+data.classDetail[i].employ_id+">";
				shuldTimeCount = shuldTimeCount + data.classDetail[i].should_time;
				employeeCount = employeeCount +1;
				daysCount = daysCount + data.classDetail[i].dayCount;
				mustAttnTimeCount = mustAttnTimeCount + data.classDetail[i].must_attn_time;
				for(var j=1;j<=m-5;j++){
					if(typeof(data.classDetail[i][j])=="undefined"){
						classDetail +="<td style='text-align:center;'></td>";
					}else{
						if(data.classDetail[i][j].split(",")[3]=="notMove"){
							//白,2019-03-25,1,notMove-->原班次信息，日期，原班次id，没有调班
							classDetail +="<td onclick='showClassSettingInfo(this);' employName="+data.classDetail[i].employ_name+" isMove='notMove' date="+data.classDetail[i][j].split(",")[1]+" setId="+data.classDetail[i][j].split(",")[2]+" style='text-align:center;cursor:pointer;'>"+data.classDetail[i][j].split(",")[0]+"</td>";
						}else if(data.classDetail[i][j].split(",")[3]=="move"){
							//休,2019-03-27,0,move,早,41-->原班次信息，日期，原班次id，调班,调整后班次信息,调整后班次id
							//休息的班次不存在，默认id=0，特殊处理
							classDetail +="<td onclick='showClassSettingInfo(this);' employName="+data.classDetail[i].employ_name+" isMove='move' date="+data.classDetail[i][j].split(",")[1]+" setId="+data.classDetail[i][j].split(",")[2]+" newSetId="+data.classDetail[i][j].split(",")[5]+" style='text-align:center;color:blue;cursor:pointer;'>"+data.classDetail[i][j].split(",")[4]+"</td>";
						}
					}
				}
				classDetail += "</tr>";
				$("#reportListTitle").find("tbody").append(classDetailTitle);
				$("#reportList").find("tbody").append(classDetail);
			}
			var counts = "<tr>";
			counts +="<td>排班统计</td>";
			counts +="<td>"+employeeCount+"</td>";
			counts +="<td>"+shuldTimeCount+"</td>";
			counts +="<td>"+daysCount+"</td>";
			counts +="<td>"+mustAttnTimeCount+"</td>";
			counts +="</tr>";
			var countsDetail="<tr>";
			for(var j=1;j<=m-5;j++){
				countsDetail +="<td style='text-align:center;height: 19px;'></td>";
			}
			countsDetail += "</tr>";
			$("#reportListTitle").find("tbody").append(counts);
			$("#reportList").find("tbody").append(countsDetail);
		
			$(".buttonDiv").empty();
			
			
			var returnbox = "<div class='button-wrap ml-4'>" +
			"<button  onclick='returnbox();' class='blue-but'><span><i class='icon'></i>返回</span></button></div>";
			$(".tablebox").append(returnbox);
		}
	});
}

function returnbox(){
	$("#reportList").find("thead").html("");
	$("#reportList").find("tbody").html("");
	$("#reportListTitle").find("thead").html("");
	$("#reportListTitle").find("tbody").html("");
	
	$("#showTitle").hide();
	$(".second").show();
	$("#reportList").hide();
	$(".tablebox").hide();
	
	$("#commonPage1").show();
}


//关闭窗口操作
function closeDiv(){
	$("#fastApproveDiv").hide();
	$("#passDiv").hide();
	$("#attnRefuseDiv").hide();
	$("#refuseDiv").hide();
	$("#detailDiv").hide();
}






function CompareTime(date1,date2){
	date1 = new Date(date1).format("yyyy-MM-dd");
	date2 = new Date(date2).format("yyyy-MM-dd");
	
    var oDate1 = new Date(date1);
    var oDate2 = new Date(date2);
    if(oDate1.getTime() > oDate2.getTime()){
    	return 1;
    } else {
    	return 2;
    }
}


//显示某员工某天班次信息
function showSettingInfo(obj){
	var employeId=$(obj).attr("classempid");
	if($(".tablebox").find("button").length>1){
		
		$("#classbut").empty();
		$("#classSet").empty();
		if($(obj).attr("setId")!="null"){
			
			var isMove = $(obj).attr("isMove");
			if(isMove=="notMove"){
				var param = {
					date:$(obj).attr("date"),
					setId:$(obj).attr("setId")
				};
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
				$("#pb_date").val("");
				$("#pb_name").val("");
				var employName=$(obj).attr("employName");
					var date ="";
					if($(obj).attr("setId")=="null" || $(obj).attr("setId")==undefined){
						date='休';
					}else{
						date=data.setName+" "+data.startTime+"-"+data.endTime;
					}
				//调班赋值 empid 和 时间
				$("#emp_date").val($(obj).attr("date"));
				$("#pb_date").html(data.date);
				$("#pb_weekDay").html(data.weekDay);
				$("#pb_name").text(employName);
				$("#classEmpId").val($(obj).attr("classEmpId"));
	
			}
		});
	   	
		$.ajaxSettings.async = false;
		$.getJSON(contextPath +'/employeeClass/getClassSet.htm?groupId='+$("#groupIdSave").val()+"",function(data){
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
		var tdId=$(obj).attr("id");
		var beforeClassId=$(obj).attr("setId");
		var beforeClass=$(obj).text();
		var mustAttnTime=$(obj).attr("mustAttnTime");
		if(mustAttnTime==undefined || typeof(mustAttnTime) == "undefined"){
			mustAttnTime=0;
		}
		var html ="<button class='red-but btn-middle' onclick='reconfirm(\""+tdId+"\",\""+beforeClass+"\","+beforeClassId+","+mustAttnTime+","+employeId+")'><span><i class='icon'></i>确定</span></button>"+ 
			 "<button class='red-but btn-middle' onclick='closeCounterDiv()'><span><i class='icon'></i>取消</span></button>";      
	
		   
		$("#classbut").append(html);
		
		
		$("#updateDiv").show();
	}
}
function closeCounterDiv(){
	$("#updateDiv").hide();
	$("#settingInfo").hide();
	$("#reconfirmDiv").hide();
}

//显示某员工某天班次信息
function showClassSettingInfo(obj){
	$("#commonPage1").hide();
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
			html +="<i onclick='closeCounterDiv();' class='mo-houtai-box-close' style='width: 25px;height: 30px;'></i></div>";
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

function exportScheduleTemplate(month,departId,groupId){
	var data="month="+month+"&departId="+departId+"&groupId="+groupId;
	window.location.href = basePath + "schedule/exportChangeClassTemplate.htm?"+data;
};

var SignExcel = function(){  
    
    this.init = function(){  
          
        //模拟上传excel  
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
            var url =  basePath + 'schedule/importChangeClassTemplate.htm';
            var departId = $("#firstDepart").val();
            var groupId = $("#groupId").val();
            var month = $("#month").val();
            $("#uploadDepart").attr("value",departId);
            $("#uploadGroup").attr("value",groupId);
            $("#uploadMonth").attr("value",month);
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
