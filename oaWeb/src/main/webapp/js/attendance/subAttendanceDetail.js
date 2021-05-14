$(function() {
	getMyAttendance();
	
	var startDate = new Date();//开始时间
	startDate.setDate(1); //第一天
	if(startDate.format("yyyy-MM-dd") == $("#subDetailForm #startTime").val()){//开始时间是本月第一天
		
	}else{
		$(".selMon").empty();
		$(".attenMonthShow").text("上月出勤");
		var newDiv = '<span class="thisMonth" id="lastMonth">上月</span>'+
            '<span class="lastMonth" style="display:none" id="thisMonth">本月</span>'+
            '<ul class="lastMonthUl" style="display:none" id="thisMonthUl">'+
            '<li><span>本月</span></li></ul>';
		$(".selMon").append(newDiv);
	}
	
	$(".thisMonth,.lastMonthUl,.lastMonth").click(function(){
		var thisMonth = $(".thisMonth");
		var lastMonthUl = $(".lastMonthUl");
		var lastMonth = $(".lastMonth");
        var id =$(this).attr("id");
		if(thisMonth.is(':hidden') || lastMonthUl.is(':hidden')){//有一个是影藏的
			
			lastMonth.hide();
			thisMonth.show();
			lastMonthUl.show();
			return;
		}
		
		if(thisMonth.is(':visible') && lastMonthUl.is(':visible')){//有两个是显示的
			var startDate = new Date();//开始时间
			var endDate = new Date();//结束时间
			
			startDate.setDate(1); //第一天
			
			thisMonth.hide();
			lastMonth.hide();
			lastMonthUl.hide();
			if(id=="thisMonth" || id=="thisMonthUl"){
				$(".thisMonth").show();//查询本月
			}else if(id=="lastMonth" || id=="lastMonthUl"){
				startDate.setMonth(startDate.getMonth() - 1);
				endDate = new Date(startDate);
				endDate.setMonth(endDate.getMonth()+1);
				endDate.setDate(0);
				$(".lastMonth").show();//查询上个月
			}

			$("#subDetailForm #startTime").val(startDate.format("yyyy-MM-dd"));
			$("#subDetailForm #endTime").val(endDate.format("yyyy-MM-dd"));
			$("#subDetailForm").attr("action",contextPath + "/empAttn/subIndex.htm");
			$("#subDetailForm").submit();
		}
		
	})
});

var getMyAttendance = function(){
	$.ajax({
	    type:"POST",
		url : contextPath + "/empAttn/getAttStatisticsList.htm?flag=attendance",
		data : $("#subDetailForm").serialize(),
		dataType : 'json',
		success : function(response) {
			$(".punchtime").empty();
			if(''== response || response == null ) {
				$(".oa-timecard").append('<div class="null"><div class="img p3"></div><p class="a">考勤正常</p>'+
                '<p class="b">您的考勤正常，请继续保持哦~</p></div>');
			} else {
				var li0 = "";
				$.each(response,function(index,response){
					var attnStatistics = response.attnStatistics;
					var attnWorkHoursList = response.attnWorkHoursList;
					var li = "";
					li= li+'<li isClick="0"><div class="abnormalIntro"><p class="date attnDate">'+attnStatistics.attnDate+
					'</p>';
					$.each(attnWorkHoursList,function(i,attnWorkHours){
						var title = "考勤时间";
						if(attnWorkHours.dataType==60){
							title = "请假时间";
						}
						//绝对修改
						if(attnWorkHours.dataType==3){
							isHaveAbsoluteUpdate = false;
							li = "";
							li= li+'<li isClick="1"><div class="abnormalIntro"><p class="date attnDate">'+attnStatistics.attnDate+
							'</p>';
							li= li+'<p class="name clearfix"><span>'+title+'</span><span><i>上</i><em class="startWorkTime">'+(attnWorkHours.startTimeStr==''?"空卡":attnWorkHours.startTimeStr)+
							'</em></span><span><i>下</i><em class="endWorkTime">'+(attnWorkHours.endTimeStr==''?"空卡":attnWorkHours.endTimeStr)+
							'</em></span></p>';
							return false;
						}
						li= li+'<p class="name clearfix"><span>'+title+'</span><span><i>上</i><em class="startWorkTime">'+(attnWorkHours.startTimeStr==''?"空卡":attnWorkHours.startTimeStr)+
						'</em></span><span><i>下</i><em class="endWorkTime">'+(attnWorkHours.endTimeStr==''?"空卡":attnWorkHours.endTimeStr)+
						'</em></span></p>';
					});
					li= li+'<em class="late">'+showAttnError(attnStatistics)+
					'</em></div><i class="right-icon"></i></li>';
					li0 +=li;
				})
				$(".punchtime").append(li0);
				//绑定点击事件
				$("li").click(function(){
					var li = $(this);
					if(li.attr("isClick")==0){
						liClick(li);
					}
				});
			}
        	
		}
	 });
}

var liClick = function(li){
	var attnDate = li.find(".attnDate").text();
	var startWorkTime = li.find(".startWorkTime").text();
	var endWorkTime = li.find(".endWorkTime").text();
	
	$("#toApplyForm #attnDate").val(attnDate);
	$("#toApplyForm #startWorkTime").val(startWorkTime);
	$("#toApplyForm #endWorkTime").val(endWorkTime);
	$("#toApplyForm").attr("action",contextPath + "/empApplicationAbnormalAttendance/myAttnToIndex.htm");
	$("#toApplyForm").submit();
}

var showAttnError = function(item){
	var result = "";
	var workTypeName = $("#workTypeName").val();
	//迟到，早退，缺勤，旷工
	if(workTypeName=='standard'){
		//60分钟内是迟到，超过60分钟，3小时内，算旷工0.5天，超过3小时，是旷工1天,没有迟到早退一说;
		
		if(item.comeLateTime != '' && item.comeLateTime<60){
			result = result + "迟到"+item.comeLateTime+"分钟;";
		}
		
	    if(item.leftEarlyTime != '' && item.leftEarlyTime<60){
			result = result + "早退"+item.leftEarlyTime+"分钟;";
		}
	    
	    if(item.absenteeismTime != '' && item.absenteeismTime>=60){
			result = "旷工"+showTimeDynamic(item.absenteeismTime,item.mustAttnTime);
		}
	    
	    if(item.absenteeismTime == '' && item.leftEarlyTime == '' && item.comeLateTime == ''){
			result = "非工作日打卡;";
	    }
	    
	}else if(workTypeName == 'comprehensive'){
	    if(item.lackAttnTime != ''){//数据库存放的是小时
	    	var hour_ ;
	    	var minute_;
	    	if(item.lackAttnTime<1){
	    		result = "缺勤"+(item.lackAttnTime*60)+"分钟;"
	    	}else if(item.lackAttnTime>=1 && item.lackAttnTime<3){
	    		hour_ = parseInt(item.lackAttnTime);
	    		minute_ = (item.lackAttnTime-hour_)*60;
	    		
	    		result = "缺勤"+hour_+"小时";
	    		if(minute_ != '' && minute_ !=0){
	    			result = result+minute_.toFixed(0)+"分钟;";
	    		}
	    	}else if(item.lackAttnTime == 3){
	    		result = "缺勤0.5天";
	    	}else if(item.lackAttnTime>3 && item.lackAttnTime<8){
	    		hour_ = parseInt(item.lackAttnTime);
	    		minute_ = (item.lackAttnTime-hour_)*60;
	    		
	    		result = "缺勤"+hour_+"小时";
	    		if(minute_ != '' && minute_ !=0){
	    			result = result+minute_.toFixed(0)+"分钟;";
	    		}
	    	}else if(item.lackAttnTime>=8){
	    		result = "缺勤1天";
	    	}
	    	
		}else if(item.lackAttnTime == '0'){
			result = result + "非工作日打卡;";
		}
	}
	return result;
}

var showAttnStatus = function(status){
	if(status==0 || status=='0'){
		status='正常';
	}else{
		status='异常';
	}
	return status;
}

var showTimeDynamic = function(time,mustAttnTime){
	var result;
	if(mustAttnTime>=8){
		mustAttnTime = 8;
	}
	if(time<60){
		result = ( Math.round(time*100)/100)+"分钟;"
	}else if(time>=60 && time <240){
		result = ( Math.round(time/60*100)/100)+"小时;";
	}else if(time>=240){
		result = ( Math.round((time/60)/8*100)/100)+"天;";
	}
	return result;
}