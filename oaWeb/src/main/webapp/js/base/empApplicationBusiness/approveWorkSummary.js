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

function exportPdf(obj){
	OA.twoSurePop({
		tips:'确认打印吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{id:$(obj).attr("id"),ruProcdefId:$("#ruProcdefId").val(),token:$("#token").val()},
				url:contextPath + "/empApplicationBusiness/exportBusinessReportPdf.htm",
				success:function(data){
					if(data.success){
						OA.pageLoading(false);
						OA.titlePup('文件以邮件形式发送，请下载后打印！','win');
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
						$("#token").val(data.token);
					}
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}