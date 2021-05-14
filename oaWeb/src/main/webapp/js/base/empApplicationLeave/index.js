var idNum = 0;
var affairFlag = false;
$(function(){
	$(".problem").click(function(){
		window.location.href = contextPath + "/empApplicationLeave/leaveRule.htm";
	})
	//获取默认请假类型
    $.getJSON(contextPath +'/empApplicationLeave/getLeaveTypes.htm',function(data){
        var holidayArr = [];

        for(var i=0;i<data.length;i++){
            holidayArr.push(data[i].name);
        }

        //映射假期对应id
        var holidayReg = {
            '1':'年假',
            '2':'病假',
            '3':'婚假',
            '4':'哺乳假',
            '5':'调休',
            '6':'产前假',
            '7':'产假',
            '8':'流产假',
            '9':'陪产假',
            '10':'丧假',
            '11':'事假',
            '12':'其他'
        }
        //选择假期类型
        $('body').on('click','.holidayType',function(){
            var _this = $(this);

            var _parent = $(this).closest('.selTimeTpl');
            //每次选择假期类型将选择时间清空
            _parent.find(".selMatter").find("input").val('');
            OA.bPup(holidayArr,function(data){
                _this.children('input').val(data);
                for(var item in holidayReg){
                    if(holidayReg[item] === data){
                    	affairFlag = false;
                        _this.children('input').attr('data-id',item);
                        //如果选择是哺乳假
                        if(item == '4'){
                            _parent.find('.lactation-holiday').show();
                            _parent.find('.normal-holiday').hide();
                            _parent.find('.living-holiday').hide();
                            _parent.find('.abortion-holiday').hide();
                            _parent.find('.funeral-holiday').hide();
                            _parent.find('.antenatal-holiday').hide();
                            _parent.find('.marriage-holiday').hide();
                            $.getJSON(contextPath +'/empApplicationLeave/getChildren.htm',function(data){
                                _parent.find('.childrenNum').text(data.childrenNum);
                                _parent.find('.hourNum').text(data.childrenNum+"小时/天");
                                _parent.find('.lactation-holiday-end-time').text(data.endTime);
                            });
                        }else if(item == '7'){
                            _parent.find('.lactation-holiday').hide();
                            _parent.find('.normal-holiday').hide();
                            _parent.find('.living-holiday').show();
                            _parent.find('.abortion-holiday').hide();
                            _parent.find('.funeral-holiday').hide();
                            _parent.find('.antenatal-holiday').hide();
                            _parent.find('.marriage-holiday').hide();
                        }else if(item == '8'){
                            _parent.find('.lactation-holiday').hide();
                            _parent.find('.normal-holiday').hide();
                            _parent.find('.abortion-holiday').show();
                            _parent.find('.living-holiday').hide();
                            _parent.find('.funeral-holiday').hide();
                            _parent.find('.antenatal-holiday').hide();
                            _parent.find('.marriage-holiday').hide();
                        }else if(item == '10'){
                        	//丧假
                            _parent.find('.lactation-holiday').hide();
                            _parent.find('.normal-holiday').hide();
                            _parent.find('.abortion-holiday').hide();
                            _parent.find('.living-holiday').hide();
                            _parent.find('.funeral-holiday').show();
                            _parent.find('.antenatal-holiday').hide();
                            _parent.find('.marriage-holiday').hide();
                            initfuneral(_parent.find(".selMatter:eq(21)").find("input"),_parent.find(".selMatter:eq(20)").find("input"),
                            		_parent.find(".selMatter:eq(22)").find("div"),_parent.find(".selMatter:eq(23)").find("div"));
                        }else if(item == '6'){
                        	 //产前假
                        	 _parent.find('.lactation-holiday').hide();
                             _parent.find('.normal-holiday').hide();
                             _parent.find('.abortion-holiday').hide();
                             _parent.find('.living-holiday').hide();
                             _parent.find('.funeral-holiday').hide();
                             _parent.find('.antenatal-holiday').show();
                             _parent.find('.marriage-holiday').hide();
                             initantenatal(_parent.find(".selMatter:eq(24)").find("input"),
                            		 _parent.find(".selMatter:eq(25)").find("input"),_parent.find(".selMatter:eq(26)").find("div"));
                        }else if(item == '3' || item == '9'){
                        	//婚假 || 陪产假
                       	    _parent.find('.lactation-holiday').hide();
                            _parent.find('.normal-holiday').hide();
                            _parent.find('.abortion-holiday').hide();
                            _parent.find('.living-holiday').hide();
                            _parent.find('.funeral-holiday').hide();
                            _parent.find('.antenatal-holiday').hide();
                            _parent.find('.marriage-holiday').show();
                            if(item == '3'){
                            	$("#leaveType").val(3);
                            	initMarriage(_parent.find(".selMatter:eq(27)").find("input"),
                          	       		 _parent.find(".selMatter:eq(28)").find("div"),_parent.find(".selMatter:eq(29)").find("div"));
                            }else if(item == '9'){  //陪产假
                            	$("#leaveType").val(9);//赋值隐藏框
                            	initMarriage(_parent.find(".selMatter:eq(27)").find("input"),
                         	       		 _parent.find(".selMatter:eq(28)").find("div"),_parent.find(".selMatter:eq(29)").find("div"));
                            }
                        }else{
                            _parent.find('.lactation-holiday').hide();
                            _parent.find('.normal-holiday').show();
                            _parent.find('.living-holiday').hide();
                            _parent.find('.abortion-holiday').hide();
                            _parent.find('.funeral-holiday').hide();
                            _parent.find('.antenatal-holiday').hide();
                            _parent.find('.marriage-holiday').hide();
                    		//计算请假天数
                            var start_date = _parent.find(".selMatter:eq(1)").find("input").val();
                    		var end_date = _parent.find(".selMatter:eq(2)").find("input").val();
                    		var leaveType = item;
                        	if(start_date != "" && end_date != ""&&leaveType!="") {
                        		if(ckdate(start_date,end_date)) {
                        			var startTime1 = start_date.substring(0,10);
                        			var startTime2 = start_date.substring(11,13);
                        			var endTime1 = end_date.substring(0,10);
                        			var endTime2 = end_date.substring(11,13);
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
                                					_parent.find(".selMatter:eq(3)").find("div").text(data.leaveHours+"小时");
                    	        				}else{
                    	        					_parent.find(".selMatter:eq(3)").find("div").text(data.leaveDays+"天");
                    	        				}
                                				_parent.find(".selMatter:eq(3)").find("div").attr("leaveDays",data.leaveDays);
                                				_parent.find(".selMatter:eq(3)").find("div").attr("leaveHours",data.leaveHours);
                                			}
                                		}
                                	});
                        		} else {
                        			_parent.find(".selMatter:eq(2)").find("input").val("");
                        			_parent.find(".selMatter:eq(3)").find("div").text("");
                        		}
                        	}
                        }
                    }
                }
            });
        })
    })
   //哺乳假请假时间
   $('body').on('click', '.holidayTime', function () {
        var _this = $(this);
        OA.bPup(['上午', '下午'], function (data) {
        	if(_this.parent().prev().prev().find("div").text().trim()==0){
        		 _this.children('input').val(data);
        	}
            switch (data) {
                case '上午':
                	if(_this.parent().prev().prev().find("div").text().trim()>0){
               		    var endT = 9+parseInt(_this.parent().prev().prev().find("div").text().trim());
                		_this.children('input').val("09:00-"+endT+":00");
                 	}
                    _this.children('input').attr('data-id', '100')
                    break;
                case '下午':
                	 if(_this.parent().prev().prev().find("div").text().trim()>0){
               		    var startT = 18-parseInt(_this.parent().prev().prev().find("div").text().trim());
                		_this.children('input').val(startT+":00-"+"18:00");
                 	 }
                    _this.children('input').attr('data-id', '200')
                    break;
            }
        });
    })
    //丧假亲属关系
   $('body').on('click', '.relatives', function () {
        var _this = $(this);
        var _parent = $(this).closest('.selTimeTpl');
        OA.bPup(['父母','配偶','子女','祖父母','外祖父母','兄弟姐妹','配偶父母'], function (data) {
            _this.children('input').val(data);
            switch (data) {
                case '父母':
                    _this.children('input').attr('data-id', '100')
                    break;
                case '配偶':
                    _this.children('input').attr('data-id', '200')
                    break;
                case '子女':
                    _this.children('input').attr('data-id', '300')
                    break;
                case '祖父母':
                    _this.children('input').attr('data-id', '400')
                    break;
                case '外祖父母':
                    _this.children('input').attr('data-id', '500')
                    break;
                case '兄弟姐妹':
                    _this.children('input').attr('data-id', '600')
                    break;
                case '配偶父母':
                    _this.children('input').attr('data-id', '700')
                    break;
            }
            initfuneral( _parent.find(".selMatter:eq(21)").find("input"),_this.find("input"),
            		_parent.find(".selMatter:eq(22)").find("div"),_parent.find(".selMatter:eq(23)").find("div"));
        });
    })    
    //产假开始日期
    var calendartime = new lCalendar();
    calendartime.init({
        'trigger': 'maternityLeaveStart',
        'type': 'date'
    });
    var calendartime = new lCalendar();
    calendartime.init({
        'trigger': 'maternityLeaveEnd',
        'type': 'date'
    });
    //丧假开始时间初始化
    var calendartime = new lCalendar();
    calendartime.init({
        'trigger': 'funeral-date',
        'type': 'date'
    });
    //产前假时间初始化
    var calendartime = new lCalendar();
    calendartime.init({
        'trigger': 'antenatal-start-date',
        'type': 'date'
    });
    //流产假时间初始化
    var calendartime = new lCalendar();
    calendartime.init({
        'trigger': 'abortion-start-date',
        'type': 'date'
    });
    var calendartime = new lCalendar();
    calendartime.init({
        'trigger': 'antenatal-end-date',
        'type': 'date'
    });
    //婚假时间初始化
    var calendartime = new lCalendar();
    calendartime.init({
        'trigger': 'marriage-start-date',
        'type': 'date'
    });
    //模拟单选框
    $('body').on('touchstart','.singleSelGroup span',function(){
        $(this).children('em').addClass('current');
        $(this).siblings('span').children('em').removeClass('current');
    })
    //绑定妊娠时间
    $('body').on('click','.abortionSel',function(){
        var _this = $(this);
        OA.bPup(['4个月以下','4个月以上（含4个月）'],function(data){
            _this.children('input').val(data);
            switch(data){
                case '4个月以下':
                    _this.children('input').attr('data-id','100')
                    initAbortion1(_this,100);
                    break;
                case '4个月以上（含4个月）':
                    _this.children('input').attr('data-id','200');
                    initAbortion1(_this,200);
                    break;
            }
        });
    })
	//监控开始结束时间计算天数
	$("#start-date").bind('input propertychange', function() {
		if($(this).val().indexOf(" ")<0){
			return;
		}
		initDuration($(this),1);
        
    }); 
	$("#end-date").bind('input propertychange', function() {
		if($(this).val().indexOf(" ")<0){
			return;
		}
		initDuration($(this),2);
    });
	//监控哺乳假请假天数
	$("#lactation-start-date").bind('input propertychange', function() {  
		calLactations(this);
    }); 
	//产假联动
    $("#living-start-date").bind('input propertychange', function() {  
    	calMaternityDays1(this);
    }); 
    $("#maternityLeaveStart").bind('input propertychange', function() {  
		var leaveDays = 0;
		var leaveDaysStr=$(".selTimeTpl").find(".selMatter:eq(15)").find("div").text().trim();
		//产假字符截取 含+ 则相加天数
		if(leaveDaysStr.indexOf("+") != -1 ){
			var leaveArr=leaveDaysStr.split("+");
			var leaveDays = parseInt(leaveArr[0]);
		}else{
			leaveDays = parseInt(leaveDaysStr);
		}
		var empId= $("#employeeId").val();
		countmaternityLeave(leaveDays,$(this).val(),empId,$(this),5)
    }); 
    
    $("#maternityLeaveEnd").bind('input propertychange', function() {  
    	if($("#maternityLeaveStart").val()!=""){
    		var endDate = new Date(Date.parse($(this).val().replace(/-/g,  "/"))); 
    		var startDate = new Date(Date.parse($("#maternityLeaveStart").val().replace(/-/g,  "/"))); 
    		var leaveDays = (endDate.getTime()-startDate.getTime())/(3600*24*1000)+1;
    		var empId= $("#employeeId").val();
    		countmaternityLeave(leaveDays,startDate.format("yyyy/MM/dd").replaceAll("/","-"),empId,$(this),4)
    	}
    }); 
    
    //根据子女数量和生产情况生成结束日期
    $("#living-child-num").bind('input propertychange', function() {
    	calMaternityDays2(this);
    });
    $("#singleSelGroup").find("span").each(function(){
    	$(this).click(function(){
    		calMaternityDays3(this);
    	})
    })
    //流产假控制天数和结束日期
    $("#abortion-start-date").bind('input propertychange', function() {
    	initAbortion(this);
    });
    //丧假控制天数和结束日期
    $("#funeral-date").bind('input propertychange', function() {
    	 var _parent = $(this).closest('.selTimeTpl');
    	 initfuneral($(this),_parent.find(".selMatter:eq(20)").find("input"),
    			 _parent.find(".selMatter:eq(22)").find("div"),_parent.find(".selMatter:eq(23)").find("div"));
    });
    //产前假控制天数
    $("#antenatal-start-date").bind('input propertychange', function() {
    	 var _parent = $(this).closest('.selTimeTpl');
    	 initantenatal(_parent.find(".selMatter:eq(24)").find("input"),
        		 _parent.find(".selMatter:eq(25)").find("input"),_parent.find(".selMatter:eq(26)").find("div"));
    });
    $("#antenatal-end-date").bind('input propertychange', function() {
   	     var _parent = $(this).closest('.selTimeTpl');
   	     initantenatal(_parent.find(".selMatter:eq(24)").find("input"),
     		     _parent.find(".selMatter:eq(25)").find("input"),_parent.find(".selMatter:eq(26)").find("div"));
    });   
    //婚假 陪产假 控制天数
    $("#marriage-start-date").bind('input propertychange', function() {
	   	 var _parent = $(this).closest('.selTimeTpl');
	   	 var leaveType=$("#leaveType").val();
	   	 initMarriage(_parent.find(".selMatter:eq(27)").find("input"),
	       		 _parent.find(".selMatter:eq(28)").find("div"),_parent.find(".selMatter:eq(29)").find("div"),leaveType);	  
    }); 
})
function save(){
	if($("#reason").val().trim()==""){
		OA.titlePup('请假事由不能为空！','lose');
		return;
	}
	if($("#mobile").val().trim()==""){
		OA.titlePup('紧急联系电话不能为空！','lose');
		return;
	}
	var ret = /^1[3456789]\d{9}$/;
	if(!ret.test($("#mobile").val())){
		OA.titlePup('紧急联系电话格式不正确！','lose');
		return;
	}
	if($("#agentId").val().trim()==""){
		OA.titlePup('假期代理人不能为空！','lose');
		return;
	}
	if($("#agentMobile").val().trim()!=""){
		if(!ret.test($("#agentMobile").val().trim())){
			OA.titlePup('代理人电话格式不正确！','lose');
			return;
		}
	}
	var reg = /^((([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6}\,))*(([a-z0-9_\.-]+)@([\da-z\.-]+)\.([a-z\.]{2,6})))$/;
    if($("#toEmails").val().trim()!=""){
    	if(!reg.test($("#toEmails").val().trim())){
    		OA.titlePup('请填写正确的邮箱并用逗号英文给开！','lose');
			return;
        }
    }
	//哺乳假开始时间
	var lactationSt = false;
	//哺乳假请假时间
	var lactationDt = false;
	//哺乳假孩子数
	var lactationChn = false;
	
	//产假子女数量
	var livingChn = false;
	//产假开始日期
	var livingSt = false;
	//产假结束日期
	var livingEt = false;
	//产假子女出生日期
	var livingLt = false;
	//产假请假天数
	var livingLds = false;
	
	//流产假开始日期
	var abortionSt = false;
	//流产假结束日期
	var abortionEt = false;
	//流产假请假天数
	var abortionLds = false;
	
	//丧假开始日期
	var funeralSt = false;
	//丧假结束日期
	var funeralEt = false;
	//丧假请假天数
	var funeralLds = false;
	
	//产前假开始日期
	var antenatalSt = false;
	//产前假结束日期
	var antenatalEt = false;
	//产前假请假天数
	var antenatalLds = false;
	
	//婚假单独标记
	var marriageFlag = false; 
	//产前假开始日期
	var  marriageSt = false;
	//产前假结束日期
	var  marriageEt = false;
	//产前假请假天数
	var  marriageLds = false;
	
	//陪产假单独标记
	var paternityFlag = false; 
	
	//普通假期开始时间
	var leaveSt = false; 
	//普通假期结束时间
	var leaveEt = false; 
	//普通假期请假天数
	var leaveLds = false; 
	
	var list = new Array();
	$(".selTimeTpl").each(function(i){
		//哺乳假
		if($(this).find(".selMatter:eq(0)").find("input").attr("data-id")==4){
			if($(this).find(".selMatter:eq(4)").find("input").val().trim()==""){
				lactationSt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(8)").find("input").attr("data-id")==""){
				lactationDt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(6)").find("div").text().trim()<=0||$(this).find(".selMatter:eq(6)").find("div").text().trim()==""){
				lactationChn = true;
				return false;
			}
			//计算请假时间
			if($(this).find(".selMatter:eq(8)").find("input").attr("data-id")==100){
				var startTime = "09:00:00"
				var endTime = (9+parseInt($(this).find(".selMatter:eq(6)").find("div").text().trim()))+":00:00"
			}else{
				var startTime = (18-parseInt($(this).find(".selMatter:eq(6)").find("div").text().trim()))+":00:00";
			    var endTime = "18:00:00"
			}
			var detail = {
				leaveType:4,
				startTime:$(this).find(".selMatter:eq(4)").find("input").val().trim()+" "+startTime,
				endTime:$(this).find(".selMatter:eq(5)").find("div").text().trim()+" "+endTime,
				leaveDays:$(this).find(".selMatter:eq(9)").find("div").text().trim(),
				childrenNum:$(this).find(".selMatter:eq(6)").find("div").text().trim(),
				dayType:$(this).find(".selMatter:eq(8)").find("input").attr("data-id")
				//小时数后台计算
			};
			list.push(detail);
		}else if($(this).find(".selMatter:eq(0)").find("input").attr("data-id")==7){	
            if($(this).find(".selMatter:eq(13)").find("input").val().trim()==""){
            	livingSt = true;
            	return false;
            }
            if($(this).find(".selMatter:eq(11)").find("input").val().trim()==""||$(this).find(".selMatter:eq(11)").find("input").val().trim()<=0){
            	livingChn = true;
            	return false;
            }
            if($(this).find(".selMatter:eq(14)").find("input").val().trim()==""){
            	livingEt = true;
            	return false;
            }
            if($(this).find(".selMatter:eq(15)").find("div").text().trim()==""||$(this).find(".selMatter:eq(15)").find("div").text().trim()<=0){
            	livingLds = true;
            	return false;
            }
            if($(this).find(".selMatter:eq(10)").find("input").val().trim()==""){
            	livingLt = true;
            	return false;
            }
			//产假
			var birthType = 100;
			$(this).find(".selMatter:eq(12)").find("em").each(function(){
				if($(this).hasClass("current")){
					birthType = $(this).attr("data-id");
				}
			})
			var leaveDays;
			var leavesNote=0;
			var leaveDaysStr=$(this).find(".selMatter:eq(15)").find("div").text().trim();
			//产假字符截取 含+ 则相加天数
			if(leaveDaysStr.indexOf("+") != -1 ){
				var leaveArr=leaveDaysStr.split("+");
				var leavesNum = parseInt(leaveArr[0]);
				leavesNote= parseInt(leaveArr[1]);
				leaveDays=leavesNum.toString();
			}else{
				leaveDays=leaveDaysStr;
			}
			var detail = {
				leaveType:7,
				startTime:$(this).find(".selMatter:eq(13)").find("input").val().trim()+" 00:00:00",
				endTime:$(this).find(".selMatter:eq(14)").find("input").val().trim()+" 23:59:59",
				leaveDays:leaveDays,
				leavesNote:leavesNote,
				childrenNum:$(this).find(".selMatter:eq(11)").find("input").val().trim(),
				birthType:birthType
			};
			list.push(detail);
		}else if($(this).find(".selMatter:eq(0)").find("input").attr("data-id")==8){
			if($(this).find(".selMatter:eq(16)").find("input").val().trim()==""){
				abortionSt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(18)").find("div").text().trim()==""){
				abortionEt = true;
				return false;
			}
            if($(this).find(".selMatter:eq(19)").find("div").text().trim()==""||$(this).find(".selMatter:eq(19)").find("div").text().trim()<=0){
            	abortionLds = true;
            	return false;
            }
			var detail = {
				leaveType:8,
				startTime:$(this).find(".selMatter:eq(16)").find("input").val().trim()+" 00:00:00",
				endTime:$(this).find(".selMatter:eq(18)").find("div").text().trim()+" 23:59:59",
				leaveDays:$(this).find(".selMatter:eq(19)").find("div").text().trim()
			};
			list.push(detail);
		}else if($(this).find(".selMatter:eq(0)").find("input").attr("data-id")==5){
			if($(this).find(".selMatter:eq(1)").find("input").val()==""){
				leaveSt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(2)").find("input").val()==""){
				leaveEt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(3)").find("div").attr("leaveDays")==""||$(this).find(".selMatter:eq(3)").find("div").attr("leaveDays")<=0){
				leaveLds = true;
				return false;
			}
			//调休
			var startTime = $(this).find(".selMatter:eq(1)").find("input").val();
			var endTime = $(this).find(".selMatter:eq(2)").find("input").val();
			var leaveDays = $(this).find(".selMatter:eq(3)").find("div").attr("leaveDays");
			var leaveHours = $(this).find(".selMatter:eq(3)").find("div").attr("leaveHours");
			var detail = {
				leaveType:$(this).find(".selMatter:eq(0)").find("input").attr("data-id"),
				startTime:startTime.substring(0,10)+" "+startTime.substring(11,13)+":00:00",
				endTime:endTime.substring(0,10)+" "+endTime.substring(11,13)+":00:00",		
				leaveDays:leaveDays,
				leaveHours:leaveHours
			};
			list.push(detail);
		}else if($(this).find(".selMatter:eq(0)").find("input").attr("data-id")==10){
			
			//丧假
			if($(this).find(".selMatter:eq(21)").find("input").val()==""){
				funeralSt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(22)").find("div").text().trim()==""){
				funeralEt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(23)").find("div").attr("leaveDays").trim()==""){
				funeralLds = true;
				return false;
			}
			var startTime = $(this).find(".selMatter:eq(21)").find("input").val();
			var endTime = $(this).find(".selMatter:eq(22)").find("div").text().trim();
			var leaveDays = $(this).find(".selMatter:eq(23)").find("div").attr("leavedays").trim();
			var relatives = $(this).find(".selMatter:eq(20)").find("input").attr("data-id");
			var detail = {
				leaveType:10,
				startTime:startTime+" 00:00:00",
				endTime:endTime+" 23:59:59",		
				leaveDays:leaveDays,
				relatives:relatives
			};
			list.push(detail);
		} else if($(this).find(".selMatter:eq(0)").find("input").attr("data-id")==6){
			//产前假
			if($(this).find(".selMatter:eq(24)").find("input").val()==""){
				antenatalSt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(25)").find("input").val()==""){
				antenatalEt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(26)").find("div").attr("leaveDays").trim()==""){
				antenatalLds = true;
				return false;
			}
			var startTime = $(this).find(".selMatter:eq(24)").find("input").val();
			var endTime = $(this).find(".selMatter:eq(25)").find("input").val();
			var leaveDays = $(this).find(".selMatter:eq(26)").find("div").attr("leavedays").trim();
			var detail = {
				leaveType:6,
				startTime:startTime+" 00:00:00",
				expectedDate:endTime+" 23:59:59",		
				leaveDays:leaveDays
			};
			list.push(detail);
		}else if($(this).find(".selMatter:eq(0)").find("input").attr("data-id")==3){
			if($(this).find(".selMatter:eq(3)").find("div").attr("leaveDays")>10){
				marriageFlag = true;
				return false;
			}
			if($(this).find(".selMatter:eq(27)").find("input").val()==""){
				marriageSt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(28)").find("div").text()==""){
				marriageEt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(29)").find("div").attr("leaveDays").trim()==""){
				marriageLds = true;
				return false;
			}
			var startTime = $(this).find(".selMatter:eq(27)").find("input").val();
			var endTime = $(this).find(".selMatter:eq(28)").find("div").text();
			var leaveDays = $(this).find(".selMatter:eq(29)").find("div").attr("leaveDays");
			var detail = {
				leaveType:3,
				startTime:startTime+" 00:00:00",
				endTime:endTime+" 23:59:59",		
				leaveDays:leaveDays
			};
			list.push(detail);
		}//新增陪产假 
		else if($(this).find(".selMatter:eq(0)").find("input").attr("data-id")==9){
			if($(this).find(".selMatter:eq(3)").find("div").attr("leaveDays")>10){
				leaveSt = true;
				leaveEt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(27)").find("input").val()==""){
				leaveSt = true;
				leaveEt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(28)").find("div").text()==""){
				leaveSt = true;
				leaveEt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(29)").find("div").attr("leaveDays").trim()==""){
				leaveSt = true;
				leaveEt = true;
				return false;
			}
			var startTime = $(this).find(".selMatter:eq(27)").find("input").val();
			var endTime = $(this).find(".selMatter:eq(28)").find("div").text();
			var leaveDays = $(this).find(".selMatter:eq(29)").find("div").attr("leaveDays");
			var detail = {
				leaveType:9,
				startTime:startTime+" 00:00:00",
				endTime:endTime+" 23:59:59",		
				leaveDays:leaveDays
			};
			list.push(detail);
		}else{
			if($(this).find(".selMatter:eq(0)").find("input").attr("data-id")==9){
				if($(this).find(".selMatter:eq(3)").find("div").attr("leaveDays")>10){
					paternityFlag = true;
					return false;
				}
			}
			if($(this).find(".selMatter:eq(1)").find("input").val()==""){
				leaveSt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(2)").find("input").val()==""){
				leaveEt = true;
				return false;
			}
			if($(this).find(".selMatter:eq(3)").find("div").attr("leaveDays")==""||$(this).find(".selMatter:eq(3)").find("div").attr("leaveDays")<=0){
				leaveLds = true;
				return false;
			}
			var startTime = $(this).find(".selMatter:eq(1)").find("input").val();
			var endTime = $(this).find(".selMatter:eq(2)").find("input").val();
			var leaveDays = $(this).find(".selMatter:eq(3)").find("div").attr("leaveDays");
			var detail = {
				leaveType:$(this).find(".selMatter:eq(0)").find("input").attr("data-id"),
				startTime:startTime.substring(0,10)+" "+startTime.substring(11,16)+":00",
				endTime:endTime.substring(0,10)+" "+endTime.substring(11,16)+":00",		
				leaveDays:leaveDays
			};
			list.push(detail);
		}
	})
	if(lactationSt){
		OA.titlePup('开始日期不能为空！','lose');
		return;
	}
	if(lactationDt){
		OA.titlePup('请假时间不能为空！','lose');
		return;
	}
	if(lactationChn){
		OA.titlePup('请确认子女信息！','lose');
		return;
	}
	
	if(livingSt){
		OA.titlePup('开始日期不能为空！','lose');
		return;
	}
	if(livingChn){
		OA.titlePup('请确认子女信息！','lose');
		return;
	}
	if(livingEt){
		OA.titlePup('结束日期不能为空！','lose');
		return;
	}
	if(livingLds){
		OA.titlePup('请确认请假天数！','lose');
		return;
	}
	if(livingLt){
		OA.titlePup('子女出生日期不能为空！','lose');
		return;
	}
	
	if(abortionSt){
		OA.titlePup('开始日期不能为空！','lose');
		return;
	}
	if(abortionEt){
		OA.titlePup('结束日期不能为空！','lose');
		return;
	}
	if(abortionLds){
		OA.titlePup('请确认请假天数！','lose');
		return;
	}
	
	if(funeralSt){
		OA.titlePup('开始日期不能为空！','lose');
		return;
	}
    if(funeralEt){
    	OA.titlePup('结束日期不能为空！','lose');
		return;
	}
    if(funeralLds){
    	OA.titlePup('请确认请假天数！','lose');
		return;
	}
    
	if(antenatalSt){
		OA.titlePup('开始日期不能为空！','lose');
		return;
	}
    if(antenatalEt){
    	OA.titlePup('预产期不能为空！','lose');
		return;
	}
    if(antenatalLds){
    	OA.titlePup('请确认请假天数！','lose');
		return;
	}
    
	if(marriageFlag){
		OA.titlePup('婚假最多只能请10天！','lose');
		return;
	}
	if(marriageSt){
		OA.titlePup('开始日期不能为空！','lose');
		return;
	}
    if(marriageEt){
    	OA.titlePup('预产期不能为空！','lose');
		return;
	}
    if(marriageLds){
    	OA.titlePup('请确认请假天数！','lose');
		return;
	}
	
	if(paternityFlag){
		OA.titlePup('陪产假最多只能请10天！','lose');
		return;
	}
	
	if(leaveSt){
		OA.titlePup('开始时间不能为空！','lose');
		return;
	}
	if(leaveEt){
		OA.titlePup('结束时间不能为空！','lose');
		return;
	}
	if(leaveLds){
		OA.titlePup('请确认请假天数！','lose');
		return;
	}
	
	if(affairFlag){
		OA.titlePup('有剩余年假请先申请年假！','lose');
		return;
	}
	
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
		    	OA.pageLoading(true);
				$.ajax({
					async:true,
					type:'post',
					timeout : 5000,
					dataType:'json',
					data:{
						reason:$("#reason").val(),
						mobile:$("#mobile").val(),
						agentId:$("#agentId").attr("data-id"),
						agentMobile:$("#agentMobile").val(),
						toPersions:$("#toPersions").attr("data-id"),
					    toEmails:$("#toEmails").val(),
						token:$("#token").val(),
						businessDetailList:JSON.stringify(list)
					},
					url:contextPath + "/empApplicationLeave/save.htm",
					success:function(data){
						if(data.success){
							window.location.href=contextPath + "/runTask/index.htm";
						}else{
							OA.pageLoading(false);
							OA.titlePup(data.message,'lose');
							$("#token").val(data.token);
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
}

//计算请假天数
function initDuration(obj,type){
	if(type==1){
		var start_date = $(obj).val();
		var end_date = $(obj).parent().parent().next().find("input").val();
		var leaveType = $(obj).parent().parent().prev().find("input").attr("data-id");		
	}else if(type==2){
		var start_date = $(obj).parent().parent().prev().find("input").val();
		var end_date = $(obj).val();
		var leaveType = $(obj).parent().parent().prev().prev().find("input").attr("data-id");
	}
	if(((leaveType=='9')&&type==2)||(leaveType!='9')){
		
		if(start_date != "" && end_date != ""&&leaveType!="") {
			if(ckdate(start_date,end_date)) {
				var startTime1 = start_date.substring(0,10);
				var startTime2 = start_date.substring(11,13);
				var endTime1 = end_date.substring(0,10);
				var endTime2 = end_date.substring(11,13);
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
	        					$(obj).parent().parent().parent().find(".selMatter:eq(3)").find("div").text(data.leaveHours+"小时");
	        				}else{
	        					$(obj).parent().parent().parent().find(".selMatter:eq(3)").find("div").text(data.leaveDays+"天");
	        				}
	        				$(obj).parent().parent().parent().find(".selMatter:eq(3)").find("div").attr("leaveDays",data.leaveDays);
	        				$(obj).parent().parent().parent().find(".selMatter:eq(3)").find("div").attr("leaveHours",data.leaveHours);
	        			}
	        		}
	        	});
			} else {
				$(obj).val("");
				$(obj).parent().parent().parent().find(".selMatter:eq(3)").find("div").text("");
			}
		}
	}
}
function babyBirthdayToNowDay(today){
	var countDay=0;
	var babyBirthday;
	today = Date.parse(today);
	$.ajax({
		async:false,
		type:'get',
		dataType:'json',
		data:{
			employeeId:$("#employeeId").val()
		},
		url:contextPath + "/empFamilyMember/getListByCondition.htm",
		success:function(data){
			$(data).each(function(index){
				if("1" == data[index].relation){//子女
					babyBirthday=getValByUndefined(data[index].birthday);
				}
			});
		}
	});
	babyBirthday = Date.parse(new Date(babyBirthday));
	var day=(today-babyBirthday)/(1000*60*60*24);/*不用考虑闰年否*/
	if(day<30 && day>20){
		countDay=30-day;
	}else if(day<=20){ //相差天数小于二十天
		countDay=10;
	}else{
		countDay=0;
	}
	return countDay.toFixed(0);
}
//计算产假天数
function calMaternityDays1(obj){
	if($(obj).val().trim()!=""){
		$.getJSON(contextPath +'/empApplicationLeave/getAntenatalLeave.htm?birthDays='+$(obj).val().trim(),function(data){
			//开始日期
	    	var startDay = data.birthDays;
	    	$("#maternityLeaveStart").val(startDay);
	    	$("#maternityLeaveStart").attr("data-lcalendar",startDay+","+$(obj).val());
	    	if($(obj).parent().parent().next().find("input").val()!=""){
	    		var type= 100;
	        	$(obj).parent().parent().next().next().find("em").each(function(){
	        		if($(this).hasClass("current")){
	        			type = $(this).attr("data-id");
	        		}
	        	})
	    		var leaveDays = 0;
	    		leaveDays = 128+(parseInt($(obj).parent().parent().next().find("input").val())-1)*15;
	    		if(type==200){
	    			leaveDays = leaveDays+15;
	    		}
	    		/*var startDate = new Date(Date.parse(startDay.replace(/-/g,  "/"))); 
	    		startDate.setDate(startDate.getDate()+parseInt(leaveDays)-1);
	    		$(obj).parent().parent().next().next().next().next().find("div").text(startDate.format("yyyy/MM/dd").replaceAll("/","-"));
	    		*/
	    		var maternityLeaveStart= $("#maternityLeaveStart").val();
	        	var empId=$("#employeeId").val();
	        	
	        	countmaternityLeave(leaveDays,maternityLeaveStart,empId,obj,1);
	    	}
	    });
		
	}
}
//计算产假天数
function calMaternityDays2(obj){
	if($(obj).val().trim()!=""){
		if($(obj).parent().parent().prev().find("input").val()!=""){
	    	var type= 100;
	    	$(obj).parent().parent().next().find("em").each(function(){
	    		if($(this).hasClass("current")){
	    			type = $(this).attr("data-id");
	    		}
	    	})
	    	
	    	var empId=$("#employeeId").val();
	    	var leaveDays = 0;
	    	if($(obj).val()>0){
	    		leaveDays = 128+(parseInt($(obj).val())-1)*15;
	    		if(type==200){
	    			leaveDays = leaveDays+15;
	    		}
	    		//计算开始 结束日期
	    		var startTime = $(obj).parent().parent().next().next().find("div").text();
	    		var startDate = new Date(Date.parse(startTime.replace(/-/g,  "/"))); 
	    		startDate.setDate(startDate.getDate()+parseInt(leaveDays)-1);
	    		$(obj).parent().parent().next().next().next().find("input").val(startDate.format("yyyy/MM/dd").replaceAll("/","-"));
	    		
	    		var maternityLeaveStart= $("#maternityLeaveStart").val();
	    		countmaternityLeave(leaveDays,maternityLeaveStart,empId,obj,2);
	    	}
		}
		
	}else{
		$(obj).parent().parent().next().next().next().find("input").val("");
		$(obj).parent().parent().next().next().next().next().find("div").text("");
	}
}
//计算产假天数
function calMaternityDays3(obj){
	var type = $(obj).find("em").attr("data-id");
	if($(obj).parent().parent().parent().prev().find("input").val()!=""){
		var leaveDays = 128+(parseInt($(obj).parent().parent().parent().prev().find("input").val())-1)*15;
		
    	var empId=$("#employeeId").val();
		if(type==200){
			leaveDays = leaveDays+15;
		}
		
		if($(obj).parent().parent().parent().next().find("div").text().trim()!=""){
			var startTime = $(obj).parent().parent().parent().next().find("div").text().trim();
			var startDate = new Date(Date.parse(startTime.replace(/-/g,  "/"))); 
			startDate.setDate(startDate.getDate()+parseInt(leaveDays)-1);
			$(obj).parent().parent().parent().next().next().find("div").text(startDate.format("yyyy/MM/dd").replaceAll("/","-"));
		}
		var babyDay = $("#living-start-date").val();
		var maternityLeaveStart=addDate(babyDay,-15);
		var replaceAll=maternityLeaveStart.format("yyyy/MM/dd").replaceAll("/","-");
		countmaternityLeave(leaveDays,replaceAll,empId,obj,3);
		
	}
}
//产假计算开始时间和结束时间
function countmaternityLeave(leaveDays,maternityLeaveStart,empId,obj,type){
	var strLeaves=leaveDays;
	$.getJSON(contextPath +'/empApplicationLeave/getLeaveCountStartEndTime.htm?leaveDays='+leaveDays+'&startTime='+maternityLeaveStart+'&empId='+empId,function(data){
		var startTimeFlag = data.startTimeFlag;
		var endTimeFlag = data.endTimeFlag;
		var addLegalTime=data.addLegalTime;
		var startDate = new Date(Date.parse(startTimeFlag.replace(/-/g,  "/")));
		var endDate = new Date(Date.parse(endTimeFlag.replace(/-/g,  "/")));
		$("#maternityLeaveStart").val(startDate.format("yyyy/MM/dd").replaceAll("/","-"));
		$("#maternityLeaveEnd").val(endDate.format("yyyy/MM/dd").replaceAll("/","-"));
		if(addLegalTime!=0){
			strLeaves=leaveDays+"+"+addLegalTime;
		}else{
			strLeaves=leaveDays;
		}
		
		if(type==1){
			$(obj).parent().parent().next().next().next().next().next().find("div").text(strLeaves);
		}else if(type==2){
			$(obj).parent().parent().next().next().next().next().find("div").text(strLeaves);
		}else if (type==3){
			$(obj).parent().parent().parent().next().next().next().find("div").text(strLeaves);
		}else if (type==4){
			$(obj).parent().parent().next().find("div").text(strLeaves);
		}else if (type==5){
			$(obj).parent().parent().next().next().find("div").text(strLeaves);
		}
	});
}

function addDate(dd,dadd){
	var a = new Date(dd)
	a = a.valueOf()
	a = a + dadd * 24 * 60 * 60 * 1000
	a = new Date(a)
	return a;
}

//计算哺乳假天数
function calLactations(obj){
	if($(obj).parent().parent().next().next().find("div").text().trim()>0){
		//结束时间
		var endDay = $(obj).parent().parent().next().find("div").text().trim();
		var endDate = new Date(Date.parse(endDay.replace(/-/g,  "/"))); 
		//出生日期
		var birthDays = (parseInt(endDay.substring(0,4))-1)+"-"+endDay.substring(5,10);
		var birthDate = new Date(Date.parse(birthDays.replace(/-/g,  "/"))); 
		//开始日期
		var startDate = new Date(Date.parse($(obj).val().trim().replace(/-/g,  "/")));
		if(startDate.getTime()<birthDate.getTime()){
			OA.titlePup('开始日期不能小于孩子出生日期！','lose');
			return;
		}
		var leaveDays = 1;
		while(true){
			startDate.setDate(startDate.getDate()+1);
			if(startDate.getTime()>endDate.getTime()){
				break;
			}
			leaveDays = leaveDays+1;
		}
		$(obj).parent().parent().next().next().next().next().next().find("div").text(leaveDays);
	}
}
//计算流产假
function initAbortion(obj){
	var startDays = $(obj).val();
	if(startDays.trim()!=""){
		var startDate = new Date(Date.parse(startDays.replace(/-/g,  "/"))); 
		//判断妊娠周期
		if($(obj).parent().parent().next().find("input").attr("data-id")==100){
			startDate.setDate(startDate.getDate()+15-1);
			var endDay = startDate.format("yyyy/MM/dd").replaceAll("/","-");
			var leaveDays = 15;
		}else{
			startDate.setDate(startDate.getDate()+42-1);
			var endDay = startDate.format("yyyy/MM/dd").replaceAll("/","-");
			var leaveDays = 42;
		}
		$(obj).parent().parent().next().next().find("div").text(endDay);
		$(obj).parent().parent().next().next().next().find("div").text(leaveDays);
	}
}

//计算丧假
function initfuneral(startDateObj,relativesObj,endDateObj,leaveDaysObj){
	 //开始日期
    var start_date = $(startDateObj).val();
    //亲属关系
    var relatives = $(relativesObj).attr("data-id");
    if(start_date!=""){
    	var startDate = new Date(Date.parse(start_date.replace(/-/g,  "/"))); 
//    	//校验开始时间是否上班
//		 var flag = false;
//		 $.ajaxSettings.async = false;
//		 $.getJSON(contextPath +'/empApplicationOvertime/getEmpClassTime.htm?classDate='+start_date,function(data){
//				if(data.date_type != 0){
//					OA.titlePup('尚未排班，不能请假！','lose');
//					flag = true;
//					$(leaveDaysObj).attr("leaveDays",0);
//				}
//			 });
//		 if(flag){
//			 return;
//		 }
    	//直系亲属3天
    	var day=0;
    	if(relatives==100||relatives==200||relatives==300){
    		startDate.setDate(startDate.getDate()+2);
    		$(endDateObj).text(startDate.format("yyyy/MM/dd").replaceAll("/","-"));
    		$(leaveDaysObj).text("3天");
    		$(leaveDaysObj).attr("leaveDays",3);
    		day=3;
    	}else{
    		startDate.setDate(startDate.getDate()+0);
    		$(endDateObj).text(startDate.format("yyyy/MM/dd").replaceAll("/","-"));
    		$(leaveDaysObj).text("1天");
    		$(leaveDaysObj).attr("leaveDays",1);
    		day=1;
    	}
    	var d = {
    	        type:day,
    	        date:start_date,
    	        leaveType:10
    	    }
    		$.getJSON(contextPath +'/empApplicationLeave/countLeave10.htm',d,function(data){
    	        if(data.code == '0000'){
    	        	startDate=data.endTime;
    	    		$(endDateObj).text(startDate.format("yyyy/MM/dd").replaceAll("/","-"));
    	    		
    	        }else{
    	        	$(endDateObj).text("");
    				$(leaveDaysObj).val("");
    	            OA.titlePup(data.message,'close')
    	        }
    	    })
    }
}

//计算产前假
function initantenatal(startDateObj,endDateObj,leaveDaysObj){
	 //开始日期
    var start_date = $(startDateObj).val();
    var end_date = $(endDateObj).val();
    if(start_date!=""&&end_date!=""){
    	var startDate = new Date(Date.parse(start_date.replace(/-/g,  "/"))); 
    	var startDateCount120 = new Date(Date.parse(start_date.replace(/-/g,  "/"))); 
    	var endDate = new Date(Date.parse(end_date.replace(/-/g,  "/"))); 
    	//四个月计算
    	startDateCount120.setDate(startDateCount120.getDate()+120);
    	
    	if(startDate.getTime()>endDate.getTime()){
    		$(leaveDaysObj).text(0+" 天");
            $(leaveDaysObj).attr("leaveDays",0);
     	    OA.titlePup('开始不能大于预产期！','lose');
        }else if(startDateCount120.getTime()<endDate.getTime()){
        	$(leaveDaysObj).text(0+" 天");
            $(leaveDaysObj).attr("leaveDays",0);
            $(startDateObj).val(null);
            $(endDateObj).val(null);
     	    OA.titlePup('产前假不能多于120天！','lose');
        }else{
         	var length = 0;
         	while(true){
         		length = length+1;
         		startDate.setDate(startDate.getDate()+1);
         		if(startDate.getTime()>endDate.getTime()){
         			break;
         		}
         	}
         	$(leaveDaysObj).text(length+" 天");
            $(leaveDaysObj).attr("leaveDays",length);
         }
    }
}

//计算婚假
function initMarriage(startDateObj,endDateObj,leaveDaysObj,type){
	 //开始日期
    var start_date = $(startDateObj).val();
    var leaveType = type;
    
    if(leaveType == '9'){ 
		if(start_date != ""){
			var startDateFomart = new Date(Date.parse(start_date.replace(/-/g,  "/"))); 
			var startDate = new Date(Date.parse(start_date.replace(/-/g,  "/"))); 			
			var len = 10;//parseInt(babyBirthdayToNowDay(startDate));
			startDate.setDate(startDateFomart.getDate()+len-1);
			$(endDateObj).text(startDate.format("yyyy/MM/dd").replaceAll("/","-"));
			$(leaveDaysObj).text(len+"天");
			$(leaveDaysObj).attr("leaveDays",len);
			
		}
	}else if(start_date!=""){
    	 var startDate = new Date(Date.parse(start_date.replace(/-/g,  "/"))); 
    	 startDate.setDate(startDate.getDate()+9);
         $(endDateObj).text(startDate.format("yyyy/MM/dd").replaceAll("/","-"));
         $(leaveDaysObj).text("10天");
    	 $(leaveDaysObj).attr("leaveDays",10);
    }
}
function initAbortion1(obj,type){
	var startDays = $(obj).parent().prev().find("input").val();
	if(startDays.trim()!=""){
		var startDate = new Date(Date.parse(startDays.replace(/-/g,  "/"))); 
		//判断妊娠周期
		if(type==100){
			startDate.setDate(startDate.getDate()+15-1);
			var endDay = startDate.format("yyyy/MM/dd").replaceAll("/","-");
			var leaveDays = 15;
		}else{
			startDate.setDate(startDate.getDate()+42-1);
			var endDay = startDate.format("yyyy/MM/dd").replaceAll("/","-");
			var leaveDays = 42;
		}
		$(obj).parent().next().find("div").text(endDay);
		$(obj).parent().next().next().find("div").text(leaveDays);
	}
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
function isSeries(){
	//第一个请假的结束时间
	var firstEndDate = $(".selTimeTpl:eq(0)").find(".selMatter:eq(2)").find("input").val();
	//校验请假时间必须连续
	var flag = true;
	var num = 0;
	$(".selTimeTpl").each(function(i){
		if(i>0){
			var startDate = $(".selTimeTpl:eq("+i+")").find(".selMatter:eq(1)").find("input").val();
			var endDate = $(".selTimeTpl:eq("+i+")").find(".selMatter:eq(2)").find("input").val();
			if(firstEndDate != startDate){
				flag = false;
				num = i;
				return false;
			}else{
				firstEndDate = endDate;
			}
		}
	});
	if(!flag){
		$(".selTimeTpl:eq("+num+")").find(".selMatter:eq(1)").find("input").val("");
		$(".selTimeTpl:eq("+num+")").find(".selMatter:eq(2)").find("input").val("");
		OA.titlePup('请假时间必须连续！','lose');
		return;
	}
}

function getHour(t,leaveType){//查询请假时间接口返回小时
	var dom = _this.trigger;
	var leaveType = $(dom).closest('.selTimeTpl').find(".selMatter:eq(0)").find("input").attr("data-id");
    if(!leaveType){
    	OA.titlePup('请先选择假期类型！','lose');
    	 $(dom).val('');
		return;
    }
    var d = {
        type:1,
        date:t,
        leaveType:leaveType
    }
    $.getJSON(contextPath +'/empApplicationLeave/getLeaveHours.htm',d,function(data){
        if(data.code == '0000'){
        	if(data.list!=null){
	            _this.timeQuantum = data.list;
	            selHour(dom);
        	}else{
        	}
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
function getHourLater(t,leaveType){//查询请假时间接口返回小时
	var dom = _this.trigger;
	var leaveType = $(dom).closest('.selTimeTpl').find(".selMatter:eq(0)").find("input").attr("data-id");
    if(!leaveType){
    	 $(dom).val('');
    	OA.titlePup('请先选择假期类型！','lose');
		return;
    }
	var d = {
        type:2,
        date:t,
        leaveType:leaveType
    }
    $.getJSON(contextPath +'/empApplicationLeave/getLeaveHours.htm',d,function(data){
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
