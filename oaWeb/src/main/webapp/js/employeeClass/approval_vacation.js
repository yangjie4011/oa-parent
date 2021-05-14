function refuseVacation(obj){
	OA.twoSurePop({
		tips:'确认打回吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{processInstanceId:$(obj).attr("id"),token:$("#token").val(),approvalReason:$("#approvalReason").val()},
				url:contextPath + "/employeeClass/refuseVacation.htm",
				success:function(data){
					if(data.success){
						clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm");
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}
function passVacation(obj){
	OA.twoSurePop({
		tips:'确认同意吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{processInstanceId:$(obj).attr("id"),token:$("#token").val(),approvalReason:$("#approvalReason").val()},
				url:contextPath + "/employeeClass/passVacation.htm",
				success:function(data){
					if(data.success){
						clickSxyIcon(contextPath + "/ruProcdef/my_examine.htm");
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}