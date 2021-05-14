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
            JEND.page.alert({type:"error", message:"请选择excel,再上传！"});
        }else if(uploadEventFile.lastIndexOf(".xls")<0){//可判断以.xls和.xlsx结尾的excel  
            JEND.page.alert({type:"error", message:"只能上传Excel文件！"});
        }else{  
            var url =  basePath + 'empAttn/uploadSignExcel.htm';  
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
                JEND.page.alert({type:"success",message:result.response});
                pageLoading(false);//关闭动画
            },  
            error : function() {  
                JEND.page.alert({type:"error",message:"excel上传失败"});
                pageLoading(false);//关闭动画
            },  
            cache : false,  
            contentType : false,  
            processData : false  
        });  
    };  
    

    //点击上传按钮  
    this.openCalculate = function(){  
        	$("#calculateDiv").toggle(500);   
    }; 
    
    this.reCalculate = function(){

        var url =  basePath + 'empAttn/reCalculate.htm';  
        var formData = $('#calculateForm').serialize();
        
        var startTime = $('#startTime').val();
        var endTime = $('#endTime').val();
        var dateUtil = new DateUtil();
        var yesterday = dateUtil.getCurrentDate();
            yesterday.setDate(yesterday.getDate() - 1);
            yesterday = yesterday.Format("yyyy-MM-dd")
        
        if(endTime > yesterday){
        	JEND.page.alert({type:"error",message:"不可计算大于昨日的考勤"});
        	return;
        }
        
        var diffType = "day";
        var dayDiff = getDateDiff(startTime,yesterday,diffType);
        if(dayDiff > 6){
        	JEND.page.alert({type:"error",message:"只可计算最近7日的考勤"});
        	return;
        }
        
       $.ajax({  
            url : url,  
            type : 'post',  
            data : formData,  
    		dataType:'json',
            success : function(result) {  
            	JEND.page.alert({type:"success",message:result.response});
            }
        });
    	
    }
}  
      
  
var signExcel;  
$(function(){  
	signExcel = new SignExcel();  
	signExcel.init();  
}); 