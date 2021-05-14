//假期详情
function showLeaveDetail(){
	$(".artical-box-bg").show();
}
function closeLeaveDetail(){
	$(".artical-box-bg").hide();
}

//销假
function toLeaveBack(obj){
	window.location.href = contextPath + "/empApplicationLeaveBack/index.htm?leaveId="+$(obj).attr("id");	
}

//出差申请单 修改
function tobusinessBack(obj){
	window.location.href = contextPath + "/empApplicationBusiness/businessBack.htm?businessId="+$(obj).attr("id");	
}

//拒绝
function refuse(obj){
	OA.twoSurePop({
		tips:'确认拒绝吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:{
					processId:$(obj).attr("id"),message:$("#approvalReason").val(),
					token:$("#token").val()
				},
				url:contextPath + "/ruProcdef/refuse.htm",
				success:function(data){
					if(data.success){
						clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm");
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				},
				complete:function(XMLHttpRequest,status){
		             if(status=="timeout"){
		            	 clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm");
		             }
				}
			});
		},
		cancelFn:function(){
		
		}
	})
	
}
function pass(obj){
	console.log($(obj).attr("id"),$("#approvalReason").val())
	OA.twoSurePop({
		tips:'确认同意吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:{
					processId:$(obj).attr("id"),message:$("#approvalReason").val(),
					token:$("#token").val()
				},
				url:contextPath + "/ruProcdef/pass.htm",
				success:function(data){
					if(data.success){
						clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm");
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				},
				complete:function(XMLHttpRequest,status){
		             if(status=="timeout"){
		            	 clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm");
		             }
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}