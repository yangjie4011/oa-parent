$(function(){
   
})

//新增工作内容
function addWorkContent(){
	var html = '<textarea style="width:80%;float:left;margin-top:10px;" rows="5" placeholder="填写当日完成工作进度、工作任务或工作产出（限100字）"></textarea><span onclick="delWorkContent(this);" class="del"></span>';
	$(".workContent").append(html);
}

//删除工作内容
function delWorkContent(obj){
	$(obj).prev("textarea").remove();
	$(obj).remove();
}

//新增工作计划
function addWorkPlan(){
	var html = '<textarea style="width:80%;float:left;margin-top:10px;" rows="5" placeholder="（限100字）"></textarea><span onclick="delWorkPlan(this);" class="del"></span>';
	$(".workPlan").append(html);
}
//删除工作计划
function delWorkPlan(obj){
	$(obj).prev("textarea").remove();
	$(obj).remove();
}

//新增工作问题
function addWorkProblem(){
	var html = '<textarea style="width:80%;float:left;margin-top:10px;" rows="5" placeholder="（限100字）"></textarea><span onclick="delWorkProblem(this);" class="del"></span>';
	$(".workProblem").append(html);
}

//删除工作问题
function delWorkProblem(obj){
	$(obj).prev("textarea").remove();
	$(obj).remove();
}

function save(){
	
	var workDate = $("#workDate").text().trim();
	var workContent = "";
	var workContentFlag  = false;
	$(".workContent").find("textarea").each(function(){
		if($(this).val().trim()==""){
			workContentFlag = true;
			return;
		}
		workContent = workContent+$(this).val().trim()+"===";
	});
	if(workContentFlag){
		OA.titlePup('工作内容与输出不能为空！','lose');
		return;
	}
	
	var workPlan = "";
	var workPlanFlag  = false;
	$(".workPlan").find("textarea").each(function(){
		if($(this).val().trim()==""){
			workPlanFlag = true;
			return;
		}
		workPlan = workPlan+$(this).val().trim()+"===";
	});
	if(workPlanFlag){
		OA.titlePup('下一个工作日工作计划不能为空！','lose');
		return;
	}
	
	var workProblem = "";
	$(".workProblem").find("textarea").each(function(){
		workProblem = workProblem+$(this).val().trim()+"===";
	});
	
	var param = {
		workDate:workDate,
		workContent:workContent.trim().substring(0,workContent.trim().length-3),
		nextDayWorkPlan:workPlan.trim().substring(0,workPlan.trim().length-3),
		workProblem:workProblem.trim().substring(0,workProblem.trim().length-3)
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
				url:contextPath + "/workLog/save.htm",
				success:function(data){
					if(data.success){
						window.location.href=contextPath + "/runTask/index.htm";
					}else{
						OA.pageLoading(false);
						OA.titlePup(data.message,'lose');
						$("#token").val(data.token);
					}
				},
				complete:function(XMLHttpRequest,status){
		             if(status=="timeout"){
		            	 window.location.href=contextPath + "/runTask/index.htm";
		             }
				},
				error:function(data) {
					console.log("error" + data);
				}
			});
		},
		cancelFn:function(){
			
		}
	})
}

