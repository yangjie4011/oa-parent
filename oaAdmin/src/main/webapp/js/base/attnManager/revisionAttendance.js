$(function(){
	$('#absolutStartTime').attr("disabled",true);
	$('#absolutEndTime').attr("disabled", true);
	$('#relativeclickMe,#absolutclickMe').click(function(){
		$("input[name='pageStr']").val();
		$("#cancelQuitIds").val();
        $("#employeeIds").val();
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
	            $('#relativeclickMe,#absolutclickMe').val(nameVal+'等,共计'+ids.length+'个员工');
	            $("#relativeemployeeIds").val(ids);
	            $("#absolutemployeeIds").val(ids);
	        }
	    })
	})
	
	
	$("#query").click(function(){
		gotoPage(1);
	});
	$(".jui-tabswitch").find("li").each(function(i){
		var obj = $(this);
		if(i==0&&$(obj).attr("id")=="listTab"){
			$(".daiban").show();
		}else if(i==0&&$(obj).attr("id")=="detailTab"){
			$(".yiban").show();
			$("#commonPage1").show();
		}else if(i==0&&$(obj).attr("id")=="thridTab"){
			$("#commonPage").show();
			$(".attnHistoryQuery").show();
			$("#query").unbind("click");
			gotoPage(1);
			$("#query").click(function(){
				gotoPage(1);
			});
		}
		$(obj).click(function(){
			if($(this).attr("id")=="listTab"){
				$(".daiban").show();
				$("#commonPage").hide();
				$(".yiban").hide();
				$("#commonPage1").hide();
				$(".attnHistoryQuery").hide();
				$("#commonPage2").hide();
				
				$("#absolutAttnDate").val("");
				$("#absolutStartTime").val("");
				$("#absolutEndTime").val("");
				$("#downAttnTime").val("");
				$("#absolutRemark").val("");
				$("#upAttnTime").prop("checked",false);
				$("#downAttnTime").prop("checked",false);
				$("#AttntowDay").prop("checked",false);
				
			}else if($(this).attr("id")=="detailTab"){
				$(".daiban").hide();
				$("#commonPage").hide();
				$(".attnHistoryQuery").hide();
				$("#commonPage2").hide();
				$(".yiban").show();
				$("#commonPage1").show();
				
				$("#relativeAttnTime").val("");
				$("#relativeStatr").val("");
				$("#relativeEnd").val("");
				$("#attnTimePrint").val("");
				$("#relativeRemark").val("");
			}else if($(this).attr("id")=="thridTab"){
				$(".daiban").hide();
				$("#commonPage").show();
				$(".yiban").hide();
				$("#commonPage1").hide();
				$(".attnHistoryQuery").show();
				$("#query").unbind("click");
				gotoPage(1);
				$("#query").click(function(){
					gotoPage(1);
				});
				
			}
			$('#relativeclickMe,#absolutclickMe').val("");
            $("#relativeemployeeIds,#absolutemployeeIds").val("");
		})
	});
	
	var currentCompanyId = $("#currentCompanyId").val();
	//初始化工时类型
	initWorkType(currentCompanyId);
	//初始化员工类型
	initEmployeeType(currentCompanyId);
	//初始化是否排班
	initWhetherScheduling();
	//初始化单据状态
	initApprovalStatus(currentCompanyId);
	//初始化部门
	getFirstDepart();
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		getSecondDepart(this.value);
	});
	
	//上周
	$("#lastWeek").click(function(){
		setLastWeek("applyStartDate","applyEndDate", 'yyyy-MM-dd')
	});
	//本月
	$("#thisMonth").click(function(){
		setCurrentMonthTime("applyStartDate","applyEndDate", 'yyyy-MM-dd');
	});
	//上月
	$("#lastMonth").click(function(){
		setLastMonthTime("applyStartDate","applyEndDate", 'yyyy-MM-dd');
	});
});

//再次确认相对修改
function relativeSure(){	
	if($(".relativeForm  :text[name='startTime']").val()=="" ){
		JEND.page.alert({type:"error",message:"请输入考勤时间"});
		return;
	}
	var attnTime=$(".relativeForm  :text[name='attnDate']").val();
	var attnTime1 = attnTime;
	if($("#nextDay").is(':checked')){
		var attnDate = new Date(Date.parse(attnTime.replace(/-/g,  "/"))); 
		attnDate.setDate(attnDate.getDate()+1);
		attnTime1 = attnDate.format("yyyy/MM/dd").replaceAll("/","-");
	}
	var startTime;
	if($(".relativeForm  :text[name='startTime']").val()!=""){
		 startTime=attnTime1+$(".relativeForm  :text[name='startTime']").val();
	}
	if($("#relativeemployeeIds").val()=="" ){
		JEND.page.alert({type:"error",message:"请选择员工"});
		return;
	}
	
	if(attnTime==""){
		JEND.page.alert({type:"error",message:"考勤不能为空"});
		return;
	}
	
	$("#upRelativeAttn").text(startTime);
	$("#relativeDiv").show();
}
function zcrelativeSure(){
	var attnTime=$(".relativeForm  :text[name='attnDate']").val();
	var attnTime1 = attnTime;
	if($("#nextDay").is(':checked')){
		var attnDate = new Date(Date.parse(attnTime.replace(/-/g,  "/"))); 
		attnDate.setDate(attnDate.getDate()+1);
		attnTime1 = attnDate.format("yyyy/MM/dd").replaceAll("/","-");
	}
	var startTime;
	if($(".relativeForm  :text[name='startTime']").val()!=""){
		 startTime=attnTime1+$(".relativeForm  :text[name='startTime']").val();
	}
	data={employeeIds:$("#relativeemployeeIds").val().toString(),attnDate:attnTime,remark:$("#relativeRemark").val(),
			startTime:startTime,dataType:$("#dateType").val()
	};
	$.ajax({
		async:true,
		type:'POST',
		dataType:'json',
		data: data,
		url:  basePath + "empAttn/relativeUpdateAttnTime.htm",
		success:function(response){
			$("#messageBox #messageContent").text(response.message);  
			$("#messageBox").show();
			$("#relativeemployeeIds").val("");
			$("#relativeclickMe").val("");
			$("#relativeAttnTime").val("");
			$("#relativeStatr").val("");
			$("#relativeEnd").val("");
			$("#attnTimePrint").val("");
			$("#relativeRemark").val("");		
			$("#nextDay").attr("checked",false); 
			closeDiv('relativeDiv');
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				closeDiv('relativeDiv');
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
	    }
	});
	
}
//再次确认绝对修改
function absolutSure(){	
var attnTime =$("#absolutAttnDate").val();
	
	var startTime,endTime;
	if(typeof($("#absolutStartTime").val())!="undefined" && $("#absolutStartTime").val()!=""){
		startTime=attnTime+$("#absolutStartTime").val();
	}
	if(typeof($("#absolutEndTime").val())!="undefined" && $("#absolutEndTime").val()!=""){
		endTime=attnTime+$("#absolutEndTime").val();
	}
	
	if($("#AttntowDay").is(':checked')){
		startDate = new Date(endTime);
        startDate = +startDate + 1000*60*60*24;
        startDate = new Date(startDate);
        endTime = startDate.getFullYear()+"-"+(startDate.getMonth()+1)+"-"+startDate.getDate()+$("#absolutEndTime").val();
	}
	
	
	
	if($("#absolutemployeeIds").val()=="" ){
		JEND.page.alert({type:"error",message:"请选择员工"});
		return;
	}
	
	if(attnTime==""){
		JEND.page.alert({type:"error",message:"考勤不能为空"});
		return;
	}
	var count=null;
	if($("#absolutStartTime").val()=="" ){
		count++;
	}
	if($("#absolutEndTime").val()==""){
		count++;
	}
	if(count>1){
		JEND.page.alert({type:"error",message:"至少插入一条考勤时间"});
		return;
	}
	count=0;
	if(typeof(startTime)!="undefined" && $("#absolutStartTime").val()!=""){
		$("#upAbsoluteAttn").text(startTime);
	}else{
		if($("#upAttnTime").is(':checked')){
			$("#upAbsoluteAttn").text("未刷卡");
		}else{
			$("#upAbsoluteAttn").text("");
		}	
	}
	if(typeof(endTime)!="undefined" && $("#absolutEndTime").val()!=""){
		$("#downAbsoluteAttn").text(endTime);
	}else{
		if($("#downAttnTime").is(':checked')){
			$("#downAbsoluteAttn").text("未刷卡");
		}else{
			$("#downAbsoluteAttn").text("");
		}
	}
	$("#absoluteDiv").show();
}
function zcabsolutSure(){
	var attnTime =$("#absolutAttnDate").val();
	
	var startTime,endTime;
	if(typeof($("#absolutStartTime").val())!="undefined" && $("#absolutStartTime").val()!=""){
		startTime=attnTime+$("#absolutStartTime").val();
	}
	if(typeof($("#absolutEndTime").val())!="undefined" && $("#absolutEndTime").val()!=""){
		endTime=attnTime+$("#absolutEndTime").val();
	}
	
	if($("#AttntowDay").is(':checked')){
		startDate = new Date(endTime);
        startDate = +startDate + 1000*60*60*24;
        startDate = new Date(startDate);
        endTime = startDate.getFullYear()+"-"+(startDate.getMonth()+1)+"-"+startDate.getDate()+$("#absolutEndTime").val();
	}
	
	
	data={employeeIds:$("#absolutemployeeIds").val().toString(),attnDate:attnTime,remark:$("#absolutRemark").val(),
			startTime:startTime,endTime:endTime,dataType:$("#absolutDateType").val()
	};
	
	
	$.ajax({
		async:true,
		type:'POST',
		dataType:'json',
		data: data,
		url:  basePath + "empAttn/absolutUpdateAttnTime.htm",
		success:function(response){
			$("#messageBox #messageContent").text(response.message);  
			$("#messageBox").show();
			$("#absolutemployeeIds").val("");
			$("#absolutclickMe").val("");
			$("#absolutAttnDate").val("");
			$("#absolutStartTime").val("");
			$("#absolutEndTime").val("");
			$("#absolutRemark").val("");			
			closeDiv('absoluteDiv');
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				closeDiv('absoluteDiv');
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
	    }
	});
	
}
function relativeChangeTime(){
	
	var ids = $("#relativeemployeeIds").val().toString();
	if($(".relativeForm  :text[name='attnDate']").val()==null || ids.indexOf(",") != -1){
		return;
	}
	$(".relativeForm  :text[name='attnStartTime']").val("");
	$(".relativeForm  :text[name='endTime']").val("");
	data={employeeIds:$("#relativeemployeeIds").val(),type:2,
			attnDate:$(".relativeForm  :text[name='attnDate']").val()};
			$.ajax({
				async:false,
				type:'POST',
				dataType:'json',
				data: data,
				url:  basePath + "empAttn/getAttTimeById.htm",
				success:function(response){
					if(response.flag){
						var str = "1970-01-01";
						$(".relativeForm  :text[name='attnStartTime']").val(response.result.startWorkTime==null?"":response.result.startWorkTime.replace(str,""));
						$(".relativeForm  :text[name='endTime']").val(response.result.endWorkTime==null?"":response.result.endWorkTime.replace(str,""));
					}
				}	
			});
}

function absolutClearTime(){
	$("#absolutStartTime").val("");
	$("#absolutEndTime").val("");
}

function absolutChangeTime(){
	var ids = $("#absolutemployeeIds").val().toString();
	if($(".absolutForm  :text[name='attnDate']").val()==null || ids.indexOf(",") != -1){
		$('#absolutStartTime').attr("disabled",false);
		$('#absolutEndTime').attr("disabled", false);
		$("#absolutStartTime").val("");
		$("#absolutEndTime").val("");
		$("#upAttnTime").prop("checked",false);
		$("#downAttnTime").prop("checked",false);
		return;
	}
	$("#absolutStartTime").val("");
	$("#absolutEndTime").val("");
	data={employeeIds:$("#relativeemployeeIds").val(),type:3,
			attnDate:$(".absolutForm  :text[name='attnDate']").val()};
			$.ajax({
				async:false,
				type:'POST',
				dataType:'json',
				data: data,
				url:  basePath + "empAttn/getAttTimeById.htm",
				success:function(response){
					if(response.upAttn){
						$('#absolutStartTime').attr("disabled",true);
						$("#upAttnTime").prop("checked",true);
					}else{
						$('#absolutStartTime').attr("disabled",false);
						$("#upAttnTime").prop("checked",false);
					}
					if(response.downAttn){
			            $('#absolutEndTime').attr("disabled", true);
			            $("#downAttnTime").prop("checked",true);
					}else{
						$('#absolutEndTime').attr("disabled", false);
						$("#downAttnTime").prop("checked",false);
					}
					if(response.flag){
						var str = "1970-01-01";
						if(!response.upAttn){
							$("#absolutStartTime").val(response.result.startWorkTime.replace(str,""));
						}
						if(!response.downAttn){
							$("#absolutEndTime").val(response.result.endWorkTime.replace(str,""));
						}
					}
				}	
			});
}

function startTimeHover(){
	if($("#upAttnTime").is(':checked')){
		$("#absolutStartTime").val("");
		$('#absolutStartTime').attr("disabled",true);
		
	}else{
		$("#absolutStartTime").val("");
		$('#absolutStartTime').attr("disabled",false);
		
	}
}
function endTimeHover(){
	if($("#downAttnTime").is(':checked')){
		$("#absolutEndTime").val("");
		$('#absolutEndTime').attr("disabled", true);
	}else{
		$("#absolutEndTime").val(""); 
		$('#absolutEndTime').attr("disabled", false);		 
	}
}


function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=15";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "empAttn/getUpdateLogList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].code)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].cnName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].departName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].updateAttnDate)+"</td>";				
				var updateType=null;
				var startType=null;
				var endType=null;
				if(response.rows[i].type==2){
					updateType = "相对修改";
				}else if(response.rows[i].type==3){
					updateType = "绝对修改";
					if("undefined" == response.rows[i].updateStartTime || "undefined" == typeof(response.rows[i].updateStartTime)||null==response.rows[i].updateStartTime){
						startType="未刷卡";
					}else{
						startType=response.rows[i].updateStartTime;
					}
					if("undefined" == response.rows[i].updateEndTime || "undefined" == typeof(response.rows[i].updateEndTime)||null==response.rows[i].updateEndTime){
						endType="未刷卡";
					}else{
						endType=response.rows[i].updateEndTime;
					}
				}
				html += "<td style='text-align:center;'>"+getValByUndefined(updateType)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].insertAttnTime).substring(11,19)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(startType)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(endType)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].createTime)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].createUser)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].remark)+"</td>";
				html += "</tr>";
			}
			$("#reportList").append(html);
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



//提示框关闭
var closeDiv = function(divName){
	if(divName=="absoluteDiv"){		
		$("#absolutezcqr").attr("class","red-but");//设置Id为two的class属性。
		$('#absolutezcqr').attr("disabled",false)//将input元素设置为disabled
		$('#absolutezcqx').attr("disabled",false)//将input元素设置为disabled		
	}
	if(divName=="relativeDiv"){		
		$("#relativezcqr").attr("class","red-but");//设置Id为two的class属性。
		$('#relativezcqr').attr("disabled",false)//将input元素设置为disabled
		$('#relativezcqx').attr("disabled",false)//将input元素设置为disabled		
	}
	$("#"+divName).hide();
}


