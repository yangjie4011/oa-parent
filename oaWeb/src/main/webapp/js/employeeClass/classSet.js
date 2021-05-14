$(function(){
	$("#start").bind('input propertychange', function() {  
		initDuration();
    }); 
	$("#end").bind('input propertychange', function() {  
		initDuration();
    }); 
	
})

function initDuration(){
	if($("#start").val()!=""&&$("#end").val()!=""){
		var startTime = new Date();
		startTime.setMinutes(0);
		startTime.setSeconds(0);
		startTime.setMilliseconds(0);
		startTime.setHours(parseInt($("#start").val().substring(0,2)));
		if(parseInt($("#start").val().substring(3,5)) > 30) {
			var newHours = parseInt($("#start").val().substring(0,2)) + 1;
			startTime.setHours(newHours);
			startTime.setMinutes(0);
			$("#start").val(newHours + ":00");
		} else if(parseInt($("#start").val().substring(3,5)) > 0) {
			startTime.setMinutes(30);
			$("#start").val($("#start").val().substring(0,2) + ":30");
		}
        var endTime = new Date();
        endTime.setSeconds(0);
        endTime.setMinutes(0);
        endTime.setMilliseconds(0);
        endTime.setHours(parseInt($("#end").val().substring(0,2)));
        if(parseInt($("#end").val().substring(3,5)) < 30) {
        	endTime.setMinutes(0);
        	$("#end").val($("#end").val().substring(0,2) + ":00");
        } else if(parseInt($("#end").val().substring(3,5)) >= 30) {
        	endTime.setMinutes(30);
        	$("#end").val($("#end").val().substring(0,2) + ":30");
        }
        var mustAttnTime = 0;
        if(startTime.getTime()>endTime.getTime()){
        	mustAttnTime = parseInt( 24 - startTime.getHours());
        	 if(startTime.getMinutes() == 30) {
        		 mustAttnTime = mustAttnTime - 0.5;
        	 }
        	 mustAttnTime = mustAttnTime + parseInt($("#end").val().substring(0,2));
    		 if(parseInt($("#end").val().substring(3,5)) >= 30) {
    			 mustAttnTime = mustAttnTime + 0.5;
    		 }
        } else if(startTime.getTime() <= endTime.getTime()){
        	mustAttnTime= parseInt((endTime.getTime()-startTime.getTime())/(1800*1000))/2;
        } 
        if(mustAttnTime >= 0) {
    		if(mustAttnTime >=10 && mustAttnTime <16) {
    			mustAttnTime = mustAttnTime - 2;
    		} else if(mustAttnTime > 5&& mustAttnTime<10) {
    			mustAttnTime = mustAttnTime -1;
    		}
        	$("#mustAttnTime").attr("mustAttnTime",mustAttnTime);
        	$("#mustAttnTime").text(mustAttnTime+" 小时");
        } 
    }
}

//新增班次
function addClassSet(obj){
	if($("#name").val().trim()==""){
		OA.titlePup('班次名称不能为空！','lose');
		return;
	}
	if($("#name").val().trim().length>1){
		OA.titlePup('班次名称只能是一个字！','lose');
		return;
	}
	if($("#start").val().trim()==""){
		OA.titlePup('上班时间不能为空！','lose');
		return;
	}
	if($("#end").val().trim()==""){
		OA.titlePup('下班时间不能为空！','lose');
		return;
	}
	var isInterDay = 0;
	var outStart = parseInt($("#start").val().substring(0,2));
    var outStartT = parseInt($("#start").val().substring(3,5));
    
    var outEnd = parseInt($("#end").val().substring(0,2));
    var outEndT = parseInt($("#end").val().substring(3,5));
    
    var outS = parseInt(outStart) * 60 + parseInt(outStartT);
    var outE = parseInt(outEnd) * 60 + parseInt(outEndT);
	if(outS > outE){
		isInterDay = 1;
	} 
	OA.twoSurePop({
		tips:'确认新增吗？',
		sureFn:function(){
		    	OA.pageLoading(true);
				$.ajax({
					async:true,
					type:'post',
					dataType:'json',
					data:{
						name:$("#name").val(),
						startTime:$("#start").val(),
						endTime:$("#end").val(),
						isInterDay:isInterDay,
						mustAttnTime:$("#mustAttnTime").attr("mustAttnTime"),
						token:$("#token").val()
					},
					url:contextPath + "/classSetting/save.htm",
					success:function(data){
						if(data.success){
							window.location.href=contextPath + "/employeeClass/index.htm";
						}else{
							OA.pageLoading(false);
							OA.titlePup(data.message,'lose');
						}
					}
				});
		},
		cancelFn:function(){
			
		}
	})
}