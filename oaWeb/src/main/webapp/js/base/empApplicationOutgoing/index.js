$(function(){
	//监控开始结束时间计算天数
	$("#out-start").bind('input propertychange', function() {  
		initDuration();
    }); 
	$("#out-end").bind('input propertychange', function() {  
		initDuration();
    }); 
	$("#out-time").bind('input propertychange', function() {  
		initDuration();
    }); 
})
function save(){
	if($("#out-time").val().trim()==""){
		OA.titlePup('外出日期不能为空！','lose');
		return;
	}
	if($("#out-start").val().trim()==""){
		OA.titlePup('外出开始时间不能为空！','lose');
		return;
	}
	if($("#out-end").val().trim()==""){
		OA.titlePup('外出结束时间不能为空！','lose');
		return;
	}
	if($("#duration").attr("duration")==0){
		OA.titlePup('外出时长必须不可以为空！','lose');
		return;
	}
	if($("#address").val().trim()==""){
		OA.titlePup('外出地点不能为空！','lose');
		return;
	}
	var ret = /^1[3456789]\d{9}$/;
	if($("#mobile").val().trim()==""){
		OA.titlePup('联系电话不能为空！','lose');
		return;
	}
	if(!ret.test($("#mobile").val())){
		OA.titlePup('手机格式不正确！','lose');
		return;
	}
	if($("#reason").val().trim()==""){
		OA.titlePup('外出事由不能为空！','lose');
		return;
	}
	var param = {
		outDate:$("#out-time").val(),
		startTime:$("#out-time").val()+" "+$("#out-start").val()+":00",
		endTime:$("#out-time").val()+" "+$("#out-end").val()+":00",
		duration:$("#duration").attr("duration"),
		address:$("#address").val(),
		mobile:$("#mobile").val(),
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
				url:contextPath + "/empApplicationOutgoing/save.htm",
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

function initDuration(){
	if($("#out-time").val() == "") {
		OA.titlePup('请选择外出日期！','lose');
	} else if($("#out-end").val()!=""&&$("#out-start").val()!=""&&$("#out-time").val()!=""){
        var outStart = parseInt($("#out-start").val().substring(0,2));
        var outStartT = parseInt($("#out-start").val().substring(3,5));
        var outEnd = parseInt($("#out-end").val().substring(0,2));
        var outEndT = parseInt($("#out-end").val().substring(3,5));
        
        var outS = parseInt(outStart) * 60 + parseInt(outStartT);
        var outE = parseInt(outEnd) * 60 + parseInt(outEndT);
		if(outS == outE){
			OA.titlePup('开始时间不可等于结束时间！','lose');
		} else if(outS > outE) {
			OA.titlePup('开始时间必须小于结束时间!','lose');
		} else if(outStart >= 18) {
			OA.titlePup('开始时间必须小于18点!','lose');
		} else if(outE <= 9) {
			OA.titlePup('结束时间必须大于9点!','lose');
		} else {
			if(outStartT>0&&outStartT<30){
				outStartT = 0;
			}
			if(outStartT>30&&outStartT<=59){
				outStartT = 30;
			}
			if(outEndT>0&&outEndT<30){
				outEndT = 30;
			}
			if(outEndT>30&&outEndT<=59){
				outEnd = outEnd+1;
				outEndT = 0 ;
			}
			if(outStart < 9) {
				outStart = 9;
				outStartT = 0;
			}
			//最晚18点
			if(outEnd >= 18) {
				outEnd = 18;
				outEndT = 0;
			}
			if(outStartT == 0) {
				$("#out-start").val(outStart + ":00");
			} else {
				$("#out-start").val(outStart + ":" + outStartT);
			} 
			
			if(outEndT == 0) {
				$("#out-end").val(outEnd + ":00") ;
			} else {
				$("#out-end").val(outEnd + ":" + outEndT) ;
			}
			
			var hours = getHours(outStart, outStartT, outEnd, outEndT);
			
			if(hours >=10 ) {
				hours = hours - 2;
			} else if(hours >= 5) {
				hours = hours -1;
			}
			$("#duration").attr("duration",hours);
			$("#duration").text(hours+" 小时");
		}
	}
}