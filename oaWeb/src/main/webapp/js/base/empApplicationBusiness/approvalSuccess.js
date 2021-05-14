$(function(){
	//拆分地址
	var address=$("#address").val();
	var addressSplit=address.split("-"); //字符分割 
	var startAddress="";
	var addhtml="";
	for (var i=0;i<addressSplit.length-1 ;i++ ){ 
		if(i==0){
			//出差始发地
			startAddress=addressSplit[0];
			$("#startAddress").val(startAddress);
		}else if(i==1){
			//第一个地点
			$("#firstAddress").val(addressSplit[1]);
		}else{
			addhtml+=
		    "<div>"+
	            "<h4 class='fl'>新增出差地点</h4>"+
	            "<div class='selarea fr'>"+
	                "<input type='text'   name='adressVal' placeholder='请输入出差地点' id='del' value='"+addressSplit[i]+"'>"+
	            "</div>"+
	        "</div>";
		}
	} 
	$("#addAddress").append(addhtml);
})

//出差申请单 修改
function tobusinessBack(obj){
	window.location.href = contextPath + "/empApplicationBusiness/businessBack.htm?businessId="+$(obj).attr("id");	
}
	

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
	$(".workDay").find("textarea").each(function(i,val){
		if($(this).val().trim()==""){
			flag = false;
			return;
		}
		var detail = {
			businessDate:$(this).attr("businessDate"),
			workPlan:$(this).val(),
			workTarget:$('#workTarget'+i).val()
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
				url:contextPath + "/empApplicationBusiness/exportBusinessPdf.htm",
				success:function(data){
					if(data.success){
						OA.pageLoading(false);
						OA.titlePup('文件以邮件形式发送，请下载后打印！','win');
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