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