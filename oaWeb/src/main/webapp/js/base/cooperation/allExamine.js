var pageNoValue = 0;
var currentValue = 0;
$(function() {
	var calendar = new lCalendar();
	calendar.init({
	     'trigger': 'examineDate1',
	     'type': 'date'
	});
	allExamine();
	$("#allExamineDiv").scroll(function () {
	    //已经滚动到上面的页面高度
	   var scrollTop = $(this).scrollTop();
	    //页面高度
	   var scrollHeight = $("#allExamineList").height();
	     //浏览器窗口高度
	   var windowHeight = $(this).height();
	    //此处是滚动条到底部时候触发的事件，在这里写要加载的数据，或者是拉动滚动条的操作
	   if (scrollTop + windowHeight == scrollHeight) {
		  if(pageNoValue > currentValue) {
			  allExamine(pageNoValue, 10);
			  currentValue++;
		  }
	   }
	});
});

function onSearchBtn() {
	$("#theEnd").empty();
	$("#allExamineList").empty();
	allExamine();
}

function allExamine(pageNo, pageSize) {
	if(!pageNo) {
		pageNo = 0;
	}
	if(!pageSize) {
		pageSize = 10;
	}
	$.ajax({
	    type:"POST",
		url : contextPath + "/ruProcdef/getTaskPageList.htm?pageNo="+pageNo+"&pageSize="+pageSize+"&departId="+$("#partId").val()+"&nameOrCode=" + $("#nameOrCode").val() + "&startTime=" + $("#examineDate").val()+ "&endTime=" + $("#examineDate1").val(),
		data : '',
		dataType : 'json',
		success : function(response) {
			if((response.rows == null || response.rows == "" || response.rows.length < 1) && pageNo == 0) {
				var htm = "<div class='null pf'><div class='img p3'></div><p class='a'>暂无消息</p></div>";
				$("#allExamineList").append(htm);
			} else {
				for(var i=0;i<response.rows.length;i++){
					var classType = "";
					if(response.rows[i].reProcdefCode == 30) {
						classType = "delay_icon";
					} else if(response.rows[i].reProcdefCode == 40||response.rows[i].reProcdefCode == 120||response.rows[i].reProcdefCode == 41) {
						classType = "abnormal_icon";
					} else if(response.rows[i].reProcdefCode == 60) {
						classType = "leave_icon";
					} else if(response.rows[i].reProcdefCode == 70) {
						classType = "out_icon";
					} else if(response.rows[i].reProcdefCode == 80) {
						classType = "business_icon";
					}else if(response.rows[i].reProcdefCode == 81) {
						classType = "business_icon";
					} else if(response.rows[i].reProcdefCode == 90) {
						classType = "summary_icon";
					}else if(response.rows[i].reProcdefCode == 91) {
						classType = "summary_icon";
					} else if(response.rows[i].reProcdefCode == 100) {
						classType = "late_icon";
					}else if(response.rows[i].reProcdefCode == 50){
						classType = "worker_query";
					}else if(response.rows[i].reProcdefCode == 110){
						classType = "worker_query";
					}
					var html = "<a id="+response.rows[i].id+" href='"+contextPath + response.rows[i].url +"&urlType=10'>";
					//var html = "<a id="+response.rows[i].id+" href='"+contextPath + "/runTask/view.htm?urlType=10&ruTaskId=" + response.rows[i].id+"'>";
					html += "<li class='wait clearfix " +classType+ " refuse patternpt'>";
					html += "<div class='info fl'>";
					html += "<h3>" + response.rows[i].processName + "</h3>";
					html += "<p>" + response.rows[i].creatorDepart + "－" + response.rows[i].creator + "</p>";
					if(response.rows[i].subordinateName != undefined && response.rows[i].subordinateName != ''){
						html += "<p>下属考勤员工：" + response.rows[i].subordinateName + "</p>";
					}
					if(response.rows[i].createTime == undefined || response.rows[i].createTime == "undefined") {
						html += "<span></span>";
					} else {
						html += "<span>" + response.rows[i].createTime + "</span>";
					}
					html += "</div>";
					if(response.rows[i].processStatu == 100){
						html += "<div class='btn fr'>处理中</div>";
					} else if(response.rows[i].processStatu == 200){
						html += "<div class='btn fr'>已完成</div>";
					} else if(response.rows[i].processStatu == 300){
						if(response.rows[i].reProcdefCode == 110){
							html += "<div class='btn fr'>已打回</div>";
						}else{
							html += "<div class='btn fr'>已拒绝</div>";
						}
					} else if(response.rows[i].processStatu == 400){
						html += "<div class='btn fr'>已撤回</div>";
					} else {
						html +="<div class='btn fr'></div>";
					}
					/*html += "<div class='right-icon sr'></div>";*/
					html += "<i class='sr icon'></i></li>";
					html += "</a>";
					$("#allExamineList").append(html);
				}
			}
			if(response.rows.length < pageSize && pageNo != 0) {
				var htm = "<div class='theEnd'>已经到底了啦～</div>";
				$("#theEnd").empty();
				$("#theEnd").append(htm);
			} else {
				pageNoValue++; 
			}
		}
	 });
}
