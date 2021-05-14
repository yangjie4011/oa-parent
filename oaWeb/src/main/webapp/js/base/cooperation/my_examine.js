var pageNo = 1;
var currentValue = 1;
var flag=true;
$(function() {
	if($("#current").val() == 0) {
		$("#myExamineingList").parent().show();
		$("#myExaminedList").parent().hide();
		$("#myAbateList").parent().hide();
		getMyExamineing();
	} else {
		$("#myExamineingList").parent().hide();
		$("#myExaminedList").parent().show();
		myExamined(2);
	}
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
		  if(currentValue == 2) {
			  getMyExamined(++pageNo, 11);
		  }
	   }
	});
});


function approvalModel() {
	$.ajax({
	    type:"POST",
		url : contextPath + "/ruProcdef/getPageList.htm?flag=allsel",
		data : '',
		dataType : 'json',
		success : function(response) {
        	$("#myExamineingList").empty();
			if(response.rows == null || response.rows == "" || response.rows.length < 1) {
				var htm = "<div class='null pf'><div class='img p3'></div><p class='a'>暂无消息</p></div>";
				$("#myExamineingList").append(htm);
			} else {
				if($("#isLeader").val() == "true") {
					$("#myExamineingList").append("<li class='changePattern'><span class='allsel'><em class=''><i class='sr'></i></em><b>全选</b></span><div onclick='getMyExamineing()'>普通模式</div></li>");
				}		
				for(var i=0;i<response.rows.length;i++){
					var classType = "";
					if(response.rows[i].reProcdefCode == 30) {
						classType = "delay_icon";
						var html = getHtml(response,i,classType);
						$("#myExamineingList").append(html);
					} else if(response.rows[i].reProcdefCode == 40||response.rows[i].reProcdefCode == 120||response.rows[i].reProcdefCode == 41) {
						classType = "abnormal_icon";
						var html = getHtml(response,i,classType);
						$("#myExamineingList").append(html);
					} else if(response.rows[i].reProcdefCode == 60) {
						classType = "leave_icon";
						var html = getHtml(response,i,classType);
						$("#myExamineingList").append(html);
					} else if(response.rows[i].reProcdefCode == 70) {
						classType = "out_icon";
						var html = getHtml(response,i,classType);
						$("#myExamineingList").append(html);
					} else if(response.rows[i].reProcdefCode == 100) {
						classType = "late_icon";
						var html = getHtml(response,i,classType);
						$("#myExamineingList").append(html);
					}else if(response.rows[i].reProcdefCode == 50){
						classType = "worker_query";
						var html = getHtml(response,i,classType);
						$("#myExamineingList").append(html);
					}else if(response.rows[i].reProcdefCode == 110){
						classType = "worker_query";
						var html = getHtml(response,i,classType);
						$("#myExamineingList").append(html);
					}else if(response.rows[i].reProcdefCode == 130) {
						classType = "leave_icon";
						var html = getHtml(response,i,classType);
						$("#myExamineingList").append(html);
					}
				}
				$("#myExamineingList").append("<li class='lastwait'><a href='#' onclick='refuse()'>一键拒绝</a><a href='#' onclick='approval()'>一键同意</a></li>");
			}
		}
	 });
}

function getHtml(response,i,classType){
	var html = "";
	html += "<li class='wait clearfix " +classType+ " refuse patternsp'>";
	html += "<div class='info fl'>";
	html += "<h3>" + response.rows[i].processName + "</h3>";
	html += "<p class='name'><strong>" +response.rows[i].creator+ "</strong>　<em>" +response.rows[i].creatorDepart+ "</em></p>";
	if(response.rows[i].reProcdefCode == 30) {
		html += "<p class='name'><em>申请事由</em>　<b>" +response.rows[i].view3+ "</b></p>"; 
		html += "<p class='name'><em>时间时长</em>　<b>" +response.rows[i].view4+ "</b></p>"; 
		html += "<p class='name'><em>事由说明</em>　<b>" +response.rows[i].view5+ "</b></p>"; 
	} else if(response.rows[i].reProcdefCode == 40||response.rows[i].reProcdefCode == 41) {
		html += "<p class='name'><em>实际考勤时间</em>　<b>" +response.rows[i].view4+ "</b></p>"; 
		html += "<p class='name'><em>申诉考勤时间</em>　<b>" +response.rows[i].view3+ "</b></p>"; 
		html += "<p class='name'><em>申诉事由</em>　<b>" +response.rows[i].view5+ "</b></p>"; 
	} else if(response.rows[i].reProcdefCode == 60) {
		html += "<p class='name'><em>假期类型</em>　<b>" +response.rows[i].view3+ "</b></p>"; 
		html += "<p class='name'><em>时间时长</em>　<b>" +response.rows[i].view4+ "</b></p>"; 
		html += "<p class='name'><em>请假事由</em>　<b>" +response.rows[i].view5+ "</b></p>"; 
	} else if(response.rows[i].reProcdefCode == 70) {
		html += "<p class='name'><em>外出地点</em>　<b>" +response.rows[i].view3+ "</b></p>"; 
		html += "<p class='name'><em>时间时长</em>　<b>" +response.rows[i].view4+ "</b></p>"; 
		html += "<p class='name reason'><em>外出事由</em>　<b>" +response.rows[i].view5+ "</b></p>"; 
	} else if(response.rows[i].reProcdefCode == 100) {
		html += "<p class='name'><em>延时工作时间</em>　<b>" +response.rows[i].view3+ "</b></p>"; 
		html += "<p class='name'><em>允许晚到时间</em>　<b>" +response.rows[i].view4+ "</b></p>"; 
		html += "<p class='name'><em>实际晚到时间</em>　<b>" +response.rows[i].view5+ "</b></p>"; 
		html += "<p class='name'><em>申请理由</em>　<b>" +response.rows[i].view6+ "</b></p>";
	}else if(response.rows[i].reProcdefCode == 120){
		html += "<p class='name'><em>下属考勤员工</em>　<b>" +response.rows[i].view6+ "</b></p>"; 
		html += "<p class='name'><em>实际考勤时间</em>　<b>" +response.rows[i].view4+ "</b></p>"; 
		html += "<p class='name'><em>申诉考勤时间</em>　<b>" +response.rows[i].view3+ "</b></p>"; 
		html += "<p class='name'><em>申诉事由</em>　<b>" +response.rows[i].view5+ "</b></p>"; 
	}else if(response.rows[i].reProcdefCode == 130) {
		html += "<p class='name'><em>假期类型</em>　<b>" +response.rows[i].view3+ "</b></p>"; 
		html += "<p class='name'><em>销假时长</em>　<b>" +response.rows[i].view4+ "</b></p>"; 
		html += "<p class='name'><em>销假事由</em>　<b>" +response.rows[i].view5+ "</b></p>"; 
	}
	html += "</div>";
	html += "<div class='right-icon sr'></div>";
	html += "<i class='sr icon'></i><div class='singleSel' value='" +response.rows[i].processId+ "'><i class='sr'></i></div></li>";
	return html;
}

/**
 * 获取我的审批数据(待办)
 */
function getMyExamineing() {
	$.ajax({
	    type:"POST",
		url : contextPath + "/ruProcdef/getPageList.htm?flag=myExamine",
		data : '',
		dataType : 'json',
		success : function(response) {
        	$("#myExamineingList").empty();
			if(response.rows == null || response.rows == "" || response.rows.length < 1) {
				var htm = "<div class='null pf'><div class='img p3'></div><p class='a'>暂无消息</p></div>";
				$("#myExamineingList").append(htm);
			} else {
				if($("#isLeader").val() == "true") {
					$("#myExamineingList").append("<li onclick='approvalModel()' class='changePattern'><span class='allsel'></span>审批模式</li>");
				}		
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
					} else if(response.rows[i].reProcdefCode == 81) {
						classType = "business_icon";
					} else if(response.rows[i].reProcdefCode == 90) {
						classType = "summary_icon";
					} else if(response.rows[i].reProcdefCode == 91) {
						classType = "summary_icon";
					} else if(response.rows[i].reProcdefCode == 100) {
						classType = "late_icon";
					}else if(response.rows[i].reProcdefCode == 10){
						classType = "worker_icon";
					}else if(response.rows[i].reProcdefCode == 50){
						classType = "worker_query";
					}else if(response.rows[i].reProcdefCode == 110){
						classType = "worker_query";
					}else if(response.rows[i].reProcdefCode == 130) {
						classType = "leave_icon";
					} else if(response.rows[i].reProcdefCode == 140) {
						classType = "abnormal_icon";
					}
					
					var html = "<a id="+response.rows[i].id+" href='"+contextPath + "/ruProcdef/edit.htm?processId=" + response.rows[i].processId +"&redirectUrl="+response.rows[i].redirectUrl+ "&urlType=5'>";
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
					html += "<div class='right-icon sr'></div>";
					html += "<i class='sr icon'></i><div class='singleSel' value='" +response.rows[i].processId+ "'><i class='sr'></i></div></li>";
					html += "</a>";
					$("#myExamineingList").append(html);
				}
			}
		}
	 });
}

/**
 * 获取我的审批数据(已办)
 */
function getMyExamined(pageNo, pageSize) {
	if(!pageNo) {
		pageNo = 0;
	}
	if(!pageSize) {
		pageSize = 11;
	}
	$.ajax({
	    type:"POST",
		url : contextPath + "/ruProcdef/getHiPageList.htm?pageNo="+pageNo+"&pageSize="+pageSize,
		data : '',
		dataType : 'json',
		success : function(response) {
//        	$("#myExaminedList").empty();
			if((response.rows == null || response.rows == "" || response.rows.length < 1) && pageNo == 0) {
				var htm = "<div class='null pf'><div class='img p3'></div><p class='a'>暂无消息</p></div>";
				$("#myExaminedList").append(htm);
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
					}else if(response.rows[i].reProcdefCode == 130) {
						classType = "leave_icon";
					}else if(response.rows[i].reProcdefCode == 140) {
						classType = "abnormal_icon";
					}
					//点击跳转
					var html = "<a id="+response.rows[i].id+" href='"+contextPath + "/ruProcdef/view.htm?redirectUrl=" + response.rows[i].redirectUrl+"&urlType=5'>";
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
					//显示单据状态
					html += "<div class='btn fr'>"+response.rows[i].operation+"</div>";
					html += "<i class='sr icon'></i></li>";
					html += "</a>";
					$("#myExaminedList").append(html);
				}
			}
			if(response.rows.length < pageSize && pageNo != 0) {
				var htm = "<div class='theEnd'>已经到底了啦～</div>";
				$("#theEnd").empty();
				$("#theEnd").append(htm);
			} 
		}
	 });
}

/**
 * 获取我的审批数据(失效)
 */
function getMyAbate(pageNo, pageSize) {
	if(!pageNo) {
		pageNo = 0;
	}
	if(!pageSize) {
		pageSize = 11;
	}
	$.ajax({
	    type:"POST",
		url : contextPath + "/ruProcdef/getOverDuePageList.htm?pageNo="+pageNo+"&pageSize="+pageSize,
		data : '',
		dataType : 'json',
		success : function(response) {
//        	$("#myExaminedList").empty();
			if((response.rows == null || response.rows == "" || response.rows.length < 1) && pageNo == 0) {
				var htm = "<div class='null pf'><div class='img p3'></div><p class='a'>暂无消息</p></div>";
				$("#myAbateList").append(htm);
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
					}else if(response.rows[i].reProcdefCode == 130){//销假图标
						classType = "leave_icon";
					}else if(response.rows[i].reProcdefCode == 140) {
						classType = "abnormal_icon";
					}
					//点击跳转
					var html = "<a id="+response.rows[i].id+" href='"+contextPath + "/ruProcdef/view.htm?redirectUrl=" + response.rows[i].redirectUrl+"&urlType=5'>";
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
					//显示单据状态
					html += "<div class='btn fr'>"+response.rows[i].operation+"</div>";
					html += "<i class='sr icon'></i></li>";
					html += "</a>";
					$("#myAbateList").append(html);
				}
			}
			if(response.rows.length < pageSize && pageNo != 0) {
				var htm = "<div class='theEnd'>已经到底了啦～</div>";
				$("#myAbatetheEnd").empty();
				$("#myAbatetheEnd").append(htm);
			} 
		}
	 });
}

//待办点击事件
function myExamineing(current) {
	currentValue = current;
	$("#myExamineingList").empty();
	getMyExamineing();
}

//已办点击事件
function myExamined(current) {
	currentValue = current;
	$("#myExaminedList").empty();
	$("#theEnd").empty();
	pageNo = 0;
	getMyExamined(pageNo, 11);
}

//失效点击事件
function myAbate(current) {
	currentValue = current;
	$("#myAbateList").empty();
	$("#myAbatetheEnd").empty();
	pageNo = 0;
	getMyAbate(pageNo, 11);
}

function refuse() {
	var list=document.getElementsByClassName("singleSel current");
	var processIds = "";
	for(var i=0;i<list.length;i++){
		if(processIds != "") {
			processIds = processIds + ",";
		}
		processIds = processIds + list[i].getAttribute("value");
	}
	if(processIds != "") {
		if(flag){
			flag = false;
			OA.twoSurePop({
				tips:'确认一键拒绝？',
				sureFn:function(){
					OA.pageLoading(true);
					$.ajax({
						type:"POST",
						timeout : 5000,
						url : contextPath + "/ruProcdef/refuseTasks.htm?processIds="+processIds,
						data : '',
						dataType : 'json',
						success : function(data) {
							if(data.success){
								clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm");
							}else{
								OA.pageLoading(false);
								OA.titlePup(data.msg,'lose');
							}
						},
						complete:function(XMLHttpRequest,status){
				             if(status=="timeout"){
				            	    flag = true;
									OA.pageLoading(false);
									getMyExamineing();
				             }
						}
					});
				},
				cancelFn:function(){
					flag = true;
				}
			})
		} else {
			OA.titlePup('正在处理，主不要重复提交','lose');
		}
	} else {
		OA.titlePup('请选择操作数据','lose');
	}
}

function approval() {
	var list=document.getElementsByClassName("singleSel current");
	var processIds = "";
	for(var i=0;i<list.length;i++){
		if(processIds != "") {
			processIds = processIds + ",";
		}
		processIds = processIds + list[i].getAttribute("value");
	}
	if(processIds != "") {
		if(flag){
			flag = false;
			OA.twoSurePop({
				tips:'确认一键同意？',
				sureFn:function(){
					OA.pageLoading(true);
					$.ajax({
						type:"POST",
						timeout : 5000,
						url : contextPath + "/ruProcdef/passTasks.htm?processIds="+processIds,
						data : '',
						dataType : 'json',
						success : function(data) {
							if(data.success){
								flag=true;
								clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm");
							}else{
								OA.pageLoading(false);
								flag=true;
								OA.titlePup(data.msg,'lose');
								getMyExamineing();
							}
						},
						complete:function(XMLHttpRequest,status){
				             if(status=="timeout"){
				            	    flag = true;
									OA.pageLoading(false);
									getMyExamineing();
				             }
						}
					});
				},
				cancelFn:function(){
					flag = true;
				}
			})
		} else {
			OA.titlePup('正在处理，主不要重复提交','lose');
		}
	} else {
		OA.titlePup('请选择操作数据','lose');
	}
}

