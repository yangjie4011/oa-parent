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
				data:{id:$(obj).attr("id"),approvalStatus:300,approvalReason:$("#approvalReason").val(),ruProcdefId:$("#ruProcdefId").val(),nodeCode:$("#nodeCode").val(),token:$("#token").val()},
				url:contextPath + "/empApplicationOvertimeLate/refuse.htm",
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
//通过
function pass(obj){
	OA.twoSurePop({
		tips:'确认同意吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:{id:$(obj).attr("id"),approvalStatus:200,approvalReason:$("#approvalReason").val(),ruProcdefId:$("#ruProcdefId").val(),nodeCode:$("#nodeCode").val(),token:$("#token").val()},
				url:contextPath + "/empApplicationOvertimeLate/pass.htm",
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