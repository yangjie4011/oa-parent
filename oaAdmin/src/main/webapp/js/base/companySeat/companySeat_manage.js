$(function(){
	//初始化楼层下拉框
	floorInit();
	
	//初始化座位图
	setPicUrl();
	
	$("#floor").change(function(){
		setPicUrl();
	});
})

function floorInit(){
	var options = "";
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{},
		url:contextPath + "/companySeat/floors.htm",
		success:function(data){
			for (var int = 0; int < data.length; int++) {
				options += "<option value='" + data[int].id + "'>" + data[int].name + "</option>";
			}
		}
	});
	$("#floor").html(options);
}

function save(){
	var fileName = $("#file").val();
	if(fileName == ""){
		$.messager.alert('警告信息', "请选择图片!", "warning");
		return;
	}
	var floorNum = $("#floorNum").val();
	if(floorNum == ""){
		$.messager.alert('警告信息', "请选择楼层!", "warning");
		return;
	}
	var form = new FormData(document.getElementById("queryform"));
    $.ajax({
        url: contextPath + "/companySeat/uploadAndUpdate.htm",
        type:"post",
        data:form,
        processData:false,
        contentType:false,
        success:function(data){
			$.messager.alert('提示信息', data.message, "info");
			//window.location.reload();
        }
    });
}

function setPicUrl(){
	var id = $("#floor").val();
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{id:id},
		url:contextPath + "/companySeat/getPic.htm",
		success:function(data){
			$("#seatImg").attr("src", data);
		}
	});
}
