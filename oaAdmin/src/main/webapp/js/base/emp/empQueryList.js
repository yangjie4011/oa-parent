$(function(){
	signExcel = new SignExcel();  
	signExcel.init();
	
	$("#yearAndMonth").val(getLastMonthStartDate("yyyy-MM"));
	//默认查询所有
	gotoPage(1);
	$("#query").click(function(){
		gotoPage(1);
	});
	var currentCompanyId = $("#currentCompanyId").val();
	//初始化工时类型
	initWorkType(currentCompanyId);
	//初始化员工类型
	initEmployeeType(currentCompanyId);
	//初始化是否排班
	initWhetherScheduling();
	//初始化婚姻状态
	initMaritalStatus();
	//初始化学历
	initDegreeOfEducation()
	//初始化部门
	getFirstDepart();
	getFirstDepart("firstDepartOfUpd");
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		getSecondDepart(this.value);
		initEmployeeLeader(this.value);
		initPositionList(this.value);
	});
	
	$("#export").click(function() {
		var data = encodeURI(encodeURI(decodeURIComponent($("#queryform").serialize(),true)));
		window.location.href = basePath + "employee/exportEmployeeList.htm?"+data;
	});
	
	$("#checkAll").click(function(){
		if (this.checked) {
			$(".tdCheck").attr("checked",true);
		}else{
			$(".tdCheck").attr("checked",false);
		}
		/*$('.tdCheck').each(function() {
			console.log("数据："+$(this).attr('checked'));
			if ($(this).attr('checked') == 'checked') {
			//alert($(this).val());
			}
		});*/
	});
	$("#updateBatch").click(function(){
		//$("#updateReporterDiv .select_v1").empty();
		$("#nameDisplay").empty();
		var checkNames = "";
		var checkIds = "";
		$('.tdCheck').each(function(index) {
			if ($(this).attr('checked') == 'checked') {
				checkNames = checkNames + $(this).attr('title') + ",";
				if((index+1)%4 == 0){
					checkNames = checkNames+"</br>";
				}
				checkIds = checkIds + $(this).val() + ",";
			}
		});
		
		$("#nameDisplay").append(checkNames);
		
		if(checkIds != ""){
			checkIds = checkIds.substring(0, checkIds.length-1);
		}
		$("#employIdDisplay").val(checkIds);
		
		$("#firstDepartOfUpd").change(function(){
			initEmployeeLeader(this.value,"employeeLeaderOfUpd");
		});
		$("#updateReporterDiv").show();
	});
});

var SignExcel = function(){  
    
    this.init = function(){  
          
        //模拟上传excel  
         $("#uploadEventBtn").unbind("click").bind("click",function(){  
             $("#uploadEventFile").click();  
         });  
         $("#uploadEventFile").bind("change",function(){  
             $("#uploadEventPath").attr("value",$("#uploadEventFile").val());  
         });  
          
    };  
    //点击上传按钮  
    this.uploadBtn = function(){  
        var uploadEventFile = $("#uploadEventFile").val();  
        if(uploadEventFile == ''){  
        	JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("请选择Excel文件");
			});  
        }else if(uploadEventFile.lastIndexOf(".xls")<0){//可判断以.xls和.xlsx结尾的excel  
        	JEND.load('util.dialog', function() {
				JEND.util.dialog.alert("只能上传Excel文件");
			});  
        }else{  
            var url =  basePath + 'employee/importEmployeeList.htm';  
            var formData = new FormData($('form')[1]);  
            signExcel.sendAjaxRequest(url,'POST',formData);  
        }  
    };  
      
    this.sendAjaxRequest = function(url,type,data){
    	pageLoading(true);//加载动画
    	
        $.ajax({  
            url : url,  
            type : type,  
            data : data,  
    		dataType:'json',
            success : function(result) {  
            	var msg = result.response;
            	getSetForm(msg)
                pageLoading(false);//关闭动画
            },  
            error : function() {  
            	pageLoading(false);//关闭动画
            },  
            complete:function(XMLHttpRequest,textStatus){  
    			if(XMLHttpRequest.status=="403"){
    				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
    			}
    			pageLoading(false);//关闭动画
            },
            cache : false,  
            contentType : false,  
            processData : false  
        });  
    };  
} 
//导入弹框
function getSetForm(obj){
	$("#importMsg").val(obj);
	$("#importDiv").show();
}
function closeImportDiv(){
	$("#importDiv").hide();
}
function gotoPage(page){
	pageLoading(true);//加载动画
	
	if(!page){
		page = 1;
	}
	$("#pageNo").val(page);
	var data = $("#queryform").serialize() + "&page=" + $("#pageNo").val() + "&rows=10";
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: data,
		url:  basePath + "employee/getPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				//console.log("数据："+JSON.stringify(response.rows[i]));
				html += "<tr>";
				html += "<td style='text-align:cneter;'><input class='tdCheck' type ='checkbox' value='"+response.rows[i].id+"' title = '"+response.rows[i].cnName+"'/></td>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(formatStr(response.rows[i].empTypeName))+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].departName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].positionName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].workTypeName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(getJobStatusName(response.rows[i].jobStatus))+"</td>";
				if(getValByUndefined(getJobStatusName(response.rows[i].jobStatus))=="在职"){
					html += "<td style='text-align:left;'><a style='color:blue;' id="+response.rows[i].id+" onclick='update("+response.rows[i].id+",2)' >修改指纹ID</a></td>";
				}else{
					html += "<td style='text-align:left;'>&nbsp;</td>"					
				}
				html += "</tr>";
			}
			$("#reportList").append(html);
			if(response != null && response.pageNo != null) {
				page = response.pageNo;
			}
			initPage("commonPage",response,page);
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}

var formatStr = function(o){
	
	var result = "";
	if(typeof(o) == "undefined"){
		
	}else{
		result = o;
	}
	return result;
}

var getJobStatusName = function(o){
	var JobStatus = "";
	if(o==0){
		JobStatus="在职";
	}else if(o==1){
		JobStatus="离职";
	}else if(o==2){
		JobStatus="待离职";
	}
	return JobStatus;
}

//提示框关闭
var closeDiv = function(divName){
	if(divName=="deleteDiv2"){		
		$("#zcqr").attr("class","red-but");//设置Id为two的class属性。
		$('#zcqr').attr("disabled",false)//将input元素设置为disabled
		$('#zcqx').attr("disabled",false)//将input元素设置为disabled		
	}	
	$("#"+divName).hide();
}

var updateBatch = function(){	
	pageLoading(true);//开启动画
	var employeeIds = $("#employIdDisplay").val();
	var reporterId = $("#employeeLeaderOfUpd").val();
	if('' == employeeIds){
		JEND.page.alert({type:"error", message:"请至少选择一个需要更改的员工！"});
		return;
	}
	if('' == reporterId){
		JEND.page.alert({type:"error", message:"请选择汇报对象！"});
		return;
	}
	$.ajax({
		async:true,
		type:'post',
		dataType:'json',
		data: {reporterId:reporterId,employeeIds:employeeIds},
		url:  basePath + "employee/updReporterBatch.htm",
		success:function(response){
			JEND.page.alert({type:"success", message:response.response});		
			closeDiv('updateReporterDiv');
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	})
} 

//弹出更新职位界面
function update(id,num){
	$("#salaryBalanceDate").val("");
	$("#quitTime").val("");
	//创建新的更新窗口	
	$("#addOrEidt").val('2');//修改标识
	$("#id").val(id);
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data:{'id':id},
		url: basePath + "employee/getEmpInfo.htm",
		success:function(data){
			if(data != null && data.id != null){						
				//选中职级和职位序列下拉
				$(".updateForm :text[name='cnName']").val(data.cnName);
				$(".updateForm :text[name='departName']").val(data.departName);
				$(".updateForm :text[name='positionName']").val(data.positionName);				
				if(num==2){
					$(".updateForm :text[name='fingerprintId']").val(data.fingerprintId);
					$("#id2").val(data.id);
					$("#version2").val(data.version);
					$("#updateDiv2").show();	
				}else{
					$("#id").val(data.id);
					$("#version").val(data.version);
					$("#firstEntryTime").val(data.firstEntryTime);
					$("#updateDiv").show();					
				}
			}
		}
	});
}

//修改zhiwenid
function updateEmpFingerprintId(){	
	var fingerprintId=$("#fingerprintId").val();
	if(fingerprintId=="" || fingerprintId==null){
		JEND.page.alert({type:"error", message:"指纹id不能为空"});
		return;
	}
	$.ajax({
		async:false,
		type:'post',
		dataType:'json',
		data: $("#updateForm2").serialize(),
		url:basePath + "employee/updateEmpFingerprintId.htm",
		success:function(data){		
          if(data.result.flag==true){
          	closeDiv("updateDiv2");
          	JEND.page.alert({type:"success", message:data.result.message});
          	//$("#messageBox #messageContent").text(data.result.message);
          }else{
        	  closeDiv("updateDiv2");
        	  JEND.page.alert({type:"error", message:data.result.message});
        	  //$("#messageBox #messageContent").text(data.result.message);  
          }
          //$("#messageBox").show();
          $('#reportList').children('tr').remove();//删除 dom操作节点        	
          gotoPage(1);//跳转到第几页
		},
		complete:function(XMLHttpRequest,textStatus){  
			if(XMLHttpRequest.status=="403"){
				closeDiv("updateDiv2");
				JEND.page.alert({type:"error", message:"您没有该操作权限！"});
			}
			pageLoading(false);//关闭动画
        }
	});
}

//shanchu
function deletePostion() {
	 
	var id = $("#deleteId").val();
	var version = $("#version").val();
	$.post(basePath + "employee/deleteEmp.htm", {
		id : id,
		version : version
	}, function(data) {
		//1.关闭提示框
  	closeDiv("deleteDiv");
  	$("#messageBox #messageContent").text("删除成功！");
  	$("#messageBox").show();
  	$('#reportList').children('tr').remove();//删除 dom操作节点
  	gotoPage(1);//跳转到第几页
	}, 'json');
}

//删除职位
function del(id,version){
	$("#deleteId").val(id);
	$("#version").val(version);
	$("#deleteDiv").show();
}
//再次确认离职
function reconfirmQuit(){	
	var quitTime=$("#quitTime").val();
	if(quitTime=="" || quitTime==null){
		JEND.page.alert({type:"error", message:"离职日期不能为空"});
		return;
	}
	$("#zcqr").attr("class","grey-but");
	$('#zcqr').attr("disabled",true);
	$('#zcqx').attr("disabled",true);
	$("#deleteDiv2").show();
}

function quitDay(s1){
	s1 = new Date(s1.replace(/-/g, "/"));
	s2 = new Date();
	var days = s2.getTime() - s1.getTime();
	var time = parseInt(days / (1000 * 60 * 60 * 24));
	return time;
}