$(function(){
	$(".problem").click(function(){
		window.location.href = contextPath + "/empApplicationBusiness/businessRule.htm";
	})
	//模拟单选框
    $('body').on('touchstart','.singleSelGroup span',function(){
        $(this).children('em').addClass('current');
        $(this).siblings('span').children('em').removeClass('current');
    })
})


	

function save(){
	if($("#go-date").val().trim()==""){
		OA.titlePup('去程日期不能为空！','lose');
		return;
	}
	if($("#back-date").val().trim()==""){
		OA.titlePup('返程日期不能为空！','lose');
		return;
	}
	if($("#duration").attr("duration").trim()==0){
		OA.titlePup('时长必须大于0！','lose');
		return;
	}
	if($("#travelAddress0").val().trim()==""){
		OA.titlePup('始发地不能为空！','lose');
		return;
	}
	
	if($("#travelAddress1").val().trim()==""){
		OA.titlePup('地点不能为空！','lose');
		return;
	}
	if($("#reason").val().trim()==""){
		OA.titlePup('项目/业务名称不能为空！','lose');
		return;
	}
	var list = new Array();
	var flag = true;
	$(".workDay").each(function(i,val){
		if($(this).find("textarea:eq(0)").val().trim()==""){
			flag = false;
			return;
		}
		var detail = {
			workStartDate:$(this).find("h4").text(),
			workEndDate:$(this).find(".jh_finish_data").find("span").text(),
			workPlan:$(this).find("textarea:eq(0)").val(),
			workTarget:$(this).find("textarea:eq(1)").val()
		};
		list.push(detail);
	});

	var travelClassList = new Array();
	var travelClassProvince = new Array();
	var travelClassCity = new Array();
	var insertBoxNum=document.querySelectorAll('.hiddenTravelCity');
	$(".hiddenTravelProvince").each(function(){
		if($(this).val()!=""){
			travelClassProvince.push($(this).val());
		}	
	})
	$(".hiddenTravelCity").each(function(i,v){
		if($(this).val()!=""){
			travelClassCity.push($(this).val());
		}	
	})
	for (var i = 1; i <= travelClassCity.length; i++) {				
		var detail = {
			travelProvince:travelClassProvince[i],
			travelCity:travelClassCity[i]
		}
		travelClassList.push(detail);
	}
	
	if(!flag){
		OA.titlePup('每日行程及工作计划安排不能为空！','lose');
		return;
	}
	var vehicle = "100";
	
	
	$("#singleSelGroup").find("em").each(function(){
		if($(this).hasClass("current")){
		   vehicle = $(this).attr("vehicle");
		}
	})
	var businessType = "100";
	$("#singleSelContentGroup").find("em").each(function(){
		if($(this).hasClass("current")){
			businessType = $(this).attr("businessType");
		}
	})
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
					duration:$("#duration").attr("duration"),
					startTime:$("#go-date").val(),
					endTime:$("#back-date").val(),
					vehicle:vehicle,
					address:$("#beginTravelEnd").text(),
					businessType:businessType,
					reason:$("#reason").val(),
					businessDetailList:JSON.stringify(list),
					travelClassList:JSON.stringify(travelClassList),
					travelProvinceBeginEnd:$('.hiddenTravelProvince').eq(0).val(),
					travelCityBeginEnd:$(".hiddenTravelCity").eq(0).val(),
					token:$("#token").val()
				},
				url:contextPath + "/empApplicationBusiness/save.htm",
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
				}
			});
		},
		cancelFn:function(){
			
		}
	})
	
}


  