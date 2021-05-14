$(function() {
	
	getSubAttendance();

	
	//绑定搜索框事件
    $('.searchStaff input').focus(function () {
        //惰性单例模式
        if (!$('.popserch').length) {
            $(this).closest('.searchStaff').addClass('fix');
            $('.under-kq').append('<div class="popserch"></div>');
        }
    })
	$(".searchBtn").click(function(){
        $(this).closest('.searchStaff').removeClass('fix');
        $('.popserch').remove();
		getSubAttendance();
	});
    //取消搜索
    $('.returnsearch').click(function () {
        $(this).closest('.searchStaff').removeClass('fix');
        $('.popserch').remove();
    })
});

var getSubAttendance = function(){
	var employId=$("#employId").val(),
		startTime=$("#startTime").val(),
	    endTime=$("#endTime").val(),
	    departId=$("#partId").val(),
	    nameOrCode=$("#nameOrCode").val();
	
	if(employId=='' || startTime=='' || endTime ==''){
		return false;
	}
	$.ajax({
	    type:"POST",
		url : contextPath + "/empAttn/getSubAttStatisticsList.htm?flag=attendance",
		data : {employId:employId,startTime:startTime,
			    endTime:endTime,departId:departId,
			    nameOrCode:nameOrCode},
		dataType : 'json',
		success : function(response) {
			$(".main > .liUl").empty();
			if(''== response || response == null ) {
				$(".main > .liUl").append("<li><div><p>未查询到异常考勤数据</p></div></li>");
			} else {
					var li = '';
					$.each(response,function(index,item){
						 li = li+'<li><div style="display:none;" class="employeeId">'+item.employeeId+'</div><div>'+
						     item.employeeName+'</div><div>本月累计出勤：'+
							 item.totalAllAttnTime+'小时</div><div class="p3">异常考勤：'+
							 item.errorTimes+'次</div><i class="right-icon"></i></li>';
					})
					$(".main > .liUl").append(li);
					//绑定点击事件
					$(".main > .liUl > li").click(function(){
						var li = $(this);
						liClick(li);
					});
			}
        	
		}
	 });
}

var liClick = function(li){
	var employeeId = li.find(".employeeId").text();
    $("#employId").val(employeeId);
	$("#subForm").attr("action",contextPath + "/empAttn/subIndex.htm");
	$("#subForm").submit();
}
