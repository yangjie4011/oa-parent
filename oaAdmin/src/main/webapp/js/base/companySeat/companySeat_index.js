$(function(){
	//初始化楼层下拉框
	floorInit();
	//初始化座位图
	setPicUrl();
	
	$("#floor").change(function(){
		setPicUrl();
	});
});

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
