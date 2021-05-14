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