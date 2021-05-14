$(function(){
	
	$("#query").click(function(){
		gotoPage(1);
	});
	
	//初始化部门
	getFirstDepart();
	
	//初始化员工控件
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
	
	//tab切换
	$(".jui-tabswitch").find("li").each(function(){
		var obj = $(this);
		$(obj).click(function(){
			if($(this).attr("id")=="leaveRegister"){
				$(".leaveRegister").show();
				$(".leaveRegisterSearch").hide();
				$("#commonPage").hide();
			}else if($(this).attr("id")=="leaveRegisterSearch"){
				$(".leaveRegister").hide();
				$(".leaveRegisterSearch").show();
				$("#commonPage").show();
			}
		})
	});
	
});

//切换假期类型
function changeLeaveType(obj){
	var leaveType = $(obj).val();
	if(leaveType=="1"||leaveType=="2"||leaveType=="5"||leaveType=="11"||leaveType=="12"){
		//年假，病假，调休，事假，其它（需要根据班次得到请假时间段，选择之后在计算请假时间）
		$(".normalLeave").show();//普通假期
		$(".marriageLeave").hide();//婚假
		$(".prenatalLeave").hide();//产前假
		$(".lactationLeave").hide();//哺乳假
		$(".maternityLeave").hide();//产假
		$(".abortionLeave").hide();//流产假
		$(".bereavementLeave").hide();//丧假
	}else if(leaveType=="3"||leaveType=="9"){
		//婚假，陪产假
		$(".normalLeave").hide();//普通假期
		$(".marriageLeave").show();//婚假
		$(".prenatalLeave").hide();//产前假
		$(".lactationLeave").hide();//哺乳假
		$(".maternityLeave").hide();//产假
		$(".abortionLeave").hide();//流产假
		$(".bereavementLeave").hide();//丧假
	}else if(leaveType=="4"){
		//哺乳假
		$(".normalLeave").hide();//普通假期
		$(".marriageLeave").hide();//婚假
		$(".prenatalLeave").hide();//产前假
		$(".lactationLeave").show();//哺乳假
		$(".maternityLeave").hide();//产假
		$(".abortionLeave").hide();//流产假
		$(".bereavementLeave").hide();//丧假
		//获取小孩数
		var employeeId = $("#relativeemployeeIds").val();
		if(employeeId==""){
			return;
		}
		employeeId = employeeId.split(",")[0];
		$.getJSON(contextPath +'/leaveApplyRegister/getChildren.htm?employeeId='+employeeId,function(data){
              $("#lactationEndDate").val(data.endTime);
              $("#lactationChildrenNum").val(data.childrenNum);
              $("#lactationLeaveHours").val(data.childrenNum+"小时/天");
              if(data.childrenNum>0){
            	 var endT = 9+parseInt(data.childrenNum);
            	 var time1 = "09:00-"+endT+":00";
            	 $("#lactationTime").append("<option value='100'>" + time1 + "</option>");
          		 var startT = 18-parseInt(data.childrenNum);
          		 var time2 = startT+":00-"+"18:00";
          		 $("#lactationTime").append("<option value='200'>" + time2 + "</option>");
              }
         });
	}else if(leaveType=="6"){
		//产前假
		$(".normalLeave").hide();//普通假期
		$(".marriageLeave").hide();//婚假
		$(".prenatalLeave").show();//产前假
		$(".lactationLeave").hide();//哺乳假
		$(".maternityLeave").hide();//产假
		$(".abortionLeave").hide();//流产假
		$(".bereavementLeave").hide();//丧假
	}else if(leaveType=="7"){
		//产假
		$(".normalLeave").hide();//普通假期
		$(".marriageLeave").hide();//婚假
		$(".prenatalLeave").hide();//产前假
		$(".lactationLeave").hide();//哺乳假
		$(".maternityLeave").show();//产假
		$(".abortionLeave").hide();//流产假
		$(".bereavementLeave").hide();//丧假
	}else if(leaveType=="8"){
		//流产假
		$(".normalLeave").hide();//普通假期
		$(".marriageLeave").hide();//婚假
		$(".prenatalLeave").hide();//产前假
		$(".lactationLeave").hide();//哺乳假
		$(".maternityLeave").hide();//产假
		$(".abortionLeave").show();//流产假
		$(".bereavementLeave").hide();//丧假
	}else if(leaveType=="10"){
		//丧假
		$(".normalLeave").hide();//普通假期
		$(".marriageLeave").hide();//婚假
		$(".prenatalLeave").hide();//产前假
		$(".lactationLeave").hide();//哺乳假
		$(".maternityLeave").hide();//产假
		$(".abortionLeave").hide();//流产假
		$(".bereavementLeave").show();//丧假
	}
	
}

//获得普通假期开始时间小时数
function getNoamalLeaveStartHours(){
	var leaveType = $("#leaveType").val();
	var date = $("#normalStartDate").val();
	var employeeId = $("#relativeemployeeIds").val();
	if(employeeId==""){
		JEND.page.alert({type:"error", message:"请选择员工！"});
		return;
	}
	employeeId = employeeId.split(",")[0];
	if(date==""){
		JEND.page.alert({type:"error", message:"请选择请假时间！"});
		return;
	}
	var d = {
	    employeeId:employeeId,
        type:1,
        date:date,
        leaveType:leaveType
    }
	$.getJSON(contextPath +'/leaveApplyRegister/getLeaveHours.htm',d,function(data){
        if(data.code == '0000'){
        	var normalStartTime = $("#normalStartTime");
        	normalStartTime.empty();
        	if(data.list!=null){
        		var list = data.list.split(",");
        		normalStartTime.append("<option>请选择</option>");
        		$.each(list, function(index) {
        			var time = list[index]+":00";
        			normalStartTime.append("<option value= " + time + ">" + time + "</option>");
    			});
        	}
        }else{
            JEND.page.alert({type:"error", message:data.message});
        }
	 })
	
}

//获得普通假期开始时间小时数
function getNoamalLeaveEndHours(){
	var leaveType = $("#leaveType").val();
	var date = $("#normalEndDate").val();
	var employeeId = $("#relativeemployeeIds").val();
	if(employeeId==""){
		JEND.page.alert({type:"error", message:"请选择员工！"});
		return;
	}
	employeeId = employeeId.split(",")[0];
	if(date==""){
		JEND.page.alert({type:"error", message:"请选择请假时间！"});
		return;
	}
	var d = {
	    employeeId:employeeId,
        type:2,
        date:date,
        leaveType:leaveType
    }
	$.getJSON(contextPath +'/leaveApplyRegister/getLeaveHours.htm',d,function(data){
        if(data.code == '0000'){
        	var normalEndTime = $("#normalEndTime");
        	normalEndTime.empty();
        	if(data.list!=null){
        		var list = data.list.split(",");
        		normalEndTime.append("<option>请选择</option>");
        		$.each(list, function(index) {
        			var time = list[index]+":00";
        			normalEndTime.append("<option value= " + time + ">" + time + "</option>");
    			});
        	}
        }else{
            JEND.page.alert({type:"error", message:data.message});
        }
	 })
	
}

//计算婚假，陪产假时间（默认10天）
function calMarriageLeaveDays(){
	var marriageStartDate = $("#marriageStartDate").val();
	var marriageEndDate = $("#marriageEndDate").val();
	if(marriageStartDate!=""&&marriageEndDate==""){
		var startDateFomart = new Date(Date.parse(marriageStartDate.replace(/-/g,  "/"))); 
		var startDate = new Date(Date.parse(marriageStartDate.replace(/-/g,  "/"))); 			
		var len = 10;
		startDate.setDate(startDateFomart.getDate()+len-1);
		$("#marriageEndDate").val(startDate.format("yyyy/MM/dd").replaceAll("/","-"));
		$("#marriageLeaveDays").val(len+"天");
		$("#marriageLeaveDays").attr("leaveDays",len);
	}
	if(marriageStartDate!=""&&marriageEndDate!=""){
		var startDate = new Date(Date.parse(marriageStartDate.replace(/-/g,  "/")));
		var endDate = new Date(Date.parse(marriageEndDate.replace(/-/g,  "/")));
		var days = (endDate.getTime()-startDate.getTime())/3600/1000/24+1;
		$("#marriageLeaveDays").val(days+"天");
		$("#marriageLeaveDays").attr("leaveDays",days);
	}
}

//计算产前假请假天数
function calPrenatalLeaveDays(){
	var prenatalStartDate = $("#prenatalStartDate").val();
	var prenatalEndDate = $("#prenatalEndDate").val();
	if(prenatalStartDate!=""&&prenatalEndDate!=""){
		var startDate = new Date(Date.parse(prenatalStartDate.replace(/-/g,  "/")));
		var endDate = new Date(Date.parse(prenatalEndDate.replace(/-/g,  "/")));
		var days = (endDate.getTime()-startDate.getTime())/3600/1000/24+1;
		if(days>120){
			JEND.page.alert({type:"error", message:"产前假不能多于120天！"});
			return;
		}
		$("#prenatalLeaveDays").val(days+"天");
		$("#prenatalLeaveDays").attr("leaveDays",days);
	}
}

//计算产假请假天数按规则
function calMaternityLeaveDaysByRule(){
	var childrenBirthday = $("#childrenBirthday").val();//出生日期
	var maternityChildrenNum = $("#maternityChildrenNum").val();//子女数量
	if(maternityChildrenNum<=0){
		maternityChildrenNum = 1;
		$("#maternityChildrenNum").val(1);
	}
	var livingState = $("#livingState").val();//生产情况
	if(childrenBirthday!=""&&maternityChildrenNum>0&&livingState!=""){
		//请假开始时间默认是孩子出生日期往前15天
		var birthday = new Date(Date.parse(childrenBirthday.replace(/-/g,  "/")));
		var startDate = new Date(birthday.setDate(birthday.getDate()-15));	
		$("#maternityStartDate").val(startDate.format("yyyy/MM/dd").replaceAll("/","-"));
		var leaveDays = 128+(parseInt(maternityChildrenNum)-1)*15;//默认128天，增加一个孩子+15天
		if(livingState==200){//难产增加15天
			leaveDays = leaveDays+15;
		}
		getExtraMaternityLeaveDays($("#maternityStartDate").val(),leaveDays);
	}
}

//计算产假请假天数
function calMaternityLeaveDays(){
	var maternityStartDate = $("#maternityStartDate").val();//开始日期
	var maternityEndDate = $("#maternityEndDate").val();//结束日期
	if(maternityStartDate!=""&&maternityEndDate!=""){
        var leaveDaysStr = $("#maternityLeaveDays").attr("leaveDays");
        var leaveDays = 0;
        if(leaveDaysStr.indexOf("+") != -1 ){
			var leaveArr=leaveDaysStr.split("+");
			var leavesNum = parseInt(leaveArr[0]);
			leaveDays = leavesNum;
		}else{
			leaveDays = parseInt(leaveDaysStr);
		}
		getExtraMaternityLeaveDays(maternityStartDate,leaveDays);
	}
}

function getExtraMaternityLeaveDays(maternityStartDate,maternityLeaveDays){
	$.getJSON(contextPath +'/leaveApplyRegister/getLeaveCountStartEndTime.htm?leaveDays='+maternityLeaveDays+'&startTime='+maternityStartDate,function(data){
		var startTimeFlag = data.startTimeFlag;
		var endTimeFlag = data.endTimeFlag;
		var addLegalTime=data.addLegalTime;
		var startDate = new Date(Date.parse(startTimeFlag.replace(/-/g,  "/")));
		var endDate = new Date(Date.parse(endTimeFlag.replace(/-/g,  "/")));
		$("#maternityStartDate").val(startDate.format("yyyy/MM/dd").replaceAll("/","-"));
		$("#maternityEndDate").val(endDate.format("yyyy/MM/dd").replaceAll("/","-"));
		if(addLegalTime!=0){
			strLeaves=maternityLeaveDays+"+"+addLegalTime;
		}else{
			strLeaves=maternityLeaveDays;
		}
		$("#maternityLeaveDays").val(strLeaves+"天");
		$("#maternityLeaveDays").attr("leaveDays",strLeaves);
	});
}

//计算流产假请假天数1
function calAbortionLeaveDaysByRule(){
	var abortionStartDate = $("#abortionStartDate").val();//开始日期
	var treatmentCycle = $("#treatmentCycle").val();//妊娠周期
	if(abortionStartDate!=""){
		var startDate = new Date(Date.parse(abortionStartDate.replace(/-/g,  "/")));
		if(treatmentCycle==100){
			//4个月以下，往后推15天
			var endDate = new Date(startDate.setDate(startDate.getDate()+14));	
			$("#abortionEndDate").val(endDate.format("yyyy/MM/dd").replaceAll("/","-"));
			$("#abortionLeaveDays").val(15+"天");
			$("#abortionLeaveDays").attr("leaveDays",15);
		}else if(treatmentCycle==200){
			//4个月及4个月以上，往后推42天
			var endDate = new Date(startDate.setDate(startDate.getDate()+41));	
			$("#abortionEndDate").val(endDate.format("yyyy/MM/dd").replaceAll("/","-"));
			$("#abortionLeaveDays").val(42+"天");
			$("#abortionLeaveDays").attr("leaveDays",42);
		}
	}
}

//计算流产假请假天数1
function calAbortionLeaveDays(){
	var abortionStartDate = $("#abortionStartDate").val();//开始日期
	var abortionEndDate = $("#abortionEndDate").val();//结束日期
	if(abortionStartDate!=""&&abortionEndDate!=""){
		var startDate = new Date(Date.parse(abortionStartDate.replace(/-/g,  "/")));
		var endDate = new Date(Date.parse(abortionEndDate.replace(/-/g,  "/")));
		var days = (endDate.getTime()-startDate.getTime())/3600/1000/24+1;
		$("#abortionLeaveDays").val(days+"天");
		$("#abortionLeaveDays").attr("leaveDays",days);
	}
}

//计算丧假请假天数
function calBereavementLeaveDays(){
	var bereavementStartDate = $("#bereavementStartDate").val();//开始日期
	var relation = $("#relation").val();//亲属关系
	if(bereavementStartDate!=""){
		if(relation==100||relation==200||relation==300){
			//父母，配偶，子女 3天
			var startDate = new Date(Date.parse(bereavementStartDate.replace(/-/g,  "/")));
			var endDate = new Date(startDate.setDate(startDate.getDate()+2));	
			$("#bereavementEndDate").val(endDate.format("yyyy/MM/dd").replaceAll("/","-"));
			$("#bereavementLeaveDays").val(3+"天");
			$("#bereavementLeaveDays").attr("leaveDays",3);
		}else{
			var startDate = new Date(Date.parse(bereavementStartDate.replace(/-/g,  "/")));
			var endDate = new Date(startDate.setDate(startDate.getDate()));	
			$("#bereavementEndDate").val(endDate.format("yyyy/MM/dd").replaceAll("/","-"));
			$("#bereavementLeaveDays").val(1+"天");
			$("#bereavementLeaveDays").attr("leaveDays",1);
		}
	}
}

//计算哺乳假请假天数
function calLactationLeaveDays(){
	var lactationStartDate = $("#lactationStartDate").val();
	var lactationEndDate = $("#lactationEndDate").val();
	if(lactationStartDate!=""&&lactationEndDate!=""){
		var startDate = new Date(Date.parse(lactationStartDate.replace(/-/g,  "/")));
		var endDate = new Date(Date.parse(lactationEndDate.replace(/-/g,  "/")));
		var days = (endDate.getTime()-startDate.getTime())/3600/1000/24+1;
		$("#lactationLeaveDays").val(days+"天");
		$("#lactationLeaveDays").attr("leaveDays",days);
		
	}
}

//计算普通请假天数
function calNormalLeaveDays(){
	var normalStartDate = $("#normalStartDate").val();
	var normalStartTime = $("#normalStartTime").val();
	var normalEndDate = $("#normalEndDate").val();
	var normalEndTime = $("#normalEndTime").val();
	var leaveType = $("#leaveType").val();
	var employeeId = $("#relativeemployeeIds").val();
	if(employeeId==""){
		JEND.page.alert({type:"error", message:"请选择员工！"});
		return;
	}
	employeeId = employeeId.split(",")[0];
	if(normalStartDate!=""&&normalStartTime!=""&&normalEndDate!=""&&normalEndTime!=""){
		var startTime1 = normalStartDate.substring(0,10);
		var startTime2 = normalStartTime.substring(0,2);
		var endTime1 = normalEndDate.substring(0,10);
		var endTime2 = normalEndTime.substring(0,2);
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data:{
				empId:employeeId,
				startTime1:startTime1,
				endTime1:endTime1,
				startTime2:startTime2,
				endTime2:endTime2,
				leaveType:leaveType
			},
			url:contextPath + "/leaveApplyRegister/calculatedLeaveDays.htm",
			success:function(data){
				if(data.success){
					if(leaveType==5){
						$("#normalLeaveDays").val(data.leaveHours+"小时");
					}else{
						$("#normalLeaveDays").val(data.leaveDays+"天");
					}
					$("#normalLeaveDays").attr("leaveDays",data.leaveDays);
					$("#normalLeaveDays").attr("leaveHours",data.leaveHours);
				}
			}
		});
	}
}

//确认登记假期
function confirmRegisterLeave(){
	
	var leaveType = $("#leaveType").val();
	if(leaveType==""){
		JEND.page.alert({type:"error", message:"请选择登记假期类型！"});
		return;
	}
	
	var employeeId = $("#relativeemployeeIds").val();
	if(employeeId==""){
		JEND.page.alert({type:"error", message:"请选择所需登记员工！"});
		return;
	}
	employeeId = employeeId.split(",")[0];
	
	if(leaveType=="1"||leaveType=="2"||leaveType=="5"||leaveType=="11"||leaveType=="12"){
		//年假，病假，调休，事假，其它（需要根据班次得到请假时间段，选择之后在计算请假时间）
	    var normalStartDate = $("#normalStartDate").val();
	    var normalStartTime = $("#normalStartTime").val();
	    var normalEndDate = $("#normalEndDate").val();
	    var normalEndTime = $("#normalEndTime").val();
	    var normalReason = $("#normalReason").val();
	    if(normalStartDate==""||normalStartTime==""||normalEndDate==""||normalEndTime==""){
	    	JEND.page.alert({type:"error", message:"请选择登记假期时间！"});
			return;
	    }
	    if(normalReason==""){
	    	JEND.page.alert({type:"error", message:"登记理由不能为空！"});
			return;
	    }
	    var leaveHours = 0;
	    if(leaveType==5){
	    	leaveHours = $("#normalLeaveDays").attr("leaveHours");
	    }
	    var param = {
	        employeeId:employeeId,
    		leaveType:leaveType,
			startTime:normalStartDate + " " + normalStartTime,
			endTime:normalEndDate + " " + normalEndTime,
			leaveDays:$("#normalLeaveDays").attr("leaveDays"),	
			leaveHours:leaveHours,
			reason:normalReason,
			token:$("#token").val()
	    };
	    registerLeave(param);
	}else if(leaveType=="3"||leaveType=="9"){
		//婚假，陪产假
		var marriageStartDate = $("#marriageStartDate").val();
		var marriageEndDate = $("#marriageEndDate").val();
		var marriageReason = $("#marriageReason").val();
		if(marriageStartDate==""||marriageEndDate==""){
			JEND.page.alert({type:"error", message:"请选择登记假期时间！"});
			return;
		}
		if(marriageReason==""){
	    	JEND.page.alert({type:"error", message:"登记理由不能为空！"});
			return;
		}
		var param = {
	        employeeId:employeeId,
    		leaveType:leaveType,
			startTime:marriageStartDate + " 00:00:01",
			endTime:marriageEndDate + " 23:59:59",
			leaveDays:$("#marriageLeaveDays").attr("leaveDays"),	
			reason:marriageReason,
			token:$("#token").val()
	    };
	    registerLeave(param);
	}else if(leaveType=="4"){
		//哺乳假
		var lactationStartDate = $("#lactationStartDate").val();
		var lactationEndDate = $("#lactationEndDate").val();
		var lactationTime = $("#lactationTime").val();
		var lactationReason = $("#lactationReason").val();
		var lactationChildrenNum = $("#lactationChildrenNum").val();
		if(lactationStartDate==""||lactationEndDate==""||lactationTime==""){
			JEND.page.alert({type:"error", message:"请选择登记假期时间！"});
			return;
		}
		if(lactationReason==""){
			JEND.page.alert({type:"error", message:"登记理由不能为空！"});
			return;
		}
		if(lactationTime==100){
			var startTime = "09:00:00"
			var endTime = (9+parseInt(lactationChildrenNum))+":00:00"
		}else{
			var startTime = (18-parseInt(lactationChildrenNum))+":00:00";
		    var endTime = "18:00:00"
		}
		var param = {
	        employeeId:employeeId,
    		leaveType:leaveType,
			startTime:lactationStartDate +" " + startTime,
			endTime:lactationEndDate +" " + endTime,
			leaveDays:$("#lactationLeaveDays").attr("leaveDays"),	
			reason:lactationReason,
			childrenNum:lactationChildrenNum,
			dayType:lactationTime,
			token:$("#token").val()
	    };
	    registerLeave(param);
	}else if(leaveType=="6"){
		//产前假
		var prenatalStartDate = $("#prenatalStartDate").val();
		var prenatalEndDate = $("#prenatalEndDate").val();
		var prenatalReason = $("#prenatalReason").val();
		if(prenatalStartDate==""||prenatalEndDate==""){
			JEND.page.alert({type:"error", message:"请选择登记假期时间！"});
			return;
		}
		if(prenatalReason==""){
			JEND.page.alert({type:"error", message:"登记理由不能为空！"});
			return;
		}
		var param = {
	        employeeId:employeeId,
    		leaveType:leaveType,
			startTime:prenatalStartDate +" 00:00:01",
			endTime:prenatalEndDate +" 23:59:59",
			leaveDays:$("#prenatalLeaveDays").attr("leaveDays"),	
			reason:prenatalReason,
			token:$("#token").val()
	    };
		registerLeave(param);
	}else if(leaveType=="7"){
		//产假
		var childrenBirthday = $("#childrenBirthday").val();
		var maternityChildrenNum = $("#maternityChildrenNum").val();
		var livingState = $("#livingState").val();
		var maternityStartDate = $("#maternityStartDate").val();
		var maternityEndDate = $("#maternityEndDate").val();
		var maternityReason = $("#maternityReason").val();
		var maternityLeaveDays = $("#maternityLeaveDays").attr("leaveDays");
		
		if(childrenBirthday==""||maternityStartDate==""||maternityEndDate==""){
			JEND.page.alert({type:"error", message:"请选择登记假期时间！"});
			return;
		}
		if(maternityReason==""){
			JEND.page.alert({type:"error", message:"登记理由不能为空！"});
			return;
		}
		if(maternityChildrenNum<=0){
			JEND.page.alert({type:"error", message:"请完善子女信息！"});
			return;
		}
		
		var leavesNote=0;
		//产假字符截取 含+ 则相加天数
		if(maternityLeaveDays.indexOf("+") != -1 ){
			var leaveArr= maternityLeaveDays.split("+");
			var leavesNum = parseInt(leaveArr[0]);
			leavesNote = parseInt(leaveArr[1]);
		}else{
			var leaveArr= maternityLeaveDays.split("+");
			leavesNum = parseInt(leaveArr[0]);
		}
		
		var param = {
			employeeId:employeeId,
			leaveType:leaveType,
			startTime:maternityStartDate+" 00:00:01",
			endTime:maternityEndDate+" 23:59:59",
			leaveDays:leavesNum+leavesNote,
			childrenNum:maternityChildrenNum,
			livingState:livingState,
			reason:maternityReason,
			token:$("#token").val()
		};
		registerLeave(param);
	}else if(leaveType=="8"){
		//流产假
		var abortionStartDate = $("#abortionStartDate").val();
		var abortionEndDate = $("#abortionEndDate").val();
		var abortionReason = $("#abortionReason").val();
		if(abortionStartDate==""||abortionEndDate==""){
			JEND.page.alert({type:"error", message:"请选择登记假期时间！"});
			return;
		}
		if(abortionReason==""){
			JEND.page.alert({type:"error", message:"登记理由不能为空！"});
			return;
		}
		var param = {
			employeeId:employeeId,
			leaveType:leaveType,
			startTime:abortionStartDate+" 00:00:01",
			endTime:abortionEndDate+" 23:59:59",
			leaveDays:$("#abortionLeaveDays").attr("leaveDays"),
			reason:abortionReason,
			token:$("#token").val()
		};
		registerLeave(param);
	}else if(leaveType=="10"){
		//丧假
		var bereavementStartDate = $("#bereavementStartDate").val();
		var relation = $("#relation").val();
		var bereavementEndDate = $("#bereavementEndDate").val();
		var bereavementReason = $("#bereavementReason").val();
		if(bereavementStartDate==""||bereavementEndDate==""){
			JEND.page.alert({type:"error", message:"请选择登记假期时间！"});
			return;
		}
		if(bereavementReason==""){
			JEND.page.alert({type:"error", message:"登记理由不能为空！"});
			return;
		}
		var param = {
			employeeId:employeeId,
			leaveType:leaveType,
			startTime:bereavementStartDate+" 00:00:01",
			endTime:bereavementEndDate+" 23:59:59",
			leaveDays:$("#bereavementLeaveDays").attr("leaveDays"),
			reason:bereavementReason,
			relatives:relation,
			token:$("#token").val()
		};
		registerLeave(param);
	}
}

//登记假期
function registerLeave(param){
	secondConfirmation({
	   "msg":"是否确认登记？",
		sureFn:function(){
			pageLoading(true);//加载动画	
			$.ajax({
				async:true,
				type:'post',
				timeout : 50000,
				dataType:'json',
				data:param,
				url:contextPath + "/leaveApplyRegister/registerLeave.htm",
				success:function(data){
					if(data.code == "true"){
						JEND.page.alert({type:"success", message:data.message});
					}else{
						JEND.page.alert({type:"error", message:data.message});
					}
					$("#token").val(data.data.token);
				},
				error:function(XMLHttpRequest) { 
		        	if(XMLHttpRequest.status=="9999"){
		        		JEND.page.showError("请勿重复提交，请刷新页面重试。");
					}
			    },
				complete:function(){
					pageLoading(false);//关闭动画
				}
			});
		}
	});
}

function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var departName = $("select[name='departId']").find("option:selected").text();
	if(departName=="请选择"){
		departName = "";
	}
	var data = {
		code:$("input[name='code']").val(),
		cnName:$("input[name='cnName']").val(),
		departName:departName,
		leaveType:$("select[name='leaveType']").val(),
		startDate:$("input[name='startDate']").val(),
		endDate:$("input[name='endDate']").val(),
		page:$("#pageNo").val(),
		rows:15
	}
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "leaveApplyRegister/getRegisterLeaveListByPage.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].departName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].positionName+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].typeOfWork+"</td>";
				var leaveType = "";
				if(response.rows[i].leaveType==1){
					leaveType = "年假";
				}else if(response.rows[i].leaveType==2){
					leaveType = "病假";
				}else if(response.rows[i].leaveType==3){
					leaveType = "婚假";
				}else if(response.rows[i].leaveType==4){
					leaveType = "哺乳假";
				}else if(response.rows[i].leaveType==5){
					leaveType = "调休";
				}else if(response.rows[i].leaveType==6){
					leaveType = "产前假";
				}else if(response.rows[i].leaveType==7){
					leaveType = "产假";
				}else if(response.rows[i].leaveType==8){
					leaveType = "流产假";
				}else if(response.rows[i].leaveType==9){
					leaveType = "陪产假";
				}else if(response.rows[i].leaveType==10){
					leaveType = "丧假";
				}else if(response.rows[i].leaveType==11){
					leaveType = "事假";
				}else if(response.rows[i].leaveType==12){
					leaveType = "其他";
				}
				html += "<td style='text-align:center;'>"+leaveType+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].startTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].endTime+"</td>";
				if(response.rows[i].leaveType==5){
					html += "<td style='text-align:center;'>"+response.rows[i].leaveDays+"天("+response.rows[i].leaveHours+"小时)</td>";
				}else{
					if(response.rows[i].leaveType==7 && response.rows[i].leaveHours!=null){
						html += "<td style='text-align:center;'>"+response.rows[i].leaveDays+"+"+response.rows[i].leaveHours+"</td>";
					}else{
						html += "<td style='text-align:center;'>"+response.rows[i].leaveDays+"</td>";
					}
				}
				html += "<td style='text-align:center;'>"+response.rows[i].reason+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].createTime+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].createUser+"</td>";
				html += "</tr>";
			}
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest){  
			pageLoading(false);//关闭动画
        }
	});
}