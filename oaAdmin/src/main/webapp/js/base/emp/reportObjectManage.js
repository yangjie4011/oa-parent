$(function() {
	getReportPerson();
	
	$('#chooseEmployee').click(function(){
		$("input[name='pageStr']").val();
		$("#cancelQuitIds").val();
        $("#employeeIds").val();
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
	            $('#chooseEmployee').val(nameVal+'等,共计'+ids.length+'个员工');
	            $("#employeeIds").val(ids);
	        }
	    })
	})
});

//获取所有汇报对象
function getReportPerson(){
	$.ajax({
		async:true,
		type:'post',
		url:contextPath + "/employee/getAllReportPerson.htm",
		success:function(data){
			$.each(data, function(index) {
				$("#employeeLeader").append("<option value= " + data[index].id + ">" + data[index].cnName + "</option>");
			});
		}
	});
}
//保存
function save(){
	if($("#employeeIds").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择员工！");
		});
		return;
	}
	if($("#employeeLeader").val()==""){
		JEND.load('util.dialog', function() {
			JEND.util.dialog.alert("请选择汇报对象！");
		});
		return;
	}
	secondConfirmation({
		"msg":"是否确认修改汇报对象？",
		sureFn:function(){
			pageLoading(true);//开启动画
			var empIds = $("#employeeIds").val();
			var employeeLeader = $("#employeeLeader").val();
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{'empIds':empIds,'employeeLeader':employeeLeader},
				url:contextPath + "/employee/saveReportPerson.htm",
				success:function(data){
					if(data.success){
						JEND.page.alert({type:"success", message:data.message});
						$("#employeeIds").val('');
						$("#chooseEmployee").val('');
						$("#employeeLeader").val('');
					}else{
						JEND.load('util.dialog', function() {
							JEND.util.dialog.alert(data.message);
						});
					}
					pageLoading(false);//关闭动画
				},
				complete:function(XMLHttpRequest,textStatus){  
					if(XMLHttpRequest.status=="403"){
						JEND.page.alert({type:"error", message:"您没有该操作权限！"});
					}
					pageLoading(false);//关闭动画
		        }
			});
		}
	});
	
}