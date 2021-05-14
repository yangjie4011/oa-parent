var classSettList = [];
var employeeClassList = [];
$(function(){
	$('.selMonth').removeClass('selMonth');
	//初始化部门班次
	getClassSetByDepart();
	//初始化部门员工姓名和总工时
	getEmployeeClassHours();
	//点击员工切换日历
	switchCalendar();
	//初始化员工指定月份排班数据信息
	getEmployeeClassSetByMonth($(".on").attr("employid"));
	//初始化日历
	initCalendar($(".on").attr("employid"));
})

function getWeek(dateCurrent){
    //日期
    var h = $('td').length;
    var dataList = {
        '0': '星期天',
        '1': '星期一',
        '2': '星期二',
        '3': '星期三',
        '4': '星期四',
        '5': '星期五',
        '6': '星期六',
    }
    var weekNum = ''
    for(var a = 0; a < h ; a++){
        if($($('td')[a]).hasClass('current')){
            weekNum = ',' + dataList[a%7];
        }
    }
    var dateArr = dateCurrent.split('-');
    $('.showWorkInfo').html('日期： ' + dateArr[0]+'年'+dateArr[1]+'月'+dateArr[2]+'日'+ weekNum);
}

function dataKey(res,dateCurrent,currentObj){
    //日期
    var h = $('td').length;
    var dataList = {
        '0': '星期天',
        '1': '星期一',
        '2': '星期二',
        '3': '星期三',
        '4': '星期四',
        '5': '星期五',
        '6': '星期六',
    }
    var weekNum = '';
    for(var a = 0; a < h ; a++){
        if($($('td')[a]).attr('date_day')==$(currentObj).attr("date_day")){
            weekNum = ',' + dataList[a%7];
        }
    }
    var dateArr = dateCurrent.split('-');
    $('.showWorkInfo').html('日期： ' + dateArr[0]+'年'+dateArr[1]+'月'+dateArr[2]+'日'+ weekNum);
    $('.adjustWorkTime').html('');
    //班次信息
    if(res.startTime){
        $('.workTime').html('班次： '+res.name+'  工作时间：'+res.startTime+' — '+res.endTime);
    }else{
        $('.workTime').html('未排班~');
    }
    if($("#isMove").val()==1&&$(currentObj).hasClass("adjustT")){
   	    $('.adjustWorkTime').html('调整为： 休息');
    }
    if($("#isMove").val()==1&&typeof($(currentObj).attr("startTime"))!="undefined"&&$(currentObj).hasClass("adjustT")){
   	    $('.adjustWorkTime').html('调整为： '+$(currentObj).attr("name")+'  工作时间：'+$(currentObj).attr("startTime")+' — '+$(currentObj).attr("endTime"));
    }
}

//获取部门所有的员工信息
function getEmployeeClassHours(){
	$.ajaxSettings.async = false;
	$.getJSON(contextPath +'/employeeClass/getEmployeeClassHours.htm?attnApplicationEmployClassId='+$("#classdetailId").val(),function(data){
    	$("#employeeList").empty();
    	$.each(data, function(index) {
    		var scale = data[index].mustAttnTime+"H/"+data[index].shouldTime+"H";
    		if(index==0){
    			$("#employeeList").append("<li shouldTime="+data[index].shouldTime+" entryTime="+data[index].firstEntryTime+" quitTime="+data[index].quitTime+" mustAttnTime="+data[index].mustAttnTime+" employId="+data[index].employId+" class='on'><div class='mid'><p>"+data[index].employName+"</p><p>"+scale+"</p></div></li>");
    		}else{
    			$("#employeeList").append("<li shouldTime="+data[index].shouldTime+" entryTime="+data[index].firstEntryTime+" quitTime="+data[index].quitTime+" mustAttnTime="+data[index].mustAttnTime+" employId="+data[index].employId+"><div class='mid'><p>"+data[index].employName+"</p><p>"+scale+"</p></div></li>");
    		}
		});
        OA.ySilde();
  });
}
//获取部门的班次信息
function getClassSetByDepart(){
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		url:contextPath + "/classSetting/getListByCondition.htm",
		success:function(data){
			classSettList = data;
		}
	});
}
//获取员工一个月所有排班信息
function getEmployeeClassSetByMonth(employeeId){
  $.ajaxSettings.async = false;
  $.getJSON(contextPath +'/employeeClass/getEmployeeClassSetByMonth.htm?attnApplicationEmployClassId='+$("#classdetailId").val()+"&employeeId="+employeeId+"&type=2&classMonth="+$("#classMonth").val(),function(data){
	  employeeClassList = data;
  });
}
function initCalendar(employeeId){
	h_dateBag.initDatePage({
        url:contextPath+"/sysConfig/vacation.htm?year="+$("#classMonth").val().substring(0,4),
        id:'calendarBox',
        paramsDate:$("#classMonth").val().replaceAll('-','/'),
        list:employeeClassList,
        callback:function(){
            //默认获取一号排班
        	var isMove = 0;
        	if($('#calendarBox .default:eq(0)').hasClass("adjustT")){
        		isMove = 1;
        	}
            $.ajax({
				async:false,
				type:'post',
				dataType:'json',
				data:{
				  isMove:isMove,
				  employClassId:$("#classdetailId").val(),
				  classDate:$("#classMonth").val()+"-01",
                  employeeId:employeeId
				},
				url:contextPath + "/classSetting/getByEmpAndDate.htm",
				success:function(data){
					dataKey(data,$("#classMonth").val()+"-01", $('#calendarBox .default:eq(0)'));
				}
			});
            $('#calendarBox .default').click(function(event) {
            	//班次信息
            	var currentObj = $(this);
            	var classSetId_old = $(this).attr('oldId');
            	var classSetName_old = $(this).attr('oldName');
            	var classSetMustattntime_old = $(this).attr('mustattntime');
                $(this).addClass('current');
                var dateCurrent = $(this).attr('date_day');
                getWeek(dateCurrent);
                //判断入职和离职日期
                if(typeof($(".on").attr("entrytime"))!="undefined"){
                	if(new Date(Date.parse($(".on").attr("entrytime").replaceAll('/','-'))).getTime()-new Date(Date.parse(dateCurrent.replaceAll('/','-'))).getTime()>0){
                		$(this).removeClass('current');
                		OA.titlePup('该员工当天未入职!','lose');
                		return;
                	}
                }
                if(typeof($(".on").attr("quittime"))!="undefined"){
                	if(new Date(Date.parse($(".on").attr("quittime").replaceAll('/','-'))).getTime()-new Date(Date.parse(dateCurrent.replaceAll('/','-'))).getTime()<0){
                		$(this).removeClass('current');
                		OA.titlePup('该员工当天已离职!','lose');
                		return;
                	}
                }
            	if(new Date().getTime()-new Date(Date.parse(dateCurrent.replaceAll('/','-'))).getTime()>24*3600*1000){
            		OA.titlePup('只能调今天之后的班次!','lose');
            		return;
            	}
                //获取指定日期排班
            	var isMove = 0;
             	if($(this).hasClass("adjustT")){
             		isMove = 1;
             	}
                $.ajax({
					async:false,
					type:'post',
					dataType:'json',
					data:{
					  isMove:isMove,
					  employClassId:$("#classdetailId").val(),
					  classDate:dateCurrent,
	                  employeeId:employeeId
					},
					url:contextPath + "/classSetting/getByEmpAndDate.htm",
					success:function(data){
						dataKey(data,dateCurrent,currentObj);
					}
				});
                var nameArr = [];
                var idArr = [];
                var mustAttnTimeArr = [];
                var startTimeArr = [];
                var endTimeArr = [];
                $.each(classSettList, function(index) {
                	nameArr.push(classSettList[index].name);
                	idArr.push(classSettList[index].id);
                	mustAttnTimeArr.push(classSettList[index].mustAttnTime);
                	startTimeArr.push(classSettList[index].startTime);
                	endTimeArr.push(classSettList[index].endTime);
                });
                bPup(nameArr,idArr,mustAttnTimeArr,startTimeArr,endTimeArr,function(name,id,mustAttnTime,startTime,endTime){
                	 //班次改变了联动修改员工月上班总时间和底部班次信息
                	 if(classSetId_old!=id){
                		 $(currentObj).attr('id',id);
                		 $(currentObj).addClass('adjustT');
                		 if(typeof(classSetMustattntime_old)=="undefined"){
                			 classSetMustattntime_old = 0;
                		 }
                		 //修改总工时
                		 var new_mustAttnTime = parseFloat($(".on").attr("mustAttnTime"))-parseFloat(classSetMustattntime_old)+parseFloat(mustAttnTime);
                		 $(".on").find("div").find("p:eq(1)").text(new_mustAttnTime+"H/"+$(".on").attr("shouldTime")+"H");
                		 $(".on").attr("mustAttnTime",new_mustAttnTime);
                		 if(id==0){
                			 //修改日期上的属性和班次
                         	 $(currentObj).attr('name',name);
                         	 $(currentObj).attr('mustattntime',mustAttnTime);
                         	 $(currentObj).attr('startTime',startTime);
                             $(currentObj).attr('endTime',endTime);
                             if($(currentObj).find("p").length==0){
                         		 $(currentObj).find('span').append('<p></p>')
                          	 }else{
                          		 $(currentObj).find("p").text("");
                          	 }
                    		 $(currentObj).removeClass("current");
                    		 //修改底部班次
                    		 $('.workTime').html('');
                    		 $('.workTime').html('班次：无');
                		 }else{
                			 //修改日期上的属性和班次
                         	 $(currentObj).attr('name',name);
                         	 $(currentObj).attr('mustattntime',mustAttnTime);
                         	 $(currentObj).attr('startTime',startTime);
                          	 $(currentObj).attr('endTime',endTime);
                          	 if($(currentObj).find("p").length==0){
                         		 $(currentObj).find('span').append('<p>'+ name +'</p>')
                         	 }else{
                         		 $(currentObj).find("p").text(name);
                         	 }
                    		 //修改底部班次
                    		 $('.workTime').html('');
                    		 $('.workTime').html('班次： '+name+'  工作时间：'+startTime+' — '+endTime);
                		 }
                		 if(id==0&&typeof(classSetId_old)!="undefined"){
                			 //去除老班次的缓存
                			 $.ajax({
                					async:true,
                					type:'post',
                					dataType:'json',
                					data:{
                					  classDate:dateCurrent,
                	                  employeeId:$(".on").attr("employid"),
                	                  classSetId:classSetId_old,
                	                  classMonth:$("#classMonth").val(),
                	                  type:1
                					},
                					url:contextPath + "/employeeClass/removeEmployeeClassCache.htm",
                					success:function(data){
                					     
                					}
                			  });
                		 }
                		 if(id!=0){
                			 $.ajax({
               					async:true,
               					type:'post',
               					dataType:'json',
               					data:{
               					  classDate:dateCurrent,
               	                  employeeId:$(".on").attr("employid"),
               	                  classSetId:id,
               	                  classMonth:$("#classMonth").val()
               					},
               					url:contextPath + "/employeeClass/cacheMoveEmployeeClass.htm",
               					success:function(data){
               					     
               					}
               				});
                		 }
                	 }else{
                		 $(currentObj).removeClass('adjustT');
                		 if(typeof(classSetMustattntime_old)=="undefined"){
                			 classSetMustattntime_old = 0;
                		 }
                		 //修改总工时
                		 var new_mustAttnTime = parseFloat($(".on").attr("mustAttnTime"))-parseFloat(classSetMustattntime_old)+parseFloat(mustAttnTime);
                		 $(".on").find("div").find("p:eq(1)").text(new_mustAttnTime+"H/"+$(".on").attr("shouldTime")+"H");
                		 $(".on").attr("mustAttnTime",new_mustAttnTime);
                		 if(id==0){
                			 $(currentObj).removeClass('current'); 
                			 $(currentObj).attr('name',name);
                         	 $(currentObj).attr('mustattntime',mustAttnTime);
                         	 if($(currentObj).find("p").length==0){
                         		 $(currentObj).find('span').append('<p>'+ name +'</p>')
                          	 }else{
                          		 $(currentObj).find("p").text(name);
                          	 }
                    		 //修改底部班次
                    		 $('.workTime').html('');
                    		 $('.workTime').html('班次： 无');
                		 }else{
                			 $(currentObj).attr('name',name);
                         	 $(currentObj).attr('mustattntime',mustAttnTime);
                         	 if($(currentObj).find("p").length==0){
                         		 $(currentObj).find('span').append('<p>'+ name +'</p>')
                          	 }else{
                          		 $(currentObj).find("p").text(name);
                          	 }
                    		 //修改底部班次
                    		 $('.workTime').html('');
                    		 $('.workTime').html('班次： '+name+'  工作时间：'+startTime+' — '+endTime);
                		 }
                		 $.ajax({
           					async:true,
           					type:'post',
           					dataType:'json',
           					data:{
           					  classDate:dateCurrent,
           	                  employeeId:$(".on").attr("employid"),
           	                  classSetId:id,
           	                  classMonth:$("#classMonth").val(),
           	                  type:2
           					},
           					url:contextPath + "/employeeClass/removeEmployeeClassCache.htm",
           					success:function(data){
           					     
           					}
           				});
                	 }
                },function(){
	                 //取消回调
	               	 if(typeof(classSetId_old)=="undefined"){
	               		 $(currentObj).removeClass('current');
	               	 }else{
	               		 //修改底部班次
                		 if(classSetId_old==0){
                			 $(currentObj).removeClass('current');
                			 $('.workTime').html('');
                    		 $('.workTime').html('班次： 无');
                		 }else{
                			 $('.workTime').html('');
                    		 $('.workTime').html('班次： '+$(currentObj).attr("name")+'  工作时间：'+$(currentObj).attr("startTime")+' — '+$(currentObj).attr("endTime"));
                		 }
	               	 }
                });
            });
        }
    });
}
//点击员工切换日历
function switchCalendar(){
	$("#employeeList").find("li").each(function(){
		$(this).bind("click",function(){
			var employeeId = $(this).attr("employid");
			employeeClassList = [];
			getEmployeeClassSetByMonth(employeeId);
			initCalendar(employeeId);
		})
	})
}
//保存排班信息
function moveNormal(){
//	var i = 0;
//	$("#employeeList").find("li").each(function(){
//		var shouldtime = parseFloat($(this).attr("shouldtime"));
//		var mustattntime = parseFloat($(this).attr("mustattntime"));
//		if(mustattntime<shouldtime){
//			i++;
//		}
//	});
//	if(i>0){
//		OA.titlePup(i+'位员工工时不足，请修改排班！','lose');
//		return;
//	}
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
		    	OA.pageLoading(true);
		    	$.ajax({
		    		async:true,
		    		type:'post',
		    		dataType:'json',
		    		data:{
		    		  classMonth:$("#classMonth").val(),
		    		  classdetailId:$("#classdetailId").val(),
		    		  token:$("#token").val()
		    		},
		    		url:contextPath + "/employeeClass/moveEmployeeClass.htm",
		    		success:function(data){
		    			if(data.success){
							//跳页面
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
function bPup(war,war1,war2,war3,war4, call,call1) {
    if($('.pup-bottom').length > 0){return}
    var arr = [];
    arr.push('<div class="pup-bottom">')
    arr.push('<div class="pup-bg"></div>')
    arr.push('<div class="pb-main">')
    arr.push('<ul>')
    arr.push('<div class="t"></div>')
    arr.push('<div class="b"></div>')
    arr.push('<li class="no"></li>')
    for (var a = 0; a < war.length; a++) {
        arr.push('<li name='+war[a]+' startTime='+war3[a]+' endTime='+war4[a]+' mustAttnTime='+war2[a]+' id='+war1[a]+'>' + war[a] + '&nbsp;'+war3[a]+'~'+war4[a]+'</li>')
    }
    arr.push('<li class="no"></li>')
    arr.push('</ul>')
    arr.push('<div class="cancel">取消</div>')
    arr.push(' </div></div>')
    $('body').append(arr.join(''));
    var pb = $('.pup-bottom');
    $('.pup-bg , .cancel').click(function () { 
    	call1();
    	pb.remove() ;
    })
    $('.pb-main ul li').click(function () {
        call($(this).attr('name'),$(this).attr('id'),$(this).attr('mustAttnTime'),$(this).attr('startTime'),$(this).attr('endTime'));
        pb.remove();
    })
}