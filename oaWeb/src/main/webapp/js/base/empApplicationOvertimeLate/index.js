$(function(){
	$("#late-date").bind('input propertychange', function() {  
		init(this);
    }); 
})
function save(){
	if($("#late-date").val().trim()==""){
		OA.titlePup('延迟工作日期不能为空！','lose');
		return;
	}
	if($("#type").val()==1){
		OA.titlePup('昨天19点之后没有打卡记录，不能申请晚到！','lose');
		return;
	}
	if($("#type").val()==2){
		OA.titlePup('昨天21点之后没有打卡记录，不能申请晚到！','lose');
		return;
	}
	if($(".edit-info").find(".selMatter:eq(1)").find("div").attr("endWorkTime")==""){
		OA.titlePup('延时工作时间不能为空！','lose');
		return;
	}
	if($(".edit-info").find(".selMatter:eq(2)").find("div").attr("allowTime")==""){
		OA.titlePup('允许晚到时间不能为空！','lose');
		return;
	}
	if($(".edit-info").find(".selMatter:eq(3)").find("div").attr("actualTime")==""){
		OA.titlePup('实际晚到时间不能为空！','lose');
		return;
	}
	if($("#reason").val()==""){
		OA.titlePup('申请理由不能为空！','lose');
		return;
	}
	var param = {
		overTimeDate:$("#late-date").val(),
		overTimeStartTime:"19:00:00",
		overTimeEndTime:$(".edit-info").find(".selMatter:eq(1)").find("div").attr("endWorkTime")+":00",
		allowTime:$(".edit-info").find(".selMatter:eq(2)").find("div").attr("allowTime"),
		actualTime:$(".edit-info").find(".selMatter:eq(3)").find("div").attr("actualTime"),
		reason:$("#reason").val(),
		token:$("#token").val()
	};
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				timeout : 5000,
				dataType:'json',
				data:param,
				url:contextPath + "/empApplicationOvertimeLate/save.htm",
				success:function(data){
					if(data.success){
						window.location.href=contextPath + "/runTask/index.htm";
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
					}
				},
				complete:function(XMLHttpRequest,status){
		             if(status=="timeout"){
		            	 window.location.href=contextPath + "/runTask/index.htm";
		             }
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}
function init(obj){
	if($(obj).val().trim()!=""){
		$.ajax({
			async:false,
			type:'post',
			dataType:'json',
			data:{overTimeDate:$(obj).val()},
			url:contextPath + "/empApplicationOvertimeLate/init.htm",
			success:function(data){
				if(data.success){
					$("#type").val(data.type);
					if(data.type==0){
						$(".edit-info").find(".selMatter:eq(1)").find("div").text("19:00-"+data.endWorkTime);
						$(".edit-info").find(".selMatter:eq(1)").find("div").attr("endWorkTime",data.endWorkTime);
						$(".edit-info").find(".selMatter:eq(2)").find("div").text(data.allowTime);
						$(".edit-info").find(".selMatter:eq(2)").find("div").attr("allowTime",data.allowTime)
						$(".edit-info").find(".selMatter:eq(3)").find("div").text(data.actualTime);
						$(".edit-info").find(".selMatter:eq(3)").find("div").attr("actualTime",data.actualTime)
					}else if(data.type==1){
						OA.titlePup("昨天19点之后没有打卡记录，不能申请晚到！",'lose');
					}else if(data.type==2){
						OA.titlePup("昨天21点之后没有打卡记录，不能申请晚到！",'lose');
					}
				} else {
					OA.titlePup(data.msg,'lose');
				}
			}
		});
	}
}