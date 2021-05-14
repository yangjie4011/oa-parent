var employeeClassList = [];
$(function(){
	//初始化部门员工姓名和总工时
	getEmployeeClassHours();
	//点击员工切换日历
	switchCalendar();
	//初始化员工指定月份排班数据信息
	getEmployeeClassSetByMonth($(".on").attr("employid"));
	//初始化日历
	initCalendar($(".on").attr("employid"));
})

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
    			$("#employeeList").append("<li mustAttnTime="+data[index].mustAttnTime+" employId="+data[index].employId+" class='on'><div class='mid'><p>"+data[index].employName+"</p><p>"+scale+"</p></div></li>");
    		}else{
    			$("#employeeList").append("<li mustAttnTime="+data[index].mustAttnTime+" employId="+data[index].employId+"><div class='mid'><p>"+data[index].employName+"</p><p>"+scale+"</p></div></li>");
    		}
		});
        OA.ySilde();
  });
}
//获取员工一个月所有排班信息
function getEmployeeClassSetByMonth(employeeId){
  $.ajaxSettings.async = false;
  $.getJSON(contextPath +'/employeeClass/getEmployeeClassSetByMonth.htm?attnApplicationEmployClassId='+$("#classdetailId").val()+"&employeeId="+employeeId,function(data){
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
            	var classSetId_old = $(this).attr('id');
            	var classSetName_old = $(this).attr('name');
            	var classSetMustattntime_old = $(this).attr('mustattntime');
                var dateCurrent = $(this).attr('date_day');
                var isMove = 0;
            	if($(this).hasClass("adjustT")){
            		isMove = 1;
            	}
                //获取指定日期排班
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
function bPup(war,war1,war2,war3,war4, call) {
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
    $('.pup-bg , .cancel').click(function () { pb.remove() })
    $('.pb-main ul li').click(function () {
        call($(this).attr('name'),$(this).attr('id'),$(this).attr('mustAttnTime'),$(this).attr('startTime'),$(this).attr('endTime'));
        pb.remove();
    })
}