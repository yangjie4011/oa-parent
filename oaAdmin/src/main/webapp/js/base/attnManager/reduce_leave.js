$(function(){
	
	signExcel = new SignExcel();  
	signExcel.init();
	
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
            var url =  basePath + 'empLeave/reduceAffairAndYearLeave.htm';  
            var formData = new FormData($('form')[0]);  
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
	$("#updateDiv").show();
}
