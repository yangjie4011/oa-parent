$(function(){
    
})

//保存
function save(obj){
	var dutyDetail = {};
	$("#vacationList").find("li").each(function(index){
		var date = $(this).find("p").text();
		var list = new Array();
		$(".tab-list-li:eq("+index+")").find(".paiban-list").each(function(i){
			    var startTime = $(this).find("ul").find("li:eq(4)").find("input").val();
			    var endTime = $(this).find("ul").find("li:eq(5)").find("input").val();
			    var workHours = $(this).find("ul").find("li:eq(6)").attr("workHours");
			    var dutyItem = $(this).find("ul").find("li:eq(7)").find("textarea").val();
			    if(startTime!=""&&endTime!=""&&workHours!=""&&dutyItem!=""){
			    	var detail = {
							id:$(this).find("ul").attr("id"),
							employeeIds:$(this).find("ul").find("li:eq(3)").attr("employeeIds"),
							startTime:startTime,
							endTime:endTime,
							workHours:workHours,
							dutyItem:dutyItem
						};
						list.push(detail);
			    }
		})
		dutyDetail[date] = list;
	})
	if(JSON.stringify(dutyDetail)=="{}"){
		OA.titlePup('您提交的数据为空!','lose');
	}
	OA.twoSurePop({
		tips:'确认提交吗？',
		sureFn:function(){
			OA.pageLoading(true);
			$.ajax({
				async:true,
				type:'post',
				dataType:'json',
				data:{id:$(obj).attr("id"),token:$("#token").val(),dutyDetail:JSON.stringify(dutyDetail)},
				url:contextPath + "/employeeClass/moveEmployeeDuty.htm",
				success:function(data){
					if(data.success){
						window.location.href=contextPath + "/employeeClass/index.htm";
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
           
            
           