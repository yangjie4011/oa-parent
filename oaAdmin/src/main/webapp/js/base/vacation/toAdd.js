$(function(){
	$("#add").click(function(){
		add();
	})
});



function add(){
	var data = $("#addForm").serialize();
	$.ajax({
 		async : true,
 		type : "post",
		dataType:"json",
		data : data,
 		url : contextPath + "/vacation/add.htm",
 		success : function(response) {
 			
		},  
        error : function(XMLHttpRequest,textStatus) { 
        	
        },
		complete:function(XMLHttpRequest,textStatus){  
			
        }
 	});
}









