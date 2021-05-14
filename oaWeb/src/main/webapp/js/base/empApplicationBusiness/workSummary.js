function save(obj){
	if($(obj).attr("id").trim()==""){
		return;
	}
	var list = new Array();
	var flag = true;
	$(".textinput").each(function(){
		if($(this).find("textarea").val().trim()==""){
			flag = false;
			return;
		}
		var detail = {
			id:$(this).attr("id"),
			workSummary:$(this).find("textarea").val()
		};
		list.push(detail);
	})
	if(!flag){
		OA.titlePup('每日行程及任务完成情况不能为空！','lose');
		return;
	}
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:{
					id:$(obj).attr("id"),
					businessDetailList:JSON.stringify(list),
					remark:$("#remark").val(),
					token:$("#token").val()
				},
				url:contextPath + "/empApplicationBusiness/saveWorkSummary.htm",
				success:function(data){
					if(data.success){
						window.location.href=contextPath + "/ruProcdef/my_examine.htm?current=1";
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
						$("#token").val(data.token);
					}
				},
				complete:function(XMLHttpRequest,status){
					   if(status=="timeout"){
						   window.location.href=contextPath + "/ruProcdef/my_examine.htm?current=1";
					   }
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}