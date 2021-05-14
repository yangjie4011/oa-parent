var pageNo = 1;
$(function() {
	getEmpList(null,null);

	//绑定搜索框事件
    $('.searchStaff input').focus(function () {
        //惰性单例模式
        if (!$('.popserch').length) {
            $(this).closest('.searchStaff').addClass('fix');
            $('.under-kq').append('<div class="popserch"></div>');
        }
    })
	$(".searchBtn").click(function(){//自己重写的查询事件，不用前端方法，前端方法存在跨域问题,zhangjintao
        $(this).closest('.searchStaff').removeClass('fix');
        $('.popserch').remove();
		$('#empList').empty();
		getEmpList(null,null);
	});
    //取消搜索
    $('.returnsearch').click(function () {
        $(this).closest('.searchStaff').removeClass('fix');
        $('.popserch').remove();
    })
    
	$('#empInfo').scroll(function () {
	    //已经滚动到上面的页面高度
	   var scrollTop = $(this).scrollTop();
	    //页面高度
	   var scrollHeight = $('#empList').height();
	     //浏览器窗口高度
	   var windowHeight = $(this).height();
	    //此处是滚动条到底部时候触发的事件，在这里写要加载的数据，或者是拉动滚动条的操作
	   if (scrollTop + windowHeight == scrollHeight) {
//		   dragThis.insertDom();
		   getEmpList(++pageNo, 10);
	   }
	});
});

function getEmpList(pageNo, pageSize) {
	if(!pageNo) {
		pageNo = 1;
	}
	if(!pageSize) {
		pageSize = 11;
	}
	
	$.ajax({
	    type:"POST",
		url : contextPath + "/employeeApp/getPageList.htm",
		data : {'pageNo':pageNo,'pageSize':pageSize,'depart.id':$("#partId").val(),'nameOrCode':$("#nameOrCode").val(),sid:Math.random(),jobStatus:0},
		dataType : 'json',
		success : function(response) {
//        	$("#empList").empty();
			if(response.rows == null || response.rows == "" || response.rows.length < 1) {
				var htm = "<div class='theEnd'>已经到底了啦～</div>";
				$("#theEnd").empty();
				$("#theEnd").append(htm);
			} else {
				for(var i=0;i<response.rows.length;i++){
					var html = "<li onclick='check("+response.rows[i].id+")'>";
					html += "<div>";
					html += response.rows[i].cnName;
					html += "<span class='ad'>";
					if("" != getValByUndefined(response.rows[i].depart)){
						if(response.rows[i].depart.leaderDeptName != null) {
							html += response.rows[i].depart.leaderDeptName + "/" + response.rows[i].depart.name;
						} else {
							html += response.rows[i].depart.name;
						}
						
						if("" != getValByUndefined(response.rows[i].position) 
								&& "" != getValByUndefined(response.rows[i].position.positionName)){
							html += "/" + response.rows[i].position.positionName;
						}
					}
					html += "</span></div>";
					
					html += "<div class='mail'>";
					if("" != getValByUndefined(response.rows[i].email)){
						html += response.rows[i].email;
					}
					html += "</div>";
					
					html += "<div class='mobile'>";
					if("" != getValByUndefined(response.rows[i].extensionNumber)){
						html += response.rows[i].extensionNumber;
					}
					html += "</div>";
					html += "<i></i>";
					
					html += "</li>";
					$("#empList").append(html);
				}
				if((parseInt(response.total/pageSize) + 1) == pageNo) {
					var htm = "<div class='theEnd'>已经到底了啦～</div>";
					$("#theEnd").empty();
					$("#theEnd").append(htm);
				}
			}
		}
	 });
}

//查看
function check(id){
	$("#checkForm").attr("action",contextPath + "/employeeApp/toPersonDetail.htm");
	$("#checkEmployeeId").val(id);
	$("#checkForm").submit();
}
