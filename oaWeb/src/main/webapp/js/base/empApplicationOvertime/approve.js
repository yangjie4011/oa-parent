$(function(){
	//监控开始结束时间计算天数
	$("#start-time").bind('input propertychange', function() {  
		initDuration();
    }); 
	$("#end-time").bind('input propertychange', function() {  
		initDuration();
    }); 
})
//拒绝
function refuse(obj){
	OA.twoSurePop({
		tips:'确认拒绝吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:{processInstanceId:$(obj).attr("id"),approvalReason:$("#approvalReason").val(),token:$("#token").val()},
				url:contextPath + "/empApplicationOvertime/refuse.htm",
				success:function(data){
					if(data.success){
						clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm");
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				},
				complete:function(XMLHttpRequest,status){
		             if(status=="timeout"){
		            	 clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm");
		             }
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}
//通过
function pass(obj){
	OA.twoSurePop({
		tips:'确认同意吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:{processInstanceId:$(obj).attr("id"),approvalReason:$("#approvalReason").val(),token:$("#token").val()},
				url:contextPath + "/empApplicationOvertime/pass.htm",
				success:function(data){
					if(data.success){
						clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm");
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				},
				complete:function(XMLHttpRequest,status){
		             if(status=="timeout"){
		            	 clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm");
		             }
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}
function addActualTime(obj){
    if($("#start-time").val()==""){
    	OA.titlePup('实际开始时间不能为空！','lose');
    	return;
    }
    if($("#end-time").val()==""){
    	OA.titlePup('实际结束时间不能为空！','lose');
    	return;
    }
    if($("#actualDuration").val() == "" || $("#actualDuration").val() < 1) {
    	OA.titlePup('加班时间必须不小于1小时','lose');
    	return;
    }
	OA.twoSurePop({
		tips:'确认同意吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:{
					processInstanceId:$(obj).attr("id"),
					actualStartTime:addDate($(obj).attr("applyDate"),$("#start-time").attr("data-nextday"))+" "+$("#start-time").val()+":00",
					actualEndTime:addDate($(obj).attr("applyDate"),$("#end-time").attr("data-nextday"))+" "+$("#end-time").val()+":00",
					actualDuration:$("#actualDuration").val(),
					token:$("#token").val()
				},
				url:contextPath + "/empApplicationOvertime/addActualTime.htm",
				success:function(data){
					if(data.success){
						clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm?current=1");
					}else{
						$("#token").val(data.token);
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				},
				complete:function(XMLHttpRequest,status){
		             if(status=="timeout"){
		            	 clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm?current=1");
		             }
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}

function initDuration(){
	if($("#start-time").val()!=""&&$("#end-time").val()!=""){
		var outStart = parseInt($("#start-time").val().substring(0,2));
        var outStartT = parseInt($("#start-time").val().substring(3,5));
        
        var outEnd = parseInt($("#end-time").val().substring(0,2));
        var outEndT = parseInt($("#end-time").val().substring(3,5));
        
        var outS = parseInt(outStart) * 60 + parseInt(outStartT);
        var outE = parseInt(outEnd) * 60 + parseInt(outEndT);
		if((outS == outE)&&($("#start-time").attr("data-nextday")==$("#end-time").attr("data-nextday"))){
			OA.titlePup('开始时间不可等于结束时间！','lose');
			$("#end-time").val("");
			$("#duration").attr("duration","");
        	$("#duration").text("");
        	$("#actualDuration").val(0);
			return ;
		} 
	    var startTime = new Date(Date.parse($(".r").attr("applyDate").replace(/-/g,  "/"))); 
	    startTime.setDate(startTime.getDate()+parseInt($("#start-time").attr("data-nextday"))); 
		startTime.setMinutes(outStartT);
		startTime.setSeconds(0);
		startTime.setMilliseconds(0);
		startTime.setHours(parseInt($("#start-time").val().substring(0,2)));
		
		var endTime = new Date(Date.parse($(".r").attr("applyDate").replace(/-/g,  "/")));
		endTime.setDate(endTime.getDate()+parseInt($("#end-time").attr("data-nextday"))); 
		endTime.setMinutes(outEndT);
		endTime.setSeconds(0);
        endTime.setMilliseconds(0);
        endTime.setHours(parseInt($("#end-time").val().substring(0,2)));
     	$.ajaxSettings.async = false;  
     	var date_type = 0;
   	    $.getJSON(contextPath +'/empApplicationOvertime/getEmpClassTime.htm?classDate='+$(".r").attr("applyDate"),function(data){
   	    	date_type = data.date_type;//0-正常工作日，1-正常休息日，2-法定节假日，3-法定调休日
   	    	if(data.endTime!=""){
	        	  var stTime = parseInt(data.endTime.substring(0,2))+1;
	        	  var stTime = new Date(Date.parse($(".r").attr("applyDate").replace(/-/g,  "/"))); 
	        	  stTime.setMinutes(parseInt(data.endTime.substring(3,5)));
	        	  stTime.setSeconds(0);
	        	  stTime.setMilliseconds(0);
	        	  stTime.setHours(parseInt(data.endTime.substring(0,2))+1);
	        	  //stTime开始时间的天数和申请加班的日期不一致，开始时间控制要默认到次日
	        	  if(date_type!=2){
	        		  if(stTime.format("yyyy/MM/dd").replaceAll("/","-")!=$(".r").attr("applyDate")){
		        		  $("#start-time").attr("data-nextday","1");
		        	  }
	        	  }
	        	  //跨天
	        	  if(data.isInterDay=="1"){
	        		  stTime.setDate(stTime.getDate()+1); 
	        	  }
	        	  //判断
	        	  var nowStTime = parseInt($("#start-time").val().substring(0,2));
	        	  if(stTime.getTime() > startTime.getTime()) {//判断有问题，要转成date类型比较
	        		  if(date_type=="0"){
	        			  startTime = stTime;
	        			  $("#start-time").val(stTime.format("hh:mm"));
	        		  }
	        	  } else {
	        		    startTime.setHours(parseInt($("#start-time").val().substring(0,2)));
						if(parseInt($("#start-time").val().substring(3,5)) > 30) {
							var newHours = parseInt($("#start-time").val().substring(0,2)) + 1;
							startTime.setHours(newHours);
							startTime.setMinutes(0);
						} else if(parseInt($("#start-time").val().substring(3,5)) > 0) {
							startTime.setMinutes(30);
						}
					    $("#start-time").val(startTime.format("hh:mm"));
	        	  }
	          }else{
	        	  startTime.setHours(parseInt($("#start-time").val().substring(0,2)));
	        	  if(parseInt($("#start-time").val().substring(3,5)) > 30) {
	        		    var newHours = parseInt($("#start-time").val().substring(0,2)) + 1;
						startTime.setHours(newHours);
						startTime.setMinutes(0);
					} else if(parseInt($("#start-time").val().substring(3,5)) > 0) {
						startTime.setMinutes(30);
					}
	        	    $("#start-time").val(startTime.format("hh:mm"));
	          }
        });
       
        if(parseInt($("#end-time").val().substring(3,5)) < 30) {
        	endTime.setMinutes(0);
        } else if(parseInt($("#end-time").val().substring(3,5)) >= 30) {
        	endTime.setMinutes(30);
        }
        $("#end-time").val(endTime.format("hh:mm"));
        var durationValue = 0;
        if(date_type == "0" && parseInt($("#end-time").val().substring(0,2)) > 9 && parseInt($("#end-time").attr("data-nextday"))==1) {
	   		 durationValue = parseInt(durationValue) + 9;
	   		 $("#end-time").val("09:00");
	   		 endTime.setHours(parseInt($("#end-time").val().substring(0,2)));
   	    } 
        
        durationValue= parseInt((endTime.getTime()-startTime.getTime())/(1800*1000))/2;

        if(durationValue > 0) {
        	//非工作日10减2,5减1
        	if(date_type == "1"||date_type == "2") {
        		if(durationValue >=10 ) {
        			durationValue = durationValue - 2;
        		} else if(durationValue >=5) {
        			durationValue = durationValue -1;
        		}
        	}
        	$("#duration").attr("duration",durationValue);
        	$("#duration").text(durationValue+" 小时");
        	$("#actualDuration").val(durationValue);
        } else {
        	if(date_type == "0") {
        		OA.titlePup('延时申请时间须要大于半小时！','lose');
        		$("#isWork").val(1);
        	} else {
        		OA.titlePup('延时申请时间须要大于一小时！','lose');
        		$("#isWork").val(0);
        	}
        	$("#duration").attr("duration",0);
        	$("#duration").text(0+" 小时");
        	$("#actualDuration").val(durationValue);
        }
    }
}


function toRuProcdefHtm(){
	window.location.href=contextPath + "/ruProcdef/my_examine.htm?urlType=1";
}

