$(function () {
	$(".yanz").click(function(){
		var phone = $("#phone").val();
		if(!phone == ""){
			OA.identifyingCode($(this));
			
			//发送验证码
			sendRadmonCode();
		}
    })
})   


//保存
function save(){
	var username = $("#username").val();
	if(username == ""){
		OA.titlePup('请输入要找回的帐号！','lose');
		return false;
	}
	var randomCode = $("#randomCode").val();
	if(randomCode == ""){
		OA.titlePup('请输入验证码！','lose');
		return false;
	}
	
  	var password = $("#password").val();
  	var reg = /(?![0-9A-Z!@#$%]+$)(?![0-9a-z!@#$%]+$)(?![a-zA-Z!@#$%]+$)[0-9A-Za-z!@#$%]{10,18}$/;
  	if(password == ""){
  		OA.titlePup('请输入新密码！','lose');
  		return false;
  	}
	if(reg.test(password) === false){
		OA.titlePup('员工通行证密码必须包含大小写字母，数字，!@#$%符号，且长度至少10位，不能超过18位。','lose');
  		return false;
	}
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:$("#modifyForm").serializeParams(),
		url:contextPath + "/user/resetPwd.htm",
		success:function(data){
			if(data.success){
				OA.titlePup('重置密码成功！','win');
				
				//登录系统
				window.location.href = contextPath + "/login/logout.htm";
			}else{
				OA.titlePup(data.message,'lose');
			}
		}
	});
	
	$('#username').keydown(function(e){
		//回车
		if(e.keyCode==13){
			getPhone($(this).val());
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
   		url : contextPath + "/employeeApp/getEmpMobileByUserName.htm",
   		success : function(data) {
   			if(data.success){
   				$("#phone").val(data.result.mobile);
   			}else{
   				OA.titlePup(data.message,'lose');
   			}
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