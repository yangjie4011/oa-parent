$(function(){
	
	$("#go-date").unbind();
	
	//拆分地址
	var address=$("#address").val();
	var addressSplit=address.split("-"); //字符分割 
	var startAddress="";
	var addhtml="";
	var province="";
	var city="";
	for (var i=0;i<addressSplit.length-1 ;i++ ){ 
		province=$("#travelProvince"+i).val();
		city=$("#travelCity"+i).val();
		if(i==0){
			//出差始发地
			startAddress=addressSplit[0];
			$("#travelAddress0").val(startAddress);
			$("#travelProvinceVal0").val($("#travelProvinceStart").val());
			$("#travelCityVal0").val($("#travelCityValStart").val());
		}else if(i==1){
			//第一个地点
			$("#travelAddress1").val(addressSplit[1]);
			$(".travelProvinceVal1").val(province);
			$(".travelCityVal1").val(city);
		}else{
			addhtml+=
		    "<div class='insertBox' id='insertBox'>"+
	            "<h4 class='fl'>新增出差地点</h4>"+
	            "<div class='selarea fr'>"+
	                "<input type='text'  class='travelAddressVla' name='adressVal' placeholder='请输入出差地点' id='del' value='"+addressSplit[i]+"'>"+
	                "<span class='z_del'  id='del'></span>"+
	                "<input type='hidden' class='hiddenTravelProvince' id='travelProvince' value='"+province+"' />"+
	                "<input type='hidden' class='hiddenTravelCity' id='travelCity' value='"+city+"' />"+
	            "</div>"+
	        "</div>";
		}
	} 
	if(addressSplit.length-1!=6){
		addhtml+=
		"<div class='insertBox' id='insertBox'>"+
		    "<h4 class='fl'>新增出差地点</h4>"+
		    "<div class='selarea fr'>"+
		        "<input type='text'  class='travelAddressVla' name='adressVal' placeholder='请输入出差地点' id='del'>"+
		        "<span class='z_del'  id='del'></span>"+
		        "<input type='hidden' class='hiddenTravelProvince' id='travelProvince' />"+
		        "<input type='hidden' class='hiddenTravelCity' id='travelCity' />"+
		    "</div>"+
		"</div>";
	}
	$("#addAddress").append(addhtml);
	
	var businessId= $("#businessId").val();
	$.getJSON(contextPath +'/empApplicationBusiness/getBusinessDetail.htm?businessId='+businessId,function(result){
        if(result.success){
        	for(var i=0;i<result.data.length;i++){
           		var sT = new Date(result.data[i].workStartDate);
           		var eT = new Date(result.data[i].workEndDate);
           		var html = "<div class='workDay'>"
           		html += "<h4>"+getNowFormatDate(sT)+"</h4><span class='zhi'>至</span><div class='jh_finish_data'><span>"+getNowFormatDate(eT)+"</span><i></i><ul style='display: none;'>";
             	while(true){
             		html+= "<li>"+getNowFormatDate(sT)+"</li>";
             		sT.setDate(sT.getDate()+1);
	         		if(sT.getTime()>eT.getTime()){
	         			break;
	         		}
	         	}
             	html+= "</ul></div>";
             	html+= "<textarea rows='5' placeholder='例：1. 情况分析（制定计划的根据）&#13;&#10;      2. 工作任务和要求（做什么）&#13;&#10;      3. 工作的方法、步骤和措施'>"+result.data[i].workPlan+"</textarea>";
             	html+= "<textarea class='textarea' rows='5' placeholder='工作目标(非必填)'>"+result.data[i].workObjective+"</textarea></div>";
             	$(".textinput").append(html);
        	}	
        }
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
	if($("#duration").attr("duration")==0){
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
	$(".edit-info").find(".selMatter:eq(4)").find("em").each(function(){
		if($(this).hasClass("current")){
		   vehicle = $(this).attr("vehicle");
		}
	})
	var businessType = "100";
	$(".edit-info").find(".selMatter:eq(5)").find("em").each(function(){
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
					originalBillId:$("#originalBillId").val(),
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
				url:contextPath + "/empApplicationBusiness/update.htm",
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
function initDuration(){
	if($("#go-date").val()!=""&&$("#back-date").val()!=""){
        var startTime = new Date(Date.parse($("#go-date").val().trim().replace(/-/g,  "/")));
        var endTime = new Date(Date.parse($("#back-date").val().trim().replace(/-/g,  "/")));
        if(startTime.getTime()>endTime.getTime()){
       	    $(".textinput").empty();
       	    $("#duration").text(0+" 天");
            $("#duration").attr("duration",0);
        	OA.titlePup('去程日期不能大于返程日期！','lose');
        }else{
        	var length = 0;
        	while(true){
        		length = length+1;
        		startTime.setDate(startTime.getDate()+1);
        		if(startTime.getTime()>endTime.getTime()){
        			break;
        		}
        	}
       	 $("#duration").text(length+" 天");
         $("#duration").attr("duration",length);
       	 $(".textinput").empty();
       	 $("#detail").show();
       	 for(var i=0;i<length;i++){
       		var d = new Date($("#go-date").val());
	        	d.setDate(d.getDate()+i);
	        	var html = "<span class='workDay'><h4>"+getNowFormatDate(d)+"</h4><textarea  businessDate="+getNowFormatDate(d)+" rows='5' placeholder='例：1. 情况分析（制定计划的根据）&#13;&#10;      2. 工作任务和要求（做什么）&#13;&#10;      3. 工作的方法、步骤和措施'></textarea></span>";
	        	html+= "<textarea id='workTarget"+i+"' rows='5' placeholder='工作目标(非必填)'></textarea>";
	        	$(".textinput").append(html);
       	 }
        }
    }
}
function getNowFormatDate(date) {  
    var seperator1 = "-";  
    var year = date.getFullYear();  
    var month = date.getMonth() + 1;  
    var strDate = date.getDate();  
    if (month >= 1 && month <= 9) {  
        month = "0" + month;  
    }  
    if (strDate >= 0 && strDate <= 9) {  
        strDate = "0" + strDate;  
    }  
    var currentdate = year + seperator1 + month + seperator1 + strDate;  
    return currentdate;  
}  