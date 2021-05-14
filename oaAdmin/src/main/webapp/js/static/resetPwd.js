var wait=60; 
$(function () {
	$(".yanz").click(function(){
		if(!$("#btn").hasClass("disabled")){//禁用图标
			$("#btn").addClass("disabled");
			time(wait);
		}else{//图标目前还处于禁用，不再重复发送短信
			return;
		}
		
		var phone = $("#phone").val();
		if(!phone == ""){
			//发送验证码
			sendRadmonCode();
		}
    })
    $('#username').keydown(function(e){
		//回车
		if(e.keyCode==13){
			getPhone($(this).val());
		}
	});
	updatePass();
})   

function time(wait) {		
        if (wait == 0) {	        	
        	$("#btn").removeClass("disabled");         
            $("#getyzm").text("免费获取验证码");
            wait = 60; 
        } else { 
            $("#getyzm").text(wait+"秒后可以重新发送"); 
            wait--; 
            setTimeout(function() { 
                time(wait) 
            }, 
            1000) 
        } 
    } 

//保存
function save(){
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: $("#updateForm").serialize(),
		url:basePath + "user/resetPwd.htm",
		success:function(data){
			if(data.success){
				 JEND.page.alert({type:"error", message:"重置密码成功"});
				//登录系统
				window.location.href = contextPath + "/login/logout.htm";
			}else{
				JEND.page.alert({type:"error", message:data.message});										
			}
		}
	});
}

//获得手机号
function getPhone(username){
	$.ajax({
   		async : true,
   		type : "post",
 		dataType:"json",
 		data : {'userName':username},
   		url : basePath + "employee/getEmpMobileByUserName.htm",
   		success : function(data) {
   			if(data.success){
   				$("#phone").val(data.result.mobile);
   			}else{
				var name=$("#username").val();					
				if(name==""){								
				}else{
					$("#username").val("");	
					JEND.page.alert({type:"error", message:data.message});	
				}
   			}
   		}
	});
}




//按钮认证


function checkPassLevel(v){
	
	var flag = false;
	
	if(v.length>=8){
		var type = 0; // 记录包含几种
		if(v.match(/\d+/g)) { // 数字
			type+=1;
		}
		if(v.match(/[a-z]+/g)){ // 小写字母
			type+=1;
		}
		if(v.match(/[A-Z]+/g)){ // 大写字母
			type+=1;
		}
		if(v.match(/[\u4E00-\u9FFF]+/g)){ // 汉字
			type+=1;
		}
		if(v.match(/[`~!@#$%^&*()\-_+=|\\\][\]\{\}:;'\,.<>?\/]+/g)){ // 特殊字符
			type+=1;
		}
		
		var safe1 = $("#updatePass #safe1 i"); // 安全级别
		var safe2 = $("#updatePass #safe2 i"); // 第一条规则
		var safe3 = $("#updatePass #safe3 i"); // 第二条规则
		
		safe1.attr("class","login-icon safe1"); // 1颗星
		safe2.attr("class","login-icon safe1"); // 满足第一条规则
		safe3.attr("class","login-icon safe");	
		
		if (type == 2 || type == 3) {
			safe2.attr("class","login-icon safe1");
			safe3.attr("class","login-icon safe1");
			flag = true;
			if(type==2){
				safe1.attr("class","login-icon safe2"); // 2颗星
			}
			else if(type>=3){ 
				safe1.attr("class","login-icon safe3");// 3颗星
			}
		}		
	} else{
		$("#updatePass .safetip ul i").attr("class","login-icon safe");
	}
	return flag;
}

function updatePass(){
	
	var  level = false; // 是否满足密码规则
	var np2tips = $("#updatePass #newpass2").next(); //确认密码提示
	
	// 密码明、暗文切换
	var ele_pass = document.getElementById('password');
	var ele_eye = document.getElementById('eye1');
	ele_eye.onclick = function () {
		var state = this.getAttribute("state");
		if(state === "off") {
		ele_pass.setAttribute("type", "text");
		ele_eye.setAttribute("state", "on");
		} else {
		ele_pass.setAttribute("type", "password");
		ele_eye.setAttribute("state", "off");
	  }
	}	
	// 密码明、暗文切换
	var ele_pass2 = document.getElementById('password2');
	var ele_eye2 = document.getElementById('eye2');
	ele_eye2.onclick = function () {
		var state = this.getAttribute("state");
		if(state === "off") {
		ele_pass2.setAttribute("type", "text");
		ele_eye2.setAttribute("state", "on");
		} else {
		ele_pass2.setAttribute("type", "password");
		ele_eye2.setAttribute("state", "off");
	  }
	}	
	
	$("#updatePass input").on({
		
		'click':function(){
			var safetip = $("#updatePass .safetip");
			var p = $(this).closest(".form");
			var id  = p.attr("id");
			
			if (id == "newpass1"){
				safetip.show();
			}else{
				safetip.hide();
			}
		},
		'a':function(){
			
		}
		,
		'keyup':function(){
			
			var p = $(this).closest(".form");
			var id  = p.attr("id");
			var v = $.trim($(this).val());
			
			var pass  = p.find("input[type=password]"); // 暗文
			var text  = p.find("input[type=text]"); 	// 明文
			
			if ($(this).hasClass("form-pass")) {
				pass.val(v);
			}else{
				text.val(v)
			}
			//var op = $("#updatePass #oldpass input[type=password]").val(); // 旧密码
			var np1 = $("#password").val(); // 新密码
			var np2 = $("#password2").val(); // 确认密码
			var randomCode=$("#randomCode").val();//验证码
			var phone=$("#phone").val();//手机
			if (id == "newpass1"){
				level = checkPassLevel(v);
			}
			if (id == "newpass2" && level ){
				if (np1 != np2){
					np2tips.show();
				}else{
					np2tips.hide();
				}
			}
			if (level && (np1 == np2) ){
				$("#updatePass .red-but").removeClass("disabled");
			}else{
				$("#updatePass .red-but").addClass("disabled");
			}
		}
	});
	
	
	//按钮验证
	$("#updatePass .red-but").click(function(){
		if($("#updatePass .red-but").hasClass("disabled")) return false;
		else{
			save();
		}
	});
}


//如果节点值为空，转json默认会去掉这个节点
function getValByUndefined(nodeVal){
	if("undefined" == nodeVal || "undefined" == typeof(nodeVal)){
		return "";
	}
	
	return nodeVal;
}