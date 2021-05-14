$(function(){
	$("#kaoqin-date").bind('input propertychange', function() {  
		$("#kaoqin-start").val("");
		$("#kaoqin-end").val("");
		$("#type").val(0);
		getSignTime(this);
    }); 
	
	if($("#kaoqin-date").val() != '') {
		$("#kaoqin-start").val("");
		$("#kaoqin-end").val("");
		$("#type").val(0);
		getSignTimeData($("#kaoqin-date").val());
	}
})
function save(){
	if($("#kaoqin-date").val().trim()==""){
		OA.titlePup('考勤日期不能为空！','lose');
		return;
	}
	if($("#type").val()==1){
		if($("#kaoqin-start").val().trim()==""||$("#kaoqin-end").val().trim()==""){
			OA.titlePup('请选择申诉打卡时间！','lose');
			return; 
		}
	}else{
		if($("#kaoqin-start").val().trim()==""&&$("#kaoqin-end").val().trim()==""){
			OA.titlePup('请选择申诉打卡时间！','lose');
			return; 
		}
	}
	if($("#reason").val().trim()==""){
		OA.titlePup('申诉事由不能为空！','lose');
		return;
	}
	
	var startTime = $("#kaoqin-start").val().trim();
	var endTime = $("#kaoqin-end").val().trim();
	if(startTime==""){
		var param = {
				abnormalDate:$("#kaoqin-date").val(),
				endTime:addDate($("#kaoqin-date").val(),$("#kaoqin-end").attr("data-nextday"))+" "+$("#kaoqin-end").val()+($("#kaoqin-end").val().length>5?"":":59"),
				reason:$("#reason").val(),
				token:$("#token").val(),
				employeeId:$("#employId").val(),
				applyType:$("#applyType").val()
		};
	}
	if(endTime==""){
		var param = {
				abnormalDate:$("#kaoqin-date").val(),
				startTime:$("#kaoqin-date").val()+" "+$("#kaoqin-start").val()+($("#kaoqin-start").val().length>5?"":":00"),
				reason:$("#reason").val(),
				token:$("#token").val(),
				employeeId:$("#employId").val(),
				applyType:$("#applyType").val()
		};
	}
	if(startTime!=""&&endTime!=""){
		var param = {
				abnormalDate:$("#kaoqin-date").val(),
				startTime:$("#kaoqin-date").val()+" "+$("#kaoqin-start").val()+($("#kaoqin-start").val().length>5?"":":00"),
				endTime:addDate($("#kaoqin-date").val(),$("#kaoqin-end").attr("data-nextday"))+" "+$("#kaoqin-end").val()+($("#kaoqin-end").val().length>5?"":":59"),
				reason:$("#reason").val(),
				token:$("#token").val(),
				employeeId:$("#employId").val(),
				applyType:$("#applyType").val()
		};
	}
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{abnormalDate:$("#kaoqin-date").val(),employId:$("#employId").val(),applyType:$("#applyType").val()},
		url:contextPath + "/empApplicationAbnormalAttendance/getSignTime.htm",
		success:function(data){
			if(data.success){
				var sureS = $("#kaoqin-start").val().trim()==""?$("#startPunchTime").val():$("#kaoqin-start").val();
				sureS = sureS.substring(0,5);
				var sureE = $("#kaoqin-end").val().trim()==""?$("#endPunchTime").val():$("#kaoqin-end").val();
				sureE = sureE.substring(0,5);
				OA.twoSurePop({
					tips:'是否确认本日考勤时间修改为'+sureS+'至'+sureE+'？',
					sureFn:function(){
						OA.pageLoading(true);
						$.ajax({
							async:true,
							type:'post',
							timeout : 5000,
							dataType:'json',
							data:param,
							url:contextPath + "/empApplicationAbnormalAttendance/save.htm",
							success:function(data){
								if(data.success){
									if($("#applyType").val()==0){
										window.location.href=contextPath + "/runTask/index.htm";
									}else{
										window.location.href=contextPath + "/empAttn/subIndex.htm?employId="+$("#employId").val();
									}
								}else{
									OA.titlePup(data.message,'lose');
									$("#token").val(data.token);
								}
							},
							complete:function(XMLHttpRequest,status){
							   OA.pageLoading(false);
							   if(status=="timeout"){
								   window.location.href=contextPath + "/runTask/index.htm";
							   }else if(XMLHttpRequest.status == 9999){
								   OA.titlePup("请勿重复提交表单",'lose');
							   }
							}
						});
					},
					cancelFn:function(){
						
					}
				})
			} else {
				OA.titlePup(data.msg,'lose');
			}
		}
	});
}

function getSignTime(obj){
	if($(obj).val()!=null&&$(obj).val()!=""){
		getSignTimeData($(obj).val());
	}
}

function getSignTimeData(attnDate){
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{abnormalDate:attnDate,employId:$("#employId").val(),applyType:$("#applyType").val()},
		url:contextPath + "/empApplicationAbnormalAttendance/getSignTime.htm",
		success:function(data){
			if(data.success){
				var startPunchTime = data.startPunchTime==""?"空卡":data.startPunchTime.substring(11,16);
				if(data.firstPunch=="正常"){
					$(".edit-info").find(".selMatter:eq(4)").hide();
					$("#kaoqin-start").val(data.startPunchTime==""?"":data.startPunchTime.substring(11,19));
					if(data.attnStatus==1){
						$("#kaoqin-start").val("");
						$(".edit-info").find(".selMatter:eq(4)").show();
					}
				}else{
					$(".edit-info").find(".selMatter:eq(4)").show();
				}
				var endPunchTime = data.endPunchTime==""?"空卡":data.endPunchTime.substring(11,16);
				if(data.lasePunch=="正常"){
					$(".edit-info").find(".selMatter:eq(5)").hide();
					$("#kaoqin-end").val(data.endPunchTime==""?"":data.endPunchTime.substring(11,19));
					if(data.attnStatus==1){
						$("#kaoqin-end").val("");
						$(".edit-info").find(".selMatter:eq(5)").show();
					}
				}else{
					$(".edit-info").find(".selMatter:eq(5)").show();
				}
				if(startPunchTime == "空卡" && endPunchTime == "空卡"){
					$("#type").val(1);
				}
				var firstPunch = startPunchTime == "空卡" ? startPunchTime : startPunchTime + "(" + data.firstPunch + ")";
				var lasePunch = endPunchTime == "空卡" ? endPunchTime : endPunchTime + "(" + data.lasePunch + ")";
				$(".edit-info").find(".selMatter:eq(3)").find("div").text(firstPunch+"-"+lasePunch);
				$("#startPunchTime").val(startPunchTime);
				$("#endPunchTime").val(endPunchTime);
			} else {
				OA.titlePup(data.msg,'lose');
			}
		}
	});
}