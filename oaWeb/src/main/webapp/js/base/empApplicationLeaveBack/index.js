$(function(){
	//审批后 已完成 页面时长显示赋值
	
	//日历初始化
	var calendar = new lCalendar();
	if($("#leaveType").val()==1||$("#leaveType").val()==2||$("#leaveType").val()==5
			||$("#leaveType").val()==11||$("#leaveType").val()==12){
		calendar.init({
	        'trigger': 'start_time',
	        'type': 'date',
	        'callback':function(t){
	        	getHour(t);
	        },
		'startDate':$("#startTimeBefor").val()
	    });
	}else{
		calendar.init({
	        'trigger': 'start_time',
	        'type': 'date'
	    });
	}
    //监控开始结束时间计算天数
	$("#start_time").bind('input propertychange', function() {
		var leaveType = $("#leaveType").val();
		if(leaveType==1||leaveType==2||leaveType==5
				||leaveType==11||leaveType==12){			
			if($(this).val().indexOf(" ")<0){
				return;
			}
			var start_date = $("#start_time").val().trim();
			var end_date = $("#end_time").text().trim();
			var startTime1 = start_date.substring(0,10);
			var startTime2 = start_date.substring(11,13);
			var endTime1 = end_date.substring(0,10);
			var endTime2 = end_date.substring(11,13);
			if(ckdate(start_date,end_date)){
				$.ajax({
		    		async:false,
		    		type:'post',
		    		dataType:'json',
		    		data:{
		    			startTime1:startTime1,
		    			endTime1:endTime1,
		    			startTime2:startTime2,
		    			endTime2:endTime2,
		    			leaveType:leaveType
		    		},
		    		url:contextPath + "/empApplicationLeave/calculatedLeaveDays.htm",
		    		success:function(data){
		    			if(data.success){
		    				if(leaveType==5){
		    					$("#back_leaveDays").text(data.leaveDays+"天");
		    		         	var actual_leaveHours = $("#leaveHours").val()-data.leaveHours;
		    		         	var actual_leaveDays = $("#leaveDays").val()-data.leaveDays;
		    		         	$("#actual_leaveDays").text(actual_leaveDays+" 天");
		    				}else{
		    					$("#back_leaveDays").text(data.leaveDays+"天");
		    					//$("#actual_end_time").text(start_date);
		    		         	var back_leaveDays = $("#leaveDays").val()-data.leaveDays;
		    		         	$("#actual_leaveDays").text(back_leaveDays+" 天");
		    				}
		    				$("#back_leaveDays").attr("leaveDays",data.leaveDays);
		    				$("#back_leaveDays").attr("leaveHours",data.leaveHours);
		    			}
		    		}
		    	});
			}else{
				$(this).val("");
			}
		}else{
			//js计算整天
			var start_date = $("#start_time").val().trim();
		    var end_date = $("#end_time").text().trim();
		    if(leaveType==10){					//丧假单独做限制
		    	$.ajax({
		    		async:false,
		    		type:'post',
		    		dataType:'json',
		    		data:{
		    			date:start_date,
		    			type:end_date,
		    			leaveType:leaveType
		    		},
		    		url:contextPath + "/empApplicationLeave/countLeave10.htm",
		    		success:function(data){
		    			if(data.code == '0000'){
		    				$("#back_leaveDays").text(data.days+" 天");
		    				$("#back_leaveDays").attr("leaveDays",data.days);
		    	        }else{
		    	            OA.titlePup(data.message,'lose');
							OA.pageLoading(false);
		    	        }
		    		}
		    		});
		    }else if(start_date!=""&&end_date!=""){
		    	var startDate = new Date(Date.parse(start_date.replace(/-/g,  "/"))); 
		    	var endDate = new Date(Date.parse(end_date.replace(/-/g,  "/"))); 
		    	if(startDate.getTime()>endDate.getTime()){
		    		$("#back_leaveDays").text(0+" 天");
		            $("#back_leaveDays").attr("leaveDays",0);
		     	    OA.titlePup('开始时间不能大于结束时间！','lose');
		        }else{
		         	var length = 0;
		         	while(true){
		         		length = length+1;
		         		startDate.setDate(startDate.getDate()+1);
		         		if(startDate.getTime()>endDate.getTime()){
		         			break;
		         		}
		         	}
		         	//$("#actual_end_time").text(start_date);
		         	var back_leaveDays = $("#leaveDays").val()-length;
		         	$("#actual_leaveDays").text(back_leaveDays+" 天");
		         	$("#back_leaveDays").text(length+" 天");
		            $("#back_leaveDays").attr("leaveDays",length);
		         }
		    }
		}
    }); 
})

function save(obj){
    if($("#start_time").val().trim()==""){
		OA.titlePup('未选择销假开始时间','lose');
		return;
	}
    var startTimeBefor = "";//原假期开始时间
    var endTimeBefor = "";//原假期结束时间
	var startTime = "";//销假开始时间
	var endTime = "";//销假开始时间
	var leaveDays = 0;
	var leaveHours = 0;
	if($("#leaveType").val()==1||$("#leaveType").val()==2
			||$("#leaveType").val()==5||$("#leaveType").val()==11
			||$("#leaveType").val()==12||$("#leaveType").val()==4){
		startTimeBefor = $("#startTimeBefor").val().substring(0,16);
		endTimeBefor = $("#endTimeBefor").val().substring(0,16);
		startTime = $("#start_time").val().trim();
		endTime = $("#end_time").text().trim()+":00";
	}else if($("#leaveType").val()==3||$("#leaveType").val()==6
			||$("#leaveType").val()==7||$("#leaveType").val()==8
			||$("#leaveType").val()==9||$("#leaveType").val()==10){
		startTimeBefor = $("#startTimeBefor").val().substring(0,10);
		endTimeBefor = $("#endTimeBefor").val().substring(0,10);
		startTime = $("#start_time").val().trim()+" 00:00:00";
		endTime = $("#end_time").text().trim()+" 23:59:59";
	}
	
	if($("#leaveType").val()==5){
		leaveDays = $("#back_leaveDays").attr("leaveDays");
		leaveHours = $("#back_leaveDays").attr("leaveHours");
	}else{
		leaveDays = $("#back_leaveDays").attr("leaveDays");
	}
	if(leaveDays==0){
		OA.titlePup('该时间段无法销假请重新选择销假时间','lose');
		return;
	}
	
	$.ajax({
		async:true,
		type:'post',
		timeout : 5000,
		dataType:'json',
		data:{
			leaveId:$(obj).attr("leaveId"),
			startTime:$("#startTimeBefor").val(),
			endTime:startTime,
		},
		url:contextPath + "/empApplicationLeaveBack/getActualEndTime.htm",
		success:function(data){
			var tips = '原假期起止时间\<br/>'
				+startTimeBefor+'至'+endTimeBefor+'\<br/>'
				+$("#leaveDays").val()+'天'+'\<br/>'
			if(($("#leaveDays").val()-leaveDays) > 0){
				tips+='销假后假期起止时间\<br/>'
				+startTimeBefor+'至'+data.endTime+'\<br/>'
				+($("#leaveDays").val()-leaveDays)+'天'
			}	
			if(data.success=="true"){
				OA.twoSurePop({
					tips:tips,
					sureFn:function(){
						OA.pageLoading(true);
						$.ajax({
							async:true,
							type:'post',
							timeout : 5000,
							dataType:'json',
							data:{
								leaveId:$(obj).attr("leaveId"),
								startTime:startTime,
								endTime:endTime,
								leaveDays:leaveDays,
								leaveHours:leaveHours,
								token:$("#token").val()
							},
							url:contextPath + "/empApplicationLeaveBack/saveAbolishLeave.htm",
							success:function(data){
								if(data.success){
									window.location.href=contextPath + "/runTask/index.htm";
								}else{
									OA.titlePup(data.message,'lose');
									OA.pageLoading(false);
								}
							},
							complete:function(XMLHttpRequest,status){
					             if(status=="timeout"){
					            	 window.location.href=contextPath + "/runTask/index.htm";
					             }
							}
						});
					},
					cancelFn:function(){
						
					}
				})
			}else{
				OA.titlePup(data.msg,'lose');
			}
		}
	});
	
}


//判断日期，时间大小  
function ckdate(startDate, endDate) {
	if(startDate.indexOf(" ")<0||endDate.indexOf(" ")<0){
		return;
	}
	if (startDate.length > 0 && endDate.length > 0) {
		var allStartDate = new Date(Date.parse(startDate.replace(/-/g,"/")));
		var allEndDate = new Date(Date.parse(endDate.replace(/-/g,"/")));
		if (allStartDate.getTime() >= allEndDate.getTime()) {
			OA.titlePup("结束时间必需大于开始时间");
			return false;
		} else {
			return true;
		}
	} else {
		OA.titlePup("时间不能为空");
		return false;
	}
}

function getHour(t,leaveType){//查询请假时间接口返回小时
	var dom = _this.trigger;
	var leaveType = $("#leaveType").val();
	var leaveId = $("#leaveApplicationId").val();
    var d = {
        date:t,
        leaveId:leaveId,
        leaveType:leaveType
    }
    $.getJSON(contextPath +'/empApplicationLeave/getLeaveBackHours.htm',d,function(data){
        if(data.code == '0000'){
            _this.timeQuantum = data.list;
            selHour(dom);
        }else{
            $(dom).val('');
            OA.titlePup(data.message,'close')
        }
    })
    function selHour(dom){//绑定小时
        _this.bindEvent("onlyHour",'','popuponlyHour',function(){
            if($(dom).val() != '' && $(dom).val().indexOf(' ') < 0){
                OA.twoSurePop({
                    tips:'请选择小时！',
                    sureFn:function(){
                        selHour(dom)
                    },
                    cancelFn:function(){
                        $(dom).val('');
                    }
                });
            }
        });
    }
}
