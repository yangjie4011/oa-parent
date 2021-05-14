var pageNoValue = 0;
var currentValue = 0;
$(function() {
	getMyExamined();
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
		   if(pageNoValue > 0 && pageNoValue > currentValue) {
			   currentValue++;
			   getMyExamined(pageNoValue, 11);
		   }
	   }
	});
});

function getMyExamined(pageNo, pageSize) {
	if(!pageNo) {
		pageNo = 0;
	}
	if(!pageSize) {
		pageSize = 11;
	}
	$.ajax({
	    type:"POST",
		url : contextPath + "/empMsg/getList.htm?pageNo="+pageNo+"&pageSize="+pageSize,
		data : '',
		dataType : 'json',
		success : function(response) {
			if(pageNo == 0) {
				$("#mynewsList").empty();
			}
			if((response.rows == null || response.rows == "" || response.rows.length < 1) && pageNo == 0) {
				var htm = "<div class='null'><div class='img p3'></div><p class='a'>暂无消息</p></div>";
				$("#mynewsList").append(htm);
			} else {
				for(var i=0;i<response.rows.length;i++){
					var readFlag = "unread";
					if(response.rows[i].readFlag == 1) {
						readFlag = "unread";
					} else {
						readFlag = "read";
					}
					var html = "<li onclick='read(" +response.rows[i].id +")' id='read" +response.rows[i].id+ "' class='" +readFlag+ "'>";
					html += "<h3><em>" + response.rows[i].title + "</em><span>" + response.rows[i].crTime;
					html += "</span></h3><p>" + response.rows[i].content;
					html += "</p></li>";
					$("#mynewsList").append(html);
				}
				if(response.rows.length < pageSize) {
					var htm = "<div class='theEnd'>已经到底了啦～</div>";
					$("#theEnd").append(htm);
				} else {
					pageNoValue++;
				}
			}
		}
	 });
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
				$("#read" + id).removeClass("unread");
			    $("#read" + id).addClass("read");
			}else{
				OA.titlePup(data.message,'lose');
			}
		}
	});
}