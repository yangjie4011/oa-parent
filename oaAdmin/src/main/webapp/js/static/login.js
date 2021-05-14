$(function() {
	$(".login-btn").on("click", function() {
		var username = $("#username").val();
		if(username == ""){
			JEND.page.showError('用户名不能为空');
			return false;
		}
		var password = $("#password").val();
		if(password == ""){
			JEND.page.showError('密码不能为空');
			return false;
		}
		
		JEND.load('util.dialog', function() {
			JEND.util.dialog.showLoading({loadingText:'正在登录中…'});
		});
		
		$.ajax({
    	    type:"POST",
    		url : contextPath + "/login/login.htm",
    		data : {username: $("#username").val(),password: $("#password").val(),remFlag:$("#remFlag").val()},
    		dataType : 'json',
    		success : function(data) {
    			if (!data.success) {
					JEND.page.showSuccess(data.message);
				}else{
					//获得缓存中的密码
					getPwd();
					window.location.href = contextPath + "/login/index.htm";				
				}
    		},  
	        error:function(data, status, e){ //服务器响应失败时的处理函数  
	        	JEND.page.showError(data.message);
	        }
    	});
	});
	
	$('.radio').click(function () {
        if($(this).hasClass('on')){
            $(this).removeClass('on');
            remember(false);
        }else{//记住密码
            $(this).addClass('on');
            remember(true);
        }
    })
    //获得缓存中的密码
	getPwd();
});

function getPwd(){
	//记住密码功能
    var str = getCookie("loginInfo");
    if(str.charAt(0)=='"'){//小写的时候，去掉两边的双引号
    	str = str.substring(1,str.length-1);
    }else{//大写的时候，直接取值
    	str = str.substring(0,str.length);
    }
    var username = decodeURIComponent(str.split("!!||")[0]);
    var password = decodeURIComponent(str.split("!!||")[1]);
    var remFlag = str.split("!!||")[2];
    var pic = decodeURIComponent(str.split("!!||")[3]);
    $("#ePic").css("background-image","url(" + pic + ")");
    //自动填充用户名和密码
    $("#username").val(getValByUndefined(username));
    $("#password").val(getValByUndefined(password));
    if('' == getValByUndefined(remFlag)){
    	$("#remFlag").val("");
    	$('.radio').removeClass('on');
    }else{
    	$("#remFlag").val("1");
    	$('.radio').addClass('on');
    }
}


//记住密码功能
function remember(remFlag){
	if(remFlag==true){ //如果选中设置remFlag为1
		$("#remFlag").val("1");
		$('.radio').addClass('on');
    }else{ //如果没选中设置remFlag为""
        $("#remFlag").val("");
    }
}

//获取cookie
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) != -1) return c.substring(name.length, c.length);
    }
    return "";
}

//如果节点值为空，转json默认会去掉这个节点
function getValByUndefined(nodeVal){
	if("undefined" == nodeVal || "undefined" == typeof(nodeVal)){
		return "";
	}
	
	return nodeVal;
}

//检查用户头像
function checkPicByName(name){
	//验证页面输入的用户和缓存中的用户是否一致
	var str = getCookie("loginInfo");
	if(str.charAt(0)=='"'){//小写的时候，去掉两边的双引号
    	str = str.substring(1,str.length-1);
    }else{//大写的时候，直接取值
    	str = str.substring(0,str.length);
    }
    var username = decodeURIComponent(str.split("!!||")[0]);
    var pic = decodeURIComponent(str.split("!!||")[3]);
    
    //判断输入的用户名和缓存中的不一致，清空图片
    if(name == username){
    	$("#ePic").css("background-image","url(" + pic + ")");
    }else{//清空图片
    	$("#ePic").css("background-image","url('//i0.beta.ulecdn.com/ule/oa/i/head.jpg')");
    }
}
