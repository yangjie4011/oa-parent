$(function(){
	signExcel = new SignExcel();  
	signExcel.init();
	
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
	//初始化部门
	getFirstDepart();
	getFirstDepart("firstDepartOfUpd");
	//根据一级部门选择二级部门
	$("#firstDepart").change(function(){
		initEmployeeLeader(this.value);
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
            var url =  basePath + 'attentanceSet/importTemplate.htm';  
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
		url:  basePath + "attentanceSet/getPageList.htm",
		success:function(response){
			$("#reportList").empty();
			var html = "";
			for(var i=0;i<response.rows.length;i++){
				html += "<tr>";
				html += "<td style='text-align:center;'>"+response.rows[i].code+"</td>";
				html += "<td style='text-align:center;'>"+response.rows[i].cnName+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(formatStr(response.rows[i].departName))+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].reportToLeaderName)+"</td>";
				html += "<td style='text-align:center;'>"+getValByUndefined(response.rows[i].workTypeName)+"</td>";
				html += "<td style='text-align:left;'>"+getValByUndefined(response.rows[i].className)+"</td>"	
				var classTime =getValByUndefined(response.rows[i].startTime) ;
				if(classTime!=""){
					classTime = classTime+ "-" + getValByUndefined(response.rows[i].endTime);
				}
				html += "<td style='text-align:left;'>"+classTime+"</td>"		
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

//提示框关闭
var closeDiv = function(divName){
	if(divName=="deleteDiv2"){		
		$("#zcqr").attr("class","red-but");//设置Id为two的class属性。
		$('#zcqr').attr("disabled",false)//将input元素设置为disabled
		$('#zcqx').attr("disabled",false)//将input元素设置为disabled		
	}	
	$("#"+divName).hide();
}