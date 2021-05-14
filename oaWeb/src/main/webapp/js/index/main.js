$(function() {
	var unReadCount = 0;
	getRuProcdefList();
	unReadMsgCount();
});

/**
 * 获取我的待办数据
 */
function getRuProcdefList() {
	$.ajax({
	    type:"POST",
		url : contextPath + "/ruProcdef/getPageList.htm?flag=main",
		data : '',
		dataType : 'json',
		success : function(response) {
			$("#rpCount").empty();
			$("#unCount1").empty();
			var total = parseInt(response.total);
			if(total > 0) {
				var htl ="<span class='num'>"+total+"</span>";
				$("#rpCount").append(htl);
				$("#unCount1").append("(" + total + ")");
			}
        	$("#ruProcdefList").empty();
        	$("#noData").empty();
			if(response.rows == null || response.rows == "" || response.rows.length < 1) {
				var htm = "<div class='null'><div class='img p3'></div><p class='a'>暂无消息</p></div>";
				$("#noData").append(htm);
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
					} else if(response.rows[i].reProcdefCode == 90) {
						classType = "summary_icon";
					} else if(response.rows[i].reProcdefCode == 100) {
						classType = "late_icon";
					}else if(response.rows[i].reProcdefCode == 50){
						classType = "scheduling_icon";
					}
					var html = "<a id="+response.rows[i].id+" href='"+contextPath + "/runTask/edit.htm?ruProcdefId=" + response.rows[i].id+"&urlType=1'>";
					html += "<li class='wait clearfix " +classType+ " refuse patternpt'>";
					html += "<div class='info fl'>";
					html += "<h3>" + response.rows[i].processName + "</h3>";
					html += "<p>" + response.rows[i].creatorDepart + "－" + response.rows[i].creator + "</p>";
					if(response.rows[i].createTime == undefined || response.rows[i].createTime == "undefined") {
						html += "<span></span>";
					} else {
						html += "<span>" + response.rows[i].createTime + "</span>";
					}
					html += "</div>";
					/*html += "<div class='btn fr'><a id="+response.rows[i].id+" href='"+contextPath + "/runTask/edit.htm?ruProcdefId=" + response.rows[i].id+"'>立即处理</a></div>";*/
					html += "<i class='arrow sr'></i></div><i class='sr icon'></i></li>";
					html += "</a>";
					$("#ruProcdefList").append(html);
				}
				if(response.total > 4) {
					var htm = "<li class='load-more'><a href='"+contextPath+"/ruProcdef/my_examine.htm'>查看更多</a></li>";
					$("#ruProcdefMore").append(htm);
				}
			}
		}
	 });
}

/**
 * 获取消息提醒数据
 */
function getEmpMsgList() {
	$.ajax({
	    type:"POST",
		url : contextPath + "/empMsg/getUnReadPageList.htm?flag=main",
		data : '',
		dataType : 'json',
		success : function(response) {
        	$("#empMsgList").empty();
        	$("#noData").empty();
			if(response.rows == null || response.rows == "" || response.rows.length < 1) {
				var htm = "<div class='null'><div class='img p3'></div><p class='a'>暂无消息</p></div>";
				$("#noData").append(htm);
			} else {
				for(var i=0;i<response.rows.length;i++){
					var html = "<li onclick='read(" +response.rows[i].id +")' id='read" +response.rows[i].id+ "' class='new clearfix'>";
					html += "<div class='info fl'>";
					html += "<h3>" + response.rows[i].content + "</h3>";
					if(response.rows[i].createTime == undefined || response.rows[i].createTime == "undefined") {
						html += "<span></span>";
					} else {
						html += "<span>" + response.rows[i].createTime + "</span>";
					}
					html += "</div>";
					html += "<i class='icon sr'></i></li>";
					$("#empMsgList").append(html);
				}
				if(response.total > 4) {
					var htm = "<li class='load-more'><a href='"+contextPath + "/empMsg/index_mynews.htm'>查看更多</a></li>";
					$("#empMsgMore").append(htm);
				}
			}
		}
	 });
}

//待办点击事件
function ruProcdef() {
	$("#ruProcdefList").empty();
	$("#ruProcdefMore").empty();
	getRuProcdefList();
}

//消息提醒点击事件
function empMsg() {
	$("#empMsgList").empty();
	$("#empMsgMore").empty();
	getEmpMsgList();
}

function read(id) {
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{id:id},
		url:contextPath + "/empMsg/read.htm",
		success:function(data){
			if(data.success){
				/*$("#read" + id).empty(); */
			}else{
				$.messager.alert('提示信息', data.message, "info", function() {});
			}
		}
	});
}

//获取未读消息数
function unReadMsgCount() {
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:'',
		url:contextPath + "/empMsg/getUnReadCount.htm",
		success:function(data){
			$("#unCount2").empty();
			var htm = "<dd class=\"on\"></dd>";
			if(parseInt(data) > 0) {
				$("#unReadMsgCount").append(htm);
				$("#unCount2").append("(" + data + ")");
			}
		}
	});
}
