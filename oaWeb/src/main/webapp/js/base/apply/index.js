var processingPageNoValue = 0;
var processingCurrentValue = 0;

var processedPageNoValue = 0;
var processedCurrentValue = 0;

var processbackPageNoValue = 0;
var processbackCurrentValue = 0;

var processallPageNoValue = 0;
var processallCurrentValue = 0;
$(function() {
	processing();
	$(window).scroll(function () {
	    //已经滚动到上面的页面高度
	   var scrollTop = $(this).scrollTop();
	    //页面高度
	   var scrollHeight = $(document).height();
	     //浏览器窗口高度
	   var windowHeight = $(this).height();
	    //此处是滚动条到底部时候触发的事件，在这里写要加载的数据，或者是拉动滚动条的操作
	   if (scrollTop + windowHeight == scrollHeight) {
//		   dragThis.insertDom();
		   if(processingPageNoValue > 0 && processingPageNoValue > processingCurrentValue) {
			   processingCurrentValue++;
			   processing(processingPageNoValue, 11);
		   }
		   
		   if(processedPageNoValue > 0 && processedPageNoValue > processedCurrentValue) {
			   processedCurrentValue++;
			   processed(processedPageNoValue, 11);
		   }
		   
		   if(processbackPageNoValue > 0 && processbackPageNoValue > processbackCurrentValue) {
			   processbackCurrentValue++;
			   processback(processbackPageNoValue, 11);
		   }
		   
		   if(processallPageNoValue > 0 && processallPageNoValue > processallCurrentValue) {
			   processallCurrentValue++;
			   processall(processallPageNoValue, 11);
		   }
	   }
	});
});



/**
 * 处理中
 */
function processing(pageNo, pageSize) {
	if(!pageNo) {
		pageNo = 0;
	}
	if(!pageSize) {
		pageSize = 11;
	}
	$.ajax({
	    type:"POST",
		url : contextPath + "/ruProcdef/getMyPageList.htm?flag=100&pageNo="+pageNo+"&pageSize="+pageSize,
		data : '',
		dataType : 'json',
		success : function(response) {
			if(pageNo == 0) {
				$("#processing").empty();
			}
			if((response.rows == null || response.rows == "" || response.rows.length < 1) && pageNo == 0) {
				var htm = "<div class='null pf'><div class='img p3'></div><p class='a'>暂无消息</p></div>";
				$("#processing").append(htm);
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
					}else if(response.rows[i].reProcdefCode == 90) {
						classType = "summary_icon";
					}else if(response.rows[i].reProcdefCode == 91) {
						classType = "summary_icon";
					}else if(response.rows[i].reProcdefCode == 100) {
						classType = "late_icon";
					}else if(response.rows[i].reProcdefCode == 10){
						classType = "worker_icon";
					}else if(response.rows[i].reProcdefCode == 50){
						classType = "worker_query";
					}else if(response.rows[i].reProcdefCode == 110){
						classType = "worker_query";
					}else if(response.rows[i].reProcdefCode == 130){
						classType = "leave_icon";
					}
					var html = "<a id="+response.rows[i].id+" href='"+contextPath + response.rows[i].url +"&urlType=6'>";
					html += "<li class='wait clearfix " +classType+ " refuse patternpt'>";
					html += "<div class='info fl'>";
					html += "<h3>" + response.rows[i].processName + "</h3>";
					html += "<p>" + response.rows[i].creatorDepart + "－" + response.rows[i].creator + "</p>";
					if(response.rows[i].subordinateName !=undefined&& response.rows[i].subordinateName != ''){
						html += "<p>下属考勤员工：" + response.rows[i].subordinateName + "</p>";
					}
					if(response.rows[i].createTime == undefined || response.rows[i].createTime == "undefined") {
						html += "<span></span>";
					} else {
						html += "<span>" + response.rows[i].createTime + "</span>";
					}
					html += "</div>";
					html += "<div class='btn fr'>处理中</div>";
					html += "<i class='sr icon'></i></li>";
					html += "</a>";
					$("#processing").append(html);
				}
				
			}
			if(response.rows.length < pageSize  && pageNo != 0) {
				var htm = "<div class='theEnd'>已经到底了啦～</div>";
				$("#processingEnd").empty();
				$("#processingEnd").append(htm);
			} else {
				processingPageNoValue++;
			}
		}
	 });
}

/**
 * 已完成
 */
function processed(pageNo,pageSize) {
	if(!pageNo) {
		pageNo = 0;
	}
	if(!pageSize) {
		pageSize = 11;
	}
	$.ajax({
	    type:"POST",
		url : contextPath + "/ruProcdef/getMyPageList.htm?flag=200,600,700&pageNo="+pageNo+"&pageSize="+pageSize,
		data : '',
		dataType : 'json',
		success : function(response) {
        	if(pageNo == 0) {
				$("#processed").empty();
			}
			if((response.rows == null || response.rows == "" || response.rows.length < 1) && pageNo == 0) {
				var htm = "<div class='null pf'><div class='img p3'></div><p class='a'>暂无消息</p></div>";
				$("#processed").append(htm);
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
					}else if(response.rows[i].reProcdefCode == 10){
						classType = "worker_icon";
					}else if(response.rows[i].reProcdefCode == 50){
						classType = "worker_query";
					}else if(response.rows[i].reProcdefCode == 110){
						classType = "worker_query";
					}else if(response.rows[i].reProcdefCode == 130){
						classType = "leave_icon";
					}
					var html = "<a id="+response.rows[i].id+" href='"+contextPath + response.rows[i].url +"&urlType=6'>";
					html += "<li class='wait clearfix " +classType+ " refuse patternpt'>";
					html += "<div class='info fl'>";
					html += "<h3>" + response.rows[i].processName + "</h3>";
					html += "<p>" + response.rows[i].creatorDepart + "－" + response.rows[i].creator + "</p>";
					if(response.rows[i].subordinateName !=undefined&& response.rows[i].subordinateName != ''){
						html += "<p>下属考勤员工：" + response.rows[i].subordinateName + "</p>";
					}
					if(response.rows[i].createTime == undefined || response.rows[i].createTime == "undefined") {
						html += "<span></span>";
					} else {
						html += "<span>" + response.rows[i].createTime + "</span>";
					}
					html += "</div>";
					
					if(response.rows[i].processStatu == 200){
						html += "<div class='btn fr'>已完成</div>";
					} else if(response.rows[i].processStatu == 600){
						html += "<div class='btn fr'>失效同意</div>";
					} else if(response.rows[i].processStatu == 700){
						html += "<div class='btn fr'>失效拒绝</div>";
					}
					html += "<i class='sr icon'></i></li>";
					html += "</a>";
					$("#processed").append(html);
				}
			}
			if(response.rows.length < pageSize && pageNo != 0) {
				var htm = "<div class='theEnd'>已经到底了啦～</div>";
				$("#processedEnd").empty();
				$("#processedEnd").append(htm);
			} else {
				processedPageNoValue++;
			}
		}
	 });
}

/**
 * 已撤回
 */
function processback(pageNo,pageSize) {
	if(!pageNo) {
		pageNo = 0;
	}
	if(!pageSize) {
		pageSize = 11;
	}
	$.ajax({
	    type:"POST",
		url : contextPath + "/ruProcdef/getMyPageList.htm?flag=400&pageNo="+pageNo+"&pageSize="+pageSize,
		data : '',
		dataType : 'json',
		success : function(response) {
			if(pageNo == 0) {
				$("#processback").empty();
			}
			if((response.rows == null || response.rows == "" || response.rows.length < 1) && pageNo == 0) {
				var htm = "<div class='null pf'><div class='img p3'></div><p class='a'>暂无消息</p></div>";
				$("#processback").append(htm);
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
					}else if(response.rows[i].reProcdefCode == 10){
						classType = "worker_icon";
					}else if(response.rows[i].reProcdefCode == 50){
						classType = "worker_query";
					}else if(response.rows[i].reProcdefCode == 110){
						classType = "worker_query";
					}else if(response.rows[i].reProcdefCode == 130){
						classType = "leave_icon";
					}
					var html = "<a id="+response.rows[i].id+" href='"+contextPath + response.rows[i].url+"&urlType=6'>";
					html += "<li class='wait clearfix " +classType+ " refuse patternpt'>";
					html += "<div class='info fl'>";
					html += "<h3>" + response.rows[i].processName + "</h3>";
					html += "<p>" + response.rows[i].creatorDepart + "－" + response.rows[i].creator + "</p>";
					if(response.rows[i].subordinateName !=undefined&& response.rows[i].subordinateName != ''){
						html += "<p>下属考勤员工：" + response.rows[i].subordinateName + "</p>";
					}
					if(response.rows[i].createTime == undefined || response.rows[i].createTime == "undefined") {
						html += "<span></span>";
					} else {
						html += "<span>" + response.rows[i].createTime + "</span>";
					}
					html += "</div>";
						html += "<div class='btn fr'>已撤回</div>";
					html += "<i class='sr icon'></i></li>";
					html += "</a>";
					$("#processback").append(html);
				}
			}
			
			if(response.rows.length < pageSize && pageNo != 0) {
				var htm = "<div class='theEnd'>已经到底了啦～</div>";
				$("#processbackEnd").empty();
				$("#processbackEnd").append(htm);
			} else {
				processbackPageNoValue++;
			}
		}
	 });
}

/**
 * 全部
 */
function processall(pageNo,pageSize) {
	if(!pageNo) {
		pageNo = 0;
	}
	if(!pageSize) {
		pageSize = 11;
	}
	$.ajax({
	    type:"POST",
		url : contextPath + "/ruProcdef/getMyPageList.htm?flag=all&pageNo="+pageNo+"&pageSize="+pageSize,
		data : '',
		dataType : 'json',
		success : function(response) {
			if(pageNo == 0) {
				$("#processall").empty();
			}
			if((response.rows == null || response.rows == "" || response.rows.length < 1) && pageNo == 0) {
				var htm = "<div class='null pf'><div class='img p3'></div><p class='a'>暂无消息</p></div>";
				$("#processall").append(htm);
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
					}else if(response.rows[i].reProcdefCode == 10){
						classType = "worker_icon";
					}else if(response.rows[i].reProcdefCode == 50){
						classType = "worker_query";
					}else if(response.rows[i].reProcdefCode == 110){
						classType = "worker_query";
					}else if(response.rows[i].reProcdefCode == 130){
						classType = "leave_icon";
					}
					var html = "<a id="+response.rows[i].id+" href='"+contextPath + response.rows[i].url+"&urlType=6'>";
					html += "<li class='wait clearfix " +classType+ " refuse patternpt'>";
					html += "<div class='info fl'>";
					html += "<h3>" + response.rows[i].processName + "</h3>";
					html += "<p>" + response.rows[i].creatorDepart + "－" + response.rows[i].creator + "</p>";
					if(response.rows[i].subordinateName !=undefined&& response.rows[i].subordinateName != ''){
						html += "<p>下属考勤员工：" + response.rows[i].subordinateName + "</p>";
					}
					if(response.rows[i].createTime == undefined || response.rows[i].createTime == "undefined") {
						html += "<span></span>";
					} else {
						html += "<span>" + response.rows[i].createTime + "</span>";
					}
					html += "</div>";
					//100-处理中、200-已完成、300-拒绝、400-撤消
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
					}else if(response.rows[i].processStatu == 500){
						html += "<div class='btn fr'>已失效</div>";
					}else if(response.rows[i].processStatu == 600){
						html += "<div class='btn fr'>失效同意</div>";											
					}else if(response.rows[i].processStatu == 700){
						html += "<div class='btn fr'>失效拒绝</div>";											
					} else {
						html +="<td></td>";
					}
					html += "<i class='sr icon'></i></li>";
					html += "</a>";
					$("#processall").append(html);
				}
				
			}
			if(response.rows.length < pageSize  && pageNo != 0) {
				var htm = "<div class='theEnd'>已经到底了啦～</div>";
				$("#processallEnd").empty();
				$("#processallEnd").append(htm);
			} else {
				processallPageNoValue++;
			}
		}
	 });
}

//处理中点击事件
function processingLI() {
	$("#processing").empty();
	processingPageNoValue = 0;
	processingCurrentValue = 0;

	processing();
}

//已完成点击事件
function processedLI() {
	$("#processed").empty();
	processedPageNoValue = 0;
	processedCurrentValue = 0;
	processed();
}

//已撤回点击事件
function processbackLI() {
	$("#processback").empty();
	processbackPageNoValue = 0;
	processbackCurrentValue = 0;
	processback();
}

//全部点击事件
function processallLI() {
	$("#processall").empty();
	processallPageNoValue = 0;
	processallCurrentValue = 0;
	processall();
}

