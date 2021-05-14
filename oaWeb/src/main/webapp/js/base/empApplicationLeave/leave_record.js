$(function() {
	
	getRecordList();
	
	$(".thisYear,.lastYearUl,.lastYear").click(function(){
		var thisYear = $(".thisYear");
		var lastYearUl = $(".lastYearUl");
		var lastYear = $(".lastYear");
        var id =$(this).attr("id");
		if(thisYear.is(':hidden') || lastYearUl.is(':hidden')){//有一个是影藏的
			
			lastYear.hide();
			thisYear.show();
			lastYearUl.show();
			return;
		}
		
		if(thisYear.is(':visible') && lastYearUl.is(':visible')){//有两个是显示的
			thisYear.hide();
			lastYear.hide();
			lastYearUl.hide();
			if(id=="thisYear" || id=="thisYearUl"){
				$(".thisYear").show();//当年
				getRecordList("thisYear");
			}else if(id=="lastYear" || id=="lastYearUl"){
				$(".lastYear").show();//去年
				getRecordList("lastYear");
			}
		}
		
	})
});


function getRecordList(year){
	OA.pageLoading(true);
	$.ajax({
		async:true,
		type:'post',
		timeout : 5000,
		dataType:'json',
		data:{
			yearType:year
		},
		url:contextPath + "/empLeave/getRecordList.htm",
		success:function(data){
			var list = $(".punchtime");
			list.empty();
			if(data.success){
				var html = '';
				$.each(data.list, function(index) {
					var daysUnit ="天";//单位
					var days = 0-parseFloat(data.list[index].days);//天数
					if(days>0){
						days = "+"+days;
					}
					if(data.list[index].daysUnit==1){
						daysUnit = "小时";
					}
					var leaveType = "";//假期类型
					var leaveTypeC = "";//假期子分类
					if(data.list[index].type==1){
						leaveType = "年假";
						if(data.list[index].category==1){
							leaveTypeC = "法定";
						}else if(data.list[index].category==2){
							leaveTypeC = "福利";
						}
					}else if(data.list[index].type==2){
						leaveType = "病假";
						leaveTypeC = "非带薪";
						if(data.list[index].category==0){
							leaveTypeC = "带薪";
						}
					}else if(data.list[index].type==3){
						leaveType = "婚假";
					}else if(data.list[index].type==4){
						leaveType = "哺乳假";
					}else if(data.list[index].type==5){
						leaveType = "调休";
					}else if(data.list[index].type==6){
						leaveType = "产前假";
					}else if(data.list[index].type==7){
						leaveType = "产假";
					}else if(data.list[index].type==8){
						leaveType = "流产假";
					}else if(data.list[index].type==9){
						leaveType = "陪产假";
					}else if(data.list[index].type==10){
						leaveType = "丧假";
					}else if(data.list[index].type==11){
						leaveType = "事假";
					}else if(data.list[index].type==12){
						leaveType = "其他";
					}else if(data.list[index].type==13){
						leaveType = "调休";
					}
					html += '<li billId='+data.list[index].billId+' billType='+data.list[index].billType+' onclick="getBillInfo(this);"><div class="abnormalIntro"><div><p style="width:60%;float:left;" class="date attnDate">'+leaveType+'&nbsp;&nbsp;&nbsp;'+leaveTypeC+'</p><p style="width:40%;float:left;text-align:right;"class="date attnDate">'+days+''+daysUnit+'</p></div>';
					var startTime = data.list[index].startTime;
					var endTime = data.list[index].endTime;
					var remark = data.list[index].remark!=null?data.list[index].remark:"";
					if(startTime!=null||endTime!=null){
						var timeStr = startTime +"-"+endTime;
						html +='<p class="name clearfix"><span>备注</span><span>'+remark+'</span></p><em class="late" style="color:#b2b2b2;">'+timeStr+'</em></div>';
					}else{
						html +='<p class="name clearfix"><span>备注</span><span>'+remark+'</span></p></div>';
					}
					//存在单据id的需要看详情
					if(data.list[index].billId!=0&&data.list[index].billType!="registerLeave"){
						html +='<i class="right-icon"></i>';
					}
					html +='</li>';
				});
				list.html(html);
			}
		},
		complete:function(){
			OA.pageLoading(false);
		}
	});
}

function getBillInfo(obj){
	var billId = $(obj).attr("billId");
	var billType = $(obj).attr("billType");
	if(billId!=0){
		window.location.href = contextPath + "/empLeave/billDetail.htm?billId="+billId+"&billType="+billType;	
	}
}