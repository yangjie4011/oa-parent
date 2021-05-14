
$(function () {
})   


//保存
function save(){
	var oldPwd = $("#oldPwd").val();
  	if(oldPwd == ""){
  		OA.titlePup('请输入原密码！','lose');
  		return false;
  	}
  	var password = $("#password").val();
  	if(password == ""){
  		OA.titlePup('请输入新密码！','lose');
  		return false;
  	}
  	var password2 = $("#password2").val();
  	if(password2 == ""){
  		OA.titlePup('请输入确认密码！','lose');
  		return false;
  	}
	//正则 表达式    
	var pattern = /(?![0-9A-Z!@#$%]+$)(?![0-9a-z!@#$%]+$)(?![a-zA-Z!@#$%]+$)[0-9A-Za-z!@#$%]{10,18}$/;	
	if(password!=""){
		if(!pattern.test(password)){
			OA.titlePup('密码必须包含大小写字母，数字，!@#$%符号，且长度至少10位，不能超过18位。','lose');
	  		return false;
		}
	}
	
  	if(password != password2){
  		OA.titlePup('新密码和确认密码不一致！','lose');
  		return false;
  	}
  	
  	
  	
  	
	
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:$("#modifyForm").serializeParams(),
		url:contextPath + "/user/modifyPwd.htm",
		success:function(data){
			if(data.success){
				OA.titlePup('修改密码成功！','win');
				
				//登录系统
				window.location.href = contextPath + "/login/logout.htm";
			}else{
				OA.titlePup(data.message,'lose');
			}
		}
	});
}