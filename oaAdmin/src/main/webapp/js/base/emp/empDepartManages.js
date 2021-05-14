
$(function(){
	
	getFirstDepart();
	
	$('#clickMe').click(function(){
		$("input[name='pageStr']").val();
	    PersonSel_luffy.init({
	        conditions:['quitTimeBegin','quitTimeEnd'],
	        cb:function(result){
	            var nameVal = '';
	            var ids = [];
	            for(var item in result){
	            	if(result[item].children.length){
	            		var children = result[item].children;
	            		children.forEach(function(worker){
	            			if(!nameVal)nameVal+=children[0].name;
	            			ids.push(worker.id);
	            		})
	            	}
	            }
	            $("input[name='pageStr']").val(nameVal+'等,共计'+ids.length+'个员工');
	            $("#empDepartIds").val(ids);
	        }
	    })
	})
	
	
});

//弹出更新职位界面
function update(){
	if($("#firstDepart").val()==null || $("#firstDepart").val()==''){
		JEND.page.alert({type:"error", message:"请选择要修改的部门"});
		return;
	}
	
	if($("#empDepartIds").val()==null || $("#empDepartIds").val()==''){
		JEND.page.alert({type:"error", message:"请选择员工"});
		return;
	}
	pageLoading(true);//加载动画
	var data=$("#queryform").serialize();
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:basePath + "employee/updateEmpDepart.htm",
		success:function(data){
          if(data.flag==true){
          	closeDiv("updateDiv");
          	 $("#messageBox #messageContent").text(data.msg);  
	      	 $("#messageBox").show();
	      	 $("#firstDepart").val("");
	      	 $("#clickMe").val("");
	      	 $("#empDepartIds").val("");
          }else{
        	  closeDiv('updateDiv');
        	  if (data.code == '0002'){
        		  $("#messageBox #messageContent").text("操作失败,请与开发人员确认问题！");  
        		  $("#messageBox").show();        		  
        	  }
        	  closeDiv("updateDiv");
          }
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}
function reconfirm(){
	if($("#firstDepart").val()==null || $("#firstDepart").val()==''){
		JEND.page.alert({type:"error", message:"请选择要修改的部门"});
		return;
	}
	
	if($("#empDepartIds").val()==null || $("#empDepartIds").val()==''){
		JEND.page.alert({type:"error", message:"请选择员工"});
		return;
	}
	$("#updateDiv").show();
}

var closeDiv = function(divName){
	$("#"+divName).hide();
}
