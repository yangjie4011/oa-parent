$(function() {
	
	getLogoAsync();
	var unReadCount = 0;
	unReadMsgCount();
	waitCount();
	/*加载考勤*/
	getAttnData();
	/*加载假期*/
	getLeaveData();
	
	$("#attnDiv").click(function(){
		window.location.href = basePath + "empAttn/index.htm";
	});
	
    $("#leaveDiv").click(function(){
    	window.location.href = basePath + "empLeave/myLeaveView.htm";
	});
    
    //初始化链接										  
//	$("#sxyHref").attr("href","https://"+$("#sxyDomain").val()+"/index.php?s=/addon/Tom/Tomt/index/token/gh_89640de82fb6.html");
//	$("#szHref").attr("href","https://"+$("#sxyDomain").val()+"/index.php?s=/addon/Tom/Rulet/index/token/gh_89640de82fb6.html");
    //初始化首页图标地址
    initIconUrl();
    
    iniEmpPic();
});

function initIconUrl(){
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'companyId':$("#companyId").val(),'code':'mainPageIcon'},
		url:contextPath + "/companyConfig/getListByCondition.htm",
		success:function(data){
			$.each(data, function(index) {
				$("#" + data[index].description).attr("href",data[index].displayCode);
			});
		}
	});
}

//我的待办显示记录数
function waitCount() {
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:'',
		url:contextPath + "/ruProcdef/getCount.htm",
		success:function(data){
			$("#unCount2").empty();
			if(data > 99){
			   data = "99+";
			}
			$("#unCount2").append("我的待办(" + data + ")");
		}
	});
}

//获取未读消息数
function unReadMsgCount() {
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:'',
		url:contextPath + "/empMsg/getUnReadCount.htm",
		success:function(data){
			//$("#unCount2").empty();
			var htm = "<dd class=\"on\"></dd>";
			if(parseInt(data) > 0) {
				$("#unReadMsgCount").append(htm);
			}
		}
	});
}

var getAttnData = function(){

	var allAttnTime = 0,mustAttnTime = 0,
	    attnProportion = 0,monthTotalMustAttnTime = 0;
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:'',
		url:contextPath + "/empAttn/getAttnOfMain.htm",
		success:function(data){
			
            if(null != data && "" != data){
                
            	allAttnTime =data.totalAllAttnTime;
            	mustAttnTime =data.totalMustAttnTime;
            	attnProportion =data.proportion;
            	monthTotalMustAttnTime =data.monthTotalMustAttnTime;
            }
            
			if(allAttnTime>=mustAttnTime){
				$(".attnStatus").hide();
			}else{
				$(".attnStatus").show();
			}
			
			if(allAttnTime>=monthTotalMustAttnTime){
				$(".allAttnTime").addClass("complete");
	            $(".allAttnTime").text("已达标");
			}else{
				$(".allAttnTime").css("font-size","16px");
	            $(".allAttnTime").text(allAttnTime+"h");
			}
			
            $(".attnProportion").text(attnProportion.toFixed(2));
            $(".mustAttnTime").text("/"+mustAttnTime+"h");
            
            $("#allAttnTime").val(allAttnTime);
            $("#mustAttnTime").val(mustAttnTime);
            //canvans 绘出出勤率
//            oaIndex.drawTotalCicle({
//                canvasId:'arcAttendance', //cavas id
//                totalTimeId:'mustAttnTime',  //总工时 隐藏域id
//                specificTimeId:'allAttnTime' //已到工时隐藏域id
//            })
            drawTotalCicle({
                canvasId:'arcAttendance', //cavas id
                totalTimeId:'mustAttnTime',  //总工时 隐藏域id
                specificTimeId:'allAttnTime' //已到工时隐藏域id
            });
		}
	});
}

function drawTotalCicle(data){
	 var totalTime = parseInt($('#' + data.totalTimeId).val());
     var specificTime = parseInt($('#' + data.specificTimeId).val());

     var thanTime = specificTime / totalTime;

     var timecan = document.getElementById(data.canvasId);
     var tp = timecan.getContext('2d');


     tp.strokeStyle = 'rgba(153,153,153,0.2)';
     tp.lineWidth = 7;
     tp.beginPath();
    
     tp.arc(50, 50, 40, 360 / 180 * Math.PI, 0, false);
     tp.stroke();
     //tp.closePath();
 
     tp.strokeStyle = '#25b5f5';
     tp.lineWidth = 7;
     tp.lineCap = 'round';
     tp.beginPath();
     tp.arc(50, 50, 40, 90 / 180 * Math.PI, (thanTime * 2 + 0.5) * Math.PI, false);
     tp.stroke();
    // tp.closePath();
}


var getLeaveData = function(){

	var leftYearLeave = 0,leftSickLeave = 0,leftRestLeave = 0;
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:'',
		url:contextPath + "/empLeave/getLeaveOfMain.htm",
		success:function(data){
			
            if(null != data && "" != data){
                
            	leftYearLeave =data.leftYearLeave;
            	leftSickLeave =data.leftSickLeave;
            	leftRestLeave =data.leftRestLeave;
            }
            
			
            $("#leftYearLeave").text(leftYearLeave);
            $("#leftSickLeave").text(leftSickLeave);
            $("#leftRestLeave").text(leftRestLeave);
		}
	});
}
	
var getLogoAsync = function(){

	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data:{'companyId':$("#companyId").val()},
		url:contextPath + "/login/getLogoAsync.htm",
		success:function(data){
			
            if(null != data && "" != data){
                
            	var imgSrc = data.imgSrc;
            	var preSrc = $("#logoImg").attr("data");
            	$("#logoImg").attr("src",preSrc+imgSrc)
            }
		}
	});
 }