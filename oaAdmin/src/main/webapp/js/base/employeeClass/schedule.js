var map = {};
$(function(){
	signExcel = new SignExcel();  
	signExcel.init();
	var currentCompanyId = $("#currentCompanyId").val();
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
	$("#downloadScheduleTemplate").click(function(){
		var departId = $("#firstDepart").val();
		var groupId = $("#groupId").val();
		if($("#firstDepart").val()==""){
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("请选择部门！");
			});
			return;
		}
		if($("#groupId").val()==""){
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("请选择排班班组！");
			});
			return;
		}
		exportScheduleTemplate(departId,groupId);
			
		
	});
});

//获取未提交的排班数据
function getUnCommitData(type){
	//type=0,编辑模式，type=1查看模式
	if($("#firstDepart").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择部门！");
		});
		return;
	}
    if($("#groupId").val()==""){
    	JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择组别！");
		});
    	return;
	}
    $("#groupIdStr").val($("#groupId").val());
	map = {};
	pageLoading(true);//加载动画
	//查询排班明细生成表格
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{departId:$("#firstDepart").val(),groupId:$("#groupId").val()},
		url:contextPath + "/employeeClass/getUnCommitData.htm",
		success:function(data){
			if(data.success){
				$("#detailListTitle").find("thead").html("");
				$("#detailListTitle").find("tbody").html("");
				$("#detailList").find("thead").html("");
				$("#detailList").find("tbody").html("");
				//标题
				$("#detailTitle").empty();
				$("#detailTitle").html("<strong>"+data.title+"</strong>");
				//申请日期
				$("#groupName").val(data.groupName);
				//状态
				$("#approvalStatus").val(data.approvalStatus);
				//加载标题
				var headText = "<tr>";
				var head = "<tr>";
				for(var i=0;i<data.weekDays.length;i++){
					if(i<=5){
						headText +="<th style='overflow-x:auto;width:10px;text-align:center; height: 19px;'>"+data.weekDays[i]+"</th>";
					}else{
						head +="<th style='overflow-x:auto;width:10px;text-align:center;'>"+data.weekDays[i]+"</th>";
					}
				}
				headText += "</tr>";
				head += "</tr>";
				$("#detailListTitle").find("thead").append(headText);
				$("#detailList").find("thead").append(head);
				//天
				var daysTitle = "<tr>";
				var days = "<tr>";
				for(var i=0;i<data.days.length;i++){
					if(i<=5){
						daysTitle +="<td style='text-align:center;  height: 19px;'>"+data.days[i]+"</td>";
					}else{
						days +="<td style='text-align:center;'>"+data.days[i]+"</td>";
					}
				}
				daysTitle += "</tr>";
				days += "</tr>";
				$("#detailListTitle").find("tbody").append(daysTitle);
				$("#detailList").find("tbody").append(days);
				//排班详情
				var m = data.days.length;
				var shuldTimeCount = 0;//应出勤统计
				var employeeCount = 0;//人数统计
				var daysCount = 0;//排班天数统计
				var mustAttnTimeCount  = 0;//排班工时统计
				for(i=0;i<data.classDetail.length;i++){
					var classDetailTitle = "<tr class='classDetail' employName="+data.classDetail[i].employ_name+" employId="+data.classDetail[i].employ_id+">";
					classDetailTitle +="<td style='text-align:center;'>"+data.classDetail[i].code+"</td>";
					classDetailTitle +="<td style='text-align:center;'>"+data.classDetail[i].positionName+"</td>";
					classDetailTitle +="<td style='text-align:center;'>"+data.classDetail[i].employ_name+"</td>";
					classDetailTitle +="<td style='text-align:center;'>"+data.classDetail[i].should_time+"</td>";
					classDetailTitle +="<td style='text-align:center;'>"+data.classDetail[i].dayCount+"</td>";
					classDetailTitle +="<td class='mustAttnTime' style='text-align:center;'>"+data.classDetail[i].must_attn_time+"</td>";
					classDetailTitle +="</tr>";
					var classDetail="<tr employName="+data.classDetail[i].employ_name+" employId="+data.classDetail[i].employ_id+">";
					shuldTimeCount = shuldTimeCount + data.classDetail[i].should_time;
					employeeCount = employeeCount +1;
					daysCount = daysCount + data.classDetail[i].dayCount;
					mustAttnTimeCount = mustAttnTimeCount + data.classDetail[i].must_attn_time;
					for(var j=1;j<=m-6;j++){
						if(typeof(data.classDetail[i][j])=="undefined"){
							if(type==0){
								classDetail +="<td onclick='selectClassSet(this);'rowsList="+i+" date="+data.dates[j+5]+" mustAttnTime='0' style='text-align:center;'>&nbsp;</td>";
							}else{
								classDetail +="<td date="+data.dates[j+5]+" mustAttnTime='0' style='text-align:center;'>&nbsp;</td>";
							}
						}else{
							if(type==0){
								classDetail +="<td onclick='selectClassSet(this);' rowsList="+i+" employName="+data.classDetail[i].employ_name+" date="+data.dates[j+5]+" setId="+data.classDetail[i][j].split(",")[2]+" mustAttnTime="+data.classDetail[i][j].split(",")[3]+" style='text-align:center;cursor:pointer;'>"+data.classDetail[i][j].split(",")[0]+"</td>";
							}else{
								classDetail +="<td employName="+data.classDetail[i].employ_name+" date="+data.dates[j+5]+" setId="+data.classDetail[i][j].split(",")[2]+" mustAttnTime="+data.classDetail[i][j].split(",")[3]+" style='text-align:center;cursor:pointer;'>"+data.classDetail[i][j].split(",")[0]+"</td>";
							}
						}
					}
					classDetail += "</tr>";
					$("#detailListTitle").find("tbody").append(classDetailTitle);
					$("#detailList").find("tbody").append(classDetail);
				}
				var counts = "<tr>";
				counts +="<td colspan='2'>排班统计</td>";
				counts +="<td>"+employeeCount+"</td>";
				counts +="<td>"+shuldTimeCount+"</td>";
				counts +="<td>"+daysCount+"</td>";
				counts +="<td id='mustAttnTimeCount'>"+mustAttnTimeCount+"</td>";
				var countsDetail="<tr>"
				for(var j=1;j<=m-6;j++){
					 countsDetail +="<td style='text-align:center; height: 19px;'></td>";
				}
				countsDetail+="</tr>";
				counts += "</tr>";
				$("#detailListTitle").find("tbody").append(counts);
				$("#detailList").find("tbody").append(countsDetail);
			
				var html = "";
				if(type==0){
					html += "<button onclick='saveSchedule();' class='btn-green btn-large'><span><i class='icon'></i>保存</span></button>";
					html += "<button onclick='getUnCommitData(1);' class='btn-blue btn-large'><span><i class='icon'></i>返回</span></button>"; 
				}else{
					html += "<button onclick='commitSchedule();' class='btn-green btn-large'><span><i class='icon'></i>提交审核</span></button>"; 
					html += "<button onclick='getUnCommitData(0);' class='btn-blue btn-large'><span><i class='icon'></i>编辑</span></button>";
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

//选择班次
function selectClassSet(obj){
	var rowsIndex=parseFloat($(obj).closest("tr").index())+parseFloat(1);
	$("#classSet").empty();
	$.ajaxSettings.async = false;
	$.getJSON(contextPath +'/employeeClass/getClassSet.htm?groupId='+$("#groupIdStr").val()+"",function(data){
		$.each(data, function(index) {
			var option = "";
			if(index==0){
				option +="<option mustAttnTime='0' value="+data[index].id+">"+data[index].name+"</option>";
			}else{
				option +="<option mustAttnTime="+data[index].mustAttnTime+" value="+data[index].id+">"+data[index].name+"&nbsp;"+data[index].startTime+"-"+data[index].endTime+"&nbsp;"+data[index].mustAttnTime+"小时</option>";
			}
			$("#classSet").append(option);
		});
	});
	$("#employName").val("");
	$("#employId").val("");
	$("#classDate").val("");
	$("#employName").val($(obj).parent().attr("employName"));
	$("#employId").val($(obj).parent().attr("employId"));
	$("#classDate").val($(obj).attr("date"));
	$("#classSetDiv").show();
	$("#setClassSet").unbind();
	var tdObj = obj;
	$("#setClassSet").click(function(){
		    var param = {
				"empId":$("#employId").val(),
				"date":$("#classDate").val(),
				"classId":$("#classSet").val()
			};
			var key=$("#employId").val()+"_"+$("#classDate").val();
			if(isHaving(key)){
				deleteMap(key);
			}
			setMap(param);
			$(tdObj).text($("#classSet").find("option:selected").text().substring(0,1));
			var oldMustAttnTime = $(tdObj).attr("mustAttnTime");
			var newMustAttnTime = $("#classSet").find("option:selected").attr("mustAttnTime");
			$("#detailListTitle").find("tr:eq("+rowsIndex+")").find("td:eq(5)").text(parseFloat($("#detailListTitle").find("tr:eq("+rowsIndex+")").find("td:eq(5)").text())-parseFloat(oldMustAttnTime)+parseFloat(newMustAttnTime));
			$(tdObj).attr("mustAttnTime",newMustAttnTime);
			var mustAttnTimeCount = 0;
			$(".mustAttnTime").each(function(){
				mustAttnTimeCount +=parseFloat($(this).text());
			});
			$("#mustAttnTimeCount").text(mustAttnTimeCount);
			$("#classSetDiv").hide();
			rowsIndex=0;
	})
}

//关闭弹框
function closeDiv(){
	$("#employName").val("");
	$("#employId").val("");
	$("#classDate").val("");
	$("#classSetDiv").hide();
}

//保存排班数据
function saveSchedule(){
	if(JSON.stringify(map)=="{}"){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("没有任何班次变更，无需保存！");
		});
		return;
	}
	pageLoading(true);//加载动画
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{departId:$("#firstDepart").val(),groupId:$("#groupId").val(),info:JSON.stringify(map)},
		url:contextPath + "/employeeClass/saveSchedule.htm",
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

//提交审核
function commitSchedule(){
	var flag = true;
	$(".classDetail").each(function(){
		var shouleTime = $(this).find("td:eq(3)").text();
		var mustTime = $(this).find("td:eq(5)").text();
		if(parseFloat(mustTime)<parseFloat(shouleTime)){
			flag = false;
			return;
		}
	})
	if(!flag){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("排班表中有员工应出勤工时未排满，请确认排满后提交！");
		});
		return;
	}
	pageLoading(true);//加载动画
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{departId:$("#firstDepart").val(),groupId:$("#groupId").val()},
		url:contextPath + "/employeeClass/commitSchedule.htm",
		success:function(data){
			JEND.load('util.dialog', function() {
				JEND.util.dialog.alert(data.message);
			});
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

//分页查询排班记录
function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=15";
	var url = "employeeClass/getScheduleRecord.htm";
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
				html += "<td style='text-align:center;'>"+response.rows[i].classMonth+"</td>";//年月
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";//部门
				html += "<td style='text-align:center;'>"+response.rows[i].groupName+"</td>";//组别
				html += "<td style='text-align:center;'>"+response.rows[i].classSettingPerson+"</td>";//排班人
				html += "<td style='text-align:center;'>"+response.rows[i].createTime+"</td>";//排班日期
				html += "<td style='text-align:center;'>"+response.rows[i].auditorName+"</td>";//排班批核人
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
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{id:id},
		url:contextPath + "/employeeClass/getDetailById.htm",
		success:function(data){
			$("#detailListTitle").find("thead").html("");
			$("#detailListTitle").find("tbody").html("");
			$("#detailList").find("thead").html("");
			$("#detailList").find("tbody").html("");
			//标题
			$("#detailTitle").empty();
			$("#detailTitle").html("<strong>"+data.title+"</strong>");
			//组别
			$("#groupName").val(data.groupName);
			//状态
			$("#approvalStatus").val(data.approvalStatus);
			//加载标题
			var headTitle = "<tr>";
			var head = "<tr>";
			for(var i=0;i<data.weekDays.length;i++){
				if(i<=4){
					headTitle +="<th style='overflow-x:auto;width:10px;text-align:center; height: 19px;'>"+data.weekDays[i]+"</th>";
				}else{
					head +="<th style='overflow-x:auto;width:10px;text-align:center; height: 19px;'>"+data.weekDays[i]+"</th>";
				}
			}
			headTitle += "</tr>";
			head += "</tr>";
			$("#detailListTitle").find("thead").append(headTitle);
			$("#detailList").find("thead").append(head);
			//天
			var daysTitle = "<tr>";
			var days = "<tr>";
			for(var i=0;i<data.days.length;i++){
				if(i<=4){
					daysTitle +="<td style='text-align:center; height: 19px;'>"+data.days[i]+"</td>";
				}else{
					days +="<td style='text-align:center; height: 19px;'>"+data.days[i]+"</td>";
				}
			}
			daysTitle += "</tr>";
			days += "</tr>";
			$("#detailListTitle").find("tbody").append(daysTitle);
			$("#detailList").find("tbody").append(days);
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
				var classDetail="<tr employName="+data.classDetail[i].employ_name+" employId="+data.classDetail[i].employ_id+">";
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
							classDetail +="<td onclick='showSettingInfo(this);' employName="+data.classDetail[i].employ_name+" isMove='notMove' date="+data.classDetail[i][j].split(",")[1]+" setId="+data.classDetail[i][j].split(",")[2]+" style='text-align:center;cursor:pointer;'>"+data.classDetail[i][j].split(",")[0]+"</td>";
						}else if(data.classDetail[i][j].split(",")[3]=="move"){
							//休,2019-03-27,0,move,早,41-->原班次信息，日期，原班次id，调班,调整后班次信息,调整后班次id
							//休息的班次不存在，默认id=0，特殊处理
							classDetail +="<td onclick='showSettingInfo(this);' employName="+data.classDetail[i].employ_name+" isMove='move' date="+data.classDetail[i][j].split(",")[1]+" setId="+data.classDetail[i][j].split(",")[2]+" newSetId="+data.classDetail[i][j].split(",")[5]+" style='text-align:center;color:blue;cursor:pointer;'>"+data.classDetail[i][j].split(",")[4]+"</td>";
						}
					}
				}
				classDetail += "</tr>";
				$("#detailListTitle").find("tbody").append(classDetailTitle);
				$("#detailList").find("tbody").append(classDetail);
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
				countsDetail +="<td style='text-align:center; height: 19px;'></td>";
			}
			countsDetail += "</tr>";
			$("#detailListTitle").find("tbody").append(counts);
			$("#detailList").find("tbody").append(countsDetail);
			$(".tableDiv").hide();//隐藏待办列表
			$("#commonPage").hide();//隐藏分页控件
			$(".buttonDiv").empty();
			var html = "";
			html += "<button onclick='backWaitList();' class='btn-blue btn-large'><span><i class='icon'></i>返回</span></button>"; 
			$(".buttonDiv").append(html);
			$("#detailDiv").show();//显示详情表格
			$(".scheduleRecord").hide();
			$("#commonPage").hide();
		}
	});
	
}

function backWaitList(){
	$("#detailDiv").hide();//显示详情表格
	$(".scheduleRecord").show();
	$("#commonPage").show();
}

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

function exportScheduleTemplate(departId,groupId){
	var data="departId="+departId+"&groupId="+groupId;
	window.location.href = basePath + "schedule/exportScheduleTemplate.htm?"+data;
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
            var url =  basePath + 'schedule/importScheduleTemplate.htm';
            var departId = $("#firstDepart").val();
            var groupId = $("#groupId").val();
            $("#uploadDepart").attr("value",departId);
            $("#uploadGroup").attr("value",groupId);
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
