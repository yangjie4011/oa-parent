<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<%@include file="../common/common.jsp"%>
<link rel="icon" href="data:;base64,=">
<title>员工考勤异常明细</title>
    <meta charset="UTF-8">
</head>


<body class="b-f2f2f2 mt-160">
    <div class="oa-timecard">
        <header>
            <h1 class="clearfix"><a href="javascript:window.history.go(-1)" class="goback fl"><i class="back sr"></i></a>
                <p>员工考勤明细</p>
            </h1>
            <div class="head-list">
                <p class="q">${date }</p>
                <p class="w">${employeeName }<span>${departName }</span></p>
                <p class="e">${showType }共<spam>${count }</span></p>
                <input type="hidden" id="employeeId" value="${employeeId }">
                <input type="hidden" id="date" value="${date }">
                <input type="hidden" id="type" value="${type }">
                <input type="hidden" id="timeType" value="${timeType }">
                <input type="hidden" id="workTypeName" value="${workTypeName }">
            </div>
        </header>
        <ul class="punchtime">
            <!-- <li>
                <div class="abnormalIntro">
                    <p class="date">2017-09-21</p>
                    <p class="name">
                        <span>考勤时间</span>
                        <span><i>上</i><em>09:15:23</em></span>
                        <span><i>下</i><em>09:15:23</em></span>
                    </p>
                    <em class="late">迟到15分钟</em>
                </div>
            </li> -->
        </ul>
    </div>
    <script type="text/javascript">
    $(function(){
    	getAttnList();
    })
    var getAttnList = function(){
    	
    	var date = $("#date").val();
    	var type = $("#type").val();
    	var timeType = $("#timeType").val();
    	var employeeId = $("#employeeId").val();
    	
    	$.ajax({
    	    type:"POST",
    		url : contextPath + "/empAttn/getEmpAttnTimesDetail.htm",
    		data : {date:date,type:type,timeType:timeType,employeeId:employeeId},
    		dataType : 'json',
    		success : function(response) {
    			$(".punchtime").empty();
    			if(''== response || response == null ) {
    				$(".oa-timecard").append('<div class="null pf"><div class="img p3"></div><p class="a">考勤正常</p>'+
                    '<p class="b">您的考勤正常，请继续保持哦~</p></div>');
    			} else {
    				var li = "";
    				$.each(response,function(index,item){
    					
    					li= li+'<li><div class="abnormalIntro"><p class="date attnDate">'+item.attnDate+
    					'</p><p class="name clearfix"><span>考勤时间</span><span><i>上</i><em class="startWorkTime">'+(item.startWorkTimeStr==''?"空卡":item.startWorkTimeStr)+
    					'</em></span><span><i>下</i><em class="endWorkTime">'+(item.endWorkTimeStr==''?"空卡":item.endWorkTimeStr)+
    					'</em></span></p><em class="late">'+showAttnError(item)+
    					'</em></div></li>';
    				})
    				$(".punchtime").append(li);
    			}
    		}
    	 });
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
    			result = result + "旷工"+showTimeDynamic(item.absenteeismTime);
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

    var showTimeDynamic = function(time){
    	var result;
    	if(time<60){
    		result = ( Math.round(time*100)/100)+"分钟;"
    	}else if(time>=60 && time <240){
    		result = ( Math.round(time/60*100)/100)+"小时;";
    	}else if(time>=240){
    		result = ( Math.round((time/60)/8*100)/100)+"天;";
    	}
    	return result;
    }
    </script>
</body>
</html>